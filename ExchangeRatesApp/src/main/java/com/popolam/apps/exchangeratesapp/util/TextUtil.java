package com.popolam.apps.exchangeratesapp.util;

import android.content.Context;
import android.text.TextUtils;

import com.popolam.apps.exchangeratesapp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 22.10.13.
 */
public class TextUtil {

    public static double parseParseDouble(String src) {
        double result = 0;
        if (!TextUtils.isEmpty(src)) {
            DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
            format.applyPattern("@@##");
            try {
                result = format.parse(src).doubleValue();
            } catch (ParseException e) {

            }
        }
        return result;
    }
    public static String reformatDouble(String src) {
        String result = "";
        if (!TextUtils.isEmpty(src)) {
            DecimalFormat formatParse = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
            formatParse.applyPattern("@@##");
            DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
            format.applyPattern("0.00##");
            try {
                double val = formatParse.parse(src).doubleValue();
                result = format.format(val);
            } catch (ParseException e) {

            }
        }
        return result;
    }
    public static String formatDouble(Double src) {
        String result = "";
        DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
        format.applyPattern("0.00##");
            try {
                result = format.format(src);
            } catch (IllegalArgumentException e) {

            }

        return result;
    }
    public static String formatDistance(Context context, double val) {
        String result = "";
        boolean isKm;
            DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
            if (val<1000){
                isKm = false;
                format.applyPattern("0");
            } else {
                isKm = true;
                val = val/new Double(1000);
                format.applyPattern("0.0#");
            }
            try {
                result = format.format(val);
            } catch (IllegalArgumentException e) {

            }


        return isKm?context.getString(R.string.rate_distance_km, result): context.getString(R.string.rate_distance_m, result);
    }

    public static String formatDate(Date src){
        if (src!=null)  {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            return sdf.format(src);
        }
        return "";
    }

}
