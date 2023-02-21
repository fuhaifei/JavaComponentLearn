package com.fhf.spring.IOC.annotation;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestBean {

    @PostConstruct
    public void postConstruct(){
        System.out.println("初始化方法");
    }
}
