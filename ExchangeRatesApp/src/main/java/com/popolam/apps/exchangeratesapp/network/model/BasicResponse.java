package com.popolam.apps.exchangeratesapp.network.model;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class BasicResponse {
    String message;
    int errorCode;

    public BasicResponse(String error) {
        this.message = error;
    }

    public String getError() {
        return message;
    }

    public void setError(String error) {
        this.message = error;
    }

    public boolean isSuccess(){
        return message == null;
    }
}
