package com.popolam.apps.exchangeratesapp.network.model;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class DictResponse extends BasicResponse{

    List<Currency> currencies = new ArrayList<>();
    List<City> cities = new ArrayList<>();

    public DictResponse(String error) {
        super(error);
    }

    public DictResponse(List<Currency> currencies, List<City> cities) {
        super(null);
        this.currencies = currencies;
        this.cities = cities;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    /**
     * The web service always returns a HTTP header code of 200 and communicates errors
     * through a 'cod' field in the JSON payload of the response body.
     */
    public Observable filterWebServiceErrors() {
        if (isSuccess()) {
            return Observable.just(this);
        } else {
            return Observable.error(
                    new Exception(message));
        }
    }
}
