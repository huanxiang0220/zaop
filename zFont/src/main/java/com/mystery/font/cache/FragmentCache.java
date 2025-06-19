package com.mystery.font.cache;

import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FragmentCache {

    private static final FragmentCache sInstance = new FragmentCache();

    private FragmentCache() {

    }

    public static FragmentCache getInstance() {
        return sInstance;
    }

    private final Set<FragmentRecord> mCache = new HashSet<>();

    public void put(FragmentRecord record) {
        mCache.add(record);
    }

    public void remove(Fragment f) {
        Iterator<FragmentRecord> iterator = mCache.iterator();
        while (iterator.hasNext()) {
            FragmentRecord next = iterator.next();
            if (next.fragment == f) {
                iterator.remove();
                break;
            }
        }
    }

    public FragmentRecord contains(Fragment f) {
        for (FragmentRecord next : mCache) {
            if (next.fragment == f) {
                return next;
            }
        }
        return null;
    }

}