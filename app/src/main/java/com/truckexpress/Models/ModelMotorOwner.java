package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelMotorOwner {

    @SerializedName("dateofbirth")
    private String dateofbirth;

    @SerializedName("pincode")
    private int pincode;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("address")
    private String address;

    @SerializedName("roleid")
    private Object roleid;

    @SerializedName("address1")
    private Object address1;

    @SerializedName("phone2")
    private String phone2;

    @SerializedName("gstno")
    private Object gstno;

    @SerializedName("phone3")
    private Object phone3;

    @SerializedName("driverlicence")
    private String driverlicence;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("phone1")
    private Object phone1;

    @SerializedName("photourl")
    private Object photourl;

    @SerializedName("motorownerid")
    private int motorownerid;

    @SerializedName("tbldriverid")
    private Object tbldriverid;

    @SerializedName("companyname")
    private Object companyname;

    @SerializedName("UserId")
    private int userId;

    @SerializedName("id")
    private int id;

    @SerializedName("doj")
    private String doj;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private Object username;

    public String getDateofbirth() {
        return dateofbirth;
    }

    public int getPincode() {
        return pincode;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getAddress() {
        return address;
    }

    public Object getRoleid() {
        return roleid;
    }

    public Object getAddress1() {
        return address1;
    }

    public String getPhone2() {
        return phone2;
    }

    public Object getGstno() {
        return gstno;
    }

    public Object getPhone3() {
        return phone3;
    }

    public String getDriverlicence() {
        return driverlicence;
    }

    public String getLastname() {
        return lastname;
    }

    public Object getPhone1() {
        return phone1;
    }

    public Object getPhotourl() {
        return photourl;
    }

    public int getMotorownerid() {
        return motorownerid;
    }

    public Object getTbldriverid() {
        return tbldriverid;
    }

    public Object getCompanyname() {
        return companyname;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getDoj() {
        return doj;
    }

    public String getEmail() {
        return email;
    }

    public Object getUsername() {
        return username;
    }
}