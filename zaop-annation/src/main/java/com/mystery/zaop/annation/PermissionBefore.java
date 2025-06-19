package com.mystery.zaop.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 进行注解权限请求之前，通过该标记，可以用来提示用户为什么要申请权限，
 * 注意：当前使用到此注解的时候与华为手机申请前提示将形成冲突，故华为手机申请前也是走该注解方法，原有逻辑不再继续
 * 被注解的方法需要携带一个参数IRationale ration或者两个参数int requestCode,IRationale ration
 * <p>
 *
 * 用法示例一
 * @PermissionBefore
 * public void method(IRationale ration){
 * }
 *
 * 用法示例二
 * @PermissionBefore
 * public void method(int requestCode,IRationale ration){
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionBefore {
}
