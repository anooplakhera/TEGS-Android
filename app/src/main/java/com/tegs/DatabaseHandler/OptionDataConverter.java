package com.tegs.DatabaseHandler;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * Created by heena on 1/3/18.
 */

public class OptionDataConverter {
    @TypeConverter
    public static List<OptionData> fromString(String value) {
        Type listType = new TypeToken<List<OptionData>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(List<OptionData> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
