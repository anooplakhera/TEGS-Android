package com.tegs.DatabaseHandler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by heena on 1/3/18.
 */

public class QuesChildArray {

    @Embedded(prefix = "questionData")
    private QuesData question;

    @TypeConverters({OptionDataConverter.class})
    private List<OptionData> options = new ArrayList<>();

    @ColumnInfo(name = "is_required")
    private String is_required;

    @ColumnInfo(name = "question_type")
    private String question_type;

    @ColumnInfo(name = "answer_type")
    private String answer_type;

    public QuesData getQuestion() {
        return question;
    }

    public void setQuestion(QuesData question) {
        this.question = question;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void setOptions(List<OptionData> options) {
        this.options = options;
    }

    public String getIs_required() {
        return is_required;
    }

    public void setIs_required(String is_required) {
        this.is_required = is_required;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }
}
