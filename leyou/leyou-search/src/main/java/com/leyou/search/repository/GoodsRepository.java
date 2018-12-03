package com.leyou.search.repository;


import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 10:38
 */
@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
