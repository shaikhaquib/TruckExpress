package com.truckexpress.Network;


import com.google.gson.JsonObject;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.Models.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("Booking/bookinglist")
    Call<JSONArray> bookinglist(@Body JSONObject body);

}
