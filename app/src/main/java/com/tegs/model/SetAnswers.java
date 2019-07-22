package com.tegs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by heena on 13/2/18.
 */

public class SetAnswers implements Serializable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("answers")
    @Expose
    private List<SetAnswersChild> answers = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public List<SetAnswersChild> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SetAnswersChild> answers) {
        this.answers = answers;
    }
}
