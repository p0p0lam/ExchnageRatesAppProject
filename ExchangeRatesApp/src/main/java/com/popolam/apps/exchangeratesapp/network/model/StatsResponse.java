package com.popolam.apps.exchangeratesapp.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Project MOSST Code
 * Created by Serhiy Plekhov on 29.06.2017.
 */

public class StatsResponse extends BasicResponse {
    @SerializedName("stats")
    public List<Stats> stats;

    @SerializedName("minAsk")
    public float minAsk;
    @SerializedName("maxAsk")
    public float maxAsk;
    @SerializedName("minBid")
    public float minBid;
    @SerializedName("maxBid")
    public float maxBid;

    public StatsResponse(String error) {
        super(error);
    }
}
