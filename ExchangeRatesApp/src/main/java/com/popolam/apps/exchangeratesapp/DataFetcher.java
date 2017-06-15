package com.popolam.apps.exchangeratesapp;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.popolam.apps.exchangeratesapp.db.ExRateDatabaseHelper;
import com.popolam.apps.exchangeratesapp.model.Currency;
import com.popolam.apps.exchangeratesapp.model.LocationWrapper;
import com.popolam.apps.exchangeratesapp.network.ApiNetworker;
import com.popolam.apps.exchangeratesapp.network.model.City;
import com.popolam.apps.exchangeratesapp.network.model.DictResponse;
import com.popolam.apps.exchangeratesapp.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Project: ExchnageRatesAppProject
 * Created by sergey.plekhov on 25.08.2016.
 */
public class DataFetcher {
    private static final String TAG = DataFetcher.class.getSimpleName();
    private WeakReference<OnDataLoadedListener> mOnDataLoadedListener;
    private final ReactiveLocationProvider mLocationProvider;
    private CompositeSubscription mCompositeSubscription;
    private final Observable<LocationWrapper> mLocationObservable;
    private final Observable<List<Currency>> mDictDbObservable;
    private final Observable<List<Currency>> mDictNetObservable;
    private final Subscriber<List<Currency>> mDictSubscriber;

    private long UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(1);
    private long FASTEST_INTERVAL = 1000; /* 1 sec */

    public DataFetcher(OnDataLoadedListener listener) {
        mOnDataLoadedListener = new WeakReference<>(listener);
        mCompositeSubscription = new CompositeSubscription();
        mLocationProvider = new ReactiveLocationProvider((Context) listener);
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setExpirationDuration(TimeUnit.MINUTES.toMillis(5))
                .setFastestInterval(FASTEST_INTERVAL);
        mLocationObservable = mLocationProvider.getUpdatedLocation(request).map(new Func1<Location, LocationWrapper>() {
            @Override
            public LocationWrapper call(Location location) {
                return new LocationWrapper(location);
            }
        });
        mDictDbObservable = Observable.fromCallable(new Callable<List<Currency>>() {
            @Override
            public List<Currency> call() throws Exception {
                Log.d(TAG, "Got dicts from db");
                return App.getInstance().getDatabaseHelper().getCurrencies();
            }
        });
        mDictNetObservable = new ApiNetworker(true).getDictionariesRx()
                .map(new Func1<DictResponse, List<Currency>>() {
                    @Override
                    public List<Currency> call(DictResponse dicts) {
                        Log.d(TAG, "Got dicts from network");
                        ExRateDatabaseHelper dbHelper = App.getInstance().getDatabaseHelper();
                        List<Currency> currencies = new ArrayList<>(dicts.getCurrencies().size());
                        for (com.popolam.apps.exchangeratesapp.network.model.Currency curr : dicts.getCurrencies()) {
                            currencies.add(new Currency(curr.getId(), curr.getName()));
                        }
                        dbHelper.saveCurrencies(currencies);
                        List<com.popolam.apps.exchangeratesapp.model.City> cities = new ArrayList<>(dicts.getCities().size());
                        for (City city : dicts.getCities()) {
                            cities.add(new com.popolam.apps.exchangeratesapp.model.City(city.getId(), city.getName()));
                        }
                        dbHelper.saveCities(cities);
                        Settings.INSTANCE.setLastDictCheck(System.currentTimeMillis());
                        return currencies;
                    }
                });
        mDictSubscriber = new Subscriber<List<Currency>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "Dict loaded");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Can't load dict", e);
            }

            @Override
            public void onNext(List<Currency> currencies) {
                OnDataLoadedListener listener = mOnDataLoadedListener.get();
                if (listener!=null){
                    listener.onDictLoaded(currencies);
                }
            }
        };
    }



    public void getLocation(){
        if (!Settings.INSTANCE.isAutoLocation()){
            OnDataLoadedListener listener = mOnDataLoadedListener.get();
            if (listener!=null){
                listener.onLocationRetrieved(new LocationWrapper(Settings.INSTANCE.getCityName(), Settings.INSTANCE.getManualLocation()));
            }
            return;
        }
        OnDataLoadedListener listener = mOnDataLoadedListener.get();
        if (listener!=null){
            listener.onLocationStarted();
        }
        mCompositeSubscription.add(mLocationObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LocationWrapper>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Location completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Can't get location.", e);
                    }

                    @Override
                    public void onNext(LocationWrapper locationWrapper) {
                        Log.d(TAG, "Got location " + locationWrapper);
                        OnDataLoadedListener listener = mOnDataLoadedListener.get();
                        if (listener!=null){
                            listener.onLocationRetrieved(locationWrapper);
                        }
                    }
                }));
    }

    public void getDicts(){
        OnDataLoadedListener listener = mOnDataLoadedListener.get();
        if (listener!=null){
            listener.onDictStared();
        }
        mCompositeSubscription.add(mDictDbObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDictSubscriber)
        );
        if (Settings.INSTANCE.isDictExpired()){
            Log.d(TAG, "Need to update dicts");
            mCompositeSubscription.add(mDictNetObservable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Currency>>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "Dict loaded from network");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Can't load dict from network", e);
                        }

                        @Override
                        public void onNext(List<Currency> currencies) {
                            Log.d(TAG, "onNext dict");
                            OnDataLoadedListener listener = mOnDataLoadedListener.get();
                            if (listener!=null){
                                listener.onDictLoaded(currencies);
                            }
                        }
                    })
            );
        }
    }

    public void stop(){
        mCompositeSubscription.unsubscribe();
    }

    public interface OnDataLoadedListener {
        void onDictStared();

        void onDictLoaded(List<Currency> currencies);

        void onLocationStarted();

        void onLocationRetrieved(LocationWrapper location);
    }
}
