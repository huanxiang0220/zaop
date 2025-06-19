package com.mystery.font.inter.imp;

import com.mystery.font.FontConstant;
import com.mystery.font.inter.IFont;
import com.mystery.font.R;

public class OppoFont implements IFont {

    @Override
    public int getFontCons() {
        return FontConstant.OppoFont;
    }

    @Override
    public int getTypefaceId(int style) {
        return style != 0 ? R.font.oplussans3_medium : R.font.oplussans3_regular;
    }

    /**
     * oplussans3字体需要包含IncludeFontPadding
     */
    @Override
    public boolean isIncludeFontPadding() {
        return false;
    }

}