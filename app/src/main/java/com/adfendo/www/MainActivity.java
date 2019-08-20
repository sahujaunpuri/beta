package com.adfendo.www;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.adfendo.beta.ads.AdFendo;
import com.adfendo.beta.ads.AdFendoInterstitialAd;
import com.adfendo.beta.ads.BannerAd;
import com.adfendo.beta.ads.VideoAd;
import com.adfendo.beta.interfaces.BannerAdListener;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.interfaces.VideoAdListener;

public class MainActivity extends AppCompatActivity {
    AdFendoInterstitialAd adFendoInterstitialAd;
    private static final String TAG = "MainActivity";
    Button interstitialButton;
    Button videoButton;
    VideoAd videoAd;
    BannerAd bannerAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interstitialButton = findViewById(R.id.interstetial);
        videoButton = findViewById(R.id.video);
        AdFendo.initialize("pub-app-200439590");

        bannerAd = findViewById(R.id.banner);
        BannerAd banner= new BannerAd(this,"pub-ad-unit-id-200439590~756146633");
        banner.setOnBannerAdListener(new BannerAdListener() {
            @Override
            public void onRequest(boolean isSuccessful) {

            }

            @Override
            public void onClosed() {

            }

            @Override
            public void onFailedToLoad(int errorCode) {

            }

            @Override
            public void isLoaded(boolean isLoaded) {

            }

            @Override
            public void onImpression() {

            }
        });



        adFendoInterstitialAd = new AdFendoInterstitialAd(this, "pub-ad-unit-id-200439590~959492310");
        adFendoInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
            @Override
            public void onClosed() {
                adFendoInterstitialAd.requestAd();
               // Toast.makeText(MainActivity.this, "onClosed", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailedToLoad(int errorMessage) {
               // Toast.makeText(MainActivity.this, "onFailed " + errorMessage, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void isLoaded(boolean isLoaded) {
                //Toast.makeText(MainActivity.this, "Interstitial isLoaded " + isLoaded, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onImpression() {
                //Toast.makeText(MainActivity.this, "onImpression called", Toast.LENGTH_SHORT).show();
            }


        });
        adFendoInterstitialAd.requestAd();
        interstitialButton.setOnClickListener(v -> {
            if (adFendoInterstitialAd.isLoaded()) {
                adFendoInterstitialAd.showAd();
            }else {
                adFendoInterstitialAd.requestAd();
            }
        });


        videoAd = new VideoAd(this, "pub-ad-unit-id-200439590~326159499");
        videoAd.setVideoAdListener(new VideoAdListener() {
            @Override
            public void onTimeCount(int miliSeconds) {
            }

            @Override
            public void onClosed() {
                videoAd.requestAd();
            }
            @Override
            public void onFailedToLoad(int errorCode) {
                //Toast.makeText(MainActivity.this, "Video Error"+errorCode, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void isLoaded(boolean isLoaded) {
                //Toast.makeText(MainActivity.this, "Video loaded :"+isLoaded, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onImpression() {
                //Toast.makeText(MainActivity.this, "Video onImpression", Toast.LENGTH_SHORT).show();
            }
        });
        videoAd.requestAd();
        videoButton.setOnClickListener(view -> {
            if (videoAd.isLoaded()){
                videoAd.showVideoAd();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}