package com.tegs.model;

/**
 * Created by heena on 28/12/17.
 */

public class GetSparePartQuotesData {
    private String title, date, company, address;

    public GetSparePartQuotesData(String title, String date, String company, String address) {
        this.title = title;
        this.date = date;
        this.company = company;
        this.address = address;
    }

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
