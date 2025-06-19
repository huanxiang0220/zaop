package com.mystery.font.inter;

import androidx.annotation.FontRes;

import com.mystery.font.FontConstant;

public interface IFont {

    @FontConstant.FontType
    int getFontCons();

    /**
     * 等于0则是默认，非0即medium
     */
    @FontRes
    int getTypefaceId(int style);

    /**
     * 是否需要包含IncludeFontPadding
     */
    boolean isIncludeFontPadding();
}