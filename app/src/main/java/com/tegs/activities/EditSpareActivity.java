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

import com.tegs.R;
import com.tegs.adapters.SparePartPagerAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityCreateSparePartQuotesBinding;
import com.tegs.model.Datum;
import com.tegs.model.GetEditSpareResponse;
import com.tegs.model.SparePartImage;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created
 * by heena on 11/1/18.
 */

public class EditSpareActivity extends BaseActivity implements View.OnClickListener {
    private ActivityCreateSparePartQuotesBinding binding;
    private Activity mActivity = EditSpareActivity.this;
    private String TAG = EditSpareActivity.class.getSimpleName();
    private String imagePath = "";
    private File tempFile;
    private MediaHelper mediaHelper;
    private Uri resultUri;
    private Context mContext = EditSpareActivity.this;
    private SparePartPagerAdapter pagerAdapter;
    private int position;
    private boolean imageAlreadyExists;
    private Datum partDetails;
    private int id;
    private List<String> mImageList = new ArrayList<>();
    private List<String> uploadImageArrayList = new ArrayList<>();
    private List<String> deleteImageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_spare_part_quotes);
        mediaHelper = new MediaHelper(mActivity);
        pagerAdapter = new SparePartPagerAdapter(this, true);
        binding.viewPager.setAdapter(pagerAdapter);
        pagerAdapter.makeEditable();
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

                    deleteImageArrayList.add(pagerAdapter.imageList.get(pos));
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
        if (getIntent().hasExtra(Constants.SPARE_DETAILS)) {
            partDetails = getIntent().getParcelableExtra(Constants.SPARE_DETAILS);
            fillData(partDetails);
            setupViewPager(partDetails);
        }
        initToolbar(this);
        setToolbarTitle(getString(R.string.edit_spare_part));
        onClickEvents();
    }

    public void fillData(Datum partDetails) {
        binding.etModalName.setText(partDetails.modalName);
        binding.etName.setText(partDetails.make);
        binding.etMessage.setText(partDetails.message);
        id = partDetails.id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (validations()) {
                    callEditSparePartsWS();
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
    private void callEditSparePartsWS() {
        Utils.showProgressDialog(mActivity);
        MultipartBody.Part[] uploadImageParts = new MultipartBody.Part[uploadImageArrayList.size()];
        for (int i = 0; i < uploadImageArrayList.size(); i++) {
            File file = new File(uploadImageArrayList.get(i));
            RequestBody requestBodyUploadImages = RequestBody.create(MediaType.parse("image/*"), file);
            String uploadImagesKeyName = "image" + "[" + i + "]";
            uploadImageParts[i] = MultipartBody.Part.createFormData(uploadImagesKeyName, file.getName(), requestBodyUploadImages);
        }
        MultipartBody.Part[] deleteImageParts = new MultipartBody.Part[deleteImageArrayList.size()];
        for (int i = 0; i < deleteImageArrayList.size(); i++) {
            RequestBody requestBodyDeleteImages = RequestBody.create(MediaType.parse("text/plain"), deleteImageArrayList.get(i));
            String deleteImagesKeyName = "deleted_image" + "[" + i + "]";
            deleteImageParts[i] = MultipartBody.Part.createFormData(deleteImagesKeyName, null, requestBodyDeleteImages);
        }
        /*
        PARAMETERS
        */
        RequestBody spareId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.etName.getText().toString().trim());
        RequestBody modalName = RequestBody.create(MediaType.parse("text/plain"), binding.etModalName.getText().toString().trim());
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), binding.etMessage.getText().toString().trim());

        Call<GetEditSpareResponse> call = RestClient.getInstance(true).getApiInterface().callEditSpareWS(Utils.getRequestMap(true),
                spareId,
                name,
                modalName,
                message,
                deleteImageParts,
                uploadImageParts);
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                GetEditSpareResponse result = (GetEditSpareResponse) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
//                    Utils.showSnackBar(mActivity, getString(R.string.success_create_spare_parts));
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    AppLog.d(TAG,"ResultOk");
                    finish();
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {

            }
        });
    }

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
                        CropImage.startPickImageActivity(EditSpareActivity.this);
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
                    if (pagerAdapter.imageList.get(position).matches("^(http|https|ftp)://.*$"))
                        deleteImageArrayList.add(pagerAdapter.imageList.get(position));
                    else
                        uploadImageArrayList.add(path);
                    pagerAdapter.setItem(path, position);
                } else {
                    uploadImageArrayList.add(path);
                    pagerAdapter.addItem(path, position);
                    binding.viewPager.setCurrentItem(position);

                    if (pagerAdapter.imageList.size() == 1)
                        pagerAdapter.addItem("", position + 1);
                    if (position == 3)
                        pagerAdapter.removeItem(position + 1);
                }
            }

            imagePath = path;
        }
    }

    // Viewpager auto item scroll
    private void setupViewPager(Datum partDetail) {
        if (partDetail != null && partDetail.sparePartImages != null && partDetail.sparePartImages.size() > 0) {
            for (SparePartImage image : partDetail.sparePartImages)
                mImageList.add(image.getImageUrl());
            binding.txtAddImage.setVisibility(View.GONE);
            pagerAdapter.addAll(mImageList);

            if (mImageList.size() < 4 && mImageList.size() > 0) {
                pagerAdapter.addItem("", mImageList.size());
            }

        }
    }

}