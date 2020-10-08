package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelState {

    @SerializedName("stateid")
    private int stateid;

    @SerializedName("state")
    private String state;

    public int getStateid() {
        return stateid;
    }

    public String getState() {
        return state;
    }
}