package com.mystery.font.aspect;

import androidx.fragment.app.Fragment;

import com.mystery.font.FontContext;
import com.mystery.font.cache.FragmentCache;
import com.mystery.font.cache.FragmentRecord;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * com.zhitongcaijin.ztc.controller.BaseController
 * 对LayoutInflater.from替换上下文
 */
@Aspect
public class BaseControllerLayoutInflaterAspect {

    // 使用 ThreadLocal 保存调用者对象 A 的上下文
    private static final ThreadLocal<Object> callerContext = new ThreadLocal<>();

    // 拦截所有创建 BaseController 子类对象的 new 操作（call 切点）
    @Pointcut("call(com.zhitongcaijin.ztc.controller.BaseController+.new(..))")
    public void baseControllerSubclassCreation() {
    }

    // 拦截 BaseController 构造函数执行（execution 切点）
    @Pointcut("execution(com.zhitongcaijin.ztc.controller.BaseController+.new(..))")
    public void baseControllerConstructorExecution() {
    }

//    // 拦截 inflaterAdd 方法调用
//    @Pointcut("call(* com.zhitongcaijin.ztc.controller.BaseController.inflaterAdd(..)) " +
//            "&& withincode(com.zhitongcaijin.ztc.controller.BaseController.new(..))")
//    public void inflaterAddCall() {
//    }

    // 切点：在BaseController中匹配LayoutInflater.from(Context) 的调用
    @Pointcut("call(* android.view.LayoutInflater.from(android.content.Context)) && within(com.zhitongcaijin.ztc.controller.BaseController)")
    public void layoutInflater_from_call() {
    }

    // 在 new 操作前触发：保存调用者对象 A 到 ThreadLocal
    @Before("baseControllerSubclassCreation()")
    public void captureCaller(JoinPoint joinPoint) {
        Object caller = joinPoint.getThis(); // 获取调用者对象 A
        callerContext.set(caller);
    }

    // 在 BaseController 构造函数执行后触发：清理 ThreadLocal
    @After("baseControllerConstructorExecution()")
    public void clearCallerContext() {
        callerContext.remove();
    }

    // 在BaseController的inflaterAdd方法中的LayoutInflater.from 方法调用前后触发：获取 A 和 B 对象
    @Around("layoutInflater_from_call()")
    public Object logObjects(ProceedingJoinPoint joinPoint) throws Throwable {
        Object a = callerContext.get(); // 调用者对象 A
        System.out.println("调用者对象 A: " + a);
        System.out.println("创建的子类对象 B: " + joinPoint.getThis());

        Object[] args = joinPoint.getArgs();

        if (a instanceof Fragment) {
            Fragment f = (Fragment) a;
            FragmentRecord record = FragmentCache.getInstance().contains(f);
            if (record != null) {
                int font = record.font;
                FontContext fontContext = new FontContext(record.activity, font);
                // 替换参数中的 Activity
                args[0] = fontContext;
            }
        }
        return joinPoint.proceed(args);
    }


    // 前置通知：在inflaterAdd调用前触发
//    @Before("baseControllerSubclassConstructorCall()")
//    public void beforeLayoutInflaterCall(JoinPoint joinPoint) {
//        ZLogger.eTag("BaseControllerAspect", "beforeLayoutInflaterCall");
//
//        Object aThis = joinPoint.getThis();
//        if (aThis instanceof Fragment) {
//            Fragment fragment = (Fragment) aThis;
//            FragmentRecord record = FragmentCache.getInstance().contains(fragment);
//            if (record != null) {
//                ControllerCache.getInstance().put(joinPoint);
//            }
//        }
//    }

//    // 定义切点，匹配 BaseController 类的 afterInflaterAdd 方法
//    @Pointcut("execution(* com.zhitongcaijin.ztc.controller.BaseController.inflaterAdd(..))")
//    public void afterInflaterAddMethod() {}
//
//    // 定义切点，匹配 LayoutInflater.from(activity) 方法调用
//    @Pointcut("call(* android.view.LayoutInflater.from(android.content.Context))")
//    public void layoutInflaterFromCall() {}
//
//    // 定义复合切点，匹配在 afterInflaterAdd 方法中调用 LayoutInflater.from(activity)
//    @Pointcut("afterInflaterAddMethod() && cflow(layoutInflaterFromCall())")
//    public void targetCallInAfterInflaterAdd() {
//
//    }
//
//    // 定义通知，在切点匹配时执行
//    @Before("targetCallInAfterInflaterAdd()")
//    public void afterTargetCall(JoinPoint joinPoint) {
//        System.out.println("在 BaseController 的 afterInflaterAdd 方法中调用了 LayoutInflater.from(activity)");
//    }


    // 定义 Pointcut 表达式，匹配 LayoutInflater.from(activity) 的调用
//    @Pointcut("call(android.view.LayoutInflater android.view.LayoutInflater.from(..)) " +
//            "&& withincode(* com.zhitongcaijin.ztc.controller.BaseController.inflaterAdd(..));")
//    public void inflaterFromCall(){
//
//    }


//    // 在 inflaterAdd 方法执行后执行
//    @After("inflaterAddMethod()")
//    public void afterInflaterAdd(JoinPoint joinPoint) {
//        System.out.println("After calling inflaterAdd method: " + joinPoint.getSignature().getName());
//    }
//
    // 在 LayoutInflater.from(activity) 调用前执行
//    @Before("inflaterFromCall()")
//    public void beforeLayoutInflaterFromCall(JoinPoint joinPoint) {
//        System.out.println("Before calling LayoutInflater.from() in inflaterAdd method");
//    }

//    @After("inflaterAddMethod()")
//    public void afterObjectCreation(JoinPoint joinPoint) throws IllegalAccessException, InstantiationException {
//        Class<?> clazz = joinPoint.getThis().getClass();
//        //自身是否有注解
//        boolean annotationPresent = clazz.isAnnotationPresent(CustomFont.class);
//        if (annotationPresent) {
//            Object obj = joinPoint.getThis();
//            CustomFont customFont = clazz.getAnnotation(CustomFont.class);
//            if (customFont != null) {
//                BaseControllerContextModifier.modifyContext(obj, customFont.value());
//            }
//            return;
//        }
//
//        //解决自身没有注解，寻求外层View所处的类是否有注解
////        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
////        if (stackTrace.length >= 5) {
////            StackTraceElement caller = stackTrace[4];
////            //得到类名
////            String creatingClassName = caller.getClassName();
////            ZLogger.e("创建我的类是: " + creatingClassName);
////            FragmentRecord fragmentRecord = FragmentRecord.getInstance();
////
////            try {
////                Class<?> clazz = Class.forName(creatingClassName);
////                boolean isFragment = Fragment.class.isAssignableFrom(clazz);
////                if (isFragment) {
////
////
////                    String methodName = caller.getMethodName();
////                    Method method = clazz.getDeclaredMethod(methodName);
////                    Object obj = method.getDeclaringClass().newInstance();
////                    ZLogger.e(obj.toString());
////                }
////            } catch (ClassNotFoundException | NoSuchMethodException e) {
////                ZLogger.e(e.getMessage());
////            }
////        }
//    }

}