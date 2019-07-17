package com.adfendo.beta.utilities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.model.IpLocatoin;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utils {
    public static String location = "";
    IpLocatoin ipLocatoin;

    public void getLocation() {
        new LocationInBackground().execute();
    }

    private static final String TAG = "Utils";

    @SuppressLint("StaticFieldLeak")
    private class LocationInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<IpLocatoin> call = apiInterface.getLocation();
            call.enqueue(new Callback<IpLocatoin>() {
                @Override
                public void onResponse(Call<IpLocatoin> call, Response<IpLocatoin> response) {
                    ipLocatoin = response.body();
                    if (ipLocatoin != null) {
                        if (!ipLocatoin.getCountryLong().isEmpty()) {
                            location = ipLocatoin.getRegion() +","+ipLocatoin.getCountryLong();
                        }
                    } else {
                        location = ",";
                    }
                }
                @Override
                public void onFailure(Call<IpLocatoin> call, Throwable t) {
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
        final String[] units = new String[]{"", "K", "M", "B", "P"};
        int digitGroups = (int) (Math.log10(value) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1000, digitGroups)) + "" + units[digitGroups];
    }
    public static String getAgentInfo(){
       return System.getProperty("http.agent");
    }
}
