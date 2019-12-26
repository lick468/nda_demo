package com.nenu.controller;


import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblOrgnizationMapper;
import com.nenu.service.INdaTemplateService;
import com.nenu.service.IOrgService;
import com.nenu.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * NDA模板业务层
 */
@Controller
@Log(classFunctionDescribe = "用户--NDA模板业务")
public class NDATemplateController {
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;
    @Autowired
    private TblOrgnizationMapper orgnizationMapper;
    
    @Resource(name = "ndatemplateservice")
    private INdaTemplateService ndaTemplateService;
    
    @Resource(name = "orgservice")
    IOrgService orgService;

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
        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        String ndaItem = request.getParameter("ndaitem");
        String ndaTitle = request.getParameter("ndatitle");
        try {
            int orgId = Integer.parseInt(request.getParameter("orgida"));
            tblNdaitemtpl.setOrgIda(orgId);
        } catch (NumberFormatException e) {
            tblNdaitemtpl.setOrgIda(-1);
        }
        try {
            int orgId = Integer.parseInt(request.getParameter("orgidb"));
            tblNdaitemtpl.setOrgIdb(orgId);
        } catch (NumberFormatException e) {
            tblNdaitemtpl.setOrgIdb(-1);
        }
        tblNdaitemtpl.setCreateusername(username);
        tblNdaitemtpl.setNdatitle(ndaTitle);
        tblNdaitemtpl.setCreatetime(LocalDateTime.now());
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setCreateip(IpUtil.getIpAddress(request));
        //tblNdaitemtpl.setTemplateType("");
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
        example.orderBy("createtime").desc();
        List<TblNdaitemtpl> tblNdaItemTplList = tblNdaitemtplMapper.selectByExample(example);
        List<TblNdaitemtpl> rows = new ArrayList<>();
        if (null == tblNdaItemTplList || tblNdaItemTplList.isEmpty()) {
            map.put("total", 0);
        } else {
            int nTotalCnt = tblNdaItemTplList.size();
            if (offset < nTotalCnt) {
                int endIdx = Math.min(nTotalCnt, offset + pageSize);
                rows.addAll(tblNdaItemTplList.subList(offset, endIdx));
            }
            map.put("total", nTotalCnt);
        }
        map.put("rows", rows);
        return map;
    }
    
    /**
     * 跳转NDAList页面
     * @return
     */
    @GetMapping(value = "/ndatemplates")
    public String NDATemplateList(ModelMap map, HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        List<TblNdaitemtpl>  ndaItemTemplates = ndaTemplateService.RetrieveUserNdaTemplates(currentUser);
        map.put("modelID",
                (null == ndaItemTemplates || ndaItemTemplates.size() < 1)? -1 : ndaItemTemplates.get(0).getId());
        map.put("ndatemplates", ndaItemTemplates);
        
        Map<String, Object> orgMap = orgService.RetrieveOrgs4User(currentUser);
        map.putAll(orgMap);
        
        return "ndatemplates";
    }
    
    @GetMapping(value = "/orgndatemplates")
    @ResponseBody
    public Map<String, Object> orgNDATemplateList(HttpSession session, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String belongedOrgIdStr = request.getParameter("belongedorgid");
        String OtherOrgIdStr = request.getParameter("otherorgid");
        int belongedOrgId = -1;
        int otherOrgId = -1;
        try {
            belongedOrgId = Integer.parseInt(belongedOrgIdStr);
            otherOrgId = Integer.parseInt(OtherOrgIdStr);
        } catch (NumberFormatException e) {
        
        }
        List<TblNdaitemtpl> ndaItemTpls= ndaTemplateService.RetrieveOrgNdaTemplates(currentUser, belongedOrgId,
                                            otherOrgId);
        map.put("ndatemplates", ndaItemTpls);
        map.put("templatecnt", (null == ndaItemTpls)? 0 : ndaItemTpls.size());
    
        return map;
    }

}
