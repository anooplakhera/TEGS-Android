package com.tegs.DatabaseHandler;

import com.tegs.utils.AppLog;

import java.util.List;

/**
 * Created by heena on 1/3/18.
 */

public class DBSpareMethodSync {

    public static void insertData(AppDatabase db, List<SpareDataEntity> datum) {
        db.setSpareDao().insert(datum);
        AppLog.d("Inserting", "Successfully");
    }

    public static List<SpareDataEntity> fetchData(AppDatabase db, int user_id) {
        List<SpareDataEntity> fetchanswers = db.setSpareDao().fetchAllByIds(user_id);
        AppLog.d("DatabaseData", "Rows Count: " + fetchanswers.size());
        return fetchanswers;
    }

    public static void deleteAll(AppDatabase db) {
        db.setSpareDao().deleteAll();
        AppLog.d("Deleted", "all successfully");
    }

    public static int getCount(AppDatabase db) {

        AppLog.d("Count ", "all successfully");
        return db.setSpareDao().getCount();
    }

//    public static SpareDataEntity fetchAtQuesID(AppDatabase db, int ques_id) {
//        SpareDataEntity quesAnsModel = db.setSpareDao().fetchAtQuesID(ques_id);
//        return quesAnsModel;
//    }

    public static void deleteInfoRow(AppDatabase db, String rawId) {
        db.setSpareDao().deleteInfoRow(rawId);
        AppLog.d("Deleted", "all successfully");
    }

}
