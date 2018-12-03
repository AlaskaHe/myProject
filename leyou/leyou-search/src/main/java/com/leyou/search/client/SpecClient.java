package com.leyou.search.client;

import com.leyou.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 14:15
 */
@FeignClient("item-service")
public interface SpecClient extends SpecApi{
}
