package com.popolam.apps.exchangeratesapp.network.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 31.03.2016.
 */
public class Distance implements Parcelable {
    double value;
    String metric;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.value);
        dest.writeString(this.metric);
    }

    public Distance() {
    }

    protected Distance(Parcel in) {
        this.value = in.readDouble();
        this.metric = in.readString();
    }

    public static final Parcelable.Creator<Distance> CREATOR = new Parcelable.Creator<Distance>() {
        @Override
        public Distance createFromParcel(Parcel source) {
            return new Distance(source);
        }

        @Override
        public Distance[] newArray(int size) {
            return new Distance[size];
        }
    };
}
