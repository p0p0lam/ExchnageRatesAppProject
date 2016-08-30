package com.popolam.apps.exchangeratesapp.model;

import android.provider.BaseColumns;

/**
 * Created by user on 07.11.13.
 */


public class City implements BaseColumns {
    public static final String TABLE_NAME="city";
    public static final String COL_CODE="code";
    public static final String COL_NAME="name";


     private long id;
    private String code;

    private String name;

    public City() {
    }

    public City(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
    public City(String code, String name) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (id != city.id) return false;
        return code.equals(city.code);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + code.hashCode();
        return result;
    }
}
