package com.tegs.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.R;
import com.tegs.databinding.RawInstallationQuotesBinding;
import com.tegs.model.GetCommonResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.util.List;

import retrofit2.Call;

/**
 * Created by heena on 28/12/17.
 */

public class InstallationQuotesAdapter extends RecyclerView.Adapter<InstallationQuotesAdapter.MyViewHolder> {
    private List<SetAnswerEntity> getData;
    private onClickQues onClickQues;
    private Activity activity;

    public InstallationQuotesAdapter(Activity activity, List<SetAnswerEntity> getData) {
        this.getData = getData;
        this.activity = activity;
    }

    public void addList(List<SetAnswerEntity> list) {
        if (list != null) {
            this.getData.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void notifyAdapter(int position) {
        this.getData.remove(position);
        this.notifyDataSetChanged();
    }

    public void clearList() {
        this.getData.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawInstallationQuotesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_installation_quotes, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SetAnswerEntity quoteData = getData.get(position);
        holder.bind(quoteData, position);
    }

    @Override
    public int getItemCount() {
        return getData == null ? 0 : getData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawInstallationQuotesBinding binding;

        public MyViewHolder(RawInstallationQuotesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final SetAnswerEntity getData, final int position) {
            binding.txtDate.setText(Utils.convertDateFormat("yyyy-MM-dd", "dd MMMM yyyy", getData.getDate()));
            binding.txtName.setText(getData.getTitle());
            if (getData.getStatus() != null) {
                binding.txtSent.setText(getData.getStatus());
                binding.lnrInstallQuotes.setBackgroundResource(R.color.colorLightGray);
                binding.txtSent.setBackgroundResource(R.drawable.rounded_corner_dark_grey_with_effect);

                //If user click's on pending button then checks if internet is connected then call webservice and subumit it
                binding.txtSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int rowID = getData.getRow_id();
                        if (rowID != 0) {
                            AppLog.d("Local Data Row_ID", String.valueOf(rowID));
                            onClickQues.onButtonClick(rowID,position);
                        }
                    }
                });


            } else {
                binding.lnrInstallQuotes.setBackgroundResource(R.color.colorWhite);
                binding.txtSent.setBackgroundResource(R.drawable.rounded_corner_coloraccent);
                binding.txtSent.setClickable(false);
                binding.txtSent.setText(R.string.btn_sent_new);
            }


            binding.lnrInstallQuotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quoteID = getData.getId();
                    if (quoteID == 0) {
                        int rowID = getData.getRow_id();
                        AppLog.d("RowID", String.valueOf(rowID));
                        onClickQues.onClickQues(rowID, Constants.PENDINGSTATUS);
                    } else {
                        int quesQuoteID = getData.getId();
                        AppLog.d("QuoteID", String.valueOf(quesQuoteID));
                        onClickQues.onClickQues(quesQuoteID, Constants.ONLINESTATUS);
                    }
                }
            });

            binding.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    deleteItem(position);
                    callDeleteQuote(getData.getId(), getAdapterPosition());

                }
            });
        }
    }

    public void deleteItem(int position) {
        this.getData.remove(position);
        notifyDataSetChanged();
    }

    public void setOnClickQues(onClickQues onClickQues) {
        this.onClickQues = onClickQues;
    }

    public interface onClickQues {
        void onClickQues(int quoteId, String status);

        void onButtonClick(int rowID,int pos);
    }

    private void callDeleteQuote(int quoteID, final int position) {
        Utils.showProgressDialog(activity);
        Call<GetCommonResponse> call = RestClient.getInstance(true).getApiInterface().DeleteAns(Utils.getRequestMap(true),quoteID);
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
