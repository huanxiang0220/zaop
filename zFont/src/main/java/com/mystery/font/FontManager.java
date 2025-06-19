package com.mystery.font;

import android.app.Application;

public class FontManager {
    private static volatile FontManager sInstance;

    private FontManager(Application application) {
        application.registerActivityLifecycleCallbacks(new FontActivityLifecycle());
    }

    /**
     * 初始化 必须在Application中先进行初始化
     */
    public static void init(Application application) {
        if (sInstance == null) {
            synchronized (FontManager.class) {
                if (sInstance == null) {
                    sInstance = new FontManager(application);
                }
            }
        }
    }

    /**
     * 是否是调试模式
     */
    private static boolean sIsDebug = false;

    /**
     * 设置是否是调试模式
     *
     * @param isDebug 是否是调试模式
     */
    public static void setDebug(boolean isDebug) {
        FontManager.sIsDebug = isDebug;
    }

    /**
     * 当前是否是调试模式
     *
     * @return 是否是调试模式
     */
    public static boolean isDebug() {
        return sIsDebug;
    }

}