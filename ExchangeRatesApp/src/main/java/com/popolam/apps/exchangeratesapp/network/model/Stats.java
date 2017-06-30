package com.popolam.apps.exchangeratesapp.network.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.popolam.apps.exchangeratesapp.network.adapter.DateTypeAdapter;

import java.util.Date;

/**
 * Project MOSST Code
 * Created by Serhiy Plekhov on 29.06.2017.
 */

public class Stats {
    @SerializedName("date")
    @JsonAdapter(DateTypeAdapter.class)
    public Date date;
    @SerializedName("currencyCode")
    public String currencyCode;
    @SerializedName("ask")
    public float ask;
    @SerializedName("bid")
    public float bid;
}
