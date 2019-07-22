package com.tegs.activities;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityForgotPasswordBinding;
import com.tegs.model.GetForgotPasswordResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import retrofit2.Call;

/**
 * Created by heena on 27/12/17.
 */

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {
    private ActivityForgotPasswordBinding binding;
    private Activity mActivity = ForgotPasswordActivity.this;
    private String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        initializeToolbar();
        onClickEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (validations()) {
                    callForgotPassWS();
                }
                break;
        }
    }

    private void initializeToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.img_left_grey_arrow);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void onClickEvents() {
        binding.btnSend.setOnClickListener(this);
    }

    private boolean validations() {
        if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
            Utils.showSnackBar(mActivity, getString(R.string.err_emailID));
            binding.etEmail.requestFocus();
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                Utils.showSnackBar(mActivity, getString(R.string.err_correct_email));
                binding.etEmail.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void callForgotPassWS() {
        Utils.showProgressDialog(mActivity);
        Call<GetForgotPasswordResponse> call = RestClient.getInstance().getApiInterface().callForgotPassWS(Utils.getRequestMap(false),binding.etEmail.getText().toString().trim());
        RestClient.makeApiRequest(ForgotPasswordActivity.this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetForgotPasswordResponse result = (GetForgotPasswordResponse) response;
                if (result.getStatus().equals(RequestParameters.STATUS)) {
                    if (result != null) {
                        AppLog.d(TAG, getString(R.string.success_result));
                        Utils.dismissDialog();
                        Utils.showSnackBar(mActivity, getString(R.string.success_forgot_pass));
                    }
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
                Utils.showSnackBar(mActivity, getString(R.string.invalid_email));
            }
        });
    }
}
