package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  Created by heena on 1/3/18.
 */

public class QuestionsArray {

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "instruction")
    private String instruction;

    @ColumnInfo(name = "image")
    private String image;

    @TypeConverters({QuesChildArrayConverters.class})
    private List<QuesChildArray> Questions = new ArrayList<>();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<QuesChildArray> getQuestions() {
        return Questions;
    }

    public void setQuestions(List<QuesChildArray> questions) {
        Questions = questions;
    }
}
