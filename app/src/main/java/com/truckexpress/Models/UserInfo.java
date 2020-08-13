package com.truckexpress.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class UserInfo{

	@SerializedName("UIMessage")
	private String uIMessage;

	@SerializedName("roleid")
	private int roleid;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("otp")
	private int otp;
	
	@PrimaryKey
	@SerializedName("id")
	private int id;

	@SerializedName("fullname")
	private String fullname;

	@SerializedName("error_status")
	private String errorStatus;

	@SerializedName("userid")
	private int userid;

	@SerializedName("status")
	private String status;

	public void setUIMessage(String uIMessage){
		this.uIMessage = uIMessage;
	}

	public String getUIMessage(){
		return uIMessage;
	}

	public void setRoleid(int roleid){
		this.roleid = roleid;
	}

	public int getRoleid(){
		return roleid;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setOtp(int otp){
		this.otp = otp;
	}

	public int getOtp(){
		return otp;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setFullname(String fullname){
		this.fullname = fullname;
	}

	public String getFullname(){
		return fullname;
	}

	public void setErrorStatus(String errorStatus){
		this.errorStatus = errorStatus;
	}

	public String getErrorStatus(){
		return errorStatus;
	}

	public void setUserid(int userid){
		this.userid = userid;
	}

	public int getUserid(){
		return userid;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}