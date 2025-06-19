package com.mystery.font.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mystery.font.CustomFont;
import com.mystery.font.FontContext;
import com.mystery.font.FontInject;
import com.mystery.zaop.logger.ZLogger;

import java.util.List;

/**
 * 根据是否@CustonFont来用于获取一个LayoutInflater
 */
public class InflaterAcquire {

    public static View getRootView(Object baseController, Activity activity, ViewGroup parent, int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        //获取@CustomFont
        CustomFont customFont = CustomFontUtil.getCustomFont(baseController);
        if (customFont != null) {
            LayoutInflater newInflater = inflater.cloneInContext(activity);
            //设置自定义的Factory
            inflater = FontInject.inject(newInflater, customFont.value());

            //创还能View
            View rootView = inflater.inflate(layoutId, parent, false);
            //经过FontFactory创建的View除了RecyclerView携带的都是mContext都是原有默认的
            //查找ViewGroup
            List<ViewGroup> recyclerViews = FindViewUtils.findAllViewGroups(rootView);
            for (ViewGroup rv : recyclerViews) {
                FontContext fontContext = new FontContext(rv.getContext(), customFont.value());
                ViewGroupContextModifier.modifyContext(rv, fontContext);
            }
            return rootView;
        } else {
            return inflater.inflate(layoutId, parent, false);
        }
    }

    /**
     * 修改 BaseController 的布局加载器
     */
    public static LayoutInflater get(Object obj, Context context) {
        ZLogger.eTag("InflaterAcquire", obj.getClass().getSimpleName());
        LayoutInflater inflater = LayoutInflater.from(context);
        //获取@CustomFont
        CustomFont customFont = CustomFontUtil.getCustomFont(obj);
        if (customFont != null) {
            LayoutInflater newInflater = inflater.cloneInContext(context);
            //设置自定义的Factory
            FontInject.inject(newInflater, customFont.value());
            return newInflater;
        }
        return inflater;
    }

}