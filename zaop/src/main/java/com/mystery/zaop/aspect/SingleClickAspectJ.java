package com.mystery.zaop.aspect;

import com.mystery.zaop.annation.SingleClick;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 防止View被连续点击
 */
@Aspect
public class SingleClickAspectJ {

    private long lastClickTime = 0;

    @Pointcut("within(@com.mystery.zaop.annation.SingleClick *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.mystery.zaop.annation.SingleClick * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }  //方法切入点

    @Around("method() && @annotation(singleClick)")//在连接点进行方法替换
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint, SingleClick singleClick) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SingleClick annotation = method.getAnnotation(SingleClick.class);
        if (annotation != null) {
            long interval = annotation.value();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime >= interval) {
                lastClickTime = currentTime;
                joinPoint.proceed();
            }
        }

//        View view = null;
//        for (Object arg : joinPoint.getArgs()) {
//            if (arg instanceof View) {
//                view = (View) arg;
//                break;
//            }
//        }
//        if (view != null) {
//            if (!ClickUtils.isFastDoubleClick(view, singleClick.value())) {
//                joinPoint.proceed();//不是快速点击，执行原方法
//            } else {
//                ZLogger.d(Utils.getMethodDescribeInfo(joinPoint) + ":发生快速点击，View id:" + view.getId());
//            }
//        }
    }

}