package com.popolam.apps.exchangeratesapp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.network.model.OrgRatesResponse;
import com.popolam.apps.exchangeratesapp.util.TextUtil;

import java.util.List;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 12.04.2016.
 */
public class OrgRatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VT_HEADER = 1;
    public static final int VT_ITEM = 2;
    private final List<OrgRatesResponse.Currency> mItems;
    public OrgRatesAdapter(List<OrgRatesResponse.Currency> items) {
        mItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case VT_HEADER:
               return new HeaderVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_org_rate, parent, false));
            case VT_ITEM:
                return new RateItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_org_rate, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderSource, int position) {
        if (getItemViewType(position) == VT_ITEM){
            OrgRatesResponse.Currency item = mItems.get(position-1);
            RateItemVH holder = (RateItemVH) holderSource;
            holder.askRate.setText(TextUtil.formatDouble(item.rate.ask));
            holder.bidRate.setText(TextUtil.formatDouble(item.rate.bid));
            holder.currency.setText(item.code);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return VT_HEADER;
        } else {
            return VT_ITEM;
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size()+1; // includes header
    }
    static class HeaderVH extends RecyclerView.ViewHolder{
        public HeaderVH(View itemView) {
            super(itemView);
        }
    }

    static class RateItemVH extends RecyclerView.ViewHolder{
        final TextView askRate;
        final TextView bidRate;
        final TextView currency;
        public RateItemVH(View itemView) {
            super(itemView);
            askRate = (TextView) itemView.findViewById(R.id.rate_ask);
            bidRate = (TextView) itemView.findViewById(R.id.rate_bid);
            currency =(TextView) itemView.findViewById(R.id.rate_currency);
        }
    }
}
