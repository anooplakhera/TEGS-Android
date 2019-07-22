package com.tegs.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tegs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by pooja on 10/1/18.
 */

public class SparePartPagerAdapter extends PagerAdapter {
    public List<String> imageList = new ArrayList<>();
    private Context mContext;
    private boolean isCreate;
    private OnClickImage onClickImage;
    private boolean isEditable = true;

    public SparePartPagerAdapter(Context context) {
        this.mContext = context;
        isCreate = true;
    }

    public SparePartPagerAdapter(Context context, boolean isEditable) {
        this.mContext = context;
        isCreate = false;
        this.isEditable = isEditable;
    }

    public void addAll(List<String> imageList) {
        this.imageList.addAll(imageList);
        notifyDataSetChanged();
    }

    public void setItem(String path, int position) {
        imageList.set(position, path);
        notifyDataSetChanged();
    }

    public void addItem(String path, int position) {
        imageList.add(position, path);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.spare_part_pager_item, container, false);

        SimpleDraweeView img_spare_parts = itemView.findViewById(R.id.img_spare_parts);
        TextView txt_add_image = itemView.findViewById(R.id.txt_add_image);
        ImageView ivDeleteImage=itemView.findViewById(R.id.ivDeleteImage);

        ivDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage.setOnImageDelete(position);
            }
        });

        img_spare_parts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditable) {
                    if (TextUtils.isEmpty(imageList.get(position)))
                        onClickImage.setOnClickImage(position, false);
                    else
                        onClickImage.setOnClickImage(position, true);
                }
            }
        });

        if (isCreate) {
            if (TextUtils.isEmpty(imageList.get(position))) {
                txt_add_image.setVisibility(View.VISIBLE);

            } else {
                ivDeleteImage.setVisibility(View.VISIBLE);
                Uri uriPhoto = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_FILE_SCHEME) // "res"
                        .path(imageList.get(position))
                        .build();
                img_spare_parts.setImageURI(uriPhoto);

                if (imageList.get(position).matches("^(http|https|ftp)://.*$")) {
                    img_spare_parts.setImageURI(imageList.get(position));
                    txt_add_image.setVisibility(View.GONE);
                }
            }
        } else {
            img_spare_parts.setImageURI(imageList.get(position));
            txt_add_image.setVisibility(View.GONE);
        }
        container.addView(itemView);
        return itemView;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    public void setOnClickImage(OnClickImage onClickImage) {
        this.onClickImage = onClickImage;
    }

    public void makeEditable() {
        isCreate = true;
    }

    public interface OnClickImage {
        void setOnClickImage(int position, boolean alreadyExists);
        void setOnImageDelete(int position);
    }
}


