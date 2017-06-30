package com.popolam.apps.exchangeratesapp.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class DateUtil {
    public static final DateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final DateFormat sdfShortISO = new SimpleDateFormat("yyyy-MM-dd");
    private static final TimeZone tzUTC = TimeZone.getTimeZone("UTC");
    public static Date parseIsoDate(String dateStr){
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                sdfISO.setTimeZone(tzUTC);
                return sdfISO.parse(dateStr);
            } catch (ParseException e) {

            }
        }
        return null;
    }
}
