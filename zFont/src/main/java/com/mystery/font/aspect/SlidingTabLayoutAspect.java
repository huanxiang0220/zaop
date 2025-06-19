//package com.mystery.font.aspect;
//
//import android.widget.TextView;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//
//@Aspect
//public class SlidingTabLayoutAspect {
//
//    // 定义切入点，匹配 setFakeBoldText 方法的调用
//    @Pointcut("call(* android.graphics.Paint.setFakeBoldText(boolean)) && target(tv_tab_title) && within(com.flyco.tablayout.SlidingTabLayout)")
//    public void setFakeBoldTextCall(TextView tv_tab_title) {
//    }
//
//    // 定义环绕通知，在方法调用前后执行自定义逻辑
//    @Around("setFakeBoldTextCall(tv_tab_title)")
//    public Object aroundSetFakeBoldText(ProceedingJoinPoint joinPoint, TextView tv_tab_title) throws Throwable {
//        // 在方法调用前执行自定义逻辑
//        System.out.println("Before setFakeBoldText: " + tv_tab_title.getText());
//
//        // 执行原始方法
//        Object result = joinPoint.proceed();
//
//        // 在方法调用后执行自定义逻辑
//        System.out.println("After setFakeBoldText: " + tv_tab_title.getText());
//
//        return result;
//    }
//
//}