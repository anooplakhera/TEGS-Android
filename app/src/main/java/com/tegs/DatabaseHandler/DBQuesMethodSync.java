package com.tegs.DatabaseHandler;

import com.tegs.utils.AppLog;

import java.util.List;

/**
 * Created by heena on 1/3/18.
 */

public class DBQuesMethodSync {

    public static void insertData(AppDatabase db, QuesAnsModelEntity quesAnsModelEntity) {
        db.daoQuesAnsModel().insert(quesAnsModelEntity);
        AppLog.d("Inserting", "Successfully");
    }

    public static List<QuesAnsModelEntity> fetchData(AppDatabase db) {
        List<QuesAnsModelEntity> fetchanswers = db.daoQuesAnsModel().fetchAll();
        AppLog.d("DatabaseData", "Rows Count: " + fetchanswers.size());
        return fetchanswers;
    }

    public static void deleteAll(AppDatabase db) {
        db.daoQuesAnsModel().deleteAll();
        AppLog.d("Deleted", "all successfully");
    }

    public static QuesAnsModelEntity fetchAtQuesID(AppDatabase db, int ques_id) {
        QuesAnsModelEntity quesAnsModel = db.daoQuesAnsModel().fetchAtQuesID(ques_id);
        return quesAnsModel;
    }
}
