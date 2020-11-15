package com.truckexpress.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelCurrentBooking implements Serializable {

    @SerializedName("totalexpenses")
    private int totalexpenses;

    @SerializedName("totalweight")
    private double totalweight;

    @SerializedName("expensesunloading")
    private String expensesunloading;

    @SerializedName("lotunitid")
    private int lotunitid;

    @SerializedName("source")
    private String source;

	@SerializedName("userid")
	private int userid;

    @SerializedName("bookingid")
    private int bookingid;

    @SerializedName("advance")
    private String advance;

    @SerializedName("lotunitname")
    private String lotunitname;

    @SerializedName("phone1")
    private String phone1;

    @SerializedName("paymentmode")
    private int paymentmode;

    @SerializedName("todate")
    private String todate;

    @SerializedName("balance")
    private String balance;

	@SerializedName("shrotageallowance")
	private String shrotageallowance;

	@SerializedName("unitname")
	private String unitname;

	@SerializedName("trucktypename")
	private String trucktypename;

	@SerializedName("totalfreight")
	private String totalfreight;

	@SerializedName("bookingtype")
	private String bookingtype;

    @SerializedName("nooftyres")
    private String nooftyres;

    @SerializedName("pickupdate")
    private String pickupdate;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("count")
    private int count;

    @SerializedName("weight")
    private String weight;

    @SerializedName("statusname")
    private String statusname;

    @SerializedName("paymentname")
    private String paymentname;

	@SerializedName("latestbidid")
	private int latestbidid;

	@SerializedName("lotweight")
	private int lotweight;

	@SerializedName("company_name")
	private String companyName;

	@SerializedName("name")
	private String name;

	@SerializedName("amountpaid")
	private String amountpaid;

	@SerializedName("goodstype")
	private int goodstype;

	@SerializedName("status")
	private String status;

	@SerializedName("truckid")
	private int truckid;

	@SerializedName("nooftrucks")
	private int nooftrucks;

	@SerializedName("tyerid")
	private String tyerid;

	@SerializedName("bookingtypename")
	private String bookingtypename;

	@SerializedName("destination")
	private String destination;

	@SerializedName("nooftruckdemand")
	private String nooftruckdemand;

	@SerializedName("goodsname")
	private String goodsname;

	@SerializedName("pickupaddress")
	private String pickupaddress;

	@SerializedName("trucktypeid")
	private String trucktypeid;

	@SerializedName("checklistcount")
	private int checklistcount;

	@SerializedName("corporate_contact_email")
	private String corporateContactEmail;

	@SerializedName("transporterid")
	private int transporterid;

	@SerializedName("unloadingpoint")
	private String unloadingpoint;

	@SerializedName("rate")
	private String rate;

	@SerializedName("waitingtime")
	private String waitingtime;

	@SerializedName("checklistname")
	private String checklistname;

	@SerializedName("truckname")
	private String truckname;

	@SerializedName("noofdays")
	private int noofdays;

	@SerializedName("corporate_contact_person")
	private String corporateContactPerson;

	@SerializedName("checklist")
	private String checklist;

	@SerializedName("corporateid")
	private int corporateid;

	@SerializedName("dropaddress")
	private String dropaddress;

	@SerializedName("unloadingno")
	private String unloadingno;

    @SerializedName("weightunloading")
    private String weightunloading;

    @SerializedName("unitid")
    private int unitid;

    @SerializedName("truckweightid")
    private String truckweightid;

    @SerializedName("fullname")
    private String fullname;

    public int getTotalexpenses() {
        return totalexpenses;
    }

    public void setTotalexpenses(int totalexpenses) {
        this.totalexpenses = totalexpenses;
    }

    public double getTotalweight() {
        return totalweight;
    }

    public void setTotalweight(double totalweight) {
        this.totalweight = totalweight;
    }

    public String getExpensesunloading() {
        return expensesunloading;
    }

    public void setExpensesunloading(String expensesunloading) {
        this.expensesunloading = expensesunloading;
    }

    public int getLotunitid() {
        return lotunitid;
    }

    public void setLotunitid(int lotunitid) {
        this.lotunitid = lotunitid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public String getAdvance() {
        return advance;
    }

    public void setAdvance(String advance) {
        this.advance = advance;
    }

    public String getLotunitname() {
        return lotunitname;
    }

    public void setLotunitname(String lotunitname) {
        this.lotunitname = lotunitname;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public int getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(int paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getShrotageallowance() {
        return shrotageallowance;
    }

    public void setShrotageallowance(String shrotageallowance) {
        this.shrotageallowance = shrotageallowance;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getTrucktypename() {
        return trucktypename;
    }

    public void setTrucktypename(String trucktypename) {
        this.trucktypename = trucktypename;
    }

    public String getTotalfreight() {
        return totalfreight;
    }

    public void setTotalfreight(String totalfreight) {
        this.totalfreight = totalfreight;
    }

    public String getBookingtype() {
        return bookingtype;
    }

    public void setBookingtype(String bookingtype) {
        this.bookingtype = bookingtype;
    }

    public String getNooftyres() {
        return nooftyres;
    }

    public void setNooftyres(String nooftyres) {
        this.nooftyres = nooftyres;
    }

    public String getPickupdate() {
        return pickupdate;
    }

    public void setPickupdate(String pickupdate) {
        this.pickupdate = pickupdate;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setStatusname(String statusname) {
        this.statusname = statusname;
    }

    public String getStatusname() {
        return statusname;
    }

    public String getPaymentname() {
        return paymentname;
    }

    public void setPaymentname(String paymentname) {
        this.paymentname = paymentname;
    }

    public int getLatestbidid() {
        return latestbidid;
    }

    public void setLatestbidid(int latestbidid) {
        this.latestbidid = latestbidid;
    }

    public int getLotweight() {
        return lotweight;
    }

    public void setLotweight(int lotweight) {
        this.lotweight = lotweight;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(String amountpaid) {
        this.amountpaid = amountpaid;
    }

    public int getGoodstype() {
        return goodstype;
    }

    public void setGoodstype(int goodstype) {
        this.goodstype = goodstype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTruckid() {
        return truckid;
    }

    public void setTruckid(int truckid) {
        this.truckid = truckid;
    }

    public int getNooftrucks() {
        return nooftrucks;
    }

    public void setNooftrucks(int nooftrucks) {
        this.nooftrucks = nooftrucks;
    }

    public String getTyerid() {
        return tyerid;
    }

    public void setTyerid(String tyerid) {
        this.tyerid = tyerid;
    }

    public String getBookingtypename() {
        return bookingtypename;
    }

    public void setBookingtypename(String bookingtypename) {
        this.bookingtypename = bookingtypename;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNooftruckdemand() {
        return nooftruckdemand;
    }

    public void setNooftruckdemand(String nooftruckdemand) {
        this.nooftruckdemand = nooftruckdemand;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getTrucktypeid() {
        return trucktypeid;
    }

    public void setTrucktypeid(String trucktypeid) {
        this.trucktypeid = trucktypeid;
    }

    public int getChecklistcount() {
        return checklistcount;
    }

    public void setChecklistcount(int checklistcount) {
        this.checklistcount = checklistcount;
    }

    public String getCorporateContactEmail() {
        return corporateContactEmail;
    }

    public void setCorporateContactEmail(String corporateContactEmail) {
        this.corporateContactEmail = corporateContactEmail;
    }

    public int getTransporterid() {
        return transporterid;
    }

    public void setTransporterid(int transporterid) {
        this.transporterid = transporterid;
    }

    public String getUnloadingpoint() {
        return unloadingpoint;
    }

    public void setUnloadingpoint(String unloadingpoint) {
        this.unloadingpoint = unloadingpoint;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWaitingtime() {
        return waitingtime;
    }

    public void setWaitingtime(String waitingtime) {
        this.waitingtime = waitingtime;
    }

    public String getChecklistname() {
        return checklistname;
    }

    public void setChecklistname(String checklistname) {
        this.checklistname = checklistname;
    }

    public String getTruckname() {
        return truckname;
    }

    public void setTruckname(String truckname) {
        this.truckname = truckname;
    }

    public int getNoofdays() {
        return noofdays;
    }

    public void setNoofdays(int noofdays) {
        this.noofdays = noofdays;
    }

    public String getCorporateContactPerson() {
        return corporateContactPerson;
    }

    public void setCorporateContactPerson(String corporateContactPerson) {
        this.corporateContactPerson = corporateContactPerson;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public int getCorporateid() {
        return corporateid;
    }

    public void setCorporateid(int corporateid) {
        this.corporateid = corporateid;
    }

    public String getDropaddress() {
        return dropaddress;
    }

    public void setDropaddress(String dropaddress) {
        this.dropaddress = dropaddress;
    }

    public String getUnloadingno() {
        return unloadingno;
    }

    public void setUnloadingno(String unloadingno) {
        this.unloadingno = unloadingno;
    }

    public void setWeightunloading(String weightunloading) {
        this.weightunloading = weightunloading;
    }

    public String getWeightunloading() {
        return weightunloading;
    }

    public void setUnitid(int unitid) {
        this.unitid = unitid;
    }

	public int getUnitid(){
        return unitid;
    }

    public void setTruckweightid(String truckweightid) {
        this.truckweightid = truckweightid;
    }

    public String getTruckweightid() {
        return truckweightid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}