package com.example.springbatchtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchTemplateApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchTemplateApplication.class,args)));
//        SpringApplication.run(SpringBatchTemplateApplication.class, args);
    }

}
