package com.leyou.search.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 14:14
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
