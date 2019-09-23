package com.tegs.fragment;


import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tegs.R;
import com.tegs.activities.PlayVideoActivity;
import com.tegs.activities.VideoAndPhotoActivity;
import com.tegs.adapters.VideoGridFragAdapter;
import com.tegs.adapters.VideoLinearFragAdapter;
import com.tegs.model.GetVideosData;
import com.tegs.model.VideoChildDataList;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 1/1/18.
 */

public class VideoFragment extends Fragment {
    private RecyclerView rvLinearVideo;
    private RecyclerView rvGridVideo;
    private CardView cardViewGrid;
    private List<VideoChildDataList> dataList = new ArrayList<>();
    private VideoLinearFragAdapter linearAdapter;
    private VideoGridFragAdapter gridFragAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_videos_linear_view, container, false);
        viewInLinear(view);
        showLinear();
        return view;
    }

    private void viewInLinear(View view) {
        rvLinearVideo = view.findViewById(R.id.recViewLinearVideo);
        rvGridVideo = view.findViewById(R.id.recViewGridVideos);
        cardViewGrid = view.findViewById(R.id.cardViewGridVideos);

        try {
            //Storing json to Model Class named GetPdfData
            GetVideosData getVideosData = new Gson().fromJson(((VideoAndPhotoActivity) getActivity()).loadJSONFromAsset(Constants.VIDEO_JSON), GetVideosData.class);
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
                dataList.add(list);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        //Recycler View With Layout Linear Layout
        rvLinearVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        linearAdapter = new VideoLinearFragAdapter(getActivity(), dataList);
        rvLinearVideo.setAdapter(linearAdapter);
        startLinearVideo();

        //Recycler View With Layout grid
        rvGridVideo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        gridFragAdapter = new VideoGridFragAdapter(getActivity(), dataList);
        rvGridVideo.setAdapter(gridFragAdapter);
        startGridVideo();
    }

    public void getFilteredData() {
        //Setting Data as per Filtered Data. ***Send List of Data as per Category
        List<VideoChildDataList> filterList = getFilteredAsCategory(((VideoAndPhotoActivity) getActivity()).loadJSONFromAsset(Constants.VIDEO_JSON), ((VideoAndPhotoActivity) getActivity()).categoryName);
        if (filterList.size() != 0) {
            linearAdapter = new VideoLinearFragAdapter(getActivity(), new ArrayList<VideoChildDataList>());
            linearAdapter.clearList();
            linearAdapter.addList(filterList);
            rvLinearVideo.setAdapter(linearAdapter);
            startLinearVideo();

            gridFragAdapter = new VideoGridFragAdapter(getActivity(), new ArrayList<VideoChildDataList>());
            gridFragAdapter.clearList();
            gridFragAdapter.addList(filterList);
            rvGridVideo.setAdapter(gridFragAdapter);
            startGridVideo();
        }
    }

    public void searchedData(String searchingString) {
        List<VideoChildDataList> filteredList = new ArrayList<VideoChildDataList>();
        for (VideoChildDataList d : dataList) {
            if (d.getTitle().toLowerCase().contains(searchingString.toLowerCase())) {
                filteredList.add(d);
            }
        }
        //update recyclerview
        linearAdapter = new VideoLinearFragAdapter(getActivity(), new ArrayList<VideoChildDataList>());
        linearAdapter.clearList();
        linearAdapter.addList(filteredList);
        rvLinearVideo.setAdapter(linearAdapter);
        startLinearVideo();

        gridFragAdapter = new VideoGridFragAdapter(getActivity(), new ArrayList<VideoChildDataList>());
        gridFragAdapter.clearList();
        gridFragAdapter.addList(filteredList);
        rvGridVideo.setAdapter(gridFragAdapter);
        startGridVideo();
    }

    private void startLinearVideo() {
        //Handling video click view event
        linearAdapter.setOnClickView(new VideoLinearFragAdapter.onVideoClickView() {
            @Override
            public void onVideoClickView(String url, String videoTitle) {
                boolean result = Utils.checkPermission(getActivity());
                if (result) {
                    if (url != null) {
                        Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                        intent.putExtra(Constants.VIDEO_URL, url);
                        intent.putExtra(Constants.VIDEO_TITLE, videoTitle);
                        startActivity(intent);
                    }
                }
            }
        });
        //Handling shareClick Event
        linearAdapter.setOnShareView(new VideoLinearFragAdapter.onShareClick() {
            @Override
            public void onShareClickView(String url, Boolean linkedVideos) {
                boolean result = Utils.checkPermission(getActivity());
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                if (result) {
                    if (linkedVideos) {
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
                        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_video)));
                    } else {
                        sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("video/mp4");
                        File file = accessAssetsVideoFile(url);
                        Uri uri = Uri.fromFile(file);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_video)));
                    }
                }
            }
        });
    }

    private File accessAssetsVideoFile(String docVideoUrl) {
        if (docVideoUrl != null) {
            AssetManager assetManager = getActivity().getAssets();
            try {
                //video is folder where all videos are stored under assets
                InputStream in = assetManager.open("video/" + docVideoUrl + ".mp4");
                OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docVideoUrl + ".mp4");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
        //Fetching File From Local and set it to play
        File fetchFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docVideoUrl + ".mp4");
        return fetchFile;
    }

    private void startGridVideo() {
        gridFragAdapter.setonClickListener(new VideoGridFragAdapter.onClickView() {
            @Override
            public void onVideoClickView(String url, String videoTitle) {
                boolean result = Utils.checkPermission(getActivity());
                if (result) {
                    if (url != null) {
                        Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                        intent.putExtra(Constants.VIDEO_URL, url);
                        intent.putExtra(Constants.VIDEO_TITLE, videoTitle);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public List<VideoChildDataList> getFilteredAsCategory(String jsonArray, String category) {
        List<VideoChildDataList> setFilterList = new ArrayList<>();
        if (category != null) {
            GetVideosData brochuresData = new Gson().fromJson(jsonArray, GetVideosData.class);
            List<VideoChildDataList> pdfChildDataList = brochuresData.getData();
            if (category.equals("Everything")) {
                for (VideoChildDataList s : pdfChildDataList) {
                    setFilterList.add(s);
                }
            } else {
                for (VideoChildDataList s : pdfChildDataList) {
                    if (s.getCatetory().equals(category)) {
                        setFilterList.add(s);
                    }
                }
            }
        }
        return setFilterList;
    }

    public void showLinear() {
        //Visibilities
        rvLinearVideo.setVisibility(View.VISIBLE);
        cardViewGrid.setVisibility(View.GONE);
    }

    public void showGrid() {
        //Visibilities
        rvLinearVideo.setVisibility(View.GONE);
        cardViewGrid.setVisibility(View.VISIBLE);
    }
}
