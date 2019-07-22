package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;

/**
 *
 * Created by heena on 1/3/18.
 */

public class QuesData {

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "media_type")
    private String media_type;

    @ColumnInfo(name = "question_id")
    private int question_id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }
}
