package com.nenu.service.impl;

import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblOrgnizationMapper;
import com.nenu.service.IOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orgservice")
public class OrgServiceImpl implements IOrgService {
    @Autowired
    TblOrgnizationMapper orgnizationMapper;
    
    @Override
    public Map<String, Object> RetrieveOrgs4User (@NotNull TblUserinfo currentUser) {
        Map<String, Object> map = new HashMap<String, Object>();
        Example example =new Example(TblOrgnization.class);
        int belongedOrgId = currentUser.getBelongedOrgId();
        List<TblOrgnization> otherOrgs = orgnizationMapper.selectAll();
        TblOrgnization belongedOrg = otherOrgs.stream()
                                              .filter(org -> org.getId().equals(belongedOrgId))
                                              .findFirst().orElse(new TblOrgnization());
        if (belongedOrg.getId() <= 0)
            belongedOrg.setId(-1);
        otherOrgs.remove(belongedOrg);
        map.put("belongedOrg", belongedOrg);
        map.put("otherorgs", otherOrgs);
        return map;
    }
}
