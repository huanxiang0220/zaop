package com.mystery.font.aspect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mystery.font.CustomFont;
import com.mystery.font.FontContext;
import com.mystery.font.FontInject;
import com.mystery.font.utils.ViewGroupContextModifier;
import com.mystery.font.utils.FindViewUtils;
import com.mystery.font.cache.FragmentCache;
import com.mystery.font.FragmentCallback;
import com.mystery.font.cache.FragmentRecord;
import com.mystery.zaop.logger.ZLogger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;

/**
 * 拦截Fragment OnCreateView方法，目前替换LayoutInflater
 */
@Aspect
public class FragmentOnCreateViewAspect {

    /**
     * 定义切点，匹配指定包下的 Fragment 及其父类的 onCreateView 方法
     */
    @Pointcut("execution(* com.zhitongcaijin.ztc.fragment..*.onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle))")
    public void onCreateViewPointcut() {
    }

    @Around("onCreateViewPointcut()")
    public View aroundOnCreateView(ProceedingJoinPoint joinPoint) throws Throwable {
        Fragment fragment = (Fragment) joinPoint.getThis();
        LayoutInflater inflater = (LayoutInflater) joinPoint.getArgs()[0];
        ViewGroup container = (ViewGroup) joinPoint.getArgs()[1];
        Object[] args = joinPoint.getArgs();

        ZLogger.e(">>>>>> " + fragment.getClass().getSimpleName());
        // 遍历自身或者父级是否有CustomFont注解
        CustomFont findAnnotation = null;
        Fragment parent = fragment;
        while (parent != null) {
            Class<? extends Fragment> fragmentClass = parent.getClass();
            if (fragmentClass.isAnnotationPresent(CustomFont.class)) {
                CustomFont annotation = fragmentClass.getAnnotation(CustomFont.class);
                if (annotation != null) {
                    findAnnotation = annotation;
                    break;
                }
            }
            parent = parent.getParentFragment();
        }

        if (findAnnotation != null) {
            //自身或者父级找到了标记CustomFont的注解
            int value = findAnnotation.value();
            // 在这里自身添加拦截逻辑
            LayoutInflater newInflater = inflater.cloneInContext(fragment.requireContext());
            //设置自定义的Factory
            FontInject.inject(newInflater, value);
            // 替换参数中的 LayoutInflater
            args[0] = newInflater;

            //遍历父类有没有存在父级@CustomFont
            if (!findParent(fragment)) {
                //添加注册(处理每一个Fragment下的RecyclerView打上标记，表示是打了CustomFont注解下的一部分视图)
                FragmentManager fragmentManager = fragment.getChildFragmentManager();
                fragmentManager.registerFragmentLifecycleCallbacks(new FragmentCallback(value), true);
            }
        }
        // 调用原方法
        return (View) joinPoint.proceed(args);
    }

    /**
     * 寻找父级是否有注解@CustomFont
     */
    private static boolean findParent(Fragment fragment) {
        Fragment parent = fragment.getParentFragment();
        while (parent != null) {
            Class<? extends Fragment> fragmentClass = parent.getClass();
            if (fragmentClass.isAnnotationPresent(CustomFont.class)) {
                return true;
            }
            parent = parent.getParentFragment();
        }
        return false;
    }


    //---------------------------------------------- Fragment onViewCreated ----------------------------

    /**
     * 定义切点，匹配指定包下的 Fragment 及其父类的 onViewCreated 方法
     */
    @Pointcut("execution(* com.zhitongcaijin.ztc.fragment..*.onViewCreated(..))")
    public void onViewCreatedPointcut() {
    }

    @Around("onViewCreatedPointcut()")
    public void aroundViewCreatedPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        Fragment fragment = (Fragment) joinPoint.getThis();
        FragmentRecord record = FragmentCache.getInstance().contains(fragment);
        if (record != null) {
            View view = fragment.getView();
            ViewGroupContextModifier.modifyContext((ViewGroup) view, new FontContext(record.activity, record.font));
            //查找ViewGroup
            List<ViewGroup> recyclerViews = FindViewUtils.findAllViewGroups(view);
            for (ViewGroup rv : recyclerViews) {
//                vg.setTag(ViewCons.TAG_FRAGMENT_FRAGMENT, true);//记录rv与fragment的关联
//                vg.setTag(ViewCons.TAG_FRAGMENT, true);//记录rv所处的最外层的fragment被@CustomFont标记
//                vg.setTag(ViewCons.TAG_FRAGMENT_FONT, font);//记录最外层的fragment的@CustomFont标记携带的字体类型
//                RecyclerContextCache.obtain(f).put(recyclerView, new FontContext(f.getActivity(), font));

                FontContext fontContext = new FontContext(rv.getContext(), record.font);
                ViewGroupContextModifier.modifyContext(rv, fontContext);
            }
        }
        joinPoint.proceed(joinPoint.getArgs());
    }

    //---------------------------------------------- Fragment new XXController ----------------------------
    // 切入点：在 com.zhitongcaijin.ztc 包及其子包下的任意类中，调用 BaseController 子类的构造方法
//    @Pointcut("call(* com.zhitongcaijin.ztc.controller.BaseController+.new(..)) " +
//            "&& within(androidx.fragment.app.Fragment+")
//    public void fragmentCreatesBaseController() {
//    }
//
//    // 在切入点匹配的操作之后执行通知
//    @Before("fragmentCreatesBaseController()")
//    public void afterControllerCreation(ProceedingJoinPoint joinPoint) {
//        System.out.println("在 Fragment 子类中创建了 BaseController 子类对象: " + joinPoint.getSignature());
//    }

}