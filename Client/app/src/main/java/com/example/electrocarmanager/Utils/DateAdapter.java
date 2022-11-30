package com.example.electrocarmanager.Utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAdapter implements JsonDeserializer<Date> {
    //"EEE MMM dd HH:mm:ss zzz yyyy" -> Wed Nov 30 19:24:56 CST 2022
    private final DateFormat df = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss aa", Locale.ENGLISH);
    public Date deserialize(JsonElement arg0, Type arg1,
                            JsonDeserializationContext arg2) throws JsonParseException {
        try {
            Date date=df.parse(arg0.getAsString());
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
