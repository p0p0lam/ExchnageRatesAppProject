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
    public StatsResponse(String error) {
        super(error);
    }
}
