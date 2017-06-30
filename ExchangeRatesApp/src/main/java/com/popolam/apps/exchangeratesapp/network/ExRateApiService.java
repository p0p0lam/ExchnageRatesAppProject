package com.popolam.apps.exchangeratesapp.network;

import com.popolam.apps.exchangeratesapp.network.model.DictResponse;
import com.popolam.apps.exchangeratesapp.network.model.OrgRatesResponse;
import com.popolam.apps.exchangeratesapp.network.model.RatesByCityRequest;
import com.popolam.apps.exchangeratesapp.network.model.RatesByLocationRequest;
import com.popolam.apps.exchangeratesapp.network.model.RatesResponse;
import com.popolam.apps.exchangeratesapp.network.model.StatsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public interface ExRateApiService {
    String BASE_URL = "http://exrate-p0p0lam.rhcloud.com/api/v1/";
    String GET_RATES ="getRates";
    String GET_ORG_RATES ="getOrgRates/{orgId}";
    String GET_RATES_BY_CITY ="getRatesByCity";
    String GET_DICTS ="getDictionaries";
    String GET_STATS ="getStat";

    @GET(GET_DICTS)
    Call<DictResponse> getDictionaries(@Query("language") String language);

    @GET(GET_DICTS)
    Single<DictResponse> getDictionariesRx(@Query("language") String language);

    @GET(GET_STATS)
    Single<StatsResponse> getStatsRx(@Query("currencyCode") String currencyCode);


    @POST(GET_RATES)
    Call<RatesResponse> getRates(@Body RatesByLocationRequest request);


    @POST(GET_RATES_BY_CITY)
    Call<RatesResponse> getRatesByCity(@Body RatesByCityRequest request);

    @GET(GET_ORG_RATES)
    Call<OrgRatesResponse> getOrgRates(@Path("orgId") String orgId);
}
