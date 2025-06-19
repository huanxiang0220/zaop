package com.mystery.font;

import android.view.LayoutInflater;

import androidx.core.view.LayoutInflaterCompat;

import com.mystery.font.factory.FontFactory;

import java.lang.reflect.Field;

public class FontInject {

    public static LayoutInflater inject(LayoutInflater inflater, @FontConstant.FontType int font) {
        if (!(inflater.getFactory2() instanceof FontFactory)) {
            forceSetFactory2(inflater, font);
        }
        return inflater;
    }

    private static void forceSetFactory2(LayoutInflater inflater, @FontConstant.FontType int font) {
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
            FontFactory factory = new FontFactory(font);
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

}