package com.popolam.apps.exchangeratesapp.network.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 23.02.2016.
 */
public class RatesResponse extends BasicResponse {
    public double bestRate;
    public double worthRate;
    public int allCount;
    public boolean hasNextPage;
    public String updatedDate;
    public List<Rate> rates = new ArrayList<>();

    public RatesResponse(String error) {
        super(error);
    }

    public double getBestRate() {
        return bestRate;
    }

    public void setBestRate(double bestRate) {
        this.bestRate = bestRate;
    }

    public double getWorthRate() {
        return worthRate;
    }

    public void setWorthRate(double worthRate) {
        this.worthRate = worthRate;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }
    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

}
