package com.popolam.apps.exchangeratesapp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import com.popolam.apps.exchangeratesapp.R;

/**
 * Project: ExchnageRatesAppProject
 * Created by Sergey on 26.02.2016.
 */
public class SeekBarPreference extends Preference {

    private static final int DEFAULT_MIN_PROGRESS = 2;
    private static final int DEFAULT_MAX_PROGRESS = 20;
    private static final int DEFAULT_PROGRESS = 5;

    private int mMinProgress;
    private int mMaxProgress;
    private int mProgress;
    private SeekBar mSeekBar;
    private TextView mSummary;
    private CharSequence mProgressTextSuffix;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // get attributes specified in XML
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, 0, 0);
        try
        {
            setMinProgress(a.getInteger(R.styleable.SeekBarPreference_min, DEFAULT_MIN_PROGRESS));
            setMaxProgress(a.getInteger(R.styleable.SeekBarPreference_max, DEFAULT_MAX_PROGRESS));
            mProgressTextSuffix = a.getString(R.styleable.SeekBarPreference_progressTextSuffix);
        }
        finally
        {
            a.recycle();
        }
        setLayoutResource(R.layout.preference_seekbar);

    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInt(index, DEFAULT_PROGRESS);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        setProgress(restore ? getPersistedInt(DEFAULT_PROGRESS) : (Integer) defaultValue);
        //mProgress = (restore ? getPersistedInt(DEFAULT_PROGRESS) : (Integer) defaultValue);

    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mSeekBar = (SeekBar) holder.findViewById(R.id.pref_seek_bar);
        mSummary = (TextView) holder.findViewById(android.R.id.summary);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // update text that displays the current SeekBar progress value
                // note: this does not persist the progress value. that is only ever done in setProgress()
                if (fromUser) {
                    setProgress(progress + mMinProgress);
                }
                String progressStr = String.valueOf(progress + mMinProgress);
                mSummary.setText(mProgressTextSuffix == null ? progressStr : progressStr.concat(mProgressTextSuffix.toString()));
            }
        });
        mSeekBar.setMax(mMaxProgress - mMinProgress);
        mSeekBar.setProgress(mProgress - mMinProgress);
    }



    public int getMinProgress()
    {
        return mMinProgress;
    }

    public void setMinProgress(int minProgress)
    {
        mMinProgress = minProgress;
        setProgress(Math.max(mProgress, mMinProgress));
    }

    public int getMaxProgress()
    {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress)
    {
        mMaxProgress = maxProgress;
        setProgress(Math.min(mProgress, mMaxProgress));
    }

    public int getProgress()
    {
        return mProgress;
    }

    public void setProgress(int progress)
    {
        progress = Math.max(Math.min(progress, mMaxProgress), mMinProgress);

        if (progress != mProgress)
        {
            mProgress = progress;
            if (persistInt(progress));
            {
                //notifyChanged();
            }
            //callChangeListener(mProgress);
        }
    }

}
