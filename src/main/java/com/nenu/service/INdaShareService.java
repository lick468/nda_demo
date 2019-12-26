package com.nenu.service;

import com.nenu.domain.TblNdashare;
import com.nenu.domain.TblUserinfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

public interface INdaShareService {
    boolean AgreeNda(@NotNull TblUserinfo curUser, String NdaID, String IpAddr) throws Exception;
    boolean ShareNda(HttpServletRequest request, TblUserinfo currentUser, String senderPubKey,
                     String senderPrivKey, String orgUpFilename) throws Exception;
    boolean ShareOneNdaDoc(String ndaid, String filename, String fileExt, String sender, String receiver,
                           String fileHash, String IpAddr, boolean senderIsInitiator) throws Exception;
    boolean UpdateNdaItems(@NotEmpty String ndaId, @NotEmpty String UserName, String ndaItems) throws Exception;
    boolean UpdateNdaStatus(@NotBlank String ndaId, @NotBlank String newStatus);
    void SetNdaInitiatorUser(TblNdashare ndashare);
    void SetNdaPartnerUser(TblNdashare ndashare);
}
