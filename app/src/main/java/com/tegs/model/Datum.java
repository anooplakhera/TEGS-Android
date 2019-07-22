package com.tegs.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by pooja on 11/1/18.
 */


public class Datum implements Parcelable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("make")
    @Expose
    public String make;
    @SerializedName("modal_name")
    @Expose
    public String modalName;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user_id")
    @Expose
    public Integer userId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModalName() {
        return modalName;
    }

    public void setModalName(String modalName) {
        this.modalName = modalName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<SparePartImage> getSparePartImages() {
        return sparePartImages;
    }

    public void setSparePartImages(List<SparePartImage> sparePartImages) {
        this.sparePartImages = sparePartImages;
    }

    @SerializedName("created_at")
    @Expose

    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
    @SerializedName("spare_part_images")
    @Expose
    public List<SparePartImage> sparePartImages = new ArrayList<>();


    protected Datum(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        make = in.readString();
        modalName = in.readString();
        message = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        createdAt = in.readString();
        updatedAt = in.readString();
        deletedAt = in.readString();
        sparePartImages = in.createTypedArrayList(SparePartImage.CREATOR);
    }

    public static final Creator<Datum> CREATOR = new Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(make);
        parcel.writeString(modalName);
        parcel.writeString(message);
        if (userId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(userId);
        }
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
        parcel.writeString(deletedAt);
        parcel.writeTypedList(sparePartImages);
    }
}
