package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelCity {

    @SerializedName("city_name")
    private String cityName;

    @SerializedName("state_id")
    private int stateId;

    @SerializedName("city_id")
    private int cityId;

    public String getCityName() {
        return cityName;
    }

    public int getStateId() {
        return stateId;
    }

    public int getCityId() {
        return cityId;
    }
}