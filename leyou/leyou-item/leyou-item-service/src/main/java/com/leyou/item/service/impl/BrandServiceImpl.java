package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * @author: Alaska He
 * Date: 2018/10/23 0023
 * Time: 11:35
 */
@Service("brandService")
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据参数进行品牌的分页查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //pageHelper ，下条查询语句将被分页
        PageHelper.startPage(page, rows);
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)) {
            //如果key不为空，进行模糊查询
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        if(StringUtils.isNotBlank(sortBy)){
        String sortByClause = sortBy + (desc ? " DESC" : " ASC");
        example.setOrderByClause(sortByClause);
        }
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult<>(pageInfo.getTotal(),pageInfo);
    }

    /**
     * 根据传入的参数新增品牌
     * @param brand
     * @param cids
     */
    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
      Boolean flag = brandMapper.insertSelective(brand)==1;
      if(flag){
          cids.forEach(cid ->this.brandMapper.insertCategoryAndBrand(cid,brand.getId()));
      }

    }

    /**
     * 通过分类Id查询品牌集合
     * @param cid
     * @return
     */
    @Override
    public List<Brand> queryBrandByCid(Long cid) {
       List<Brand> list = this.brandMapper.queryBrandByCid(cid);
        return list;
    }

    /**
     * 品牌的主键查询
     * @param id
     * @return
     */
    @Override
    public Brand queryBrandById(Long id) {
        Brand brand = this.brandMapper.selectByPrimaryKey(id);
        return brand;
    }

    /**
     * 通过ID集合查询品牌集合
     * @param ids
     * @return
     */
    @Override
    public List<Brand> queryByIds(List<Long> ids) {
        List<Brand> brandList = this.brandMapper.selectByIdList(ids);
        return brandList;
    }
}
