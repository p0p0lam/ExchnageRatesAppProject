package com.popolam.apps.exchangeratesapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class NetworkUtil {
    public static final boolean hasDataConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null) return false;
        return activeNetwork.isConnected();
    }
}
