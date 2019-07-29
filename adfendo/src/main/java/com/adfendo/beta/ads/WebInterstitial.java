package com.adfendo.beta.ads;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adfendo.beta.R;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;

import com.adfendo.beta.interfaces.NetworkListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.WebInterstitialModel;
import com.adfendo.beta.utilities.AdFendo;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.Constants;
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WebInterstitial extends AppCompatActivity {
    private WebInterstitialModel webInterstitialModel;
    private String adUnitId = "";
    private ImageView imageViewWeb;
    private Button cancelButton;
    private Button visitButton;
    private long clickedTime;
    private boolean isClicked;

    private static final String TAG = "WebInterstitial";
    long differenceBetweenImpAndClick;
    private static WebAdCloseListener webAdCloseListener;

    public interface WebAdCloseListener extends NetworkListener {
        void onWebAdClosed();
    }

    public void setListener(WebAdCloseListener listener) {
        webAdCloseListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_interstitial);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (getIntent().hasExtra(Constants.AD_WEB_INTERSTITIAL)) {
            webInterstitialModel = getIntent().getParcelableExtra(Constants.AD_WEB_INTERSTITIAL);
            adUnitId = getIntent().getStringExtra(Constants.AD_UNIT_IT);
        }
        imageViewWeb = findViewById(R.id.image_view_web);
        cancelButton = findViewById(R.id.cancelButton);
        visitButton = findViewById(R.id.visitButton);
        if (webInterstitialModel.getWebAdImageLink() != null)
            Glide.with(this).load(webInterstitialModel.getWebAdImageLink()).into(imageViewWeb);
        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                clickedTime = SystemClock.elapsedRealtime();
                differenceBetweenImpAndClick = Math.abs(clickedTime - AdFendoInterstitialAd.impressionMillisecond) / 1000;
                Toast.makeText(WebInterstitial.this, "" + differenceBetweenImpAndClick, Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webInterstitialModel.getWebUrl()));
                startActivity(browserIntent);
                saveDataToServer(isClicked, webInterstitialModel.getAdId());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webAdCloseListener != null) {
                    webAdCloseListener.onWebAdClosed();
                }
                finish();
            }
        });
    }

    private void saveDataToServer(final boolean isClicked, final int adID) {
        if (isClicked) {
            new WebInterstitialDataSaveInBackground().execute(adID);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webAdCloseListener != null) {
            webAdCloseListener.onWebAdClosed();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webAdCloseListener != null) {
            webAdCloseListener = null;
        }
    }

    class WebInterstitialDataSaveInBackground extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int adid = integers[0];
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Key key = new Key();
            Call<AdResponse> call = apiInterface.clickAd(
                    adid,
                    adUnitId,
                    AppID.getAppId(),
                    key.getApiKey(),
                    webInterstitialModel.getAdEventId(),
                    Utils.getAgentInfo(),
                    AdFendo.getAndroidId(),
                    differenceBetweenImpAndClick
            );
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
                        isClicked = false;
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(AdFendo.class.getSimpleName(), "" + t.getMessage());
                    isClicked = false;
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (webAdCloseListener != null) {
                webAdCloseListener.onWebAdClosed();
            }
            finish();
        }
    }
}
