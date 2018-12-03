package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/25 0025
 * Time: 16:32
 */
public interface SpecificationService {
    List<SpecGroup> querySpecGroups(Long cid);

    List<SpecParam> querySpecParams(Long gid,Long cid,Boolean generic,Boolean searching);

    List<SpecGroup> querySpecGroupList(Long cid);
}
