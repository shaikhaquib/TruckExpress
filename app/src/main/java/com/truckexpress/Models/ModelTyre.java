package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelTyre {

    @SerializedName("truckid")
    private int truckid;

    @SerializedName("nooftyres")
    private String nooftyres;

    @SerializedName("id")
    private int id;

    public int getTruckid() {
        return truckid;
    }

    public void setTruckid(int truckid) {
        this.truckid = truckid;
    }

    public String getNooftyres() {
        return nooftyres;
    }

    public void setNooftyres(String nooftyres) {
        this.nooftyres = nooftyres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}