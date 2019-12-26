package com.nenu.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.domain.*;
import com.nenu.mapper.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 管理员业务层
 */
@Controller
@RequestMapping(value = "/admin")
@Log(classFunctionDescribe = "管理员业务")
public class AdminLoginController {

   @Autowired
   private TblAdminMapper adminMapper;

   @Autowired
   private TblLogMapper logMapper;

    /**
     *  登录成功后，进入log页面
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String showMain() {
        return "admin/log";
    }


    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping(value = {"/login",""}, method = RequestMethod.GET)
    public String showLogin() {
        return "admin/login";
    }

    /**
     * 用户登录
     * @param admin 登录用户信息
     * @param session 登录成功，用户信息存放session 中
     * @param redirectAttributes 登录失败信息
     * @return
     */
    @Log(methodFunctionDescribe = "管理员登录",businessType = BusinessType.LOGIN)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(TblAdmin admin, HttpSession session, RedirectAttributes redirectAttributes) {

        Example example = new Example(TblAdmin.class);
        example.createCriteria().andEqualTo("username",admin.getUsername());
        TblAdmin tblAdmin = adminMapper.selectOneByExample(example);
        if(tblAdmin != null) {
            if(tblAdmin.getPassword().equals(admin.getPassword())) {
                session.setAttribute("currentAdmin",tblAdmin);
                session.setMaxInactiveInterval(60 * 60 * 2 );
                return "redirect:/admin/index";
            }else {
                redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
                return "redirect:/admin/login";
            }
        }else {
            redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
            return "redirect:/admin/login";
        }
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null) {
            session.removeAttribute("currentAdmin");
            session.invalidate();//关闭session
        }
        return "redirect:/admin/login";
    }

    /**
     * 获取操作日志用于表格显示
     * @param pageSize
     * @param offset
     * @return
     */
    @Log(methodFunctionDescribe = "获取操作日志",businessType = BusinessType.PAGELIST)
    @RequestMapping(value="/getOperData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareToData(Integer pageSize, Integer offset) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<TblLog> tblLogs = logMapper.selectAll();
        List<TblLog> rows = new ArrayList<>();
        for(int i=offset;i<offset+pageSize;i++) {
            if(tblLogs.size() > i) {
                rows.add(tblLogs.get(i));
            }
        }
        map.put("total", tblLogs.size());
        map.put("rows", rows);
        return map;
    }


}
