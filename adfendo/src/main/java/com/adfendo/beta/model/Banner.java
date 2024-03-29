package com.adfendo.beta.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("app_url")
    @Expose
    private String appUrl;
    @SerializedName("adv_id")
    @Expose
    private int advId;
    @SerializedName("ad_id")
    @Expose
    private int adId;
    @SerializedName("app_image")
    @Expose
    private String appImage;
    @SerializedName("ad_type")
    @Expose
    private String adType;
    @SerializedName("int_ad_description")
    @Expose
    private String intAdDescription;
    @SerializedName("app_rating")
    @Expose
    private double appRating;
    @SerializedName("app_review")
    @Expose
    private String appReview;
    @SerializedName("app_status")
    @Expose
    private String appStatus;
    @SerializedName("ad_event_id")
    @Expose
    private int adEventId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public int getAdvId() {
        return advId;
    }

    public void setAdvId(int advId) {
        this.advId = advId;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getIntAdDescription() {
        return intAdDescription;
    }

    public void setIntAdDescription(String intAdDescription) {
        this.intAdDescription = intAdDescription;
    }

    public double getAppRating() {
        return appRating;
    }

    public void setAppRating(double appRating) {
        this.appRating = appRating;
    }

    public String getAppReview() {
        return appReview;
    }

    public void setAppReview(String appReview) {
        this.appReview = appReview;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public int getAdEventId() {
        return adEventId;
    }

    public void setAdEventId(int adEventId) {
        this.adEventId = adEventId;
    }
}
