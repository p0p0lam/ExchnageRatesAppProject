package com.popolam.apps.exchangeratesapp.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.network.model.Organization;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.ui.adapter.MainRatesAdapter;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;
import com.popolam.apps.exchangeratesapp.util.TextUtil;

/**
 * Created by user on 07.11.13.
 */
public class RateRenderer extends DefaultClusterRenderer<Rate> {
    private IconGenerator mIconGenerator;
    private IconGenerator mClusterIconGenerator;
    private Context mContext;
    private RateListType mType;
    private double mBestRateValue;
    private double mWorthRateValue;
    TextView mRateItemView;
    private float mDensity;
    private ShapeDrawable mColoredCircleBackground;

    public RateRenderer(Context context, GoogleMap map, ClusterManager<Rate> clusterManager) {
        this(context, map, clusterManager, null);
    }

    public RateRenderer(Context context, GoogleMap map, ClusterManager<Rate> clusterManager, RateListType type) {
        super(context, map, clusterManager);
        mContext = context;
        mDensity = context.getResources().getDisplayMetrics().density;
        mIconGenerator = new IconGenerator(context);
        mClusterIconGenerator = new IconGenerator(context);
        mClusterIconGenerator.setBackground(makeClusterBackground());
        mClusterIconGenerator.setContentView(makeSquareTextView(context));
        mClusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_ClusterIcon_TextAppearance);
        mType = type;
        mRateItemView = new TextView(mContext);
        mRateItemView.setGravity(Gravity.CENTER_VERTICAL);
        mRateItemView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mIconGenerator.setContentView(mRateItemView);
    }

    public void setRateListType(RateListType type){
        mType = type;
    }

    private LayerDrawable makeClusterBackground() {
        mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        mColoredCircleBackground.getPaint().setColor(mContext.getResources().getColor(R.color.comp_4));
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(0x80ffffff); // Transparent white.
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int) (mDensity * 3);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.amu_text);
        int twelveDpi = (int) (12 * mDensity);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }

    public void setBestRateValue(double val){
        this.mBestRateValue = val;
    }

    public void setWorthRateValue(double worthRateValue) {
        mWorthRateValue = worthRateValue;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 3;
    }

    @Override
    protected void onBeforeClusterItemRendered(Rate item, MarkerOptions markerOptions) {
        mRateItemView.setText(TextUtil.formatDouble(item.getRateByType(mType)));
        switch (item.getOrganization().getType()) {
            case Organization.TYPE_BANK:
                mRateItemView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_bank), null, null, null);
                break;
            case Organization.TYPE_FOP:
                mRateItemView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_fop), null, null, null);
                break;
        }
        double diffFromMax = item.getRateDiffByType(mBestRateValue, mWorthRateValue, mType);
        int color=0;
        if (diffFromMax < MainRatesAdapter.RATE_DIFF_THRESHOLD) {
            color = ContextCompat.getColor(mContext, R.color.primary);
            mIconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        } else {
            mIconGenerator.setStyle(IconGenerator.STYLE_DEFAULT);
            color = ContextCompat.getColor(mContext, R.color.text_supl);
        }
        Drawable background = DrawableCompat.wrap(ContextCompat.getDrawable(mContext, R.drawable.ic_marker_white).mutate());
        DrawableCompat.setTint(background, color);
        mIconGenerator.setBackground(background);
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getOrganization().getTitle()).snippet(item.getOrganization().getAddress());;
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Rate> cluster, MarkerOptions markerOptions) {
        int bucket = getBucket(cluster);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(mClusterIconGenerator.makeIcon(getClusterText(bucket)));
         markerOptions.icon(descriptor);
    }


}
