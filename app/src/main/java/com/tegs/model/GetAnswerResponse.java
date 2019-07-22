package com.tegs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heena on 20/2/18.
 */

public class GetAnswerResponse {
    @SerializedName("data")
    @Expose
    private GetAnswerData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;

    public GetAnswerData getData() {
        return data;
    }

    public void setData(GetAnswerData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public class GetAnswerData {
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("answers")
        @Expose
        private List<GetAnswerDataChild> answers = null;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("id")
        @Expose
        private Integer id;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public List<GetAnswerDataChild> getAnswers() {
            return answers;
        }

        public void setAnswers(List<GetAnswerDataChild> answers) {
            this.answers = answers;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public class GetAnswerDataChild {
        @SerializedName("answer_id")
        @Expose
        private List<Integer> answerId = null;
        @SerializedName("other")
        @Expose
        private String other;
        @SerializedName("question_id")
        @Expose
        private Integer questionId;

        public List<Integer> getAnswerId() {
            return answerId;
        }

        public void setAnswerId(List<Integer> answerId) {
            this.answerId = answerId;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

    }
}
