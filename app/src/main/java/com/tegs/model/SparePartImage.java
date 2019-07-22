package com.tegs.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created
 * by pooja on 11/1/18.
 */

public class SparePartImage implements Parcelable {


    @SerializedName("spare_part_id")
    @Expose
    public Integer sparePartId;

    public boolean isLocalFile;
    public Integer getSparePartId() {
        return sparePartId;
    }

    public void setSparePartId(Integer sparePartId) {
        this.sparePartId = sparePartId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Creator<SparePartImage> getCREATOR() {
        return CREATOR;
    }

    @SerializedName("image_url")
    @Expose
    public String imageUrl;


    protected SparePartImage(Parcel in) {
        if (in.readByte() == 0) {
            sparePartId = null;
        } else {
            sparePartId = in.readInt();
        }
        imageUrl = in.readString();
    }

    public static final Creator<SparePartImage> CREATOR = new Creator<SparePartImage>() {
        @Override
        public SparePartImage createFromParcel(Parcel in) {
            return new SparePartImage(in);
        }

        @Override
        public SparePartImage[] newArray(int size) {
            return new SparePartImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (sparePartId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(sparePartId);
        }
        parcel.writeString(imageUrl);
    }
}