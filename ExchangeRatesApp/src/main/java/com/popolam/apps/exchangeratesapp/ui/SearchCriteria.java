package com.popolam.apps.exchangeratesapp.ui;

import android.os.Parcel;
import android.os.Parcelable;

import com.popolam.apps.exchangeratesapp.Settings;
import com.popolam.apps.exchangeratesapp.model.Currency;
import com.popolam.apps.exchangeratesapp.model.LocationWrapper;
import com.popolam.apps.exchangeratesapp.model.SortType;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 06.04.2016.
 */
public class SearchCriteria implements Parcelable {
    public Currency currency;
    public LocationWrapper location;
    public SortType sort = SortType.RATE;
    public int radius;
    public int itemsPerPage;
    public RateListType type;




    public boolean isCompleted() {
        return (sort != null && currency != null && location != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchCriteria that = (SearchCriteria) o;

        if (sort != that.sort) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (radius!=that.radius) return false;
        if (itemsPerPage !=that.itemsPerPage) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = currency != null ? currency.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + sort.hashCode();
        result = 31 * result + radius + itemsPerPage;
        return result;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
                "currency=" + currency +
                ", location=" + location +
                ", sort=" + sort +
                ", radius=" + radius +
                ", itemsPerPage=" + itemsPerPage +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currency, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeInt(radius);
        dest.writeInt(itemsPerPage);
        dest.writeString(type.name());
        dest.writeInt(this.sort == null ? -1 : this.sort.ordinal());
    }

    public SearchCriteria() {
        radius = Settings.INSTANCE.getRadius();
        itemsPerPage = Settings.INSTANCE.getListCount();
    }

    protected SearchCriteria(Parcel in) {
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.location = in.readParcelable(LocationWrapper.class.getClassLoader());
        this.radius = in.readInt();
        this.itemsPerPage = in.readInt();
        this.type = RateListType.valueOf(in.readString());
        int tmpSort = in.readInt();
        this.sort = tmpSort == -1 ? null : SortType.values()[tmpSort];
    }

    public static final Parcelable.Creator<SearchCriteria> CREATOR = new Parcelable.Creator<SearchCriteria>() {
        @Override
        public SearchCriteria createFromParcel(Parcel source) {
            return new SearchCriteria(source);
        }

        @Override
        public SearchCriteria[] newArray(int size) {
            return new SearchCriteria[size];
        }
    };
}
