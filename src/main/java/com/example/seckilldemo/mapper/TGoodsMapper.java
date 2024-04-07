package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.vo.GoodsVo;


import java.util.List;

public interface TGoodsMapper extends BaseMapper<GoodsVo> {
    List<GoodsVo> findGoodsVo();
    GoodsVo findGoodsVoByGoodsId(Long id);
}
