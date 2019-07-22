package com.tegs.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawVideoLinearViewBinding;
import com.tegs.model.VideoChildDataList;
import com.tegs.utils.Utils;

import java.util.List;

/**
 * Created
 * by heena on 1/1/18.
 */

public class VideoLinearFragAdapter extends RecyclerView.Adapter<VideoLinearFragAdapter.MyViewHolder> {

    private List<VideoChildDataList> getVideo;
    private onVideoClickView onVideoClickView;
    private onShareClick onShareClick;
    private Activity activity;

    public VideoLinearFragAdapter(Activity activity, List<VideoChildDataList> getVideo) {
        this.getVideo = getVideo;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawVideoLinearViewBinding view = DataBindingUtil.inflate(layoutInflater, R.layout.raw_video_linear_view, parent, false);
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
        VideoChildDataList getVideoResponse = getVideo.get(position);
        holder.bind(getVideoResponse);
    }

    @Override
    public int getItemCount() {
        return getVideo.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawVideoLinearViewBinding binding;

        public MyViewHolder(RawVideoLinearViewBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final VideoChildDataList videoChildDataList) {
            binding.txtTitle.setText(videoChildDataList.getTitle());
            binding.txtDate.setText(videoChildDataList.getDate());
            binding.txtContent.setText(videoChildDataList.getDesc());
//            if (!videoChildDataList.getDoc().equals("")) {
//                binding.txtAuthor.setText(videoChildDataList.getDoc());
//            }

            //Setting Videos ThumbNail .
            if (videoChildDataList.getDoc().equals("")) {
                binding.sdvmediaPreview.setImageURI(videoChildDataList.getThumb());
            } else {
                int resID = itemView.getResources().getIdentifier(getImage(videoChildDataList.getThumb()), "drawable", activity.getPackageName());
                binding.sdvmediaPreview.setImageResource(resID);
            }
            //if video is not there and youtube link is added
            if (videoChildDataList.getDoc().equals("")) {
                binding.imgYoutubeLink.setVisibility(View.VISIBLE);
//                binding.imgYoutubeLink.setText(videoChildDataList.getUrl());
            } else {
                binding.imgYoutubeLink.setVisibility(View.GONE);
            }
            binding.cardViewVideoLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoChildDataList.getDoc().equals("")) {
                        Utils.watchYoutubeVideo(activity, videoChildDataList.getUrl());
                    } else {
                        onVideoClickView.onVideoClickView(videoChildDataList.getDoc(), videoChildDataList.getTitle());
                    }
                }
            });
            binding.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoChildDataList.getDoc().equals("")) {
                        onShareClick.onShareClickView(videoChildDataList.getUrl(), true);
                    } else {
                        onShareClick.onShareClickView(videoChildDataList.getDoc(), false);
                    }
                }
            });
        }
    }

    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }

    public void setOnClickView(onVideoClickView clickView) {
        this.onVideoClickView = clickView;
    }

    public interface onVideoClickView {
        void onVideoClickView(String url, String videoTitle);
    }

    public void setOnShareView(onShareClick shareClick) {
        this.onShareClick = shareClick;
    }

    public interface onShareClick {
        void onShareClickView(String url, Boolean linkedVideos);
    }
}
