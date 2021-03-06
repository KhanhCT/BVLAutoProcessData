package vn.com.daisy.DAO;
//Generated Sep 28, 2016 9:13:03 AM by Hibernate Tools 4.3.1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BvlCompany generated by hbm2java
 */
@Entity
@Table(name = "BVL_COMPANY", schema = "ebanktest")
public class BvlCompany implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BvlCompanyId id;
	private String bcoCompanyName;
	private String bcoTaxCode;
	private String bcoBankAccount;
	private String bcoProvinceName;
	private String bcoAddress;
	private String bcoMobile;
	private String bcoFax;
	private String bcoEmail;
	private String bcoCompanyBvlifeId;

	public BvlCompany() {
	}

	public BvlCompany(BvlCompanyId id) {
		this.id = id;
	}

	public BvlCompany(BvlCompanyId id, String bcoCompanyName, String bcoTaxCode, String bcoBankAccount,
			String bcoProvinceName, String bcoAddress, String bcoMobile, String bcoFax, String bcoEmail,
			String bcoCompanyBvlifeId) {
		this.id = id;
		this.bcoCompanyName = bcoCompanyName;
		this.bcoTaxCode = bcoTaxCode;
		this.bcoBankAccount = bcoBankAccount;
		this.bcoProvinceName = bcoProvinceName;
		this.bcoAddress = bcoAddress;
		this.bcoMobile = bcoMobile;
		this.bcoFax = bcoFax;
		this.bcoEmail = bcoEmail;
		this.bcoCompanyBvlifeId = bcoCompanyBvlifeId;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "bcoId", column = @Column(name = "BCO_ID", nullable = false, precision = 22, scale = 0) ),
			@AttributeOverride(name = "bcoCompanyCode", column = @Column(name = "BCO_COMPANY_CODE", nullable = false, length = 9) ) })
	public BvlCompanyId getId() {
		return this.id;
	}

	public void setId(BvlCompanyId id) {
		this.id = id;
	}

	@Column(name = "BCO_COMPANY_NAME", length = 100)
	public String getBcoCompanyName() {
		return this.bcoCompanyName;
	}

	public void setBcoCompanyName(String bcoCompanyName) {
		this.bcoCompanyName = bcoCompanyName;
	}

	@Column(name = "BCO_TAX_CODE", length = 30)
	public String getBcoTaxCode() {
		return this.bcoTaxCode;
	}

	public void setBcoTaxCode(String bcoTaxCode) {
		this.bcoTaxCode = bcoTaxCode;
	}

	@Column(name = "BCO_BANK_ACCOUNT", length = 30)
	public String getBcoBankAccount() {
		return this.bcoBankAccount;
	}

	public void setBcoBankAccount(String bcoBankAccount) {
		this.bcoBankAccount = bcoBankAccount;
	}

	@Column(name = "BCO_PROVINCE_NAME", length = 50)
	public String getBcoProvinceName() {
		return this.bcoProvinceName;
	}

	public void setBcoProvinceName(String bcoProvinceName) {
		this.bcoProvinceName = bcoProvinceName;
	}

	@Column(name = "BCO_ADDRESS", length = 250)
	public String getBcoAddress() {
		return this.bcoAddress;
	}

	public void setBcoAddress(String bcoAddress) {
		this.bcoAddress = bcoAddress;
	}

	@Column(name = "BCO_MOBILE", length = 100)
	public String getBcoMobile() {
		return this.bcoMobile;
	}

	public void setBcoMobile(String bcoMobile) {
		this.bcoMobile = bcoMobile;
	}

	@Column(name = "BCO_FAX", length = 100)
	public String getBcoFax() {
		return this.bcoFax;
	}

	public void setBcoFax(String bcoFax) {
		this.bcoFax = bcoFax;
	}

	@Column(name = "BCO_EMAIL", length = 100)
	public String getBcoEmail() {
		return this.bcoEmail;
	}

	public void setBcoEmail(String bcoEmail) {
		this.bcoEmail = bcoEmail;
	}

	@Column(name = "BCO_COMPANY_BVLIFE_ID", length = 9)
	public String getBcoCompanyBvlifeId() {
		return this.bcoCompanyBvlifeId;
	}

	public void setBcoCompanyBvlifeId(String bcoCompanyBvlifeId) {
		this.bcoCompanyBvlifeId = bcoCompanyBvlifeId;
	}

}
