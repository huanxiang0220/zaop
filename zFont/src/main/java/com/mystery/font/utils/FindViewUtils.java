package com.mystery.font.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FindViewUtils {

    /**
     * 查找出Fragment下所有的RecyclerView好打上标机
     */
    public static List<RecyclerView> findAllRecyclerViews(View view) {
        List<RecyclerView> recyclerViews = new ArrayList<>();
        findAllSpeView(recyclerViews, view, RecyclerView.class);
        return recyclerViews;
    }

    public static List<ViewGroup> findAllViewGroups(View view) {
        List<ViewGroup> views = new ArrayList<>();
        findAllSpeView(views, view, ViewGroup.class);
        return views;
    }

    static <T extends ViewGroup> void findAllSpeView(List<T> views, View findView, Class<T> clazz) {
        if (clazz.isInstance(findView)) {
            T viewGroup = (T) findView;
            views.add(viewGroup);

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findAllSpeView(views, viewGroup.getChildAt(i), clazz);
            }
        }
    }

}