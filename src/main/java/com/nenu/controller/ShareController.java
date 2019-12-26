package com.nenu.controller;

import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.constant.NdaConst;
import com.nenu.domain.*;
import com.nenu.mapper.TblNdabasicinfoMapper;
import com.nenu.mapper.TblNdadocinfoMapper;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.service.*;
import com.nenu.utils.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.nenu.constant.FileDirConst.*;
import static com.nenu.utils.RSAUtils.*;

/**
 * NDA共享业务控制层
 * Author: Sunct, 2019.11.30开始，增加了一个Service层，
 * 即比较复杂一点的数据库处理逻辑和文件上传下载处理业务都放到了Service层
 */
@Controller
//@SessionScope
@Log(classFunctionDescribe = "用户交易业务_Controller")
public class ShareController{
    @Autowired
    private TblNdashareMapper tblNdashareMapper;

    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;

    @Autowired
    private TblNdabasicinfoMapper tblNdabasicinfoMapper;

    @Autowired
    private TblNdadocinfoMapper tblNdadocinfoMapper;

    @Autowired
    MyServerPathProperties myServerFilepathProperties;

    private  static final String FILESEPARATOR = File.separator;
    /*private  static final String NDA_ROOTDIR = "C:\\ndadata\\";
    private static final String DEFAULT_PREFIX_PDFDOWNPATH_PASS = NDA_ROOTDIR + "download\\outpath\\";
    private static final String DEFAULT_PREFIX_PDFDOWNPATH_DECRPT = NDA_ROOTDIR + "download\\decpath\\";
    private static final String DEFAULT_TMPPDFFILENAME = "tmpfile.pdf";
    private static final String DEFAULT_FILEEXTENSION = ".pdf";
    private static final String DEFAULT_PREFIX_UPPATH_PASS = NDA_ROOTDIR + "upload\\outpath\\";
    private static final String DEFAULT_PREFIX_UPPATH_ORG = NDA_ROOTDIR + "upload\\orgpath\\";
    private static final String DEFAULT_PREFIX_UPPATH_NDADOC = NDA_ROOTDIR + "upload\\ndadoc\\";
    private static final String DEFAULT_SUFFIX_NDADOC_ORG = "_org";
    private static final String DEFAULT_SUFFIX_NDADOC_PASS = "_pass";
    */

    
    @Resource(name="ndashareservice")
    private INdaShareService ndaShareService;
    @Resource(name="filetransferservice")
    private IFileIOService fileTransferService;
    @Resource(name="userservice")
    private IUserService userService;
    @Resource(name = "ndatemplateservice")
    private INdaTemplateService ndaTemplateService;
    @Resource(name="ndadocservice")
    private INdaDocService ndaDocService;

    /**
     * 跳转到share页面
     * @return
     */
    @GetMapping(value = "/share")
    public String share() {
        return "share";
    }

