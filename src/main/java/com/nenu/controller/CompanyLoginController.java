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
import com.nenu.domain.TblNdashare;
import com.nenu.domain.TblOrgnization;
import com.nenu.mapper.TblNdadocinfoMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.mapper.TblOrgnizationMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 公司登录业务层
 */
@Controller
@RequestMapping(value = "/company")
@Log(classFunctionDescribe = "公司业务")
public class CompanyLoginController {

   @Autowired
   private TblOrgnizationMapper orgnizationMapper;

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
        TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
        map.put("currentCompany",currentCompany);
        return "company/main";
    }

    /**
     * 跳转到找回密码页面
     * @return
     */
    @RequestMapping(value = "/findPsd", method = RequestMethod.GET)
    public String findPsd() {
        return "company/findPassword";
    }

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public String share() {
        return "company/share";
    }
    @RequestMapping(value = "/findPsd", method = RequestMethod.POST)
    public String findPsdPost(HttpServletRequest request, ModelMap map) {
        String phone = request.getParameter("mobilephone");
        Example example = new Example(TblOrgnization.class);
        example.createCriteria().andEqualTo("telephone", phone);
        TblOrgnization tblOrgnization = orgnizationMapper.selectOneByExample(example);
        if(tblOrgnization != null) {
            map.put("id",tblOrgnization.getId());
        }
        return "company/newPassword";
    }
    @Log(methodFunctionDescribe="找回秘密",businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/updatePsd", method = RequestMethod.POST)
    public String updatePsd(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");

        TblOrgnization orgnization = new TblOrgnization();
        orgnization.setId(Integer.valueOf(id));
        orgnization.setPassword(password);
        orgnizationMapper.updateByPrimaryKeySelective(orgnization);

        return "redirect:/company/login";
    }

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping(value = {"/login",""}, method = RequestMethod.GET)
    public String showLogin() {
        return "company/login";
    }


    /**
     * 跳转到注册页面
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "company/register";
    }
    @Log(methodFunctionDescribe = "公司管理员注册",businessType = BusinessType.REGISTER)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(TblOrgnization orgnization, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Example example = new Example(TblOrgnization.class);
        example.createCriteria().andEqualTo("orgname",orgnization.getOrgname());
        List<TblOrgnization> tblOrgnizations = orgnizationMapper.selectByExample(example);
        if(tblOrgnizations.size()> 0) {
            redirectAttributes.addFlashAttribute("errorOrgname","该公司已注册");
        }
        example.createCriteria().andEqualTo("orgleader",orgnization.getOrgleader());
        List<TblOrgnization> tblOrgnizations1 = orgnizationMapper.selectByExample(example);
        if(tblOrgnizations1.size()> 0) {
            redirectAttributes.addFlashAttribute("errorUsername","用户名已存在");
        }

        example.createCriteria().andEqualTo("telephone",orgnization.getTelephone());
        List<TblOrgnization> tblOrgnizations2 = orgnizationMapper.selectByExample(example);
        if(tblOrgnizations2.size()> 0) {
            redirectAttributes.addFlashAttribute("errorMobilephone","手机号已注册过");
            return "redirect:/company/register";
        }

        //注册公司用户
        orgnization.setCreatetime(new Date());
        orgnizationMapper.insert(orgnization);
        return "redirect:/company/login";
    }

    /**
     * 用户登录
     * @param orgnization 登录用户信息
     * @param session 登录成功，用户信息存放session 中
     * @param redirectAttributes 登录失败信息
     * @return
     */
    @Log(methodFunctionDescribe = "公司管理员登录",businessType = BusinessType.LOGIN)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(TblOrgnization orgnization, HttpSession session, RedirectAttributes redirectAttributes) {

        Example example = new Example(TblOrgnization.class);
        example.createCriteria().andEqualTo("orgleader",orgnization.getOrgleader());
        TblOrgnization tblOrgnization = orgnizationMapper.selectOneByExample(example);
        if(tblOrgnization != null) {
            if(tblOrgnization.getPassword().equals(orgnization.getPassword())) {
                session.setAttribute("currentCompany",tblOrgnization);
                session.setMaxInactiveInterval(60 * 60 * 2 );
                return "redirect:/company/index";
            }else {
                redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
                return "redirect:/company/login";
            }
        }else {
            redirectAttributes.addFlashAttribute("msg","用户名或密码错误");
            return "redirect:/company/login";
        }
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe = "公司管理员退出登录",businessType = BusinessType.LOGOUT)
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null) {
            session.removeAttribute("currentCompany");
            session.invalidate();//关闭session
        }
        return "redirect:/company/login";
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



    /**
     * 获取自己分享给别人的信息，用于表格显示
     * @param session
     * @param pageSize
     * @param offset
     * @param searchShare
     * @return
     */
    @Log(methodFunctionDescribe="获取分享的数据信息",businessType = BusinessType.PAGELIST)
    @RequestMapping(value="/getShareData",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getShareData(HttpSession session, Integer pageSize, Integer offset, String searchShare) {
        TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
        Map<String, Object> map = new HashMap<String, Object>();
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        List<TblNdashare> tblNdashares = new ArrayList<>();
        if(searchShare!=null && searchShare.length() > 0) {
            criteria.orLike("createusername","%" + searchShare + "%").orLike("ndatitle","%" + searchShare + "%").orLike("username","%" + searchShare + "%");
            Example.Criteria name = example.createCriteria();
            name.andEqualTo("orgname",currentCompany.getOrgname());
            example.and(name);

            tblNdashares = tblNdashareMapper.selectByExample(example);
            List<TblNdashare> rows = new ArrayList<>();
            for(int i=offset;i<offset+pageSize;i++) {
                if(tblNdashares.size() > i) {
                    rows.add(tblNdashares.get(i));
                }
            }
            map.put("total", tblNdashares.size());
            map.put("rows", rows);
            return map;
        }
        criteria.andEqualTo("orgname",currentCompany.getOrgname());
        tblNdashares = tblNdashareMapper.selectByExample(example);
        List<TblNdashare> rows = new ArrayList<>();
        for(int i=offset;i<offset+pageSize;i++) {
            if(tblNdashares.size() > i) {
                rows.add(tblNdashares.get(i));
            }
        }
        map.put("total", tblNdashares.size());
        map.put("rows", rows);
        return map;
    }

    /**
     * 跳转到分享页面
     * @param
     * @return
     */
    @GetMapping(value = "/fileTranslation")
    public String shareFile() {
        return "/company/timeline";
    }
    /**
     * 跳转到详情页面
     * @param
     * @return
     */
    @GetMapping(value = "/detail")
    public String detail(HttpSession session,ModelMap map) {
        TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
        map.put("company",currentCompany);
        return "/company/detail";
    }
    @Log(methodFunctionDescribe="修改个人信息",businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateCompany")
    public String updateCompany(TblOrgnization orgnization,ModelMap map) {
        orgnizationMapper.updateByPrimaryKeySelective(orgnization);
        map.put("company",orgnization);
        return "/company/detail";
    }



}
