package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelDestination {

    @SerializedName("routeid")
    private int routeid;

    @SerializedName("destination")
    private String destination;

    @SerializedName("userid")
    private Object userid;

    public int getRouteid() {
        return routeid;
    }

    public void setRouteid(int routeid) {
        this.routeid = routeid;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Object getUserid() {
        return userid;
    }

    public void setUserid(Object userid) {
        this.userid = userid;
    }
}