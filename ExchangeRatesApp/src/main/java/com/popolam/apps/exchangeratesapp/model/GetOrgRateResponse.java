package com.popolam.apps.exchangeratesapp.model;

import android.text.TextUtils;

import com.popolam.apps.exchangeratesapp.network.model.Rate;

import java.util.List;

/**
 * Created by serhiy.plekhov on 18.12.13.
 */
public class GetOrgRateResponse {
    public List<Rate> rates;
    public String error;
    public boolean isError(){
        return !TextUtils.isEmpty(error);
    }

}
