package com.nenu.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.domain.*;
import com.nenu.mapper.TblNdabasicinfoMapper;
import com.nenu.mapper.TblNdadocinfoMapper;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.utils.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import static com.nenu.utils.RSAUtils.*;

/**
 * NDA共享业务层
 */
@Controller
@Log(classFunctionDescribe = "用户--交易业务")
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
    private static final String DEFAULT_TMPPDFPATH_PASS = "C:\\ndadata\\download\\outpath\\";
    private static final String DEFAULT_TMPPDFPATH_DECRPT = "C:\\ndadata\\download\\decpath\\";
    private static final String DEFAULT_TMPPDFFILENAME = "tmpfile.pdf";
    private static final String DEFAULT_FILEEXTENSION = ".pdf";


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
    public String showNDAForLookOrBack(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        return "showNDAForLookOrBack";
    }
    /**
     * 删除NDA交易请求
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe="删除NDA交易请求",businessType = BusinessType.DELETE)
    @GetMapping(value = "/deleteNDA")
    @ResponseBody
    public String deleteNDA(HttpServletRequest request) {
        String ndaid = request.getParameter("ndaid");
        Example example =  new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid",ndaid);
        tblNdashareMapper.deleteByExample(example);
        tblNdabasicinfoMapper.deleteByPrimaryKey(ndaid);
        return "success";
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
            // 发起人拒绝 2 我方中止 2 对方中止
            if(currentUser.getUsername().equals(tblNdashare.getCreateusername())) {
                tblNdashare.setSharestatus("2");
                tblNdashare.setReceiverstatus("2");
                // 接收人拒绝 3 我方拒绝 3 对方拒绝
            }else  if(currentUser.getUsername().equals(tblNdashare.getUsername())){
                tblNdashare.setSharestatus("3");
                tblNdashare.setReceiverstatus("3");
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
            Example example1 = new Example(TblNdabasicinfo.class);
            example1.createCriteria().andEqualTo("id",ndaid);
            TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
            //判断发起人是否携带文件发起交易 1 是 2 不是
            if(tblNdashare.getHavefile().equals("1")) {
                String path = tblNdashare.getFilepath();  // 加密前文件路径
                String passPath = path.replace("upload","outPath"); // 加密后文件路径
                int i = path.lastIndexOf("\\");
                int j = passPath.lastIndexOf("\\");
                File file3=new File(path.substring(0,i));
                File file4=new File(passPath.substring(0,j));
                if(!file3.exists()){
                    file3.mkdirs();
                }
                if(!file4.exists()){
                   file4.mkdirs();
                }
                File file5 = new File(path);
                File file6 = new File(passPath);
                if(!file5.exists()) {
                    try {
                        file5.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(!file6.exists()) {
                    try {
                        file6.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String upload = "";
                String filename = tblNdashare.getFilepath();
                String fileName = filename.substring(filename.lastIndexOf("\\")+1).split("\\.")[0]; //文件名
                String fileExtension = filename.split("\\.")[1]; //文件名后缀

                try {
                    // 文件加密
                    encryptFile(path ,passPath,tblNdabasicinfo.getSenderpubkey());
                    // 对加密后的进行进行上传
                    upload = IPFSUtils.upload(passPath );
                    // 上传成功后删除本地文件
                    if(file3.exists()){
                        file3.delete();
                    }
                    if(file4.exists()){
                        file4.delete();
                    }

                    //对上传文件返回的hash  进行加密
                    upload = Base64.getEncoder().encodeToString(encrypt(upload.getBytes(),tblNdabasicinfo.getSenderpubkey()));

                    TblNdadocinfo ndadocinfo = new TblNdadocinfo();
                    ndadocinfo.setNdadocid(tblNdashare.getNdaid());
                    ndadocinfo.setDochash(upload);
                    ndadocinfo.setFilename(fileName);
                    ndadocinfo.setFileextension(fileExtension);
                    ndadocinfo.setUploadusername(tblNdashare.getCreateusername());
                    ndadocinfo.setUploadtime(new Date());
                    ndadocinfo.setUploadip(IpUtil.getIpAddress(request));

                    // 构建时间戳  key=文件hash&sign=send&sender=发件人&recevier=收件人&timestamp=当地时间戳  之后MD5加密
                    String timestamp = "key="+upload+"&sign=send&sender"+tblNdashare.getCreateusername()+"&receiver="+tblNdashare.getUsername()+"&timastamp="+System.currentTimeMillis();
                    timestamp = MD5Util.getMD5(timestamp);
                    ndadocinfo.setTimestamp(timestamp);

                    //生成NDA条款.pdf文件  上传到IPFS
                    //页面大小
                    Rectangle rect = new Rectangle(PageSize.A4.rotate());
                    //页面背景色
                    rect.setBackgroundColor(BaseColor.WHITE);
                    Document doc = new Document(rect);
                    PdfWriter writer = null;
                    long time = System.currentTimeMillis();
                    String pathNDA = "C:\\"+time+"\\"+tblNdashare.getNdatitle() +".pdf";
                    String passPathNDA = "C:\\"+time+"\\outPath\\"+tblNdashare.getNdatitle() +".pdf";
                    int i1 = pathNDA.lastIndexOf("\\");
                    int j1 = passPathNDA.lastIndexOf("\\");
                    File file=new File(pathNDA.substring(0,i1));
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    File file1=new File(passPathNDA.substring(0,j1));
                    if(!file1.exists()){
                        file1.mkdirs();
                    }
                    File file7 = new File(pathNDA);
                    File file8 = new File(passPathNDA);
                    if(!file7.exists()) {
                        file7.createNewFile();
                    }
                    if(!file8.exists()) {
                        file8.createNewFile();
                    }
                    try {
                        try {
                            writer = PdfWriter.getInstance(doc, new FileOutputStream(pathNDA));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    //页边空白
                    doc.setMargins(10, 20, 30, 40);
                    doc.open();
                    //Step 4—Add content.

                    String title = "<h1  style=\"text-align: center\">"+ tblNdashare.getNdatitle() +"</h1>";
                    String sender = "<h4  style=\"text-align: left\"> 发起人："+ tblNdashare.getCreateusername()+"</h4>";
                    String receiver = "<h4  style=\"text-align: left\"> 接收人："+ tblNdashare.getUsername()+"</h4>";
                    String NDAItem = tblNdabasicinfo.getNdaitems();
                    //noinspection deprecation
                    HTMLWorker htmlWorker = new HTMLWorker(doc);
                    try {
                        htmlWorker.parse(new StringReader(title));
                        htmlWorker.parse(new StringReader(sender));
                        htmlWorker.parse(new StringReader(receiver));
                        htmlWorker.parse(new StringReader(NDAItem));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    doc.close();
                    //对文件使用发送人公钥加密
                    encryptFile(pathNDA,passPathNDA,tblNdabasicinfo.getSenderpubkey());
                    //将加密后的文件上传到IPFS上
                    String upload1 = IPFSUtils.upload(passPathNDA);
                    //删除文件
                    if(file.exists()) {
                        file.delete();
                    }
                    if(file1.exists()) {
                        file1.delete();
                    }
                    ndadocinfo.setNdahash(upload1);

                    //更新tblNdabasicinfo表 添加接收人签名 NDA条款信息
                    tblNdabasicinfo.setFilename(tblNdashare.getNdatitle());
                    tblNdabasicinfo.setFileextension("pdf");
                    tblNdabasicinfo.setFilehash(upload1);
                    byte[] sign = sign(tblNdabasicinfo.getNdaitems().getBytes(), tblNdabasicinfo.getReceiverprivatekey());
                    tblNdabasicinfo.setReceiversign(new String(sign));

                    tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);

                    tblNdadocinfoMapper.insert(ndadocinfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //新建交易时没有携带文件
            }else if(tblNdashare.getHavefile().equals("2")) {
                //生成NDA条款.pdf文件  上传到IPFS
                //页面大小
                Rectangle rect = new Rectangle(PageSize.A4.rotate());
                //页面背景色
                rect.setBackgroundColor(BaseColor.WHITE);
                Document doc = new Document(rect);
                PdfWriter writer = null;
                long time = System.currentTimeMillis();
                String pathNDA = "C:\\"+time+"\\"+tblNdashare.getNdatitle() +".pdf";
                String passPathNDA = "C:\\"+time+"\\outPath\\"+tblNdashare.getNdatitle() +".pdf";
                int i1 = pathNDA.lastIndexOf("\\");
                int j1 = passPathNDA.lastIndexOf("\\");
                File file=new File(pathNDA.substring(0,i1));
                if(!file.exists()){
                    file.mkdirs();
                }
                File file1=new File(passPathNDA.substring(0,j1));
                if(!file1.exists()){
                    file1.mkdirs();
                }
                File file7 = new File(pathNDA);
                File file8 = new File(passPathNDA);
                if(!file7.exists()) {
                    try {
                        file7.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(!file8.exists()) {
                    try {
                        file8.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    try {
                        writer = PdfWriter.getInstance(doc, new FileOutputStream(pathNDA));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                //页边空白
                doc.setMargins(10, 20, 30, 40);
                doc.open();
                //Step 4—Add content.

                String title = "<h1  style=\"text-align: center\">"+ tblNdashare.getNdatitle() +"</h1>";
                String sender = "<h4  style=\"text-align: left\"> 发起人："+ tblNdashare.getCreateusername()+"</h4>";
                String receiver = "<h4  style=\"text-align: left\"> 接收人："+ tblNdashare.getUsername()+"</h4>";
                String NDAItem = tblNdabasicinfo.getNdaitems();
                HTMLWorker htmlWorker = new HTMLWorker(doc);
                try {
                    htmlWorker.parse(new StringReader(title));
                    htmlWorker.parse(new StringReader(sender));
                    htmlWorker.parse(new StringReader(receiver));
                    htmlWorker.parse(new StringReader(NDAItem));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doc.close();

                String upload1 = null;
                try {
                    //对文件使用发送人公钥加密
                    //上传到IPFS上
                    encryptFile(pathNDA,passPathNDA,tblNdabasicinfo.getSenderpubkey());
                    upload1 = IPFSUtils.upload(passPathNDA);
                    //删除文件
                    if(file.exists()) {
                        file.delete();
                    }
                    if(file1.exists()) {
                        file1.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //更新tblNdabasicinfo表 添加接收人签名 NDA条款信息
                tblNdabasicinfo.setFilename(tblNdashare.getNdatitle());
                tblNdabasicinfo.setFileextension("pdf");
                tblNdabasicinfo.setFilehash(upload1);
                byte[] sign = new byte[0];
                try {
                    sign = sign(tblNdabasicinfo.getNdaitems().getBytes(), tblNdabasicinfo.getReceiverprivatekey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tblNdabasicinfo.setReceiversign(new String(sign));

                tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);
            }
        }
        return "success";
    }

    /**
     * 显示修改NDA条款弹窗
     * @param request
     * @param map
     * @return
     */
    @GetMapping(value = "/showUpdateNDA")
    public String showUpdateNDA(HttpServletRequest request, ModelMap map) {
        String ndaid = request.getParameter("ndaid");
        Example example = new Example(TblNdabasicinfo.class);
        example.createCriteria().andEqualTo("id",ndaid);
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example);
        map.put("tblNdabasicinfo",tblNdabasicinfo);
        return "editorNDA";
    }

    /**
     * 修改NDA条款
     * @param session
     * @param request
     * @return
     */
    @Log(methodFunctionDescribe="修改NDA条款",businessType = BusinessType.UPDATE)
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

        Example exampleShare  = new Example(TblNdashare.class);
        exampleShare.createCriteria().andEqualTo("ndaid",id);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(exampleShare);
        if(tblNdashare!= null){
            // 发起人修改 0 等待确认 4 对方修改
            if(currentUser.getUsername().equals(tblNdashare.getCreateusername())) {
                tblNdashare.setSharestatus("0");
                tblNdashare.setReceiverstatus("4");
                try {
                    byte[] sign = sign(tblNdabasicinfo.getNdaitems().getBytes(), tblNdabasicinfo.getSenderprivatekey());
                    tblNdabasicinfo.setSendersign(new String(sign));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 接收人修改 4 对方修改 5等待确认
            }else if(currentUser.getUsername().equals(tblNdashare.getUsername())) {
                tblNdashare.setSharestatus("4");
                tblNdashare.setReceiverstatus("5");
                try {
                    byte[] sign = sign(tblNdabasicinfo.getNdaitems().getBytes(), tblNdabasicinfo.getReceiverprivatekey());
                    tblNdabasicinfo.setReceiversign(new String(sign));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
        }
        tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);
        return "share";
    }

    /**
     * 发起NDA分享
     * @param request
     * @param session
     * @return
     */
    @Log(methodFunctionDescribe="发起NDA分享",businessType = BusinessType.CREATE)
    @PostMapping(value = "/share")
    public String share(HttpServletRequest request, HttpSession session) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String username = currentUser.getUsername();
        String title = request.getParameter("title");
        String haveFile = request.getParameter("haveFile");

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
        try {
            senderMap = initKey();
            receiverMap = initKey();
            tblNdabasicinfo.setSenderpubkey(getPublicKeyStr(senderMap));
            tblNdabasicinfo.setSenderprivatekey(getPrivateKeyStr(senderMap));
            tblNdabasicinfo.setReceiverprivatekey(getPrivateKeyStr(receiverMap));
            tblNdabasicinfo.setReceiverpubkey(getPublicKeyStr(receiverMap));
            byte[] sign = sign(tblNdaitemtpl.getNdaitem().getBytes(), getPrivateKeyStr(senderMap));
            tblNdabasicinfo.setSendersign(new String(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //插入一条NDA基本信息
        tblNdabasicinfoMapper.insert(tblNdabasicinfo);


        TblNdashare tblNdashare = new TblNdashare();
        tblNdashare.setCreateusername(username);
        tblNdashare.setOrgname(currentUser.getOrgname());
        tblNdashare.setCreatetime(new Date());
        tblNdashare.setOperateip(IpUtil.getIpAddress(request));
        tblNdashare.setNdaid(uuid);
        tblNdashare.setNdatitle(title);
        // 发送方  0 等待确认    接收人  0 交易请求
        tblNdashare.setSharestatus("0");
        tblNdashare.setReceiverstatus("0");

        tblNdashare.setShareuseruploadcount(0);//初始化未读数量
        tblNdashare.setCreateuseruploadcount(0);//初始化未读数量

        if(haveFile != null && haveFile.length() > 0) {
            tblNdashare.setHavefile("1");
            tblNdashare.setFilepath(haveFile);
        }else {
            tblNdashare.setHavefile("2");
        }
        tblNdashare.setUsername(username1);
        //插入一条分享记录
        tblNdashareMapper.insert(tblNdashare);
        // TODO 考虑上传到IPFS后删除暂存文件，2.localtime使用
        return "redirect:/share";
    }

    /** 用户通过拖曳或上传方式上传文件时，会触发此事件，
     * 将上传的文件暂存于服务器对应目录
     */
    @PostMapping(value = "/upload")
    @ResponseBody
    public List<String> upload(MultipartHttpServletRequest requestFile,HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        long time = System.currentTimeMillis();
        String path = "C:\\upload\\"+currentUser.getUsername()+"\\"+time+"\\";
        List<String> list = new ArrayList<>();
        String upload = "";
        Iterator<String> itr = requestFile.getFileNames();
        while (itr.hasNext()) {
            String uploadedFile = itr.next();
            MultipartFile file = requestFile.getFile(uploadedFile);
            String filename = file.getOriginalFilename();
            list.add(path+filename);
            File localFile = new File(path, filename);
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
            try {
                file.transferTo(localFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }



    /**
     * 在操作一栏“同意”对应的动作
     * 跳转到分享页面
     * @param
     * @return
     */
    @GetMapping(value = "/fileTranslation")
    public String shareFile(HttpServletRequest request,HttpSession session,ModelMap map) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        String id = request.getParameter("id");
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("id",Integer.parseInt(id));
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        // 该用户是交易接收人
        if(tblNdashare.getUsername().equals(currentUser.getUsername())) {
            map.put("recevier",tblNdashare.getCreateusername());
            map.put("sender",tblNdashare.getUsername());
            // 更改未读信息数量
            tblNdashare.setCreateuseruploadcount(0);
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            // 该用户是交易发起人
        }else {
            map.put("recevier",tblNdashare.getUsername());
            map.put("sender",tblNdashare.getCreateusername());
            // 更改未读信息数量
            tblNdashare.setShareuseruploadcount(0);
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
    @PostMapping(value = "/fileUpload")
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
                    tblNdashare.setCreateuseruploadcount(tblNdashare.getCreateuseruploadcount()+1);
                    tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
                }else {
                    tblNdashare.setShareuseruploadcount(tblNdashare.getShareuseruploadcount()+1);
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
        String path = new StringBuffer(DEFAULT_TMPPDFPATH_PASS).append(FilePathRighter).toString(); //DEFAULT_TMPPDFFILENAME;// +ndadocinfo.getFilename()+"."+ndadocinfo.getFileextension();
        String noPassPath = new StringBuffer(DEFAULT_TMPPDFPATH_DECRPT).append(FilePathRighter).toString();//"C:\\download\\outPath\\tmpfile.pdf";//+ndadocinfo.getFilename()+"."+ndadocinfo.getFileextension();
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

        System.out.println("Executed Here-836");
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
        StringBuffer newFilePath = new StringBuffer(DEFAULT_TMPPDFPATH_DECRPT);
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
    public void pdfStreamHandler2(HttpServletRequest request, HttpServletResponse response) {
        String docID = request.getParameter("did");
        Example example = new Example(TblNdadocinfo.class);
        example.createCriteria().andEqualTo("id", Integer.parseInt(docID));
        TblNdadocinfo ndadocinfo = tblNdadocinfoMapper.selectOneByExample(example);
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id",ndadocinfo.getNdadocid());
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        String hash = "";
        byte[] fileData = new byte[1];
        //String fileData = "";
        byte[] decData = new byte[1];

        //使用文件上传人的密钥解密
        String privKey = tblNdabasicinfo.getSenderprivatekey();
        if(!tblNdabasicinfo.getInitiatorusername().equals(ndadocinfo.getUploadusername())) {
            privKey = tblNdabasicinfo.getReceiverprivatekey();
        }
        try {
            hash = new String(decrypt(Base64.getDecoder().decode(ndadocinfo.getDochash()), privKey));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendRedirect("window.history.go(-1)");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        // 根据文件Hash 从IPFS 上下载文件
        /*如果对应的文件夹不存在，首先创建*/

        System.out.println("Executed Here-1013");
        try {
            fileData = IPFSUtils.download2Bytes(hash);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("下载文档出错： "+ e.getMessage());
        }
        //将下载的文件进行解密
        /*try {
            decData = decrypt(Base64.getDecoder().decode(fileData), privKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileData = null;
        int dataLen = decData.length;
        */
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            OutputStream outputStream = response.getOutputStream();
            //outputStream.write(decData, 0, dataLen);
            decryptFile1(fileData, outputStream, privKey);
            //response.setContentLength((int) outputStream.);
            // 人走带门
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("pdf文件处理异常：" + e.getMessage());
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

    /**Sunct, 2010.10.13
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
                searchCriteria.orLike("ndatitle", "%" + searchString + "%").orLike("nreateusername", "%" + searchString + "%");
            }
            criteria.andEqualTo("username", UserName);
        }
        else {
            if(searchString != null && searchString.length() > 0) {
                searchCriteria.orLike("ndatitle", "%" + searchString + "%").orLike("username", "%" + searchString + "%");
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
                rows.addAll(tblNdashares.subList(offset, lastIdx));
            }
            map.put("total", tblNdashares.size());
        }
        map.put("rows", rows);
        return map;
    }

}
