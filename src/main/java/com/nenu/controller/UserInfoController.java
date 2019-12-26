package com.nenu.controller;

import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户信息业务层
 */
@Controller
@Log(classFunctionDescribe = "用户控制层")
public class UserInfoController {
    
    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;
    
    @Resource(name = "userservice")
    IUserService userService;
    
    /**
     * 跳到用户详情页面
     *
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/showUserDetail", method = RequestMethod.GET)
    public String showUserDetail(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("id", Integer.parseInt(id));
        TblUserinfo user = tblUserinfoMapper.selectOneByExample(example);
        map.put("userInfo", user);
    
        return "userDetail";
    }
    
    @RequestMapping(value = "/showUserUpdate", method = RequestMethod.GET)
    public String showUserUpdate(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("id", Integer.parseInt(id));
        TblUserinfo user = tblUserinfoMapper.selectOneByExample(example);
        map.put("userInfo", user);
    
        return "userUpdate";
    }
    
    @Log(methodFunctionDescribe = "修改密码", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public String updatePsd(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setPassword(password);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);
        return "success";
    }
    
    @Log(methodFunctionDescribe = "修改个人信息", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser(HttpServletRequest request) {
        String id = request.getParameter("id");
        String username = request.getParameter("username");
        String name = request.getParameter("name");
        String certid = request.getParameter("certid");
        String mobilephone = request.getParameter("mobilephone");
        String orgname = request.getParameter("orgname");
        String email = request.getParameter("email");
    
        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setCertid(certid);
        userinfo.setMobilephone(mobilephone);
        userinfo.setUsername(username);
        userinfo.setOrgname(orgname);
        userinfo.setName(name);
        userinfo.setEmail(email);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);
        TblUserinfo userinfo1 = tblUserinfoMapper.selectByPrimaryKey(Integer.parseInt(id));
        if (userinfo1.getOrgname().equals(orgname)) {
            // do nothing
        } else {
            // 是否应该修改分享记录里的公司名称
        }
    
        return "success";
    }
    
    @GetMapping(value = "/orgusers")
    @ResponseBody
    public List<TblUserinfo> RetrieveOrgUsers(@RequestParam(name = "belongedorg") int curOrgId,
                                              @RequestParam(name = "mustbelong2org") boolean mustBelong2Org,
                                              HttpServletRequest request) {
        List<TblUserinfo> users;
        if (mustBelong2Org)
            users = userService.retrieveOrgUsers(curOrgId);
        else
            users = userService.retrieveUsers(curOrgId);
        return users;
    }
}
