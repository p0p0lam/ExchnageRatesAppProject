package com.popolam.apps.exchangeratesapp.ui.loader;

import android.content.Context;

import com.popolam.apps.exchangeratesapp.Settings;
import com.popolam.apps.exchangeratesapp.network.ApiNetworker;
import com.popolam.apps.exchangeratesapp.network.model.RatesResponse;
import com.popolam.apps.exchangeratesapp.ui.SearchCriteria;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;
import com.popolam.apps.exchangeratesapp.util.Log;

import java.util.Locale;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 31.03.2016.
 */
public class RatesLoader extends BaseNetworkLoader<RatesResponse> {
    private static final String TAG = RatesLoader.class.getSimpleName();
    private final ApiNetworker mApiNetworker;
    private final SearchCriteria mSearchCriteria;
    private final int page;
    private final RateListType queryType;
    public RatesLoader(Context context, RateListType type, SearchCriteria criteria, int page) {
        super(context);
        mApiNetworker = new ApiNetworker();
        this.page = page;
        this.mSearchCriteria = criteria;
        queryType = type;
    }

    @Override
    public RatesResponse loadInBackground() {
        if (mSearchCriteria.currency!=null && mSearchCriteria.location!=null && mSearchCriteria.sort!=null) {
            Log.d(TAG, String.format("loadInBackground. Page: %d, Type: %s, Currency: %s, Sort: %s", page, queryType.name(), mSearchCriteria.currency.code, mSearchCriteria.sort.name()));
            if (mSearchCriteria.location.isAutoLocation()) {
                return mApiNetworker.getRates(page, Settings.INSTANCE.getListCount(), mSearchCriteria.location.getLatitude(), mSearchCriteria.location.getLongitude(),
                        mSearchCriteria.currency.code, Settings.INSTANCE.getRadius(), Locale.getDefault().getLanguage(), queryType.name(), mSearchCriteria.sort.name());
            } else {
                return mApiNetworker.getRatesByCity(page, Settings.INSTANCE.getListCount(), mSearchCriteria.location.cityName,
                        mSearchCriteria.currency.code, Locale.getDefault().getLanguage(), queryType.name());
            }
        }
        return null;
    }


}
