package com.tegs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.facebook.common.util.UriUtil;
import com.google.gson.Gson;
import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivitySignUpBinding;
import com.tegs.model.GetQuestionsListResponse;
import com.tegs.model.GetSignUpResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.MediaHelper;
import com.tegs.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 *  Created
 *  by heena on 27/12/17.
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySignUpBinding binding;
    private Activity mActivity;
    private String TAG = SignUpActivity.class.getSimpleName();
    private File tempFile;
    private MediaHelper mediaHelper;
    private Uri resultUri;
    private String imagePath = "";
    private Utils utils;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = SignUpActivity.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mediaHelper = new MediaHelper(SignUpActivity.this);
        utils = new Utils(mActivity);
        gson = new Gson();
        initializeToolbar();
        onClickEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_user_img:
                showImagePicker();
                break;
            case R.id.btn_sign_up:
                if (validations()) {
                    callSignUpWS();
                }
                break;
        }
    }

    private void initializeToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.img_left_grey_arrow);//Set Toolbar Icon
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void onClickEvents() {
        binding.btnSignUp.setOnClickListener(this);
        binding.sdvUserImg.setOnClickListener(this);
    }

    private boolean validations() {
        if (TextUtils.isEmpty(binding.etCompanyName.getText().toString().trim())) {
            Utils.hideKeyboard(SignUpActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_name));
            binding.etCompanyName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etMobileNumber.getText().toString().trim())) {
            Utils.hideKeyboard(SignUpActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_phone));
            binding.etMobileNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
            Utils.hideKeyboard(SignUpActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_emailID));
            binding.etEmail.requestFocus();
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                Utils.hideKeyboard(SignUpActivity.this);
                Utils.showSnackBar(mActivity, getString(R.string.err_correct_email));
                binding.etEmail.requestFocus();
                return false;
            }
        }
        if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
            Utils.hideKeyboard(SignUpActivity.this);
            Utils.showSnackBar(mActivity, getString(R.string.err_password));
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void callSignUpWS() {
        Utils.showProgressDialog(mActivity);
        /*
            Parameters
            */
        final File imgFile = new File(imagePath);
        Log.e("Image-Path", String.valueOf(imgFile));
        RequestBody emailID = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), binding.etPassword.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), RequestParameters.DEVICE_TYPE_VALUE);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.etCompanyName.getText().toString().trim());
        RequestBody roleID = RequestBody.create(MediaType.parse("text/plain"), RequestParameters.ROLE_ID_VALUE);
        RequestBody phoneNumber = RequestBody.create(MediaType.parse("text/plain"), binding.etMobileNumber.getText().toString().trim());
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), RequestParameters.DEVICE_TOKEN_VALUE);

        RequestBody profilepicReq = null;
        MultipartBody.Part fileToUpload = null;
        if (!TextUtils.isEmpty(imagePath)) {
            profilepicReq = RequestBody.create(MediaType.parse("form-userDataModel"), imgFile);
            fileToUpload = MultipartBody.Part.createFormData(RequestParameters.IMAGE, imgFile.getName(), profilepicReq);
        }

        Call<GetSignUpResponse> call = RestClient.getInstance().getApiInterface().callSignUpWS(Utils.getRequestMap(false),fileToUpload, emailID, password, deviceType, name, roleID, phoneNumber, deviceToken);
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                GetSignUpResponse result = (GetSignUpResponse) response;
                if (result.getStatus().equals(RequestParameters.STATUS)) {
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    utils.setUserImage(result.getData().getImage());
                    utils.setCompanyName(result.getData().getName());
                    utils.setPrefAuthToken(result.getData().getUserToken());
                    utils.setUserId(result.getData().getId());
                    callQuestionAnswersWS();
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                Utils.showSnackBar(mActivity, getString(R.string.err_the_email_has_already_been_taken));
                Log.d(TAG, "onFailure");
            }
        });
    }

    private void showImagePicker() {
        final CharSequence[] options = {"Take Image", "From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Take Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) // Camera
                {
                    boolean result = Utils.checkPermission(mActivity);
                    if (result) {
                        tempFile = mediaHelper.captureCamera(Constants.REQUEST_CAPTURE_IMAGE, SignUpActivity.this);
                        Log.e("TempFile", String.valueOf(tempFile));
                    }
                } else if (item == 1) // Gallery
                {
                    boolean result = Utils.checkPermission(mActivity);
                    if (result) {
                        CropImage.startPickImageActivity(SignUpActivity.this);
                    }
//                        galleryIntent();
                } else if (item == 2) // Cancel
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show(); // Show image choose dialog
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CAPTURE_IMAGE) {
                if (tempFile.exists()) {
                    System.out.println("FileSize: " + tempFile.length());
                } else {
                    System.out.println("temp file is null or does not exist");
                }

                CropImage.activity(Uri.fromFile(tempFile))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(mActivity);

            } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
                Uri imageUri = CropImage.getPickImageResultUri(mActivity, data);
                binding.sdvUserImg.setImageURI(imageUri);
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(mActivity);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                resultUri = result.getUri();
                Log.e("ImagePath****", String.valueOf(resultUri));
                handleCrop(resultUri.getPath());
            }

        }
    }

    private void handleCrop(String path) {
        if (!TextUtils.isEmpty(path)) {
            Uri uriPhoto = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_FILE_SCHEME) // "res"
                    .path(path)
                    .build();
            binding.sdvUserImg.setImageURI(uriPhoto);
            Log.e("Setting First Image", String.valueOf(uriPhoto));
            binding.sdvUserImg.setImageURI(uriPhoto);
            imagePath = path;
        }
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
