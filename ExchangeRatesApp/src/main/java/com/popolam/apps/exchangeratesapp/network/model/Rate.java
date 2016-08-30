package com.popolam.apps.exchangeratesapp.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;

import java.util.Date;

/**
 * Created by Sergey on 23.02.2016.
 */
public class Rate implements ClusterItem, Parcelable {
    private String currencyCode;
    private double bid;
    private double ask;
    private Organization organization;
    private Date updatedAt;
    private Distance distance;

    public Rate() {
    }


    public Rate(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    @Override
    public LatLng getPosition() {
        if (organization!=null){
            return new LatLng(organization.getLatitude(), organization.getLongitude());
        }
        return null;
    }

    public double getRateDiffByType(double bestValue, double worthValue, RateListType type) {
        double window = Math.abs(bestValue - worthValue);
        double diff = Math.abs(getRateByType(type)-bestValue);
        return diff/window;
    }

    public double getRateByType(RateListType rateType) {
        switch (rateType) {
            case BID:
                return getAsk();
            case ASK:
                return getBid();
        }
        return 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.currencyCode);
        dest.writeDouble(this.bid);
        dest.writeDouble(this.ask);
        dest.writeParcelable(this.organization, flags);
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
        dest.writeParcelable(this.distance, flags);
    }

    protected Rate(Parcel in) {
        this.currencyCode = in.readString();
        this.bid = in.readDouble();
        this.ask = in.readDouble();
        this.organization = in.readParcelable(Organization.class.getClassLoader());
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.distance = in.readParcelable(Distance.class.getClassLoader());
    }

    public static final Parcelable.Creator<Rate> CREATOR = new Parcelable.Creator<Rate>() {
        @Override
        public Rate createFromParcel(Parcel source) {
            return new Rate(source);
        }

        @Override
        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };
}