    /**
     * 显示NDA条款
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "/showNDA")
    public String showNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        return "showNDA";
    }
    /**
     * 显示NDA条款(未同意之前的查看)
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "/showNDAForLookOrBack")
    public String showNDAForLookOrBack(HttpServletRequest request, HttpSession session, ModelMap map) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        if (null == currentUser)
            return "login";
        String ndaid = request.getParameter("ndaid");
        RetrieveNdaBasicInfoandTemplates(currentUser, ndaid, map, false);
        map.put("curstatus", request.getParameter("curstatus"));
        map.put("shareor2", request.getParameter("shareor2"));
        //System.out.println(request.getParameter("curstatus") + "   " + request.getParameter("shareor2"));
        return "showNDAForLookOrBack";
    }

    /**现在还存在一个问题：如果该NDA有文件上传到了服务器，需要删除相应文件
     * 删除NDA交易请求
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe="删除NDA交易请求",businessType = BusinessType.DELETE)
    @GetMapping(value = "/deleteNDA")
    @ResponseBody
    public String deleteNDA(HttpServletRequest request) {
        String ndaid = request.getParameter("ndaid");
        String curStatus = request.getParameter("curstatus");
        /*Example example =  new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        tblNdashareMapper.deleteByExample(example);
        tblNdabasicinfoMapper.deleteByPrimaryKey(ndaid);*/
        try {
            if (StringUtils.isBlank(curStatus)
                    || NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("交易请求").equals(curStatus))
                ndaShareService.UpdateNdaStatus(ndaid, NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("甲方撤回"));
            else
                ndaShareService.UpdateNdaStatus(ndaid, NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("我方中止"));
            return "success";
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return "error";
        }
    }

    /**
     * 拒绝NDA交易请求
     * @param request
     * @param map
     * @param session
     * @return
     */
    @Log(methodFunctionDescribe="拒绝NDA交易请求",businessType = BusinessType.UPDATE)
    @GetMapping(value = "/refuseNDA")
    @ResponseBody
    public String refuseNDA(HttpServletRequest request, ModelMap map,HttpSession session) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        if(tblNdashare!= null){
            TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
            String curUserName = currentUser.getUsername();
            // 发起人拒绝 2 我方中止 2 对方中止
            if(curUserName.equals(tblNdashare.getCreateusername())) {
                tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("我方中止"));//2
                //tblNdashare.setReceiverstatus("2");
                // 接收人拒绝 3 我方拒绝 3 对方拒绝
            }else  if(curUserName.equals(tblNdashare.getUsername())){
                tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("对方拒绝"));//3
                //tblNdashare.setReceiverstatus("3");
            }
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        return "success";
    }

    /**
     * 同意请求 开始活动交易
     * @param request
     * @param map
     * @return
     */
    @Log(methodFunctionDescribe="同意NDA交易请求",businessType = BusinessType.UPDATE)
    //@GetMapping(value = "/agreeNDA")
    @ResponseBody
    @Transactional      //@Transactional在接收到RuntimeException时才会回滚事务
    public String agreeNDA(HttpServletRequest request, ModelMap map) throws Exception{
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        if(null == tblNdashare) {
            return "success";
        }
    
        //ndaShareService.AgreeNda(ndaid);
        String createUsername = tblNdashare.getCreateusername();
        String ndaDocTitle = tblNdashare.getNdatitle();

        StringBuffer passFilepath = new StringBuffer(DEFAULT_PREFIX_UPPATH_PASS);
        passFilepath.append(createUsername).append("\\");
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        //判断发起人是否携带文件发起交易 1 是 2 不是
        /*Map<String, Object> receiverMap;
        receiverMap = initKey();
        String receiverPrivKey = getPrivateKeyStr(receiverMap);
        tblNdabasicinfo.setReceiverprivatekey(receiverPrivKey);
        tblNdabasicinfo.setReceiverpubkey(getPublicKeyStr(receiverMap));*/
        String senderPubKey = tblNdabasicinfo.getSenderpubkey();
        String receiverPrivKey = tblNdabasicinfo.getReceiverprivatekey();

        if(tblNdashare.getHavefile().equals("1")) {
            /*文件名是 "上传文件名_time.pdf"形式*/
            String passFilename = "";
            String orgFileName = ""; //上传时选择的原始文件名
            String upFilepath = tblNdashare.getFilepath();
            String fileExtension = "";
            if (upFilepath.indexOf(":\\") >= 0) {//如果是全路径
                //String[] strArray = upFilepath.split("\\\\");
                //int fileParts = strArray.length;
                passFilename = upFilepath.substring(upFilepath.lastIndexOf("\\")+1);
                passFilepath.append(passFilename);
                int pointPos = passFilename.lastIndexOf(".");
                if (pointPos > 0)
                    orgFileName = passFilename.substring(0, pointPos);
                else
                    orgFileName = passFilename;
            } else {
                passFilepath.append(upFilepath);
                passFilename = upFilepath;
                int pointPos = passFilename.lastIndexOf("_");
                if (pointPos > 0)
                    orgFileName = passFilename.substring(0, pointPos);
                else {
                    pointPos = passFilename.lastIndexOf(".");
                    if (pointPos > 0)
                        orgFileName = passFilename.substring(0, pointPos);
                    else
                        orgFileName = passFilename;
                }
            }

            String uploadFileHash = "";
            int pointPos = passFilename.lastIndexOf(".");
            if (pointPos > 0)
                fileExtension = passFilename.substring(pointPos + 1); //扩展名

            try {
                // 对加密后的进行进行上传
                uploadFileHash = IPFSUtils.upload(passFilepath.toString());
                //System.out.println("first file hash: " + uploadFileHash);
                // 上传成功后删除本地文件
                //myFileUtils.DeleteFilewithParentDir(passFilepath.toString(), true);
                myFileUtils.deleteFile(passFilepath.toString());

                //对上传文件返回的hash  进行加密
                uploadFileHash = Base64.getEncoder().encodeToString(encrypt(uploadFileHash.getBytes(), senderPubKey));

                TblNdadocinfo ndadocinfo = new TblNdadocinfo();
                ndadocinfo.setNdadocid(tblNdashare.getNdaid());
                ndadocinfo.setDochash(uploadFileHash);
                ndadocinfo.setFilename(orgFileName);
                ndadocinfo.setFileextension(fileExtension);
                ndadocinfo.setUploadusername(tblNdashare.getCreateusername());
                ndadocinfo.setUploadtime(new Date());
                ndadocinfo.setUploadip(IpUtil.getIpAddress(request));

                // 构建时间戳  key=文件hash&sign=send&sender=发件人&recevier=收件人&timestamp=当地时间戳  之后MD5加密
                String timestamp = new StringBuilder("key=").append(uploadFileHash).append("&sign=send&sender")
                                    .append(tblNdashare.getCreateusername())
                                    .append("&receiver=").append(tblNdashare.getUsername())
                                    .append("&timastamp=").append(System.currentTimeMillis()).toString();
                timestamp = MD5Util.getMD5(timestamp);
                ndadocinfo.setTimestamp(timestamp);

                tblNdadocinfoMapper.insert(ndadocinfo);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("保存文档信息时出错!", e);
            }
        }
        long time = System.currentTimeMillis();
        StringBuffer pathPrefix = new StringBuffer(DEFAULT_PREFIX_UPPATH_NDADOC)
                                        .append(createUsername).append("\\");
        //StringBuffer fileSuffix = new StringBuffer("_").append(time).append(".pdf");
        /*StringBuffer pathNDA = new StringBuffer().append(pathPrefix)
                                    .append(ndaDocTitle).append(DEFAULT_SUFFIX_NDADOC_ORG)
                                    .append("_").append(time).append(".pdf");//(fileSuffix);*/
        pathPrefix.append(ndaDocTitle).append(DEFAULT_SUFFIX_NDADOC_ORG)
                .append("_").append(time).append(".pdf");
        //StringBuffer passPathNDA = new StringBuffer().append(pathPrefix)
        //                                .append(ndaDocTitle).append(DEFAULT_SUFFIX_NDADOC_PASS).append(fileSuffix);
        String ndaFilename = pathPrefix.toString();
        String ndaItems = tblNdabasicinfo.getNdaitems();
        myFileUtils.CreateFilewithDir(ndaFilename);
        //myFileUtils.CreateFilewithDir(passPathNDA.toString());
        PDFUtils.createNdaItemFile(ndaFilename, tblNdashare, ndaItems);

        String ndaDocHash = null;
        try {
            //对文件使用发送人公钥加密
            //上传到IPFS上
            FileInputStream fis = new FileInputStream(ndaFilename);
            //byte[] passData;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            //encryptFile(pathNDA,passPathNDA,tblNdabasicinfo.getSenderpubkey());
            encryptStream(fis, bos, senderPubKey);
            //passData = bos.toByteArray();
            //upload1 = IPFSUtils.upload(passPathNDA);
            ndaDocHash = IPFSUtils.upload(bos.toByteArray());
            fis.close();
            bos.close();
            //删除文件
            myFileUtils.deleteFile(ndaFilename);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存文档时出错!", e);
        }

        try {
            //更新tblNdabasicinfo表 添加接收人签名 NDA条款信息
            tblNdabasicinfo.setFilename(ndaDocTitle);
            tblNdabasicinfo.setFileextension("pdf");
            tblNdabasicinfo.setFilehash(ndaDocHash);
            byte[] sign = new byte[0];
            try {
                sign = sign(ndaItems.getBytes(), receiverPrivKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tblNdabasicinfo.setReceiversign(new String(sign));
    
            tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);
            tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("活动交易"));//"1");
            //tblNdashare.setReceiverstatus("1");
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新NDA信息时出错!", e);
        }
    }
    
    /**
     * 同意请求 开始活动交易
     * @param request
     * @param map
     * @return
     */
    //@Transactional      //@Transactional在接收到RuntimeException时才会回滚事务
    //@PostMapping(value = "/agreeNDA")
    //@Log(methodFunctionDescribe="同意NDA交易请求", businessType = BusinessType.UPDATE)
    @GetMapping(value = "/agreeNDA")
    @ResponseBody
    public String agreeNDA2(HttpServletRequest request, ModelMap map){
        TblUserinfo currentUser = null;
        try {
            currentUser = (TblUserinfo) request.getSession().getAttribute("currentUser");
        } catch (Exception e) {
            e.printStackTrace();
            return "login";
        }
        String ndaid = request.getParameter("ndaid");
        String ipAddr = IpUtil.getIpAddress(request);
        boolean bSucceed = false;
        try {
            bSucceed = ndaShareService.AgreeNda(currentUser, ndaid, ipAddr);
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println(e.getMessage());
            //throw new RuntimeException("Agree NDA", e);
        } finally {
            if (bSucceed) {
                System.out.println("success");
                return "success";
            } else {
                System.out.println("failed");
                return "failed";
            }
        }
    }
    
    /**
     * 显示修改NDA条款弹窗
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "/showEditNDAItem")
    public String showEditNDAItem(HttpServletRequest request, HttpSession session, ModelMap map) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String prevPage = request.getParameter("prevpage");
        if (null == currentUser)
            return "login";
        String ndaid = request.getParameter("ndaid");
        RetrieveNdaBasicInfoandTemplates(currentUser, ndaid, map, true);
        map.put("prevpage", prevPage);
        return "editndaitem";
    }
    
    private void RetrieveNdaBasicInfoandTemplates(TblUserinfo currentUser, String ndaid, ModelMap map,
                                                  boolean includeTemplates) {
        String username = currentUser.getUsername();
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        if (includeTemplates) {
            example = new Example(TblNdashare.class);
            example.createCriteria().andEqualTo("ndaid", ndaid);
            TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
            String partnerUserName;
            int belongedOrgId = -1;
            int partnerOrgId = -1;
            if (null != tblNdashare) {
                if (username.equals(tblNdashare.getCreateusername())) {
                    partnerUserName = tblNdashare.getUsername();
                } else {
                    partnerUserName = tblNdashare.getCreateusername();
                }
                TblOrgnization partnerOrg = userService.getBelongedOrg(partnerUserName);
                if (null != partnerOrg)
                    partnerOrgId = partnerOrg.getId();
            }
            TblOrgnization thisOrg = userService.getBelongedOrg(currentUser);
            if (null != thisOrg)
                belongedOrgId = thisOrg.getId();
            List<TblNdaitemtpl> ndaItemTpls = ndaTemplateService.RetrieveOrgNdaTemplates(currentUser, belongedOrgId,
                    partnerOrgId);
            map.put("ndaitemtemplates", ndaItemTpls);
        }
    }
    
    /**
     * 修改NDA条款
     * @param session
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe="修改NDA条款",businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updatendaitem")
    @ResponseBody
    public String updateNDAItems(HttpSession session, HttpServletRequest request) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String ndaId = request.getParameter("id");
        String ndaItem = request.getParameter("ndaitems");
        try {
            ndaShareService.UpdateNdaItems(ndaId, currentUser.getUsername(), ndaItem);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    /**
     * 发起NDA分享，在此时将文件由普通文件转换为加密文件()
     * 记录里保存的是文件名_time的形式
     * @param request
     * @param session
     * @return
     */
    @Log(methodFunctionDescribe="发起NDA分享",businessType = BusinessType.CREATE)
    //@PostMapping(value = "/share")
    @Transactional
    public String share0(HttpServletRequest request, HttpSession session) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        String title = request.getParameter("title");
        String uploadFilePath = request.getParameter("haveFile");

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
        //生成公钥私钥  私钥签名   公钥加密
        Map<String, Object> senderMap;
        Map<String, Object> receiverMap;
        String senderPubKey = "";
        String senderPrivKey = "";
        TblNdashare tblNdashare = new TblNdashare();
        try {
            senderMap = initKey();
            receiverMap = initKey();
            senderPubKey = getPublicKeyStr(senderMap);
            tblNdabasicinfo.setSenderpubkey(senderPubKey);
            senderPrivKey = getPrivateKeyStr(senderMap);
            tblNdabasicinfo.setSenderprivatekey(senderPrivKey);
            String receiverPrivKey = getPrivateKeyStr(receiverMap);
            tblNdabasicinfo.setReceiverprivatekey(receiverPrivKey);
            tblNdabasicinfo.setReceiverpubkey(getPublicKeyStr(receiverMap));

            byte[] sign = sign(tblNdaitemtpl.getNdaitem().getBytes(), senderPrivKey);
            tblNdabasicinfo.setSendersign(new String(sign));

            //插入一条NDA基本信息
            tblNdabasicinfoMapper.insert(tblNdabasicinfo);

            tblNdashare.setCreateusername(username);
            tblNdashare.setOrdernumber(1);
            tblNdashare.setOrgname(currentUser.getOrgname());
            tblNdashare.setCreatetime(new Date());
            tblNdashare.setOperateip(IpUtil.getIpAddress(request));
            tblNdashare.setNdaid(uuid);
            tblNdashare.setNdatitle(title);
            // 发送方  0 等待确认    接收人  0 交易请求
            tblNdashare.setSharestatus("0");
            //tblNdashare.setReceiverstatus("0");

            tblNdashare.setShareUserUploadCount(0);//初始化未读数量
            tblNdashare.setCreateUserUploadCount(0);//初始化未读数量

            if(uploadFilePath != null && uploadFilePath.length() > 0) {
                tblNdashare.setHavefile("1");
                String passPath = uploadFilePath.replace("\\orgpath\\","\\outPath\\"); // 加密后文件路径
                myFileUtils.CreateFilewithDir(passPath);
                //System.out.println("encryped file data in /share>>>");
                encryptFile(uploadFilePath, passPath, senderPubKey);
                //System.out.println("<<<encryped file data in /share END");
                String[] strArray = uploadFilePath.split("\\\\|/");
                String orgUpFilename = strArray[strArray.length - 1];
                myFileUtils.deleteFile(uploadFilePath);//DeleteFilewithParentDir(uploadFilePath, false);
                tblNdashare.setFilepath(orgUpFilename);
            }else {
                tblNdashare.setHavefile("2");
            }
            tblNdashare.setUsername(username1);
            //插入一条分享记录
            tblNdashareMapper.insert(tblNdashare);

        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("发起NDA共享时出错!");
        }
        return "redirect:/share";
    }
    
    @Log(methodFunctionDescribe="发起NDA分享",businessType = BusinessType.CREATE)
    @PostMapping(value = "/share")
    @Transactional
    public String share(HttpServletRequest request, HttpSession session) {
        //String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String uploadFilePath = request.getParameter("haveFile");
        String orgUpFilename = "";
        if(uploadFilePath != null && uploadFilePath.length() > 0) {
            String[] strArray = uploadFilePath.split("\\\\|/");
            orgUpFilename = strArray[strArray.length - 1];
        }
        //return orgUpFilename;
        try {
            Map<String, Object> senderMap;
            senderMap = initKey();
            String senderPubKey = getPublicKeyStr(senderMap);
            String senderPrivKey = getPrivateKeyStr(senderMap);
            if(uploadFilePath != null && uploadFilePath.length() > 0) {
                fileTransferService.EncryptUploadedFile(uploadFilePath, senderPubKey);
            }
            ndaShareService.ShareNda(request, currentUser, senderPubKey, senderPrivKey, orgUpFilename);
        } catch (Exception e) {
        
        } finally {
            return "redirect:/share";
        }
    }

    /** 用户通过拖曳或上传方式上传文件时，会触发此事件，
     * 将上传的文件暂存于服务器对应目录
     * CreateNDA.html
     */
    @PostMapping(value = "/upload")
    @ResponseBody
    public List<String> upload(MultipartHttpServletRequest requestFile, HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        StringBuilder basePathBuffer =
                new StringBuilder(DEFAULT_PREFIX_UPPATH_ORG).append(currentUser.getUsername()).append("\\");
        List<String> list = fileTransferService.UploadFileFromClient2Server(requestFile, basePathBuffer.toString());
        return list;
    }

    /**
     * 在操作一栏“同意”对应的动作
     * 跳转到分享页面
     * @param
     * @return
     */
    @GetMapping(value = "/fileTranslation")
    public String showNdaTimeline(HttpServletRequest request, HttpSession session, ModelMap map) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String id = request.getParameter("id");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        // 该用户是交易接收人
        if(tblNdashare.getUsername().equals(currentUser.getUsername())) {
            map.put("receiver",tblNdashare.getCreateusername());
            map.put("sender",tblNdashare.getUsername());
            // 更改未读信息数量
            tblNdashare.setCreateUserUploadCount(0);
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            // 该用户是交易发起人
        }else {
            map.put("receiver",tblNdashare.getUsername());
            map.put("sender",tblNdashare.getCreateusername());
            // 更改未读信息数量
            tblNdashare.setShareUserUploadCount(0);
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        map.put("ndaID",tblNdashare.getNdaid());
        //获取已发送文件记录用于 时间轴展示
        Example exampleTime = new Example(TblNdadocinfo.class);
        exampleTime.createCriteria().andEqualTo("ndadocid",tblNdashare.getNdaid());
        List<TblNdadocinfo> tblNdadocinfos = tblNdadocinfoMapper.selectByExample(exampleTime);
        map.put("ndaDocInfos",tblNdadocinfos);
        return "timeline";
    }

    /**
     * 活动交易 新发文件
     * @param requestFile  文件集合
     * @return
     */
    @Log(methodFunctionDescribe="分享文件",businessType = BusinessType.CREATE)
    //@PostMapping(value = "/fileUpload")
    @ResponseBody
    public JSON fileUpload(MultipartHttpServletRequest requestFile,HttpServletRequest request){
        JSON json = new JSONObject();
        String sender = request.getParameter("sender");
        String receiver = request.getParameter("receiver");
        String ndaID = request.getParameter("ndaID");
        if (null == sender || sender.isEmpty()
            || null == receiver || receiver.isEmpty()
            || null == ndaID || ndaID.isEmpty()) {
            return json;
        }
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndaID);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        if (null == tblNdabasicinfo) {
            return json;
        }
        long time = System.currentTimeMillis();
        String origUpFilePath = "C:/upload/"+sender+"/"+time+"/";
        /*String passPath = "C:\\outPath\\"+sender+"\\"+time+"\\";
        int i = passPath.lastIndexOf("\\");
        File pa = new File(passPath.substring(0,i));=>
        */
        String passPath = "C:/outPath/"+sender+"/"+time;
        File pa = new File(passPath);
        if(!pa.exists()) {
            pa.mkdirs();
        }

        /*存放在上传过程中产生的临时文件列表，准备最后删除*/
        List<String> orgUpFilePathList = new ArrayList<String>();
        List<String> passFilePathList = new ArrayList<String>();
        passPath += "/";
        String upload = "";
        String pubKey4Encrypt = tblNdabasicinfo.getSenderpubkey();
        if(tblNdabasicinfo.getInitiatorusername().equals(receiver)) {
            pubKey4Encrypt = tblNdabasicinfo.getReceiverpubkey();
        }

        Iterator<String> itr = requestFile.getFileNames();
        while (itr.hasNext()) {
            String uploadedFile = itr.next();
            MultipartFile file = requestFile.getFile(uploadedFile);
            String fullFileName = file.getOriginalFilename();
            int idxPoint = fullFileName.lastIndexOf(".");
            String fileName = fullFileName; //文件名   test.doc  test
            String fileExtension = ""; //文件名后缀  E:\test.doc  doc
            if (idxPoint < 0) {

            } else if (idxPoint == 0) {
                continue;
            } else {
                fileName = fullFileName.substring(0, idxPoint); //文件名   test.doc  test
                fileExtension = fullFileName.substring(idxPoint + 1); //文件名后缀  E:\test.doc  doc
            }
            String orgFullFilePath = origUpFilePath + fullFileName;
            String passFullFilePath = passPath + fullFileName;
            orgUpFilePathList.add(orgFullFilePath);
            passFilePathList.add(passFullFilePath);
            File serverLocalFile = new File(orgFullFilePath);
            if(!serverLocalFile.exists()){
                serverLocalFile.mkdirs();
            }
            try {
                file.transferTo(serverLocalFile);
                //上传之前先加密
                encryptFile(orgFullFilePath, passFullFilePath, pubKey4Encrypt);
                upload = IPFSUtils.upload(passFullFilePath);

                //对上传文件返回的hash  进行加密
                upload = Base64.getEncoder().encodeToString(encrypt(upload.getBytes(),pubKey4Encrypt));

                TblNdadocinfo ndadocinfo = new TblNdadocinfo();
                ndadocinfo.setNdadocid(ndaID);
                ndadocinfo.setDochash(upload);
                ndadocinfo.setFilename(fileName);
                ndadocinfo.setFileextension(fileExtension);
                ndadocinfo.setUploadusername(sender);
                ndadocinfo.setUploadtime(new Date());
                ndadocinfo.setUploadip(IpUtil.getIpAddress(request));
                // 构建时间戳  key=文件hash&sign=send&sender=发件人&recevier=收件人&timestamp=当地时间戳  之后MD5加密
                String timestamp = "key="+upload+"&sign=send&sender"+sender+"&receiver="+receiver+"&timastamp="+System.currentTimeMillis();
                timestamp = MD5Util.getMD5(timestamp);
                ndadocinfo.setTimestamp(timestamp);

                Example example = new Example(TblNdadocinfo.class);
                example.createCriteria().andEqualTo("ndadocid",ndaID);
                example.orderBy("uploadtime").desc();
                List<TblNdadocinfo> tblNdadocinfos = tblNdadocinfoMapper.selectByExample(example);
                if(tblNdadocinfos.size() > 0) {
                    ndadocinfo.setPrevid(tblNdadocinfos.get(0).getId());
                    ndadocinfo.setPrevtimestamp(tblNdadocinfos.get(0).getTimestamp());
                }
                tblNdadocinfoMapper.insert(ndadocinfo);
                // 文件上传人添加一个文件未读
                Example example2 = new Example(TblNdashare.class);
                example2.createCriteria().andEqualTo("ndaid",tblNdabasicinfo.getId());
                TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example2);
                if(tblNdabasicinfo.getInitiatorusername().equals(sender)) {
                    tblNdashare.setCreateUserUploadCount(tblNdashare.getCreateUserUploadCount()+1);
                    tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
                }else {
                    tblNdashare.setShareUserUploadCount(tblNdashare.getShareUserUploadCount()+1);
                    tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //删除中间文件
        myFileUtils.deleteFiles(orgUpFilePathList);
        myFileUtils.deleteFiles(passFilePathList);

        return json;
    }

    /**
     * 在查看活动交易页面中 上传新文件
     * 改为直接在内存中加密和上传
     * @param requestFile  文件集合
     * @return
     */
    @Log(methodFunctionDescribe="分享文件",businessType = BusinessType.CREATE)
    @PostMapping(value = "/fileUpload")
    @ResponseBody
    //@Transactional /*每个文件只要上传到服务器和对应的信息记录正确即可，不需要本次上传的所有信息正确才认为正确*/
    public JSON fileUploadInTimeLine(MultipartHttpServletRequest requestFile,HttpServletRequest request){
        JSON json = new JSONObject();
        String sender = request.getParameter("sender");
        String receiver = request.getParameter("receiver");
        String ndaID = request.getParameter("ndaID");
        //System.out.println("Sender:" + sender + "&Receiver:" + receiver + "&ndaid:" + ndaID);
        if (null == sender || sender.isEmpty()
                || null == receiver || receiver.isEmpty()
                || null == ndaID || ndaID.isEmpty()) {
            return json;
        }
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndaID);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        if (null == tblNdabasicinfo) {
            return json;
        }

        String ndaInitiatorUser = tblNdabasicinfo.getInitiatorusername();
        String pubKey4Encrypt = tblNdabasicinfo.getSenderpubkey();
        boolean senderIsInitiator = true;
        if(ndaInitiatorUser.equals(receiver)) {
            pubKey4Encrypt = tblNdabasicinfo.getReceiverpubkey();
            senderIsInitiator = false;
        }
        
        String IpAddr = IpUtil.getIpAddress(request);
        //System.out.println("pubKey for upload file: " + pubKey4Encrypt);

        Iterator<String> itr = requestFile.getFileNames();
        while (itr.hasNext()) {
            String uploadedFile = itr.next();
            MultipartFile file = requestFile.getFile(uploadedFile);
            //System.out.println(uploadedFile);
            String fullFileName = file.getOriginalFilename();
            int idxPoint = fullFileName.lastIndexOf(".");
            String fileName = fullFileName; //文件名   test.doc  test
            String fileExtension = ""; //文件名后缀  E:\test.doc  doc
            int fileLen = 0;
            if (idxPoint <= 0) {
                continue;
            } else {
                fileName = fullFileName.substring(0, idxPoint); //文件名   test.doc  test
                fileExtension = fullFileName.substring(idxPoint + 1); //文件名后缀  E:\test.doc  doc
            }
            try {
                String uploadFileHash = fileTransferService.UploadFileFromClient2Ipfs(file, pubKey4Encrypt);
                ndaShareService.ShareOneNdaDoc(ndaID, fileName, fileExtension, sender, receiver, uploadFileHash,
                        IpAddr, senderIsInitiator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /**
     * 文件预览 pdf文件
     * 从网页链接查看pdf文件时，首先响应此函数，所做工作包括：
     * 1.接收相关参数；
     * 2.读取文件hash；
     * 3.根据hash从ipfs读文件；
     * 4.对文件进行解密，生成暂存文件，删除未解密的暂存文件；
     * 5.redirect到利用pdsviewer预览文件
     * 6.预览之前触发/pdfhandler，将文件内容以数据流的形式传递到网页
     * @return
     */
    @Log(methodFunctionDescribe="文件预览",businessType = BusinessType.DETAIL)
    //@GetMapping(value = "/previewFile")
    @Deprecated
    public String previewFile(HttpServletRequest request,ModelMap map) {
        String curUsername = getUserNamefromRequest(request);
        String id = request.getParameter("id");
        Example example = new Example(TblNdadocinfo.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdadocinfo ndadocinfo = tblNdadocinfoMapper.selectOneByExample(example);
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndadocinfo.getNdadocid());
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        String hash = "";

        //使用文件上传人的密钥解密
        String privKey = tblNdabasicinfo.getSenderprivatekey();
        if(!tblNdabasicinfo.getInitiatorusername().equals(ndadocinfo.getUploadusername())) {
            privKey = tblNdabasicinfo.getReceiverprivatekey();
        }
        try {
            hash = new String(decrypt(Base64.getDecoder().decode(ndadocinfo.getDochash()), privKey));
            System.out.println("Enc hash len: " + hash.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 根据文件Hash 从IPFS 上下载文件
        long time = System.currentTimeMillis();
        StringBuffer FilePathRighter = new StringBuffer(curUsername).append(FILESEPARATOR).append(time).append(DEFAULT_FILEEXTENSION);
        String path = new StringBuffer(DEFAULT_PREFIX_PDFDOWNPATH_PASS).append(FilePathRighter).toString();
        String noPassPath = new StringBuffer(DEFAULT_PREFIX_PDFDOWNPATH_DECRPT).append(FilePathRighter).toString();
        System.out.println("Executed Here-804");
        //System.out.println(path);
        //System.out.println(noPassPath);
        /*如果对应的文件夹不存在，首先创建*/
        int i1 = path.lastIndexOf(FILESEPARATOR);
        int j1 = noPassPath.lastIndexOf(FILESEPARATOR);
        File file=new File(path.substring(0,i1));
        if(!file.exists()){
            file.mkdirs();
        }
        File file1=new File(noPassPath.substring(0,j1));
        if(!file1.exists()){
            file1.mkdirs();
        }
        System.out.println("Executed Here-818");
        File orgDownFile = new File(path);
        File decrpdFile = new File(noPassPath);
        if(!orgDownFile.exists()) {
            try {
                orgDownFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!decrpdFile.exists()) {
            try {
                decrpdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("Executed Here-836");
        boolean downSucceed = false;
        try {
            downSucceed = IPFSUtils.download(path, hash);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("下载文档出错： "+ e.getMessage());
        }
        //将下载的文件进行解密

        if (!downSucceed)
        {
            System.out.println("down failure");
            return "redirect:hitory:goback(-1)";
        } else {
            System.out.println("Executed Here-851");
        }
        try {
            decryptFile(path, noPassPath, privKey);
            // 删除下载的文件
            if(orgDownFile.exists()) {
                orgDownFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        map.put("path", ndadocinfo.getFilename()+"."+ndadocinfo.getFileextension());//"tmpfile.pdf");//
        map.put("time",time);
        map.put("fullPath",noPassPath);
        return "previewPDFJS";
        * */
        String url = new StringBuffer("/pdfhandler?filename=")
                    .append(ndadocinfo.getFilename()).append(".")
                    .append(ndadocinfo.getFileextension())
                    .append("&tid=").append(time).toString();
        String encodedUrl = url;
        try {
            encodedUrl = URLEncoder.encode(url, "utf-8");
        } catch (Exception e)
        {
            encodedUrl = url;
        }
        //TODO 按用户建暂存文件目录
        return "redirect:/pdfjs/web/viewer.html?file=" + encodedUrl;
    }

    /**
     * 文件预览 pdf文件-完全内存方式，不经过文件读写
     * 从网页链接查看pdf文件时，首先响应此函数，所做工作包括：
     * 1.接收相关参数，查询文档名称，然后跳转到文档预览页，下面工作由/pdfhandler2做；
     * 2.读取文件hash；
     * 3.根据hash从ipfs读文件；
     * 4.对文件进行解密，生成暂存文件，删除未解密的暂存文件；
     * 5.redirect到利用pdsviewer预览文件
     * 6.预览之前触发/pdfhandler，将文件内容以数据流的形式传递到网页
     * @return
     */
    @Log(methodFunctionDescribe="文件预览",businessType = BusinessType.DETAIL)
    @GetMapping(value = "/previewFile")
    public String previewFile2(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        Example example = new Example(TblNdadocinfo.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdadocinfo ndadocinfo = tblNdadocinfoMapper.selectOneByExample(example);
        String url = new StringBuffer("/pdfhandler?filename=")
                .append(ndadocinfo.getFilename()).append(".")
                .append(ndadocinfo.getFileextension())
                .append("&did=").append(id).toString();
        String encodedUrl = url;
        try {
            encodedUrl = URLEncoder.encode(url, "utf-8");
        } catch (Exception e)
        {
            encodedUrl = url;
        }
        //TODO 按用户建暂存文件目录
        return "redirect:/pdfjs/web/viewer.html?file=" + encodedUrl;
        //return "redirect:/pdfjs/web/viewer.html";
    }
    
    @Deprecated
    @Log(methodFunctionDescribe="文件预览",businessType = BusinessType.DETAIL)
    //@GetMapping(value = "/previewFile")
    public RedirectView previewFile3(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String id = request.getParameter("id");
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(8192);
        BufferedOutputStream boStream = new BufferedOutputStream(baoStream);
        RedirectView rdView = new RedirectView();
        rdView.setContextRelative(true);
        rdView.setUrl("/pdfjs/web/viewer.html");
        try {
            //ndaDocService.DownloadDocfromIpfs2Stream(id, boStream);
            //System.out.println("长度：" + baoStream.toByteArray().length);
            redirectAttributes.addFlashAttribute("docfound", "success");
            redirectAttributes.addFlashAttribute("docdata", baoStream.toByteArray());
            //return "redirect:/pdfjs/web/viewer.html";
            //boStream.close();
            //baoStream.close();
            boStream = null;
            baoStream = null;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("docfound", "fail");
            e.printStackTrace();
        } finally {
            return rdView;//"redirect:/pdfjs/web/viewer.html";
        }
        /*
        Example example = new Example(TblNdadocinfo.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdadocinfo ndadocinfo = tblNdadocinfoMapper.selectOneByExample(example);
        String url = new StringBuffer("/pdfhandler?filename=")
                .append(ndadocinfo.getFilename()).append(".")
                .append(ndadocinfo.getFileextension())
                .append("&did=").append(id).toString();
        String encodedUrl = url;
        try {
            encodedUrl = URLEncoder.encode(url, "utf-8");
        } catch (Exception e)
        {
            encodedUrl = url;
        }
        //TODO 按用户建暂存文件目录
        return "redirect:/pdfjs/web/viewer.html?file=" + encodedUrl;*/
        //return "redirect:/pdfjs/web/viewer.html";
    }

    private String getUserNamefromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String curUsername = "";
        if (null != session) {
            TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
            if (null != currentUser)
                curUsername = currentUser.getUsername();
        }
        if (null == curUsername)
            curUsername = "";
        return curUsername;
    }

    /**
     * 预览pdf文件/downlaod/decpath/username/time.pdf
     * 读文件内容到response的输出流并删除暂存文件
     * @param
     */
    //@RequestMapping(value = "/pdfhandler", method = RequestMethod.GET)
    public void pdfStreamHandler(HttpServletRequest request, HttpServletResponse response) {
        String timeStr = (String)request.getParameter("tid");
        //System.out.println((String)request.getParameter("filename"));
        String curUsername = getUserNamefromRequest(request);
        StringBuffer newFilePath = new StringBuffer(DEFAULT_PREFIX_PDFDOWNPATH_DECRPT);
        if (curUsername.isEmpty()) {
            return;
        } else {
            newFilePath.append(curUsername).append(FILESEPARATOR);
        }
        newFilePath.append(timeStr).append(DEFAULT_FILEEXTENSION);
        //System.out.println(newFilePath.toString());
        File file = new File(newFilePath.toString());//"C:/download/outPath/tmpfile.pdf");//+fileName);
        if (file.exists() && file.canRead() && file.length() > 0){
            try {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setContentLength((int) file.length());
                    response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    OutputStream outputStream = response.getOutputStream();
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while ((len = bufferedInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    // 人走带门
                    bufferedInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    file.delete();//删除了服务器上暂存的解密文件
                } catch (Exception e) {
                    System.out.println("pdf文件处理异常：" + e.getMessage());
            }
        } else{
            return;
        }
    }

    /**
     * 预览pdf文件/downlaod/decpath/username/time.pdf
     * 读文件内容到response的输出流并删除暂存文件
     * @param
     */
    @RequestMapping(value = "/pdfhandler", method = RequestMethod.GET)
    public void pdfStreamHandler2(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String docID = request.getParameter("did");
        Example example = new Example(TblNdadocinfo.class);
        example.createCriteria().andEqualTo("id", Integer.parseInt(docID));
        TblNdadocinfo ndadocinfo = tblNdadocinfoMapper.selectOneByExample(example);
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndadocinfo.getNdadocid());
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);

        //使用文件上传人的密钥解密
        String privKey = tblNdabasicinfo.getSenderprivatekey();
        if(!tblNdabasicinfo.getInitiatorusername().equals(ndadocinfo.getUploadusername())) {
            privKey = tblNdabasicinfo.getReceiverprivatekey();
        }

        // 根据文件Hash 从IPFS 上下载文件
        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Accept-Ranges", "bytes");
            //response.setHeader("Content-Length", "2097152");//2M
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("title", "aaaaaaa");
            
            fileTransferService.DownloadFileFromIpfs2Stream(ndadocinfo.getDochash(), outputStream, privKey, privKey);
        } catch (Exception e) {
            //throw new IOException("pdf文件处理异常!", e);
            //System.out.println("pdf文件处理异常：" + e.getMessage());
            /*try {
                response.sendRedirect("window.history.go(-1)");
            } catch (Exception e1) {
                e1.printStackTrace();
            }*/
        } finally {
            //outputStream.flush();
            outputStream.close();
        }
    }
    
    //@GetMapping(value="/getndaitemstatus")
    @ResponseBody
    public List<Boolean> getNdaItemStatus1(@RequestParam("ndasharedata")String ndaShareData) {
        System.out.println("Here getNdaItemStatus");
        List<Boolean> ndaItemsStatus = new ArrayList<Boolean>(1);
        System.out.println(ndaShareData);
        if(StringUtils.isNotBlank(ndaShareData)){
            JSONArray jsonArray = JSONArray.fromObject(ndaShareData);
            //批量新增文件信息
            if (jsonArray != null) {
                ndaItemsStatus = new ArrayList<Boolean>(jsonArray.size());
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String ndaid = jsonObject.getString("ndaid");
                    Example example = new Example(TblNdabasicinfo.class);
                    example.selectProperties("id", "ndaitems");
                    Example.Criteria creteria = example.createCriteria();
                    creteria.andEqualTo("id", ndaid);
                    TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
                    if (null == tblNdabasicinfo) {
                        ndaItemsStatus.add(false);
                    } else {
                        if (StringUtils.isEmpty(tblNdabasicinfo.getNdaitems()))
                            ndaItemsStatus.add(false);
                        else
                            ndaItemsStatus.add(true);
                    }
                }
            }
        }
        return ndaItemsStatus;
    }
    
    //@PostMapping(value="/getndaitemstatus")
    @ResponseBody
    public List<Boolean> getNdaItemStatus2(@RequestParam("ndaids")String[] ndaIdArray) {
        //System.out.println("Here getNdaItemStatus");
        List<Boolean> ndaItemsStatus = new ArrayList<Boolean>(1);
        if (null == ndaIdArray || ndaIdArray.length <= 0)
            return ndaItemsStatus;
        int ndaIdListCnt = ndaIdArray.length;
        ndaItemsStatus = new ArrayList<Boolean>(ndaIdListCnt);
        for (int idx = 0; idx < ndaIdListCnt; idx++) {
            Example example = new Example(TblNdabasicinfo.class);
            example.selectProperties("id", "ndaitems");
            Example.Criteria creteria = example.createCriteria();
            creteria.andEqualTo("id", ndaIdArray[idx]);
            TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
            if (null == tblNdabasicinfo) {
                ndaItemsStatus.add(false);
            } else {
                if (StringUtils.isEmpty(tblNdabasicinfo.getNdaitems()))
                    ndaItemsStatus.add(false);
                else
                    ndaItemsStatus.add(true);
            }
        }
        //System.out.println(ndaItemsStatus.size());
        return ndaItemsStatus;
    }
    
    @PostMapping(value="/getndaitemstatus")
    @ResponseBody
    public List<Boolean> getNdaItemStatus(@RequestParam("ndaids")String[] ndaIdArray) {
        //System.out.println("Here getNdaItemStatus");
        List<Boolean> ndaItemsStatus = new ArrayList<Boolean>(1);
        if (null == ndaIdArray || ndaIdArray.length <= 0)
            return ndaItemsStatus;
        int ndaIdListCnt = ndaIdArray.length;
        List<String> ndaIdList = Arrays.asList(ndaIdArray);
        ndaItemsStatus = new ArrayList<Boolean>(ndaIdListCnt);
        Example example = new Example(TblNdabasicinfo.class);
        example.selectProperties("id", "ndaitems");
        Example.Criteria creteria = example.createCriteria();
        creteria.andIn("id", ndaIdList);
        List<TblNdabasicinfo> tblNdabasicinfos = tblNdabasicinfoMapper.selectByExample(example);
        if (null == tblNdabasicinfos || tblNdabasicinfos.isEmpty())
            return ndaItemsStatus;
        for (int idx = 0; idx < ndaIdListCnt; idx++) {
            final int nidx = idx;
            if (tblNdabasicinfos.stream().filter(ndaBasic->StringUtils.isNotEmpty(ndaBasic.getNdaitems())
                                                && ndaBasic.getId().equalsIgnoreCase(ndaIdArray[nidx]))
                                         .count() > 0)
                 ndaItemsStatus.add(true);
            else
                ndaItemsStatus.add(false);
        }
        //System.out.println(ndaItemsStatus.size());
        return ndaItemsStatus;
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
        if (null == currentUser)
            return null;
        
        return getShareInfo(currentUser.getUsername(), pageSize, offset, searchShare, false);
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
        if (null == currentUser)
            return null;

        return getShareInfo(currentUser.getUsername(), pageSize, offset, searchShareTo, true);
    }

    /**Sunct, 2019.10.13
     * 获取别人分享信息,用于getShare和getShareTo调用，最后表格显示
     * @param UserName:当前用户名
     * @param pageSize
     * @param offset
     * @param searchString
     * @param share2me:用于标记是查询分享给我的(true)还是我分享的(false)
     * @return
     */
    private Map<String, Object> getShareInfo(String UserName,
                                             Integer pageSize, Integer offset, String searchString, boolean share2me) {
        Map<String, Object> map = new HashMap<String, Object>();
        Example example = new Example(TblNdashare.class);
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria searchCriteria = example.createCriteria();
        List<TblNdashare> tblNdashares = new ArrayList<>();
        //System.out.println("Executed here -904");
        if (share2me) {
            if(searchString != null && searchString.length() > 0) {
                searchCriteria.orLike("ndatitle", "%" + searchString + "%")
                              .orLike("createusername", "%" + searchString + "%");
            }
            criteria.andEqualTo("username", UserName)/*
                    .andNotEqualTo("sharestatus", NdaConst.NDA_SHARESTATUSMAP4PARTNER.get("对方中止"))
                    .andNotEqualTo("sharestatus", NdaConst.NDA_SHARESTATUSMAP4PARTNER.get("我方拒绝"))*/;
        }
        else {
            if(searchString != null && searchString.length() > 0) {
                searchCriteria.orLike("ndatitle", "%" + searchString + "%")
                              .orLike("username", "%" + searchString + "%");
            }
            criteria.andEqualTo("createusername", UserName);
        }
        example.and(searchCriteria);
        example.orderBy("createtime").desc();//Sunct,2019.10.04
        tblNdashares = tblNdashareMapper.selectByExample(example);
        List<TblNdashare> rows = new ArrayList<>();
        if (null == tblNdashares || tblNdashares.isEmpty()) {
            map.put("total", 0);
        } else {
            if (offset < tblNdashares.size()) {
                int lastIdx = Math.min(offset + pageSize, tblNdashares.size());
                List<TblNdashare> tblNdashares1 = tblNdashares.subList(offset, lastIdx);
                tblNdashares1.parallelStream().forEach(curNdaShare -> {
                    ndaShareService.SetNdaInitiatorUser(curNdaShare);
                    ndaShareService.SetNdaPartnerUser(curNdaShare);
                });
                rows.addAll(tblNdashares1);
            }
            map.put("total", tblNdashares.size());
        }
        map.put("rows", rows);
        return map;
    }

}
