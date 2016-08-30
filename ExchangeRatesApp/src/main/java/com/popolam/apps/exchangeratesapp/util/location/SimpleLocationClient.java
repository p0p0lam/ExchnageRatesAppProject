package com.popolam.apps.exchangeratesapp.util.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.popolam.apps.exchangeratesapp.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 30.03.2016.
 */
public class SimpleLocationClient implements LocationListener {
    private static final String TAG = SimpleLocationClient.class.getSimpleName();
    private WeakReference<LocationListener> mLocationListenerWeakReference;
    public Location currentLocation;
    private final LocationManager mLocationManager;

    public SimpleLocationClient(Context context, LocationListener listener) {
        mLocationListenerWeakReference = new WeakReference<>(listener);
        mLocationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = getBestProvider();
        Log.d(TAG, "SimpleLocationClient: Using best provider: " + bestProvider);
        if (bestProvider!=null) {
            mLocationManager.requestLocationUpdates(
                    bestProvider,
                    TimeUnit.SECONDS.toMillis(5),
                    500,
                    this); // здесь можно указать другие более подходящие вам параметры

            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentLocation != null) {
                listener.onLocationChanged(currentLocation);
            }
        } else {
            Log.d(TAG, "SimpleLocationClient. No location providers exists. disabling");
        }
    }

    private String getBestProvider(){
        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled =  mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled){
                return LocationManager.GPS_PROVIDER;
            }
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network_enabled){
            return LocationManager.NETWORK_PROVIDER;
        }
        return null;
    }

    public void stop(){
        Log.d(TAG, "stopping location updates");
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ");
        currentLocation = location;
        LocationListener listener = mLocationListenerWeakReference.get();
        if (listener!=null){
            listener.onLocationChanged(currentLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
