package com.nenu.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nenu.domain.TblNdabasicinfo;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblNdashare;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdabasicinfoMapper;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.utils.IpUtil;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * NDA共享业务层
 */
@Controller
public class ShareController {
    @Autowired
    private TblNdashareMapper tblNdashareMapper;

    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

    @Autowired
    private TblNdabasicinfoMapper tblNdabasicinfoMapper;


    /**
     * 跳转到share页面
     * @param session
     * @param map
     * @return
     */
    @GetMapping(value = "/share")
    public String share() {
        return "share";
    }
    @GetMapping(value = "/showNDA")
    public String showNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        return "showNDA";
    }

    @GetMapping(value = "/refuseNDA")
    @ResponseBody
    public String refuseNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        if(tblNdashare!= null){
            tblNdashare.setSharestatus("2");
            tblNdashare.setReceiverstatus("2");
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        return "success";
    }
    @GetMapping(value = "/agreeNDA")
    @ResponseBody
    public String agreeNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        if(tblNdashare!= null){
            tblNdashare.setSharestatus("1");
            tblNdashare.setReceiverstatus("1");
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        return "success";
    }
    @GetMapping(value = "/showUpdateNDA")
    public String showUpdateNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        return "editorNDA";
    }
    @PostMapping(value = "/updateNDA")
    @ResponseBody
    public String updateNDA(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String id = request.getParameter("id");
        String ndaItem = request.getParameter("ndaitems");
        Example example = new Example(TblNdabasicinfo.class);
        TblNdabasicinfo tblNdabasicinfo = new TblNdabasicinfo();
        tblNdabasicinfo.setId(id);
        tblNdabasicinfo.setNdaitems(ndaItem);
        tblNdabasicinfo.setUpdatetime(new Date());
        tblNdabasicinfo.setUpdateusername(currentUser.getUsername());
        tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);

        Example exampleShare  = new Example(TblNdashare.class);
        exampleShare.createCriteria().andEqualTo("ndaid",id);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(exampleShare);
        if(tblNdashare!= null){
            tblNdashare.setSharestatus("0");
            tblNdashare.setReceiverstatus("3");
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        return "share";
    }

    /**
     * 发起NDA分享
     * @param request
     * @param session
     * @return
     */
    @PostMapping(value = "/share")
    public String share(HttpServletRequest request, HttpSession session) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        String title = request.getParameter("title");
        String abstractcontext = request.getParameter("abstractcontext");
        // 根据NDA模板ID获取条款
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(ndaid));
        TblNdaitemtpl tblNdaitemtpl = tblNdaitemtplMapper.selectOneByExample(example);
        String username1 = request.getParameter("username");

        String memo = request.getParameter("memo");

        TblNdabasicinfo tblNdabasicinfo = new TblNdabasicinfo();
        tblNdabasicinfo.setId(uuid);
        tblNdabasicinfo.setAbstractcontext(abstractcontext);
        tblNdabasicinfo.setInitiatortime(new Date());
        tblNdabasicinfo.setInitiatorusername(username);
        tblNdabasicinfo.setInitiatorip(IpUtil.getIpAddress(request));
        tblNdabasicinfo.setMemo(memo);
        tblNdabasicinfo.setNdaitems(tblNdaitemtpl.getNdaitem());
        tblNdabasicinfo.setTitle(title);
        //插入一条NDA基本信息
        tblNdabasicinfoMapper.insert(tblNdabasicinfo);


        TblNdashare tblNdashare = new TblNdashare();
        tblNdashare.setCreateusername(username);
        tblNdashare.setCreatetime(new Date());
        tblNdashare.setOperateip(IpUtil.getIpAddress(request));
        tblNdashare.setNdaid(uuid);
        tblNdashare.setSharestatus("3");
        tblNdashare.setReceiverstatus("0");
        // TODO 是否携带文件发送

        tblNdashare.setUsername(username1);
        //插入一条分享记录
        tblNdashareMapper.insert(tblNdashare);

        return "share";
    }

    /**
     * 跳转到分享页面
     * @param
     * @return
     */
    @GetMapping(value = "/fileTranslation")
    public String shareFile() {
        System.out.println("====");
        return "shareFile";
    }

    /**
     * 获取自己分享给别人的信息，用于表格显示
     * @param session
     * @param pageSize
     * @param offset
     * @param searchShare
     * @return
     */
    @RequestMapping(value="/getShareData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareData(HttpSession session, Integer pageSize, Integer offset, String searchShare) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Map<String, Object> map = new HashMap<String, Object>();
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        PageHelper.startPage(offset, pageSize);
        // 设置分页查询条件
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createusername",currentUser.getUsername());
        if(searchShare!=null && searchShare.length() > 0) {
            criteria.andLike("username","%" + searchShare + "%");
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
     * @param searchShareTo
     * @return
     */
    @RequestMapping(value="/getShareToData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareToData(HttpSession session, Integer pageSize, Integer offset, String searchShareTo) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        Map<String, Object> map = new HashMap<String, Object>();
        // PageHelper 使用非常简单，只需要设置页码和每页显示笔数即可
        PageHelper.startPage(offset, pageSize);
        // 设置分页查询条件
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",currentUser.getUsername());
        if(searchShareTo!=null && searchShareTo.length() > 0) {
            criteria.andLike("createusername","%" + searchShareTo + "%");
        }
        PageInfo<TblNdashare> pageInfo = new PageInfo<TblNdashare>(tblNdashareMapper.selectByExample(example));

        map.put("total", pageInfo.getTotal());
        map.put("rows", pageInfo.getList());
        return map;
    }
}
