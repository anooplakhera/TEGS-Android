package com.tegs.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawGalleryGridViewLayoutBinding;
import com.tegs.model.VideoChildDataList;

import java.util.List;

/**
 * Created by heena on 24/2/18.
 */

public class GalleryGridFragAdapter extends RecyclerView.Adapter<GalleryGridFragAdapter.MyViewHolder> {

    private List<VideoChildDataList> getGalleryData;
    private onGalleryImageClickView onGalleryImageClickView;
    private Activity activity;

    public GalleryGridFragAdapter(Activity activity, List<VideoChildDataList> getGalleryData) {
        this.getGalleryData = getGalleryData;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawGalleryGridViewLayoutBinding view = DataBindingUtil.inflate(layoutInflater, R.layout.raw_gallery_grid_view_layout, parent, false);
        return new GalleryGridFragAdapter.MyViewHolder(view);
    }

    public void addList(List<VideoChildDataList> list) {
        if (list != null) {
            this.getGalleryData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clearList() {
        this.getGalleryData.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VideoChildDataList getGalleryResponse = getGalleryData.get(position);
        holder.bind(getGalleryResponse);
    }

    @Override
    public int getItemCount() {
        return getGalleryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawGalleryGridViewLayoutBinding binding;

        public MyViewHolder(RawGalleryGridViewLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final VideoChildDataList getGalleryResponse) {
            int resID = itemView.getResources().getIdentifier(getImage(getGalleryResponse.getDoc()), "drawable", activity.getPackageName());
            binding.sdvThumbImage.setImageResource(resID);
            binding.sdvThumbImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGalleryImageClickView.onGalleryImageClickView(getGalleryResponse.getDoc(), getGalleryResponse.getTitle(), getGalleryResponse.getDesc());
                }
            });
        }
    }

    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }

    public interface onGalleryImageClickView {
        void onGalleryImageClickView(String url, String imageTitle, String imageDescription);
    }

    public void setOnClickView(onGalleryImageClickView clickView) {
        this.onGalleryImageClickView = clickView;
    }
}