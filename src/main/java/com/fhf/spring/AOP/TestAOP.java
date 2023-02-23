package com.fhf.spring.AOP;

import com.fhf.spring.AOP.proxy.Calculator;
import com.fhf.spring.AOP.proxy.Casio;
import org.checkerframework.checker.units.qual.C;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAOP {

    static ApplicationContext ap;
    @BeforeClass
    public static void init(){
        //包含三个子类：ClassPathXmlApplicationContext,FileSystemXmlApplicationContext,ConfigurableApplicationContext
        ap = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @Test
    public void testAOP(){
        //无法直接获得切面包含对象，只能通过实现接口的方式获得对应对象
        Calculator casio = ap.getBean(Calculator.class);
        casio.divide(1, 0);
    }
}
