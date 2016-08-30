package com.popolam.apps.exchangeratesapp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.model.LocationWrapper;
import com.popolam.apps.exchangeratesapp.network.ApiNetworker;
import com.popolam.apps.exchangeratesapp.network.model.OrgRatesResponse;
import com.popolam.apps.exchangeratesapp.network.model.Organization;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.ui.adapter.MainRatesAdapter;
import com.popolam.apps.exchangeratesapp.ui.adapter.OrgRatesAdapter;
import com.popolam.apps.exchangeratesapp.ui.loader.BaseNetworkLoader;
import com.popolam.apps.exchangeratesapp.util.Log;
import com.popolam.apps.exchangeratesapp.util.TextUtil;
import com.popolam.apps.exchangeratesapp.util.location.LocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a org details view.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<OrgRatesResponse>{
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private Rate rate;
    private LocationWrapper mLocation;
    private TextView mNameTextView;
    private TextView mAddrTextView;
    private TextView mTelTextView;
    private TextView mWebTextView;
    private TextView mDistanceTextView;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private RateListType mType;

    private double mBestRate;
    private double mWorthRate;
    private List<OrgRatesResponse.Currency> mRates = new ArrayList<>();
    private OrgRatesAdapter mAdapter;
    private RecyclerView mOrgRatesList;
    private ProgressBar mLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rate = getArguments().getParcelable("org");
            mLocation = getArguments().getParcelable("location");
            mType = RateListType.fromIndex(getArguments().getInt("type"));
            mBestRate = getArguments().getDouble("bestRate");
            mWorthRate = getArguments().getDouble("worthRate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mNameTextView = (TextView) rootView.findViewById(R.id.rate_item_org_title);
        mAddrTextView = (TextView) rootView.findViewById(R.id.rate_item_org_address);
        mTelTextView = (TextView) rootView.findViewById(R.id.rate_item_org_tel);
        mWebTextView = (TextView) rootView.findViewById(R.id.rate_item_org_www);
        mDistanceTextView = (TextView) rootView.findViewById(R.id.rate_item_distance);
        mOrgRatesList = (RecyclerView) rootView.findViewById(R.id.org_rates_list);
        mOrgRatesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new OrgRatesAdapter(mRates);
        mOrgRatesList.setAdapter(mAdapter);
        mLoading = (ProgressBar) rootView.findViewById(R.id.org_rates_progress);
        //mOrgRatesListContainer = (FrameLayout) rootView.findViewById(R.id.org_rates_list_container);
        populateView();
        setUpMapIfNeeded();
        showProgress();
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    void showProgress(){
         mOrgRatesList.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
    }

    void showRates(){
        mOrgRatesList.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

    private void populateView() {
        if (rate != null && rate.getOrganization() != null) {
            final Organization org = rate.getOrganization();
            mNameTextView.setText(rate.getOrganization().getTitle());

            mTelTextView.setText(rate.getOrganization().getPhone());
            mTelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + rate.getOrganization().getPhone()));
                    startActivity(intent);
                }
            });
            mAddrTextView.setText(rate.getOrganization().getAddress());
            mAddrTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMap(org.getLatitude(), org.getLongitude());
                }
            });
            mWebTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(rate.getOrganization().getLink()));
                    startActivity(i);
                }
            });

            if (mLocation.isAutoLocation()) {
                float[] dist = new float[3];
                Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), org.getLatitude(), org.getLongitude(), dist);
                mDistanceTextView.setText(TextUtil.formatDistance(getActivity(), dist[0]));
            } else {
                mDistanceTextView.setVisibility(View.GONE);
            }
            Drawable drawable = null;
            switch (rate.getOrganization().getType()) {
                case Organization.TYPE_BANK:
                    drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_bank_large));
                    break;
                case Organization.TYPE_FOP:
                    drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_fop_large));
                    break;
            }
            if (drawable!=null) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.primary));
                mNameTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }


    private void openMap(double latidude, double longitude) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f (%s)", latidude, longitude, rate.getOrganization().getTitle());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    drawMyPosition();
                }
            });
        }
    }



    private void drawMyPosition() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        if (mMap != null && mLocation != null && mLocation.isAutoLocation()) {
            LatLng loc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
            builder.include(loc);
        }
        if (mMap != null && rate != null) {
            LatLng loc = rate.getPosition();
            LatLngBounds bounds = null;
            if (mLocation.isAutoLocation()) {
                builder.include(loc);
                bounds = builder.build();
            } else {
                bounds = LocationUtils.toBounds(loc, 500d);
            }
            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            updateMapCameraPosition(update, false);

            MarkerOptions markerOptions = new MarkerOptions().position(loc).title(rate.getOrganization().getTitle()).snippet(rate.getOrganization().getAddress());
            double diffFromMax = rate.getRateDiffByType(mBestRate, mWorthRate, mType);
            IconGenerator ig = new IconGenerator(getActivity());

            ImageView rateTv = new ImageView(getActivity());
            //TextView rateTv = new TextView(getActivity());

            switch (rate.getOrganization().getType()) {
                case Organization.TYPE_BANK:
                    rateTv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bank));


                    //rateTv.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_bank, null), null, null, null);
                    break;
                case Organization.TYPE_FOP:
                    rateTv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fop));
                    //rateTv.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_bank, null), null, null, null);
                    break;
            }
            int color = 0;
            if (diffFromMax < MainRatesAdapter.RATE_DIFF_THRESHOLD) {
                ig.setStyle(IconGenerator.STYLE_GREEN);
                color = ContextCompat.getColor(getContext(), R.color.primary);
                Drawable background = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_marker_white));
                DrawableCompat.setTint(background, color);
                ig.setBackground(background);
                //Bitmap bitmap = bif.makeIcon(TextUtil.formatDouble(rate.getRateByType(mType)));

                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(UiUtil.getHueFromColor(getResources().getColor(R.color.primary_1))));
            } else {
                ig.setStyle(IconGenerator.STYLE_DEFAULT);
                color = ContextCompat.getColor(getContext(), R.color.text_supl);
                Drawable background = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_marker_white));
                DrawableCompat.setTint(background, color);
                ig.setBackground(background);
            }

            ig.setContentView(rateTv);
            // fix to align icon in center
            FrameLayout view  = (FrameLayout) rateTv.getParent();
            if (view!=null){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                view.setLayoutParams(params);
                ViewParent parent = view.getParent();
                if (parent instanceof LinearLayout){
                    LinearLayout root = (LinearLayout) parent;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    root.setLayoutParams(layoutParams);
                }
            }

            Bitmap bitmap = ig.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

            final Marker orgMarker = mMap.addMarker(markerOptions);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.equals(orgMarker)) {
                        openMap(marker.getPosition().latitude, marker.getPosition().longitude);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    private void updateMapCameraPosition(final CameraUpdate update, final boolean animate) {
        if (mMap != null) {
            final View mapView = mMapFragment.getView();
            try {
                if (animate) {
                    mMap.animateCamera(update);
                } else {
                    mMap.moveCamera(update);
                }


            } catch (IllegalStateException e) {
                // layout not yet initialized

                if (mapView.getViewTreeObserver().isAlive()) {
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

    @Override
    public Loader<OrgRatesResponse> onCreateLoader(int id, Bundle args) {
        return new OrgRatesLoader(getContext(), rate.getOrganization().getId());
    }

    @Override
    public void onLoadFinished(Loader<OrgRatesResponse> loader, OrgRatesResponse data) {
        showRates();
        if (data!=null){
            if (data.isSuccess()){
                Log.d(TAG, "onLoadFinished. Got org rates");
                mRates.clear();
                mRates.addAll(data.rates);
                mAdapter.notifyDataSetChanged();
            } else {
                Snackbar.make(mOrgRatesList, getResources().getString(R.string.error_load_rate), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<OrgRatesResponse> loader) {
        mRates.clear();
        mAdapter.notifyDataSetChanged();
    }

    private static class OrgRatesLoader extends BaseNetworkLoader<OrgRatesResponse>{
        private static final String TAG = OrgRatesLoader.class.getSimpleName();
        private final ApiNetworker mApiNetworker;
        private final String mOrgId;
        public OrgRatesLoader(Context context, String orgId) {
            super(context);
            mApiNetworker = new ApiNetworker();
            mOrgId = orgId;
        }

        @Override
        public OrgRatesResponse loadInBackground() {
            OrgRatesResponse response =  mApiNetworker.getOrgRates(mOrgId);
            if (!response.isSuccess()){
                Log.d(TAG, "loadInBackground. Can't get rates. Error: " + response.getError());
                return null;
            }
            return response;
        }
    }

}
