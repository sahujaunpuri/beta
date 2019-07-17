package com.adfendo.beta.utilities;

import android.content.Context;

public class AdFendo {
    private static Context context;
    private static final String TAG = "AdFendo";
    public AdFendo() {
    }
    public static void initialize(Context context, String appID) {
        AdFendo.context = context;
        AppID.setAppId(appID);
        Utils utils = new Utils();
        utils.getLocation();
    }
}
