package com.popolam.apps.exchangeratesapp.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.network.model.Organization;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.network.model.RatesResponse;
import com.popolam.apps.exchangeratesapp.ui.SearchCriteria;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;
import com.popolam.apps.exchangeratesapp.util.DateUtil;
import com.popolam.apps.exchangeratesapp.util.TextUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**

 */
public class MainRatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_RATE = 2;
    public static final int ITEM_TYPE_FOOTER_LOADING = 3;
    public static final double RATE_DIFF_THRESHOLD = 0.3d; // 30%
    public static final String ARG_HAS_NEXT_PAGE = "has_next_page";
    public static final String ARG_ALL_ITEMS_COUNT = "all_items_count";
    public static final String ARG_UPDATE_DATE = "update_date";
    public static final String ARG_BEST_RATE_VALUE = "best_rate_value";
    public static final String ARG_WORTH_RATE_VALUE = "worth_rate_value";
    private final List<Rate> mValues;
    private final OnListIteraction mListener;
    private double bestRateValue;
    private double worthRateValue;
    private boolean mHasNextPage;
    private RateListType mType;
    final boolean mIsLandscape;
    private int mAllItemCount;
    private Date mUpdatedDate;


    public MainRatesAdapter(List<Rate> items, OnListIteraction listener, RateListType type, boolean isLandscape) {
        mValues = items;
        mListener = listener;
        setHasStableIds(true);
        mType = type;
        this.mIsLandscape = isLandscape;
        if (!items.isEmpty()) {
            Rate firstItem = mValues.get(0);
            if (firstItem.getOrganization() != null) {
                // add header
                mValues.add(0, new Rate());
            }
            Rate lastItem = mValues.get(mValues.size() - 1);
            if (lastItem.getOrganization() == null) {
                mHasNextPage = true;
            }
        }
    }

    public void saveInstantState(Bundle state){
        state.putBoolean(ARG_HAS_NEXT_PAGE, mHasNextPage);
        state.putInt(ARG_ALL_ITEMS_COUNT, mAllItemCount);
        if (mUpdatedDate!=null) {
            state.putLong(ARG_UPDATE_DATE, mUpdatedDate.getTime());
        }
        state.putDouble(ARG_BEST_RATE_VALUE, bestRateValue);
        state.putDouble(ARG_WORTH_RATE_VALUE, worthRateValue);
    }

    public void restoreState(Bundle savedState){
        mHasNextPage = savedState.getBoolean(ARG_HAS_NEXT_PAGE);
        mAllItemCount = savedState.getInt(ARG_ALL_ITEMS_COUNT);
        mUpdatedDate = new Date(savedState.getLong(ARG_UPDATE_DATE));
        bestRateValue = savedState.getDouble(ARG_BEST_RATE_VALUE);
        worthRateValue = savedState.getDouble(ARG_WORTH_RATE_VALUE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
                return new HeaderViewHolder(view);
            case ITEM_TYPE_FOOTER_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_next, parent, false);
                return new FooterViewHolder(view);
            case ITEM_TYPE_RATE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_rate, parent, false);
                return new RateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_HEADER:
                fillItemHeader((HeaderViewHolder) holder);
                break;
            case ITEM_TYPE_FOOTER_LOADING:
                fillItemFooter((FooterViewHolder) holder);
                break;
            case ITEM_TYPE_RATE:
                Rate item = mValues.get(position);
                fillRateItem((RateViewHolder) holder, item);
                break;
        }
    }


    public List<Rate> getRates() {
        if (mValues.isEmpty()) {
            return new ArrayList<>(0);
        }
        if (mHasNextPage) {
            return mValues.subList(1, mValues.size() - 1);
        }
        return mValues.subList(1, mValues.size());

    }



    private void fillItemFooter(FooterViewHolder holder) {
        if (mListener != null) {
            mListener.loadNextPage();
        }
    }

    private void fillItemHeader(HeaderViewHolder holder) {
        holder.itemCount.setText(String.valueOf(mAllItemCount));
    }

    public int getAllItemCount() {
        return mAllItemCount;
    }

    public void setAllItemCount(int allItemCount) {
        mAllItemCount = allItemCount;
    }

    private void fillRateItem(RateViewHolder holder, Rate rate) {
        Context context = holder.orgName.getContext();
        holder.mItem = rate;
        Organization org = rate.getOrganization();
        holder.orgName.setText(org.getTitle());
        holder.orgAddress.setText(org.getAddress());
        /*if (mUpdatedDate != null) {
            int formatFlags = DateUtils.FORMAT_SHOW_TIME;
            if (!DateUtils.isToday(mUpdatedDate.getTime())) {
                formatFlags = formatFlags | DateUtils.FORMAT_SHOW_DATE;
            }
            String updatedAtText = DateUtils.formatDateTime(context, mUpdatedDate.getTime(), formatFlags);
            holder.updateDate.setText(updatedAtText);
        } else {
            holder.updateDate.setText(null);
        }*/
        SearchCriteria searchCriteria = mListener.getSearchCriteria();
        if (searchCriteria.location.isAutoLocation()) {
            holder.distance.setVisibility(View.VISIBLE);
            if (rate.getDistance() != null) {
                holder.distance.setText(TextUtil.formatDistance(holder.distance.getContext(), rate.getDistance().getValue()));
            } else {
                holder.distance.setText(null);
            }
        } else {
            holder.distance.setVisibility(View.GONE);
        }
        double diffFromMax = rate.getRateDiffByType(bestRateValue, worthRateValue, mType);

        Drawable typeIcon = null;
        switch (rate.getOrganization().getType()) {
            case Organization.TYPE_BANK:
                typeIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_bank).mutate());
                break;
            case Organization.TYPE_FOP:
                typeIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_fop).mutate());
                break;
        }
        int color = 0;
        switch (mType) {
            case ASK:
                holder.rateMain.setText(TextUtil.formatDouble(rate.getBid()));
                holder.rateSup.setText(TextUtil.formatDouble(rate.getAsk()));

                if (diffFromMax < Double.valueOf(RATE_DIFF_THRESHOLD)) {
                    color = ContextCompat.getColor(context, R.color.primary);

                } else {
                    color = ContextCompat.getColor(context, R.color.text_supl);
                }
                break;
            case BID:
                holder.rateMain.setText(TextUtil.formatDouble(rate.getAsk()));
                holder.rateSup.setText(TextUtil.formatDouble(rate.getBid()));
                if (diffFromMax < Double.valueOf(RATE_DIFF_THRESHOLD)) {
                    color = ContextCompat.getColor(context, R.color.primary);
                } else {
                    color = ContextCompat.getColor(context, R.color.text_supl);
                }
                break;
        }
        if (typeIcon != null){
            DrawableCompat.setTint(typeIcon, color);
            holder.orgName.setCompoundDrawablesWithIntrinsicBounds(typeIcon, null, null, null);
        }
        holder.rateMain.setTextColor(color);
        holder.orgName.setTextColor(color);
        if (rate.getDistance()!=null) {
            Drawable locIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_point).mutate());
            DrawableCompat.setTint(locIcon, color);
            holder.distance.setCompoundDrawablesWithIntrinsicBounds(null, null, locIcon, null);
        }

    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_HEADER:
                return 1L;
            case ITEM_TYPE_RATE:
                Rate rate = mValues.get(position);
                return rate.getOrganization().getId().hashCode();
            case ITEM_TYPE_FOOTER_LOADING:
                return 2L;
        }
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (position == mValues.size() - 1 && mHasNextPage) {
            return ITEM_TYPE_FOOTER_LOADING;
        } else {
            return ITEM_TYPE_RATE;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public double getBestRateValue() {
        return bestRateValue;
    }

    public double getWorthRateValue() {
        return worthRateValue;
    }

    public void setData(RatesResponse newData, int page) {
        int startChange = 0;
        int itemCount = 0;
        mHasNextPage = newData.isHasNextPage();
        mAllItemCount = newData.allCount;
        mUpdatedDate = DateUtil.parseIsoDate(newData.updatedDate);
        if (page == 1) {
            mValues.clear();
            bestRateValue = newData.getBestRate();
            worthRateValue = newData.getWorthRate();
            if (newData.getRates().size() > 0) {
                mValues.add(0, new Rate());//header empty view
            }
        } else {
            if (mValues.size() > 0) {
                mValues.remove(mValues.size() - 1); // remove footer
            }
            startChange = mValues.size();
            itemCount = newData.getRates().size();
        }
        mValues.addAll(newData.getRates());
        if (mHasNextPage) {
            mValues.add(new Rate()); // footer loading view
        }
        if (page > 1) {
            notifyItemRangeChanged(startChange, itemCount);
        } else {
            notifyDataSetChanged();
        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView itemCount;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            if (!mIsLandscape) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onHeaderClicked();
                        }
                    }
                });
            }
            itemCount = (TextView) itemView.findViewById(R.id.rate_list_all_count);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        TextView orgName;
        TextView orgAddress;
        TextView distance;
        TextView rateMain;
        TextView rateSup;
        //ViewGroup rateContainer;
        View clickContainer;
        Rate mItem;

        public RateViewHolder(View view) {
            super(view);
            orgName = (TextView) view.findViewById(R.id.rate_item_org_title);
            orgAddress = (TextView) view.findViewById(R.id.rate_item_org_address);
            distance = (TextView) view.findViewById(R.id.rate_item_distance);
            rateMain = (TextView) view.findViewById(R.id.rate_item_rate_main);
            rateSup = (TextView) view.findViewById(R.id.rate_item_rate_sup);
            //rateContainer = (ViewGroup) view.findViewById(R.id.rate_item_rate_container);
            //holder.indicator = view.findViewById(R.id.rate_item_indicator);
            clickContainer = view.findViewById(R.id.click_container);
            clickContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListItemSelected(mItem);
                }
            });
        }

    }

    public interface OnListIteraction {
        void onListItemSelected(Rate item);
        void onHeaderClicked();

        SearchCriteria getSearchCriteria();

        void loadNextPage();
    }
}
