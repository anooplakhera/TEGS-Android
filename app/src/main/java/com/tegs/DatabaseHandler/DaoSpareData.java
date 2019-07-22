package com.tegs.DatabaseHandler;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DaoSpareData {

    //Inserting Data
    @Insert
    void insert(List<SpareDataEntity> spareDataEntity);


    //Fetch all data
    @Query("select * from SpareDataEntity")
    List<SpareDataEntity> fetchAll();


    @Query("SELECT * FROM SpareDataEntity WHERE user_id IN (:userIds)")
    List<SpareDataEntity> fetchAllByIds(int userIds);

    //Clear all the data
    @Query("delete from SpareDataEntity")
    void deleteAll();

    @Query("SELECT COUNT(modal_name) FROM SpareDataEntity")
    int getCount();

//    //Fetch List at particular QuestionID
//    @Query("select * from SpareDataEntity where ques_id =:ques_id")
//    SpareDataEntity fetchAtQuesID(int ques_id);

    @Query("DELETE FROM SpareDataEntity WHERE row_id =:rowId")
    void deleteInfoRow(String rowId);


}
