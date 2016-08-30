package com.popolam.apps.exchangeratesapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by serhiy.plekhov on 28.11.13.
 */
public enum Settings {
    INSTANCE;
    public final static long DICT_EXPIRE= TimeUnit.HOURS.toMillis(2);

    public static final String PREFS_NAME = "exrate_prefs";
    public static final String KEY_LAST_DICT_CHECK="last_dict_check_time";
    public static final String KEY_LOCATION="location";
    public static final String KEY_LOCATION_CITY_CODE ="manual_location";
    public static final String KEY_LOCATION_CITY_NAME ="city_name";
    public static final String KEY_RADIUS="radius";
    public static final String KEY_LIST_COUNT="list_count";
    public static final String KEY_SELECTED_CURRENCY ="selected_currency";
    public static final String KEY_LAST_LOCALE ="last_locale";
    public static final String KEY_IS_GOOGLE_PLAY_SERVICES_AVAILABLE ="is_gps_available";

    private final SharedPreferences prefs;

    private long lastDictCheck;
    private boolean isAutoLocation;
    private String manualLocation;
    private String cityName;
    private int radius;
    private int listCount;
    private String selectedCurrency;
    private String lastLocale;
    private boolean isGooglePlayServicesAvailable;

    private Settings(){
        prefs = App.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromPrefs();
    }

    public void loadFromPrefs(){
        lastDictCheck = prefs.getLong(KEY_LAST_DICT_CHECK, 0);
        isAutoLocation = prefs.getBoolean(KEY_LOCATION, true);
        manualLocation = prefs.getString(KEY_LOCATION_CITY_CODE, null);
        cityName = prefs.getString(KEY_LOCATION_CITY_NAME, null);
        radius = prefs.getInt(KEY_RADIUS, 5);
        listCount = prefs.getInt(KEY_LIST_COUNT, 15);
        selectedCurrency = prefs.getString(KEY_SELECTED_CURRENCY, null);
        lastLocale = prefs.getString(KEY_LAST_LOCALE, Locale.getDefault().getLanguage());
        isGooglePlayServicesAvailable = prefs.getBoolean(KEY_IS_GOOGLE_PLAY_SERVICES_AVAILABLE, false);
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
        prefs.edit().putString(KEY_SELECTED_CURRENCY, selectedCurrency).apply();
    }

    public long getLastDictCheck() {
        return lastDictCheck;
    }

    public void setLastDictCheck(long lastDictCheck) {
        this.lastDictCheck = lastDictCheck;
        prefs.edit().putLong(KEY_LAST_DICT_CHECK, lastDictCheck).apply();
    }

    public boolean isDictExpired(){
        if (lastDictCheck==0){
            return true;
        }
        return System.currentTimeMillis()-lastDictCheck>DICT_EXPIRE;
    }

    public boolean isAutoLocation() {
        return isAutoLocation;
    }

    public void setAutoLocation(boolean isAutoLocation) {
        this.isAutoLocation = isAutoLocation;
        prefs.edit().putBoolean(KEY_LOCATION, isAutoLocation).apply();
    }

    public String getManualLocation() {
        return manualLocation;
    }

    public void setManualLocation(String manualLocation) {
        this.manualLocation = manualLocation;
        prefs.edit().putString(KEY_LOCATION_CITY_CODE, manualLocation).apply();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        prefs.edit().putString(KEY_LOCATION_CITY_NAME, cityName).apply();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        prefs.edit().putInt(KEY_RADIUS, radius).apply();
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
        prefs.edit().putInt(KEY_LIST_COUNT, listCount).apply();
    }

    public String getLastLocale() {
        return lastLocale;
    }

    public void setLastLocale(String lastLocale) {
        this.lastLocale = lastLocale;
        prefs.edit().putString(KEY_LAST_LOCALE, lastLocale).apply();
    }

    public boolean isGooglePlayServicesAvailable() {
        return isGooglePlayServicesAvailable;
    }

    public void setGooglePlayServicesAvailable(boolean googlePlayServicesAvailable) {
        isGooglePlayServicesAvailable = googlePlayServicesAvailable;
        prefs.edit().putBoolean(KEY_IS_GOOGLE_PLAY_SERVICES_AVAILABLE, isGooglePlayServicesAvailable).apply();
    }
}
