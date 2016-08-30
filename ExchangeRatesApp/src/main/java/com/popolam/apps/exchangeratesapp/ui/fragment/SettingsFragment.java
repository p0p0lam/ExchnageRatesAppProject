package com.popolam.apps.exchangeratesapp.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.popolam.apps.exchangeratesapp.App;
import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.Settings;
import com.popolam.apps.exchangeratesapp.model.City;
import com.popolam.apps.exchangeratesapp.ui.widget.SeekBarPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements LoaderManager.LoaderCallbacks<List<City>> {

    private List<City> cities = new ArrayList<>();

    private ListPreference mManLocPreference;
    private CheckBoxPreference mAutoLocPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Settings.PREFS_NAME);
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.preference);
        mAutoLocPreference = (CheckBoxPreference) findPreference("location");
        mAutoLocPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        mManLocPreference = (android.support.v7.preference.ListPreference) findPreference("manual_location");
        mManLocPreference.setEntries(new String[]{});
        mManLocPreference.setEntryValues(new String[]{});
        mManLocPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        SeekBarPreference radiusPref = (SeekBarPreference) findPreference("radius");
        radiusPref.setSummary(String.valueOf(radiusPref.getProgress()) + getString(R.string.radius_measure));
        radiusPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        SeekBarPreference listCountPref = (SeekBarPreference) findPreference("list_count");
        listCountPref.setSummary(String.valueOf(listCountPref.getProgress()));
        listCountPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }



    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof CheckBoxPreference && "location".equals(preference.getKey())){
                Boolean val = (Boolean) newValue;
                if (!val){
                    if (mManLocPreference.getValue()==null && !cities.isEmpty()){
                        City city  = cities.get(0);
                        mManLocPreference.setValue(city.getCode());
                        mManLocPreference.setSummary(city.getName());
                        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                        editor.putString("city_name", city.getName());
                        editor.putString("manual_location", city.getCode());
                        editor.apply();
                    }
                }
            }
            if (preference instanceof ListPreference && preference.getKey().equals("manual_location")){
                City city = findCityByCode((String) newValue);
                preference.setSummary(city==null?null:city.getName());
                getPreferenceManager().getSharedPreferences().edit().putString("city_name", city.getName()).apply();
            }
            if (preference instanceof SeekBarPreference && preference.getKey().equals("radius")){
                preference.setSummary(String.valueOf(newValue)  + getString(R.string.radius_measure));
            }
            if (preference instanceof SeekBarPreference && preference.getKey().equals("list_count")){
                preference.setSummary(String.valueOf(newValue));
            }
            return true;
        }
    };

    private City findCityByCode(String code){
        for (City city : cities) {
            if (city.getCode().equals(code)){
                return city;
            }
        }
        return null;
    }



    @Override
    public Loader<List<City>> onCreateLoader(int id, Bundle args) {
        return new CitiesLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<City>> loader, List<City> data) {
        if (data!=null){
            cities.clear();
            cities.addAll(data);
            Collections.sort(cities, new Comparator<City>() {
                @Override
                public int compare(City lhs, City rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            String[] entries = new String[cities.size()];
            String[] values = new String[cities.size()];
            for (int i=0; i<cities.size(); i++){
                entries[i] = cities.get(i).getName();
                values[i] = cities.get(i).getCode();
            }
            mManLocPreference.setEntries(entries);
            mManLocPreference.setEntryValues(values);
            if (mManLocPreference.getValue()!=null){
                City city = findCityByCode(mManLocPreference.getValue());
                mManLocPreference.setSummary(city==null?null:city.getName());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<City>> loader) {

    }

    private static class CitiesLoader extends AsyncTaskLoader<List<City>> {
        public CitiesLoader(Context context) {
            super(context);
        }

        @Override
        public List<City> loadInBackground() {
            return App.getInstance().getDatabaseHelper().getCities();
        }
    }
}
