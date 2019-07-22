package com.tegs.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tegs.R;
import com.tegs.databinding.RawInstantMessageBinding;
import com.tegs.model.GetInstantMessageResponse.Datum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 29/12/17.
 */

public class InstantMessageAdapter extends RecyclerView.Adapter<InstantMessageAdapter.MyViewHolder> {
    private List<Datum> getMsgData = new ArrayList<>();

    public InstantMessageAdapter(List<Datum> getData) {
        this.getMsgData = getData;
    }

    public void addList(List<Datum> list) {
        this.getMsgData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        this.getMsgData.clear();
        notifyDataSetChanged();
    }

    public void refreshNotifyData() {
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawInstantMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_instant_message, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Datum userMsg = getMsgData.get(position);
        holder.bind(userMsg);
    }

    @Override
    public int getItemCount() {
        return getMsgData == null ? 0 : getMsgData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawInstantMessageBinding binding;

        public MyViewHolder(RawInstantMessageBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        private void bind(Datum getData) {
            binding.setData(getData);
            binding.executePendingBindings();
        }
    }
}
