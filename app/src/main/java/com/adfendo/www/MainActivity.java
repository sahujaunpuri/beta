package com.adfendo.www;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adfendo.beta.ads.AdFendoInterstitialAd;

import com.adfendo.beta.ads.VideoAd;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.ads.AdFendo;
import com.adfendo.beta.interfaces.VideoAdListener;

public class MainActivity extends AppCompatActivity {
    AdFendoInterstitialAd adFendoInterstitialAd;
    private static final String TAG = "Debug";
    Button interstitialButton;
    Button videoButtno;

    VideoAd videoAd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interstitialButton = findViewById(R.id.interstetial);
        videoButtno = findViewById(R.id.video);
        AdFendo.initialize("pub-app-200439590");
//        adFendoInterstitialAd = new AdFendoInterstitialAd(this, "pub-ad-unit-id-200439590~959492310");
//        adFendoInterstitialAd.requestAd();
//
//        adFendoInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
//            @Override
//            public void onClosed() {
//                adFendoInterstitialAd.requestAd();
//            }
//
//            @Override
//            public void onFailedToLoad(int errorMessage) {
//                Toast.makeText(MainActivity.this, "onFailed " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void isLoaded(boolean isLoaded) {
//                Toast.makeText(MainActivity.this, "Interstitial isLoaded " + isLoaded, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onImpression() {
//                Toast.makeText(MainActivity.this, "onImpression called", Toast.LENGTH_SHORT).show();
//            }
//        });
//        interstitialButton.setOnClickListener(v -> {
//            if (adFendoInterstitialAd.isLoaded()) {
//                adFendoInterstitialAd.showAd();
//            } else {
//                Toast.makeText(MainActivity.this, "something went error", Toast.LENGTH_SHORT).show();
//            }
//        });

        videoAd = new VideoAd(this, "pub-ad-unit-id-200439590~326159499");

        videoButtno.setOnClickListener(view -> {
            if (videoAd.isLoaded()){
                videoAd.showVideoAd();
            }
        });
        videoAd.setVideoAdListener(new VideoAdListener() {
            @Override
            public void onTimeCount(int miliSeconds) {

            }

            @Override
            public void onClosed() {

            }

            @Override
            public void onFailedToLoad(int errorCode) {
                Toast.makeText(MainActivity.this, "Video Error"+errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void isLoaded(boolean isLoaded) {

                Toast.makeText(MainActivity.this, "Video loaded :"+isLoaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImpression() {

            }
        });
        videoAd.requestAd();

    }
}