package com.nenu.service.impl;

import com.nenu.domain.TblNdaTemplateType;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblOrgnizationMapper;
import com.nenu.service.INdaTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service("ndatemplateservice")
public class NdaTemplateServiceImpl implements INdaTemplateService {
    @Autowired
    TblOrgnizationMapper orgnizationMapper;
    @Autowired
    TblNdaitemtplMapper ndaTplMapper;
    
    @Override
    public List<TblNdaitemtpl> RetrieveUserNdaTemplates(TblUserinfo user) {
        List<TblNdaitemtpl> ndaItemTemplates = new ArrayList<TblNdaitemtpl>();
        if (null == user) {
            ndaItemTemplates = ndaTplMapper.selectAll();
        } else {
            Example example = new Example(TblNdaitemtpl.class);
            int belongedOrgId = user.getBelongedOrgId();
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createusername", user.getUsername());
                    /*.orEqualTo("orgida", belongedOrgId)
                    .orEqualTo("orgidb", belongedOrgId);*/
            example.orderBy("createtime").desc();
            ndaItemTemplates = ndaTplMapper.selectByExample(example);
            example = new Example(TblOrgnization.class);
            //example.createCriteria().andNotEqualTo("id", currentUser.getBelongedOrg());
        }
        return ndaItemTemplates;
    }
    
    @Override
    /*Author: Sunct
    * Date: 2019.12.05
    * 查询本用户或者其所属公司的NDA协议模板*/
    public List<TblNdaitemtpl> RetrieveUserNdaTemplates(TblUserinfo user, int orgId) {
        List<TblNdaitemtpl> ndaItemTemplates = new ArrayList<TblNdaitemtpl>();
        List<TblNdaitemtpl> tmpTemplates;
        if (null == user) {
            ndaItemTemplates = ndaTplMapper.selectAll();
        } else {
            Example example = new Example(TblNdaitemtpl.class);
            Example.Criteria criteria = example.createCriteria();
            /*搜索有没有本公司公用模板*/
            example = new Example(TblNdaitemtpl.class);
            criteria = example.createCriteria();
            criteria.andEqualTo("orgida", orgId)
                    .orEqualTo("orgidb", orgId);
            example.orderBy("createtime").desc();
            tmpTemplates = ndaTplMapper.selectByExample(example);
            if (null != tmpTemplates && !tmpTemplates.isEmpty())
                ndaItemTemplates.addAll(tmpTemplates);
        
            /*当前用户创建，但不属于本公司的模板*/
            example = new Example(TblNdaitemtpl.class);
            criteria = example.createCriteria();
            criteria.andEqualTo("createusername", user.getUsername());
            example.and(example.createCriteria().andNotEqualTo("orgida", orgId)
                                                .andNotEqualTo("orgidb", orgId));
            example.orderBy("createtime").desc();
            tmpTemplates = ndaTplMapper.selectByExample(example);
            if (null != tmpTemplates && !tmpTemplates.isEmpty()) {
                ndaItemTemplates.addAll(tmpTemplates);
            }
        }
        return ndaItemTemplates;
        
    }
    
    /*本来可以用条件语句一次性查询，但是为了排序方便，所以分三次查询*/
    @Override
    public List<TblNdaitemtpl> RetrieveOrgNdaTemplates(TblUserinfo user, int orgId, int otherOrgid) {
        List<TblNdaitemtpl> ndaItemTemplates = new ArrayList<TblNdaitemtpl>();
        List<TblNdaitemtpl> tmpTemplates;
        if (null == user) {
            ndaItemTemplates = ndaTplMapper.selectAll();
        } else {
            Example example = new Example(TblNdaitemtpl.class);
            Example.Criteria criteria = example.createCriteria();
            /*首先搜索两个公司之间是否有协议模板*/
            criteria.andEqualTo("orgida", orgId)
                    .andEqualTo("orgidb", otherOrgid);
            example.or(example.createCriteria().andEqualTo("orgidb", orgId)
                                                .andEqualTo("orgida", otherOrgid));
            example.orderBy("createtime").desc();
            tmpTemplates = ndaTplMapper.selectByExample(example);
            if (null != tmpTemplates && !tmpTemplates.isEmpty()) {
                ndaItemTemplates.addAll(tmpTemplates);
            }
            /*搜索有没有本公司公用模板，模板对方公司id为-1*/
            example = new Example(TblNdaitemtpl.class);
            criteria = example.createCriteria();
            criteria.andEqualTo("orgida", orgId)
                    .andEqualTo("orgidb", -1);
            example.or(example.createCriteria().andEqualTo("orgidb", orgId)
                              .andEqualTo("orgida", -1));
            example.orderBy("createtime").desc();
            tmpTemplates = ndaTplMapper.selectByExample(example);
            if (null != tmpTemplates && !tmpTemplates.isEmpty()) {
                ndaItemTemplates.addAll(tmpTemplates);
            }
            
            /*当前用户的模板，本公司和对方公司ID都为-1*/
            example = new Example(TblNdaitemtpl.class);
            criteria = example.createCriteria();
            criteria.andEqualTo("createusername", user.getUsername())
                    .andEqualTo("orgida", -1)
                    .andEqualTo("orgidb", -1);
            example.orderBy("createtime").desc();
            tmpTemplates = ndaTplMapper.selectByExample(example);
            if (null != tmpTemplates && !tmpTemplates.isEmpty()) {
                ndaItemTemplates.addAll(tmpTemplates);
            }
        }
        return ndaItemTemplates;
    }
}
