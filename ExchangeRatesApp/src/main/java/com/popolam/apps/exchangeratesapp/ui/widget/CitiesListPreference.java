package com.popolam.apps.exchangeratesapp.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Created by user on 07.11.13.
 */
public class CitiesListPreference extends ListPreference {
    public CitiesListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        super.onPrepareDialogBuilder(builder);
    }
}
