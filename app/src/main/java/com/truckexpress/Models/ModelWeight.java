package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelWeight {

    @SerializedName("tyresid")
    private int tyresid;

    @SerializedName("weight")
    private int weight;

    @SerializedName("id")
    private int id;

    public int getTyresid() {
        return tyresid;
    }

    public void setTyresid(int tyresid) {
        this.tyresid = tyresid;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}