package com.mystery.zaop.annation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 进行注解权限请求标记
 * 注意：当同一个类中出现两个需要Permission注解的时候必须携带上requestCode进行区分,
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {

    /**
     * 设置华为手机申请之前的提示语
     * 注意：当注册了PermissionBefore,该参数失效，自行在PermissionBefore处理
     * 1.0.3之后不再使用资源id，请使用string资源名称，id不可靠，
     */
    String prompt();

    /**
     * 所申请的那些权限
     */
    String[] value();

    /**
     * 本次权限申请请求码
     */
    int requestCode() default PermissionConst.PARAM_PERMSSION_CODE_DEFAULT;

    /**
     * 从设置界面回来之后是否继续执行原有的逻辑
     */
    boolean goBackContinue() default false;
}
