package com.popolam.apps.exchangeratesapp.ui.fragment;

import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.ui.SearchCriteria;

import java.util.List;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 06.04.2016.
 */
public interface OnActivityInteractionListener {
    RateListType getType();
    double getBestRate();
    double getWorthRate();
    int getListViewWidth();
    void onFilterChanged(SearchCriteria newFilter, boolean forceLoad);
    List<Rate> getRates();
}
