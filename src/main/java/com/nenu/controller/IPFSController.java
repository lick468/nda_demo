package com.nenu.controller;

import com.nenu.utils.IPFSUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * ipfs星际文件管理层
 */
@RestController
public class IPFSController {

    @GetMapping(value = "/ipfs")
    public String ipfs() {
        try {
            String upload = IPFSUtils.upload();
            return upload;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }
    @GetMapping(value = "/download")
    public String download(HttpServletResponse response) {
        /**
         * https://ipfs.io/ipfs/QmaKRRXQgmeTrGENQZaHFZepyPbsHmf72DMujz2eZ5ZeZC
         */
        try {
           IPFSUtils.download("QmQybokRwe57aEtM5JiKCEg7PHvad6AGQG81MQWPNz534J",response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}


