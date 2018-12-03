package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/26 0026
 * Time: 2:45
 */
public interface GoodsService {
    PageResult<SpuBo> querySpu(String key, Boolean saleable, Integer page, Integer rows);

    void saveSpu(SpuBo spuBo);

    SpuDetail querySpuDetail(Long spuId);

    List<Sku> querySkuList(Long id);

    void updateSpu(SpuBo spuBo);

    Spu querySpuById(Long id);
}
