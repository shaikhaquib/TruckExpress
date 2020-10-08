package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelSource {

    @SerializedName("routeid")
    private int routeid;

    @SerializedName("sorcename")
    private String sorcename;

    @SerializedName("userid")
    private Object userid;

    public int getRouteid() {
        return routeid;
    }

    public void setRouteid(int routeid) {
        this.routeid = routeid;
    }

    public String getSorcename() {
        return sorcename;
    }

    public void setSorcename(String sorcename) {
        this.sorcename = sorcename;
    }

    public Object getUserid() {
        return userid;
    }

    public void setUserid(Object userid) {
        this.userid = userid;
    }
}