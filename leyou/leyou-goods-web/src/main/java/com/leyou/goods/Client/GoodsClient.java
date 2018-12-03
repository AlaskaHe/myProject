package com.leyou.goods.Client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/11/1 0001
 * Time: 20:14
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
