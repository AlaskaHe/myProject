package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 11:43
 */
@RequestMapping("brand")
public interface BrandApi {
    /**
     * 通过ID查询品牌
     * @param brandId
     * @return
     */
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id")Long brandId);

    /**
     * 通过ID集合查询品牌集合
     * @return
     */
    @GetMapping("list")
    List<Brand> queryByIds(@RequestParam("ids") List<Long> ids);
}
