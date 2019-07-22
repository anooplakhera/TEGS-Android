package com.tegs.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityHomeBinding;
import com.tegs.utils.Utils;

/**
 * Created
 * by heena on 27/12/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private ActivityHomeBinding binding;
    private Activity currentActivity;
    private Utils utils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        currentActivity = this;
        utils = new Utils(currentActivity);
        onClickEvents();

        /*Change statusbar color*/
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorOrange));
        }

        if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
            binding.txtCompanyName.setText(utils.getCompanyName());
        } else {
            binding.txtCompanyName.setText(getString(R.string.app_name));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
            binding.txtCompanyName.setText(utils.getCompanyName());
        } else {
            binding.txtCompanyName.setText(getString(R.string.app_name));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_settings:
                if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
                    startactivtiy(SettingsActivity.class);
                } else {
                    Toast.makeText(this, getString(R.string.add_info), Toast.LENGTH_SHORT).show();
                    startactivtiy(SignUpActivity.class);
                    finish();
                }
                break;
            case R.id.txt_spare_parts_quotes:
                if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
                    startactivtiy(SparePartQuotesActivity.class);
                } else {
                    Toast.makeText(this, getString(R.string.add_info), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_data_sheets_manuals:
                startactivtiy(DataSheetsAndManualsActivity.class);
                break;
            case R.id.txt_easy_automation_quotes:
                if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
                    startactivtiy(InstallationQuotesActivity.class);
                } else {
                    Toast.makeText(this, getString(R.string.add_info), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_instant_msg:
                if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
                    startactivtiy(InstantMessageActivity.class);
                } else {
                    Toast.makeText(this, getString(R.string.add_info), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_brochures:
                startactivtiy(BrochuresActivity.class);
                break;
            case R.id.txt_video_photo_gallery:
                startactivtiy(VideoAndPhotoActivity.class);
                break;
        }
    }

    private void onClickEvents() {
        binding.imgSettings.setOnClickListener(this); //Image for Setting Activity
        binding.txtEasyAutomationQuotes.setOnClickListener(this); //Link for Installation Quotes
        binding.txtDataSheetsManuals.setOnClickListener(this);
        binding.txtSparePartsQuotes.setOnClickListener(this);//Link for Spare Parts
        binding.txtBrochures.setOnClickListener(this);// Link for PdfChildData
        binding.txtVideoPhotoGallery.setOnClickListener(this);//Link for Video and Gallery
        binding.txtInstantMsg.setOnClickListener(this);//Link for Instant Messages
    }

    private void startactivtiy(Class targetActivity) {
        startActivity(new Intent(currentActivity, targetActivity));
    }
}
