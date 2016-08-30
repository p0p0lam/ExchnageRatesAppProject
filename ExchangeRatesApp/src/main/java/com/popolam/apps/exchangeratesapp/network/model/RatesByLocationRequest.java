package com.popolam.apps.exchangeratesapp.network.model;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 08.04.2016.
 */
public class RatesByLocationRequest extends PagingRequest {
    public double lat;
    public double lng;
    public int maxDistance;
    public String sortType;
}
