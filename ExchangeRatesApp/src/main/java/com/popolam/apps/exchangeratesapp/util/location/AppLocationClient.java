package com.popolam.apps.exchangeratesapp.util.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.popolam.apps.exchangeratesapp.BuildConfig;
import com.popolam.apps.exchangeratesapp.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/** Retrieve current location of user;
 * User: Serhiy Plekhov
 * Date: 20.08.13
 * Time: 13:18
 */
public class AppLocationClient implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, android.location.LocationListener {

    private static final String TAG = Log.calculateTag(AppLocationClient.class);
    //Update interval for location updates in seconds
    private static final long UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    private GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;
    private Location mLocation;
    private WeakReference<Context> mContextWeakReference;
    private SimpleLocationClient mSimpleLocationClient;

    public AppLocationClient(Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
        // check for GooglePlayServices
        int resultCode =
                GoogleApiAvailability.getInstance().
                        isGooglePlayServicesAvailable(context);
        listProviders(context);
        if (resultCode == ConnectionResult.SUCCESS){
            Log.d(TAG, "AppLocationClient. Using Google Play Services location client");
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(TimeUnit.SECONDS.toMillis(5));
            mLocationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(1));
            mLocationRequest.setNumUpdates(1);
            mLocationClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            Log.d(TAG, "AppLocationClient. Using simple location client");
            mSimpleLocationClient = new SimpleLocationClient(context, this);
        }

    }

    private static void listProviders(Context context){
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        for (String provider : locationManager.getAllProviders()) {
            Log.d(TAG, "listProviders: " + provider);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged. provider: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled " + provider);
    }

    public Location getLocation(){
        return mLocation;
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Retrieve new location update " + location);
        Context context = mContextWeakReference.get();
        if (context==null){
            return;
        }
        if (location!=null){
            mLocation = location;
            disconnect();
            if (context instanceof LocationListener){
                Log.d(TAG, "Returning location: " + mLocation);
                ((LocationListener)context).OnLocationObtained(mLocation);
            }
        }
    }

    /**
     * Connects Location Client
     * call this in Activity.onStart
     */
    public void connect(){
        if (mLocationClient!=null){
            Log.d(TAG, "LocationClient connecting ...");
            mLocationClient.connect();
        }
    }

    /**
     * Disconnects Location Client
     * call this in Activity.onStop
     */
    public void disconnect(){
        if (mLocationClient!=null){
            Log.d(TAG, "LocationClient disconnecting ...");
            if (mLocationClient.isConnected()){
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mLocationClient, this);
            }
            mLocationClient.disconnect();
        }
        if (mSimpleLocationClient!=null){
            mSimpleLocationClient.stop();
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location client successfully connected. Try to retrieve location.");
        requestLocationUpdate();
    }

    public void requestLocationUpdate(){
        if (mLocationClient.isConnected()){
            /*
            final Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mLocationClient);
            if (location!=null && System.currentTimeMillis() - location.getTime() < UPDATE_INTERVAL)
            {
                mLocation = location;
                disconnect();
                    Log.d(TAG, "Returning: " + mLocation);
                    getListener().OnLocationObtained(mLocation);
                return;
            }
            */
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mLocationClient, mLocationRequest, this);
        }
    }

    private LocationListener getListener(){
        Context context = mContextWeakReference.get();
        if (context !=null && context instanceof LocationListener){
            return (LocationListener) context;
        }
        return mFakeLocationListener;
    }

    private LocationListener mFakeLocationListener = new LocationListener() {
        @Override
        public void OnLocationObtained(Location location) {

        }
    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        getListener().OnLocationObtained(null);
        Log.d(TAG, "Location client can't connect. Disable location updates...");
    }

    public interface LocationListener{
        void OnLocationObtained(Location location);
    }

}
