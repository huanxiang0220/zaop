package com.mystery.font.aspect;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mystery.font.CustomFont;
import com.mystery.font.FontUtils;
import com.mystery.zaop.logger.ZLogger;
import com.mystery.font.utils.CustomFontUtil;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Dialog的setContentView方法之后进行拦截，根据Dialog是否有注解，没有注解则追踪栈查询自己是否处于一个被注解的一个元素内
 */
@Aspect
public class DialogAspect {

    // 定义切点，匹配 Dialog.setContentView 方法执行
    @Pointcut("execution(* android.app.Dialog+.setContentView(..))")
    public void dialogSetContentView() {
    }

    // 定义后置通知，在 Dialog.setContentView 方法执行后执行
    @After("dialogSetContentView() && args(view)")
    public void afterDialogSetContentView(JoinPoint joinPoint, View view) {
        ZLogger.eTag("DialogAspect", "Dialog.setContentView 方法调用完成");

        Object thisObj = joinPoint.getThis();
        CustomFont customFont = CustomFontUtil.getCustomFont(thisObj);
        if (customFont != null && view != null) {
            recursion(view, customFont.value());
        }
    }

    private void recursion(View view, int font) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int childCount = vg.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childAt = vg.getChildAt(i);
                if (childAt instanceof TextView) {
                    ZLogger.e(">>>> " + childAt);
                    Typeface typeface = FontUtils.getTypeface(childAt.getContext(), font, 0);
                    ((TextView) childAt).setTypeface(typeface);
                } else {
                    recursion(childAt, font);
                }
            }
        }
    }

}