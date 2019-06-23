package com.nenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = "com.nenu.mapper")
public class NdaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NdaDemoApplication.class, args);
    }

}
