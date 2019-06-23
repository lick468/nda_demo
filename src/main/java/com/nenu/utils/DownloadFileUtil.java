package com.nenu.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文件下载工具类
 */
public class DownloadFileUtil {
    /**
     * w文件下载
     * @param filePath  路径
     * @param fileName 名称
     * @param response
     * @return
     */
    public String download(String filePath, String fileName, HttpServletResponse response) {
        boolean flag = false;
        try {
            fileName = URLEncoder.encode(fileName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addHeader("Content-Disposition","attachment;filename=" + fileName);
        OutputStream outp = null;
        FileInputStream in = null;
        try{
            outp = response.getOutputStream();
            in = new FileInputStream(filePath);
            byte[] b = new byte[1024]; //缓冲区大小
            int i = 0;
            while((i = in.read(b)) > 0)  {
                outp.write(b, 0, i);
            }
            outp.flush();
            flag = true;

        } catch(Exception e){
            e.printStackTrace();
        }finally{
            if(in != null)
            {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                in = null;
            }
            if(outp != null)
            {
                try {
                    outp.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                outp = null;
            }
        }
        if(flag) {
            return "success";
        }
        return "fail";
    }

}
