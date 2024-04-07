package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.vo.GoodsVo;

import java.util.List;
public interface ITGoodsService extends IService<GoodsVo> {

    List<GoodsVo> findTGoods();

    GoodsVo findTGoodsById(Long goodsId);
}
