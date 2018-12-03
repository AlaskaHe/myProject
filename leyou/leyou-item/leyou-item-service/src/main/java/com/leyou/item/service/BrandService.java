package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/23 0023
 * Time: 11:34
 */
public interface BrandService {
    PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(Brand brand, List<Long> cids);

    List<Brand> queryBrandByCid(Long cid);

    Brand queryBrandById(Long id);

    List<Brand> queryByIds(List<Long> ids);
}
