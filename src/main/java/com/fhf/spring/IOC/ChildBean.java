package com.fhf.spring.IOC;

public class ChildBean extends FatherBean implements Comparable<ChildBean>{
    private Integer age;

    public ChildBean(String name, Integer age){
        this.name = name;
        this.age = age;
    }
    public ChildBean(){

    }
    @Override
    public void introduce(){
        System.out.println("my name is " + name + ", i have a father");
    }

    @Override
    public int compareTo(ChildBean o) {
        return this.age - o.age;
    }
}
