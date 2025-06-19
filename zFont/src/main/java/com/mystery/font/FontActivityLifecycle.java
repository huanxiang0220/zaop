package com.mystery.font;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mystery.font.factory.AbsFactory;
import com.mystery.font.factory.FontActivityFactory;
import com.mystery.font.factory.FontDefaultFactory;

public class FontActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Class<?> clazz = activity.getClass();
        CustomFont customFont = FontUtils.getCustomFont(clazz);
        LayoutInflater inflater = activity.getLayoutInflater();
        if (customFont != null) {
            AbsFactory.inject(inflater, new FontActivityFactory(customFont.value()));
        } else {
            AbsFactory.inject(inflater, new FontDefaultFactory());
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
