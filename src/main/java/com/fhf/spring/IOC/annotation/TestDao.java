package com.fhf.spring.IOC.annotation;

import org.springframework.stereotype.Repository;

@Repository
public class TestDao {
    public void testDao(){
        System.out.println("测试基于注解的自动装配");
    }
}
