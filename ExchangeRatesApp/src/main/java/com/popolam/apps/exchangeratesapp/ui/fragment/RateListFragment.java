package com.popolam.apps.exchangeratesapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.network.model.RatesResponse;
import com.popolam.apps.exchangeratesapp.ui.DetailsActivity;
import com.popolam.apps.exchangeratesapp.ui.SearchCriteria;
import com.popolam.apps.exchangeratesapp.ui.adapter.MainRatesAdapter;
import com.popolam.apps.exchangeratesapp.ui.loader.RatesLoader;
import com.popolam.apps.exchangeratesapp.util.Log;
import com.popolam.apps.exchangeratesapp.util.NetworkUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RateListFragment extends Fragment implements OnActivityInteractionListener, LoaderManager.LoaderCallbacks<RatesResponse>, MainRatesAdapter.OnListIteraction {
    public static final String KEY_CURRENT_PAGE = "current_page";
    public static final String KEY_RATES_LIST = "rates_list";
    public static final String KEY_SEARCH_CRITERIA = "search_criteria";
    public static final String KEY_ALL_ITEM_COUNT = "all_item_count";
    public static final String KEY_IS_MAP_COLLAPSED = "is_map_collapsed";

    private String TAG = RateListFragment.class.getSimpleName();
    public static final String ARG_RATE_LIST_TYPE = "rate_list_type";
    List<Rate> mRates = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;
    private RateListType mRateListType;
    private SearchCriteria mSearchCriteria = new SearchCriteria();
    private View mProgressView;
    private View mEmptyView;
    private MainRatesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyText;
    private int mCurrentPage =1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsLandscape;
    private boolean mIsMapCollapsed = true;
    private View mListViewContainer;


    public static RateListFragment newInstance(RateListType rateListType) {
        Bundle args = new Bundle();
        args.putString(ARG_RATE_LIST_TYPE, rateListType.name());
        RateListFragment fragment = new RateListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public RateListType getType() {
        return mRateListType;
    }

    @Override
    public void onFilterChanged(SearchCriteria newFilter, boolean forceLoad) {
        if (forceLoad || !newFilter.equals(mSearchCriteria)){
            Log.d(TAG, "onFilterChanged: " + newFilter);
            mSearchCriteria.location = newFilter.location;
            mSearchCriteria.currency = newFilter.currency;
            mSearchCriteria.sort = newFilter.sort;
            mSearchCriteria.radius = newFilter.radius;
            mSearchCriteria.itemsPerPage = newFilter.itemsPerPage;
            mCurrentPage=1;
            if (mSearchCriteria.isCompleted()){
                loadRates();
            }
        }
    }

    private void loadRates(){
        if (NetworkUtil.hasDataConnection(getContext())) {
            Log.d(TAG, "loadRates started");
            showProgress();
            getLoaderManager().restartLoader(0, null, this);
            // mLoader = (NewRatesLoader) getLoaderManager().restartLoader(1, null, newCallback);
        } else {
            showEmpty(getString(R.string.error_network_not_available));
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RateListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRateListType = RateListType.valueOf(getArguments().getString(ARG_RATE_LIST_TYPE));
        mSearchCriteria.type = mRateListType;
        if (savedInstanceState!=null){
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            mRates = savedInstanceState.getParcelableArrayList(KEY_RATES_LIST);
            mSearchCriteria = savedInstanceState.getParcelable(KEY_SEARCH_CRITERIA);
            mIsMapCollapsed = savedInstanceState.getBoolean(KEY_IS_MAP_COLLAPSED, true);
        }
        TAG = TAG+"-" + mRateListType.name();
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
        outState.putParcelableArrayList(KEY_RATES_LIST, (ArrayList) mRates);
        outState.putParcelable(KEY_SEARCH_CRITERIA, mSearchCriteria);
        outState.putInt(KEY_ALL_ITEM_COUNT, mAdapter.getAllItemCount());
        outState.putBoolean(KEY_IS_MAP_COLLAPSED, mIsMapCollapsed);
        mAdapter.saveInstantState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        // Set the adapter
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MainRatesAdapter(mRates, this, getType(), mIsLandscape);
        if (savedInstanceState!=null){
            mAdapter.restoreState(savedInstanceState);
        }
        mRecyclerView.setAdapter(mAdapter);
        mProgressView = view.findViewById(R.id.progress);
        mEmptyView = view.findViewById(R.id.empty_container);
        mEmptyText = (TextView) mEmptyView.findViewById(android.R.id.empty);
        Button reloadButton = (Button) mEmptyView.findViewById(R.id.empty_reload_button);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRates();
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.hasDataConnection(getContext())) {
                    mCurrentPage = 1;
                    getLoaderManager().restartLoader(0, null, RateListFragment.this);
                }
            }
        });
        if (savedInstanceState==null) {
            showProgress();
        } else {
            int allItemCount = savedInstanceState.getInt(KEY_ALL_ITEM_COUNT);
            mAdapter.setAllItemCount(allItemCount);
        }
        if (mIsLandscape) {
            FrameLayout headerView = (FrameLayout) view.findViewById(R.id.map_container_switcher);
            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHeaderClicked();
                }
            });
        }

        mListViewContainer = view.findViewById(R.id.listview_container);

        /*
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_MAP_CAMERA_POSITION)){
            options.camera((CameraPosition)savedInstanceState.getParcelable(KEY_MAP_CAMERA_POSITION));
        }
        */
        if (savedInstanceState == null) {
            showProgress();
        } else if (!mRates.isEmpty()){
            showData();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.attachFilterChangeListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!NetworkUtil.hasDataConnection(getContext())){
            showEmpty(getResources().getString(R.string.error_network_not_available));
        }
    }

    void showProgress(){
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    void showData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
    }

    void showEmpty(String text){
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyText.setText(text);
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRateListType = RateListType.valueOf(getArguments().getString(ARG_RATE_LIST_TYPE));
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.detachFilterChangeListener(this);
        mListener = null;
    }

    @Override
    public SearchCriteria getSearchCriteria() {
        return mSearchCriteria;
    }

    @Override
    public void onListItemSelected(Rate item) {
        if (mListener!=null){
            mListener.onItemSelected(item);
            startDetailsActivity(item);
        }
    }
    private void startDetailsActivity(Rate item){
        Intent details = new Intent(getActivity(), DetailsActivity.class);
        Bundle args = new Bundle();
        args.putParcelable("org", item);
        args.putParcelable("location", mSearchCriteria.location);
        args.putInt("type", mRateListType.getIndex());
        args.putDouble("bestRate", mAdapter.getBestRateValue());
        args.putDouble("worthRate", mAdapter.getWorthRateValue());
        details.putExtras(args);
        startActivity(details);
    }

    @Override
    public void loadNextPage() {
        mCurrentPage++;
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onHeaderClicked() {
        Log.d(TAG, "onHeaderClicked ");
        /*if (mIsMapCollapsed){
            expandMap();
        }*/
        mListener.expandMap(true);
    }

    @Override
    public Loader<RatesResponse> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader ");
        switch (id){
            case 0:
                return new RatesLoader(getContext(), mRateListType, mSearchCriteria, mCurrentPage);
        }
        return null;
    }



    @Override
    public void onLoadFinished(Loader<RatesResponse> loader, RatesResponse data) {
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        switch (loader.getId()){
            case 0:
            if (data!=null && data.getRates()!=null && !data.getRates().isEmpty()){
                Log.d(TAG, "onLoadFinished. Got rates");
                mAdapter.setData(data, mCurrentPage);
                showData();
                mListener.showMarkers(mRateListType, mAdapter.getRates(), data.getBestRate(), data.getWorthRate());
                //drawMarkers();
                //setMapBounds();

            } else {
                Log.d(TAG, "onLoadFinished. Empty response");
                if (mSearchCriteria.isCompleted()) {
                    showEmpty(getString(R.string.rates_empty));
                    mListener.showMarkers(mRateListType, Collections.<Rate>emptyList(), 0, 0);
                }
            }
            break;
            case 1:
                Log.d(TAG, "onLoadFinished new rates loader");
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<RatesResponse> loader) {
        loader.cancelLoad();
    }

    @Override
    public double getBestRate() {
        return mAdapter.getBestRateValue();
    }

    @Override
    public double getWorthRate() {
        return mAdapter.getWorthRateValue();
    }

    @Override
    public int getListViewWidth() {
        return mListViewContainer.getWidth();
    }

    @Override
    public List<Rate> getRates() {
        if (mAdapter!=null){
            return mAdapter.getRates();
        }
        return Collections.emptyList();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void attachFilterChangeListener(OnActivityInteractionListener listener);
        void detachFilterChangeListener(OnActivityInteractionListener listener);
        void onItemSelected(Rate item);
        void expandMap(boolean animate);
        void collapseMap(boolean animate);
        void showMarkers(RateListType type, List<Rate> rates, double bestRate, double worthRate);
    }
}
