package com.nenu.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nenu.domain.TblNdashare;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdashareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * NDA共享业务层
 */
@Controller
public class ShareController {
    @Autowired
    private TblNdashareMapper tblNdashareMapper;

    /**
     * 跳转到share页面
     * @param session
     * @param map
     * @return
     */
    @GetMapping(value = "/share")
    public String share(HttpSession session, ModelMap map) {
        return "share";
    }

    /**
     * 跳转到分享页面
     * @param id
     * @return
     */
    @GetMapping(value = "/shareFile/{id}")
    public String shareFile(@PathVariable("id")Integer id) {
        System.out.println(id+"====");
        return "shareFile";
    }

    /**
     * 获取自己分享给别人的信息，用于表格显示
     * @param session
     * @param pageSize
     * @param offset
     * @param searchTitle
     * @return
     */
    @RequestMapping(value="/getShareData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareData(HttpSession session, Integer pageSize, Integer offset, String searchTitle) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Map<String, Object> map = new HashMap<String, Object>();
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        PageHelper.startPage(offset, pageSize);
        // 设置分页查询条件
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createusername",currentUser.getUsername()).andEqualTo("status",1);
        if(searchTitle!=null && searchTitle.length() > 0) {
            criteria.andLike("username","%" + searchTitle + "%");
        }
        PageInfo<TblNdashare> pageInfo = new PageInfo<TblNdashare>(tblNdashareMapper.selectByExample(example));

        map.put("total", pageInfo.getTotal());
        map.put("rows", pageInfo.getList());
        return map;
    }

    /**
     * 获取别人分享给自己信息用于表格显示
     * @param session
     * @param pageSize
     * @param offset
     * @param searchTitle
     * @return
     */
    @RequestMapping(value="/getShareToData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareToData(HttpSession session, Integer pageSize, Integer offset, String searchTitle) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Map<String, Object> map = new HashMap<String, Object>();
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        PageHelper.startPage(offset, pageSize);
        // 设置分页查询条件
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",currentUser.getUsername()).andEqualTo("status",1);
        if(searchTitle!=null && searchTitle.length() > 0) {
            criteria.andLike("createusername","%" + searchTitle + "%");
        }
        PageInfo<TblNdashare> pageInfo = new PageInfo<TblNdashare>(tblNdashareMapper.selectByExample(example));

        map.put("total", pageInfo.getTotal());
        map.put("rows", pageInfo.getList());
        return map;
    }
}
