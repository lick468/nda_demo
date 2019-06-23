package com.nenu.controller;

import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblUserinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * NDA业务层
 */
@Controller
public class NDAController {
    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;

    /**
     * 创建NDA
     * @param map
     * @param session
     * @return
     */
    @GetMapping(value = "createNDA")
    public String createNDA(ModelMap map, HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        System.out.println(username);
        List<TblUserinfo> tblUserinfos = tblUserinfoMapper.selectAll();
        List<TblUserinfo> users = new ArrayList<>();
        for (TblUserinfo user:tblUserinfos) {
            if(!user.getUsername().equals(username)) {
                users.add(user);
            }
        }
        map.put("users",users);
        return "createNDA";
    }

    /**
     * 跳转NDAList页面
     * @return
     */
    @GetMapping(value = "NDAList")
    public String NDAList() {
        return "NDAList";
    }
}
