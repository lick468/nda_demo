package com.nenu.service.impl;

import com.nenu.constant.FileDirConst;
import com.nenu.service.IFileIOService;
import com.nenu.utils.IPFSUtils;
import com.nenu.utils.PDFUtils;
import com.nenu.utils.myFileUtils;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;

import static com.nenu.utils.RSAUtils.*;
import static com.nenu.utils.myRSAUtils.*;

/* 文件上传下载服务的实现类
* */
@Service("filetransferservice")
public class FileIOServiceImpl implements IFileIOService {
    @Override
    public String CreateNdaItemFileandUpload2Ipfs(@NotEmpty String ndaTitle, @NotEmpty String creatorName,
                                                   @NotNull String ReceiverName,  @NotNull String ndaItems,
                                                   @NotEmpty String senderPubKey) throws IOException {
        long time = System.currentTimeMillis();
        StringBuffer pathPrefix = new StringBuffer(FileDirConst.DEFAULT_PREFIX_UPPATH_NDADOC)
                .append(creatorName).append("\\");
        pathPrefix.append(ndaTitle).append(FileDirConst.DEFAULT_SUFFIX_NDADOC_ORG)
                  .append("_").append(time).append(".pdf");
        String ndaFilename = pathPrefix.toString();
        //myFileUtils.CreateFilewithDir(ndaFilename);
        PDFUtils.createNdaItemFile(ndaFilename, ndaTitle, creatorName, ReceiverName, ndaItems);
    
        String ndaDocHash = "";
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
            return ndaDocHash;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存文档时出错!", e);
        }
    }
    
    @Override
    public String UploadFilefromServer2Ipfs(@NotEmpty String baseFilePath, @NotEmpty String upFilepath,
                                     @NotEmpty String senderPubKey) throws IOException {
        /*文件名是 "上传文件名_time.pdf"形式*/
        String passFilepath = baseFilePath;
        if (upFilepath.indexOf(":\\") >= 0) {//如果是全路径
            String passFilename = "";
            passFilename = upFilepath.substring(upFilepath.lastIndexOf("\\") + 1);
            passFilepath = passFilepath + passFilename;
        } else {
            passFilepath = passFilepath + upFilepath;
        }
    
        try {
            // 对加密后的文件进行上传
            //对上传文件返回的hash  进行加密
            String uploadFileHash = UploadFilefromServer2Ipfs(passFilepath, senderPubKey);
            return uploadFileHash;
        } catch (Exception e) {
            throw new RuntimeException("文件从暂存服务器向IPFS服务器传输时出错!", e);
        }
    }
    
    @Override
    public String UploadFilefromServer2Ipfs(@NotEmpty String filePath, @NotEmpty String senderPubKey) throws IOException {
        try {
            // 对加密后的文件进行上传
            String uploadFileHash = IPFSUtils.upload(filePath);
            myFileUtils.deleteFile(filePath);
            
            //对上传文件返回的hash  进行加密
            uploadFileHash = Base64.getEncoder().encodeToString(encrypt(uploadFileHash.getBytes(), senderPubKey));
            return uploadFileHash;
        } catch (Exception e) {
            throw new RuntimeException("文件从暂存服务器向IPFS服务器传输时出错!", e);
        }
    }
    
    @Override
    public String UploadFileFromClient2Ipfs(@NotNull MultipartFile mpFile, String pubKey) throws IOException {
        String uploadFileHash = "";
        try {
            if (null == pubKey || pubKey.isEmpty()) {
                uploadFileHash = IPFSUtils.upload(mpFile.getBytes());
            } else {
                BufferedInputStream curInputStream = new BufferedInputStream(mpFile.getInputStream()); //当前文件对应的InputStream
                ByteArrayOutputStream curOutputStream = new ByteArrayOutputStream();//用来存放加密结果的OutputStream
                //上传之前先加密
                encryptStream(curInputStream, curOutputStream, pubKey);
                //System.out.println("<<<encryped file data in /fileUpload END");
                //passDataArray = curOutputStream.toByteArray();
                //System.out.println("Uploaded file data:\n" + Convert.byteArrayToHexStr(curOutputStream.toByteArray()));
                uploadFileHash = IPFSUtils.upload(curOutputStream.toByteArray());//passDataArray);
                //System.out.println("Successive file hash: " + uploadFileHash);
                curInputStream.close();
                curOutputStream.close();
                curInputStream = null;
                curOutputStream = null;
                //System.out.println("Uploaded Hash: " + uploadFileHash);
    
                //对上传文件返回的hash  进行加密
                uploadFileHash = Base64.getEncoder().encodeToString(encrypt(uploadFileHash.getBytes(), pubKey));
            }
            return uploadFileHash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    
    }
    
    @Override
    public void DownloadFileFromIpfs2Stream(@NotEmpty String hashStr, @NotNull OutputStream oStream,
                                            String hashKeyStr, String contentKeyStr) throws IOException {
        try {
            byte[] fileData = null;
            if (null == hashKeyStr || hashKeyStr.isEmpty()) {
                fileData = IPFSUtils.download2Bytes(hashStr);
            } else {
                String hash = new String(decrypt(Base64.getDecoder().decode(hashStr), hashKeyStr));
                fileData = IPFSUtils.download2Bytes(hash);
            }
            if (null == fileData || fileData.length <= 0) {
                throw new IOException("服务器上未找到相应文件！");
            }
            if (null == contentKeyStr || contentKeyStr.isEmpty()) {
                if (!(oStream instanceof BufferedOutputStream)) {
                    BufferedOutputStream boStream = new BufferedOutputStream(oStream);
                    boStream.write(fileData);
                    boStream.close();//其中调用了flush函数
                    boStream = null;
                } else {
                    oStream.write(fileData);
                }
            } else {
                myDecryptBytes2Stream(fileData, oStream, contentKeyStr);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new IOException("下载文件出错:%n" + e.getMessage());
        }
    }
    
    @Override
    public byte[] DownloadFileFromIpfs2Bytes(@NotEmpty String hashStr, String hashKeyStr, String contentKeyStr) throws IOException {
        try {
            ByteArrayOutputStream baoStream = new ByteArrayOutputStream(8192);
            BufferedOutputStream boStream = new BufferedOutputStream(baoStream);
            DownloadFileFromIpfs2Stream(hashStr, boStream, hashKeyStr, contentKeyStr);
            byte[] bOut = baoStream.toByteArray();
            baoStream.close();
            boStream.close();
            baoStream = null;
            boStream = null;
            return bOut;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public boolean EncryptUploadedFile(@NotEmpty String uploadFilePath, String pubKeyStr) throws IOException{
        try {
            String passPath = uploadFilePath.replace("\\orgpath\\", "\\outPath\\"); // 加密后文件路径
            myFileUtils.CreateFilewithDir(passPath);
            //System.out.println("encryped file data in /share>>>");
            encryptFile(uploadFilePath, passPath, pubKeyStr);
            //System.out.println("<<<encryped file data in /share END");
            myFileUtils.deleteFile(uploadFilePath);//DeleteFilewithParentDir(uploadFilePath, false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
    @Override
    public List<String> UploadFileFromClient2Server(MultipartHttpServletRequest requestFile, String basePath) {
        long time = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        Iterator<String> itr = requestFile.getFileNames();
        StringBuffer pathBuffer;
        while (itr.hasNext()) {
            pathBuffer = new StringBuffer(basePath);
            String uploadedFile = itr.next();
            MultipartFile file = requestFile.getFile(uploadedFile);
            String filename = file.getOriginalFilename();
            int pointPos = filename.lastIndexOf(".");
            if (pointPos > 0) {/*有扩展名*/
                pathBuffer.append(filename.substring(0, pointPos))
                          .append("_").append(time).append(filename.substring(pointPos));
            } else {/*无扩展名*/
                pathBuffer.append(filename).append("_").append(time);
            }
            list.add(pathBuffer.toString());
            File localFile = new File(pathBuffer.toString());
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
    
}
