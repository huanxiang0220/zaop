package com.mystery.zaop.permission;

import android.content.Context;

public interface OnPermissionListener {

    /**
     * 根据权限获得对应权限的提示标题
     */
    String getPromptTitle(Context context, String[] permission);

//    /**
//     * 申请前进行提示（华为）
//     * prompt：提示内容
//     */
//    void startPrompt(Context context, String prompt, IRationale rationale);

    /**
     * 权限被拒绝，并且“不再提醒”，需要跳转到系统设置开启权限
     */
    void onDenied(Context context, String title, String desc, IRationale rationale);

}