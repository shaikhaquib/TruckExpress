package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelExpense {

    @SerializedName("unit")
    private String unit;

    @SerializedName("rate")
    private double rate;

    @SerializedName("name")
    private String name;

    @SerializedName("unitid")
    private int unitid;

    @SerializedName("Id")
    private int id;

    @SerializedName("userid")
    private int userid;

    @SerializedName("status")
    private int status;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitid() {
        return unitid;
    }

    public void setUnitid(int unitid) {
        this.unitid = unitid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}