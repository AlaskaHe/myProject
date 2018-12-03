package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/29 0029
 * Time: 11:32
 */
@RequestMapping("spec")
public interface SpecApi {
    @GetMapping("groups/{cid}")
   ResponseEntity<List<SpecGroup>> querySpecGroup(@PathVariable("cid") Long cid);
    @GetMapping("params")
     List<SpecParam> queryParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    );
    @GetMapping({"{cid}"})
    List<SpecGroup> querySpecGroupList(@PathVariable("cid")Long cid);
}
