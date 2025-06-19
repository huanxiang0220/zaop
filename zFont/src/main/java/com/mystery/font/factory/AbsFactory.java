package com.mystery.font.factory;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;

public abstract class AbsFactory implements LayoutInflater.Factory2 {

    //<editor-fold desc="注入Factory2">
    public static LayoutInflater inject(LayoutInflater inflater, AbsFactory factory) {
        LayoutInflater.Factory2 factory2 = inflater.getFactory2();
        if (!(factory2 instanceof AbsFactory && factory2.getClass() == factory.getClass())) {
            Class<LayoutInflaterCompat> compatClass = LayoutInflaterCompat.class;
            Class<LayoutInflater> inflaterClass = LayoutInflater.class;
            try {
                Field sCheckedField = compatClass.getDeclaredField("sCheckedField");
                sCheckedField.setAccessible(true);
                sCheckedField.setBoolean(inflater, false);
                Field mFactory = inflaterClass.getDeclaredField("mFactory");
                mFactory.setAccessible(true);
                Field mFactory2 = inflaterClass.getDeclaredField("mFactory2");
                mFactory2.setAccessible(true);
                if (inflater.getFactory2() != null) {
                    factory.setInterceptFactory2(inflater.getFactory2());
                } else if (inflater.getFactory() != null) {
                    factory.setInterceptFactory(inflater.getFactory());
                }
                mFactory2.set(inflater, factory);
                mFactory.set(inflater, factory);

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return inflater;
    }
    //</editor-fold>

    protected LayoutInflater.Factory mViewCreateFactory;
    protected LayoutInflater.Factory2 mViewCreateFactory2;

    public final void setInterceptFactory(LayoutInflater.Factory factory) {
        this.mViewCreateFactory = factory;
    }

    public final void setInterceptFactory2(LayoutInflater.Factory2 factory2) {
        this.mViewCreateFactory2 = factory2;
    }

    @Nullable
    @Override
    public final View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        //防止与其他调用factory库冲突，例如字体、皮肤替换库，用已经设置的factory来创建view
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }

        if (view instanceof ViewGroup) {
            setViewGroup(context, (ViewGroup) view);
        }

        if (view instanceof TextView) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{
                    android.R.attr.textStyle,
                    android.R.attr.fontFamily,
            });
            boolean hasTextStyle = a.hasValue(0);
            boolean hasFontFamily = a.hasValue(1);
            a.recycle();

            setTypeface(context, (TextView) view, hasFontFamily, hasTextStyle);
        }
        return view;
    }

    public void setViewGroup(Context context, ViewGroup vg) {

    }

    public abstract void setTypeface(Context context, TextView tv, boolean hasFamily, boolean hasStyle);

}