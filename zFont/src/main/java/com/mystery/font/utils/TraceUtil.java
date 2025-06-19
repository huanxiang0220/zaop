package com.mystery.font.utils;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.mystery.font.CustomFont;

/**
 * 栈跟踪查询是否有标记@CustomFont
 */
public class TraceUtil {

    /**
     * 当前的栈查询是否处于一个被标记的元素内
     */
    protected static CustomFont traceBack() {
        //进行追溯
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3) {
            //从第四个开始追溯
            return TraceUtil.traceBack(stackTrace, 3);
        }
        return null;
    }

    /**
     * 一直往上层追踪直到Fragment或Activity即可到：
     * 如果第一个满足的元素是Fragment，那么第一个Fragment看是否标注@CustomFont，没有则继续往上直到Fragment或Activity
     * 标注@CustomFont或者没有找到
     * 如果第一个满足的元素是Activity，直接获取标注之后返回
     * <p>
     *
     * @param stackTrace 追踪栈
     * @param index      第几层
     * @return 返回@CustomFont的结果
     */
    private static CustomFont traceBack(StackTraceElement[] stackTrace, int index) {
        try {
            if (index >= stackTrace.length || index < 0) {
                return null;
            }
            StackTraceElement caller = stackTrace[index];
            //得到类名
            String creatingClassName = caller.getClassName();
            //得到字节码
            Class<?> traceClazz = Class.forName(creatingClassName);
            boolean isFragment = Fragment.class.isAssignableFrom(traceClazz);
            if (isFragment) {
                //那必然是Activity嵌套Fragment的结构
                boolean isPresent = traceClazz.isAnnotationPresent(CustomFont.class);
                if (isPresent) {
                    //最近的Fragment找打了CustomFont.class
                    return traceClazz.getAnnotation(CustomFont.class);
                }
            }
            boolean isActivity = Activity.class.isAssignableFrom(traceClazz);
            if (isActivity) {
                //无论有无注解都要结束追溯了

                //那必然是Activity嵌套Fragment的结构
                boolean isPresent = traceClazz.isAnnotationPresent(CustomFont.class);
                if (isPresent) {
                    //最近的Fragment找打了CustomFont.class
                    return traceClazz.getAnnotation(CustomFont.class);
                }
                return null;//没有找到
            }
            index++;
            return traceBack(stackTrace, index);

        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}