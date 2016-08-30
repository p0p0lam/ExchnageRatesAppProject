package com.popolam.apps.exchangeratesapp.network;

import com.popolam.apps.exchangeratesapp.BuildConfig;
import com.popolam.apps.exchangeratesapp.Settings;
import com.popolam.apps.exchangeratesapp.network.model.DictResponse;
import com.popolam.apps.exchangeratesapp.network.model.OrgRatesResponse;
import com.popolam.apps.exchangeratesapp.network.model.RatesByCityRequest;
import com.popolam.apps.exchangeratesapp.network.model.RatesByLocationRequest;
import com.popolam.apps.exchangeratesapp.network.model.RatesResponse;
import com.popolam.apps.exchangeratesapp.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class ApiNetworker {
    public static final String TAG = ApiNetworker.class.getSimpleName();
    private final ExRateApiService mService;

    public ApiNetworker() {
       this(false);
    }

    public ApiNetworker(boolean isRx) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();
        if (isRx){
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
                Retrofit retrofit = builder.baseUrl(ExRateApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mService = retrofit.create(ExRateApiService.class);
    }

    public DictResponse getDictionaries(){
        try {
            Response<DictResponse> response = mService.getDictionaries(Settings.INSTANCE.getLastLocale()).execute();
            if (response.isSuccessful()){
                return response.body();
            }
        } catch (IOException e){
            Log.e(TAG, "Can't get dictionaries", e);
            return new DictResponse(e.getMessage());
        }
        return new DictResponse("Unknown error");
    }

    public Observable<DictResponse> getDictionariesRx(){
        Log.d(TAG, "Loading dict from server");
        return mService.getDictionariesRx(Settings.INSTANCE.getLastLocale());
    }

    public RatesResponse getRates(int page, int count, double lat, double lng, String currency, int maxDistance, String language, String queryType, String sortType){
        try {
            RatesByLocationRequest request = new RatesByLocationRequest();
            request.page = page;
            request.count = count;
            request.lat = lat;
            request.lng = lng;
            request.maxDistance = maxDistance;
            request.sortType = sortType;
            request.currency = currency;
            request.language = language;
            request.queryType = queryType;
            Response<RatesResponse> response = mService.getRates(request).execute();
            if (response.isSuccessful()){
                return response.body();
            }
        } catch (IOException e){
            Log.e(TAG, "Can't get rates", e);
            return new RatesResponse(e.getMessage());
        }
        return new RatesResponse("Unknown error");
    }

    public RatesResponse getRatesByCity(int page, int count, String city, String currency, String language, String queryType){
        try {
            RatesByCityRequest request = new RatesByCityRequest();
            request.page = page;
            request.count = count;
            request.city = city;
            request.currency = currency;
            request.language = language;
            request.queryType = queryType;
            Response<RatesResponse> response = mService.getRatesByCity(request).execute();
            if (response.isSuccessful()){
                return response.body();
            }
        } catch (IOException e){
            Log.e(TAG, "Can't get rates", e);
            return new RatesResponse(e.getMessage());
        }
        return new RatesResponse("Unknown error");
    }

    public OrgRatesResponse getOrgRates(String orgId){
        try{
            Response<OrgRatesResponse> response = mService.getOrgRates(orgId).execute();
            if (response.isSuccessful()){
                return response.body();
            } else {
                Log.d(TAG, "getOrgRates. Can't get org rates. Error: " + response.errorBody().string());
            }
        } catch (IOException e){
            Log.e(TAG, "Can't get rates", e);
            return new OrgRatesResponse(e.getMessage());
        }
        return new OrgRatesResponse("Unknown error");
    }
}
