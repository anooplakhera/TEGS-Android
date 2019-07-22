package com.tegs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.tegs.utils.MediaHelper.Media;
import java.io.File;


/**
 * Created by heena on 4/1/18.
 */


public abstract class MediaCallback {

    public abstract void onResult(boolean status, File file, Media mediaType, Object object);

    public Object object;
    public File file;
    public MediaHelper.Media mediaType;
    public Bitmap bitmap, thumbnailBitmap;

    /**
     * Called to show original image
     *
     * @param imageView
     */
    public void showInImageView(ImageView imageView) {
        if (file.exists() && mediaType == Media.IMAGE) {

            if (bitmap == null)

                bitmap = createBitmapFromFile(file);

            imageView.setImageBitmap(bitmap);

        } else if (file.exists() && mediaType == Media.VIDEO) {
            showThumbnailInImageView(imageView);
        }
    }

    public Bitmap createBitmapFromFile(File file) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(file.getPath(), options);

            return bm;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Called to show thumbnail image
     *
     * @param imageView
     */
    public void showThumbnailInImageView(ImageView imageView) {
        if (file.exists() && mediaType == Media.IMAGE) {

            imageView.setImageBitmap(getThumbnailBitmap());

        } else if (file.exists() && mediaType == Media.VIDEO) {

            imageView.setImageBitmap(getThumbnailBitmap());

        }
    }

    public Bitmap getThumbnailBitmap() {
        if (thumbnailBitmap == null) {
            if (file.exists() && mediaType == Media.IMAGE) {

                if (bitmap == null)
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                int[] size = getThumbnailWidthHeight();
                thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, size[0], size[1]);

            } else if (file.exists() && mediaType == Media.VIDEO) {

                thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                if (thumbnailBitmap == null) {
                    thumbnailBitmap = createThumbnailAtTime(file.getPath(), 150);
                }

                if (thumbnailBitmap == null) {
                    onMessageCallback("Cannot generate thumbnail", null);
                }
            }
        }
        return thumbnailBitmap;
    }

    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds) {
        try {
            MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
            mMMR.setDataSource(filePath);
            // api clinicTime unit is microseconds
            return mMMR.getFrameAtTime(timeInSeconds * 1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
            onMessageCallback(e.getMessage(), e);
        }
        return null;
    }

    public int[] getThumbnailWidthHeight() {
        return new int[]{512, 384};
    }

    public void onMessageCallback(String message, Exception exception) {

    }
}