package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelGoods {

    @SerializedName("price")
    private String price;

    @SerializedName("goodsname")
    private String goodsname;

    @SerializedName("id")
    private int id;

    @SerializedName("units")
    private String units;

    @SerializedName("userid")
    private int userid;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}