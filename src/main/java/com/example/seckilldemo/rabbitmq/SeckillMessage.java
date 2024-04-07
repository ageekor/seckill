package com.example.seckilldemo.rabbitmq;

import com.example.seckilldemo.entity.TUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private TUser user;
    private Long goodsId;
}