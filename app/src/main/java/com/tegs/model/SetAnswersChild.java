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

public class SetAnswersChild implements Serializable {
    @SerializedName("other")
    @Expose
    private String other;
    @SerializedName("answer_id")
    @Expose
    private List<Integer> answerId = new ArrayList<>();
    @SerializedName("question_id")
    @Expose
    private int questionId;

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public List<Integer> getAnswerId() {
        return answerId;
    }

    public void setAnswerId(List<Integer> answerId) {
        this.answerId = answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
