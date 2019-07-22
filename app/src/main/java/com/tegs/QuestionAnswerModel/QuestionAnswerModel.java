package com.tegs.QuestionAnswerModel;

import com.tegs.model.GetQuestionsListResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by heena on 29/1/18.
 */

public class QuestionAnswerModel implements Serializable{

    private List<Datum> data = new ArrayList<>();

    private String message;

    private int status;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
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


        private int id;

        private GetQuestionsListResponse.Questions questions;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public GetQuestionsListResponse.Questions getQuestions() {
            return questions;
        }

        public void setQuestions(GetQuestionsListResponse.Questions questions) {
            this.questions = questions;
        }
    }

    public class Question  implements Serializable {

        private GetQuestionsListResponse.Question_ question;

        private List<GetQuestionsListResponse.Option> options = new ArrayList<>();

        private String isRequired;

        private String questionType;

        private String answerType;

        public GetQuestionsListResponse.Question_ getQuestion() {
            return question;
        }

        public void setQuestion(GetQuestionsListResponse.Question_ question) {
            this.question = question;
        }

        public List<GetQuestionsListResponse.Option> getOptions() {
            return options;
        }

        public void setOptions(List<GetQuestionsListResponse.Option> options) {
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

    public class Option {


        private String text;

        private String image;

        private String nextId;

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

    public class Question_ {


        private String text;


        private String link;

        private String mediaType;


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

    public class Questions {


        private String title;

        private String instruction;


        private String image;


        private List<GetQuestionsListResponse.Question> questions = new ArrayList<>();

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

        public List<GetQuestionsListResponse.Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<GetQuestionsListResponse.Question> questions) {
            this.questions = questions;
        }
    }
}
