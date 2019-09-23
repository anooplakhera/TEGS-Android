package com.tegs.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityLoginBinding;
import com.tegs.model.GetLoginResponse;
import com.tegs.model.GetQuestionsListResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created
 * by heena on 27/12/17.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLoginBinding binding;
    private Activity mActivity = LoginActivity.this;
    private String TAG = LoginActivity.class.getSimpleName();
    private Utils utils;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        utils = new Utils(mActivity);
        gson = new Gson();
        onClickEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_forgot_password:
                startactivtiy(ForgotPasswordActivity.class);
                break;
            case R.id.txt_sign_up:
                startactivtiy(SignUpActivity.class);
                break;
            case R.id.txtSkipLogin:
                startactivtiy(HomeActivity.class);
                finish();
                break;
            case R.id.btn_login:
                if (validations()) {
                    callLoginWS();
                }
                break;
        }
    }

    private void onClickEvents() {
        binding.txtSignUp.setOnClickListener(this);
        binding.txtForgotPassword.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.txtSkipLogin.setOnClickListener(this);
    }

    private void startactivtiy(Class targetActivity) {
        startActivity(new Intent(mActivity, targetActivity));
    }

    private boolean validations() {
        if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
            Utils.hideKeyboard(LoginActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_emailID));
            binding.etEmail.requestFocus();
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                Utils.hideKeyboard(LoginActivity.this);
                Utils.showSnackBar(mActivity, getString(R.string.err_correct_email));
                binding.etEmail.requestFocus();
                return false;
            }
        }
        if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
            Utils.hideKeyboard(LoginActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_password));
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void callLoginWS() {
        /*
         Parameters
        */
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(RequestParameters.EMAIL, binding.etEmail.getText().toString().trim());
        hashMap.put(RequestParameters.PASSWORD, binding.etPassword.getText().toString().trim());
        hashMap.put(RequestParameters.DEVICE_TYPE, RequestParameters.DEVICE_TYPE_VALUE);
        hashMap.put(RequestParameters.DEVICE_TOKEN, RequestParameters.DEVICE_TOKEN_VALUE);

        Call<GetLoginResponse> call = RestClient.getInstance().getApiInterface().callLoginWS(Utils.getRequestMap(false), Utils.getJSONRequestBody(hashMap));
        Utils.showProgressDialog(mActivity);
        RestClient.makeApiRequest(LoginActivity.this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetLoginResponse result = (GetLoginResponse) response;
                if (result != null) {
                    if (result.getStatus() == RequestParameters.STATUS) {
                        AppLog.d(TAG, getString(R.string.success_result));
                        utils.setPrefAuthToken(result.getData().getUserToken());
                        utils.setUserId(result.getData().getId());
                        utils.setCompanyName(result.getData().getName());
                        utils.setPassword(binding.etPassword.getText().toString().trim());
                        utils.setEmail(binding.etEmail.getText().toString().trim());
                        callQuestionAnswersWS();
                        startactivtiy(HomeActivity.class);
                        finish();
                    }
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
                Utils.showSnackBar(mActivity, getString(R.string.invalid_wrong_email_password));
            }
        });
    }

    private void callQuestionAnswersWS() {
        Call<GetQuestionsListResponse> call = RestClient.getInstance(true).getApiInterface().callQuestionListWS(Utils.getRequestMap(true));
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                GetQuestionsListResponse result = (GetQuestionsListResponse) response;
                AppLog.d(TAG, getString(R.string.success_response));
                if (result.getStatus() == RequestParameters.STATUS) {
                    String resultString = gson.toJson(result);  //Response in Json String
                    AppLog.d("ResultString****", resultString);
                    utils.setQuestionAnswerJsonData(resultString);  //Store QuestionAnswer response to Utils.
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.d(TAG, throwable.toString());
            }
        });
    }
}
