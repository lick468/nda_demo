package com.nenu.controller;

import com.nenu.utils.IPFSUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * ipfs星际文件管理层
 */
@Controller
public class IPFSController {

    @GetMapping(value = "/ipfs")
    public String ipfs() {
        return "ipfs";
    }
    @PostMapping(value = "/upload")
    @ResponseBody
    public String upload(MultipartHttpServletRequest requestFile) {
        String path = "D:\\upload\\";
        String url = "https://ipfs.io/ipfs/";
        String upload = "";
        Iterator<String> itr = requestFile.getFileNames();
        while (itr.hasNext()) {
            String uploadedFile = itr.next();
            MultipartFile file = requestFile.getFile(uploadedFile);
            String filename = file.getOriginalFilename();
            File localFile = new File(path, filename);
            if(!localFile.exists()){
                localFile.mkdirs();
            }
            try {
                file.transferTo(localFile);
                 upload = IPFSUtils.upload(path + filename);
                System.out.println(url + upload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "success";
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


