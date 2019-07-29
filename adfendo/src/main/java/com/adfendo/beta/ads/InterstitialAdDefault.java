package com.adfendo.beta.ads;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.adfendo.beta.R;
import com.adfendo.beta.adapter.SliderImageAdapter;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.interfaces.NetworkListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.InterstitialModel;
import com.adfendo.beta.utilities.AdFendo;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.Constants;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InterstitialAdDefault extends AppCompatActivity {
    private static final String TAG = "InterstitialModel";
    InterstitialAdListener interstitialAdListener;
    ApiInterface apiInterface;
    private String isIsImpressionSuccessfull = "";
    private static InterstitialModel interstitialModel;
    private static List<String> listOfImages;
    ViewPager viewPager;
    private long mLastClickTime = 0;
    //your activity listener interface
    private static InterstitialAdCloseListener onClosedListener;

    public void setListener(InterstitialAdCloseListener listener) {
        onClosedListener = listener;
    }

    public interface InterstitialAdCloseListener extends NetworkListener {
        void onCloseListener();
    }

    public InterstitialAdDefault() {
    }

    ImageView appLogo;
    TextView textViewAppName;
    TextView textViewRating, textViewOfferedBy, textViewTotalReview, descripotionOne;
    SliderImageAdapter adapter;
    TextView textViewStatusOfApp;
    Button actionButton;
    Button cancelButton;
    LinearLayout background;

    private static int randomAndroidColor;
    int[] androidColors;
    ImageView infoButton;
    ImageView infoClose;
    TextView infoTextView;
    int color;
    private static boolean infoShow = true;

    long review = 0;
    Utils utils;
    AdResponse adResponse;

    String adUnitId = "";
    long clickedTime;
    long differenceBetweenImpAndClick;
    private boolean isClicked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (getIntent().hasExtra(Constants.AD_INTERSTITIAL)) {
            interstitialModel = getIntent().getParcelableExtra(Constants.AD_INTERSTITIAL);
            adUnitId = getIntent().getStringExtra(Constants.AD_UNIT_IT);
        }


        listOfImages = new ArrayList<>();
        listOfImages.add(interstitialModel.getIntAdImageLink1());
        listOfImages.add(interstitialModel.getIntAdImageLink2());
        listOfImages.add(interstitialModel.getIntAdImageLink3());

        descripotionOne = findViewById(R.id.description_one);
        infoButton = findViewById(R.id.infoButton);
        androidColors = getResources().getIntArray(R.array.androidcolors);
        actionButton = findViewById(R.id.install_button);
        cancelButton = findViewById(R.id.cancelButton);
        viewPager = findViewById(R.id.view_pager);
        appLogo = findViewById(R.id.app_logo);
        textViewAppName = findViewById(R.id.app_name_text_view);
        textViewRating = findViewById(R.id.text_view_rating_in_point);
        textViewOfferedBy = findViewById(R.id.text_view_offered_by);
        textViewStatusOfApp = findViewById(R.id.text_view_free_or_paid);
        textViewTotalReview = findViewById(R.id.text_view_total_review);
        background = findViewById(R.id.ad_details_background);
        infoTextView = findViewById(R.id.info_text);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    isClicked = true;
                    clickedTime = SystemClock.elapsedRealtime();
                    differenceBetweenImpAndClick = Math.abs(clickedTime - AdFendoInterstitialAd.impressionMillisecond) / 1000;
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    saveDataToServer(true, interstitialModel.getAdId());
                    String[] appPackageName = interstitialModel.getAppUrl().split("=");
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName[1])));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(interstitialModel.getAppUrl())));
                    }
                } else {
                    if (onClosedListener != null) {
                        onClosedListener.onNetworkFailedListener();
                    }

                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClosedListener != null) {
                    onClosedListener.onCloseListener();
                }
                finish();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoShow) {
                    infoTextView.setVisibility(View.GONE);
                    infoShow = false;
                } else {
                    infoShow = true;
                    infoTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        if (savedInstanceState != null) {
            background.setBackgroundColor(randomAndroidColor);
        } else {
            randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        }
        display();

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


    private void display() {
        color = randomAndroidColor;
        background.setBackgroundColor(randomAndroidColor);
        actionButton.setTextColor(color);
        adapter = new SliderImageAdapter(this, listOfImages);
        viewPager.setAdapter(adapter);
        if (!interstitialModel.getAppImage().equals("")) {
            Glide.with(this).load(interstitialModel.getAppImage()).into(appLogo);
        }
        textViewAppName.setText(interstitialModel.getAppName());
        textViewRating.setText(String.valueOf(interstitialModel.getAppRating()));
        textViewOfferedBy.setText(String.valueOf(interstitialModel.getIntAdDescription1()));
        textViewStatusOfApp.setText(String.valueOf(interstitialModel.getAppStatus()));
        review = Long.valueOf(interstitialModel.getAppReview().replaceAll(",", ""));
        textViewTotalReview.setText(Utils.getRoughNumber(review));
        actionButton.setText(interstitialModel.getAppButtonText());
        isIsImpressionSuccessfull = "1";
        descripotionOne.setText(interstitialModel.getIntAdDescription());
        Timer timeTasker = new Timer();
        timeTasker.scheduleAtFixedRate(new TimeTasker(), 2000, 3000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("color", randomAndroidColor);
        onSaveInstanceState(outState);
    }

    private void saveDataToServer(final boolean isClicked, final int adID) {

        if (isClicked) {
            new AppInterstitialDataSaveInBackground().execute(adID);
        }
    }

    class TimeTasker extends TimerTask {
        @Override
        public void run() {
            InterstitialAdDefault.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else if (viewPager.getCurrentItem() == 1) {
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onClosedListener != null) {
            onClosedListener.onCloseListener();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onClosedListener != null) {
            onClosedListener = null;
        }
        interstitialModel = null;
    }

    class AppInterstitialDataSaveInBackground extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int adid = integers[0];
            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Key key = new Key();
            Call<AdResponse> call = apiInterface.clickAd(adid,
                    adUnitId,
                    AppID.getAppId(),
                    key.getApiKey(),
                    interstitialModel.getAdEventId(),
                    Utils.getAgentInfo(), AdFendo.getAndroidId(),
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
                    }
                }

                @Override
                public void onFailure(Call<AdResponse> call, Throwable t) {
                    Log.d(AdFendo.class.getSimpleName(), "" + t.getMessage());
                    if (onClosedListener != null) {
                        onClosedListener.onCloseListener();
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
                onClosedListener.onCloseListener();
            }
            finish();
        }
    }
}
