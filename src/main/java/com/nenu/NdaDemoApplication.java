package com.nenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.spring.annotation.MapperScan;



@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = "com.nenu.mapper")
/*添加@ServletComponentScan注解后Servlet、Filter、Listener
* 可以直接通过 @WebServlet、@WebFilter、@WebListener 注解自动注册，无需其他代码。
* */
//@ServletComponentScan
public class NdaDemoApplication  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(NdaDemoApplication.class, args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(NdaDemoApplication.class);
    }
}
