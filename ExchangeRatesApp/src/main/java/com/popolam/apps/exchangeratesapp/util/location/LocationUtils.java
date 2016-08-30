package com.popolam.apps.exchangeratesapp.util.location;

import android.app.Dialog;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.popolam.apps.exchangeratesapp.ui.widget.ErrorDialogFragment;
import com.popolam.apps.exchangeratesapp.util.Log;

/**
 * Created by user on 16.10.13.
 */
public class LocationUtils {
    private static final String TAG = Log.calculateTag(LocationUtils.class);
    public static final int GPS_CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public  static <U extends FragmentActivity> boolean servicesConnected (U context) {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(TAG,
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error code
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    context,
                    GPS_CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(context.getSupportFragmentManager(),
                        "Location Updates");
            }
            return false;
        }
    }

    /**
     * calculate distance between to locations
     * @param location1
     * @param location2
     * @return distance in meters or max float value if one of locations is null
     */
    public static float distanceTo(Location location1, Location location2){
        if (location1!=null && location2!=null){
            return Math.abs(location1.distanceTo(location2));
        } else {
            return Float.MAX_VALUE; // invalid location
        }
    }

    public static LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
}
