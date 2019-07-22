package com.tegs.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.common.util.UriUtil;
import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivitySettingsBinding;
import com.tegs.databinding.DialogChangePasswordBinding;
import com.tegs.databinding.DialogLogoutBinding;
import com.tegs.model.GetChangePasswordResponse;
import com.tegs.model.GetEditProfileResponse;
import com.tegs.model.GetLogOutResponse;
import com.tegs.model.GetProfileResponse;
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
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created
 * by heena on 27/12/17.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySettingsBinding binding;
    private DialogChangePasswordBinding diaChangePassBinding;
    private DialogLogoutBinding dialogoutBinding;
    private Context mContext = SettingsActivity.this;
    private Activity mActivity = SettingsActivity.this;
    private String TAG = SettingsActivity.class.getSimpleName();
    private Dialog dialog, logoutDialog;
    private File tempFile;
    private MediaHelper mediaHelper;
    private Uri resultUri;
    private String imagePath = "";
    private Utils utils;
    private MultipartBody.Part fileToUpload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.activity_settings);
        mediaHelper = new MediaHelper(mActivity);
        utils = new Utils(mContext);
        disableEditText(); //For Disable The Edit Text
        setValues();
        callGetProfileWS();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_change_password: //TextView to open Change Password Dialog
                showChangePasswordDialog();
                break;
            case R.id.txt_logout: // TextView to open LogOut Dialog
                showLogoutDialog();
                break;
            case R.id.btn_cancel: //Cancel Button in Password Dialog to dismiss Dialog.
                dialog.dismiss();
                break;
            case R.id.btn_send: //Send Button in Password Dialog to send
                if (dialogValidations()) {
                    callChangePassWS();
                }
                break;
            case R.id.btn_no: // No Button in LogOut Dialog to dismiss dialog.
                logoutDialog.dismiss();
                break;
            case R.id.btn_yes: // Yes Button in LogOut Dialog to Logout User.
                logoutDialog.dismiss();
                callLogoutWS();
                break;
            case R.id.img_edit_profile: //Edit Image to open Update Profile Screen
                binding.imgEditProfile.setVisibility(View.INVISIBLE);
                enableEditText();
                break;
            case R.id.btn_edit_save: //Save Button in Update Profile
                callUpdateProfileWS();
                break;
            case R.id.btn_edit_cancel: //Cancel Button in Update Profile
                binding.setData(binding.getData());
                binding.imgEditProfile.setVisibility(View.VISIBLE);
                disableEditText();
                break;
            case R.id.sdv_img: //To Change the image
                showImagePicker();
                break;
        }
    }

    private void setValues() {
        //Setting ToolBar
        initToolbar(this);
        setToolbarTitle(getString(R.string.settings));
        binding.txtChangePassword.setOnClickListener(this);
        binding.txtLogout.setOnClickListener(this);
        binding.imgEditProfile.setOnClickListener(this);
        binding.etProfileName.setEnabled(false);
        binding.etProfileNumber.setEnabled(false);
        binding.etProfileTelephone.setEnabled(false);
        disableEditText();
    }

    /*
    Get Profile WebService
     */
    private void callGetProfileWS() {
        Utils.showProgressDialog(mActivity);
        Call<GetProfileResponse> call = RestClient.getInstance(true).getApiInterface().callGetProfileWS(Utils.getRequestMap(true));
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetProfileResponse result = (GetProfileResponse) response;
                if (result != null) {
                    if (result.getStatus() == RequestParameters.STATUS) {
                        AppLog.d(TAG, getString(R.string.success_result));
                        binding.setData(result.getData());
                    }
                    if (result.getStatus() == RequestParameters.STATUS_403) {
                        utils.logout(mContext);
                    }
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
    Update Profile WebService and Enable and Disable of EditText
     */
    private void disableEditText() {
        binding.lnrPasswordLogout.setVisibility(View.GONE);
        binding.lnrPasswordLogout.animate().alpha(1.0f);
        binding.lnrMessage.setVisibility(View.VISIBLE);
        binding.lnrEditButtons.setVisibility(View.GONE);
        binding.lnrEditButtons.animate().alpha(0.0f);
        binding.sdvImg.setClickable(false);
        binding.etProfileName.setEnabled(false);
        binding.etProfileNumber.setEnabled(false);
        binding.etProfileTelephone.setEnabled(false);
        binding.etProfileName.getBackground().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_ATOP);
        binding.etProfileTelephone.getBackground().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_ATOP);
        binding.etProfileNumber.getBackground().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_ATOP);
    }

    private void enableEditText() {
        binding.lnrPasswordLogout.setVisibility(View.GONE); // Current Layout to be not visible.
        binding.lnrMessage.setVisibility(View.GONE); // Current Layout to be not visible.
        binding.lnrPasswordLogout.animate().alpha(0.0f);
        binding.lnrEditButtons.setVisibility(View.VISIBLE);// Buttons to be visible
        binding.lnrEditButtons.animate().alpha(1.0f);
        binding.sdvImg.setClickable(true); // Set Image to be Clickable
        binding.sdvImg.setOnClickListener(this);
        binding.btnEditSave.setOnClickListener(this);
        binding.btnEditCancel.setOnClickListener(this);
        binding.etProfileName.setEnabled(true);
        binding.etProfileNumber.setEnabled(true);
        binding.etProfileTelephone.setEnabled(true);
        binding.etProfileName.getBackground().setColorFilter(getResources().getColor(R.color.colorDarkBrown), PorterDuff.Mode.SRC_ATOP);
        binding.etProfileTelephone.getBackground().setColorFilter(getResources().getColor(R.color.colorDarkBrown), PorterDuff.Mode.SRC_ATOP);
        binding.etProfileNumber.getBackground().setColorFilter(getResources().getColor(R.color.colorDarkBrown), PorterDuff.Mode.SRC_ATOP);
    }

    private void callUpdateProfileWS() {
         /*
         Parameters
        */
        final File imgFile = new File(imagePath);
        Log.e("Image-Path", String.valueOf(imgFile));
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), RequestParameters.DEVICE_TYPE_VALUE);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.etProfileName.getText().toString().trim());
        RequestBody phone_number = RequestBody.create(MediaType.parse("text/plain"), binding.etProfileNumber.getText().toString().trim());
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), RequestParameters.DEVICE_TOKEN_VALUE);
        RequestBody phone_no_2 = RequestBody.create(MediaType.parse("text/plain"), binding.etProfileTelephone.getText().toString().trim());

        RequestBody profilepicReq = null;
        MultipartBody.Part fileToUpload = null;
        if (!TextUtils.isEmpty(imagePath)) {
            profilepicReq = RequestBody.create(MediaType.parse("form-userDataModel"), imgFile);
            fileToUpload = MultipartBody.Part.createFormData(RequestParameters.IMAGE, imgFile.getName(), profilepicReq);
        }
        Call<GetEditProfileResponse> call = RestClient.getInstance(true).getApiInterface().callEditProfileWS(Utils.getRequestMap(true),fileToUpload, deviceType, name, phone_number, deviceToken, phone_no_2);
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetEditProfileResponse result = (GetEditProfileResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    binding.imgEditProfile.setVisibility(View.VISIBLE);
                    GetProfileResponse.Data profileData = binding.getData();
                    binding.setData(profileData);
                    disableEditText();
                    utils.setCompanyName(result.getData().getName());
                    Utils.showSnackBar(mActivity, getString(R.string.success_profile_update));
                    callGetProfileWS();
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
    Change Password Dialog
    */
    private void showChangePasswordDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        diaChangePassBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_change_password, null, false);
        dialog.setContentView(diaChangePassBinding.getRoot());
        diaChangePassBinding.btnCancel.setOnClickListener(this);
        diaChangePassBinding.btnSend.setOnClickListener(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout((int) (Utils.getScreenWidth(mActivity) * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private boolean dialogValidations() {
        diaChangePassBinding.etCurrentPassword.addTextChangedListener(new GenericEditTextWatcher(diaChangePassBinding.etCurrentPassword));
        diaChangePassBinding.etNewPassword.addTextChangedListener(new GenericEditTextWatcher(diaChangePassBinding.etNewPassword));
        diaChangePassBinding.etConfirmPassword.addTextChangedListener(new GenericEditTextWatcher(diaChangePassBinding.etConfirmPassword));
        if (TextUtils.isEmpty(diaChangePassBinding.etCurrentPassword.getText().toString().trim())) {
            diaChangePassBinding.tilCurrentPassword.setError(getString(R.string.err_password));
            diaChangePassBinding.etCurrentPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(diaChangePassBinding.etNewPassword.getText().toString().trim())) {
            diaChangePassBinding.tilNewPassword.setError(getString(R.string.err_password));
            diaChangePassBinding.etNewPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(diaChangePassBinding.etConfirmPassword.getText().toString().trim())) {
            diaChangePassBinding.tilConfirmPassword.setError(getString(R.string.err_password));
            diaChangePassBinding.etConfirmPassword.requestFocus();
            return false;
        }
        if (!diaChangePassBinding.etConfirmPassword.getText().toString().trim().equals(diaChangePassBinding.etNewPassword.getText().toString().trim())) {
            diaChangePassBinding.tilConfirmPassword.setError(getString(R.string.err_match_password));
            diaChangePassBinding.etNewPassword.requestFocus();
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
                case R.id.et_current_password:
                    diaChangePassBinding.tilCurrentPassword.setErrorEnabled(false);
                    break;
                case R.id.et_new_password:
                    diaChangePassBinding.tilNewPassword.setErrorEnabled(false);
                    break;
                case R.id.et_confirm_password:
                    diaChangePassBinding.tilConfirmPassword.setErrorEnabled(false);
                    break;
            }
        }

        public void afterTextChanged(Editable editable) {

        }
    }

    private void callChangePassWS() {
        /*
        Parameters
         */
        Utils.showProgressDialog(mActivity);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(RequestParameters.OLD_PASS, diaChangePassBinding.etCurrentPassword.getText().toString().trim());
        hashMap.put(RequestParameters.NEW_PASS, diaChangePassBinding.etNewPassword.getText().toString().trim());

        Call<GetChangePasswordResponse> call = RestClient.getInstance(true).getApiInterface().callChangePassWS(Utils.getRequestMap(true),Utils.getJSONRequestBody(hashMap));
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetChangePasswordResponse result = (GetChangePasswordResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
                    Utils.showSnackBar(mActivity, getString(R.string.success_password_change));
                    dialog.dismiss();
                }
                if (result.getStatus() == RequestParameters.STATUS_403) {
                    Utils.dismissDialog();
                    utils.logout(mContext);
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
                Utils.showSnackBar(mActivity, getString(R.string.err_password_change));
            }
        });
    }

    /*
    Logout Dialog
    */
    private void showLogoutDialog() {
        logoutDialog = new Dialog(mContext);
        dialogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_logout, null, false);
        logoutDialog.setContentView(dialogoutBinding.getRoot());
        dialogoutBinding.btnYes.setOnClickListener(this);
        dialogoutBinding.btnNo.setOnClickListener(this);
        logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutDialog.setCanceledOnTouchOutside(true);
        logoutDialog.getWindow().setLayout((int) (Utils.getScreenWidth(mActivity) * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutDialog.show();
    }

    private void callLogoutWS() {
        Utils.showProgressDialog(mActivity);
        Call<GetLogOutResponse> call = RestClient.getInstance(true).getApiInterface().callLogOutWS(Utils.getRequestMap(true));
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetLogOutResponse result = (GetLogOutResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    if (result != null) {
                        AppLog.d(TAG, getString(R.string.success_result));
                        Utils.showSnackBar(mActivity, getString(R.string.success_logout));
                        RestClient.clearInstance();
                        utils.logout(mContext);
                    }
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
    Image Picker
      */
    private void showImagePicker() {
        final CharSequence[] options = {"Take Image", "From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Take Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) // Camera
                {
                    boolean result = Utils.checkPermission(mContext);
                    if (result) {
                        tempFile = mediaHelper.captureCamera(Constants.REQUEST_CAPTURE_IMAGE, mContext);
                        Log.e("TempFile", String.valueOf(tempFile));
                    }
                } else if (item == 1) // Gallery
                {
                    boolean result = Utils.checkPermission(mContext);
                    if (result)
                        CropImage.startPickImageActivity(mActivity);
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
                binding.sdvImg.setImageURI(imageUri);
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
            binding.sdvImg.setImageURI(uriPhoto);
            Log.e("Setting First Image", String.valueOf(uriPhoto));
            binding.sdvImg.setImageURI(uriPhoto);
            imagePath = path;
        }
    }
}
