package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author: Alaska He
 * Date: 2018/10/25 0025
 * Time: 16:33
 */
@Service("specificationService")
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;
    /**
     * 根据参数查询规格参数组
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecGroups(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = this.specGroupMapper.select(specGroup);
        return specGroups;
    }

    /**
     * 根据参数查询规格参数
     * @param gid
     * @param cid
     * @return
     */
    @Override
    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        List<SpecParam> params = this.specParamMapper.select(specParam);
        return params;
    }

    @Override
    public List<SpecGroup> querySpecGroupList(Long cid) {
        List<SpecGroup> groupList = querySpecGroups(cid);
        groupList.forEach(group ->{
            List<SpecParam> specParams = querySpecParams(group.getId(), null, null, null);
            group.setParams(specParams);
        });
        return groupList;
    }
}
