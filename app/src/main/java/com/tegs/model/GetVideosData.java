package com.tegs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 9/3/18.
 */

public class GetVideosData {
    private List<VideoChildDataList> data = new ArrayList<>();

    public List<VideoChildDataList> getData() {
        return data;
    }

    public void setData(List<VideoChildDataList> data) {
        this.data = data;
    }
}
