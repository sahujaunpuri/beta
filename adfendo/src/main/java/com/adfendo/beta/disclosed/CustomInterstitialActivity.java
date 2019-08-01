package com.adfendo.beta.disclosed;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adfendo.beta.R;
import com.adfendo.beta.adapter.SliderImageAdapter;
import com.adfendo.beta.ads.AdFendoInterstitialAd;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.interfaces.NetworkListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.CustomInterstitialModel;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.Constants;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomInterstitialActivity extends AppCompatActivity {

    private ImageView appLogo;
    private TextView textViewAppName;
    private TextView textViewRating, textViewTotalReview;
    private SliderImageAdapter adapter;
    private TextView textViewStatusOfApp;
    private TextView description;
    private Button actionButton;
    private Button cancelButton;
    private LinearLayout imageContainer;

    private Utils utils;
    private AdResponse adResponse;
    private String adUnitId = "";
    private boolean isClicked = false;

    private static CustomInterstitialModel customInterstitialAd;
    private ImageView fullImage;
    private long mLastClickTime = 0;
    long clickedTime;
    private  CustomAdClosedListener onClosedListener;
    private long differenceBetweenImpAndClick = 0;
    private static final String TAG = "CustomInterstitialActiv";

    public void setListener(CustomAdClosedListener listener) {
        onClosedListener = listener;
    }

    public interface CustomAdClosedListener extends NetworkListener {
        void onCustomAdClosed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_interstitial);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (getIntent().hasExtra(Constants.AD_CUSTOM_INTERSTITIAL)) {
            customInterstitialAd = getIntent().getParcelableExtra(Constants.AD_CUSTOM_INTERSTITIAL);
            adUnitId = getIntent().getStringExtra(Constants.AD_UNIT_IT);
        }
        fullImage = findViewById(R.id.full_image);
        actionButton = findViewById(R.id.install_button);
        cancelButton = findViewById(R.id.cancelButton);
        description = findViewById(R.id.text_view_description);
        imageContainer = findViewById(R.id.custom_image_container);
        appLogo = findViewById(R.id.app_logo);
        textViewAppName = findViewById(R.id.app_name_text_view);
        textViewRating = findViewById(R.id.text_view_rating_in_point);
        textViewTotalReview = findViewById(R.id.text_view_total_review);
        textViewStatusOfApp = findViewById(R.id.text_view_free_or_paid);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedTime = SystemClock.elapsedRealtime();
                differenceBetweenImpAndClick = Math.abs(clickedTime - AdFendoInterstitialAd.impressionMillisecond) / 1000;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                saveDataToServer(true, customInterstitialAd.getAdId());
                String[] appPackageName = customInterstitialAd.getAppUrl().split("=");
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName[1])));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customInterstitialAd.getAppUrl())));
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClosedListener != null) {
                    onClosedListener.onCustomAdClosed();
                }
                finish();
            }
        });
        if (checkConnection()) {
            display();
        }
    }

    private void display() {
        Glide.with(this).load(customInterstitialAd.getIntAdImageLink()).into(fullImage);
        description.setText(customInterstitialAd.getIntAdDescription());
        actionButton.setText(customInterstitialAd.getAppButtonText());
        Glide.with(this).load(customInterstitialAd.getAppImage()).into(appLogo);
        textViewAppName.setText(customInterstitialAd.getAppName());
        textViewRating.setText(customInterstitialAd.getAppRating());
        textViewTotalReview.setText(customInterstitialAd.getAppReview());
    }

    private void saveDataToServer(final boolean isClicked, final int adID) {
        if (isClicked) {
            new CustomInterstitialDataSaveInBackground().execute(adID);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onClosedListener != null) {
            onClosedListener.onCustomAdClosed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onClosedListener != null) {
            onClosedListener = null;
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

    class CustomInterstitialDataSaveInBackground extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int adid = integers[0];
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Key key = new Key();
            Call<AdResponse> call = apiInterface.clickAd(adid, adUnitId, AppID.getAppId(), key.getApiKey(), customInterstitialAd.getAdEventId(), Utils.getAgentInfo(), AdFendoInterstitialAd.getAndroidId(), differenceBetweenImpAndClick);
            call.enqueue(new Callback<AdResponse>() {
                @Override
                public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                    AdResponse adResponse = response.body();
                    if (isClicked) {
                        if (adResponse.getCode() == ResponseCode.VALID_RESPONSE) {
                            Log.d(TAG, "onResponse: " + ResponseCode.VALID_RESPONSE);
                        } else if (adResponse.getCode() == ResponseCode.FRAUD_CLICK) {
                            Log.d(TAG, "onResponse: " + ResponseCode.FRAUD_CLICK);
                        } else if (adResponse.getCode() == ResponseCode.CLICK_ERROR) {
                            Log.d(TAG, "onResponse: " + ResponseCode.CLICK_ERROR);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(CustomInterstitialActivity.class.getSimpleName(), "" + t.getMessage());
                    if (onClosedListener != null) {
                        onClosedListener.onCustomAdClosed();
                    }
                    finish();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (onClosedListener != null) {
                onClosedListener.onCustomAdClosed();
            }
            finish();
        }
    }
}
