package com.mystery.font.utils;

import com.mystery.font.CustomFont;

public class CustomFontUtil {

    /**
     * 从当前对像上获取注解
     *
     * @param obj 需要查询的对象
     * @return CustomFont
     */
    public static CustomFont getCustomFont(Object obj) {
        //拿到当前对象的字节码
        Class<?> clazz = obj.getClass();
        boolean annotationPresent = clazz.isAnnotationPresent(CustomFont.class);
        //自身是否有注解,以自己为主
        if (annotationPresent) {
            return clazz.getAnnotation(CustomFont.class);
        }
        //从栈上尝试获取
//        return TraceUtil.traceBack();
        return null;
    }

}
