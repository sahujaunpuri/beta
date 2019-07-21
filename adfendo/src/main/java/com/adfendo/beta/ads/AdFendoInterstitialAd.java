package com.adfendo.beta.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.CustomInterstitialModel;
import com.adfendo.beta.model.InterstitialModel;
import com.adfendo.beta.model.IpLocatoin;
import com.adfendo.beta.model.WebInterstitialModel;
import com.adfendo.beta.utilities.AdFendo;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.Constants;
import com.adfendo.beta.utilities.ErrorCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdFendoInterstitialAd {

    private static Context ctx;
    private String unitId;
    private boolean isLoaded = false;
    private static AdResponse adResponse;
    private static InterstitialModel interstitialModel;
    private static CustomInterstitialModel customInterstitialModel;
    private static WebInterstitialModel webInterstitialModel;
    static InterstitialAdListener interstitialAdListener;
    private String location= ",";
    private int eventId;
    private int adId;
    private Key key;
    private static final String TAG = "AdFendoInterstitialAd";
    public static long impressionMillisecond = 0;
    private long clickedMillisecond;
    public AdFendoInterstitialAd(Context context, String adUnitID) {
        AdFendoInterstitialAd.ctx = context;
        this.unitId = adUnitID;
        key = new Key();
    }
    public void setInterstitialAdListener(InterstitialAdListener interstitialAdListener) {
        AdFendoInterstitialAd.interstitialAdListener = interstitialAdListener;
    }
    public void requestAd() {
        if (!unitId.equals("")) {
            if (!AppID.getAppId().equals("")) {
                if (checkConnection()) {
                    if (location.equals(",")) {
                        new LocationInBackground().execute();
                    } else {
                        requestForAd();
                    }
                } else {
                    interstitialAdListener.onFailedToLoad(ErrorCode.ERROR_IN_NETWORK_CONNECTION);
                }
            } else {
                interstitialAdListener.onFailedToLoad(ErrorCode.APP_ID_NOT_INITIALIZED);
            }
        } else {
            interstitialAdListener.onFailedToLoad(ErrorCode.INVALID_AD_UNIT_ID);
        }
    }

    public void showAd() {
        if (checkConnection()) {
            if (isLoaded) {
                if (adResponse != null){
                    switch (adResponse.getAdType()) {
                        case Constants.DEFAULT: {
                            interstitialModel = adResponse.getInterstitial();
                            Intent intent = new Intent(ctx, InterstitialAdDefault.class);
                            intent.putExtra(Constants.AD_UNIT_IT, unitId);
                            intent.putExtra(Constants.AD_INTERSTITIAL, interstitialModel);
                            ctx.startActivity(intent);
                            setIsLoaded(false);
                            adResponse = null;
                            break;
                        }
                        case Constants.CUSTOM: {
                            //todo custom ad interstitial
                            customInterstitialModel = adResponse.getCustomInterstitialAd();
                            Intent intent = new Intent(ctx, CustomInterstitialActivity.class);
                            intent.putExtra(Constants.AD_UNIT_IT, unitId);
                            intent.putExtra(Constants.AD_CUSTOM_INTERSTITIAL, customInterstitialModel);
                            ctx.startActivity(intent);
                            setIsLoaded(false);
                            adResponse = null;
                            break;
                        }
                        case Constants.WEB: {
                            //todo web ad interstitial
                            webInterstitialModel = adResponse.getWebInterstitialModel();
                            Intent intent = new Intent(ctx, WebInterstitial.class);
                            intent.putExtra(Constants.AD_UNIT_IT, unitId);
                            intent.putExtra(Constants.AD_WEB_INTERSTITIAL, webInterstitialModel);
                            ctx.startActivity(intent);
                            setIsLoaded(false);
                            adResponse = null;
                            break;
                        }
                    }
                    new ImpressionInBackground().execute();
                }else{
                    interstitialAdListener.onFailedToLoad(ErrorCode.SOMETHING_WENT_WRONG);
                }
            }
        } else {
            interstitialAdListener.onFailedToLoad(ErrorCode.ERROR_IN_NETWORK_CONNECTION);
        }
    }

    private void setIsLoaded(boolean b) {
        this.isLoaded = b;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private boolean checkConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    @SuppressLint("StaticFieldLeak")
    private class LocationInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getLocationClient().create(ApiInterface.class);
            Call<IpLocatoin> call = apiInterface.getLocation();
            call.enqueue(new Callback<IpLocatoin>() {
                @Override
                public void onResponse(Call<IpLocatoin> call, Response<IpLocatoin> response) {
                    IpLocatoin ipLocatoin = response.body();
                    if (ipLocatoin != null) {
                        if (!ipLocatoin.getCountryLong().isEmpty()) {
                            location = ipLocatoin.getRegion()+","+ipLocatoin.getCountryLong();
                        }
                        requestForAd();
                    }
                }
                @Override
                public void onFailure(Call<IpLocatoin> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t.getMessage());
                }
            });
            return null;
        }
    }

    private void requestForAd() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String agent = Utils.getAgentInfo();
        String deviceId = "";
        try {
            deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            Log.d(TAG, "requestForAd: "+e.getMessage());
        }
        Call<AdResponse> call = apiInterface.requestAd(unitId, AppID.getAppId(), Utils.location, key.getApiKey(),agent,deviceId);
        call.enqueue(new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                adResponse = response.body();
                switch (adResponse.getCode()) {
                    case ErrorCode.AD_NOT_AVAILABLE:
                        interstitialAdListener.onFailedToLoad(ErrorCode.AD_NOT_AVAILABLE);
                        setIsLoaded(false);
                        break;
                    case ErrorCode.INVALID_API:
                        interstitialAdListener.onFailedToLoad(ErrorCode.INVALID_API);
                        setIsLoaded(false);
                        break;
                        case ErrorCode.PUBLISHER_NOT_ACTIVE:
                        interstitialAdListener.onFailedToLoad(ErrorCode.PUBLISHER_NOT_ACTIVE);
                        setIsLoaded(false);
                        break;
                    case ErrorCode.INVALID_AD_UNIT_ID:
                        interstitialAdListener.onFailedToLoad(ErrorCode.INVALID_AD_UNIT_ID);
                        setIsLoaded(false);
                        break;
                    case ErrorCode.APP_ID_NOT_ACTIVE:
                        interstitialAdListener.onFailedToLoad(ErrorCode.APP_ID_NOT_ACTIVE);
                        break;
                    case ErrorCode.APP_NOT_ACTIVE:
                        interstitialAdListener.onFailedToLoad(ErrorCode.APP_NOT_ACTIVE);
                        break;
                    case ErrorCode.VALID_RESPONSE:
                        switch (adResponse.getAdType()) {
                            case Constants.DEFAULT:
                                setIsLoaded(true);
                                interstitialAdListener.isLoaded(isLoaded());
                                interstitialModel = adResponse.getInterstitial();
                                adId = interstitialModel.getAdId();
                                eventId = interstitialModel.getAdEventId();
                                break;
                            case Constants.CUSTOM:
                                setIsLoaded(true);
                                interstitialAdListener.isLoaded(isLoaded());
                                customInterstitialModel = adResponse.getCustomInterstitialAd();
                                adId = customInterstitialModel.getAdId();
                                eventId = customInterstitialModel.getAdEventId();
                                break;
                            case Constants.WEB:
                                setIsLoaded(true);
                                interstitialAdListener.isLoaded(isLoaded());
                                webInterstitialModel = adResponse.getWebInterstitialModel();
                                adId = webInterstitialModel.getAdId();
                                eventId = webInterstitialModel.getAdEventId();
                                break;
                        }
                        break;
                }
            }
            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                interstitialAdListener.onFailedToLoad(ErrorCode.SOMETHING_WENT_WRONG);
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private class ImpressionInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Key key = new Key();
            String appId = AppID.getAppId();
            Call<AdResponse> call = apiInterface.adImpression(
                    adId,
                    unitId,
                    appId,
                    key.getApiKey(),
                    eventId, Utils.getAgentInfo(), AdFendo.getAndroidId()
            );
            call.enqueue(new Callback<AdResponse>() {
                @Override
                public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                    impressionMillisecond = SystemClock.elapsedRealtime();
                    interstitialAdListener.onImpression();
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }
}
