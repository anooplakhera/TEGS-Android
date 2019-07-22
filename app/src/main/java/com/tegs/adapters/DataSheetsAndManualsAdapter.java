package com.tegs.adapters;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawDataSheetsManualsBinding;
import com.tegs.model.PdfChildData;

import java.io.File;
import java.util.List;

/**
 * Created
 * by heena on 3/1/18.
 */

public class DataSheetsAndManualsAdapter extends RecyclerView.Adapter<DataSheetsAndManualsAdapter.MyViewHolder> {
    private List<PdfChildData> pdfChildData;
    private onPdfClickView onPdfClickView;
    private onImageShareClick onImageShareClickView;

    public DataSheetsAndManualsAdapter(List<PdfChildData> getData) {
        this.pdfChildData = getData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawDataSheetsManualsBinding view = DataBindingUtil.inflate(layoutInflater, R.layout.raw_data_sheets_manuals, parent, false);
        return new MyViewHolder(view);
    }

    public void addList(List<PdfChildData> list) {
        if (list != null) {
            this.pdfChildData.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void clearList() {
        this.pdfChildData.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PdfChildData getData = pdfChildData.get(position);
        holder.bind(getData);
    }

    @Override
    public int getItemCount() {
        return pdfChildData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawDataSheetsManualsBinding binding;

        public MyViewHolder(RawDataSheetsManualsBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final PdfChildData pdfChildData) {
            binding.txtTitle.setText(pdfChildData.getTitle());
            binding.txtDate.setText(pdfChildData.getDate());
            binding.txtContent.setText(pdfChildData.getDesc());
            if (pdfChildData.getImagePath() != null) {
                File imgFile = new File(pdfChildData.getImagePath());
                binding.sdvPdfImg.setImageURI(Uri.fromFile(imgFile));
            }
            binding.cardViewDSManuals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!pdfChildData.getDoc().equals("")) {
                        onPdfClickView.onPdfClickView(pdfChildData.getDoc());
                    }
                }
            });
            binding.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!pdfChildData.getDoc().equals("")) {
                        onImageShareClickView.onImageShareClickView(pdfChildData.getDoc());
                    }
                }
            });
        }
    }

    public void setOnClickView(onPdfClickView clickView) {
        this.onPdfClickView = clickView;
    }

    public void setOnImageClickView(onImageShareClick clickView) {
        this.onImageShareClickView = clickView;
    }

    public interface onPdfClickView {
        void onPdfClickView(String docPdfName);
    }

    public interface onImageShareClick {
        void onImageShareClickView(String url);
    }
}
