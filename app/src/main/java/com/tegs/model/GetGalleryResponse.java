package com.tegs.model;

/**
 * Created by heena on 1/1/18.
 */

public class GetGalleryResponse {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;
    public GetGalleryResponse(String title){
        this.title = title;
    }
}
