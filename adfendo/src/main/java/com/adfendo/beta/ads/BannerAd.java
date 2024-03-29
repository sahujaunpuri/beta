package com.adfendo.beta.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.adfendo.beta.R;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.interfaces.BannerAdListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.Banner;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class BannerAd extends LinearLayout {

    public static BannerAdListener bannerAdListener;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(boolean ready) {
        isLoaded = ready;
    }

    ApiInterface apiInterface;
    private boolean isClicked;

    public void setOnBannerAdListener(BannerAdListener bannerAdListener) {
        BannerAd.bannerAdListener = bannerAdListener;
    }

    public static int mHeight = 0;
    public static int mWidth = 0;
    private View view;
    ImageView imageView;
    TextView appName;
    TextView textViewRatingPoint;
    TextView textViewTotalReview;
    TextView textViewFreeOrPaid;
    TextView textViewStar;
    Button installButton;
    long review = 0;
    LinearLayout containerLayout;
    public static Banner bannerAd;
    private static String adUnitId = "";
    Utils utils;
    AdResponse adResponse;
    boolean loadAd = false;
    private long mLastClickTime = 0;
    private long impressionTime = 0;

    public BannerAd(Context context, String adUnitId) {
        super(context);
        BannerAd.adUnitId = adUnitId;
        BannerAd.context = context;
        if (Utils.location.equals("")) {
            Utils utils = new Utils();
            utils.getLocation();
        }
    }

    public BannerAd(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!loadAd) {
            loadAd = true;
        }
        init(context, attrs, 0);
    }

    public BannerAd(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        utils = new Utils();
        init(context, attrs, defStyle);
    }

    private void init(final Context context, @Nullable AttributeSet attrs, @Nullable int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.banner_layout, this, true);

        initView(view);

        mHeight = (Resources.getSystem().getDisplayMetrics().heightPixels * 15) / 100;
        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        if (checkConnection()) {
            if (loadAd) {
                this.new RequestBannerAdInBackground().execute(view);
                loadAd = false;
            }
        }
        if (installButton != null) {
            installButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    String[] appPackageName = bannerAd.getAppUrl().split("=");
                    new OnAdClickInBackground().execute();
                    bannerAdListener.onClosed();

                    try {
                        BannerAd.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bannerAd.getAppUrl())));

                    } catch (android.content.ActivityNotFoundException anfe) {
                        BannerAd.context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName[1])));
                    }
                    new RequestBannerAdInBackground().execute(view);
                }
            });
        }
        view.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
    }

    private void initView(View view) {
        imageView = view.findViewById(R.id.app_logo);
        installButton = view.findViewById(R.id.install_button);
        appName = view.findViewById(R.id.app_name_text_view);
        textViewRatingPoint = view.findViewById(R.id.text_view_rating_in_point);
        textViewTotalReview = view.findViewById(R.id.text_view_total_review);
        textViewFreeOrPaid = view.findViewById(R.id.text_view_free_or_paid);
        textViewStar = view.findViewById(R.id.text_view_star);
        containerLayout = view.findViewById(R.id.container);
    }

    private static boolean isLoaded = false;
    private String isImpressionSuccessful = "";

    @SuppressLint("StaticFieldLeak")
    private class ImpressionInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (isImpressionSuccessful.equals("1")) {
                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Key key = new Key();
                String appId = AppID.getAppId();
                Call<AdResponse> call = apiInterface.adImpression(bannerAd.getAdId(), adUnitId, appId, key.getApiKey(), bannerAd.getAdEventId(),Utils.getAgentInfo(), AdFendoInterstitialAd.getAndroidId());
                call.enqueue(new Callback<AdResponse>() {
                    @Override
                    public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                        AdResponse adResponse = response.body();
                        if (adResponse.getCode() == ResponseCode.VALID_RESPONSE) {
                            Log.d(TAG, "onResponse: " + ResponseCode.VALID_RESPONSE);
                        } else if (adResponse.getCode() == ResponseCode.FRAUD_CLICK) {
                            Log.d(TAG, "onResponse: " + ResponseCode.FRAUD_CLICK);
                        } else if (adResponse.getCode() == ResponseCode.CLICK_ERROR) {
                            Log.d(TAG, "onResponse: " + ResponseCode.CLICK_ERROR);
                        }
                        impressionTime = SystemClock.elapsedRealtime();
                        isLoaded = false;
                    }

                    @Override
                    public void onFailure(Call<AdResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OnAdClickInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Key key = new Key();
            long diff = (impressionTime - SystemClock.elapsedRealtime())/1000;
            Call<AdResponse> call = apiInterface.clickAd(bannerAd.getAdId(), adUnitId, AppID.getAppId(), key.getApiKey(), bannerAd.getAdEventId(),
                    Utils.getAgentInfo(),AdFendoInterstitialAd.getAndroidId(),diff);
            call.enqueue(new Callback<AdResponse>() {
                @Override
                public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                    AdResponse adResponse = response.body();
                    if (isClicked) {
                        if (adResponse.getCode()== ResponseCode.VALID_RESPONSE) {
                            bannerAdListener.onClosed();
                            isLoaded = false;
                            setIsLoaded(isLoaded);
                            bannerAdListener.onRequest(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(BannerAd.class.getSimpleName(), "" + t.getMessage());
                }
            });

            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RequestBannerAdInBackground extends AsyncTask<View, Void, Void> {
        @Override
        protected Void doInBackground(final View... views) {
            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            String appId = AppID.getAppId();
            Key key = new Key();
            Call<AdResponse> call = apiInterface.requestBanner(adUnitId,
                    appId, Utils.location, key.getApiKey(), Utils.getAgentInfo(),AdFendoInterstitialAd.getAndroidId());
            call.enqueue(new Callback<AdResponse>() {
                @Override
                public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                    if (response.body() != null) {
                        adResponse = response.body();

                        bannerAd = response.body().getBannerAd();

                        if (adResponse.getCode() == ResponseCode.VALID_RESPONSE) {
                            bannerAdListener.onRequest(true);
                            appName.setText(bannerAd.getAppName());
                            textViewRatingPoint.setText(String.valueOf(bannerAd.getAppRating()));
                            review = Long.valueOf(bannerAd.getAppReview().replaceAll(",", ""));
                            textViewTotalReview.setText(Utils.getRoughNumber(review));
                            textViewFreeOrPaid.setText(String.valueOf(bannerAd.getAppStatus()));
                            Glide.with(view.getContext()).load(bannerAd.getAppImage()).into(imageView);
                            containerLayout.setVisibility(View.VISIBLE);
                            setIsLoaded(true);
                            bannerAdListener.isLoaded(isLoaded());
                            isImpressionSuccessful = "1";
                            new ImpressionInBackground().execute();
                        }
                        if (bannerAdListener != null){

                            switch (adResponse.getCode()) {
                                case ResponseCode.AD_NOT_AVAILABLE:
                                    bannerAdListener.onFailedToLoad(ResponseCode.AD_NOT_AVAILABLE);
                                    break;
                                case ResponseCode.APP_ID_NOT_INITIALIZED:
                                    bannerAdListener.onFailedToLoad(ResponseCode.APP_ID_NOT_INITIALIZED);
                                    break;
                                case ResponseCode.INVALID_API:
                                    bannerAdListener.onFailedToLoad(ResponseCode.INVALID_API);
                                    break;
                                case ResponseCode.APP_ID_NOT_ACTIVE:
                                    bannerAdListener.onFailedToLoad(ResponseCode.APP_ID_NOT_ACTIVE);
                                    break;
                                case ResponseCode.APP_NOT_ACTIVE:
                                    bannerAdListener.onFailedToLoad(ResponseCode.APP_NOT_ACTIVE);
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    isLoaded = false;
                    Log.d(BannerAd.class.getSimpleName(), t.getMessage());
                }
            });
            return null;
        }
    }

    public boolean checkConnection() {
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
}