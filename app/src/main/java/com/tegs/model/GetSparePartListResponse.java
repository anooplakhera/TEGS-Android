package com.tegs.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heena on 28/12/17.
 */

public class GetSparePartListResponse {
    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data {

        @SerializedName("current_page")
        @Expose
        public int currentPage;
        @SerializedName("data")
        @Expose
        public List<Datum> children = new ArrayList<>();
        @SerializedName("first_page_url")
        @Expose
        public String firstPageUrl;
        @SerializedName("from")
        @Expose
        public int from;
        @SerializedName("last_page")
        @Expose
        public int lastPage;
        @SerializedName("last_page_url")
        @Expose
        public String lastPageUrl;
        @SerializedName("next_page_url")
        @Expose
        public Object nextPageUrl;
        @SerializedName("path")
        @Expose
        public String path;
        @SerializedName("per_page")
        @Expose
        public int perPage;
        @SerializedName("prev_page_url")
        @Expose
        public Object prevPageUrl;
        @SerializedName("to")
        @Expose
        public int to;
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("total_records")
        @Expose
        public int totalRecords;
        @SerializedName("total_pages")
        @Expose
        public int totalPages;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<Datum> getData() {
            return children;
        }

        public void setData(List<Datum> data) {
            this.children = data;
        }

        public String getFirstPageUrl() {
            return firstPageUrl;
        }

        public void setFirstPageUrl(String firstPageUrl) {
            this.firstPageUrl = firstPageUrl;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getLastPage() {
            return lastPage;
        }

        public void setLastPage(int lastPage) {
            this.lastPage = lastPage;
        }

        public String getLastPageUrl() {
            return lastPageUrl;
        }

        public void setLastPageUrl(String lastPageUrl) {
            this.lastPageUrl = lastPageUrl;
        }

        public Object getNextPageUrl() {
            return nextPageUrl;
        }

        public void setNextPageUrl(Object nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public Object getPrevPageUrl() {
            return prevPageUrl;
        }

        public void setPrevPageUrl(Object prevPageUrl) {
            this.prevPageUrl = prevPageUrl;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

    }

    @Entity
    public class Datum {

        @SerializedName("id")
        @Expose
        public int id;

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
        public int userId;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
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

    }

    public class SparePartImage {

        @SerializedName("spare_part_id")
        @Expose
        public int sparePartId;
        @SerializedName("image_url")
        @Expose
        public String imageUrl;

        public int getSparePartId() {
            return sparePartId;
        }

        public void setSparePartId(int sparePartId) {
            this.sparePartId = sparePartId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

    }
}
