package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelChecklist {

    @SerializedName("cheklist")
    private String cheklist;

    @SerializedName("id")
    private int id;

    @SerializedName("userid")
    private int userid;

    public String getCheklist() {
        return cheklist;
    }

    public void setCheklist(String cheklist) {
        this.cheklist = cheklist;
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
}