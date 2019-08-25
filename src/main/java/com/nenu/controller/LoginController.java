package com.nenu.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.utils.IpUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
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

    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

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
        return "main";
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
        String ndaItem = "<p><strong><strong>保密合同</strong></strong></p>\n" +
                "\n" +
                "<p><strong><strong>甲方： </strong></strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><strong>&nbsp;</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><strong><strong>乙方：万晟佳音</strong></strong><strong><strong>(</strong></strong><strong><strong>厦门</strong></strong><strong><strong>)</strong></strong><strong><strong>知识产权事务有限公司</strong></strong></p>\n" +
                "\n" +
                "<p>鉴于甲乙双方（互为披露方和接受方）当事人拟进行业务合作（以下称&ldquo;本目的&rdquo;），为使合作顺利进行，并确保各项保密信息之秘密性，甲乙双方同意并约定如下：</p>\n" +
                "\n" +
                "<p><strong><strong>第一条 保密信息</strong></strong></p>\n" +
                "\n" +
                "<p>保密信息包括但不限于由接受方和/或其代表从披露方获得的，属披露方或其任何附属公司、关联公司所有或合法拥有的，不为公众所知的所有信息、文件、资料、数据、技术需求、改善需求或技术等，包括但不限于技术信息、商业信息等。</p>\n" +
                "\n" +
                "<p><strong><strong>第二条 保密人员</strong></strong></p>\n" +
                "\n" +
                "<p>保密人员包括但不限于接受方之一切与其在职务上或业务上有知悉保密信息之必要的在职员工、代表人、代理人及外聘顾问等。</p>\n" +
                "\n" +
                "<p><strong><strong>第三条 </strong></strong><strong><strong>保密措施</strong></strong></p>\n" +
                "\n" +
                "<p>接受方应当以审慎、诚实的态度对待披露方的保密信息。</p>\n" +
                "\n" +
                "<p>1、遵守披露方有关保密的各项管理规定；</p>\n" +
                "\n" +
                "<p>2、未经披露方书面许可，不得将所知的披露方保密信息以任何方式提供给任何第三方，也不得擅自披露这些保密信息；</p>\n" +
                "\n" +
                "<p>3、除了完成双方约定的工作目的之外，未经披露方书面许可，不得擅自使用披露方的保密信息；</p>\n" +
                "\n" +
                "<p>4、未经披露方书面许可，不得带走从披露方得到的任何载有披露方保密信息的介质，包括但不限于文档、图纸、资料、磁盘、录音笔、胶片等；</p>\n" +
                "\n" +
                "<p>5、接受方发现任何第三人不当使用保密信息时，应立即通知披露方并与披露方充分合作，以利披露方维护其权益，或防止不当使用之情形继续存在。</p>\n" +
                "\n" +
                "<p>保密信息的披露要符合以下要求：</p>\n" +
                "\n" +
                "<p>1、披露方以书面、电子或其他形式披露时在信息上做有保密标记；</p>\n" +
                "\n" +
                "<p>2、口头披露时披露方在披露时说明其为保密信息，而且在披露之日起10日之内向接受方发出书面公函予以指明该保密性；</p>\n" +
                "\n" +
                "<p>3、以其他形式披露时书面指明其为保密信息；</p>\n" +
                "\n" +
                "<p>4、虽然不属于上述三种情形，但信息自身性质表明其明显是保密的。</p>\n" +
                "\n" +
                "<p><strong><strong>第四条 保密期限</strong></strong></p>\n" +
                "\n" +
                "<p>自接受保密信息起至甲乙双方具体业务合作结束后5年，若有本合同第五条所述情形的，则不受保密期限约束。</p>\n" +
                "\n" +
                "<p><strong><strong>第五条 </strong></strong><strong><strong>除外条款</strong></strong></p>\n" +
                "\n" +
                "<p>保密信息之任一部分具有下列情形之一时，该部分不适用本合同：</p>\n" +
                "\n" +
                "<p>1、非因接受方之故意或过失，保密信息已为公众所知悉；</p>\n" +
                "\n" +
                "<p>2、接受方自第三人处合法取得保密信息，且该第三人未要求接受方保密；</p>\n" +
                "\n" +
                "<p>3、接受方未使用保密信息而独立开发出相同技术；</p>\n" +
                "\n" +
                "<p>4、接受方为履行任何具有管辖权的法院、政府或监管机构的要求或命令或因法律法规规定而披露保密信息；</p>\n" +
                "\n" +
                "<p>5、经披露方书面同意而披露保密信息。</p>\n" +
                "\n" +
                "<p><strong><strong>第六条 </strong></strong><strong><strong>权利保留</strong></strong></p>\n" +
                "\n" +
                "<p>1、披露方的一切保密信息均为披露方所有的财产，本合同之签订并不代表披露方已明示或暗示，或有义务将其拥有之相关专利权、著作权、商标权、集成电路布图设计权或其他知识产权授权予接受方。</p>\n" +
                "\n" +
                "<p>2、本合同之签订并未在甲乙双方之间创设诸如代理、聘雇或合伙之关系；同时亦不表示承诺开始进行或于未来从事任何技术上或商务上之合作、计划或交易。</p>\n" +
                "\n" +
                "<p>3、披露方不担保保密信息为正确无误、技术上可以操作使用或其内容为最新、不侵害任何第三人之知识产权。接受方同意不因此请求披露方赔偿损失。</p>\n" +
                "\n" +
                "<p><strong><strong>第七条 保密信息的</strong></strong><strong><strong>返还</strong></strong></p>\n" +
                "\n" +
                "<p>1、披露方可随时以书面形式要求接受方将依据本合同的条款而获披露的电子、书面保密信息及任何副本返还或销毁。</p>\n" +
                "\n" +
                "<p>2、当接受方完成本目的，或本合同经终止、解除或有效期间届满后，接受方除必要的信息登记备案外应将保密信息之原本、复印件或复制本返还披露方，或经披露方书面同意自行将保密信息销毁。</p>\n" +
                "\n" +
                "<p><strong><strong>第八条</strong></strong><strong>&nbsp;</strong><strong><strong>违约责任</strong></strong></p>\n" +
                "\n" +
                "<p>1、接受方因违反本合同而直接导致披露方遭受损失的，应对披露方能够充分且直接证明的损失进行赔偿。</p>\n" +
                "\n" +
                "<p>2、接受方之员工、代表人、代理人及外聘顾问违反本合同任一约定时，视为接受方违反本合同。</p>\n" +
                "\n" +
                "<p><strong><strong>第九条</strong></strong><strong>&nbsp;</strong><strong><strong>争议解决</strong></strong></p>\n" +
                "\n" +
                "<p>如甲乙双方就本合同内容或其执行发生任何争议，甲乙双方应进行友好协商；协商不成时，任何一方均可向厦门市湖里区人民法院提起诉讼解决。</p>\n" +
                "\n" +
                "<p><strong><strong>第十条</strong></strong><strong>&nbsp;</strong><strong><strong>其他</strong></strong></p>\n" +
                "\n" +
                "<p>1、本合同自双方盖章之日起生效。</p>\n" +
                "\n" +
                "<p>2、本合同正本一式二份，由甲乙双方各执一份为凭。</p>\n";
        tblNdaitemtpl.setNdatitle(ndaTitle);
        tblNdaitemtpl.setNdaitem(ndaItem);
        tblNdaitemtpl.setCreateip(IpUtil.getIpAddress(request));
        tblNdaitemtpl.setCreatetime(new Date());
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
     * @return
     */
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
