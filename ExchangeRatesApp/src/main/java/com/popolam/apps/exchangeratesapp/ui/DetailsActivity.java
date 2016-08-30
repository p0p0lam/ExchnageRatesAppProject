package com.popolam.apps.exchangeratesapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.model.GetOrgRateResponse;
import com.popolam.apps.exchangeratesapp.network.model.Rate;
import com.popolam.apps.exchangeratesapp.ui.fragment.DetailsFragment;
import com.popolam.apps.exchangeratesapp.util.Log;
import com.popolam.apps.exchangeratesapp.util.TextUtil;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            DetailsFragment fr = new DetailsFragment();
            fr.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fr)
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class OrgRatesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<GetOrgRateResponse> {
        private String mOrgId;
        private OrgRatesAdapter mOrgRatesAdapter;
        private static final String TAG = Log.calculateTag(OrgRatesListFragment.class);

        public static OrgRatesListFragment newInstance(String orgId) {
            OrgRatesListFragment fr = new OrgRatesListFragment();
            Bundle args = new Bundle(1);
            args.putString("org", orgId);
            fr.setArguments(args);
            return fr;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_org_rate, getListView(), false);
            getListView().addHeaderView(header);
        }

        @Override
        public void onResume() {
            super.onResume();
            if (getArguments() != null) {
                mOrgId = getArguments().getString("org");
                getLoaderManager().initLoader(3, null, this);
            }
        }

        @Override
        public Loader<GetOrgRateResponse> onCreateLoader(int i, Bundle bundle) {
            return new OrgRatesLoader(getActivity(), mOrgId);
        }

        @Override
        public void onLoadFinished(Loader<GetOrgRateResponse> listLoader, GetOrgRateResponse data) {
            Log.d(TAG, "onLoadFinished");
            if (isAdded()) {
                if (!data.isError() && data.rates != null && !data.rates.isEmpty()) {
                    mOrgRatesAdapter = new OrgRatesAdapter(getActivity(), data.rates);
                    setListAdapter(mOrgRatesAdapter);
                    setListShown(true);

                } else {
                    setListAdapter(null);
                    if (data != null)
                        setEmptyText(data.error);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<GetOrgRateResponse> listLoader) {
            setListAdapter(null);
        }

        public static class OrgRatesLoader extends AsyncTaskLoader<GetOrgRateResponse> {
            private static final String TAG = Log.calculateTag(OrgRatesLoader.class);
            private String orgId;

            public OrgRatesLoader(Context context, String orgId) {
                super(context);
                this.orgId = orgId;
            }

            @Override
            public GetOrgRateResponse loadInBackground() {
                GetOrgRateResponse resp = new GetOrgRateResponse();
                Log.d(TAG, "loadInBackground");

                return resp;
            }

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading");
                forceLoad();
            }
        }


    }

    public static class OrgRatesAdapter extends ArrayAdapter<Rate> {
        LayoutInflater mInflater;

        public OrgRatesAdapter(Context context, List<Rate> rateList) {
            super(context, 0, rateList);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_org_rate, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.ask = (TextView) convertView.findViewById(R.id.rate_ask);
                holder.bid = (TextView) convertView.findViewById(R.id.rate_bid);
                holder.currency = (TextView) convertView.findViewById(R.id.rate_currency);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Rate item = getItem(position);
            holder.ask.setText(TextUtil.formatDouble(item.getAsk()));
            holder.bid.setText(TextUtil.formatDouble(item.getBid()));
            holder.currency.setText(item.getCurrencyCode());
            return convertView;
        }

        static class ViewHolder {
            TextView ask;
            TextView bid;
            TextView currency;
        }
    }

}
