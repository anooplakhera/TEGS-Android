package com.tegs.DatabaseHandler;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 *
 *  Created by heena on 1/3/18.
 */

@Dao
public interface DAOQuesAnsModel {

    //Inserting Data
    @Insert
    void insert(QuesAnsModelEntity quesAnsModelEntity);

    //Fetch all data
    @Query("select * from QuesAnsModelEntity")
    List<QuesAnsModelEntity> fetchAll();

    //Clear all the data
    @Query("delete from QuesAnsModelEntity")
    void deleteAll();

    //Fetch List at particular QuestionID
    @Query("select * from QuesAnsModelEntity where id =:ques_id")
    QuesAnsModelEntity fetchAtQuesID(int ques_id);
}
