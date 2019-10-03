package com.nenu.controller;

import com.nenu.aspect.lang.annotation.Log;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblUserinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * NDA业务层
 */
@Controller
@Log(classFunctionDescribe = "NDA业务")
public class NDAController {
    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

    /**
     * 跳转到创建NDA页面
     * @param map
     * @param session
     * @return
     */
    @GetMapping(value = "/createNDA")
    public String createNDA(ModelMap map, HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        List<TblUserinfo> tblUserinfos = tblUserinfoMapper.selectAll();
        List<TblUserinfo> users = new ArrayList<>();
        for (TblUserinfo user:tblUserinfos) {
            if(!user.getUsername().equals(username)) {
                users.add(user);
            }
        }
        Example example =new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("createusername",currentUser.getUsername());
        List<TblNdaitemtpl> tblNdaitemtpls = tblNdaitemtplMapper.selectByExample(example);
        map.put("NDAs",tblNdaitemtpls);
        map.put("users",users);
        return "createNDA";
    }

    /**
     * 跳转NDAList页面
     * @return
     */
    @GetMapping(value = "/NDAList")
    public String NDAList(ModelMap map,HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Example example =new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("createusername",currentUser.getUsername());
        List<TblNdaitemtpl> tblNdaitemtpls = tblNdaitemtplMapper.selectByExample(example);
        map.put("modelID",tblNdaitemtpls.get(0).getId());
        map.put("NDAs",tblNdaitemtpls);
        return "NDAList";
    }
}
