package com.adfendo.beta.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.adfendo.beta.R;
import com.adfendo.beta.adapter.SliderImageAdapter;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.interfaces.VideoAdListener;
import com.adfendo.beta.model.AdResponse;
import com.adfendo.beta.model.IpLocation;
import com.adfendo.beta.model.Video;
import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.ResponseCode;
import com.adfendo.beta.utilities.Key;
import com.adfendo.beta.utilities.Utils;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private IpLocation ipLocation;

    public VideoAd(Context context, String uniteId) {
        this.context = context;
        addUnitId = uniteId;
    }

    public VideoAd() {
    }

    public void setVideoAdListener(VideoAdListener listener) {
        videoAdListener = listener;
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

    ProgressBar progressBar;
    TextView remainingTime;
    SliderImageAdapter adapter;
    DecimalFormat df;
    VideoView videoView;
    static boolean isShown = false;
    AdResponse adResponse;

    private long review = 0;
    private long mLastClickTime = 0;
    private long clickedTime = 0;
    private long impressionTime = 0;
    private boolean isVideoFinished = false;
    private static final String CURRENT_POSITION = "play_time";
    private static final String REM_TIME = "rem_time";
    private CountDownTimer countDownTimer;
    private long remainingTimeCount = 0;
    private long videoDuration = 0;
    private int mCurrentPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_ads);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setUpUI();
        isVideoFinished = false;
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                clickedTime = SystemClock.elapsedRealtime();
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
                if (videoAdListener != null) {
                    videoAdListener.onClosed();
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
        infoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.adfendo_url)));
                startActivity(browserIntent);
            }
        });
        if (savedInstanceState != null) {
            background.setBackgroundColor(randomAndroidColor);
            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            remainingTimeCount = savedInstanceState.getLong(REM_TIME);
        } else {
            randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            Log.e(TAG, "currentTimeOnCreate: " + mCurrentPosition);
        }

        MediaController controller = new MediaController(this);
        controller.setMediaPlayer(videoView);
        //controller.setVisibility(View.GONE);
        videoView.setMediaController(controller);

        display();
    }

    private void setUpUI() {
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
        videoView = (VideoView) findViewById(R.id.video);
    }

    private void startTimer(final long miliseconds) {
        remainingTime.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(miliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTime.setText(String.valueOf(millisUntilFinished / 1000));
                remainingTimeCount = millisUntilFinished;
            }

            public void onFinish() {
                isShown = true;
                remainingTime.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                isVideoFinished = true;
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
            initializePlayer();
            //new PlayVideo().execute();


            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClicked = false;
                    if (videoAdListener != null) {
                        videoAdListener.onClosed();
                    }
                    finish();
                }
            });
            if (video != null) {
                if (!video.getAppImage().equals("")) {
                    Glide.with(VideoAd.this).load(video.getAppImage()).into(appLogo);
                }
            }
            textViewAppName.setText(video.getAppName());
            textViewAppName.setSelected(true);
            textViewRating.setText(String.valueOf(df.format(Double.valueOf(video.getAppRating()))));
            textViewOfferedBy.setText(String.valueOf(video.getIntAdDescription1()));
            textViewStatusOfApp.setText(String.valueOf(video.getAppStatus()));
            review = Long.valueOf(video.getAppReview().replaceAll(",", ""));
            textViewTotalReview.setText(Utils.getRoughNumber(review));
            actionButton.setText(video.getAppButtonText());
            isIsImpressionSuccessfull = "1";
            descripotionOne.setText(video.getIntAdDescription());
        } else {
            if (videoAdListener != null)
                videoAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
        }
        impressionCall();
    }

    private void initializePlayer() {
        try {
            //String link = video.getVideoLink();
            String link = "https://www.demonuts.com/Demonuts/smallvideo.mp4";
            Uri videoUri = getMedia(link);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (mCurrentPosition > 0) {
                        videoView.seekTo(mCurrentPosition);
                        Log.e(TAG, "current: " + mCurrentPosition);
                    } else {
                        Log.e(TAG, "currentSeekTo: " + mCurrentPosition);
                        videoView.seekTo(0);
                    }
                    videoView.start();
                     
                    if (remainingTimeCount > 0) {
                        startTimer(remainingTimeCount);
                    } else {
                        startTimer(videoView.getDuration());
                    }

                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.stopPlayback();
                    isShown = true;

                }
            });

        } catch (Exception e) {

        }

    }

    private Uri getMedia(String link) {
        if (URLUtil.isValidUrl(link)) {
            // Media name is an external URL.
            return Uri.parse(link);
        } else {
            // Media name is a raw resource embedded in the app.
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + link);
        }
    }

    public void showVideoAd() {
        if (checkConnection()) {
            Intent intent = new Intent(this.context, VideoAd.class);
            this.context.startActivity(intent);
        } else {
            if (videoAdListener != null)
                videoAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
        }
    }

    public void requestAd() {
        if (checkConnection()) {
            if (Utils.location.equals(",")) {
                new GetLocation().execute();
            } else {
                new LoadAdInBackGround().execute();
            }
        } else {
            if (videoAdListener != null) {
                videoAdListener.onFailedToLoad(ResponseCode.ERROR_IN_NETWORK_CONNECTION);
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class LoadAdInBackGround extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... voids) {
            if (checkConnection()) {
                if (AppID.getAppId() != null) {
                    Key key = new Key();
                    apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<AdResponse> call = apiInterface.requestVideoAd(addUnitId,
                            AppID.getAppId(),
                            Utils.location, key.getApiKey(), Utils.getAgentInfo(), AdFendoInterstitialAd.getAndroidId());
                    call.enqueue(new Callback<AdResponse>() {
                        @Override
                        public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                            adResponse = response.body();
                            if (adResponse != null) {
                                if (adResponse.getCode() == ResponseCode.VALID_RESPONSE) {
                                    video = adResponse.getVideoAd();
                                    if (video != null) {
                                        listOfImages = new ArrayList<>();
                                        listOfImages.add(video.getIntAdImageLink1());
                                        listOfImages.add(video.getIntAdImageLink2());
                                        listOfImages.add(video.getIntAdImageLink3());
                                        setIsLoaded(true);
                                        if (videoAdListener != null) {
                                            videoAdListener.isLoaded(isLoaded());
                                        }
                                    } else {
                                        setIsLoaded(false);
                                        if (videoAdListener != null) {
                                            videoAdListener.isLoaded(isLoaded());
                                        }
                                    }
                                } else {
                                    if (videoAdListener != null) {
                                        switch (adResponse.getCode()) {
                                            case ResponseCode.VALID_RESPONSE:
                                                break;
                                            case ResponseCode.AD_NOT_AVAILABLE:
                                                videoAdListener.onFailedToLoad(ResponseCode.AD_NOT_AVAILABLE);
                                                break;
                                            case ResponseCode.INVALID_AD_UNIT_ID:
                                                videoAdListener.onFailedToLoad(ResponseCode.INVALID_AD_UNIT_ID);
                                                break;
                                            case ResponseCode.INVALID_API:
                                                videoAdListener.onFailedToLoad(ResponseCode.INVALID_API);
                                                break;
                                            case ResponseCode.APP_ID_NOT_ACTIVE:
                                                videoAdListener.onFailedToLoad(ResponseCode.APP_ID_NOT_ACTIVE);
                                                break;
                                            case ResponseCode.APP_NOT_ACTIVE:
                                                videoAdListener.onFailedToLoad(ResponseCode.APP_NOT_ACTIVE);
                                                break;
                                        }
                                        setIsLoaded(false);
                                    } else {
                                        setIsLoaded(false);
                                    }
                                }
                            } else {
                                setIsLoaded(false);
                                if (videoAdListener != null)
                                    videoAdListener.onFailedToLoad(ResponseCode.AD_NOT_AVAILABLE);
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
        if (isVideoFinished) {
            super.onBackPressed();
            if (videoAdListener != null)
                videoAdListener.onClosed();
            if (video != null)
                video = null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (video != null)
            video = null;
    }

    private void impressionCall() {
        if (checkConnection()) {
            if (isIsImpressionSuccessfull.equals("1")) {
                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Key key = new Key();
                String appId = AppID.getAppId();
                Call<AdResponse> call = apiInterface.adImpression(video.getAdId(), addUnitId, appId, key.getApiKey(), video.getAdEventId(), Utils.getAgentInfo(), AdFendoInterstitialAd.getAndroidId());
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
                        } else {
                            if (videoAdListener != null)
                                videoAdListener.onImpression();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        if (videoAdListener != null)
                            videoAdListener.onImpression();
                    }
                });
            }
        } else {
            if (videoAdListener != null)
                videoAdListener.onImpression();
        }
    }

    private void saveDataToServer(final boolean isClicked, String adID) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Key key = new Key();
        long diff = (impressionTime - clickedTime) / 1000;
        Call<AdResponse> call = apiInterface.clickAd(video.getAdId(), addUnitId, AppID.getAppId(), key.getApiKey(), video.getAdEventId(), Utils.getAgentInfo(), AdFendoInterstitialAd.getAndroidId(), diff);
        call.enqueue(new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                AdResponse adResponse = response.body();
                if (isClicked) {
                    if (adResponse.getCode() == 200) {
                        Log.d(TAG, "onResponse: " + "impresion success");
                    }
                }
                failedListener();
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                Log.d(AdFendo.class.getSimpleName(), "" + t.getMessage());
                failedListener();
            }
        });
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

    @SuppressLint("StaticFieldLeak")
    private class GetLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<IpLocation> call = apiInterface.getLocation();
            call.enqueue(new Callback<IpLocation>() {
                @Override
                public void onResponse(Call<IpLocation> call, Response<IpLocation> response) {
                    ipLocation = response.body();
                    if (ipLocation != null) {
                        if (!ipLocation.getCountryLong().isEmpty()) {
                            Utils.location = ipLocation.getCity() + "," + ipLocation.getCountryLong();
                        }
                    } else {
                        Utils.location = ",";
                    }
                    requestAd();
                }

                @Override
                public void onFailure(Call<IpLocation> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }

    private void failedListener() {
        isLoaded = false;
        setIsLoaded(isLoaded);
        if (videoAdListener != null) {
            videoAdListener.isLoaded(isLoaded);
            videoAdListener.onClosed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCurrentPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
        //new PlayVideo().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();

        //new PlayVideo().execute();
    }


    /*@Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("color", randomAndroidColor);
        onSaveInstanceState(outState);
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("color", randomAndroidColor);
        outState.putInt(CURRENT_POSITION, videoView.getCurrentPosition());
        Log.e(TAG, "onSavedCurrentTime: " + videoView.getCurrentPosition());
        outState.putLong(REM_TIME, remainingTimeCount);
        countDownTimer.cancel();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        } else {
            background.setBackgroundColor(randomAndroidColor);
            this.mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION);

            remainingTimeCount = savedInstanceState.getLong(REM_TIME);
            Log.e(TAG, "restoredCurrentTime: " + (remainingTimeCount / 100));

        }


    }

    private class PlayVideo extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //String link = video.getVideoLink();
            String link = "https://www.demonuts.com/Demonuts/smallvideo.mp4";
            Uri videoUri = getMedia(link);
            videoView.setVideoURI(videoUri);
            //videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    /*if (mCurrentPosition > 0) {
                        videoView.seekTo(mCurrentPosition);
                        Log.e(TAG, "current: " + mCurrentPosition);
                    } else {
                        Log.e(TAG, "currentSeekTo: " + mCurrentPosition);
                        videoView.seekTo(0);
                    }*/
                    //  videoView.start();
                    videoView.seekTo(mCurrentPosition);
                    videoView.start();
                    if (remainingTimeCount > 0) {
                        startTimer(remainingTimeCount);
                    } else {
                        startTimer(videoView.getDuration());
                    }
                }
            });
            do {
                mCurrentPosition = videoView.getCurrentPosition();
            } while (videoView.getCurrentPosition() <= videoView.getDuration());


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            videoView.stopPlayback();
            isShown = true;
        }
    }

}
