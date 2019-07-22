package com.tegs.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by heena on 4/1/18.
 */

public class MediaHelper {

    private static final String TAG = "-MediaHelper-";
    public int current_code_to_send = 100;
    public static final String IMAGE_FILE_EXT = ".jpg";
    public static final String VIDEO_FILE_EXT = ".mp4";
    public static final String IMAGE_STORAGE_DIR = Environment.getExternalStorageDirectory() + File.separator + Constants.PREF_NAME + File.separator + "Images";
    public static final String VIDEO_STORAGE_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "MediaHelper" + File.separator + "VIDEO";

    private HashMap<Integer, MediaCallback> mediaHolderList;

    public enum Media {
        IMAGE, VIDEO
    }


    private Activity mActivity;
    private Fragment mFragment;

    public MediaHelper(Activity activity) {
        // TODO Auto-generated constructor stub
        mActivity = activity;
        mediaHolderList = new HashMap<Integer, MediaCallback>();
        File file = new File(IMAGE_STORAGE_DIR);
        if (!file.exists())
            file.mkdirs();
    }

    public MediaHelper(Fragment fragment) {
        // TODO Auto-generated constructor stub
        mFragment = fragment;
        mediaHolderList = new HashMap<Integer, MediaCallback>();
        File file = new File(IMAGE_STORAGE_DIR);
        if (!file.exists())
            file.mkdirs();
    }

    private File getNewFile(Media type) {
        try {
            File rootDir = new File(type == Media.IMAGE ? IMAGE_STORAGE_DIR : VIDEO_STORAGE_DIR);
            if (!rootDir.exists()) {
                rootDir.mkdir();
            }

            File file = new File(rootDir + File.separator + Constants.PREF_NAME + System.currentTimeMillis() + (type == Media.IMAGE ? IMAGE_FILE_EXT : VIDEO_FILE_EXT));
            if (file.exists()) {
                file.delete();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public File camera(int RequestCode) {
        File file = getNewFile(Media.IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(file);
            } else {
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            intent.putExtra("return-data", true);
            mActivity.startActivityForResult(intent, RequestCode);

        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "camera:cannot take picture ", e);

        }
        return file;
    }


    /***
     * for new crop lib
     *
     * @param RequestCode
     * @return
     */
    public File captureCamera(int RequestCode) {
        File file = getNewFile(Media.IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            mActivity.startActivityForResult(intent, RequestCode);

        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "camera:cannot take picture ", e);

        }
        return file;
    }

    /***     * for new crop lib
     * *     * @param RequestCode
     * * @return     */
    public File captureCamera(int RequestCode, Context mContext) {
        File file = getNewFile(Media.IMAGE);
        if (file == null) {
            return null;
        }
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromfile(file, mContext));
            mActivity.startActivityForResult(intent, RequestCode);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "camera:cannot take picture ", e);
        }
        return file;
    }

    public static Uri getUriFromfile(File photoFile, Context context) {
        Uri photoURI = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(context, "com.tegs.fileprovider", photoFile);
            }
        } else {
            photoURI = Uri.fromFile(photoFile);
        }
        return photoURI;
    }


}
