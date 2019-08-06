package com.adfendo.beta.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AdResponse {

    @SerializedName("int")
    @Expose
    private InterstitialModel interstitial;

    @SerializedName("type")
    @Expose
    private String adType;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("click")
    @Expose
    private String click;

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    @SerializedName("imp")
    @Expose
    private String impression;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }


    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public CustomInterstitialModel getCustomInterstitialAd() {
        return customInterstitialAd;
    }

    public void setCustomInterstitialAd(CustomInterstitialModel customInterstitialAd) {
        this.customInterstitialAd = customInterstitialAd;
    }

    public WebInterstitialModel getWebInterstitialModel() {
        return webInterstitialModel;
    }

    public void setWebInterstitialModel(WebInterstitialModel webInterstitialModel) {
        this.webInterstitialModel = webInterstitialModel;
    }

    @SerializedName("custom")
    @Expose
    private CustomInterstitialModel customInterstitialAd;

    @SerializedName("web")
    @Expose
    private WebInterstitialModel webInterstitialModel;

    @SerializedName("ban")
    @Expose
    private Banner bannerAd;


    @SerializedName("video")
    @Expose
    private Video videoAd;

    public Video getVideoAd() {
        return videoAd;
    }

    public void setVideoAd(Video videoAd) {
        this.videoAd = videoAd;
    }

    public Banner getBannerAd() {
        return bannerAd;
    }

    public void setBannerAd(Banner bannerAd) {
        this.bannerAd = bannerAd;
    }


    public InterstitialModel getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(InterstitialModel interstitialModel) {
        this.interstitial = interstitialModel;
    }


}
