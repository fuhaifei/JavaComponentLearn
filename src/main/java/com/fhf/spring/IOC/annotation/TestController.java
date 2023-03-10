package com.fhf.spring.IOC.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

//@Controller
@Configuration
public class TestController {

    @Autowired
    TestService testService;
    @Qualifier()
    public void setTestService(@Autowired TestService testService){
        this.testService = testService;
    }

    public void testController(){
        testService.testService();
    }
}
