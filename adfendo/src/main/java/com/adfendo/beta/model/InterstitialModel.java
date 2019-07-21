package com.adfendo.beta.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InterstitialModel implements Parcelable {

    @SerializedName("ad_id")
    @Expose
    private int adId;
    @SerializedName("int_ad_title")
    @Expose
    private String intAdTitle;

    @SerializedName("app_name")
    @Expose
    private String appName;


    @SerializedName("ad_type")
    @Expose
    private String adType;
    @SerializedName("app_image")
    @Expose
    private String appImage;
    @SerializedName("int_ad_description")
    @Expose
    private String intAdDescription;
    @SerializedName("int_ad_description1")
    @Expose
    private String intAdDescription1;
    @SerializedName("int_ad_image_link")
    @Expose
    private String intAdImageLink;
    @SerializedName("int_ad_image_link1")
    @Expose
    private String intAdImageLink1;
    @SerializedName("int_ad_image_link2")
    @Expose
    private String intAdImageLink2;
    @SerializedName("int_ad_image_link3")
    @Expose
    private String intAdImageLink3;
    @SerializedName("app_url")
    @Expose
    private String appUrl;
    @SerializedName("app_rating")
    @Expose
    private String appRating;
    @SerializedName("app_review")
    @Expose
    private String appReview;
    @SerializedName("app_status")
    @Expose
    private String appStatus;
    @SerializedName("app_button_text")
    @Expose
    private String appButtonText;
    @SerializedName("ad_event_id")
    @Expose
    private int adEventId;

    protected InterstitialModel(Parcel in) {
        adId = in.readInt();
        intAdTitle = in.readString();
        appName = in.readString();
        adType = in.readString();
        appImage = in.readString();
        intAdDescription = in.readString();
        intAdDescription1 = in.readString();
        intAdImageLink = in.readString();
        intAdImageLink1 = in.readString();
        intAdImageLink2 = in.readString();
        intAdImageLink3 = in.readString();
        appUrl = in.readString();
        appRating = in.readString();
        appReview = in.readString();
        appStatus = in.readString();
        appButtonText = in.readString();
        adEventId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(adId);
        dest.writeString(intAdTitle);
        dest.writeString(appName);
        dest.writeString(adType);
        dest.writeString(appImage);
        dest.writeString(intAdDescription);
        dest.writeString(intAdDescription1);
        dest.writeString(intAdImageLink);
        dest.writeString(intAdImageLink1);
        dest.writeString(intAdImageLink2);
        dest.writeString(intAdImageLink3);
        dest.writeString(appUrl);
        dest.writeString(appRating);
        dest.writeString(appReview);
        dest.writeString(appStatus);
        dest.writeString(appButtonText);
        dest.writeInt(adEventId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InterstitialModel> CREATOR = new Creator<InterstitialModel>() {
        @Override
        public InterstitialModel createFromParcel(Parcel in) {
            return new InterstitialModel(in);
        }

        @Override
        public InterstitialModel[] newArray(int size) {
            return new InterstitialModel[size];
        }
    };

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public String getIntAdTitle() {
        return intAdTitle;
    }

    public void setIntAdTitle(String intAdTitle) {
        this.intAdTitle = intAdTitle;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getIntAdDescription() {
        return intAdDescription;
    }

    public void setIntAdDescription(String intAdDescription) {
        this.intAdDescription = intAdDescription;
    }

    public String getIntAdDescription1() {
        return intAdDescription1;
    }

    public void setIntAdDescription1(String intAdDescription1) {
        this.intAdDescription1 = intAdDescription1;
    }

    public String getIntAdImageLink() {
        return intAdImageLink;
    }

    public void setIntAdImageLink(String intAdImageLink) {
        this.intAdImageLink = intAdImageLink;
    }

    public String getIntAdImageLink1() {
        return intAdImageLink1;
    }

    public void setIntAdImageLink1(String intAdImageLink1) {
        this.intAdImageLink1 = intAdImageLink1;
    }

    public String getIntAdImageLink2() {
        return intAdImageLink2;
    }

    public void setIntAdImageLink2(String intAdImageLink2) {
        this.intAdImageLink2 = intAdImageLink2;
    }

    public String getIntAdImageLink3() {
        return intAdImageLink3;
    }

    public void setIntAdImageLink3(String intAdImageLink3) {
        this.intAdImageLink3 = intAdImageLink3;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppRating() {
        return appRating;
    }

    public void setAppRating(String appRating) {
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

    public String getAppButtonText() {
        return appButtonText;
    }

    public void setAppButtonText(String appButtonText) {
        this.appButtonText = appButtonText;
    }

    public int getAdEventId() {
        return adEventId;
    }

    public void setAdEventId(int adEventId) {
        this.adEventId = adEventId;
    }
}
