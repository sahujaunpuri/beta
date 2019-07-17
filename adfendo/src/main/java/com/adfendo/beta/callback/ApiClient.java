package com.adfendo.beta.callback;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL="https://beta.adfendo.com/api/sdk/";
    public static final String BASE_URL_V2 = "https://beta.adfendo.com/api/sdk2/";
    public static final String LOCATION_URL = "https://ip-api.io/";
   static Retrofit retrofit;
    public static Retrofit getApiClient(){
        if (retrofit== null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_V2)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

    static Retrofit locationRetrofit=null;
    public static Retrofit getLocationClient(){
        if (locationRetrofit== null){
            locationRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return locationRetrofit;
    }
}
