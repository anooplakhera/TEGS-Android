package com.tegs.activities;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tegs.R;
import com.tegs.adapters.InstantMessageAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityInstantMessageBinding;
import com.tegs.databinding.DialogInstantMessageBinding;
import com.tegs.model.CreateInstantMsgResponse;
import com.tegs.model.GetInstantMessageResponse.Datum;
import com.tegs.model.GetInstantMessageResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.EndlessScrollListener;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

/**
 * Created
 * by heena on 29/12/17.
 */

public class InstantMessageActivity extends BaseActivity implements View.OnClickListener {
    private ActivityInstantMessageBinding binding;
    private DialogInstantMessageBinding diaInstantBinding;
    private InstantMessageAdapter instantMessageAdapter;
    private Dialog dialog;
    private String TAG = InstantMessageActivity.class.getSimpleName();
    private EndlessScrollListener endlessScrollListener;
    private int currentPage = 1, totalPage = 0;
    private Utils utils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_instant_message);
        utils = new Utils(InstantMessageActivity.this);
        callGetInstantMsgList();
        findValues();
        binding.imgMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_msg: //Image to open Instant Message Dialog
                openDialog();
                break;
            case R.id.btn_send:
                Utils.hideKeyboard(this);
                if (dialogValidations()) {
                    callCreateInstantMsgWS();
                    instantMessageAdapter.refreshNotifyData();
                }
                break;
            case R.id.img_cancel_btn:
                dialog.dismiss();
                break;
        }
    }

    private void findValues() {
        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.instant_msg));

        //Setting Recycler View
        instantMessageAdapter = new InstantMessageAdapter(new ArrayList<Datum>());
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InstantMessageActivity.this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        binding.recViewInstantMsg.setLayoutManager(linearLayoutManager);
        binding.recViewInstantMsg.setAdapter(instantMessageAdapter);
        endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (currentPage <= totalPage) {
                    callGetInstantMsgList();
                }
            }
        };
        binding.recViewInstantMsg.addOnScrollListener(endlessScrollListener);

        //Swipe Refreshing
        binding.swipeMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                instantMessageAdapter.clearList();
                endlessScrollListener.resetState();
                currentPage = 1;
                callGetInstantMsgList();
                binding.swipeMessages.setRefreshing(false);
            }
        });
    }

    private void callGetInstantMsgList() {
        Utils.showProgressDialog(this);
        Call<GetInstantMessageResponse> call = RestClient.getInstance(true).getApiInterface().callInstantMsgList(Utils.getRequestMap(true),currentPage);
        RestClient.makeApiRequest(this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                Utils.dismissDialog();
                GetInstantMessageResponse result = (GetInstantMessageResponse) response;
                AppLog.d(TAG, getString(R.string.success_response));
                if (result.getStatus() == RequestParameters.STATUS) {
                    binding.txtEmptyView.setVisibility(View.GONE);
                    currentPage++;
                    totalPage = result.getData().getTotalPages();
                    instantMessageAdapter.addList(result.getData().children);
                }
                if (result.getStatus() == RequestParameters.STATUS_403) {
                    utils.logout(InstantMessageActivity.this);
                }
                if (instantMessageAdapter.getItemCount() > 0) {
                    binding.txtEmptyView.setVisibility(View.GONE);
                } else {
                    binding.txtEmptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
            }
        });
    }

    /*
    Instant Message Dialog
    */
    private void openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        diaInstantBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_instant_message, null, false);
        dialog.setContentView(diaInstantBinding.getRoot());
        diaInstantBinding.imgCancelBtn.setOnClickListener(this);
        diaInstantBinding.btnSend.setOnClickListener(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout((int) (Utils.getScreenWidth(InstantMessageActivity.this) * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private boolean dialogValidations() {
        diaInstantBinding.etSubject.addTextChangedListener(new GenericEditTextWatcher(diaInstantBinding.etSubject));
        diaInstantBinding.etMessage.addTextChangedListener(new GenericEditTextWatcher(diaInstantBinding.etMessage));
        if (TextUtils.isEmpty(diaInstantBinding.etSubject.getText().toString().trim())) {
            diaInstantBinding.tilSubject.setError(getString(R.string.err_subject));
            diaInstantBinding.etSubject.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(diaInstantBinding.etMessage.getText().toString().trim())) {
            diaInstantBinding.tilMessage.setError(getString(R.string.err_message));
            diaInstantBinding.etMessage.requestFocus();
            return false;
        }
        return true;
    }

    private class GenericEditTextWatcher implements TextWatcher {

        private View view;

        private GenericEditTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.et_subject:
                    diaInstantBinding.tilSubject.setErrorEnabled(false);
                    break;
                case R.id.et_message:
                    diaInstantBinding.tilMessage.setErrorEnabled(false);
                    break;
            }
        }

        public void afterTextChanged(Editable editable) {

        }
    }


    private void callCreateInstantMsgWS() {
        /*
        Parameters
        */
        Utils.showProgressDialog(this);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(RequestParameters.SUBJECT, diaInstantBinding.etSubject.getText().toString().trim());
        hashMap.put(RequestParameters.MESSAGE, diaInstantBinding.etMessage.getText().toString().trim());

        Call<CreateInstantMsgResponse> call = RestClient.getInstance(true).getApiInterface().callCreateInstantMsg(Utils.getRequestMap(true),Utils.getJSONRequestBody(hashMap));
        RestClient.makeApiRequest(this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                CreateInstantMsgResponse result = (CreateInstantMsgResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
                    Utils.showSnackBar(InstantMessageActivity.this, getString(R.string.success_msg));
                    dialog.dismiss();
                    instantMessageAdapter.clearList();
                    endlessScrollListener.resetState();
                    currentPage = 1;
                    callGetInstantMsgList();
                } else {
                    AppLog.e(TAG, "onError");
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
            }
        });

    }

}
