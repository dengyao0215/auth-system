package com.atguigu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ClassName: ServiceAuthApplication
 * Package: com.atguigu
 * Description:
 *
 * @Author 邓瑶
 * @Create 2023/5/5 19:20
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.atguigu.system.mapper")
public class ServiceAuthApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceAuthApplication.class,args);
    }
}