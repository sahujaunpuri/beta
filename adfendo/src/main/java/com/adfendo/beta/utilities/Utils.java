package com.adfendo.beta.utilities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.model.IpLocation;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utils {
    public static String location = ",";
    IpLocation ipLocation;

    public void getLocation() {
        new LocationInBackground().execute();
    }

    private static final String TAG = "Utils";

    @SuppressLint("StaticFieldLeak")
    private class LocationInBackground extends AsyncTask<Void, Void, Void> {
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
                            location = ipLocation.getCity() +","+ ipLocation.getCountryLong();
                        }
                    } else {
                        location = ",";
                    }
                }
                @Override
                public void onFailure(Call<IpLocation> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }
    public static String getRoughNumber(long value) {
        if (value <= 999) {
            return String.valueOf(value);
        }
        final String[] units = new String[]{"", "K", "M", "B", "T"};
        int digitGroups = (int) (Math.log10(value) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1000, digitGroups)) + "" + units[digitGroups];
    }
    public static String getAgentInfo(){
       return System.getProperty("http.agent");
    }
}
