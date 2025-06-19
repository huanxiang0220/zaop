package com.mystery.font.factory;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.mystery.font.FontUtils;
import com.mystery.font.R;

/**
 * 设置了Custom注解的解析器走这里
 */
public class FontDefaultFactory extends AbsFactory {

    @Override
    public void setTypeface(Context context, TextView tv, boolean hasFamily, boolean hasTextStyle) {
        //设置字体
        if (hasFamily) {//清除xml中同时设置了style的情况
            //如果自定义了字体，textStyle失效并设置为默认，
            // 有些字体单独的设置setFakeBoldText和setStrokeWidth(0)不能解决问题，需设置权重。
            FontUtils.clearStyle(tv);
            return;
        }

        //维持原有默认的字体
        if (hasTextStyle) {
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.din_medium), Typeface.NORMAL);
            FontUtils.setTextMediumBold(tv, 1);
        } else {
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.din_medium));
        }
    }

}