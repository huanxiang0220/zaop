package com.mystery.font;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 定义那些字体类型
 * 设置的值必须大于0，且唯一，0已被默认字体
 */
public class FontConstant {

    public static final int OppoFont = 0x0001;
    //新增字体在此注册标识值，然后实现IFont,最后注册到FontUtils.sFonts中即可，

    @IntDef({OppoFont})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FontType {

    }

    private @FontType int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}