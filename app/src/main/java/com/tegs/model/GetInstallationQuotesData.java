package com.tegs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tegs.DatabaseHandler.SetAnswerEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by heena on 28/12/17.
 */

public class GetInstallationQuotesData implements Serializable {
    @SerializedName("data")
    @Expose

    private List<SetAnswerEntity> data = new ArrayList<>();
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private int status;

    public List<SetAnswerEntity> getData() {
        return data;
    }

    public void setData(List<SetAnswerEntity> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
