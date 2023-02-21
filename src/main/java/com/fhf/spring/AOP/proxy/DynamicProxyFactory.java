package com.fhf.spring.AOP.proxy;


import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyFactory{

    public Object getProxy(Object target){
        //实现包装类包装方法
        InvocationHandler handler =  new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("log[normal]:before call class " + proxy.getClass() + " method:" + method.getName());
                Object result = null;
                try {

                    result = method.invoke(target, args);
                }catch (Exception e){
                    System.out.println("log[error]:after call class " + proxy.getClass() + " method:" + method.getName() + " error");

                }finally {
                    System.out.println("log[normal]:after call class " + proxy.getClass() + " method:" + method.getName() + " finish");
                }
                return result;
            }
        };
        //返回一个包装类对象
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), handler);
    }

    @Test
    public void testDynamicProxy(){
        Calculator proxy = (Calculator) new DynamicProxyFactory().getProxy(new Casio());
        proxy.add(1, 2);
        proxy.divide(1, 0);
    }
}
