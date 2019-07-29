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
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdFendoInterstitialAd implements InterstitialAdDefault.InterstitialAdCloseListener, WebInterstitial.WebAdCloseListener, CustomInterstitialActivity.CustomAdClosedListener {

    private static Context ctx;
    private String unitId;
    private boolean isLoaded = false;
    private static AdResponse adResponse;
    private static InterstitialModel interstitialModel;
    private static CustomInterstitialModel customInterstitialModel;
    private static WebInterstitialModel webInterstitialModel;
    private InterstitialAdListener interstitialAdListener;
    private String location = ",";
    private int eventId;
    private int adId;
    private Key key;
    InterstitialAdDefault defaultAd;
    CustomInterstitialActivity custom;
    WebInterstitial webInterstitial;
    private static final String TAG = "AdFendoInterstitialAd";
    public static long impressionMillisecond = 0;
    private long clickedMillisecond;


    public AdFendoInterstitialAd(Context context, String adUnitID) {
        AdFendoInterstitialAd.ctx = context;
        this.unitId = adUnitID;
        key = new Key();
    }

    public void setInterstitialAdListener(InterstitialAdListener interstitialAdListener) {
        this.interstitialAdListener = interstitialAdListener;
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
                    if (interstitialAdListener != null) {
                        interstitialAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
                    }
                }
            } else {
                if (interstitialAdListener != null) {
                    interstitialAdListener.onFailedToLoad(ResponseCode.APP_ID_NOT_INITIALIZED);
                }
            }
        } else {
            if (interstitialAdListener != null) {
                interstitialAdListener.onFailedToLoad(ResponseCode.INVALID_AD_UNIT_ID);
            }
        }
    }

    public void showAd() {
        if (checkConnection()) {
            if (isLoaded) {
                if (adResponse != null) {
                    switch (adResponse.getAdType()) {
                        case Constants.DEFAULT: {
                            if (interstitialAdListener != null){
                                defaultAd = new InterstitialAdDefault();
                                defaultAd.setListener(AdFendoInterstitialAd.this);
                            }
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
                            if (interstitialAdListener != null) {
                                custom = new CustomInterstitialActivity();
                                custom.setListener(AdFendoInterstitialAd.this);
                            }
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
                            if (interstitialAdListener != null) {
                                webInterstitial = new WebInterstitial();
                                webInterstitial.setListener(AdFendoInterstitialAd.this);
                            }
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
                } else {
                    if (interstitialAdListener != null) {
                        interstitialAdListener.onFailedToLoad(ResponseCode.SOMETHING_WENT_WRONG);
                    }
                }
            }
        } else {
            if (interstitialAdListener != null) {
                interstitialAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
            }
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


    @Override
    public void onCloseListener() {
        if (interstitialAdListener != null){
            interstitialAdListener.onClosed();
        }
    }


    @Override
    public void onCustomAdClosed() {
        if (interstitialAdListener != null){
            interstitialAdListener.onClosed();
        }
    }

    @Override
    public void onWebAdClosed() {
        if (interstitialAdListener != null){
            interstitialAdListener.onClosed();
        }
    }

    @Override
    public void onNetworkFailedListener() {
        interstitialAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
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
                            location = ipLocatoin.getRegion() + "," + ipLocatoin.getCountryLong();
                        }
                        requestForAd();
                    }
                }

                @Override
                public void onFailure(Call<IpLocatoin> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }

    @SuppressLint("HardwareIds")
    private void requestForAd() {
        new RequestInBackground().execute();

    }

    private void setErrorCodeListener(int code) {
        if (code == ResponseCode.AD_NOT_AVAILABLE) {
            interstitialAdListener.onFailedToLoad(ResponseCode.AD_NOT_AVAILABLE);
        } else if (code == ResponseCode.INVALID_API) {
            interstitialAdListener.onFailedToLoad(ResponseCode.INVALID_API);
        } else if (code == ResponseCode.PUBLISHER_NOT_ACTIVE) {
            interstitialAdListener.onFailedToLoad(ResponseCode.PUBLISHER_NOT_ACTIVE);
        } else if (code == ResponseCode.INVALID_AD_UNIT_ID) {
            interstitialAdListener.onFailedToLoad(ResponseCode.INVALID_AD_UNIT_ID);
        } else if (code == ResponseCode.APP_ID_NOT_ACTIVE) {
            interstitialAdListener.onFailedToLoad(ResponseCode.APP_ID_NOT_ACTIVE);
        } else if (code == ResponseCode.APP_NOT_ACTIVE) {
            interstitialAdListener.onFailedToLoad(ResponseCode.APP_NOT_ACTIVE);
        }
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
                    if (interstitialAdListener != null) {
                        interstitialAdListener.onImpression();
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }

    class RequestInBackground extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            String agent = Utils.getAgentInfo();
            String deviceId = "";
            try {
                deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                Log.d(TAG, "requestForAd: " + e.getMessage());
            }
            Call<AdResponse> call = apiInterface.requestAd(unitId, AppID.getAppId(), Utils.location, key.getApiKey(), agent, deviceId);
            call.enqueue(new Callback<AdResponse>() {
                @Override
                public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                    adResponse = response.body();
                    if (adResponse != null) {
                        if (adResponse.getCode() == ResponseCode.VALID_RESPONSE) {
                            setIsLoaded(true);
                            if (interstitialAdListener != null) {
                                interstitialAdListener.isLoaded(isLoaded());
                            }
                            switch (adResponse.getAdType()) {
                                case Constants.DEFAULT:
                                    interstitialModel = adResponse.getInterstitial();
                                    adId = interstitialModel.getAdId();
                                    eventId = interstitialModel.getAdEventId();
                                    break;
                                case Constants.CUSTOM:
                                    customInterstitialModel = adResponse.getCustomInterstitialAd();
                                    adId = customInterstitialModel.getAdId();
                                    eventId = customInterstitialModel.getAdEventId();
                                    break;
                                case Constants.WEB:
                                    webInterstitialModel = adResponse.getWebInterstitialModel();
                                    adId = webInterstitialModel.getAdId();
                                    eventId = webInterstitialModel.getAdEventId();
                                    break;
                            }
                        } else {
                            if (interstitialAdListener != null) {
                                setErrorCodeListener(adResponse.getCode());
                            }
                            setIsLoaded(false);
                        }
                    }
                }
                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    if (interstitialAdListener != null) {
                        interstitialAdListener.onFailedToLoad(ResponseCode.SOMETHING_WENT_WRONG);
                    }
                }
            });
            return null;
        }
    }
}
