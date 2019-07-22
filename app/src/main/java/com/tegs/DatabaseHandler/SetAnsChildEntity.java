package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *  Created by heena on 22/2/18.
 */
public class SetAnsChildEntity implements Serializable {

    @ColumnInfo(name = "answer_id")
    private List<Integer> answer_id= new ArrayList<>();

    @ColumnInfo(name = "other")
    private String other;

    @ColumnInfo(name = "question_id")
    private int question_id;

    public List<Integer> getAnswerId() {
        return answer_id;
    }

    public void setAnswerId(List<Integer> answerId) {
        this.answer_id = answerId;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }
}
