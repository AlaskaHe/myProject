package com.leyou.goods.Client;

import com.leyou.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/11/1 0001
 * Time: 20:45
 */
@FeignClient("item-service")
public interface SpecClient extends SpecApi {
}
