package com.leyou.item.service.impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Alaska He
 * Date: 2018/10/23 0023
 * Time: 9:42
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private  CategoryMapper categoryMapper;

    /**
     * 根据pid查分类
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        return list;
    }

    @Override
    public List<String> queryCategoryList(List<Long> ids) {
        List<Category> list = this.categoryMapper.selectByIdList(ids);
       List<String> cnamelist =  list.stream().map(category -> category.getName()).collect(Collectors.toList());

        return cnamelist;
    }
    /**
     * 根据ids查询分类名称
     * @param ids
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
