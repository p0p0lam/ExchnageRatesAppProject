package com.popolam.apps.exchangeratesapp.util;

import android.database.Cursor;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 22.04.2016.
 */
public class IOUtil {

    public static void safelyClose(Cursor c) {
        if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {

                }
        }
    }
}
