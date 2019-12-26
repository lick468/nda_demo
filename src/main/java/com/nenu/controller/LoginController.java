package com.nenu.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.constant.NdaConst;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.utils.IpUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 登录业务层
 */
@Controller
@Log(classFunctionDescribe = "用户业务")
public class LoginController {

    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;


    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;


  @GetMapping(value = {"/main","/"})
    public String index() {
        return "index";
    }

    /**
     *  登录成功后，进入index页面
     * @param session
     * @param map
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String showMain(HttpSession session, ModelMap map) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        List<TblUserinfo> tblUserinfos = tblUserinfoMapper.selectAll();
        map.put("users",tblUserinfos);
        map.put("currentUser",currentUser);
        return "index";
    }

    /**
     * 跳转到找回密码页面
     * @return
     */
    @RequestMapping(value = "/findPsd", method = RequestMethod.GET)
    public String findPsd() {
        return "findPassword";
    }

    @RequestMapping(value = "/findPsd", method = RequestMethod.POST)
    public String findPsdPost(HttpServletRequest request, ModelMap map) {
        String phone = request.getParameter("mobilephone");
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("mobilephone", phone);
        TblUserinfo tblUserinfo = tblUserinfoMapper.selectOneByExample(example);
        if(tblUserinfo!=null) {
            map.addAttribute("id",tblUserinfo.getId());
        }
        return "newPassword";
    }
    
    @Log(methodFunctionDescribe = "用户修改密码",businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/updatePsd", method = RequestMethod.POST)
    public String updatePsd(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");

        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setPassword(password);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);

        return "redirect:/login";
    }

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping(value = {"/login",""}, method = RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public String file() {
        return "fileUpload";
    }
    @RequestMapping(value = "/findPassword", method = RequestMethod.GET)
    public String findPassword() {
        return "findPassword";
    }


    /**
     * 跳转到注册页面
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "register";
    }
    
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public String time() {
        return "timeline";
    }
    
    @Log(methodFunctionDescribe = "用户注册",businessType = BusinessType.REGISTER)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(TblUserinfo userinfo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("username",userinfo.getUsername());
        List<TblUserinfo> tblUserinfos = tblUserinfoMapper.selectByExample(example);
        if(tblUserinfos.size()> 0) {
            redirectAttributes.addFlashAttribute("errorUsername","用户名已存在");
        }
        example.createCriteria().andEqualTo("mobilephone",userinfo.getMobilephone());
        List<TblUserinfo> tblUserinfos1 = tblUserinfoMapper.selectByExample(example);
        if(tblUserinfos1.size()> 0) {
            redirectAttributes.addFlashAttribute("errorMobilephone","手机号已注册过");
            return "redirect:/register";
        }

        //注册用户
        userinfo.setCreatetime(new Date());
        userinfo.setCreateip(IpUtil.getIpAddress(request));
        tblUserinfoMapper.insert(userinfo);

        //注册成功添加一条NDA模板
        TblNdaitemtpl tblNdaitemtpl = new TblNdaitemtpl();
        String ndaTitle = "模板";
        String ndaItem = NdaConst.NDAITEM_TPL;
        tblNdaitemtpl.setNdatitle(ndaTitle);
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setCreateip(IpUtil.getIpAddress(request));
        tblNdaitemtpl.setCreatetime(LocalDateTime.now());
        tblNdaitemtpl.setCreateusername(userinfo.getUsername());
        tblNdaitemtplMapper.insert(tblNdaitemtpl);

        return "redirect:/login";
    }

    /**
     * 用户登录
     * @param tbUser 登录用户信息
     * @param session 登录成功，用户信息存放session 中
     * @param redirectAttributes 登录失败信息
     * @return
     */
    @Log(methodFunctionDescribe = "用户登录",businessType = BusinessType.LOGIN)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(TblUserinfo tbUser, HttpSession session, RedirectAttributes redirectAttributes) {

        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("username",tbUser.getUsername());
        TblUserinfo tblUserinfo = tblUserinfoMapper.selectOneByExample(example);
        if(tblUserinfo != null) {
            if(tblUserinfo.getPassword().equals(tbUser.getPassword())) {
                session.setAttribute("currentUser",tblUserinfo);
                session.setMaxInactiveInterval(60 * 60 * 2);//单位 秒
                /* Sunct, 2019.10.15
                *  检查客户端是否移动设备，并写入session
                * */
                HttpServletRequest servletRequest =
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                /*Enumeration<String> HeaderList = servletRequest.getHeaderNames();
                System.out.println("=======Header content======>>");
                while (HeaderList.hasMoreElements()) {
                    String curHeaderName = HeaderList.nextElement();
                    String curContent = servletRequest.getHeader(curHeaderName).toLowerCase();
                    System.out.println("Content of " + curHeaderName + ": " + curContent);
                }
                System.out.println("<<=====Header content========");
                */
                /*UserAgentUtil usUtil = new UserAgentUtil();
                usUtil.CheckClientandSave2Session(servletRequest);*/
                return "redirect:/index";
            }else {
                redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
                return "redirect:/login";
            }
        }else {
            redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
            return "redirect:/login";
        }
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe = "用户退出登录",businessType = BusinessType.LOGOUT)
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null) {
            session.removeAttribute("currentUser");
            session.invalidate();//关闭session
        }
        return "redirect:/login";
    }
    
    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
    @ResponseBody
    public String code(HttpServletRequest request) {
        String phone = request.getParameter("phone");
        String randomNumeric = RandomStringUtils.randomNumeric(6);//生成六位验证码
        CommonResponse commonResponse = aliyunSms(phone, randomNumeric);

        String data = commonResponse.getData();
       // System.out.println(data);

        return randomNumeric;
    }

    public CommonResponse aliyunSms(String phoneNo, String randomNumeric) {
        CommonResponse response = null;
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIR4PuLwn9seqd", "m4liNPjB2VRC5cUcay7pBOsqgR5fvj");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phoneNo);
        request.putQueryParameter("SignName", "web网站");
        request.putQueryParameter("TemplateCode", "SMS_160572353");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + randomNumeric + "\"}");
        try {
            response = client.getCommonResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void aliyunSmsForPsd(String phoneNo, String randomNumeric) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIR4PuLwn9seqd", "m4liNPjB2VRC5cUcay7pBOsqgR5fvj");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();

        String TemplateParam = "{\"code\":\"" + randomNumeric + "\"}";

        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNo);
        request.putQueryParameter("SignName", "lick小说网");
        request.putQueryParameter("TemplateCode", "SMS_162545299");
        request.putQueryParameter("TemplateParam", TemplateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }


}
