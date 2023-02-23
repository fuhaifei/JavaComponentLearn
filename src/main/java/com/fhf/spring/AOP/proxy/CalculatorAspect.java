package com.fhf.spring.AOP.proxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@Aspect
public class CalculatorAspect {
    //前置通知
    @Before("execution(public int com.fhf.spring.AOP.proxy.Casio.*(..))")
    private void beforeCalculate(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("AOP 前置通知,方法名："+methodName + " 参数值" + args + " 代理对象类：" + joinPoint.getTarget().getClass());
    }
    //后置返回通知
    @AfterReturning(value = "execution(public int com.fhf.spring.AOP.proxy.Casio.*(..))", returning = "result")
    private void afterCalculateReturn(JoinPoint joinPoint, Object result){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("AOP 后置返回通知,方法名："+methodName + " 结果:" +  result+ " 代理对象类：" + joinPoint.getTarget().getClass());
    }

    //后置通知
    @After("execution(public int com.fhf.spring.AOP.proxy.Casio.*(..))")
    private void afterCalculate(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("AOP 后置通知,方法名："+methodName + " 代理对象类：" + joinPoint.getTarget().getClass() + "(执行结果)");
    }

    //异常通知

    @AfterThrowing(value = "execution(public int com.fhf.spring.AOP.proxy.Casio.*(..))", throwing = "ex")
    public void afterThrowingMethod(JoinPoint joinPoint, Throwable ex){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Logger-->异常通知，方法名："+methodName+"，异常："+ex);
    }

    @Around("execution(public int com.fhf.spring.AOP.proxy.Casio.*(..))")
    private Object around(ProceedingJoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());
        Object result = null;
        try {
            System.out.println("环绕通知-->目标对象方法执行之前");
//目标方法的执行，目标方法的返回值一定要返回给外界调用者
            result = joinPoint.proceed();
            System.out.println("环绕通知-->目标对象方法返回值之后");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println("环绕通知-->目标对象方法出现异常时");
        } finally {
            System.out.println("环绕通知-->目标对象方法执行完毕");
        }
        return result;
    }
}
