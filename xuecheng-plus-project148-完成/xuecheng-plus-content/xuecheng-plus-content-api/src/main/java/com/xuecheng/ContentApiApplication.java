package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableSwagger2Doc
@EnableFeignClients
@SpringBootApplication
public class ContentApiApplication {

    @Value("${test_config.a}")
    String a;
    @Value("${test_config.b}")
    String b;
    @Value("${test_config.c}")
    String c;
    @Value("${test_config.d}")
    String d;


    @Bean
    public Integer getTest(){
        System.out.println("a="+a);
        System.out.println("b="+b);
        System.out.println("c="+c);
        System.out.println("d="+d);
        return new Integer(1);
    }

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }

}
