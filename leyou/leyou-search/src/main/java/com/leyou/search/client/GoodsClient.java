package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 10:31
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
