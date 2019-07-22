package com.tegs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBSpareMethodSync;
import com.tegs.DatabaseHandler.SpareDataEntity;
import com.tegs.R;
import com.tegs.adapters.SparePartPagerAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityCreateSparePartQuotesBinding;
import com.tegs.global.TegsApplication;
import com.tegs.model.CreateSparePartsResponse;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.MediaHelper;
import com.tegs.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created
 * by heena on 28/12/17.
 */

public class CreateSparePartsActivity extends BaseActivity implements View.OnClickListener {
    private ActivityCreateSparePartQuotesBinding binding;
    private Activity mActivity = CreateSparePartsActivity.this;
    private String TAG = CreateSparePartsActivity.class.getSimpleName();
    private File tempFile;
    private MediaHelper mediaHelper;
    private Uri resultUri;
    private Context mContext = CreateSparePartsActivity.this;
    private SparePartPagerAdapter pagerAdapter;
    private int position;
    private Utils utils;
    private boolean imageAlreadyExists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_spare_part_quotes);
        mediaHelper = new MediaHelper(CreateSparePartsActivity.this);
        pagerAdapter = new SparePartPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        utils = new Utils(TegsApplication.getAppInstance());

        binding.btnCancel.setVisibility(View.GONE);
        binding.etMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etMessage.setRawInputType(InputType.TYPE_CLASS_TEXT);

        pagerAdapter.setOnClickImage(new SparePartPagerAdapter.OnClickImage() {
            @Override
            public void setOnClickImage(int pos, boolean already) {
                position = pos;
                imageAlreadyExists = already;
                showImagePicker();
            }

            @Override
            public void setOnImageDelete(int pos) {
                if (pagerAdapter != null) {
                    pagerAdapter.removeItem(pos);
                    if (!TextUtils.isEmpty(pagerAdapter.imageList.get(pagerAdapter.imageList.size() - 1)))
                        pagerAdapter.addItem("", pagerAdapter.imageList.size());
                    if (pagerAdapter.imageList.size() == 1 && TextUtils.isEmpty(pagerAdapter.imageList.get(0))) {
                        pagerAdapter.removeItem(0);
                        binding.txtAddImage.setVisibility(View.VISIBLE);
                    }

                    if (pagerAdapter.imageList.size() <= 0) {
                        binding.txtAddImage.setVisibility(View.VISIBLE);
                        position = 0;
                        imageAlreadyExists = false;
                    }
                }
            }
        });
        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.create_spare_part));
        onClickEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                Utils.hideKeyboard(mActivity);
                if (validations()) {
//                    saveDataInRoom();
                    callCreateSparePartsWS();
                }
                break;
            case R.id.txt_add_image:
                showImagePicker();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    private void onClickEvents() {
        binding.btnSubmit.setOnClickListener(this);
        binding.txtAddImage.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    private boolean validations() {
        if (pagerAdapter.imageList.size() == 0) {
            Utils.showSnackBar(mActivity, getString(R.string.err_image));
            return false;
        }
        if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
            Utils.showSnackBar(mActivity, getString(R.string.err_maker_name));
            binding.etName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etModalName.getText().toString().trim())) {
            Utils.showSnackBar(mActivity, getString(R.string.err_modal_name));
            binding.etModalName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etMessage.getText().toString().trim())) {
            Utils.showSnackBar(mActivity, getString(R.string.err_message));
            binding.etMessage.requestFocus();
            return false;
        }
        return true;
    }

    //Call WebService
    private void callCreateSparePartsWS() {
        Utils.showProgressDialog(mActivity);

        RequestBody rbImage1, rbImage2, rbImage3, rbImage4;

        int size = pagerAdapter.imageList.size();

        rbImage1 = null;
        rbImage2 = null;
        rbImage3 = null;
        rbImage4 = null;
        if (size == 2) {
            rbImage1 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(0)));
        } else if (size == 3) {
            rbImage1 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(0)));
            rbImage2 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(1)));
        } else if (size == 4) {
            rbImage1 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(0)));
            rbImage2 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(1)));
            rbImage3 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(2)));
            if (!TextUtils.isEmpty(pagerAdapter.imageList.get(3)))
                rbImage4 = Utils.getRequestBody(new File(pagerAdapter.imageList.get(3)));
        }
        /*
            Parameters
            */
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.etName.getText().toString().trim());
        RequestBody modalName = RequestBody.create(MediaType.parse("text/plain"), binding.etModalName.getText().toString().trim());
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), binding.etMessage.getText().toString().trim());

        Call<CreateSparePartsResponse> call = RestClient.getInstance(true).getApiInterface().callCreateSpareParts(Utils.getRequestMap(true),
                rbImage1,
                rbImage2,
                rbImage3,
                rbImage4,
                name,
                modalName,
                message);
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                CreateSparePartsResponse result = (CreateSparePartsResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
                    Utils.showSnackBar(mActivity, getString(R.string.success_create_spare_parts));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    AppLog.d(TAG, "ResultOk");
                    finish();
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e(TAG, "onError");
            }
        });

    }

//

    public static byte[] getPictureByteOfArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //      DBSetAnsMethodSync.insertData(AppDatabase.getAppDatabase(getActivity()), setAnswerEntity);
    //Image Picker
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
                        CropImage.startPickImageActivity(CreateSparePartsActivity.this);
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
            binding.txtAddImage.setVisibility(View.GONE);
            if (pagerAdapter != null) {
                if (imageAlreadyExists) {
                    pagerAdapter.setItem(path, position);
                } else {
                    pagerAdapter.addItem(path, position);
                    binding.viewPager.setCurrentItem(position);

                    if (pagerAdapter.imageList.size() == 1)
                        pagerAdapter.addItem("", position + 1);
                    if (position == 3)
                        pagerAdapter.removeItem(position + 1);
                }
            }

        }
    }
}
