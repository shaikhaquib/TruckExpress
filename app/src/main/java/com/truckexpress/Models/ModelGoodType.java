package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelGoodType {

    @SerializedName("goodsname")
    private String goodsname;

    @SerializedName("id")
    private int id;

    @SerializedName("value")
    private String value;

    @SerializedName("userid")
    private String userid;

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}