package com.leyou.goods.service;

import com.leyou.goods.Client.BrandClient;
import com.leyou.goods.Client.CategoryClient;
import com.leyou.goods.Client.GoodsClient;
import com.leyou.goods.Client.SpecClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: Alaska He
 * Date: 2018/11/1 0001
 * Time: 20:13
 */
@Service
public class GoodsService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private GoodsClient goodsClient;

    public Map<String,Object> loadDate(Long spuId) {
        Map<String,Object> map = new HashMap<>();
        //spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        //spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spuId);

        //sku集合
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);

        //商品分类（只需要id,name）map<Long,String>
        List<Long> cids = Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3());
        List<String> cnames = this.categoryClient.queryNameByIds(cids);
        List<Map<String,Object>> categoriesList = new ArrayList<>();
        for (int i = 0; i < cnames.size(); i++) {
        Map<String,Object> categoriesMap = new HashMap<>();
            categoriesMap.put("id",cids.get(i));
            categoriesMap.put("name",cnames.get(i));
            categoriesList.add(categoriesMap);
        }

        //品牌对象
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        System.out.println("brand = " + brand);

        //规格组（含对应的参数集合）

        List<SpecGroup> groupList = this.specClient.querySpecGroupList(spu.getCid3());

        //特殊参数（SpuDetail里SpecialSpec的key是特殊规格参数的Id）
        // 查询出map<id,name>的集合就能通过key获取参数名称
        List<SpecParam> paramList = this.specClient.queryParam(null, spu.getCid3(), null, false);
        Map<Long,Object> paramMap = new HashMap<>();
        paramList.forEach(param ->{
            paramMap.put(param.getId(),param.getName());

        });
        map.put("spu",spu);
        map.put("spuDetail",spuDetail);
        map.put("skus",skus);
        map.put("categories",categoriesList);
        map.put("brand",brand);
        map.put("groups",groupList);
        map.put("paramMap",paramMap);
        System.out.println(map);
        return map;

    }
}
