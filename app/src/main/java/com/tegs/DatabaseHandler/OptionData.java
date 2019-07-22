package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;

/**
 *
 *  Created by heena on 1/3/18.
 */

public class OptionData {

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "next_id")
    private String next_id;

    @ColumnInfo(name = "answer_id")
    private int answer_id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNext_id() {
        return next_id;
    }

    public void setNext_id(String next_id) {
        this.next_id = next_id;
    }

    public int getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }
}
