package com.fhf.spring;


import com.fhf.spring.IOC.Bicycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Spring Test与SpringBoot Test区别与联系简单整理
 * 1. Spring Test：两个注解开启设置测试类 @RunWith(SpringJUnit4ClassRunner.class)->使用spring测试环境替代JUnit4的测试环境
 *                                   @ContextConfiguration("classpath:applicationContext.xml") ->指定spring上下文配置文件
 * 2.
 * */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringTest {

    @Autowired
    Bicycle bicycle;
    public void test(){

    }
}
