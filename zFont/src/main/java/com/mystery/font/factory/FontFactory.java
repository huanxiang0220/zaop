package com.mystery.font.factory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.mystery.font.FontConstant;
import com.mystery.font.FontContext;
import com.mystery.font.FontManager;
import com.mystery.font.FontUtils;
import com.mystery.font.R;
import com.mystery.font.utils.ViewGroupContextModifier;

/**
 * 被@CustomFont的Fragment类（在Fragment onCreate执行的时候替换LayoutInflater）
 */
public class FontFactory extends AbsFactory {

    @FontConstant.FontType
    int font;

    public FontFactory(int font) {
        this.font = font;
    }

    @Override
    public void setViewGroup(Context context, ViewGroup vg) {
        if (!(vg.getContext() instanceof FontContext)) {
            FontContext newContext = new FontContext(context, font);
            ViewGroupContextModifier.modifyContext(vg, newContext);
        }
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

        //寻找指定字体：当前Fragment界面被CustomFont标记
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

        if (FontManager.isDebug()) {
            Log.e("FontFactory", ">>>>>>>>>>>>>>>>>>>>>>>222");
            tv.setBackgroundColor(Color.RED);
        }

        //维持原有默认的字体
        if (hasStyle) {
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.din_medium), Typeface.NORMAL);
            FontUtils.setTextMediumBold(tv, 1);
        } else {
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.din_medium));
        }
    }

}