package com.adfendo.beta.ads;

import com.adfendo.beta.utilities.AppID;
import com.adfendo.beta.utilities.Utils;

public class AdFendo {

    public AdFendo() {
    }
    public static void initialize(String appID) {
        AppID.setAppId(appID);
        Utils utils = new Utils();
        utils.getLocation();
    }



}
