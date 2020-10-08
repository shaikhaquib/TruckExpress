package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class Modellunguage {

    @SerializedName("id")
    private int id;

    @SerializedName("lunguagename")
    private String lunguagename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLunguagename() {
        return lunguagename;
    }

    public void setLunguagename(String lunguagename) {
        this.lunguagename = lunguagename;
    }
}