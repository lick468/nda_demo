package com.nenu.service.impl;

import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessType;
import com.nenu.constant.FileDirConst;
import com.nenu.constant.NdaConst;
import com.nenu.domain.*;
import com.nenu.mapper.TblNdabasicinfoMapper;
import com.nenu.mapper.TblNdadocinfoMapper;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblNdashareMapper;
import com.nenu.service.IFileIOService;
import com.nenu.service.INdaShareService;
import com.nenu.service.IUserService;
import com.nenu.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.nenu.utils.RSAUtils.*;

/* Author: Sunct
* 处理NDA共享业务相关的Service层
* */
@Service("ndashareservice")
@Log(classFunctionDescribe = "用户交易业务_Service")
public class NdaShareServiceImpl implements INdaShareService {
    @Autowired
    private TblNdashareMapper tblNdashareMapper;
    
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;
    
    @Autowired
    private TblNdabasicinfoMapper tblNdabasicinfoMapper;
    
    @Autowired
    private TblNdadocinfoMapper tblNdadocinfoMapper;
    
    @Resource(name="filetransferservice")
    IFileIOService fileTransferService;
    
    @Resource(name="userservice")
    IUserService userService;
    
    /*现在允许NdaItems暂时为空，可以后补
    * 分享者：将NdaItem生成pdf文件，上传，然后生成sendersign，将新状态设置为1活动交易，不允许再修改协议；
      接受者：如果当前状态为交易请求：如果请求时携带文档，需要上传
      *                           (1) 无协议，将新状态设置为6无协议活动交易；
                                  (2) 有协议，将NdaItem生成pdf文件，上传，然后生成receiversign，将新状态设置为1活动交易，不允许再修改协议
             如果当前状态为对方修改，则同当前状态为交易请求，且有协议时的处理过程
*/
    @Override
    @Log(methodFunctionDescribe="同意NDA交易请求", businessType = BusinessType.UPDATE)
    @Transactional//@Transactional在接收到RuntimeException时才会回滚事务
    public boolean AgreeNda(@NotNull TblUserinfo curUser, String NdaID, String IpAddr) throws Exception{
        Example example = new Example(TblNdashare.class);
        example.createCriteria().andEqualTo("ndaid", NdaID);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
        if(null == tblNdashare) {
            return false;
        }
        boolean prevProcessSuccess = false;
        try {
            if (tblNdashare.getCreateusername().equals(curUser.getUsername())) {
                prevProcessSuccess = AcceptNdaItem(tblNdashare, true);
            } else {
                Example example1 = new Example(TblNdabasicinfo.class);
                example1.createCriteria().andEqualTo("id", NdaID);
                TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
                if (null == tblNdabasicinfo)
                    return false;
        
                //String ndaDocTitle = tblNdashare.getNdatitle();
        
                //判断发起人是否携带文件发起交易 1 是 2 不是
                String senderPubKey = tblNdabasicinfo.getSenderpubkey();
                //String receiverPrivKey = tblNdabasicinfo.getReceiverprivatekey();
                String curShareStatus = tblNdashare.getSharestatus();
                if (NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("交易请求").equals(curShareStatus)) {
                    if (NdaConst.NDA_CREATENDAWITHFILE.get("有").equals(tblNdashare.getHavefile())) {
                        prevProcessSuccess = UploadInitAdhereFileandRecord(IpAddr, tblNdashare, senderPubKey);
                        if (!prevProcessSuccess)
                            return false;
                    }
                    if (StringUtils.isBlank(tblNdabasicinfo.getNdaitems())) {
                        tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("无协议活动交易"));
                        prevProcessSuccess = true;
                    } else {
                        prevProcessSuccess = AcceptNdaItem(tblNdashare, false);
                    }
                } else if (NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("等待确认").equals(curShareStatus)) {
                    prevProcessSuccess = AcceptNdaItem(tblNdashare, false);
                }
        
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (prevProcessSuccess) {
            try {
                //tblNdashare.setSharestatus("1");
                //tblNdashare.setReceiverstatus("1");
                tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
                return true;
            } catch (Exception e) {
                //e.printStackTrace();
                //return false;
                throw new RuntimeException("更新NDA信息时出错!", e);
            }
        } else {
            return false;
        }
    }
    
    @Transactional
    public boolean UploadInitAdhereFileandRecord(String IpAddr, TblNdashare tblNdashare, String senderPubKey) throws Exception{
        String createUsername = tblNdashare.getCreateusername();
        StringBuffer passFilepath = new StringBuffer(FileDirConst.DEFAULT_PREFIX_UPPATH_PASS);
        passFilepath.append(createUsername).append("\\");
        boolean prevProcessSuccess;/*文件名是 "上传文件名_time.pdf"形式*/
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
        
        int pointPos = passFilename.lastIndexOf(".");
        if (pointPos > 0)
            fileExtension = passFilename.substring(pointPos + 1); //扩展名
        
        try {
            // 对加密后的文件进行上传
            //对上传文件返回的hash  进行加密
            String uploadFileHash = fileTransferService
                    .UploadFilefromServer2Ipfs(passFilepath.toString(), senderPubKey);
    
            TblNdadocinfo ndadocinfo = new TblNdadocinfo();
            ndadocinfo.setNdadocid(tblNdashare.getNdaid());
            ndadocinfo.setDochash(uploadFileHash);
            ndadocinfo.setFilename(orgFileName);
            ndadocinfo.setFileextension(fileExtension);
            ndadocinfo.setUploadusername(tblNdashare.getCreateusername());
            ndadocinfo.setUploadtime(new Date());
            ndadocinfo.setUploadip(IpAddr);
    
            // 构建时间戳  key=文件hash&sign=send&sender=发件人&recevier=收件人&timestamp=当地时间戳  之后MD5加密
            String timestamp = new StringBuilder("key=").append(uploadFileHash).append("&sign=send&sender")
                                                        .append(tblNdashare.getCreateusername())
                                                        .append("&receiver=").append(tblNdashare.getUsername())
                                                        .append("&timastamp=").append(System.currentTimeMillis())
                                                        .toString();
            timestamp = MD5Util.getMD5(timestamp);
            ndadocinfo.setTimestamp(timestamp);
    
            tblNdadocinfoMapper.insert(ndadocinfo);
            tblNdashare.setHavefile(NdaConst.NDA_CREATENDAWITHFILE.get("无"));
            tblNdashare.setFilepath("");
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("保存文档信息时出错!", e);
        }
    }
    
    /*
    * shareor2: true, nda share initiator; false, partner*/
    @Transactional
    public boolean AcceptNdaItem(@NotNull TblNdashare tblNdashare, boolean shareor2) throws Exception{
        Example example1 = new Example(TblNdabasicinfo.class);
        example1.createCriteria().andEqualTo("id", tblNdashare.getNdaid());
        TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectOneByExample(example1);
        if (null == tblNdabasicinfo)
            return false;
        String senderPubKey = tblNdabasicinfo.getSenderpubkey();
        String ndaItems = tblNdabasicinfo.getNdaitems();
        if (!StringUtils.isBlank(ndaItems)) {
            String ndaDocTitle = tblNdashare.getNdatitle();
            String createUsername = "";
            String receiverName = "";
            if (shareor2) {
                createUsername = tblNdashare.getCreateusername();
                receiverName = tblNdashare.getUsername();
            } else {
                createUsername = tblNdashare.getUsername();
                receiverName = tblNdashare.getCreateusername();
            }
            String ndaDocHash = "";
            try {
                ndaDocHash = fileTransferService.CreateNdaItemFileandUpload2Ipfs(ndaDocTitle, createUsername,
                        receiverName, ndaItems, senderPubKey);
                //更新tblNdabasicinfo表 添加接收人签名 NDA条款信息
                tblNdabasicinfo.setFilename(ndaDocTitle);
                tblNdabasicinfo.setFileextension("pdf");
                tblNdabasicinfo.setFilehash(ndaDocHash);
                if (shareor2) {
                    byte[] sign = sign(ndaItems.getBytes(), tblNdabasicinfo.getSenderprivatekey());
                    tblNdabasicinfo.setSendersign(new String(sign));
                } else {
                    byte[] sign = sign(ndaItems.getBytes(), tblNdabasicinfo.getReceiverprivatekey());
                    tblNdabasicinfo.setReceiversign(new String(sign));
                }
            
                tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);
                tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("活动交易"));//""7");
                return true;
            } catch (Exception e) {
                //e.printStackTrace();
                throw new RuntimeException("保存NDA Item文档时出错!", e);
            }
        } else {
            tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("无协议活动交易"));//""1");
            return true;
        }
    
    }
    
    @Override
    @Log(methodFunctionDescribe="新建NDA交易", businessType = BusinessType.CREATE)
    @Transactional
    public boolean ShareNda(HttpServletRequest request, TblUserinfo currentUser, String senderPubKey,
                            String senderPrivKey, String orgUpFilename) throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String username = currentUser.getUsername();
        String title = request.getParameter("title");
        String uploadFilePath = request.getParameter("haveFile");
        String abstractcontext = request.getParameter("abstractcontext");
        // 根据NDA模板ID获取条款
        String ndaTemplateIdStr = request.getParameter("ndaid");
        String username1 = request.getParameter("username");
    
        String memo = request.getParameter("memo");
        String IpAddr = IpUtil.getIpAddress(request);
        
        String ndaItems = "";
        try {
            int ndaTemplateId = Integer.parseInt(ndaTemplateIdStr);
            Example example = new Example(TblNdaitemtpl.class);
            example.createCriteria().andEqualTo("id", ndaTemplateId);
            TblNdaitemtpl tblNdaitemtpl = tblNdaitemtplMapper.selectOneByExample(example);
            ndaItems = tblNdaitemtpl.getNdaitem();
            if (null == ndaItems)
                ndaItems = "";
        } catch (Exception e) {
        
        }
    
        try {
            TblNdabasicinfo tblNdabasicinfo = new TblNdabasicinfo();
            tblNdabasicinfo.setId(uuid);
            tblNdabasicinfo.setAbstractcontext(abstractcontext);
            tblNdabasicinfo.setInitiatortime(new Date());
            tblNdabasicinfo.setInitiatorusername(username);
            tblNdabasicinfo.setInitiatorip(IpAddr);
            tblNdabasicinfo.setMemo(memo);
            tblNdabasicinfo.setTitle(title);
            //生成公钥私钥  私钥签名   公钥加密
            TblNdashare tblNdashare = new TblNdashare();
            Map<String, Object> receiverMap;
            receiverMap = initKey();
            tblNdabasicinfo.setSenderpubkey(senderPubKey);
            tblNdabasicinfo.setSenderprivatekey(senderPrivKey);
            String receiverPrivKey = getPrivateKeyStr(receiverMap);
            tblNdabasicinfo.setReceiverprivatekey(receiverPrivKey);
            tblNdabasicinfo.setReceiverpubkey(getPublicKeyStr(receiverMap));
    
            if (!StringUtils.isBlank(ndaItems)) {
                tblNdabasicinfo.setNdaitems(ndaItems);
                byte[] sign = sign(ndaItems.getBytes(), senderPrivKey);
                tblNdabasicinfo.setSendersign(new String(sign));
            }
        
            tblNdashare.setCreateusername(username);
            //tblNdashare.setOrdernumber(1);
            tblNdashare.setOrgname(currentUser.getOrgname());
            tblNdashare.setCreatetime(new Date());
            tblNdashare.setOperateip(IpAddr);
            tblNdashare.setNdaid(uuid);
            tblNdashare.setNdatitle(title);
            // 发送方  0 等待确认    接收人  0 交易请求
            tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("交易请求"));//"0");
            //tblNdashare.setReceiverstatus("0");
        
            tblNdashare.setShareUserUploadCount(0);//初始化未读数量
            tblNdashare.setCreateUserUploadCount(0);//初始化未读数量
        
            if(orgUpFilename != null && orgUpFilename.length() > 0) {
                tblNdashare.setHavefile("1");
                tblNdashare.setFilepath(orgUpFilename);
            }else {
                tblNdashare.setHavefile("2");
            }
            tblNdashare.setUsername(username1);
            //插入一条分享记录
            tblNdashareMapper.insert(tblNdashare);
    
            //插入一条NDA基本信息
            tblNdabasicinfoMapper.insert(tblNdabasicinfo);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("发起NDA共享时出错!", e);
        }
    }
    
    @Override
    @Transactional
    public boolean ShareOneNdaDoc(String ndaID, String fileName, String fileExt, String sender, String receiver,
                                  String fileHash, String IpAddr, boolean senderIsInitiator) throws Exception {
        try {
            TblNdadocinfo ndadocinfo = new TblNdadocinfo();
            ndadocinfo.setNdadocid(ndaID);
            ndadocinfo.setDochash(fileHash);
            ndadocinfo.setFilename(fileName);
            ndadocinfo.setFileextension(fileExt);
            ndadocinfo.setUploadusername(sender);
            ndadocinfo.setUploadtime(new Date());
            ndadocinfo.setUploadip(IpAddr);
            // 构建时间戳  key=文件hash&sign=send&sender=发件人&receiver=收件人&timestamp=当地时间戳  之后MD5加密
            String timestamp = "key="+ fileHash + "&sign=send&sender" + sender
                    +"&receiver=" + receiver + "&timastamp=" + System.currentTimeMillis();
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
            //System.out.println("inserted tblndadocinfo");
            // 文件上传人添加一个文件未读
            Example example2 = new Example(TblNdashare.class);
            example2.createCriteria().andEqualTo("ndaid", ndaID);
            TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example2);
            if(senderIsInitiator) {
                tblNdashare.setCreateUserUploadCount(tblNdashare.getCreateUserUploadCount()+1);
                tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            }else {
                tblNdashare.setShareUserUploadCount(tblNdashare.getShareUserUploadCount()+1);
                tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            }
            //System.out.println("inserted tblndashare");
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("添加共享文档时出错!%n" + e.getMessage());
        }
    }
    
    @Override
    @Log(methodFunctionDescribe="更新Nda条款业务", businessType = BusinessType.UPDATE)
    @Transactional
    public boolean UpdateNdaItems(@NotEmpty String ndaId, @NotEmpty String UserName, String ndaItems) throws Exception {
        Example exampleShare  = new Example(TblNdashare.class);
        exampleShare.createCriteria().andEqualTo("ndaid", ndaId);
        TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(exampleShare);
        if(tblNdashare != null){
            try {
                TblNdabasicinfo tblNdabasicinfo = tblNdabasicinfoMapper.selectByPrimaryKey(ndaId);
                tblNdabasicinfo.setNdaitems(ndaItems);
                tblNdabasicinfo.setUpdatetime(new Date());
                tblNdabasicinfo.setUpdateusername(UserName);
                /*
                * 分享者
                *    0 交易请求 1 活动交易 2 我方中止 3 对方拒绝 4 等待确认 5 对方修改 6无协议活动交易 7我方撤回
                * 接受者
                *    0 交易请求 1 活动交易 2 对方中止 3 我方拒绝 4 对方修改 5等待确认 6无协议活动交易  7对方撤回
                * */
                if(UserName.equals(tblNdashare.getCreateusername())) {
                    if (!NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("交易请求").equals(tblNdashare.getSharestatus()))
                        tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("等待确认"));//4
                    //tblNdashare.setReceiverstatus("4");
                    if (StringUtils.isBlank(ndaItems)) {
                        tblNdabasicinfo.setSendersign("");
                    } else {
                        byte[] sign = sign(ndaItems.getBytes(), tblNdabasicinfo.getSenderprivatekey());
                        tblNdabasicinfo.setSendersign(new String(sign));
                    }
                    // 接收人修改 4 对方修改 5等待确认
                }else if(UserName.equals(tblNdashare.getUsername())) {
                    tblNdashare.setSharestatus(NdaConst.NDA_SHARESTATUSMAP4INITIATOR.get("对方修改"));//5
                    //tblNdashare.setReceiverstatus("5");
                    if (StringUtils.isBlank(ndaItems)) {
                        tblNdabasicinfo.setReceiversign("");
                    } else {
                        byte[] sign = sign(ndaItems.getBytes(), tblNdabasicinfo.getReceiverprivatekey());
                        tblNdabasicinfo.setReceiversign(new String(sign));
                    }
                }
                tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
                tblNdabasicinfoMapper.updateByPrimaryKeySelective(tblNdabasicinfo);
                return true;
            } catch (Exception e) {
                //e.printStackTrace();
                throw new RuntimeException("修改NDA协议条款时出错！\n" + e.getMessage());
            }
        } else {
            return false;
        }
    }
    
    @Override
    public boolean UpdateNdaStatus(@NotBlank String ndaId, @NotBlank String newStatus) {
        try {
            Example example = new Example(TblNdashare.class);
            example.createCriteria().andEqualTo("ndaid", ndaId);
            TblNdashare tblNdashare = tblNdashareMapper.selectOneByExample(example);
            tblNdashare.setSharestatus(newStatus);
            tblNdashareMapper.updateByPrimaryKeySelective(tblNdashare);
            return true;
        } catch (Exception e) {
            return false;
        }
    
    }
    
    @Override
    public void SetNdaPartnerUser(TblNdashare curNdaShare) {
        if (null == curNdaShare)
            return;
        try {
            if (null == curNdaShare.getPartnerUser())
                curNdaShare.setPartnerUser(userService.getUser(curNdaShare.getUsername()));
        } catch (Exception e) {
        
        }
        
    }
    
    @Override
    public void SetNdaInitiatorUser(TblNdashare curNdaShare) {
        if (null == curNdaShare)
            return;
        try {
            if (null == curNdaShare.getInitiatorUser())
                curNdaShare.setInitiatorUser(userService.getUser(curNdaShare.getCreateusername()));
        } catch (Exception e) {
        
        }
        
    }
    
}
