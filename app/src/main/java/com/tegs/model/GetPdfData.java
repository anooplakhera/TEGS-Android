package com.tegs.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 29/12/17.
 */

public class GetPdfData {

    private List<PdfChildData> data = new ArrayList<>();

    public List<PdfChildData> getData() {
        return data;
    }

    public void setData(List<PdfChildData> data) {
        this.data = data;
    }
}
