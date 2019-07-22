package com.tegs.DatabaseHandler;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by heena on 22/2/18.
 */
@Database(entities = {SetAnswerEntity.class, QuesAnsModelEntity.class, SpareDataEntity.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DAOSetAnswers setAnswersDAO();

    public abstract DAOQuesAnsModel daoQuesAnsModel();

    public abstract DaoSpareData setSpareDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
