package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/23 0023
 * Time: 9:41
 */
public interface CategoryService {
    List<Category> queryCategoryByPid(Long pid);

    List<String> queryCategoryList(List<Long> ids);

    List<String> queryNamesByIds(List<Long> ids);
}
