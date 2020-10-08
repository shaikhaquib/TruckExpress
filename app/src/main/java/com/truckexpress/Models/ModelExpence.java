package com.truckexpress.Models;

public class ModelExpence {
    private String uIMessage;
    private String unit;
    private String rate;
    private String name;
    private int bookingid;

    public String getUIMessage() {
        return uIMessage;
    }

    public void setUIMessage(String uIMessage) {
        this.uIMessage = uIMessage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
}
