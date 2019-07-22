package com.tegs.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.tegs.R;
import com.tegs.adapters.ViewPagerAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityVideoAndPhotoGalleryBinding;
import com.tegs.fragment.GalleryFragment;
import com.tegs.fragment.VideoFragment;
import com.tegs.utils.Constants;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created
 * by heena on 29/12/17.
 */

public class VideoAndPhotoActivity extends BaseActivity {
    private ActivityVideoAndPhotoGalleryBinding binding;
    private ViewPagerAdapter adapter;
    public String categoryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_and_photo_gallery);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        findValues();
        searchList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_linear_grid, menu);
//        gridMenu = menu.findItem(R.id.menu_gridView);
//        linearMenu = menu.findItem(R.id.menu_linearView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_gridView:
//                gridMenu.setVisible(false);
//                linearMenu.setVisible(true);
//                VideoFragment videoFragment = (VideoFragment) adapter.getItem(0);
//                videoFragment.showGrid();
//                GalleryFragment galleryFragment = (GalleryFragment) adapter.getItem(1);
//                galleryFragment.showLinear();
//                break;
//            case R.id.menu_linearView:
//                linearMenu.setVisible(false);
//                gridMenu.setVisible(true);
//                VideoFragment showLinear = (VideoFragment) adapter.getItem(0);
//                showLinear.showLinear();
//                GalleryFragment showgalleryFragment = (GalleryFragment) adapter.getItem(1);
//                showgalleryFragment.showGrid();
//                break;
            case R.id.menu_filter:
                int currentItem = binding.tabLayout.getSelectedTabPosition();
                if (currentItem == 0) {
                    showFilterDialog(this, loadJSONFromAsset(Constants.VIDEO_JSON), new onTextClickView() {
                        @Override
                        public void onTextClickView(String catName) {
                            categoryName = catName;
                            VideoFragment fragment = (VideoFragment) adapter.getItem(0);
                            fragment.getFilteredData();
                        }
                    });
                } else if (currentItem == 1) {
                    showFilterDialog(this, loadJSONFromAsset(Constants.GALLERY_JSON), new onTextClickView() {
                        @Override
                        public void onTextClickView(String catName) {
                            categoryName = catName;
                            GalleryFragment fragment = (GalleryFragment) adapter.getItem(1);
                            fragment.getFilteredData();
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchList() {
        EditText etSearchText = findViewById(R.id.et_search_text);
        final int currentItem = binding.tabLayout.getSelectedTabPosition();
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentItem == 0) {
                    VideoFragment fragment = (VideoFragment) adapter.getItem(0);
                    fragment.searchedData(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String loadJSONFromAsset(String jsonFile) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void findValues() {
        setupLinearPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        //Setting ToolBar
        initToolbar(this);
        setToolbarTitle(getString(R.string.video_photo_gallery));
    }

    private void setupLinearPager(ViewPager viewPager) {
        adapter.addFragment(new VideoFragment(), getString(R.string.video));
        adapter.addFragment(new GalleryFragment(), getString(R.string.gallery));
        viewPager.setAdapter(adapter);
    }
}
