package com.mystery.font.cache;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.mystery.font.FontConstant;

/**
 * 用来保存Fragment信息
 */
public class FragmentRecord {

    public final Activity activity;

    @FontConstant.FontType
    public final int font;//字体

    public final Fragment fragment;

    public FragmentRecord(Activity activity, int font, Fragment fragment) {
        this.activity = activity;
        this.font = font;
        this.fragment = fragment;
    }

}