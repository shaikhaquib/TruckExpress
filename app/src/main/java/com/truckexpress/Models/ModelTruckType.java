package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelTruckType {

    @SerializedName("trucktype")
    private String trucktype;

    @SerializedName("photo")
    private String photo;

    @SerializedName("id")
    private int id;

    public String getTrucktype() {
        return trucktype;
    }

    public void setTrucktype(String trucktype) {
        this.trucktype = trucktype;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}