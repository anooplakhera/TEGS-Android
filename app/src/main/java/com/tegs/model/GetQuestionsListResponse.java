package com.tegs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tegs.DatabaseHandler.QuesAnsModelEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heena on 27/1/18.
 */

public class GetQuestionsListResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private List<QuesAnsModelEntity> data = new ArrayList<>();
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private int status;

    public List<QuesAnsModelEntity> getData() {
        return data;
    }

    public void setData(List<QuesAnsModelEntity> data) {
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

    public class Datum implements Serializable {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("questions")
        @Expose
        private Questions questions;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Questions getQuestions() {
            return questions;
        }

        public void setQuestions(Questions questions) {
            this.questions = questions;
        }
    }

    public class Question implements Serializable {
        @SerializedName("question")
        @Expose
        private Question_ question;
        @SerializedName("options")
        @Expose
        private List<Option> options = new ArrayList<>();
        @SerializedName("is_required")
        @Expose
        private String isRequired;
        @SerializedName("question_type")
        @Expose
        private String questionType;
        @SerializedName("answer_type")
        @Expose
        private String answerType;

        public Question_ getQuestion() {
            return question;
        }

        public void setQuestion(Question_ question) {
            this.question = question;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public String getIsRequired() {
            return isRequired;
        }

        public void setIsRequired(String isRequired) {
            this.isRequired = isRequired;
        }

        public String getQuestionType() {
            return questionType;
        }

        public void setQuestionType(String questionType) {
            this.questionType = questionType;
        }

        public String getAnswerType() {
            return answerType;
        }

        public void setAnswerType(String answerType) {
            this.answerType = answerType;
        }
    }

    public class Option implements Serializable {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("next_id")
        @Expose
        private String nextId;
        @SerializedName("answer_id")
        @Expose
        private int answerId;

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

        public String getNextId() {
            return nextId;
        }

        public void setNextId(String nextId) {
            this.nextId = nextId;
        }

        public int getAnswerId() {
            return answerId;
        }

        public void setAnswerId(int answerId) {
            this.answerId = answerId;
        }
    }

    public class Question_ implements Serializable {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("media_type")
        @Expose
        private String mediaType;
        @SerializedName("question_id")
        @Expose
        private int questionId;

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

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }
    }

    public class Questions implements Serializable {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("instruction")
        @Expose
        private String instruction;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("Questions")
        @Expose
        private List<Question> questions = new ArrayList<>();

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
            return image.toLowerCase();
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }
    }
}
