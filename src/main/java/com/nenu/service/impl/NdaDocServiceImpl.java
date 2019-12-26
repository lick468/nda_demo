package com.nenu.service.impl;

import com.nenu.domain.TblNdabasicinfo;
import com.nenu.domain.TblNdadocinfo;
import com.nenu.mapper.TblNdabasicinfoMapper;
import com.nenu.mapper.TblNdadocinfoMapper;
import com.nenu.service.IFileIOService;
import com.nenu.service.INdaDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

@Service("ndadocservice")
public class NdaDocServiceImpl implements INdaDocService {
    @Autowired
    TblNdadocinfoMapper ndadocinfoMapper;
    @Autowired
    TblNdabasicinfoMapper ndabasicinfoMapper;
    @Resource(name="filetransferservice")
    IFileIOService fileTransferService;
    
    @Override
    public void DownloadFilefromIpfs2Response(@NotBlank String docId, HttpServletResponse response) throws Exception {
        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        try {
            Example example = new Example(TblNdadocinfo.class);
            example.createCriteria().andEqualTo("id", Integer.parseInt(docId));
            TblNdadocinfo ndadocinfo = ndadocinfoMapper.selectOneByExample(example);
            Example example1 = new Example(TblNdabasicinfo.class);
            example1.createCriteria().andEqualTo("id",ndadocinfo.getNdadocid());
            TblNdabasicinfo tblNdabasicinfo = ndabasicinfoMapper.selectOneByExample(example1);
        
            //使用文件上传人的密钥解密
            String privKey = tblNdabasicinfo.getSenderprivatekey();
            if(!tblNdabasicinfo.getInitiatorusername().equals(ndadocinfo.getUploadusername())) {
                privKey = tblNdabasicinfo.getReceiverprivatekey();
            }
        
            // 根据文件Hash 从IPFS 上下载文件
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Accept-Ranges", "bytes");
            //response.setHeader("Content-Length", "2097152");//2M
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            fileTransferService.DownloadFileFromIpfs2Stream(ndadocinfo.getDochash(), outputStream, privKey, privKey);
        } catch (Exception e) {
            //System.out.println("pdf文件处理异常：" + e.getMessage());
            throw new RuntimeException("pdf文件处理异常", e);
        } finally {
            //outputStream.flush();
            outputStream.close();
        }
    
    }
    
    @Override
    public void DownloadDocfromIpfs2Stream(@NotBlank String docId, OutputStream oStream) throws Exception {
        try {
            Example example = new Example(TblNdadocinfo.class);
            example.createCriteria().andEqualTo("id", Integer.parseInt(docId));
            TblNdadocinfo ndadocinfo = ndadocinfoMapper.selectOneByExample(example);
            Example example1 = new Example(TblNdabasicinfo.class);
            example1.createCriteria().andEqualTo("id", ndadocinfo.getNdadocid());
            TblNdabasicinfo tblNdabasicinfo = ndabasicinfoMapper.selectOneByExample(example1);
        
            //使用文件上传人的密钥解密
            String privKey = tblNdabasicinfo.getSenderprivatekey();
            if(!tblNdabasicinfo.getInitiatorusername().equals(ndadocinfo.getUploadusername())) {
                privKey = tblNdabasicinfo.getReceiverprivatekey();
            }
            fileTransferService.DownloadFileFromIpfs2Stream(ndadocinfo.getDochash(), oStream, privKey, privKey);
        } catch (Exception e) {
            //System.out.println("pdf文件处理异常：" + e.getMessage());
            throw new RuntimeException("pdf文件下载异常", e);
        }
    }
}
