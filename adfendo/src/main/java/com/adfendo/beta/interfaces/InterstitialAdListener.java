package com.adfendo.beta.interfaces;

public interface InterstitialAdListener {
    void onClosed();
    void onFailedToLoad(int errorCode);
    void isLoaded(boolean isLoaded);
    void onImpression();
}
