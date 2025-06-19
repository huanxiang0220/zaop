package com.mystery.font.aspect;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.mystery.font.constant.ViewCons;
import com.mystery.zaop.logger.ZLogger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 拦截RecyclerView.Adapter onCreateViewHolder
 */
@Aspect
public class RecyclerViewAdapterAspect {

//    // 定义切点，匹配指定包及子包任意一个类的 onCreateViewHolder 方法
//    @Pointcut("execution(com.chad.library.adapter.base.BaseViewHolder com.chad.library.adapter.base.BaseQuickAdapter.onCreateViewHolder(android.view.ViewGroup, int))")
//    public void onCreateViewHolderPointcut() {
//
//    }

    // 定义切点，匹配父类是 BaseQuickAdapter 的 Adapter 类中 BaseQuickAdapter 的 onCreateViewHolder 方法
    @Pointcut("execution(* *.onCreateViewHolder(android.view.ViewGroup, int)) && this(com.chad.library.adapter.base.BaseQuickAdapter+)")
    public void onCreateViewHolderPointcut() {
    }


    // 前置通知，在 onCreateViewHolder 方法执行前执行
    @Before("onCreateViewHolderPointcut()")
    public void beforeOnCreateViewHolder(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) args[0];
            Object tag = recyclerView.getTag(ViewCons.TAG_FRAGMENT);
            if (tag != null && (boolean) tag) {
                //获取parent的Context
                Context context = recyclerView.getContext();
                ZLogger.e(context.getClass().getSimpleName() + " >>> " + context.getClass().getSimpleName());
//                @FontConstant.FontType
//                int font = (int) recyclerView.getTag(ViewCons.TAG_FRAGMENT_FONT);
//                RecyclerContext newContext = RecyclerContextCache.getContext(recyclerView);
//                RecyclerViewContextModifier.modifyRecyclerViewContext(recyclerView, newContext);

//                LayoutInflater newInflater = LayoutInflater.from(context).cloneInContext(context);
//                @FontConstant.FontType
//                int font = (int) recyclerView.getTag(ViewCons.TAG_FRAGMENT_FONT);
//                FontInject.inject(newInflater, font);
            }
        }
    }

//    // 环绕通知，拦截 onCreateViewHolder 方法
//    @Around("onCreateViewHolderPointcut()")
//    public Object aroundOnCreateViewHolder(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object[] args = joinPoint.getArgs();
//        if (args.length > 0 && args[0] instanceof RecyclerView) {
//            RecyclerView recyclerView = (RecyclerView) args[0];
//            if (recyclerView.getTag(ViewCons.TAG_FRAGMENT) != null && (boolean) recyclerView.getTag(ViewCons.TAG_FRAGMENT)) {
//                //获取parent的Context
//                Context context = recyclerView.getContext();
//                LayoutInflater newInflater = LayoutInflater.from(context).cloneInContext(context);
//                @FontConstant.FontType
//                int font = (int) recyclerView.getTag(ViewCons.TAG_FRAGMENT_FONT);
//                FontInject.inject(newInflater, font);
//            }
//        }
//        return joinPoint.proceed();
//    }

}