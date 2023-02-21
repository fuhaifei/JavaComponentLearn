package com.fhf.spring.IOC;

public class FatherBean {
    String name;

    public void introduce(){
        System.out.println("my name is " + name + " ,I'm a father now");
    }

    public void setName(String name) {
        this.name = name;
    }
}
