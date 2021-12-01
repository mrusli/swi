package com.pyramix.swi.domain.coa;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "mm_coa_05_master", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_05_Master extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6005019127924075842L;

	//  `mastercoa_name` varchar(255) DEFAULT NULL
	@Column(name = "mastercoa_name")
	private String masterCoaName;
	
	//	`typecoa_no` INT(11) NULL DEFAULT NULL
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//	groupcoa_no int(11)
	@Column(name = "groupcoa_no")
	private int groupCoaNumber;	
	
	//	subaccount01coa_no int(11)
	@Column(name = "subaccount01coa_no")
	private int subaccount01CoaNumber;	
	
	//	subaccount02coa_no int(11)
	@Column(name = "subaccount02coa_no")
	private int subaccount02CoaNumber;
	
	//  `mastercoa_no` int(11) DEFAULT NULL,
	@Column(name = "mastercoa_no")
	private int masterCoaNumber;
	
	//  UNIQUE KEY `mastercoa_comp_UNIQUE` (`mastercoa_comp`),
	//  `mastercoa_comp` varchar(255) DEFAULT NULL,
	@Column(name = "mastercoa_comp")
	private String masterCoaComp;
	
	//  `create_date` datetime DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModified;
	
	//  `credit_account` char(1) DEFAULT NULL,
	@Column(name = "credit_account")
	@Type(type = "true_false")
	private boolean creditAccount;
	
	//  `restricted` char(1) DEFAULT NULL,
	@Column(name = "restricted")
	@Type(type = "true_false")
	private boolean restricted;
	
	//  `active` char(1) DEFAULT NULL,
	@Column(name = "active")
	@Type(type = "true_false")
	private boolean active;

	//	mm_coa_04_subaccount02_join_mm_coa_05
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_04_subaccount02_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_05"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_04"))
	private Coa_04_SubAccount02 subAccount02;
	
	@Override
	public String toString() {
		return "Coa_05_Master [id="+getId()+
				", Name="+getMasterCoaName()+
				", Number="+getMasterCoaNumber()+
				", Comp="+getMasterCoaComp()+"]";
	}
	
	public String getMasterCoaName() {
		return masterCoaName;
	}

	public void setMasterCoaName(String masterCoaName) {
		this.masterCoaName = masterCoaName;
	}

	public int getMasterCoaNumber() {
		return masterCoaNumber;
	}

	public void setMasterCoaNumber(int masterCoaNumber) {
		this.masterCoaNumber = masterCoaNumber;
	}

	public String getMasterCoaComp() {
		return masterCoaComp;
	}

	public void setMasterCoaComp(String masterCoaComp) {
		this.masterCoaComp = masterCoaComp;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(boolean creditAccount) {
		this.creditAccount = creditAccount;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Coa_04_SubAccount02 getSubAccount02() {
		return subAccount02;
	}

	public void setSubAccount02(Coa_04_SubAccount02 subAccount02) {
		this.subAccount02 = subAccount02;
	}

	public int getTypeCoaNumber() {
		return typeCoaNumber;
	}

	public void setTypeCoaNumber(int typeCoaNumber) {
		this.typeCoaNumber = typeCoaNumber;
	}

	public int getGroupCoaNumber() {
		return groupCoaNumber;
	}

	public void setGroupCoaNumber(int groupCoaNumber) {
		this.groupCoaNumber = groupCoaNumber;
	}

	public int getSubaccount01CoaNumber() {
		return subaccount01CoaNumber;
	}

	public void setSubaccount01CoaNumber(int subaccount01CoaNumber) {
		this.subaccount01CoaNumber = subaccount01CoaNumber;
	}

	public int getSubaccount02CoaNumber() {
		return subaccount02CoaNumber;
	}

	public void setSubaccount02CoaNumber(int subaccount02CoaNumber) {
		this.subaccount02CoaNumber = subaccount02CoaNumber;
	}	
}
