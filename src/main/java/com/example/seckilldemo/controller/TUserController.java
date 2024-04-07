package com.example.seckilldemo.controller;


import com.example.seckilldemo.rabbitmq.MQSender;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizi
 * @since 2023-10-10
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户表", tags = "用户表")
public class TUserController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.sendsecKillMessage("Hello");

    }

}
