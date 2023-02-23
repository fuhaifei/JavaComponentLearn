package com.fhf.spring.AOP.proxy;

import org.springframework.stereotype.Component;

@Component
public class Casio implements Calculator{
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int minus(int a, int b) {
        return a - b;
    }

    @Override
    public int multi(int a, int b) {
        return a * b;
    }

    @Override
    public int divide(int a, int b) {
        return a / b;
    }
}
