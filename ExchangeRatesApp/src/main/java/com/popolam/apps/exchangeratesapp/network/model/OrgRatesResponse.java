package com.popolam.apps.exchangeratesapp.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 11.04.2016.
 */
public class OrgRatesResponse extends BasicResponse {
    public OrgRatesResponse(String error) {
        super(error);
    }

    public OrgRatesResponse(String error, List<Currency> rates) {
        super(error);
        this.rates = rates;
    }

    @SerializedName("rates")
    public List<Currency> rates;

    public class Currency{
        @SerializedName("currency_id")
        public String code;
        @SerializedName("rate")
        public Rate rate;
    }

    public class Rate{
        public double ask;
        public double bid;
    }
}
