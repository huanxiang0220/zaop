package com.mystery.zaop.annation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于跳转设置界面：
 * 用户拒绝权限，但勾选了“不在提醒”，只能跳转到设置界面操作权限
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionSetting {

    int requestCode() default PermissionConst.PARAM_PERMSSION_CODE_DEFAULT;
}