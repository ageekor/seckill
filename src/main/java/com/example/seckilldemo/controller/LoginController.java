package com.example.seckilldemo.controller;

import com.example.seckilldemo.service.*;
import com.example.seckilldemo.vo.LoginVo;
import com.example.seckilldemo.vo.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
@Slf4j
@Api(value = "登录", tags = "登录")
public class LoginController {

    @Autowired
    private ITUserService itUserService;


    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }


    @ApiOperation("登录接口")
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}", loginVo);
        return itUserService.doLongin(loginVo, request, response);
    }
}
