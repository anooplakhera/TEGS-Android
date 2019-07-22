package com.tegs.adapters;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawBrochuresBinding;
import com.tegs.model.PdfChildData;

import java.io.File;
import java.util.List;

/**
 * Created
 * by heena on 29/12/17.
 */

public class BrochuresAdapter extends RecyclerView.Adapter<BrochuresAdapter.MyViewHolder> {
    private List<PdfChildData> getBrochuresData;
    private onBrochureView onBrochureView;
    private onImageShareClick onImageShareClickView;


    public BrochuresAdapter(List<PdfChildData> getBrochuresData) {
        this.getBrochuresData = getBrochuresData;
    }

    public void addList(List<PdfChildData> list) {
        if (list != null) {
            this.getBrochuresData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clearList() {
        this.getBrochuresData.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawBrochuresBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_brochures, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PdfChildData getData = getBrochuresData.get(position);
        holder.bind(getData);
    }

    @Override
    public int getItemCount() {
        return getBrochuresData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawBrochuresBinding binding;

        public MyViewHolder(RawBrochuresBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final PdfChildData getBrochuresData) {

            binding.txtTitle.setText(getBrochuresData.getTitle());
            binding.txtDate.setText(getBrochuresData.getDate());
            binding.txtBrochureContent.setText(getBrochuresData.getDesc());
            if (getBrochuresData.getImagePath() != null) {
                File imgFile = new File(getBrochuresData.getImagePath());
                binding.sdvPdfImage.setImageURI(Uri.fromFile(imgFile));
            }
            binding.cardViewBrochures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getBrochuresData.getDoc().equals("")) {
                        onBrochureView.onBrochureView(getBrochuresData.getDoc());
                    }
                }
            });

            binding.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!getBrochuresData.getDoc().equals("")) {
                        onImageShareClickView.onImageShareClickView(getBrochuresData.getDoc());
                    }
                }
            });
        }

    }

    public void setOnBrochuresView(onBrochureView onBrochuresView) {
        this.onBrochureView = onBrochuresView;
    }

    public void setOnImageClickView(onImageShareClick clickView) {
        this.onImageShareClickView = clickView;
    }

    public interface onBrochureView {
        void onBrochureView(String docPdfName);
    }

    public interface onImageShareClick {
        void onImageShareClickView(String url);
    }
}
