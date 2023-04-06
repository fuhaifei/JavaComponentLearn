package com.fhf.spring.AOP.proxy;

import org.junit.Test;

public class StaticProxy implements Calculator {

    private Calculator calculator;

    @Override
    public int add(int a, int b) {
        return 0;
    }

    @Override
    public int minus(int a, int b) {
        return 0;
    }

    @Override
    public int multi(int a, int b){
        System.out.println("进行乘法运算,输入参数为:"+a+" " + b);
        int result = calculator.multi(a, b);
        System.out.println("运算结果为:"+result);
        return result;
    }

    @Override
    public int divide(int a, int b) {
        return 0;
    }

    @Test
    public void testProxy(){
        StaticProxy staticProxy = new StaticProxy();
        staticProxy.calculator  = new Casio();
        //返回代理对象
        Calculator c = staticProxy;
        c.multi(1, 2);
    }
}
