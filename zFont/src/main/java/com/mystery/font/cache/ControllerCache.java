package com.mystery.font.cache;

import androidx.fragment.app.Fragment;

import org.aspectj.lang.JoinPoint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ControllerCache {

    private static final ControllerCache sInstance = new ControllerCache();

    private ControllerCache() {

    }

    public static ControllerCache getInstance() {
        return sInstance;
    }

    private final Set<JoinPoint> mCache = new HashSet<>();

    public void put(JoinPoint record) {
        mCache.add(record);
    }

    public void remove(Fragment f) {
        Iterator<JoinPoint> iterator = mCache.iterator();
        while (iterator.hasNext()) {
            JoinPoint next = iterator.next();
            if (f == next.getThis()) {
                iterator.remove();
                break;
            }
        }
    }

    public JoinPoint contains(Object f) {
        for (JoinPoint next : mCache) {
            if (f == next.getTarget()) {
                return next;
            }
        }
        return null;
    }
}
