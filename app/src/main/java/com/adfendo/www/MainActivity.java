package com.adfendo.www;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adfendo.beta.ads.AdFendoInterstitialAd;
import com.adfendo.beta.ads.BannerAd;
import com.adfendo.beta.ads.VideoAd;
import com.adfendo.beta.interfaces.BannerAdListener;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.interfaces.VideoAdListener;
import com.adfendo.beta.utilities.AdFendo;

public class MainActivity extends AppCompatActivity {
    AdFendoInterstitialAd adFendoInterstitialAd;
    private static final String TAG = "Debug";

    Button interstetialButton;
    Button bannerButton;
    BannerAd bannerAd;
    Button videoButton;


    VideoAd videoAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interstetialButton = findViewById(R.id.interstetial);

        AdFendo.initialize(this, "pub-app-704441634");
        videoButton = findViewById(R.id.video_ad);

        videoAd = new VideoAd(this, "pub-ad-unit-id-704441634~152658631");
        videoAd.setVideoAdListener(new VideoAdListener() {
            @Override
            public void onTimeCount(int milliSecond) {

            }

            @Override
            public void onClosed() {
                videoAd.requestAd();
                Toast.makeText(MainActivity.this, "onClosed Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedToLoad(int errorMessage) {
//                Toast.makeText(MainActivity.this, "onFailed :"+errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void isLoaded(boolean isLoaded) {
//                Toast.makeText(MainActivity.this, "onLoad :"+isLoaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImpression() {
//                Toast.makeText(MainActivity.this, "onImpression ", Toast.LENGTH_SHORT).show();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoAd.isLoaded()) {
                    videoAd.showVideoAd();
                }
            }
        });

        adFendoInterstitialAd = new AdFendoInterstitialAd(this, "pub-ad-unit-id-704441634~910330028");
        adFendoInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
            @Override
            public void onClosed() {
                adFendoInterstitialAd.requestAd();
//               Toast.makeText(MainActivity.this, "onClosed called ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedToLoad(int errorMessage) {
                Toast.makeText(MainActivity.this, "onFailed " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void isLoaded(boolean isLoaded) {
//                Toast.makeText(MainActivity.this, "Interstitial isLoaded "+isLoaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImpression() {
//                Toast.makeText(MainActivity.this, "onImpression called", Toast.LENGTH_SHORT).show();
            }
        });
        adFendoInterstitialAd.requestAd();
        interstetialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adFendoInterstitialAd.isLoaded()) {
                    adFendoInterstitialAd.showAd();
                } else {
                    Toast.makeText(MainActivity.this, "something went error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BannerAd bannerAd = new BannerAd(this, "pub-ad-unit-id-704441634~759229480");
        bannerAd.setOnBannerAdListener(new BannerAdListener() {
            @Override
            public void onClosed() {

            }

            @Override
            public void isLoaded(boolean isLoaded) {

            }

            @Override
            public void onImpression() {

            }

            @Override
            public void onRequest(boolean isSuccessful) {
            }

            @Override
            public void onFailedToLoad(int errorMessage) {
            }
        });
    }
}