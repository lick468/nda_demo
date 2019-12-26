package com.nenu.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/*文件上传下载相关服务
* 客户端<->暂存服务器
* 暂存服务器<->IPFS*/
public interface IFileIOService {
    /*Hash of file uploaded to IPFS is returned*/
    String CreateNdaItemFileandUpload2Ipfs(@NotEmpty String ndaTitle, @NotEmpty String creatorName,
                                            @NotNull String ReceiverName,  @NotNull String ndaItems,
                                            @NotEmpty String senderPubKey) throws IOException;
    String UploadFilefromServer2Ipfs(@NotEmpty String baseFilePath, @NotEmpty String upFilepath,
                                     @NotEmpty String senderPubKey) throws IOException;
    String UploadFilefromServer2Ipfs(@NotEmpty String filePath, @NotEmpty String senderPubKey) throws IOException;
    String UploadFileFromClient2Ipfs(@NotNull MultipartFile mpFile, String pubKey) throws IOException;
    void DownloadFileFromIpfs2Stream(@NotEmpty String hashStr, @NotNull OutputStream oStream,
                                     String hashKeyStr, String contentKeyStr) throws IOException;
    byte[] DownloadFileFromIpfs2Bytes(@NotEmpty String hashStr, String hashKeyStr, String contentKeyStr) throws IOException;
    boolean EncryptUploadedFile(@NotEmpty String upFilePath, String pubKeyStr) throws IOException;
    List<String> UploadFileFromClient2Server(MultipartHttpServletRequest requestFile, String basePath);
}
