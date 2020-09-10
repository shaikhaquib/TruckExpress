package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

public class ModelCurrentBooking{

	@SerializedName("truckid")
	private int truckid;

	@SerializedName("totalexpenses")
	private int totalexpenses;

	@SerializedName("nooftrucks")
	private int nooftrucks;

	@SerializedName("expensesunloading")
	private String expensesunloading;

	@SerializedName("tyerid")
	private String tyerid;

	@SerializedName("destination")
	private String destination;

	@SerializedName("nooftruckdemand")
	private String nooftruckdemand;

	@SerializedName("goodsname")
	private String goodsname;

	@SerializedName("source")
	private String source;

	@SerializedName("userid")
	private int userid;

	@SerializedName("pickupaddress")
	private String pickupaddress;

	@SerializedName("bookingid")
	private int bookingid;

	@SerializedName("trucktypeid")
	private String trucktypeid;

	@SerializedName("transporterid")
	private int transporterid;

	@SerializedName("paymentmode")
	private int paymentmode;

	@SerializedName("unloadingpoint")
	private String unloadingpoint;

	@SerializedName("balance")
	private int balance;

	@SerializedName("rate")
	private String rate;

	@SerializedName("unitname")
	private String unitname;

	@SerializedName("trucktypename")
	private String trucktypename;

	@SerializedName("waitingtime")
	private String waitingtime;

	@SerializedName("totalfreight")
	private String totalfreight;

	@SerializedName("checklistname")
	private String checklistname;

	@SerializedName("truckname")
	private String truckname;

	@SerializedName("nooftyres")
	private String nooftyres;

	@SerializedName("corporate_contact_person")
	private String corporateContactPerson;

	@SerializedName("pickupdate")
	private String pickupdate;

	@SerializedName("customer_phone")
	private String customerPhone;

	@SerializedName("weight")
	private String weight;

	@SerializedName("statusname")
	private String statusname;

	@SerializedName("paymentname")
	private String paymentname;

	@SerializedName("checklist")
	private String checklist;

	@SerializedName("latestbidid")
	private int latestbidid;

	@SerializedName("dropaddress")
	private String dropaddress;

	@SerializedName("unloadingno")
	private String unloadingno;

	@SerializedName("weightunloading")
	private String weightunloading;

	@SerializedName("company_name")
	private String companyName;

	@SerializedName("name")
	private String name;

	@SerializedName("unitid")
	private int unitid;

	@SerializedName("truckweightid")
	private String truckweightid;

	@SerializedName("goodstype")
	private int goodstype;

	@SerializedName("status")
	private String status;

	public void setTruckid(int truckid){
		this.truckid = truckid;
	}

	public int getTruckid(){
		return truckid;
	}

	public void setTotalexpenses(int totalexpenses){
		this.totalexpenses = totalexpenses;
	}

	public int getTotalexpenses(){
		return totalexpenses;
	}

	public void setNooftrucks(int nooftrucks){
		this.nooftrucks = nooftrucks;
	}

	public int getNooftrucks(){
		return nooftrucks;
	}

	public void setExpensesunloading(String expensesunloading){
		this.expensesunloading = expensesunloading;
	}

	public String getExpensesunloading(){
		return expensesunloading;
	}

	public void setTyerid(String tyerid){
		this.tyerid = tyerid;
	}

	public String getTyerid(){
		return tyerid;
	}

	public void setDestination(String destination){
		this.destination = destination;
	}

	public String getDestination(){
		return destination;
	}

	public void setNooftruckdemand(String nooftruckdemand){
		this.nooftruckdemand = nooftruckdemand;
	}

	public String getNooftruckdemand(){
		return nooftruckdemand;
	}

	public void setGoodsname(String goodsname){
		this.goodsname = goodsname;
	}

	public String getGoodsname(){
		return goodsname;
	}

	public void setSource(String source){
		this.source = source;
	}

	public String getSource(){
		return source;
	}

	public void setUserid(int userid){
		this.userid = userid;
	}

	public int getUserid(){
		return userid;
	}

