package com.tegs.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawSparePartQuotesBinding;
import com.tegs.model.GetCommonResponse;
import com.tegs.model.GetSparePartListResponse.Datum;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by heena on 28/12/17.
 */

public class SparePartQuotesAdapter extends RecyclerView.Adapter<SparePartQuotesAdapter.MyViewHolder> {
    private List<Datum> getData = new ArrayList<>();
    private int REQ_SPAREPART = 22;
    private onClickView onClickView;
    private int rowId;
    private Activity activity;

    public SparePartQuotesAdapter(Activity activity, List<Datum> list) {
        this.getData = list;
        this.activity = activity;
    }

    public void addList(List<Datum> list) {
        if (list != null) {
            this.getData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clearList() {
        this.getData.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.getData.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawSparePartQuotesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_spare_part_quotes, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Datum spareData = getData.get(position);
        holder.bind(spareData);
    }

    @Override
    public int getItemCount() {
        return getData == null ? 0 : getData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawSparePartQuotesBinding binding;

        public MyViewHolder(RawSparePartQuotesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final Datum getListData) {
            binding.setData(getListData);
            binding.executePendingBindings();
            binding.lnrSpareQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowId = getListData.getId();
                    onClickView.onClickView(rowId);
                    AppLog.d("Adapter", String.valueOf(rowId));
                }
            });

            binding.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callDeleteSpare(getListData.id, getAdapterPosition());
                }
            });
        }
    }

    public void setOnClickView(onClickView onClickView) {
        this.onClickView = onClickView;
    }

    public interface onClickView {
        void onClickView(int rowId);
    }

    private void callDeleteSpare(int spareId, final int position) {
        Utils.showProgressDialog(activity);
        Call<GetCommonResponse> call = RestClient.getInstance(true).getApiInterface().DeleteSpareAns(Utils.getRequestMap(true),spareId);
        RestClient.makeApiRequest(activity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                Utils.dismissDialog();
                GetCommonResponse result = (GetCommonResponse) response;
                Utils.showSnackBar(activity, result.getMessage());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteItem(position);
                    }
                }, 500);
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.d("TAG", "onApiError");
            }
        });
    }
}
