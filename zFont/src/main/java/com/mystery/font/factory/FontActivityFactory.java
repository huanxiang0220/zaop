package com.mystery.font.factory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.mystery.font.FontConstant;
import com.mystery.font.FontManager;
import com.mystery.font.FontUtils;

/**
 * 设置了Custom注解的解析器走这里
 */
public class FontActivityFactory extends AbsFactory {

    @FontConstant.FontType
    int font;

    public FontActivityFactory(int font) {
        this.font = font;
    }

    @Override
    public void setTypeface(Context context, TextView tv, boolean hasFamily, boolean hasStyle) {
        if (hasFamily) {//清除xml中同时设置了style的情况
            //如果自定义了字体，textStyle失效并设置为默认，
            // 有些字体单独的设置setFakeBoldText和setStrokeWidth(0)不能解决问题，需设置权重。
            FontUtils.clearStyle(tv);
            if (FontManager.isDebug()) {
                tv.setBackgroundColor(Color.YELLOW);
            }
            return;
        }

        //寻找指定字体
        if (font != 0) {
            Typeface typeface = FontUtils.getTypeface(context, font, hasStyle ? 1 : 0);
            if (typeface != null) {
                //找到自定义的字体
                tv.setTypeface(typeface, Typeface.NORMAL);
                FontUtils.setTextMediumBold(tv, 0);//目前oppo需要去除，后期哟其他字体需要兼容到IFont
                tv.setIncludeFontPadding(FontUtils.isIncludeFontPadding(font));
                if (FontManager.isDebug()) {
                    tv.setBackgroundColor(Color.CYAN);
                }
                return;
            }
        }

        //维持默认系统默认字体
        if (FontManager.isDebug()) {
            tv.setBackgroundColor(Color.RED);
        }
    }

}