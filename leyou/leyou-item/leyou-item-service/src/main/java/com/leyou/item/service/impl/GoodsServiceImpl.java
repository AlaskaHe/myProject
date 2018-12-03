package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author: Alaska He
 * Date: 2018/10/26 0026
 * Time: 2:45
 */
@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;


    /**
     * 进行spu的分页查询
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpu(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        PageHelper.startPage(page, Math.min(rows, 100));
        Page<Spu> pageInfo = (Page<Spu>) spuMapper.selectByExample(example);
        //将spu的属性注入spuBo,查询出分类和品牌的具体内容
        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<String> cname = this.categoryService.queryCategoryList(ids);
            spuBo.setCname(StringUtils.join(cname, "/"));
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    /**
     * 新增Spu
     *
     * @param spuBo
     */
    @Override
    @Transactional
    public void saveSpu(SpuBo spuBo) {
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insert(spuBo);
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        this.spuDetailMapper.insert(spuBo.getSpuDetail());
        saveSkuAndStock(spuBo.getSkus(), spuBo.getId());


    }


    /**
     * 查询修改需要的SpuDetail
     *
     * @param spuId
     * @return
     */
    @Override
    @Transactional
    public SpuDetail querySpuDetail(Long spuId) {
        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(spuId);
        return spuDetail;
    }

    /**
     * 查询修改需要的SkuList
     *
     * @param id
     * @return
     */
    @Override
    public List<Sku> querySkuList(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> list = this.skuMapper.select(sku);
        return list;
    }

    /**
     * 修改Spu
     * 先删后增
     *
     * @param spuBo
     */
    @Override
    @Transactional
    public void updateSpu(SpuBo spuBo) {

        List<Sku> skus = querySkuList(spuBo.getId());
        if (!CollectionUtils.isEmpty(skus)) {
            skus.stream().forEach(sku -> {
                this.stockMapper.deleteByPrimaryKey(sku.getId());
                this.skuMapper.deleteByPrimaryKey(sku.getId());
            });
        }
        saveSkuAndStock(spuBo.getSkus(), spuBo.getId());
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.updateByPrimaryKey(spuDetail);
        this.spuMapper.updateByPrimaryKey(spuBo);
    }

    @Override
    public Spu querySpuById(Long id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        return spu;
    }

    /**
     * 存储sku和stock
     * @param list
     * @param spuId
     */
    private void saveSkuAndStock(List<Sku> list, Long spuId) {
        for (Sku sku : list) {
            sku.setEnable(true);
            sku.setSpuId(spuId);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }
}