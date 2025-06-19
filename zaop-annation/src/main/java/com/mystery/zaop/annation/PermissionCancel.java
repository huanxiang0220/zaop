package com.mystery.zaop.annation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户拒绝权限，但未勾选“不在提醒”
 * <p>
 * 当希望在权限被拒绝未勾选“不再提醒”时可以做一些提醒操作时则添加一个带有该注解的方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionCancel {

    int requestCode() default PermissionConst.PARAM_PERMSSION_CODE_DEFAULT;
}
