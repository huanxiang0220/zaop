package com.mystery.zaop.aspect;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.mystery.zaop.ZAOP;
import com.mystery.zaop.annation.Permission;
import com.mystery.zaop.annation.PermissionBefore;
import com.mystery.zaop.annation.PermissionCancel;
import com.mystery.zaop.annation.PermissionSetting;
import com.mystery.zaop.logger.ZLogger;
import com.mystery.zaop.permission.IPermission;
import com.mystery.zaop.permission.IPermissionGranted;
import com.mystery.zaop.permission.IRationale;
import com.mystery.zaop.permission.OnPermissionListener;
import com.mystery.zaop.permission.PermissionActivity;
import com.mystery.zaop.permission.PermissionSettingActivity;
import com.mystery.zaop.permission.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
public class PermissionAspect {

    @Pointcut("within(@com.mystery.zaop.annation.Permission *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.mystery.zaop.annation.Permission * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }  //方法切入点

    @Around("method() && @annotation(permission)")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        ZLogger.e("注入成功");

        // 获取目标对象
        Object target = joinPoint.getTarget();
        // 获取目标对象的类
        Class<?> targetClass = target.getClass();

        //<editor-fold desc="上下文的处理">
        //从参数中取得Activity Context
        Context aContext = null;
        if (target instanceof Activity) {
            aContext = (Context) target;
        }
        //尝试从参数上获取
        if (aContext == null) {
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof Activity) {
                    aContext = (Context) arg;
                    break;
                }
            }
        }
        //尝试从成员变量中获取
        if (aContext == null) {
            Class<?> clazz = targetClass;
            out:
            while (clazz != null) {
                // 获取所有成员变量
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true); // 设置可访问私有变量
                    try {
                        // 获取成员变量的值
                        Object value = field.get(target);
                        if (value instanceof Activity) {
                            aContext = (Context) value;
                            break out;
                        }
                    } catch (IllegalAccessException e) {
                        ZLogger.e(e.getMessage());
                    }
                }
                //往父类上查找
                clazz = clazz.getSuperclass();
            }
        }
        if (aContext == null) {
            ZLogger.e("从当前需要注入的方法中和成员变量中并未找到当前界面的上下文Activity Context");
            return;
        }
        //</editor-fold>

        final Context fContext = aContext;

        boolean permissionResult = PermissionUtils.hasPermissionRequest(fContext, permission.value());
        if (permissionResult) {
            //告诉外界，已经授权
            joinPoint.proceed();
            return;
        }

        OnPermissionListener onPermissionListener = ZAOP.getOnPermissionListener();
        String title = onPermissionListener.getPromptTitle(aContext, permission.value());
        String desc = ResUtils.getResourceValue(aContext, permission.prompt());

        //<editor-fold desc="PermissionBefore处理">
        if (PermissionUtils.isExistAnnotation(target, PermissionBefore.class)) {
            //@PermissionBefore
            //public void method(int requestCode,IRationale rationale)
            PermissionUtils.invokePermissionBeforeAnnotation(target, permission.requestCode(), new IRationale() {
                @Override
                public void resume() {
                    try {
                        requestPermission(joinPoint, permission, title, desc, fContext);
                    } catch (Throwable e) {
                        ZLogger.e(e.getMessage());
                    }
                }
            });
            return;
        }
        //</editor-fold>

//
//        //<editor-fold desc="华为手机申请前提示">
//        if (isHuawei()) {//没有被PermissionBefore的方法并且是华为手机
//            @StringRes int prompt = permission.prompt();
//            OnPermissionListener onPermissionListener = ZAOP.getOnPermissionListener();
//
//            onPermissionListener.startPrompt(aContext, aContext.getString(prompt), new IRationale() {
//                @Override
//                public void resume() {
//                    try {
//                        requestPermission(joinPoint, permission, fContext);
//                    } catch (Throwable e) {
//                        ZLogger.e(e.getMessage());
//                    }
//                }
//            });
//            return;
//        }
//        //</editor-fold>

        //申请权限
        requestPermission(joinPoint, permission, title, desc, aContext);
    }

    void requestPermission(final ProceedingJoinPoint joinPoint, Permission permission, String title, String desc, Context context) throws Throwable {
        Object thisObj = joinPoint.getThis();
        int requestCode = permission.requestCode();
        boolean goBackContinue = permission.goBackContinue();

        PermissionActivity.requestPermissionAction(context, permission.value(), requestCode, title, desc, new IPermission() {
            @Override
            public void granted() {
                try {
                    joinPoint.proceed();//权限已授权，继续往下执行
                } catch (Throwable e) {
                    ZLogger.e(e.getMessage());
                }
            }

            @Override
            public void cancel() {
                //执行带有PermissionCancel注解的方法，使得可以做一些提醒等操作
                PermissionUtils.invokeAnnotation(joinPoint.getThis(), PermissionCancel.class, requestCode);
            }

            @Override
            public void denied() {
                //<editor-fold desc="是否存在带有PermissionSetting的方法">
                Class<?> objectClass = thisObj.getClass();
                Method[] methods = objectClass.getDeclaredMethods();
                boolean isExist = false;
                for (Method method : methods) {
                    method.setAccessible(true);

                    //是否被annotationClass 注解过的函数
                    PermissionSetting annotation = method.getAnnotation(PermissionSetting.class);
                    if (annotation != null && annotation.requestCode() == requestCode) {
                        //请求码标识那个请求
                        isExist = true;
                        break;
                    }
                }

                //</editor-fold>
                if (isExist) {
                    PermissionUtils.invokeAnnotation(joinPoint.getThis(), PermissionSetting.class, requestCode);
                } else {
                    ZAOP.getOnPermissionListener().onDenied(context, title, desc, () -> {
                        //跳转到设置界面
                        if (goBackContinue) {
                            goBackContinueActivity(context, joinPoint, permission);//内部实现onActivityResult
                        } else {
                            PermissionUtils.startAndroidSetting((Context) joinPoint.getThis());
                        }
                    });
                }
            }
        });
    }

    /**
     * 开启跳转设置界面UI,处理回来之后继续进行
     */
    private void goBackContinueActivity(Context context, ProceedingJoinPoint joinPoint, Permission permission) {
        PermissionSettingActivity.requestPermissionAction(context, permission.value(), new IPermissionGranted() {
            @Override
            public void granted() {
                try {
                    joinPoint.proceed();//权限已授权，继续往下执行
                } catch (Throwable e) {
                    ZLogger.e(e.getMessage());
                }
            }
        });
    }

    static boolean isHuawei() {
        String brand = Build.BRAND;
        return !TextUtils.isEmpty(brand) && brand.equalsIgnoreCase("huawei");
    }

    static class ResUtils {
        // 运行时获取资源 ID
        static int getResourceId(Context context, String resName) {
            return context.getResources().getIdentifier(resName, "string", context.getPackageName());
        }

        static String getResourceValue(Context context, String resName) {
            int resourceId = ResUtils.getResourceId(context, resName);
            String desc;
            try {
                if (resourceId > 0) {
                    desc = context.getString(resourceId);
                } else {
                    desc = "Please configure the corresponding text in string.xml";
                }
            } catch (Exception e) {
                desc = "Please configure the corresponding text in string.xml";
            }
            return desc;
        }
    }

}