package com.example.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.seckilldemo.entity.TOrder;
import com.example.seckilldemo.entity.TSeckillOrder;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;

import com.example.seckilldemo.rabbitmq.MQSender;
import com.example.seckilldemo.rabbitmq.SeckillMessage;
import com.example.seckilldemo.service.ITGoodsService;
import com.example.seckilldemo.service.ITOrderService;
import com.example.seckilldemo.service.ITSeckillOrderService;

import com.example.seckilldemo.utils.JsonUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;

import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
@Api(value = "秒杀", tags = "秒杀")
public class SeKillController implements InitializingBean {
    @Autowired
    private ITGoodsService itGoodsService;
    @Autowired
    private ITSeckillOrderService itSeckillOrderService;
    @Autowired
    private ITOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    private Map<Long, Boolean> EmptyStockMap = new HashMap<>(); //Map不保证线程安全

    @RequestMapping(value = "/doSeckill0")
    public String doSecKill0(Model model, TUser user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        System.out.println(user);
        //优化后代码
        model.addAttribute("user", user);
        GoodsVo goods = itGoodsService.findTGoodsById(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail:count<1";
        }
        //判断是否重复抢购
        TSeckillOrder seckillOrder = itSeckillOrderService.getOne(new
                QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        TOrder order = orderService.secKill(user, goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
//判断是否重复抢购
        String seckillOrderJson = (String) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
//内存标记,减少Redis访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
//预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
// 请求入队，立即返回排队中
        SeckillMessage message = new SeckillMessage(user, goodsId);
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);
    }
    @ApiOperation("获取验证码")
    @GetMapping(value = "/captcha")
    public void verifyCode(TUser tUser, Long goodsId, HttpServletResponse response) {
        if (tUser == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + tUser.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }


    @ApiOperation("获取秒杀结果")
    @GetMapping("getResult")
    @ResponseBody
    public RespBean getResult(TUser tUser, Long goodsId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = itSeckillOrderService.getResult(tUser, goodsId);
        return RespBean.success(orderId);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = itGoodsService.findTGoods();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

}
