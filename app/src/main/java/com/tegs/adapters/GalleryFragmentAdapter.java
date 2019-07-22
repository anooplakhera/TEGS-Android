package com.tegs.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tegs.R;
import com.tegs.model.GetGalleryResponse;

import java.util.List;

/**
 * Created by heena on 1/1/18.
 */

public class GalleryFragmentAdapter extends RecyclerView.Adapter<GalleryFragmentAdapter.MyViewHolder> {
    List<GetGalleryResponse> getGalleryData;

    public GalleryFragmentAdapter(List<GetGalleryResponse> getGalleryData) {
        this.getGalleryData = getGalleryData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_gallery_linear_view, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GetGalleryResponse getGalleryResponse = getGalleryData.get(position);
        holder.txtTitle.setText(getGalleryResponse.getTitle());
    }

    @Override
    public int getItemCount() {
        return getGalleryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
        }
    }
}
