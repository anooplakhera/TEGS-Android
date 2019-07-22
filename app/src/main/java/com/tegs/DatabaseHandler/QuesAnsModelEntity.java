package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 *
 *  Created by heena on 1/3/18.
 */

@Entity
public class QuesAnsModelEntity {

    @PrimaryKey(autoGenerate = true)
    private int row_id;

    @ColumnInfo(name = "id")
    private int id;

    @Embedded(prefix = "quesArray")
    private QuestionsArray questions;

    public QuestionsArray getQuestions() {
        return questions;
    }

    public void setQuestions(QuestionsArray questions) {
        this.questions = questions;
    }

    public int getRow_id() {
        return row_id;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
