package com.popolam.apps.exchangeratesapp.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.popolam.apps.exchangeratesapp.BuildConfig;
import com.popolam.apps.exchangeratesapp.DataFetcher;
import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.Settings;
import com.popolam.apps.exchangeratesapp.model.Currency;
import com.popolam.apps.exchangeratesapp.model.LocationWrapper;
import com.popolam.apps.exchangeratesapp.model.SortType;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.ui.fragment.OnActivityInteractionListener;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListFragment;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;
import com.popolam.apps.exchangeratesapp.ui.map.RateRenderer;
import com.popolam.apps.exchangeratesapp.ui.widget.InfoDialogFragment;
import com.popolam.apps.exchangeratesapp.ui.widget.MainPagerAdapter;
import com.popolam.apps.exchangeratesapp.util.Log;
import com.popolam.apps.exchangeratesapp.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements
        RateListFragment.OnListFragmentInteractionListener, OnMapReadyCallback, DataFetcher.OnDataLoadedListener,  ActivityCompat.OnRequestPermissionsResultCallback  {
    private static final String TAG = Log.calculateTag(MainActivity.class);
    public static final int REQUEST_CODE_SETTINGS = 0;
    public static final int TAB_ASK = 0;
    public static final int TAB_BID = 1;
    public static final String KEY_SORT = "sort";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_IS_MAP_COLLAPSED = "is_map_collapsed";
    public static final String KEY_SELECTED_TAB_POSITION = "selected_tab_position";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ArrayAdapter<Currency> mCurrencyAdapter;
    private ViewPager mPager;
    private Spinner mCurrencySpinner;
    private List<Currency> mCurrencies;
    private OnActivityInteractionListener mAskRateListener;
    private OnActivityInteractionListener mBidRateListener;

    MainPagerAdapter mPagerAdapter;
    boolean mIsAutoLocation = true;
    private SearchCriteria mSearchCriteria;
    private TabLayout mTabLayout;
    private GoogleMap mMap;
    private Button mCloseMapButton;
    private ClusterManager<Rate> mClusterManager;
    private RateRenderer mRateRenderer;
    private SupportMapFragment mMapFragment;
    private boolean mIsMapCollapsed = true;
    private boolean mIsLandscape = false;
    private FrameLayout mContentContainer;
    private int mContentHeight;
    private int mDefaultMapPadding;
    private int mDefaultHeaderHeight;
    private Marker mMyLocationMarker;
    private Animator.AnimatorListener mAnimListener;
    private int mSelectedTabPosition=0;
    private CompositeSubscription mSubscriptions;
    boolean mIsFromSavedState=false;
    private DataFetcher mDataFetcher;
    private BottomSheetBehavior mChartBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchCriteria = new SearchCriteria();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCurrencies = new ArrayList<>();
        mContentContainer = (FrameLayout) findViewById(R.id.contentContainer);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);
        mCloseMapButton = (Button) findViewById(R.id.close_map_button);
        mCloseMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseMap(true);
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mPager = (ViewPager) findViewById(R.id.pager);
        //mPager.requestTransparentRegion(mPager);

        mCurrencyAdapter = new CurrencyArrayAdapter(this, mCurrencies);
        mCurrencyAdapter.setDropDownViewResource(R.layout.spinner_actionbar_dropdown_item);
        mCurrencySpinner = (Spinner) LayoutInflater.from(this).inflate(R.layout.spinner_actionbar, toolbar, false);
        mCurrencySpinner.setAdapter(mCurrencyAdapter);

        //mCurrencyAdapter = new ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item, mCurrencies);
        //mCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (savedInstanceState != null) {
            mSearchCriteria.sort = SortType.valueOf(savedInstanceState.getString(KEY_SORT, SortType.RATE.name()));
            mSearchCriteria.currency = savedInstanceState.getParcelable(KEY_CURRENCY);
            mSearchCriteria.location = savedInstanceState.getParcelable(KEY_LOCATION);
            mIsMapCollapsed = savedInstanceState.getBoolean(KEY_IS_MAP_COLLAPSED);
            mSelectedTabPosition = savedInstanceState.getInt(KEY_SELECTED_TAB_POSITION);
            mIsFromSavedState = true;
        } else {
            mSearchCriteria.sort = SortType.RATE;
        }
        setupActionBar();
        mIsAutoLocation = Settings.INSTANCE.isAutoLocation();
        if (!mIsAutoLocation && Settings.INSTANCE.getManualLocation() == null) {
            InfoDialogFragment error = InfoDialogFragment.newInstance(getString(R.string.error_title), getString(R.string.error_autolocation_off), getString(R.string.button_settings));
            error.setOnDialogListener(new InfoDialogFragment.OnDialogListener() {
                @Override
                public void onDialogResult(int result, Bundle data) {
                    if (result == RESULT_OK) {
                        startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
                    }
                }
            });
            error.show(getSupportFragmentManager(), "error");
        }


        if (mIsMapCollapsed) {
            mCloseMapButton.setVisibility(View.GONE);
        }
        mDataFetcher = new DataFetcher(this);

        mDefaultMapPadding = getResources().getDimensionPixelSize(R.dimen.default_map_padding);
        mDefaultHeaderHeight = Float.valueOf(UiUtil.getAttrValueInPx(this, R.attr.map_collapsePreferredItemHeight)).intValue();
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mAnimListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mIsMapCollapsed) {
                    mCloseMapButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mIsMapCollapsed) {
                    mCloseMapButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        View chartView = findViewById(R.id.chart_bottom_sheet);
        mChartBehavior = BottomSheetBehavior.from(chartView);
        mChartBehavior.setSkipCollapsed(true);
        if (savedInstanceState==null){
            mChartBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void expandMap(boolean animate) {
        mIsMapCollapsed = false;
        setExpandMapPadding();
        setContentViewHeight(animate);
        if (mMap != null) {
            UiSettings settings = mMap.getUiSettings();
            settings.setZoomControlsEnabled(true);
            settings.setZoomGesturesEnabled(true);
            settings.setAllGesturesEnabled(true);

        }
        OnActivityInteractionListener listener = getActiveListener();
        if (listener != null) {
            setMapBounds(listener.getRates());
        }
    }

    @Override
    public void collapseMap(boolean animate) {
        mIsMapCollapsed = true;
        setCollapseMapPadding();
        setContentViewHeight(animate);
        if (mMap != null) {
            UiSettings settings = mMap.getUiSettings();
            settings.setZoomControlsEnabled(false);
            settings.setZoomGesturesEnabled(false);
            settings.setAllGesturesEnabled(false);
            settings.setCompassEnabled(false);
            settings.setMapToolbarEnabled(false);
            settings.setIndoorLevelPickerEnabled(false);
        }
        OnActivityInteractionListener listener = getActiveListener();
        if (listener != null) {
            setMapBounds(listener.getRates());
        }
    }

    private void setContentViewHeight(boolean animate) {
        float translationValue = 0;
        if (mIsLandscape) {
            if (!mIsMapCollapsed) {
                translationValue = mPager.getWidth();
            }
            if (animate){
                ObjectAnimator animator = ObjectAnimator.ofFloat(mPager, "translationX", -translationValue);
                animator.addListener(mAnimListener);
                animator.start();
            } else {
                mPager.setTranslationX(translationValue);
                if (mIsMapCollapsed) {
                    mCloseMapButton.setVisibility(View.GONE);
                } else {
                    mCloseMapButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (!mIsMapCollapsed) {
                translationValue = mContentHeight;
            }
            if (animate) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mPager, "translationY", translationValue);
                animator.addListener(mAnimListener);
                animator.start();
            } else {
                mPager.setTranslationY(translationValue);
                if (mIsMapCollapsed) {
                    mCloseMapButton.setVisibility(View.GONE);
                } else {
                    mCloseMapButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void showMarkers(RateListType type, List<Rate> rates, double bestRate, double worthRate) {
        if (mSelectedTabPosition == type.getIndex()) {
            Log.d(TAG, "showMarkers() called with " + "type = [" + type + "], rates = [" + rates.size() + "], bestRate = [" + bestRate + "], worthRate = [" + worthRate + "]");
            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }
            if (mMap != null && !rates.isEmpty()) {
                setMapBounds(rates);
                mRateRenderer.setRateListType(type);
                mRateRenderer.setBestRateValue(bestRate);
                mRateRenderer.setWorthRateValue(worthRate);
                mClusterManager.addItems(rates);
                mClusterManager.cluster();
                drawMyPosition();
            }
        }
    }


    private void drawMyPosition() {
        if (mMap != null && mSearchCriteria.location != null && mSearchCriteria.location.isAutoLocation()) {
            if (mMyLocationMarker != null) {
                mMyLocationMarker.remove();
            }
            mMyLocationMarker = mMap.addMarker(new MarkerOptions().position(mSearchCriteria.location.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
        }
    }


    private void setMapBounds(List<Rate> rates) {
        //LogUtil.d(TAG, "Set map bounds");
        if (mMap != null && !rates.isEmpty()) {
            if (rates.size() == 1) {
                Rate rate = rates.get(0);
                LatLng loc = rate.getPosition();
                updateMapCameraPosition(CameraUpdateFactory.newLatLngZoom(loc, 18f), true);
            } else {
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (Rate rate : rates) {
                    if (rate.getOrganization() != null) {
                        LatLng loc = rate.getPosition();
                        builder.include(loc);
                    }
                }
                final LatLngBounds bounds = builder.build();
                updateMapCameraPosition(CameraUpdateFactory.newLatLngBounds(bounds, 10), true);
            }
        }
    }

    private void updateMapCameraPosition(final CameraUpdate update, final boolean animate) {
        if (mMap != null) {

            try {
                if (animate) {
                    mMap.animateCamera(update);
                } else {
                    mMap.moveCamera(update);
                }
            } catch (IllegalStateException e) {
                // layout not yet initialized
                final View mapView = mMapFragment.getView();
                if (mapView!=null && mapView.getViewTreeObserver().isAlive()) {
                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @SuppressWarnings("deprecation")
                        @SuppressLint("NewApi")
                        // We check which build version we are using.
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            if (animate) {
                                mMap.animateCamera(update);
                            } else {
                                mMap.moveCamera(update);
                            }

                        }
                    });
                }
            }
        }
    }

    private void setCollapseMapPadding() {
        if (mMap != null) {
            if (mIsLandscape){
                int paddingLeft = getActiveListener().getListViewWidth();
                mMap.setPadding(paddingLeft, mDefaultMapPadding, mDefaultMapPadding, mDefaultMapPadding);
            } else {
                int paddingBottom = mContentContainer.getHeight() - mDefaultHeaderHeight;
                mMap.setPadding(mDefaultMapPadding, mDefaultMapPadding, mDefaultMapPadding, paddingBottom);
            }
            mMapFragment.getView().invalidate();
            mClusterManager.cluster();
        }
    }

    private void setExpandMapPadding() {
        if (mMap != null) {
            mMap.setPadding(mDefaultMapPadding, mDefaultMapPadding, mDefaultMapPadding, mDefaultMapPadding);
            mMapFragment.getView().invalidate();
            mClusterManager.cluster();
        }
    }

    private OnActivityInteractionListener getActiveListener() {
        return  getActiveListener(mSelectedTabPosition);
    }

    private OnActivityInteractionListener getActiveListener(int position) {
        switch (position) {
            case TAB_ASK:
                return mAskRateListener;
            case TAB_BID:
                return mBidRateListener;
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            Log.e(TAG, "onMapReady ", e);
        }
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(false);
        settings.setAllGesturesEnabled(false);


        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Rate>() {
            @Override
            public boolean onClusterItemClick(Rate rate) {
                startDetailsActivity(rate);
                return false;
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mRateRenderer = new RateRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(mRateRenderer);
        if (!mIsFromSavedState) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.755890, 32.368598), 5.0f)); // ukraine
        }
        switch (mSelectedTabPosition) {
            case TAB_ASK:
                mRateRenderer.setRateListType(RateListType.ASK);
                if (mAskRateListener != null) {
                    showMarkers(RateListType.ASK, mAskRateListener.getRates(), mAskRateListener.getBestRate(), mAskRateListener.getWorthRate());
                }
                break;
            case TAB_BID:
                mRateRenderer.setRateListType(RateListType.BID);
                if (mBidRateListener != null) {
                    showMarkers(RateListType.BID, mBidRateListener.getRates(), mBidRateListener.getBestRate(), mBidRateListener.getWorthRate());
                }
                break;
        }
        mContentHeight = mContentContainer.getHeight();
        if (mContentHeight == 0) {
            mContentContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                @SuppressWarnings("deprecation")
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mContentContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mContentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mContentHeight = mContentContainer.getHeight();
                    if (mIsMapCollapsed) {
                        collapseMap(false);
                    } else {
                        expandMap(false);
                    }
                }
            });
        } else {
            if (mIsMapCollapsed) {
                collapseMap(false);
            } else {
                expandMap(false);
            }
        }
    }

    private void startDetailsActivity(Rate item) {
        Intent details = new Intent(this, DetailsActivity.class);
        Bundle args = new Bundle();
        args.putParcelable("org", item);
        args.putParcelable("location", mSearchCriteria.location);
        int selectedTab = mTabLayout.getSelectedTabPosition();
        args.putInt("type", selectedTab);
        switch (selectedTab) {
            case TAB_ASK:
                args.putDouble("bestRate", mAskRateListener.getBestRate());
                args.putDouble("worthRate", mAskRateListener.getWorthRate());
                break;
            case TAB_BID:
                args.putDouble("bestRate", mBidRateListener.getBestRate());
                args.putDouble("worthRate", mBidRateListener.getWorthRate());
                break;
        }
        details.putExtras(args);
        startActivity(details);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SORT, mSearchCriteria.sort.name());
        outState.putParcelable(KEY_LOCATION, mSearchCriteria.location);
        outState.putParcelable(KEY_CURRENCY, mSearchCriteria.currency);
        outState.putInt(KEY_SELECTED_TAB_POSITION, mSelectedTabPosition);
        outState.putBoolean(KEY_IS_MAP_COLLAPSED, mIsMapCollapsed);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.d(TAG, "Received response for Location permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted, preview can be displayed
                Log.d(TAG, "Location permission has now been granted. Showing preview.");
                if (mCurrencies.isEmpty()){
                    mDataFetcher.getDicts();
                }
                //mMainDataFetcher.start(mCurrencies.isEmpty());
            } else {
                android.util.Log.d(TAG, "Location permission was NOT granted.");
                Snackbar.make(mContentContainer, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int isPlayServicesActive = availability.isGooglePlayServicesAvailable(this);
        if (isPlayServicesActive != ConnectionResult.SUCCESS && availability.isUserResolvableError(isPlayServicesActive)) {
            Settings.INSTANCE.setGooglePlayServicesAvailable(false);
            Dialog errorDialog = availability.getErrorDialog(this, isPlayServicesActive, 0);
            if (errorDialog != null && !BuildConfig.DEBUG) {
                errorDialog.show();
            }
            return;
        } else {
            Settings.INSTANCE.setGooglePlayServicesAvailable(true);
        }
        notifyFilterChangeListeners(false);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataFetcher.stop();
    }



    /*@SuppressWarnings("unchecked")
    public void onDataLoaded(@NonNull Map<String, Object> data) {
        Log.d(TAG, "onDataLoaded() called with " + "data = [" + data + "]");
        if (data != null && !data.isEmpty()) {
            List<Currency> currencies = (List<Currency>) data.get(MainDataFetcher.KEY_CURRENCIES);
            if (!currencies.isEmpty()) {
                mCurrencies.clear();
                mCurrencies.addAll(currencies);
                mCurrencyAdapter.notifyDataSetChanged();
                String saveCurrencyCode = Settings.INSTANCE.getSelectedCurrency();
                if (saveCurrencyCode != null) {
                    for (int i = 0; i < mCurrencies.size(); i++) {
                        if (mCurrencies.get(i).code.equals(saveCurrencyCode)) {
                            mSearchCriteria.currency = mCurrencies.get(i);
                            if (mCurrencySpinner != null) {
                                mCurrencySpinner.setSelection(i);
                            }
                            break;
                        }
                    }
                } else {
                    Settings.INSTANCE.setSelectedCurrency(mCurrencies.get(0).code);
                    mSearchCriteria.currency = mCurrencies.get(0);
                }
            }
        }
        mSearchCriteria.location = (LocationWrapper) data.get(MainDataFetcher.KEY_LOCATION);
        notifyFilterChangeListeners(false);
    }*/


    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), getString(R.string.tab_buy_title), getString(R.string.tab_sale_title));
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mSelectedTabPosition = position;
                OnActivityInteractionListener listener = getActiveListener(position);
                if (listener!=null){
                    showMarkers(listener.getType(), listener.getRates(), listener.getBestRate(), listener.getWorthRate());
                }
            }
        });
        mTabLayout.setupWithViewPager(mPager);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem itemCurrency = menu.findItem(R.id.menu_currency);
        MenuItemCompat.setActionView(itemCurrency, mCurrencySpinner);
        mCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mCurrencyAdapter.getItem(position).equals(mSearchCriteria.currency)) {
                    mSearchCriteria.currency = mCurrencyAdapter.getItem(position);
                    Settings.INSTANCE.setSelectedCurrency(mSearchCriteria.currency.code);
                    notifyFilterChangeListeners(false);
                    Log.d(TAG, "Currency selected: " + mSearchCriteria.currency);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsAutoLocation) {
            menu.findItem(R.id.menu_action_sort).setEnabled(true);
            Drawable iconRate = DrawableCompat.wrap(menu.findItem(R.id.menu_action_sort_rate).getIcon());
            Drawable iconDist = DrawableCompat.wrap(menu.findItem(R.id.menu_action_sort_distance).getIcon());
            switch (mSearchCriteria.sort) {
                case RATE:
                    DrawableCompat.setTint(iconRate, ContextCompat.getColor(this, R.color.primary));
                    DrawableCompat.setTint(iconDist, ContextCompat.getColor(this, R.color.inactive));
                    return true;
                case DISTANCE:
                    DrawableCompat.setTint(iconDist, ContextCompat.getColor(this, R.color.primary));
                    DrawableCompat.setTint(iconRate, ContextCompat.getColor(this, R.color.inactive));
                    return true;
            }
        } else {
            menu.findItem(R.id.menu_action_sort).setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_currency:
                Log.d(TAG, "Menu Currency selected");
                return true;
            case R.id.menu_action_chart:
                Log.d(TAG, "Menu Chart selected");
                showChart();
                return true;
            case R.id.menu_action_sort_rate:
                if (mSearchCriteria.sort != SortType.RATE) {
                    mSearchCriteria.sort = SortType.RATE;
                    notifyFilterChangeListeners(false);
                }
                supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_action_sort_distance:
                if (mSearchCriteria.sort != SortType.DISTANCE) {
                    mSearchCriteria.sort = SortType.DISTANCE;
                    notifyFilterChangeListeners(false);
                }
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChart() {
        Log.d(TAG, "showChart() called");
        mDataFetcher.getCurrencyStats();
        mChartBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SETTINGS) {
            Settings.INSTANCE.loadFromPrefs();
            mSearchCriteria.radius = Settings.INSTANCE.getRadius();
            mSearchCriteria.itemsPerPage = Settings.INSTANCE.getListCount();
            mDataFetcher.getLocation();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


    private void notifyFilterChangeListeners(boolean forceLoad) {
        Log.d(TAG, "notifyFilterChangeListeners");
        if (mAskRateListener != null) {
            mAskRateListener.onFilterChanged(mSearchCriteria, forceLoad);
        }
        if (mBidRateListener != null) {
            mBidRateListener.onFilterChanged(mSearchCriteria, forceLoad);
        }
    }



    private void requestLocationPermission(){
        Log.i(TAG, "Requesting location permission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            android.util.Log.d(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mContentContainer, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_LOCATION_PERMISSION);
                        }
                    })
                    .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    private static class CurrencyArrayAdapter extends ArrayAdapter<Currency> {
        public CurrencyArrayAdapter(Context context, List<Currency> elements) {
            super(context, 0, elements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_actionbar_item, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Currency curr = getItem(position);
            holder.text1.setText(curr.code);
            holder.text2.setText(curr.name);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_actionbar_dropdown_item, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Currency curr = getItem(position);
            holder.text1.setText(curr.code);
            holder.text2.setText(curr.name);
            return convertView;
        }

        private class ViewHolder {
            TextView text1;
            TextView text2;
        }


    }

    @Override
    public void attachFilterChangeListener(OnActivityInteractionListener listener) {
        Log.d(TAG, "attachFilterChangeListener " + listener.getType());
        switch (listener.getType()) {
            case ASK:
                mAskRateListener = listener;
                break;
            case BID:
                mBidRateListener = listener;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mAskRateListener != null && mBidRateListener != null) {
                //mMainDataFetcher.start(mCurrencies.isEmpty());
                if (mCurrencies.isEmpty()){
                    mDataFetcher.getDicts();
                }
            }
        } else {
            if (mAskRateListener != null && mBidRateListener != null) {
                requestLocationPermission();
            }
        }
    }

    @Override
    public void onDictStared() {
        Log.d(TAG, "Dict loading started");
    }

    @Override
    public void onDictLoaded(List<Currency> currencies) {
        Log.d(TAG, "Got " + currencies.size() + " currencies");
        mCurrencies.clear();
        mCurrencies.addAll(currencies);
        mCurrencyAdapter.notifyDataSetChanged();
        String saveCurrencyCode = Settings.INSTANCE.getSelectedCurrency();
        if (saveCurrencyCode != null) {
            for (int i = 0; i < mCurrencies.size(); i++) {
                if (mCurrencies.get(i).code.equals(saveCurrencyCode)) {
                    mSearchCriteria.currency = mCurrencies.get(i);
                    if (mCurrencySpinner != null) {
                        mCurrencySpinner.setSelection(i);
                    }
                    break;
                }
            }
        } else {
            Settings.INSTANCE.setSelectedCurrency(mCurrencies.get(0).code);
            mSearchCriteria.currency = mCurrencies.get(0);
        }
        mDataFetcher.getLocation();
    }

    @Override
    public void onLocationStarted() {
        Log.d(TAG, "Retrieving Location");
    }

    @Override
    public void onLocationRetrieved(LocationWrapper location) {
        mSearchCriteria.location = location;
        notifyFilterChangeListeners(false);
    }

    @Override
    public void detachFilterChangeListener(OnActivityInteractionListener listener) {
        Log.d(TAG, "detachFilterChangeListener " + listener.getType());
        switch (listener.getType()) {
            case ASK:
                mAskRateListener = null;
                break;
            case BID:
                mBidRateListener = null;
        }
    }

    @Override
    public void onItemSelected(Rate item) {
        Log.d(TAG, "onItemSelected. Rate item selected: " + item.getOrganization().getTitle());
    }
}
