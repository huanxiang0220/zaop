package com.mystery.font;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.mystery.font.inter.imp.OppoFont;
import com.mystery.font.inter.IFont;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FontUtils {

    /**
     * 缓存字体
     */
    private static final Map<String, Typeface> typefaceMap = new HashMap<>();

    //字体具体工厂缓存
    private static final Set<IFont> sFonts = new HashSet<>();

    static {
        sFonts.add(new OppoFont());
    }

    /**
     * @param font  FontConstant
     * @param style 等于0则是默认，非0即medium
     * @return 字体资源id
     */
    public static Typeface getTypeface(Context context, @FontConstant.FontType int font, int style) {
        String key = String.format("font:%s-style:%s", font, style);
        Typeface typeface = typefaceMap.get(key);
        if (typeface != null) {
            return typeface;
        }
        for (IFont next : sFonts) {
            if (next.getFontCons() == font) {
                typeface = ResourcesCompat.getFont(context, next.getTypefaceId(style));
                typefaceMap.put(key, typeface);
            }
        }
        //找不到就使用默认的
        return null;
    }

    public static CustomFont getCustomFont(Class<?> clz) {
        while (clz != null) {
            // 判断当前类是否有 CustomFont 注解
            if (clz.isAnnotationPresent(CustomFont.class)) {
                CustomFont annotation = clz.getAnnotation(CustomFont.class);
                if (annotation != null)
                    return annotation;
            }
            clz = clz.getSuperclass();
        }
        return null;
    }

    /**
     * textview 权重字体
     *
     * @param num 它的数值是  0-2（正常情况下） 0是普通字体  2是bold字体
     */
    public static void setTextMediumBold(TextView textView, float num) {
        TextPaint paint = textView.getPaint();
        paint.setStrokeWidth(num);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * CustomFont指定的字体：是否需要IncludeFontPadding
     */
    public static boolean isIncludeFontPadding(@FontConstant.FontType int font) {
        for (IFont next : sFonts) {
            if (next.getFontCons() == font) {
                return next.isIncludeFontPadding();
            }
        }
        return false;
    }

    /**
     * @param tv 将textStyle清除至默认
     */
    public static void clearStyle(TextView tv) {
        Typeface font = tv.getTypeface();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            font = Typeface.create(font, 400, false);
        }
        TextPaint paint = tv.getPaint();
        paint.setFakeBoldText(false);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        tv.setTypeface(font, Typeface.NORMAL);
    }

}