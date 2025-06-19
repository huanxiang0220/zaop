package com.mystery.font.cache;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mystery.font.FontContext;
import com.mystery.font.constant.ViewCons;

import java.util.HashMap;
import java.util.Map;

public class RecyclerContextCache {

    /**
     * 用来记录Fragment，对应一个IFragment，IFragment对应多个RV
     */
    private static HashMap<Fragment, IFragment> mCache = new HashMap<>();

    public static IFragment obtain(Fragment f) {
        if (mCache.containsKey(f)) {
            return mCache.get(f);
        }
        IFragment iFragment;
        mCache.put(f, iFragment = new IFragment());
        return iFragment;
    }

    public static FontContext getContext(RecyclerView recyclerView) {
        Fragment fragment = (Fragment) recyclerView.getTag(ViewCons.TAG_FRAGMENT_FRAGMENT);
        if (!mCache.containsKey(fragment)) {
            IFragment iFragment = new IFragment();
            int font = (int) recyclerView.getTag(ViewCons.TAG_FRAGMENT_FONT);
            FontContext temp;
            iFragment.put(recyclerView, temp = new FontContext(recyclerView.getContext(), font));
            mCache.put(fragment, iFragment);
            return temp;
        }

        for (IFragment value : mCache.values()) {
            for (Map.Entry<RecyclerView, FontContext> entry : value.mCache.entrySet()) {
                if (recyclerView == entry.getKey()) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 移除缓存的Fragment一并移除缓存的RecyclerView对应的RecyclerContext
     */
    public static void remove(Fragment f) {
        IFragment remove = mCache.remove(f);
        if (remove != null) {
            remove.mCache.clear();
        }
    }

    static class IFragment {
        /**
         * 一个Fragment下一个RecyclerView对应一个RecyclerContext
         */
        private final HashMap<RecyclerView, FontContext> mCache = new HashMap<>();

        public void put(RecyclerView recyclerView, FontContext context) {
            mCache.put(recyclerView, context);
        }

        public FontContext get(RecyclerView recyclerView) {
            return mCache.get(recyclerView);
        }
    }

}