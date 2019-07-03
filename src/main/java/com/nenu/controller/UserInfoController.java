package com.nenu.controller;

import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblUserinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息业务层
 */
@Controller
public class UserInfoController {

    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;

    /**
     * 跳到用户详情页面
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value="/showUserDetail",method = RequestMethod.GET)
    public String showUserDetail(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblUserinfo user = tblUserinfoMapper.selectOneByExample(example);
        map.put("userInfo",user);

        return "userDetail";
    }
    @RequestMapping(value="/showUserUpdate",method = RequestMethod.GET)
    public String showUserUpdate(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblUserinfo user = tblUserinfoMapper.selectOneByExample(example);
        map.put("userInfo",user);

        return "userUpdate";
    }
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public String updatePsd(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        //Example example = new Example(TbUser.class);
        //  example.createCriteria().andEqualTo("id", id);
        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setPassword(password);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);
        return "success";
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser(HttpServletRequest request) {
        String id = request.getParameter("id");
        String username = request.getParameter("username");
        String name = request.getParameter("name");
        String certid = request.getParameter("certid");
        String mobilephone = request.getParameter("mobilephone");
        String phoneno = request.getParameter("phoneno");
        String email = request.getParameter("email");

        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setCertid(certid);
        userinfo.setMobilephone(mobilephone);
        userinfo.setUsername(username);
        userinfo.setPhoneno(phoneno);
        userinfo.setName(name);
        userinfo.setEmail(email);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);
        return "success";
    }
}
