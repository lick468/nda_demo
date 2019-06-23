package com.nenu.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nenu.domain.TblNdashare;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.utils.IpUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 登录业务层
 */
@Controller
public class LoginController {

    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;

    @Autowired
    private TblNdashareMapper tblNdashareMapper;

    /**
     *  登录成功后，进入main页面
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

        //分享给我的，我未读的消息
        Example exampleShare = new Example(TblNdashare.class);
        exampleShare.createCriteria().andEqualTo("status",0).andEqualTo("username",currentUser.getUsername());
        List<TblNdashare> tblNdashares = tblNdashareMapper.selectByExample(exampleShare);
        map.put("totalShare",tblNdashares.size());
        map.put("tblNdashares",tblNdashares);

        //我分享的消息的回复(被拒绝的)
        Example exampleRefuse = new Example(TblNdashare.class);
        exampleRefuse.createCriteria().andEqualTo("createusername",currentUser.getUsername()).andEqualTo("status",3);
        List<TblNdashare> sharesRefuse = tblNdashareMapper.selectByExample(exampleRefuse);
        map.put("totalRefuse",sharesRefuse.size());
        map.put("sharesRefuse",sharesRefuse);

        //我分享的消息的回复（被修改的）
        Example exampleUpdate = new Example(TblNdashare.class);
        exampleUpdate.createCriteria().andEqualTo("createusername",currentUser.getUsername()).andEqualTo("status",2);
        List<TblNdashare> sharesUpdate = tblNdashareMapper.selectByExample(exampleUpdate);
        map.put("totalUpdate",sharesUpdate.size());
        map.put("sharesUpdate",sharesUpdate);

        return "main";
    }

    /**
     * 跳转到找回密码页面
     * @return
     */
    @RequestMapping(value = "/findPsd", method = RequestMethod.GET)
    public String findPsd() {
        return "findPsd";
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
        return "newPsd";
    }
    @RequestMapping(value = "/updatePsd", method = RequestMethod.POST)
    public String updatePsd(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        //Example example = new Example(TbUser.class);
        //  example.createCriteria().andEqualTo("id", id);
        TblUserinfo userinfo = new TblUserinfo();
        userinfo.setId(Integer.valueOf(id));
        userinfo.setPassword(password);
        tblUserinfoMapper.updateByPrimaryKeySelective(userinfo);

        return "success";
    }

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping(value = {"/login",""}, method = RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

    /**
     * 跳转到注册页面
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "register";
    }
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

        return "redirect:/login";
    }

    /**
     * 用户登录
     * @param tbUser 登录用户信息
     * @param session 登录成功，用户信息存放session 中
     * @param redirectAttributes 登录失败信息
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(TblUserinfo tbUser, HttpSession session, RedirectAttributes redirectAttributes) {

        Example example = new Example(TblUserinfo.class);
        example.createCriteria().andEqualTo("username",tbUser.getUsername());
        TblUserinfo tblUserinfo = tblUserinfoMapper.selectOneByExample(example);
        if(tblUserinfo != null) {
            if(tblUserinfo.getPassword().equals(tbUser.getPassword())) {
                session.setAttribute("currentUser",tblUserinfo);
                session.setMaxInactiveInterval(60 * 60 * 2);//单位 秒
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
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpSession session) {
        String id = request.getParameter("id");
        session.removeAttribute("currentUser");
        return "login";
    }
    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
    @ResponseBody
    public String code(HttpServletRequest request) {
        String phone = request.getParameter("phone");
        String randomNumeric = RandomStringUtils.randomNumeric(6);//生成六位验证码
        CommonResponse commonResponse = aliyunSms(phone, randomNumeric);

        String data = commonResponse.getData();
        System.out.println(data);

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
