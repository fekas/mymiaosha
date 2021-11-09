package com.zhongbin.miaoshademo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zhongbin.miaoshademo.mapper")
public class MiaoshademoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiaoshademoApplication.class, args);
    }

}
