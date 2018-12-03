package com.leyou.goods.Client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/11/1 0001
 * Time: 20:44
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
