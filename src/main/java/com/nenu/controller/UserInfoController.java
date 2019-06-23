package com.nenu.controller;

import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblUserinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
}
