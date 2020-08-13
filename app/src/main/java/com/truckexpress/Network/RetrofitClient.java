package com.truckexpress.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://truckexpress.live/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    private RetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

    }

    public static synchronized RetrofitClient getInstance(){
        if (mInstance==null)
            mInstance = new RetrofitClient();
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

}
