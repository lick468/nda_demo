package com.nenu.interceptor;

import com.nenu.domain.TblAdmin;
import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        String cookie = request.getHeader("Cookie");
//        HttpSession session = request.getSession(false);
//        String requestUrl = request.getRequestURI();
//        if(requestUrl.length() == 1) {
//            response.sendRedirect("/main");
//            return true;
//        }
//        // 静态资源放行
//        if(requestUrl.contains(".js") || requestUrl.contains(".css") || requestUrl.contains(".jpg")|| requestUrl.contains(".svg")|| requestUrl.contains(".png") || requestUrl.contains(".ico")) {
//            return true;
//        }
//        //登录和修改密码放行
//        if(requestUrl.contains("/login") || requestUrl.contains("/getCode")|| requestUrl.contains("/register")|| requestUrl.contains("/findPsd") ||  requestUrl.contains("/updatePsd") ||  requestUrl.contains("/main") ||  requestUrl.contains("/upload") ||  requestUrl.contains("/preview")||  requestUrl.contains("/previewAction")  ) {
//            return true;
//        }
//
//        //cookie 判断
//        if(cookie != null && cookie.contains("JSESSIONID")) {
//            if(session == null) {
//                response.sendRedirect("/login");
//                return false;
//            }
//            return true;
//        }else {
//            response.sendRedirect("/login");
//            return false;
//        }

        String url = request.getRequestURI();
        HttpSession session = request.getSession();
        TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        TblAdmin currentAdmin = (TblAdmin) session.getAttribute("currentAdmin");
        //System.out.println(url);
        //System.out.println("缓存user_name是否是值:" + name);
        // 静态资源放行
        if(url.contains(".js") || url.contains(".css") || url.contains(".jpg")|| url.contains(".svg")|| url.contains(".png") || url.contains(".ico") || url.contains(".eot")|| url.contains(".ttf")|| url.contains(".woff") || url.contains(".woff2")) {
            return true;
        }
        if (url.length() == 1) {
            response.sendRedirect("/main");
            return true;
        }
        if(url.indexOf("pdfjs") >=0) {
            return true;
        }
        if(url.indexOf("company")>=0) {
            if (currentCompany == null) {
                response.sendRedirect("/company/login");
                return false;
            } else {
                return true;
            }
        }else if(url.indexOf("admin")>=0){
            if (currentAdmin == null) {
                response.sendRedirect("/admin/login");
                return false;
            } else {
                return true;
            }
        }else {
            if (currentUser == null) {
                response.sendRedirect("/login");
                return false;
            } else {
                return true;
            }
        }
    }
}
