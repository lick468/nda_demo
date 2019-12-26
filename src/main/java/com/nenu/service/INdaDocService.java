package com.nenu.service;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.OutputStream;

public interface INdaDocService {
    void DownloadFilefromIpfs2Response(@NotBlank String docId, HttpServletResponse response) throws Exception;
    void DownloadDocfromIpfs2Stream(@NotBlank String docId, OutputStream oStream) throws Exception;
}
