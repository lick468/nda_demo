package com.nenu.service.impl;

import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblOrgnizationMapper;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Service("userservice")
public class UserServiceImpl implements IUserService {
    @Autowired
    TblUserinfoMapper userInfoMapper;
    @Autowired
    TblOrgnizationMapper tblOrgnizationMapper;
    
    /*查询该公司的所有用户，必须属于该公司*/
    @Override
    public List<TblUserinfo> retrieveOrgUsers(int curOrgId) {
        List<TblUserinfo> users = new ArrayList<TblUserinfo>();
        if (curOrgId > 0) {
            Example example = new Example(TblUserinfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("belongedOrgId", curOrgId);
            users = userInfoMapper.selectByExample(example);
        }
        System.out.println(users.size());
        return users;
    }
    
    /*如果curOrgId有效，则查询该公司所有用户，否则，查询没有公司的用户*/
    @Override
    public List<TblUserinfo> retrieveUsers(int curOrgId) {
        int retrieveOrgID = curOrgId;
        if (curOrgId <= 0)
            retrieveOrgID = -1;
        Example example = new Example(TblUserinfo.class);
        Example.Criteria criteria = example.createCriteria();
        example.selectProperties("username", "name", "belongedOrgId");
        criteria.andEqualTo("belongedOrgId", retrieveOrgID);
        List<TblUserinfo> users = userInfoMapper.selectByExample(example);
        return users;
    }
    
    @Override
    public TblOrgnization getBelongedOrg(@NotBlank String userName) {
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("username", userName);
        TblUserinfo tblUserinfo = userInfoMapper.selectOneByExample(example);
        return getBelongedOrg(tblUserinfo);
    }
    
    @Override
    public TblOrgnization getBelongedOrg(TblUserinfo user) {
        if (null == user)
            return new TblOrgnization();
        Example example = new Example(TblOrgnization.class);
        example.createCriteria().andEqualTo("orgname", user.getOrgname());
        TblOrgnization tblOrgnization = tblOrgnizationMapper.selectOneByExample(example);
        return tblOrgnization;
    }
    
    @Override
    public TblUserinfo getUser(@NotBlank String userName) {
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("username", userName);
        TblUserinfo thisUser = userInfoMapper.selectOneByExample(example);
        if (null == thisUser)
            thisUser = new TblUserinfo();
        return thisUser;
    }
}
