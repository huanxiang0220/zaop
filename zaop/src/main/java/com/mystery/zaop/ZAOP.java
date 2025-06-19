package com.mystery.zaop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.mystery.zaop.logger.ILogger;
import com.mystery.zaop.logger.ZLogger;
import com.mystery.zaop.permission.OnPermissionListener;


public class ZAOP {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    /**
     * 初始化
     */
    public static void init(Application application) {
        sContext = application.getApplicationContext();
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XAOP.init() 初始化！");
        }
    }

    private static OnPermissionListener sOnPermissionListener;

    public static void setOnPermissionListener(OnPermissionListener sOnPermissionListener) {
        ZAOP.sOnPermissionListener = sOnPermissionListener;
    }

    public static OnPermissionListener getOnPermissionListener() {
        return sOnPermissionListener;
    }

    /**
     * 设置是否打开调试
     *
     * @param isDebug 是否打开调试
     */
    public static void debug(boolean isDebug) {
        ZLogger.debug(isDebug);
    }

    /**
     * 设置调试模式
     *
     * @param tag tag信息
     */
    public static void debug(String tag) {
        ZLogger.debug(tag);
    }

    /**
     * 设置打印日志的等级（只打印改等级以上的日志）
     *
     * @param priority 日志的等级
     */
    public static void setPriority(int priority) {
        ZLogger.setPriority(priority);
    }

    /**
     * 设置日志记录者的接口
     *
     * @param logger 日志记录者的接口
     */
    public static void setLogger(@NonNull ILogger logger) {
        ZLogger.setLogger(logger);
    }

}