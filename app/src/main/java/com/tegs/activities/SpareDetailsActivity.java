package com.tegs.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import com.tegs.R;
import com.tegs.adapters.SparePartPagerAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivitySparePartsDetailsBinding;
import com.tegs.model.Datum;
import com.tegs.model.GetSpareDetailsResponse;
import com.tegs.model.SparePartImage;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created
 * by heena on 1/1/18.
 */

public class SpareDetailsActivity extends BaseActivity {
    private ActivitySparePartsDetailsBinding binding;
    private Activity mActivity = SpareDetailsActivity.this;
    private int spareRowID;
    private Datum partDetail;
    private final int REQ_SPAREDETAILS = 11;
    private SparePartPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spare_parts_details);
        setValues();
        callSpareDetailsWS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppLog.d("SpareDetails", "ResultCode" + String.valueOf(resultCode));
        AppLog.d("SpareDetails", "RequestCode" + String.valueOf(requestCode));
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_SPAREDETAILS) {
                AppLog.d("SpareDetails", "InActvityResponse");
                callSpareDetailsWS();
            }
        }
    }

    private void setValues() {
        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.spare_parts));
        setToolMenu(R.menu.menu_edit, mActivity);
        //Getting Id from Previous Recycler View
        Bundle extras = this.getIntent().getExtras();
        spareRowID = Integer.parseInt(extras.getString(Constants.ID));
        AppLog.d("UserId in Spare Details", String.valueOf(spareRowID));
//        binding.imgSpareDetails.setClipToOutline(true);
    }

    private void callSpareDetailsWS() {
        Call<GetSpareDetailsResponse> call = RestClient.getInstance(true).getApiInterface().callSpareDetailsWS(Utils.getRequestMap(true),spareRowID);
        RestClient.makeApiRequest(mActivity, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                GetSpareDetailsResponse result = (GetSpareDetailsResponse) response;
                if (result != null) {
                    partDetail = result.getData().get(0);
                    binding.setData(partDetail);
                    setupViewPager();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {

            }
        });
    }

    // Viewpager auto item scroll
    private void setupViewPager() {
        List<String> mImageList = new ArrayList<>();
        if (partDetail != null && partDetail.sparePartImages != null && partDetail.sparePartImages.size() > 0) {
            for (SparePartImage image : partDetail.sparePartImages)
                mImageList.add(image.getImageUrl());
            viewPagerAdapter = new SparePartPagerAdapter(SpareDetailsActivity.this, false);
            viewPagerAdapter.addAll(mImageList);
            binding.viewPager.setAdapter(viewPagerAdapter);
        }
    }

    public void menuClick() {
        Intent intent = new Intent(SpareDetailsActivity.this, EditSpareActivity.class);
        intent.putExtra(Constants.SPARE_DETAILS, partDetail);
        startActivityForResult(intent, REQ_SPAREDETAILS);
//        startActivity(new Intent(SpareDetailsActivity.this, EditSpareActivity.class).putExtra(Constants.SPARE_DETAILS, partDetail));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit, menu);
        return true;
    }
}
