package com.nenu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String cookie = request.getHeader("Cookie");
        HttpSession session = request.getSession(false);
        String requestUrl = request.getRequestURI();

        // 静态资源放行
        if(requestUrl.contains(".js") || requestUrl.contains(".css") || requestUrl.contains(".jpg") || requestUrl.contains(".ico")) {
            return true;
        }
        //登录和修改密码放行
        if(requestUrl.contains("/login") || requestUrl.contains("/getCode")|| requestUrl.contains("/register")|| requestUrl.contains("/findPsd") ||  requestUrl.contains("/updatePsd") ||  requestUrl.contains("/ipfs") ||  requestUrl.contains("/upload") ||  requestUrl.contains("/preview")||  requestUrl.contains("/previewAction")  ) {
            return true;
        }

        //cookie 判断
        if(cookie != null && cookie.contains("JSESSIONID")) {
            if(session == null) {
                response.sendRedirect("/login");
                return false;
            }
            return true;
        }else {
            response.sendRedirect("/login");
            return false;
        }
    }
}
