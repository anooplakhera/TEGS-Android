package com.tegs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.tegs.R;
import com.tegs.utils.Utils;

public class SplashActivity extends AppCompatActivity {
    private Utils utils;
    private Activity mActivity = SplashActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        utils = new Utils(mActivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(utils.getPrefAuthToken())) {
                    startActivity(new Intent(mActivity, HomeActivity.class));
                } else {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}
