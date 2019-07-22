package com.tegs.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawVideoGridViewBinding;
import com.tegs.model.VideoChildDataList;
import com.tegs.utils.Utils;

import java.util.List;

/**
 * Created
 * by heena on 24/2/18.
 */

public class VideoGridFragAdapter extends RecyclerView.Adapter<VideoGridFragAdapter.MyViewHolder> {
    private List<VideoChildDataList> getVideo;
    private onClickView onClickView;
    private Activity activity;

    public VideoGridFragAdapter(Activity activity, List<VideoChildDataList> getVideo) {
        this.getVideo = getVideo;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawVideoGridViewBinding view = DataBindingUtil.inflate(layoutInflater, R.layout.raw_video_grid_view, parent, false);
        return new MyViewHolder(view);
    }

    public void addList(List<VideoChildDataList> list) {
        if (list != null) {
            this.getVideo.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void clearList() {
        this.getVideo.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final VideoChildDataList getVideoResponse = getVideo.get(position);
        holder.bind(getVideoResponse);
    }

    @Override
    public int getItemCount() {
        return getVideo.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawVideoGridViewBinding binding;

        public MyViewHolder(RawVideoGridViewBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final VideoChildDataList videoChildDataList) {
            binding.txtTitle.setText(videoChildDataList.getTitle());
            if (videoChildDataList.getDoc().equals("")) {
                binding.sdvThumbImage.setImageURI(videoChildDataList.getThumb());
            }else{
                int resID = itemView.getResources().getIdentifier(getImage(videoChildDataList.getThumb()), "drawable", activity.getPackageName());
                binding.sdvThumbImage.setImageResource(resID);
            }

            //if video is not there and youtube link is added
            if (videoChildDataList.getDoc().equals("")) {
                binding.imgYoutubeLink.setVisibility(View.VISIBLE);
            } else {
                binding.imgYoutubeLink.setVisibility(View.GONE);
            }
            binding.cardviewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoChildDataList.getDoc().equals("")) {
                        Utils.watchYoutubeVideo(activity, videoChildDataList.getUrl());
                    } else {
                        onClickView.onVideoClickView(videoChildDataList.getDoc(), videoChildDataList.getTitle());
                    }
                }
            });
        }
    }

    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }

    public void setonClickListener(onClickView onClickView) {
        this.onClickView = onClickView;
    }

    public interface onClickView {
        void onVideoClickView(String url, String videoTitle);
    }
}
