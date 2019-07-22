package com.tegs.global;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultEncodedMemoryCacheParamsSupplier;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Created
 * by heena on 4/1/18.
 */

public class TegsApplication extends Application {
    private static TegsApplication sInstance;

    private String TAG = TegsApplication.class.getSimpleName();
    private static TegsApplication context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate:called");
     /*fresco*/
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setEncodedMemoryCacheParamsSupplier(new DefaultEncodedMemoryCacheParamsSupplier())
                .build();
        Fresco.initialize(this, config);

    }

    public static TegsApplication getInstance() {
        return sInstance;
    }

    /**
     * This method is used to get App Instance
     *
     * @return return App Instance
     */
    public static TegsApplication getAppInstance() {
        return context;
    }
}
