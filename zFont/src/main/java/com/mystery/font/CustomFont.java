package com.mystery.font;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义字体：可以指定当前界面采用那种字体
 * xml中设置了style代表bold（medium）,没有设置则是默认regular
 * 设置了fontFamily则textStyle失效以此解决不常用的bold，xml中引入或者代码设置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomFont {

    int value() default FontConstant.OppoFont;
}
