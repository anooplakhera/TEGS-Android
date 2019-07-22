package com.tegs.model;


/**
 * Created by heena on 3/1/18.
 */

public class GetDataSheetsAndManuals  {
    public GetDataSheetsAndManuals(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }

    private String title;
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

}
