package com.pyramix.swi.domain.coa;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "mm_coa_02_accountgroup", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_02_AccountGroup extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6937570064726724131L;

	//  `account_group_name` varchar(255) DEFAULT NULL,
	@Column(name = "account_group_name")
	private String accountGroupName;
	
	//	typecoa_no int(11)
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//  `account_group_no` int(11) DEFAULT NULL,
	@Column(name = "account_group_no")
	private int accountGroupNumber;
	
	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.DATE)
	private Date lastModified;

	//	mm_coa_01_accounttype_join_mm_coa_02
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_01_accounttype_join_mm_coa_02",
			joinColumns = @JoinColumn(name = "id_mm_coa_02"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_01"))
	private Coa_01_AccountType accountType;

	//	mm_coa_02_accountgroup_join_mm_coa_03
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_02_accountgroup_join_mm_coa_03",
			joinColumns = @JoinColumn(name = "id_mm_coa_02"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_03"))
	private List<Coa_03_SubAccount01> subAccount01s;
	
	@Override
	public String toString() {
		return "Coa_02_AccountGroup [id="+getId()+", Name="+getAccountGroupName()+", Number="+getAccountGroupNumber()+"]";
	}
	
	public String getAccountGroupName() {
		return accountGroupName;
	}

	public void setAccountGroupName(String accountGroupName) {
		this.accountGroupName = accountGroupName;
	}

	public int getAccountGroupNumber() {
		return accountGroupNumber;
	}

	public void setAccountGroupNumber(int accountGroupNumber) {
		this.accountGroupNumber = accountGroupNumber;
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

	public Coa_01_AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(Coa_01_AccountType accountType) {
		this.accountType = accountType;
	}

	public List<Coa_03_SubAccount01> getSubAccount01s() {
		return subAccount01s;
	}

	public void setSubAccount01s(List<Coa_03_SubAccount01> subAccount01s) {
		this.subAccount01s = subAccount01s;
	}

	public int getTypeCoaNumber() {
		return typeCoaNumber;
	}

	public void setTypeCoaNumber(int typeCoaNumber) {
		this.typeCoaNumber = typeCoaNumber;
	}

}
