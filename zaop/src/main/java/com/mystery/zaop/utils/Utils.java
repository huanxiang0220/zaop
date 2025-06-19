package com.mystery.zaop.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

public class Utils {

    public static String getClassName(Class<?> cls) {
        if (cls == null) {
            return "<UnKnow Class>";
        }
        if (cls.isAnonymousClass()) {
            return getClassName(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }

    /**
     * 获取方法的描述信息
     *
     * @param joinPoint
     * @return
     */
    public static String getMethodDescribeInfo(final ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        Class<?> cls = codeSignature.getDeclaringType(); //方法所在类
        String methodName = codeSignature.getName();    //方法名
        return Utils.getClassName(cls) + "->" + methodName;
    }

}