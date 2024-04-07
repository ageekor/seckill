package com.example.seckilldemo.exception;

import com.example.seckilldemo.vo.RespBeanEnum;

public class GlobalException extends RuntimeException {

    private RespBeanEnum respBeanEnum;
    public GlobalException(RespBeanEnum respBeanEnum) {
        this.respBeanEnum = respBeanEnum;
    }
    public RespBeanEnum getRespBeanEnum() {
        return respBeanEnum;
    }
    public void setRespBeanEnum(RespBeanEnum respBeanEnum) {
        this.respBeanEnum = respBeanEnum;
    }

}
