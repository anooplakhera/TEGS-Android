package com.tegs.activities;

import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;

import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityVideoViewBinding;
import com.tegs.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created
 * by heena on 9/3/18.
 */

public class PlayVideoActivity extends BaseActivity {
    private ActivityVideoViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_view);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        //Title to set on Toolbar
        String VideoTitle = getIntent().getStringExtra(Constants.VIDEO_TITLE);
        TextView txtTitle = findViewById(R.id.txt_toolbar_title);
        txtTitle.setText(VideoTitle);
        binding.videoView.setMediaController(mediaController);
        //Get Url from Previous Activity to start video.
        String url = getIntent().getStringExtra(Constants.VIDEO_URL);
        accessAssetsVideoFile(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cancel:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void accessAssetsVideoFile(String docVideoUrl) {
        if (docVideoUrl != null) {
            AssetManager assetManager = PlayVideoActivity.this.getAssets();
            try {
                //video is folder where all videos are stored under assets
                InputStream in = assetManager.open("video/" + docVideoUrl + ".mp4");
                OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docVideoUrl + ".mp4");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (Exception e) {
                e.getMessage();
            }

            //Fetching File From Local and set it to play
            File fetchFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docVideoUrl + ".mp4");
            startPlaying(fetchFile);
        }
    }

    private void startPlaying(File url) {
        try {
            Uri uri = Uri.fromFile(url);
            binding.videoView.setVideoURI(uri);
            binding.videoView.requestFocus();
            binding.videoView.start();
        } catch (Exception e) {
            e.getMessage();
        }
    }

}

