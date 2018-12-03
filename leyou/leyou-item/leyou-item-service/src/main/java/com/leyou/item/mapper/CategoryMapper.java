package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author: Alaska He
 * Date: 2018/10/23 0023
 * Time: 9:38
 */

public interface CategoryMapper extends Mapper<Category>,SelectByIdListMapper<Category,Long> {
}
