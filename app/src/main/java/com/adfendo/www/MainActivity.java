package com.adfendo.www;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adfendo.beta.ads.AdFendoInterstitialAd;
import com.adfendo.beta.ads.BannerAd;
import com.adfendo.beta.ads.VideoAd;
import com.adfendo.beta.interfaces.InterstitialAdListener;
import com.adfendo.beta.utilities.AdFendo;

public class    MainActivity extends AppCompatActivity {
    AdFendoInterstitialAd adFendoInterstitialAd;
    private static final String TAG = "Debug";

    Button interstetialButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interstetialButton = findViewById(R.id.interstetial);

        AdFendo.initialize(this, "pub-app-314377906");



        adFendoInterstitialAd = new AdFendoInterstitialAd(this, "pub-ad-unit-id-314377906~919981366");
        adFendoInterstitialAd.requestAd();

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
                Toast.makeText(MainActivity.this, "Interstitial isLoaded "+isLoaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImpression() {
                Toast.makeText(MainActivity.this, "onImpression called", Toast.LENGTH_SHORT).show();
            }
        });
        interstetialButton.setOnClickListener(v -> {
            if (adFendoInterstitialAd.isLoaded()) {
                adFendoInterstitialAd.showAd();
            } else {
                Toast.makeText(MainActivity.this, "something went error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}