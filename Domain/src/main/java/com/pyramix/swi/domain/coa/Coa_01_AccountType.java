package com.pyramix.swi.domain.coa;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "mm_coa_01_accounttype", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_01_AccountType extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1364546139946377169L;

	//  `account_type_name` varchar(255) DEFAULT NULL,
	@Column(name = "account_type_name")
	private String accountTypeName;
	
	//  `account_type_no` int(11) DEFAULT NULL,
	@Column(name = "account_type_no")
	private int accountTypeNumber;
	
	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.DATE)
	private Date lastModified;

	//	mm_coa_01_accounttype_join_mm_coa_02
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_01_accounttype_join_mm_coa_02",
		joinColumns = @JoinColumn(name = "id_mm_coa_01"),
		inverseJoinColumns = @JoinColumn(name = "id_mm_coa_02"))
	private List<Coa_02_AccountGroup> accountGroups;
	
	@Override
	public String toString() {
		return "Coa_01_AccountType [id="+getId()+", Name:"+getAccountTypeName()+", Number:"+getAccountTypeNumber()+"]";
	}
	
	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public int getAccountTypeNumber() {
		return accountTypeNumber;
	}

	public void setAccountTypeNumber(int accountTypeNumber) {
		this.accountTypeNumber = accountTypeNumber;
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

	public List<Coa_02_AccountGroup> getAccountGroups() {
		return accountGroups;
	}

	public void setAccountGroups(List<Coa_02_AccountGroup> accountGroups) {
		this.accountGroups = accountGroups;
	}
	
}
