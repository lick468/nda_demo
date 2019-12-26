package com.nenu.config;


import com.nenu.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
               .excludePathPatterns("/login", "/company/login","/admin/login","/register","/company/register",
                       "/getCode","/register","/main","/findPsd","/updatePsd","/company/findPsd",
                       "/company/updatePsd","/webjars/**");
    }
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/file/**").addResourceLocations("file:C:/tempFiles/");
        registry.addResourceHandler("/image/**").addResourceLocations("file:C:/download/");
        registry.addResourceHandler("/download/**").addResourceLocations("file:C:/ndadata/download");
        registry.addResourceHandler("/company/download/**").addResourceLocations("file:C:/ndadata/download");
    }

}
