package com.tegs.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tegs.DatabaseHandler.SetAnsChildEntity;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel;
import com.tegs.R;
import com.tegs.databinding.RawViewEditQuesBinding;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 24/2/18.
 */

public class EditQuesAdapter extends RecyclerView.Adapter<EditQuesAdapter.MyViewHolder> {
    private List<SetAnsChildEntity> getData = new ArrayList<>();
    private onClickView onClickView;
    private Context context;

    public EditQuesAdapter(Context context, List<SetAnsChildEntity> getData) {
        this.context = context;
        this.getData = getData;
    }

    public void addList(List<SetAnsChildEntity> list) {
        if (list != null) {
            this.getData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clearList() {
        this.getData.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RawViewEditQuesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_view_edit_ques, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SetAnsChildEntity quotesData = getData.get(position);
        holder.bind(quotesData, position);
    }

    @Override
    public int getItemCount() {
        return getData == null ? 0 : getData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RawViewEditQuesBinding binding;

        public MyViewHolder(RawViewEditQuesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(final SetAnsChildEntity getListData, int position) {
            binding.imgQuesEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int questionID = getListData.getQuestion_id();
                    List<Integer> answerID = getListData.getAnswerId();
                    String others = getListData.getOther();
                    onClickView.onClickView(questionID, answerID, others);
                }
            });

            int quesNo = position + 1;
            String quesValue = context.getString(R.string.question, quesNo);
            binding.txtQuestionNo.setText(quesValue);

            //Fetching from Local Stored Ques Ans Model
            QuestionAnswerModel questionAnswerModel = Utils.getQuestionAnswerData();

            //Size of Full Main Ques Ans Model Size.
            int quesModelSize = questionAnswerModel.getData().size();
            //Remove all views from recycler views to add for next.

            binding.lnrAnswerValue.removeAllViews();
            for (int questionModel = 0; questionModel < quesModelSize; questionModel++) {

                for (int ques = 0; ques < questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().size(); ques++) {

                    int fetchQuesID = questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();

                    if (getListData.getQuestion_id() != 0) {

                        if (fetchQuesID == getListData.getQuestion_id()) {

                            String questionText = questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().get(ques).getQuestion().getText();
                            binding.txtQuestionValue.setText(questionText);

                            if (getListData.getAnswerId().size() != 0) {
                                for (int fetchAns = 0; fetchAns < questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().get(ques).getOptions().size(); fetchAns++) {
                                    int fetchAnsID = questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().get(ques).getOptions().get(fetchAns).getAnswerId();
                                    AppLog.d("UserSelectedAnsID", String.valueOf(fetchAnsID));
                                    for (int size = 0; size < getListData.getAnswerId().size(); size++) {
                                        if (fetchAnsID == getListData.getAnswerId().get(size)) {
                                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            TextView textView = (TextView) inflater.inflate(R.layout.view_textview, null, false);
                                            textView.setText(questionAnswerModel.getData().get(questionModel).getQuestions().getQuestions().get(ques).getOptions().get(fetchAns).getText());
                                            binding.lnrAnswerValue.addView(textView);
                                        }
                                    }
                                }
                            } else {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                TextView textView = (TextView) inflater.inflate(R.layout.view_textview, null, false);
                                textView.setText(getListData.getOther());
                                binding.lnrAnswerValue.addView(textView);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOnEditView(onClickView onClickView) {
        this.onClickView = onClickView;
    }

    public interface onClickView {
        void onClickView(int quesID, List<Integer> answerID, String others);
    }
}
