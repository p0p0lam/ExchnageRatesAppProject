package com.popolam.apps.exchangeratesapp.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Project MOSST Code
 * Created by Serhiy Plekhov on 29.06.2017.
 */

public class Stats {
    @SerializedName("date")
    public String date;
    @SerializedName("currencyCode")
    public String currencyCode;
    @SerializedName("ask")
    public double ask;
    @SerializedName("bid")
    public double bid;
}
