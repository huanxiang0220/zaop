package com.mystery.font.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

public class ViewGroupContextModifier {

    /**
     * 修改 RecyclerView 的上下文对象
     *
     * @param viewGroup  要修改上下文的 RecyclerView
     * @param newContext 新的上下文对象
     */
    public static void modifyContext(ViewGroup viewGroup, Context newContext) {
        try {
            // 获取 View 类中的 mContext 字段，因为 RecyclerView 继承自 View，上下文对象存储在 mContext 中
            Field contextField = View.class.getDeclaredField("mContext");
            // 设置字段可访问
            contextField.setAccessible(true);
            // 修改字段的值为新的上下文对象
            contextField.set(viewGroup, newContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}