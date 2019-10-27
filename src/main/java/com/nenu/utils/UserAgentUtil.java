package com.nenu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

/*JAVA判断移动端还是PC端访问*/
/**/
public class UserAgentUtil {

    //private HttpServletRequest servletRequest;// = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    private static final String SessionName4ClientType = "clienttype";

    // \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
    // 字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    private static final String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            +"|windows (phone|ce)|blackberry"
            +"|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            +"|laystation portable)|nokia|fennec|htc[-_]"
            +"|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})"
            +"|mqqbrowser\\b";

    private static final String tabletReg = "\\b(ipad|tablet|(Nexus 7)|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    //移动设备正则匹配：手机端、平板
    private static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    private static Pattern tabletPat = Pattern.compile(tabletReg, Pattern.CASE_INSENSITIVE);

    /**
     * 检测是否是移动设备访问
     *
     * @param userAgent 浏览器标识
     * @return true:移动设备接入，false:pc端接入
     */
    public boolean isMobileClient(String userAgent){
        if(null == userAgent){
            return false;
        }
        return phonePat.matcher(userAgent).find() || tabletPat.matcher(userAgent).find();
    }


    public boolean CheckClientandSave2Session(HttpServletRequest servletRequest){
        boolean isFromMobile = false;

        HttpSession session = servletRequest.getSession();
        //检查是否已经记录访问方式（移动端或pc端）
        if (null == session.getAttribute(SessionName4ClientType)) {
            try {
                //获取ua，用来判断是否为移动端访问
                String userAgent = servletRequest.getHeader("USER-AGENT").toLowerCase();
                System.out.println("UserAgent: " + userAgent);
                /*if (null == userAgent) {
                    isFromMobile = false;
                } else {*/
                isFromMobile = isMobileClient(userAgent);
                //}
                //判断是否为移动端访问
                if (isFromMobile) {
                    //System.out.println("移动端访问");
                    session.setAttribute(SessionName4ClientType, "mobile");
                } else {
                    //System.out.println("pc端访问");
                    session.setAttribute(SessionName4ClientType, "pc");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            isFromMobile = session.getAttribute(SessionName4ClientType).equals("mobile");
        }
        return isFromMobile;
    }
}
