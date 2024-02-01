package com.github.tvbox.osc.util;

import android.graphics.Color;

public class ColorUtil {

    private static final int MAX_TRANSPARENT = 10;

    public static int getSubtitleColor() {
        int textTransparent = SubtitleHelper.getTextTransparent();
        int alpha = 0xFF - (int) (0xFF * (textTransparent * 1.0 / MAX_TRANSPARENT));
        int argb = Color.argb(alpha, 0xFF, 0xFF, 0xFF);
        return argb;
    }
}
