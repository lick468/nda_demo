package com.nenu.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * NDA模板业务层
 */
@Controller
public class NDATemplateController {
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

    /**
     * 创建NDA模板
     * @param session
     * @param request
     * @return
     */
    @PostMapping(value = "/createNDATemplate")
    @ResponseBody
    public String createNDATemplate(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        String ndaItem = request.getParameter("ndaItem");
        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        tblNdaitemtpl.setCreateusername(username);
        tblNdaitemtpl.setCreatetime(new Date());
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtplMapper.insert(tblNdaitemtpl);
        return "main";
    }

    /**
     * 跳转NDA模板更新页面
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "updateNDATemplate")
    public String updateNDATemplate(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdaitemtpl tblNdaitemtpl = tblNdaitemtplMapper.selectOneByExample(example);
        map.put("tblNdaitemtpl",tblNdaitemtpl);
        return "updateNDATemplate";
    }

    /**
     * 更新NDA模板
     * @param session
     * @param request
     * @return
     */
    @PostMapping(value = "/updateNDATemplate")
    @ResponseBody
    public String updateNDATemplate(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String id = request.getParameter("id");
        String ndaItem = request.getParameter("ndaItem");
        Example example = new Example(TblNdaitemtpl.class);
        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        tblNdaitemtpl.setId(Integer.parseInt(id));
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setUpdatetime(new Date());
        tblNdaitemtpl.setUpdateusername(currentUser.getUsername());
        tblNdaitemtplMapper.updateByPrimaryKeySelective(tblNdaitemtpl);
        return "main";
    }


    /**
     * NDA模板详情
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "NDATemplateDetail")
    public String NDATemplateDetail(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdaitemtpl tblNdaitemtpl = tblNdaitemtplMapper.selectOneByExample(example);
        map.put("tblNdaitemtpl",tblNdaitemtpl);
        return "NDATemplateDetail";
    }


    /**
     * 获取NDA模板用于表格显示
     * @param pageSize
     * @param offset
     * @param searchTitle
     * @return
     */
    @RequestMapping(value="/getNDATemplateData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getNoDisclosureData(Integer pageSize, Integer offset, String searchTitle) {
        Map<String, Object> map = new HashMap<String, Object>();
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        PageHelper.startPage(offset, pageSize);
        // 设置分页查询条件
        Example example = new Example(TblNdaitemtpl.class);
        if(searchTitle!=null && searchTitle.length() > 0) {
            example.createCriteria().andLike("ndaitem","%" + searchTitle + "%");
        }
        PageInfo<TblNdaitemtpl> pageInfo = new PageInfo<TblNdaitemtpl>(tblNdaitemtplMapper.selectByExample(example));

        map.put("total", pageInfo.getTotal());
        map.put("rows", pageInfo.getList());
        return map;
    }

}
