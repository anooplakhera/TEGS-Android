package com.tegs.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tegs.DatabaseHandler.SetAnsChildEntity;
import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.R;
import com.tegs.adapters.EditQuesAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityViewEditQuesAnsBinding;
import com.tegs.model.GetInstallationQuotesData;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 24/2/18.
 */

public class ViewEditQuesAnsActivity extends BaseActivity {
    private ActivityViewEditQuesAnsBinding binding;
    private EditQuesAdapter adapter;
    private int REQ_UPDATE_SUCC = 101;
    private SetAnswerEntity result;
    private int quoteID;
    private int rowID;
    private List<SetAnsChildEntity> ansList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_edit_ques_ans);

        //Fetching Quote Id and Array of Answers from Installation Quotes Activity
        result = (SetAnswerEntity) getIntent().getSerializableExtra(Constants.ANS_ARRAY);
        quoteID = getIntent().getIntExtra(Constants.QUOTEID, 0); //Quote ID for API list
        rowID = getIntent().getIntExtra(Constants.ROWID, 0);
        setValues();
    }

    private void setValues() {
        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.quote_details));
        //Get Particular row Data from Previous Quotes Data
        Utils.showProgressDialog(this);
        //Whole Submitted Ques Ans List When internet is connected
        final GetInstallationQuotesData getFullData = (GetInstallationQuotesData) getIntent().getSerializableExtra(Constants.FULL_ANS_LIST);
        //Whole Submitted Ques Ans List PENDING_Ans_List from locally
        final GetInstallationQuotesData getPendingFullData = (GetInstallationQuotesData) getIntent().getSerializableExtra(Constants.PENDING_Ans_List);

        if (result != null) {
            Utils.dismissDialog();
            ansList = result.getAnswers();
            adapter = new EditQuesAdapter(this, new ArrayList<SetAnsChildEntity>());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewEditQuesAnsActivity.this);
            binding.recViewEditQues.setLayoutManager(linearLayoutManager);
            adapter.addList(ansList);
            binding.recViewEditQues.setAdapter(adapter);

            if (adapter.getItemCount() > 0) {
                Utils.dismissDialog();
                binding.txtEmptyView.setVisibility(View.GONE);
            } else {
                Utils.dismissDialog();
                binding.recViewEditQues.setVisibility(View.GONE);
                binding.txtEmptyView.setVisibility(View.VISIBLE);
            }
        }
        adapter.setOnEditView(new EditQuesAdapter.onClickView() {
            @Override
            public void onClickView(int quesID, List<Integer> answerID, String others) {
                Intent intent = new Intent(ViewEditQuesAnsActivity.this, UpdateQuoteActivity.class);
                intent.putExtra(Constants.EDIT_QUESID, quesID);
                intent.putIntegerArrayListExtra(Constants.EDIT_ANSID, (ArrayList<Integer>) answerID);
                intent.putExtra(Constants.EDIT_OTHERS, others);
                intent.putExtra(Constants.QUOTEID, quoteID);
                intent.putExtra(Constants.ROWID, rowID);
                intent.putExtra(Constants.FULL_ANS_LIST, getFullData);
                intent.putExtra(Constants.PENDING_Ans_List, getPendingFullData);
                startActivityForResult(intent, REQ_UPDATE_SUCC);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_UPDATE_SUCC) {
                List<SetAnsChildEntity> setAnswerEntities = (List<SetAnsChildEntity>) data.getSerializableExtra(Constants.QUIZ_UPDATE_ANS);
                adapter.clearList();
                adapter.addList(setAnswerEntities);
                binding.recViewEditQues.setAdapter(adapter);
            }
        }
    }
}
