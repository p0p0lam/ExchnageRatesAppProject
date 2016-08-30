package com.popolam.apps.exchangeratesapp.ui.fragment;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 06.04.2016.
 */
public enum RateListType {
    BID(1),
    ASK(0);
    private final int index;

    private RateListType(int index) {
        this.index = index;
    }

    public static RateListType fromIndex(int index) {
        switch (index) {
            case 1:
                return BID;
            case 0:
                return ASK;
        }
        return null;
    }

    public int getIndex() {
        return index;
    }
}
