package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class BIDLog{

	@SerializedName("msg")
	private String msg;

	@SerializedName("transporterid")
	private int transporterid;

	@SerializedName("acceptedbytransporter")
	private int acceptedbytransporter;

	@SerializedName("nooftrucks")
	private String nooftrucks;

	@SerializedName("rate")
	private String rate;

	@SerializedName("unitname")
	private String unitname;

	@SerializedName("unitid")
	private int unitid;

	@SerializedName("acceptedbycorporate")
	private int acceptedbycorporate;

	@SerializedName("bookingid")
	private int bookingid;

	@SerializedName("status")
	private int status;

	@SerializedName("corporateid")
	private int corporateid;

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setTransporterid(int transporterid){
		this.transporterid = transporterid;
	}

	public int getTransporterid(){
		return transporterid;
	}

	public void setAcceptedbytransporter(int acceptedbytransporter){
		this.acceptedbytransporter = acceptedbytransporter;
	}

	public int getAcceptedbytransporter(){
		return acceptedbytransporter;
	}

	public void setNooftrucks(String nooftrucks){
		this.nooftrucks = nooftrucks;
	}

	public String getNooftrucks(){
		return nooftrucks;
	}

	public void setRate(String rate){
		this.rate = rate;
	}

	public String getRate(){
		return rate;
	}

	public void setUnitname(String unitname){
		this.unitname = unitname;
	}

	public String getUnitname(){
		return unitname;
	}

	public void setUnitid(int unitid){
		this.unitid = unitid;
	}

	public int getUnitid(){
		return unitid;
	}

	public void setAcceptedbycorporate(int acceptedbycorporate){
		this.acceptedbycorporate = acceptedbycorporate;
	}

	public int getAcceptedbycorporate(){
		return acceptedbycorporate;
	}

	public void setBookingid(int bookingid){
		this.bookingid = bookingid;
	}

	public int getBookingid(){
		return bookingid;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	public void setCorporateid(int corporateid){
		this.corporateid = corporateid;
	}

	public int getCorporateid(){
		return corporateid;
	}
}