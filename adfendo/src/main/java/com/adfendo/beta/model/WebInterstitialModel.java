package com.adfendo.beta.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebInterstitialModel implements Parcelable {

    @SerializedName("ad_id")
    @Expose
    private Integer adId;
    @SerializedName("ad_type")
    @Expose
    private String adType;
    @SerializedName("int_ad_title")
    @Expose
    private String intAdTitle;
    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("app_image")
    @Expose
    private String appImage;
    @SerializedName("web_url")
    @Expose
    private String webUrl;
    @SerializedName("web_ad_image_link")
    @Expose
    private String webAdImageLink;
    @SerializedName("web_ad_video_link")
    @Expose
    private String webAdVideoLink;
    @SerializedName("int_ad_image_link")
    @Expose
    private String intAdImageLink;
    @SerializedName("app_url")
    @Expose
    private String appUrl;
    @SerializedName("ad_event_id")
    @Expose
    private Integer adEventId;

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
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

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebAdImageLink() {
        return webAdImageLink;
    }

    public void setWebAdImageLink(String webAdImageLink) {
        this.webAdImageLink = webAdImageLink;
    }

    public String getWebAdVideoLink() {
        return webAdVideoLink;
    }

    public void setWebAdVideoLink(String webAdVideoLink) {
        this.webAdVideoLink = webAdVideoLink;
    }

    public String getIntAdImageLink() {
        return intAdImageLink;
    }

    public void setIntAdImageLink(String intAdImageLink) {
        this.intAdImageLink = intAdImageLink;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public Integer getAdEventId() {
        return adEventId;
    }

    public void setAdEventId(Integer adEventId) {
        this.adEventId = adEventId;
    }

    public static Creator<WebInterstitialModel> getCREATOR() {
        return CREATOR;
    }

    protected WebInterstitialModel(Parcel in) {
        if (in.readByte() == 0) {
            adId = null;
        } else {
            adId = in.readInt();
        }
        adType = in.readString();
        intAdTitle = in.readString();
        appName = in.readString();
        appImage = in.readString();
        webUrl = in.readString();
        webAdImageLink = in.readString();
        webAdVideoLink = in.readString();
        intAdImageLink = in.readString();
        appUrl = in.readString();
        if (in.readByte() == 0) {
            adEventId = null;
        } else {
            adEventId = in.readInt();
        }
    }

    public static final Creator<WebInterstitialModel> CREATOR = new Creator<WebInterstitialModel>() {
        @Override
        public WebInterstitialModel createFromParcel(Parcel in) {
            return new WebInterstitialModel(in);
        }

        @Override
        public WebInterstitialModel[] newArray(int size) {
            return new WebInterstitialModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (adId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(adId);
        }
        dest.writeString(adType);
        dest.writeString(intAdTitle);
        dest.writeString(appName);
        dest.writeString(appImage);
        dest.writeString(webUrl);
        dest.writeString(webAdImageLink);
        dest.writeString(webAdVideoLink);
        dest.writeString(intAdImageLink);
        dest.writeString(appUrl);
        if (adEventId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(adEventId);
        }
    }
}