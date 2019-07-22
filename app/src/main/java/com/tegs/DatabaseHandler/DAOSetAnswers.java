package com.tegs.DatabaseHandler;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created
 * by heena on 22/2/18.
 */
@Dao
public interface DAOSetAnswers {

    //Inserting Data
    @Insert
    void insert(SetAnswerEntity setAnswerEntity);

    //Fetching Whole Table
    @Query("Select * from SetAnswerEntity")
    List<SetAnswerEntity> fetchAllData();

    //Delete Whole Table
    @Query("delete from SetAnswerEntity")
    void deleteAll();

    //Fetch Data from Table at particular ID
    @Query("Select * from SetAnswerEntity where row_id = :rowid")
    SetAnswerEntity fetchAt(int rowid);

    //Delete Data from Table at particular ID
    @Query("delete from SetAnswerEntity where row_id = :row_id")
    void deleteAt(int row_id);

    //Update Table
//    @Query("UPDATE SetAnswerEntity SET SetAnsChildEntity = :answers where row_id = :row_id")
//    void updateAt(int row_id,String answers);

    @Update
    void updateAt(SetAnswerEntity setAnswerEntity);
}
