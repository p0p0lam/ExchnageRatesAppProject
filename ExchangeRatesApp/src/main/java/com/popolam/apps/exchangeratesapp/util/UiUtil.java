package com.popolam.apps.exchangeratesapp.util;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

/**
 * Created by user on 31.10.13.
 */
public class UiUtil {

    public static float getAttrValueInPx(Context context, int attr){
        float result =0;
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(
                attr, value, true)){
            result = TypedValue.complexToDimension(value.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static float getHueFromColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }
}
