package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelUnit {

    @SerializedName("unit")
    private String unit;

    @SerializedName("id")
    private int id;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}