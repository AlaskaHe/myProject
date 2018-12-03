package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 11:29
 */
@Service
public class SearchService {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecClient specClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    public PageResult<Goods> search(SearchRequest request) {
        System.out.println("1"+request.toString());

        String key = request.getKey();
        //判断是否有搜索条件，如果没有返回Null,不允许全部搜索
        if(StringUtils.isBlank(key)){
            return null;
        }
        //1.构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder basicQuery = buildBasicQueryWithFilter(request);
        //1.1.基本查询
            queryBuilder.withQuery(basicQuery);
        //返回我们需要的结果字段
            queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //1.2.分页
            int page = request.getPage();
            int size = request.getSize();
            queryBuilder.withPageable(PageRequest.of(page,size));
        //1.3聚合
        String categoryAggName = "category"; // 商品分类聚合名称
        String brandAggName = "brand"; // 品牌聚合名称
        //对分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //获取查询结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        //解析分类查询结果
        LongTerms categoryAgg = (LongTerms) goodsPage.getAggregation(categoryAggName);

        List<Map<String,Object>> categories = getCategoryAggResult(categoryAgg);
        //解析品牌查询结果
        LongTerms brandAgg = (LongTerms) goodsPage.getAggregation(brandAggName);

        List<Brand> brands = getBrandsAggResult(brandAgg);
        //当分类只有一个的时候，才进行规格参数的聚合
        List<Map<String, Object>> specs = null;
        if(categories.size() == 1){
            //获取规格参数聚合的结果集，需要cid，基本查询条件
            specs = getSpecParamAggResult((Long)categories.get(0).get("id"),basicQuery);
        }
    return new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),categories,brands,specs);

    }

    private BoolQueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("all", request.getKey());
        queryBuilder.must(matchQuery);
        //获取过滤条件
        Map<String, String> filter = request.getFilter();
        filter.entrySet().forEach(entry -> {
            String key = entry.getKey();
            if(StringUtils.equals("品牌",key)){
                key = "brandId";
            }else if(StringUtils.equals("分类",key)){
                key = "cid";
            }else {
                key = "specs."+key+".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        });
            return queryBuilder;
    }


    private List<Map<String,Object>> getSpecParamAggResult(Long cid, BoolQueryBuilder basicQuery) {
        List<SpecParam> params = this.specClient.queryParam(null, cid, null, true);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        params.forEach(param ->{
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
            AggregatedPage<Goods> aggPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        Map<String, Aggregation> aggMap = aggPage.getAggregations().asMap();
        //初始化返回的结果集
        List<Map<String,Object>> specs = new ArrayList<>();
        aggMap.entrySet().forEach(aggEntry -> {
            Map<String,Object> map = new HashMap<>();
            map.put("k",aggEntry.getKey());
            StringTerms paramAgg = (StringTerms) aggEntry.getValue();
            List<String> options = new ArrayList<>();
            paramAgg.getBuckets().forEach(bucket ->{
                        options.add(bucket.getKeyAsString()) ;
                    });
            map.put("options",options);
            specs.add(map);

        });
        return specs;
    }

    private List<Map<String,Object>> getCategoryAggResult(LongTerms categoryAgg) {
        //获取桶
        List<LongTerms.Bucket> categorybuckets = categoryAgg.getBuckets();
        List<Long> cids = new ArrayList<>();
        for (LongTerms.Bucket bucket : categorybuckets) {
            long value = bucket.getKeyAsNumber().longValue();
            System.out.println("2"+value);
            cids.add(value);
        }
        System.out.println(cids);
        //获取分类名字集合
        List<String> names = this.categoryClient.queryNameByIds(cids);
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    private List<Brand> getBrandsAggResult(LongTerms brandAgg) {
        //获取桶
        List<LongTerms.Bucket> brandAggBuckets = brandAgg.getBuckets();
        List<Long> bids = new ArrayList<>();
        for (LongTerms.Bucket brandAggBucket : brandAggBuckets) {
            long value = brandAggBucket.getKeyAsNumber().longValue();
            bids.add(value);
        }
        List<Brand> brandList = this.brandClient.queryByIds(bids);
        return brandList;
    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 根据cid1， cid2， cid3 查询对应的名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 根据brandId查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 根据spuId查询该spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        // 收集sku的集合
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 收集价格
        List<Long> prices = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            map.put("title", sku.getTitle());
            skuMapList.add(map);
        });

        // 根据cid3查询搜索的规格参数
        List<SpecParam> params = this.specClient.queryParam(null, spu.getCid3(), null, true);
        // 根据spuid查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        // 通用的规格参数Map<paramId, paramValue>
        Map<String, Object> genericMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 特殊的规格参数Map<paramId, ParamValue>
        Map<String, List<Object>> specialMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        // 收集规格参数：Map<param.getName, 参数值>
        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            if (param.getGeneric()) {
                // 如果是通用字段
                String value = genericMap.get(param.getId().toString()).toString();
                // 判断是否是数值类型的数据
                if (!param.getName().equals("上市年份")) {
                    if (param.getNumeric()) {
                        value = chooseSegment(value, param);
                    }
                }
                specs.put(param.getName(), value);
            } else{
                // 特殊的规格字段
                List<Object> value = specialMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }

        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        // 设置搜索字段：spu的title 分类名称 品牌名称
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " ") + brand.getName());
        // 设置价格集合，搜索sku中所有价格List<Long>
        goods.setPrice(prices);
        // 设置skus，搜集所有的List<sku>，序列化为字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        // 设置规格参数，Map<参数名，参数值>
        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


}
