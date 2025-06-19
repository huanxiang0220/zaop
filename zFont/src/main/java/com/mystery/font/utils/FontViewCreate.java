package com.mystery.font.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者:  tang
 * 时间： 2020/8/3 0003 上午 11:30
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
public class FontViewCreate {

    /**
     * 例如：new TextView(context, attr)  或  new Button(context, attr)  或 new Button(context, attr) ...
     * 所以需要建立 获取控件的构造方法 参数类型，好去创建构造对象
     */
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    /**
     * 进行缓存起来，因为 ClassLoader getConstructor 是耗费性能的
     */
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();

    static View onCreateView(View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        AppCompatDelegate delegate = ((AppCompatActivity) context).getDelegate();
        View view;
        if (-1 == name.indexOf('.')) {
            view = delegate.createView(parent, name, context, attrs);
        } else {
            view = customizeCreateView(name, context, attrs);
        }
        return view;
    }

    /**
     * （源码615行）
     * 真正进行反射的方式创建View
     * 1.当传入 name + s  -->  控件名 + 控件包名， 需要创建系统的控件。
     * 2.当传入 name + "" -->  控件名 + ""        这个控件名就是完整的 自定义 包名+自定义控件名
     */
    private static View customizeCreateView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructors = sConstructorMap.get(name);

        if (null == constructors) {
            // 反射找
            try {
                Class<? extends View> clzz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                Constructor<? extends View> constructor = clzz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor); // 缓存一份
                return constructor.newInstance(context, attrs);
            } catch (Exception e) {
                return null;
            }
        } else {
            try {
                constructors.setAccessible(true);
                return constructors.newInstance(context, attrs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}