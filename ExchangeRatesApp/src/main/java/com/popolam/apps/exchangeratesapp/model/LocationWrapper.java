package com.popolam.apps.exchangeratesapp.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.popolam.apps.exchangeratesapp.util.location.LocationUtils;

/**
 * Created by user on 08.11.13.
 */
public class LocationWrapper implements android.os.Parcelable {
    public String cityName;
    public String cityCode;
    private LatLng mLatLng;
    private Location mLocation;
    public LocationWrapper(Location l) {
        this.mLocation= l;
        if (l!=null){
            mLatLng = new LatLng(l.getLatitude(), l.getLongitude());
        } else {
            mLatLng = new LatLng(0, 0);
        }
        cityCode="";
        cityName="";
    }

    public Location getLocation() {
        return mLocation;
    }

    public LocationWrapper(String cityName, String cityCode){
        mLatLng = new LatLng(0, 0);
        this.cityName= cityName;
        this.cityCode = cityCode;
    }

    public boolean isAutoLocation(){
        return mLocation!=null;
    }

    public double getLatitude(){
        return mLocation==null?0:mLocation.getLatitude();
    }

    public double getLongitude(){
        return mLocation==null?0:mLocation.getLongitude();
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cityName);
        dest.writeString(this.cityCode);
        //dest.writeParcelable(this.mLatLng, flags);
        dest.writeDouble(this.mLatLng.latitude);
        dest.writeDouble(this.mLatLng.longitude);
        if (mLocation!=null){
            dest.writeInt(1);
            mLocation.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
    }

    private LocationWrapper(Parcel in){
        this.cityName = in.readString();
        this.cityCode = in.readString();
        //this.mLatLng = LatLng.CREATOR.createFromParcel(in);
        double latitude = in.readDouble();
        double longitude = in.readDouble();
        this.mLatLng = new LatLng(latitude, longitude);
        int isLocationAvailable = in.readInt();
        if (isLocationAvailable==1){
            this.mLocation = Location.CREATOR.createFromParcel(in);
        }
    }

    public static final Parcelable.Creator<LocationWrapper> CREATOR =
            new Parcelable.Creator<LocationWrapper>() {
                @Override
                public LocationWrapper createFromParcel(Parcel in) {
                    return new LocationWrapper(in);
                }

                @Override
                public LocationWrapper[] newArray(int size) {
                    return new LocationWrapper[size];
                }
            };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        LocationWrapper that = (LocationWrapper) o;

        if (cityCode != null ? !cityCode.equals(that.cityCode) : that.cityCode != null)
            return false;
        if (mLocation!=null && that.mLocation!=null && LocationUtils.distanceTo(mLocation, that.mLocation)>=100){
            return false;
        }
        if (mLocation == null || that.mLocation==null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cityCode != null ? cityCode.hashCode() : 0;
        result = 31 * result + (mLocation != null ? mLocation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocationWrapper{" +
                "cityName='" + cityName + '\'' +
                ", mLocation=" + mLocation +
                '}';
    }



}