	public void setPickupaddress(String pickupaddress){
		this.pickupaddress = pickupaddress;
	}

	public String getPickupaddress(){
		return pickupaddress;
	}

	public void setBookingid(int bookingid){
		this.bookingid = bookingid;
	}

	public int getBookingid(){
		return bookingid;
	}

	public void setTrucktypeid(String trucktypeid){
		this.trucktypeid = trucktypeid;
	}

	public String getTrucktypeid(){
		return trucktypeid;
	}

	public void setTransporterid(int transporterid){
		this.transporterid = transporterid;
	}

	public int getTransporterid(){
		return transporterid;
	}

	public void setPaymentmode(int paymentmode){
		this.paymentmode = paymentmode;
	}

	public int getPaymentmode(){
		return paymentmode;
	}

	public void setUnloadingpoint(String unloadingpoint){
		this.unloadingpoint = unloadingpoint;
	}

	public String getUnloadingpoint(){
		return unloadingpoint;
	}

	public void setBalance(int balance){
		this.balance = balance;
	}

	public int getBalance(){
		return balance;
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

	public void setTrucktypename(String trucktypename){
		this.trucktypename = trucktypename;
	}

	public String getTrucktypename(){
		return trucktypename;
	}

	public void setWaitingtime(String waitingtime){
		this.waitingtime = waitingtime;
	}

	public String getWaitingtime(){
		return waitingtime;
	}

	public void setTotalfreight(String totalfreight){
		this.totalfreight = totalfreight;
	}

	public String getTotalfreight(){
		return totalfreight;
	}

	public void setChecklistname(String checklistname){
		this.checklistname = checklistname;
	}

	public String getChecklistname(){
		return checklistname;
	}

	public void setTruckname(String truckname){
		this.truckname = truckname;
	}

	public String getTruckname(){
		return truckname;
	}

	public void setNooftyres(String nooftyres){
		this.nooftyres = nooftyres;
	}

	public String getNooftyres(){
		return nooftyres;
	}

	public void setCorporateContactPerson(String corporateContactPerson){
		this.corporateContactPerson = corporateContactPerson;
	}

	public String getCorporateContactPerson(){
		return corporateContactPerson;
	}

	public void setPickupdate(String pickupdate){
		this.pickupdate = pickupdate;
	}

	public String getPickupdate(){
		return pickupdate;
	}

	public void setCustomerPhone(String customerPhone){
		this.customerPhone = customerPhone;
	}

	public String getCustomerPhone(){
		return customerPhone;
	}

	public void setWeight(String weight){
		this.weight = weight;
	}

	public String getWeight(){
		return weight;
	}

	public void setStatusname(String statusname){
		this.statusname = statusname;
	}

	public String getStatusname(){
		return statusname;
	}

	public void setPaymentname(String paymentname){
		this.paymentname = paymentname;
	}

	public String getPaymentname(){
		return paymentname;
	}

	public void setChecklist(String checklist){
		this.checklist = checklist;
	}

	public String getChecklist(){
		return checklist;
	}

	public void setLatestbidid(int latestbidid){
		this.latestbidid = latestbidid;
	}

	public int getLatestbidid(){
		return latestbidid;
	}

	public void setDropaddress(String dropaddress){
		this.dropaddress = dropaddress;
	}

	public String getDropaddress(){
		return dropaddress;
	}

	public void setUnloadingno(String unloadingno){
		this.unloadingno = unloadingno;
	}

	public String getUnloadingno(){
		return unloadingno;
	}

	public void setWeightunloading(String weightunloading){
		this.weightunloading = weightunloading;
	}

	public String getWeightunloading(){
		return weightunloading;
	}

	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}

	public String getCompanyName(){
		return companyName;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUnitid(int unitid){
		this.unitid = unitid;
	}

	public int getUnitid(){
		return unitid;
	}

	public void setTruckweightid(String truckweightid){
		this.truckweightid = truckweightid;
	}

	public String getTruckweightid(){
		return truckweightid;
	}

	public void setGoodstype(int goodstype){
		this.goodstype = goodstype;
	}

	public int getGoodstype(){
		return goodstype;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}