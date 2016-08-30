package com.popolam.apps.exchangeratesapp.ui;


import com.popolam.apps.exchangeratesapp.network.model.Rate;

import java.util.List;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 29.02.2016.
 */
public interface OnDataLoaderListener {
    void onStartLoading();

    void onDataLoaded(List<Rate> rates);
}
