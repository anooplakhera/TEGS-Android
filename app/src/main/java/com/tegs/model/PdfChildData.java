package com.tegs.model;

/**
 * Created
 * by heena on 8/3/18.
 */

public class PdfChildData {

    private String title;

    private String addr;

    private String desc;

    private String doc;

    private String catetory;

    private String type;

    private String date;

    private String imagePath;

    public PdfChildData(){

    }

    public PdfChildData(String title, String addr, String desc, String doc, String category, String type, String date,String imagePath) {
        this.title = title;
        this.addr = addr;
        this.desc = desc;
        this.doc = doc;
        this.catetory = category;
        this.type = type;
        this.date = date;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getCatetory() {
        return catetory;
    }

    public void setCatetory(String catetory) {
        this.catetory = catetory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
