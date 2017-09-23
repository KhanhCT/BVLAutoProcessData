package vn.com.daisy.excel;

import java.util.Date;
public class Bvnt {
	
	private Date date;
	private String companyCode;
	private String companyName;
	private int transAmount;
	private double moneyAmount;
	public Bvnt(Date date, String companyCode, String companyName, int transAmount, double moneyAmount) {
		super();
		this.date = date;
		this.companyCode = companyCode;
		this.companyName = companyName;
		this.transAmount = transAmount;
		this.moneyAmount = moneyAmount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public int getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(int transAmount) {
		this.transAmount = transAmount;
	}
	public double getMoneyAmount() {
		return moneyAmount;
	}
	public void setMoneyAmount(double moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	
	

}
