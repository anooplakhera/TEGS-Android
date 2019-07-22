package com.tegs.utils;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tegs.R;

/**
 * Created
 * by heena on 9/1/18.
 */

public class ImageBinder {
    @BindingAdapter("bind:imageUrl")
    public static void setImageUri(SimpleDraweeView view, String imageUri) {
        if(TextUtils.isEmpty(imageUri)){
            view.setImageResource(R.drawable.img_bussiness_icon);
        }else{
            view.setImageURI(Uri.parse(imageUri));
        }
    }
}
