package com.news;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.news.mapper")
public class WXNewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WXNewsApplication.class, args);
    }
}
