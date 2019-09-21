package com.nenu.controller;


import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * NDA模板业务层
 */
@Controller
@Log(classFunctionDescribe = "用户--NDA模板业务")
public class NDATemplateController {
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

    /**
     * 创建NDA模板
     * @param session
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe = "创建NDA模板",businessType = BusinessType.CREATE)
    @PostMapping(value = "/createNDATemplate")
    @ResponseBody
    public String createNDATemplate(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        String ndaItem = request.getParameter("ndaitem");
        String ndaTitle = request.getParameter("ndatitle");
        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        tblNdaitemtpl.setCreateusername(username);
        tblNdaitemtpl.setNdatitle(ndaTitle);
        tblNdaitemtpl.setCreatetime(new Date());
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setCreateip(IpUtil.getIpAddress(request));
        tblNdaitemtplMapper.insert(tblNdaitemtpl);
        return "main";
    }

    /**
     * 跳转NDA模板更新页面
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "/updateNDATemplate")
    public String updateNDATemplate(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdaitemtpl tblNdaitemtpl = tblNdaitemtplMapper.selectOneByExample(example);
        map.put("tblNdaitemtpl",tblNdaitemtpl);
        return "updateNDATemplate";
    }
    /**
     * 删除NDA模板
     * @param request
     * @param map
     * @return
     */
    @Log(methodFunctionDescribe = "删除NDA模板",businessType = BusinessType.DELETE)
    @GetMapping(value = "/NDATemplateDelete")
    @ResponseBody
    public String NDATemplateDelete(HttpServletRequest request, ModelMap map) {
        String id = request.getParameter("id");
        Example example = new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        tblNdaitemtplMapper.deleteByExample(example);
        return "success";
    }


    /**
     * 更新NDA模板
     * @param session
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe = "更新NDA模板",businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateNDATemplate")
    @ResponseBody
    public String updateNDATemplate(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String id = request.getParameter("id");
        String ndaItem = request.getParameter("ndaItem");
        String ndatitle = request.getParameter("ndatitle");

        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        tblNdaitemtpl.setId(Integer.parseInt(id));
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setNdatitle(ndatitle);
        tblNdaitemtpl.setUpdatetime(new Date());
        tblNdaitemtpl.setUpdateip(IpUtil.getIpAddress(request));
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
    @GetMapping(value = "/NDATemplateDetail")
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
    @Log(methodFunctionDescribe = "查看NDA模板列表",businessType = BusinessType.PAGELIST)
    @RequestMapping(value="/getNDATemplateData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getNoDisclosureData(HttpSession session,Integer pageSize, Integer offset, String searchTitle) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Map<String, Object> map = new HashMap<String, Object>();

        // 设置查询条件
        Example example = new Example(TblNdaitemtpl.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createusername",currentUser.getUsername());
        if(searchTitle!=null && searchTitle.length() > 0) {
            criteria.andLike("ndatitle","%" + searchTitle + "%");
        }
        List<TblNdaitemtpl> tblNdaitemtpls = tblNdaitemtplMapper.selectByExample(example);
        List<TblNdaitemtpl> rows = new ArrayList<>();
        for(int i=offset;i<offset+pageSize;i++) {
            if(tblNdaitemtpls.size() > i) {
                rows.add(tblNdaitemtpls.get(i));
            }
        }
        map.put("total", tblNdaitemtpls.size());
        map.put("rows", rows);
        return map;
    }

}
