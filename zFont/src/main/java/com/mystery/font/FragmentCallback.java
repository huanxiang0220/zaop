package com.mystery.font;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mystery.font.cache.FragmentCache;
import com.mystery.font.cache.FragmentRecord;
import com.mystery.zaop.logger.ZLogger;

/**
 * 动态监听Fragment被加入到FragmentManager中
 */
public class FragmentCallback extends FragmentManager.FragmentLifecycleCallbacks {

    @FontConstant.FontType
    int font;//字体

    public FragmentCallback(@FontConstant.FontType int value) {
        this.font = value;
    }

    @Override
    public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
        super.onFragmentAttached(fm, f, context);
        FragmentCache.getInstance().put(new FragmentRecord(f.getActivity(), font, f));
    }

    @Override
    public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDestroyed(fm, f);
        FragmentCache.getInstance().remove(f);
    }

    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        ZLogger.eTag("FragmentCallback", "onFragmentViewCreated: " + f.getClass().getSimpleName());

        if ("InfTabZhuTiFragment".equals(f.getClass().getSimpleName())) {
            ZLogger.eTag("FragmentCallback", "onFragmentViewCreated: " + f.getClass().getSimpleName());
        }

//        ViewGroupContextModifier.modifyContext((ViewGroup) v, new FontContext(f.getActivity(), font));
//
//        List<RecyclerView> recyclerViews = FindViewUtils.findAllRecyclerViews(v);
//        //RecyclerView打标记（表示是打了CustomFont注解下的一部分视图）
//        for (RecyclerView recyclerView : recyclerViews) {
//            recyclerView.setTag(ViewCons.TAG_FRAGMENT_FRAGMENT, true);//记录rv与fragment的关联
//            recyclerView.setTag(ViewCons.TAG_FRAGMENT, true);//记录rv所处的最外层的fragment被@CustomFont标记
//            recyclerView.setTag(ViewCons.TAG_FRAGMENT_FONT, font);//记录最外层的fragment的@CustomFont标记携带的字体类型
//            RecyclerContextCache.obtain(f).put(recyclerView, new FontContext(f.getActivity(), font));
//
//            FontContext fontContext = new FontContext(recyclerView.getContext(), font);
//            ViewGroupContextModifier.modifyContext(recyclerView, fontContext);
//        }
    }

}