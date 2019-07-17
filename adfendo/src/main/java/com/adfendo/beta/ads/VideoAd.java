package com.adfendo.beta.ads;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.adfendo.beta.R;
import com.adfendo.beta.adapter.SliderImageAdapter;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;

import com.adfendo.beta.interfaces.VideoAdListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.Video;
import com.adfendo.beta.utilities.AdFendo;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.ErrorCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoAd extends AppCompatActivity {
    private static final String TAG = "VideoAd";
    public static VideoAdListener videoAdListener;
    public static String addUnitId;
    ApiInterface apiInterface;
    private static boolean isLoaded = false;
    private String isIsImpressionSuccessfull = "";
    private boolean isClicked;
    public static Video video;
    public static List<String> listOfImages;
    ViewPager viewPager;
    Context context;

    public VideoAd(Context context, String uniteId) {
        this.context = context;
        if (Utils.location == null || Utils.location.equals("")) {
            Utils utils = new Utils();
            utils.getLocation();
        }
        addUnitId = uniteId;
        this.requestAd();
    }

    public VideoAd() {
    }

    public void setVideoAdListener(VideoAdListener videoAdListener) {
        VideoAd.videoAdListener = videoAdListener;
    }

    ImageView appLogo;
    TextView textViewAppName;
    TextView textViewRating, textViewOfferedBy, textViewTotalReview, descripotionOne;
    TextView textViewStatusOfApp;
    Button actionButton;
    Button cancelButton;
    LinearLayout background;
    private static int randomAndroidColor;
    int[] androidColors;
    ImageView infoButton;
    TextView infoTextView;
    int color;
    private static boolean infoShow = true;
    //progress bar
    int duration = 0;
    ProgressBar progressBar;
    TextView remainingTime;
    SliderImageAdapter adapter;
    DecimalFormat df;
    VideoView videoView;
    static boolean isShown = false;
    AdResponse adResponse;

    private long review = 0;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_ads);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        df = new DecimalFormat("##.##");
        descripotionOne = findViewById(R.id.description_one);
        infoButton = findViewById(R.id.infoButton);
        androidColors = getResources().getIntArray(R.array.androidcolors);
        actionButton = findViewById(R.id.install_button);
        cancelButton = findViewById(R.id.cancelButton);
        viewPager = findViewById(R.id.view_pager);
        appLogo = findViewById(R.id.app_logo_video);
        textViewAppName = findViewById(R.id.app_name_text_view);
        textViewRating = findViewById(R.id.text_view_rating_in_point);
        textViewOfferedBy = findViewById(R.id.text_view_offered_by);
        textViewStatusOfApp = findViewById(R.id.text_view_free_or_paid);
        textViewTotalReview = findViewById(R.id.text_view_total_review);
        background = findViewById(R.id.ad_details_background);
        infoTextView = findViewById(R.id.info_text);

        progressBar = findViewById(R.id.progress_circular);
        remainingTime = findViewById(R.id.remaining_time_text_view);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                saveDataToServer(true, String.valueOf(video.getAdId()));
                String[] appPackageName = video.getAppUrl().split("=");
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName[1])));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.getAppUrl())));
                }
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoAdListener.onClosed();
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

    private void startTimer(final long miliseconds) {
        remainingTime.setVisibility(View.VISIBLE);
        new CountDownTimer(miliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTime.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                isShown = true;
                remainingTime.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void display() {
        if (checkConnection()) {
            color = randomAndroidColor;
            background.setBackgroundColor(randomAndroidColor);
            actionButton.setTextColor(color);
            adapter = new SliderImageAdapter(this, listOfImages);
            viewPager.setAdapter(adapter);
            try {
                String link = video.getVideoLink();
                videoView = (VideoView) findViewById(R.id.video);
                MediaController mediaController = new MediaController(this);
                mediaController.setVisibility(View.GONE);
                mediaController.setAnchorView(videoView);
                Uri video = Uri.parse(link);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(video);
                videoView.start();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(link, new HashMap<String, String>());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                retriever.release();
                startTimer(timeInMillisec);
            } catch (Exception e) {
                // TODO: handle exception
                Log.d(TAG, e.getMessage());
            }

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClicked = false;
                    videoAdListener.onClosed();
                    finish();
                }
            });
            if (video.getAppImage() != null) {
                Glide.with(VideoAd.this).load(video.getAppImage()).into(appLogo);
            }
            textViewAppName.setText(video.getAppName());
            textViewRating.setText(String.valueOf(df.format(Double.valueOf(video.getAppRating()))));
            textViewOfferedBy.setText(video.getIntAdDescription1() + "");
            textViewStatusOfApp.setText(video.getAppStatus() + "");
            review = Long.valueOf(video.getAppReview().replaceAll(",", ""));
            textViewTotalReview.setText(Utils.getRoughNumber(review));
            actionButton.setText(video.getAppButtonText());
            isIsImpressionSuccessfull = "1";
            descripotionOne.setText(video.getIntAdDescription());
        } else {
            videoAdListener.onFailedToLoad(ErrorCode.ERROR_IN_NETWORK_CONNECTION);
        }
        impressionCall();
    }

    public void showVideoAd() {
        if (checkConnection()) {

            Intent intent = new Intent(this.context, VideoAd.class);
            this.context.startActivity(intent);
        } else {
            videoAdListener.onFailedToLoad(ErrorCode.ERROR_IN_NETWORK_CONNECTION);
        }
    }

    public void requestAd() {
        new LoadAdInBackGround().execute();
    }

    public class LoadAdInBackGround extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... voids) {
            if (checkConnection()) {
                if (AppID.getAppId() != null) {
                    Key key = new Key();
                    apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    String agent = Utils.getAgentInfo();
                    Call<AdResponse> call = apiInterface.requestVideoAd(addUnitId,
                            AppID.getAppId(),
                            Utils.location, key.getApiKey(),
                            agent);
                    call.enqueue(new Callback<AdResponse>() {
                        @Override
                        public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                            adResponse = response.body();
                            if (adResponse != null) {
                                switch (adResponse.getCode()) {
                                    case ErrorCode.VALID_RESPONSE:
                                        video = adResponse.getVideoAd();
                                        if (video != null) {
                                            listOfImages = new ArrayList<>();
                                            listOfImages.add(video.getIntAdImageLink1());
                                            listOfImages.add(video.getIntAdImageLink2());
                                            listOfImages.add(video.getIntAdImageLink3());
                                            setIsLoaded(true);
                                            videoAdListener.isLoaded(isLoaded());
                                        } else {
                                            setIsLoaded(false);
                                            videoAdListener.isLoaded(isLoaded());
                                        }
                                        break;
                                    case ErrorCode.AD_NOT_AVAILABLE:
                                        videoAdListener.onFailedToLoad(ErrorCode.AD_NOT_AVAILABLE);
                                        break;
                                    case ErrorCode.INVALID_AD_UNIT_ID:
                                        videoAdListener.onFailedToLoad(ErrorCode.INVALID_AD_UNIT_ID);
                                        break;
                                    case ErrorCode.INVALID_API:
                                        videoAdListener.onFailedToLoad(ErrorCode.INVALID_API);
                                        break;
                                    case ErrorCode.APP_ID_NOT_ACTIVE:
                                        videoAdListener.onFailedToLoad(ErrorCode.APP_ID_NOT_ACTIVE);
                                        break;
                                    case ErrorCode.APP_NOT_ACTIVE:
                                        videoAdListener.onFailedToLoad(ErrorCode.APP_NOT_ACTIVE);
                                        break;
                                }

                            } else {
                                videoAdListener.onFailedToLoad(ErrorCode.AD_NOT_AVAILABLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<AdResponse> call, Throwable t) {
                            isLoaded = false;

                            Log.d(VideoAd.class.getSimpleName(), t.getMessage());
                        }
                    });
                }
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoAdListener.onClosed();
        finish();
    }

    private void impressionCall() {
        if (checkConnection()) {
            if (isIsImpressionSuccessfull.equals("1")) {
                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Key key = new Key();
                String appId = AppID.getAppId();
                Call<AdResponse> call = apiInterface.adImpression(video.getAdId(), addUnitId, appId, key.getApiKey(), video.getAdEventId());
                call.enqueue(new Callback<AdResponse>() {
                    @Override
                    public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                        AdResponse adResponse = response.body();

                        if (adResponse.getImpression().equals("ok")) {
                            videoAdListener.onImpression();
                            isIsImpressionSuccessfull = "0";
                            isLoaded = false;
                        } else {
                            videoAdListener.onImpression();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        videoAdListener.onImpression();
                    }
                });
            }
        } else {
            videoAdListener.onImpression();
        }
    }

    private void saveDataToServer(final boolean isClicked, String adID) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Key key = new Key();
        Call<AdResponse> call = apiInterface.clickAd(video.getAdId(), addUnitId, AppID.getAppId(), key.getApiKey(), video.getAdEventId());
        call.enqueue(new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                AdResponse adResponse = response.body();
                if (isClicked) {
                    if (adResponse.getClick().equals("ok")) {
                        videoAdListener.onClosed();
                        isLoaded = false;
                        setIsLoaded(isLoaded);
                        videoAdListener.isLoaded(isLoaded);
                    }
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                Log.d(AdFendo.class.getSimpleName(), "" + t.getMessage());

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("color", randomAndroidColor);
        onSaveInstanceState(outState);
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
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
