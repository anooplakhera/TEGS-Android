package com.tegs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tegs.R;
import com.tegs.activities.ImageDetailActivity;
import com.tegs.activities.VideoAndPhotoActivity;
import com.tegs.adapters.GalleryGridFragAdapter;
import com.tegs.model.GetVideosData;
import com.tegs.model.VideoChildDataList;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created
 * by heena on 1/1/18.
 */

public class GalleryFragment extends Fragment {
    private RecyclerView rvGridGallery;
    private GalleryGridFragAdapter gridFragAdapter;
    private List<VideoChildDataList> getGalleryResponses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gallery, container, false);
        findValues(view);
        return view;
    }

    private void findValues(View view) {
        rvGridGallery = view.findViewById(R.id.recViewGridGallery);

        try {
            //Storing json to Model Class named GetPdfData
            GetVideosData getVideosData = new Gson().fromJson(((VideoAndPhotoActivity) getActivity())
                    .loadJSONFromAsset(Constants.GALLERY_JSON), GetVideosData.class);
            for (int data = 0; data < getVideosData.getData().size(); data++) {
                VideoChildDataList list = new VideoChildDataList();
                list.setTitle(getVideosData.getData().get(data).getTitle());
                list.setAddr(getVideosData.getData().get(data).getAddr());
                list.setCatetory(getVideosData.getData().get(data).getCatetory());
                list.setDate(getVideosData.getData().get(data).getDate());
                list.setDesc(getVideosData.getData().get(data).getDesc());
                list.setDoc(getVideosData.getData().get(data).getDoc());
                list.setUrl(getVideosData.getData().get(data).getUrl());
                list.setType(getVideosData.getData().get(data).getType());
                list.setThumb(getVideosData.getData().get(data).getThumb());
                getGalleryResponses.add(list);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        //Recycler View With Layout grid
        rvGridGallery.setLayoutManager(new GridLayoutManager(getContext(), 3));
        gridFragAdapter = new GalleryGridFragAdapter(getActivity(),getGalleryResponses);
        rvGridGallery.setAdapter(gridFragAdapter);

        handleImageClick();
    }

    private void handleImageClick() {
        //Handling video click view event`
        gridFragAdapter.setOnClickView(new GalleryGridFragAdapter.onGalleryImageClickView() {
            @Override
            public void onGalleryImageClickView(String url, String imageTitle, String imageDesc) {
                boolean result = Utils.checkPermission(getActivity());
                if (result) {
                    Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                    intent.putExtra(Constants.IMAGE_URL, url);
                    intent.putExtra(Constants.IMAGE_TITLE, imageTitle);
                    intent.putExtra(Constants.IMAGE_DESC, imageDesc);
                    startActivity(intent);
                }
            }
        });
    }

    public void getFilteredData() {
        //Setting Data as per Filtered Data. ***Send List of Data as per Category
        List<VideoChildDataList> filterList = getFilteredAsCategory(((VideoAndPhotoActivity)
                getActivity()).loadJSONFromAsset(Constants.GALLERY_JSON), ((VideoAndPhotoActivity) getActivity()).categoryName);
        if (filterList.size() != 0) {
            gridFragAdapter = new GalleryGridFragAdapter(getActivity(),new ArrayList<VideoChildDataList>());
            gridFragAdapter.clearList();
            gridFragAdapter.addList(filterList);
            rvGridGallery.setAdapter(gridFragAdapter);
            handleImageClick();
        }
    }

    private List<VideoChildDataList> getFilteredAsCategory(String jsonArray, String category) {
        List<VideoChildDataList> setFilterList = new ArrayList<>();
        if (category != null) {
            GetVideosData brochuresData = new Gson().fromJson(jsonArray, GetVideosData.class);
            List<VideoChildDataList> pdfChildDataList = brochuresData.getData();

            for (VideoChildDataList s : pdfChildDataList) {
                if (s.getCatetory().equals(category)) {
                    setFilterList.add(s);
                }
            }
        }
        return setFilterList;
    }
}