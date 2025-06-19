package com.mystery.font;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.view.ContextThemeWrapper;

public class FontContext extends ContextThemeWrapper {

    private LayoutInflater mInflater;
    @FontConstant.FontType
    private final int font;

    public FontContext(Context context, @FontConstant.FontType int font) {
        super(context, context.getTheme());
        this.font = font;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
                if (mInflater != null) {
                    FontInject.inject(mInflater, font);
                }
            }
            return mInflater;
        }
        return getBaseContext().getSystemService(name);
    }

}