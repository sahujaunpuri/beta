package com.adfendo.beta.callback;

import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.IpLocatoin;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("adRequest")
    Call<AdResponse> requestAd(@Field("ad_unit_id") String adUnitId,
                               @Field("app_id") String appID,
                               @Field("location") String location,
                               @Field("api_token") String apiKey,
                               @Field("agent_info") String agentInfo,
                               @Field("android_id") String androidId
    );

    @FormUrlEncoded
    @POST("video-adRequest")
    Call<AdResponse> requestVideoAd(@Field("ad_unit_id") String adUnitId,
                                    @Field("app_id") String appID,
                                    @Field("location") String location,
                                    @Field("api_token") String apiKey,
                                    @Field("agent_info") String agentInfo,
                                    @Field("android_id") String androidId
    );

    @FormUrlEncoded
    @POST("banner-adRequest")
    Call<AdResponse> requestBanner(@Field("ad_unit_id") String adUnitId,
                                   @Field("app_id") String appID,
                                   @Field("location") String location,
                                   @Field("api_token") String apiKey,
                                   @Field("agent_info") String agentInfo,
                                   @Field("android_id") String androidId
    );

    @FormUrlEncoded
    @POST("ad-impression")
    Call<AdResponse> adImpression(@Field("ad_id") int adID,
                                  @Field("ad_unit_id") String adUnitId,
                                  @Field("app_id") String appId,
                                  @Field("api_token") String apiToken,
                                  @Field("ad_event_id") int adEventid,
                                  @Field("agent_info") String agentInfo,
                                  @Field("android_id") String androidId


    );

    @FormUrlEncoded
    @POST("ad-click")
    Call<AdResponse> clickAd(@Field("ad_id") int adID,
                             @Field("ad_unit_id") String adUnitId,
                             @Field("app_id") String appId,
                             @Field("api_token") String apiToken,
                             @Field("ad_event_id") int adEventid,
                             @Field("agent_info") String agentInfo,
                             @Field("android_id") String androidId,
                             @Field("diff") long time
    );


    @GET("getIp")
    Call<IpLocatoin> getLocation();




}
