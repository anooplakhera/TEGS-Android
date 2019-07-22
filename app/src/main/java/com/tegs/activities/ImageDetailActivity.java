package com.tegs.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityImageDetailBinding;
import com.tegs.utils.Constants;

public class ImageDetailActivity extends BaseActivity {

    private ActivityImageDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_detail);
        setData();
    }

    // Set Image,title and description
    private void setData() {
        //Setting ToolBar
        initToolbar(this);
        setToolbarTitle(getString(R.string.video_photo_gallery));

        if (getIntent() != null) {
            if (getIntent().getStringExtra(Constants.IMAGE_URL) != null) {
                String imageDoc = getIntent().getStringExtra(Constants.IMAGE_URL);
                int resID = getResources().getIdentifier(getImage(imageDoc), "drawable", getPackageName());
                binding.sdvGalleryImage.setImageResource(resID);
            }
            binding.txtTitle.setText(getIntent().getStringExtra(Constants.IMAGE_TITLE));
            binding.txtDesc.setText(getIntent().getStringExtra(Constants.IMAGE_DESC));
        }
    }

    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }
}
