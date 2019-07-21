package com.adfendo.beta.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class AdFendo {
    private static Context context;
    private static final String TAG = "AdFendo";

    public static  String ANDROID_ID = "";
    public AdFendo() {
    }

    public static void initialize(Context context, String appID) {
        AdFendo.context = context;
        AppID.setAppId(appID);
        Utils utils = new Utils();
        utils.getLocation();
        setAndroidID(AdFendo.context);
    }

    @SuppressLint("HardwareIds")
    private static void setAndroidID(Context context) {
        ANDROID_ID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getAndroidId(){
        return ANDROID_ID;
    }

}
