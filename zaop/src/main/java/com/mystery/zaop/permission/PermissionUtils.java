package com.mystery.zaop.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mystery.zaop.annation.PermissionBefore;
import com.mystery.zaop.annation.PermissionCancel;
import com.mystery.zaop.annation.PermissionConst;
import com.mystery.zaop.annation.PermissionSetting;
import com.mystery.zaop.logger.ZLogger;
import com.mystery.zaop.permission.menu.DefaultStartSetting;
import com.mystery.zaop.permission.menu.IMenu;
import com.mystery.zaop.permission.menu.OppoStartSetting;
import com.mystery.zaop.permission.menu.VIVOStartSetting;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();
    // 定义八种权限
    private static final SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSIONS.put("com.android,voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }

    //------------------------------------------------------------------------
    private static final HashMap<String, Class<? extends IMenu>> permissionMenu = new HashMap<>();
    private static final String MANUFACTURER_DEFAULT = "Default";//默认
    public static final String MANUFACTURER_HUAWEI = "huawei";//华为
    public static final String MANUFACTURER_MEIZU = "meizu";//魅族final
    public static final String MANUFACTURER_XIAOMI = "xiaomi";//小米
    public static final String MANUFACTURER_SONY = "sony";//索尼
    public static final String MANUFACTURER_OPPO = "oppo";//"Oppo";
    public static final String MANUFACTURER_LG = "lg";
    public static final String MANUFACTURER_VIVO = "vivo";
    public static final String MANUFACTURER_SAMSUNG = "samsung";//三星

    static {
        permissionMenu.put(MANUFACTURER_DEFAULT, DefaultStartSetting.class);
        permissionMenu.put(MANUFACTURER_OPPO, OppoStartSetting.class);
        permissionMenu.put(MANUFACTURER_VIVO, VIVOStartSetting.class);
    }

    /**
     * 检查是否需要去请求授权，此方法目的，就是检查是否已经授权了
     */
    public static boolean hasPermissionRequest(Context context, String... permissions) {
        for (String permission : permissions) {
            if (permissionExists(permission) && !hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) return false;
        }
        return true;
    }

    /**
     * 检查当前SDK版本权限是否存在，如果存在就return true
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        return minVersion == null || minVersion <= Build.VERSION.SDK_INT;
    }

    /**
     * 最后判断一下，是否真正的成功
     */
    public static boolean requestPermissionSuccess(int... grantedResult) {
        if (grantedResult == null || grantedResult.length < 1) {
            return false;
        }

        for (int permissionValue : grantedResult) {
            if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否拒绝权限但没有勾选“不再询问”（true）、首次和不再提醒是false
     * 用户被拒绝过一次，然后又弹出这个框，【需要给用户一个解释，为什么要授权，就需要执行此方法判断】
     * 1、如果用户之前拒绝了权限请求，但没有选择“不再询问”，则返回 true
     * 2、首次申请|绝了权限请求，并选择了“不再询问”则返回为false
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, String... deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 反射回到到权限类中带有响应注解的函数中去
     */
    public static void invokeAnnotation(Object obj, Class<? extends Annotation> annotationClass, int requestCode) {
        Class<?> objectClass = obj.getClass();

        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);

            //是否被annotationClass 注解过的函数
            boolean annotationPresent = method.isAnnotationPresent(annotationClass);
            if (annotationPresent) {
                Annotation annotation = method.getAnnotation(annotationClass);
                int code = PermissionConst.PARAM_PERMSSION_CODE_DEFAULT;
                if (annotation instanceof PermissionCancel) {
                    code = ((PermissionCancel) annotation).requestCode();
                } else if (annotation instanceof PermissionSetting) {
                    code = ((PermissionSetting) annotation).requestCode();
                }
                if (code != requestCode) {
                    continue;
                }

                //当前方法注解过的函数
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 供客户端调用自行开启AndroidSetting
     */
    public static Intent getStartAndroidSettingIntent(Context context) {
        //拿到当前手机品牌制造商，来获取细节
        Class<? extends IMenu> aClass = permissionMenu.get(Build.BRAND.toLowerCase());

        if (aClass == null) {
            aClass = permissionMenu.get(MANUFACTURER_DEFAULT);
        }

        try {
            IMenu iMenu = aClass.newInstance();
            //拿到意图
            return iMenu.getStartActivity(context);

        } catch (IllegalAccessException | InstantiationException | NullPointerException e) {
            ZLogger.e(e.getMessage());
            return new DefaultStartSetting().getStartActivity(context);
        }
    }

    /**
     * 跳转到设置界面
     */
    public static void startAndroidSetting(Context context) {
        Intent intent = getStartAndroidSettingIntent(context);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 反射被PermissionBefore注解的方法
     */
    public static void invokePermissionBeforeAnnotation(Object obj, int requestCode, IRationale rationale) {
        Class<?> objectClass = obj.getClass();

        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);

            //是否被annotationClass 注解过的函数
            boolean annotationPresent = method.isAnnotationPresent(PermissionBefore.class);
            if (annotationPresent) {
                //当前方法注解过的函数
                try {
                    int count = method.getParameterCount();
                    if (count == 1) {
                        method.invoke(obj, rationale);
                    } else if (count == 2) {
                        method.invoke(obj, requestCode, rationale);
                    } else {
                        ZLogger.e("需要定义的是有一个参数的IRationale或者两个requestCode和IRationale参数的方法");
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    ZLogger.e(e.getMessage());
                }
            }
        }
    }

    /**
     * 是否存在指定的注解
     */
    public static boolean isExistAnnotation(Object thisObj, Class<? extends Annotation> annotationClass) {
        //<editor-fold desc="是否存在带有PermissionSetting的方法">
        Class<?> objectClass = thisObj.getClass();
        Method[] methods = objectClass.getDeclaredMethods();
        boolean isExist = false;
        for (Method method : methods) {
            method.setAccessible(true);

            //是否被annotationClass 注解过的函数
            isExist = method.isAnnotationPresent(annotationClass);
            if (isExist) {
                break;
            }
        }
        return isExist;
    }

}