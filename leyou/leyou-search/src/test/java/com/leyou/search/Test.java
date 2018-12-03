package com.leyou.search;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.service.SearchService;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 21:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

   @org.junit.Test
    public void uploadTest() {
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        do {
            PageResult<SpuBo> result = this.goodsClient.querySpu(null, true, page, rows);
            List<SpuBo> spuBoList = result.getItems();
            rows = spuBoList.size();
            List<Goods> goodsList = new ArrayList<>();
            for (SpuBo spuBo : spuBoList) {
                try {
                    Goods goods = this.searchService.buildGoods(spuBo);
                    goodsList.add(goods);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.goodsRepository.saveAll(goodsList);
            page++;
        } while (rows == 100);

    }
}
