package com.tegs.DatabaseHandler;

import android.arch.persistence.room.TypeConverters;

import com.tegs.utils.AppLog;

import java.util.List;

/**
 * Created
 * by heena on 26/2/18.
 */

public class DBSetAnsMethodSync {

    public static void insertData(AppDatabase db, SetAnswerEntity setAnswerEntity) {
        db.setAnswersDAO().insert(setAnswerEntity);
        AppLog.d("Inserting", "Successfully");
    }

    public static List<SetAnswerEntity> fetchData(AppDatabase db) {
        List<SetAnswerEntity> fetchanswers = db.setAnswersDAO().fetchAllData();
        AppLog.d("DatabaseData", "Rows Count: " + fetchanswers.size());
        return fetchanswers;
    }

    public static SetAnswerEntity fetchAt(AppDatabase db, int row_id) {
        SetAnswerEntity fetchanswer = db.setAnswersDAO().fetchAt(row_id);
        return fetchanswer;
    }

    public static void deleteDataAt(AppDatabase db, int row_id) {
        db.setAnswersDAO().deleteAt(row_id);
        AppLog.d("Deleteing At row_id =" + row_id, "deleted successfully");
    }

    public static void deleteAll(AppDatabase db) {
        db.setAnswersDAO().deleteAll();
        AppLog.d("Deleted", "all successfully");
    }

    public static int updateAll(AppDatabase db, SetAnswerEntity setAnswerEntity) {
        db.setAnswersDAO().updateAt(setAnswerEntity);
        AppLog.d("Updated", "successfully");
        return 1;
    }
}
