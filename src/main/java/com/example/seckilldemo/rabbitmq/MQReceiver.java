package com.example.seckilldemo.rabbitmq;

import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.service.ITGoodsService;
import com.example.seckilldemo.service.ITOrderService;
import com.example.seckilldemo.utils.JsonUtil;
import com.example.seckilldemo.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;


@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("接收消息: " + msg);
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        TUser user = message.getUser();
        GoodsVo goods = goodsService.findTGoodsById(goodsId);
//判断库存
        if (goods.getStockCount() < 1) {
            return;
        }
        String seckillOrderJson = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return;
        }
        orderService.secKill(user, goods);
    }

}
