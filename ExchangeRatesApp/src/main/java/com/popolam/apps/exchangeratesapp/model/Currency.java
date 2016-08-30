package com.popolam.apps.exchangeratesapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * Model for Currency object
 */


public class Currency implements BaseColumns, Parcelable {
    public static final String TABLE_NAME="currency";
    public static final String COL_CODE="code";
    public static final String COL_NAME="name";

    public long id;

    public String code;


    public String name;

    public Currency(){

    }

    public Currency(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
    public Currency(String code, String name) {
        this(0, code, name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;

        if (!code.equals(currency.code)) return false;

        return true;
    }



    @Override
    public int hashCode() {
        return code.hashCode();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(this.code);
        dest.writeString(this.name);
    }

    private Currency(Parcel in) {
        this.id = in.readLong();
        this.code = in.readString();
        this.name = in.readString();
    }

    public transient  static Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };




}
