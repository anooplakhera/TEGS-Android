package com.tegs.DatabaseHandler;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * Created by heena on 22/2/18.
 */
public class Converters {
    @TypeConverter
    public static List<SetAnsChildEntity> fromString(String value) {
        Type listType = new TypeToken<List<SetAnsChildEntity>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(List<SetAnsChildEntity> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
