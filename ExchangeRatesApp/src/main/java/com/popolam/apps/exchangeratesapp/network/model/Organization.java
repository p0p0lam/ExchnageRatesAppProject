package com.popolam.apps.exchangeratesapp.network.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sergey on 23.02.2016.
 */
public class Organization implements Parcelable {
    public static final int TYPE_FOP =2;
    public static final int TYPE_BANK =1;
    private String id;
    private String phone;
    private String title;
    private String address;
    private String link;
    private String city;
    private String location;
    private int type;
    private double latitude = 0.0D;
    private double longitude = 0.0D;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeString(this.link);
        dest.writeString(this.city);
        dest.writeString(this.location);
        dest.writeInt(this.type);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public Organization() {
    }

    protected Organization(Parcel in) {
        this.id = in.readString();
        this.phone = in.readString();
        this.title = in.readString();
        this.address = in.readString();
        this.link = in.readString();
        this.city = in.readString();
        this.location = in.readString();
        this.type = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Organization> CREATOR = new Parcelable.Creator<Organization>() {
        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }

        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };
}
