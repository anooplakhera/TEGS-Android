package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 22/2/18.
 */
@Entity
public class SetAnswerEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int row_id;

    @ColumnInfo(name = "quoteid")
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "status")
    private String status;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "SetAnsChildEntity")
    private List<SetAnsChildEntity> answers = new ArrayList<>();

    public int getRow_id() {
        return row_id;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SetAnsChildEntity> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SetAnsChildEntity> answers) {
        this.answers = answers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
