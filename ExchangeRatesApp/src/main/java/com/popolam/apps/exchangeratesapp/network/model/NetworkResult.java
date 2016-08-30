package com.popolam.apps.exchangeratesapp.network.model;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class NetworkResult<T> {
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
