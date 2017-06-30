package com.popolam.apps.exchangeratesapp.network.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.popolam.apps.exchangeratesapp.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Project MOSST Code
 * Created by Serhiy Plekhov on 30.06.2017.
 */

public class DateTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(DateUtil.sdfShortISO.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        try {
            return DateUtil.sdfShortISO.parse(in.nextString());
        } catch (ParseException e){
            return null;
        }
    }
}
