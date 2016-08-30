package com.popolam.apps.exchangeratesapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.popolam.apps.exchangeratesapp.network.model.Rate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serhiy.plekhov on 09.12.13.
 */
public class GetRateResponse implements Parcelable {
    public List<Rate> rates;
    public double maxBidValue;
    public double maxAskValue;
    public int allCount;
    public boolean hasNextPage;
    public String error;

    public GetRateResponse() {
        rates = new ArrayList<Rate>();
    }

    public boolean isError(){
        return !TextUtils.isEmpty(error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.rates);
        dest.writeDouble(this.maxBidValue);
        dest.writeDouble(this.maxAskValue);
        dest.writeInt(this.allCount);
        dest.writeByte(hasNextPage ? (byte) 1 : (byte) 0);
    }

    private GetRateResponse(Parcel in) {
        this.rates = new ArrayList<Rate>();
        in.readList(this.rates, Rate.class.getClassLoader());
        this.maxBidValue = in.readDouble();
        this.maxAskValue = in.readDouble();
        this.allCount = in.readInt();
        this.hasNextPage = in.readByte() != 0;
    }

    public static Parcelable.Creator<GetRateResponse> CREATOR = new Parcelable.Creator<GetRateResponse>() {
        public GetRateResponse createFromParcel(Parcel source) {
            return new GetRateResponse(source);
        }

        public GetRateResponse[] newArray(int size) {
            return new GetRateResponse[size];
        }
    };

}
