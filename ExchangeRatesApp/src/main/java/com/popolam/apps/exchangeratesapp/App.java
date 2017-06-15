package com.popolam.apps.exchangeratesapp;

import android.app.Application;

import com.popolam.apps.exchangeratesapp.db.ExRateDatabaseHelper;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by user on 15.10.13.
 */
public class App extends Application {
    private static App instance;
    private final Lock dbHelperLock = new ReentrantLock();

    private ExRateDatabaseHelper mDbHelper;


    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ExRateDatabaseHelper getDatabaseHelper(){
        dbHelperLock.lock();
        try {
            if (mDbHelper == null) {
                mDbHelper = new ExRateDatabaseHelper(this);
            }
        } finally {
            dbHelperLock.unlock();
        }
        return mDbHelper;
    }

    public static App getInstance() {
        return instance;
    }




}
