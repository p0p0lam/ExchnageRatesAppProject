package com.popolam.apps.exchangeratesapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.popolam.apps.exchangeratesapp.App;
import com.popolam.apps.exchangeratesapp.R;
import com.popolam.apps.exchangeratesapp.model.City;
import com.popolam.apps.exchangeratesapp.model.Currency;
import com.popolam.apps.exchangeratesapp.util.IOUtil;
import com.popolam.apps.exchangeratesapp.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by serhiy.plekhov on 25.11.13.
 */

public class ExRateDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = ExRateDatabaseHelper.class.getSimpleName();
    public static final String DB_NAME = "exrateDb";
    public static final int DB_VERSION = 5;

    public ExRateDatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate ");
        createTables(db);
        populateDefCurrencies(db);
    }

    void createTables(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + Currency.TABLE_NAME);
        db.execSQL("CREATE TABLE " + Currency.TABLE_NAME + " (" + Currency._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Currency.COL_CODE + " TEXT NOT NULL UNIQUE, "
                + Currency.COL_NAME + " TEXT NOT NULL "
                + ")");
        db.execSQL("DROP TABLE IF EXISTS " + City.TABLE_NAME);
        db.execSQL("CREATE TABLE " + City.TABLE_NAME + " (" + City._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + City.COL_CODE + " TEXT NOT NULL, "
                + City.COL_NAME + " TEXT NOT NULL "
                + ")");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() called with oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
        onCreate(db);
    }

    private void populateDefCurrencies(SQLiteDatabase db){
        String[] codes = App.getInstance().getResources().getStringArray(R.array.curr_codes);
        String[] names = App.getInstance().getResources().getStringArray(R.array.curr_names);
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < codes.length; i++) {
            sql.append("insert into currency(code, name) values (");
            sql.append("'").append(codes[i]).append("',").append("'").append(names[i]).append("'); ");
            db.execSQL(sql.toString());
            sql.setLength(0);
        }
    }

    public List<Currency> getCurrencies(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(Currency.TABLE_NAME, null, null, null, null, null, Currency._ID);
            if (cursor!=null && cursor.moveToFirst()){
                List<Currency> result = new ArrayList<>(cursor.getCount());
                do {
                    Currency currency = getCurrencyFromCursor(cursor);
                    result.add(currency);
                } while (cursor.moveToNext());
                return result;
            }
        } finally {
            IOUtil.safelyClose(cursor);
        }
        return Collections.emptyList();
    }

    public void saveCities(@NonNull List<City> data){
        Log.d(TAG, "saveCities start");
        SQLiteDatabase db = getWritableDatabase();
        String sqlInsert = "INSERT INTO "+ City.TABLE_NAME +"("+ City.COL_NAME +"," + City.COL_CODE + ") " + " VALUES (?,?);";
        SQLiteStatement insertStatement = db.compileStatement(sqlInsert);
        String sqlUpdate = "UPDATE " + City.TABLE_NAME +" SET " + City.COL_NAME + "=? where " + City.COL_CODE + "=?";
        SQLiteStatement updateStatement = db.compileStatement(sqlUpdate);
        try {
            db.beginTransaction();
            for (City city : data) {
                updateStatement.clearBindings();
                String[] args = new String[]{city.getName(), city.getCode()};
                updateStatement.bindAllArgsAsStrings(args);
                int count = updateStatement.executeUpdateDelete();
                if (count==0){
                    insertStatement.clearBindings();
                    insertStatement.bindAllArgsAsStrings(args);
                    insertStatement.execute();
                    //Log.d(TAG, "saveCities inserted " + city.getName());
                } else {
                    //Log.d(TAG, "saveCities update " + city.getName());
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.e(TAG, "saveCities throws exception", e);
        } finally {
            db.endTransaction();
        }
        Log.d(TAG, "saveCities end");
    }

    public void saveCurrencies(@NonNull List<Currency> data){
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "saveCurrencies start");
        String sqlInsert = "INSERT INTO "+ Currency.TABLE_NAME +"("+ Currency.COL_NAME +"," + Currency.COL_CODE + ") " + " VALUES (?,?);";
        SQLiteStatement insertStatement = db.compileStatement(sqlInsert);
        String sqlUpdate = "UPDATE " + Currency.TABLE_NAME +" SET " + Currency.COL_NAME + "=? where " + Currency.COL_CODE + "=?";
        SQLiteStatement updateStatement = db.compileStatement(sqlUpdate);
        try {
            db.beginTransaction();
            for (Currency currency : data) {
                updateStatement.clearBindings();
                String[] args = new String[]{currency.getName(), currency.getCode()};
                updateStatement.bindAllArgsAsStrings(args);
                int count = updateStatement.executeUpdateDelete();
                if (count==0){
                    insertStatement.clearBindings();
                    insertStatement.bindAllArgsAsStrings(args);
                    insertStatement.execute();
                    //Log.d(TAG, "saveCurrencies inserted " + currency.getName());
                } else {
                    //Log.d(TAG, "saveCurrencies update " + currency.getName());
                }
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "saveCurrencies end");
        } catch (Exception e){
            Log.e(TAG, "saveCities throws exception", e);
        } finally {
            db.endTransaction();
        }
    }


    public List<City> getCities(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(City.TABLE_NAME, null, null, null, null, null, Currency._ID);
            if (cursor!=null && cursor.moveToFirst()){
                List<City> result = new ArrayList<>(cursor.getCount());
                do {
                    City city = getCityFromCursor(cursor);
                    result.add(city);
                } while (cursor.moveToNext());
                return result;
            }
        } finally {
            IOUtil.safelyClose(cursor);
        }
        return Collections.emptyList();
    }

    @NonNull
    public Currency getCurrencyFromCursor(Cursor cursor) {
        return new Currency(cursor.getLong(cursor.getColumnIndexOrThrow(Currency._ID))
                                , cursor.getString(cursor.getColumnIndexOrThrow(Currency.COL_CODE))
                                , cursor.getString(cursor.getColumnIndexOrThrow(Currency.COL_NAME)));
    }
    @NonNull
    public City getCityFromCursor(Cursor cursor) {
        return new City(cursor.getLong(cursor.getColumnIndexOrThrow(City._ID))
                                , cursor.getString(cursor.getColumnIndexOrThrow(City.COL_CODE))
                                , cursor.getString(cursor.getColumnIndexOrThrow(City.COL_NAME)));
    }
}
