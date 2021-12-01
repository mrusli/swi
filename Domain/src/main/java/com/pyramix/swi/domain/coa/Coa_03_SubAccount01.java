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
@Table(name = "mm_coa_03_subaccount01", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_03_SubAccount01 extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2705603854038855393L;

	//  `subaccount01_name` varchar(255) DEFAULT NULL,
	@Column(name = "subaccount01_name")
	private String subAccount01Name;
	
	//	typecoa_no int(11)
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//	groupcoa_no int(11)
	@Column(name = "groupcoa_no")
	private int groupCoaNumber;
	
	//  `subaccount01_no` int(11) DEFAULT NULL,
	@Column(name = "subaccount01_no")
	private int subAccount01Number;
	
	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.DATE)
	private Date lastModified;

	//	mm_coa_03_subaccount01_join_mm_coa_04
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_03_subaccount01_join_mm_coa_04",
			joinColumns = @JoinColumn(name = "id_mm_coa_03"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_04"))
	private List<Coa_04_SubAccount02> subAccount02s;
	
	// mm_coa_02_accountgroup_join_mm_coa_03
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_02_accountgroup_join_mm_coa_03",
			joinColumns = @JoinColumn(name = "id_mm_coa_03"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_02"))
	private Coa_02_AccountGroup accountGroup;

	@Override
	public String toString() {
		return "Coa_03_SubAccount01 [id="+getId()+", Name="+getSubAccount01Name()+", Number="+getSubAccount01Number()+"]";
	}
	
	public String getSubAccount01Name() {
		return subAccount01Name;
	}

	public void setSubAccount01Name(String subAccount01Name) {
		this.subAccount01Name = subAccount01Name;
	}

	public int getSubAccount01Number() {
		return subAccount01Number;
	}

	public void setSubAccount01Number(int subAccount01Number) {
		this.subAccount01Number = subAccount01Number;
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

	public Coa_02_AccountGroup getAccountGroup() {
		return accountGroup;
	}

	public void setAccountGroup(Coa_02_AccountGroup accountGroup) {
		this.accountGroup = accountGroup;
	}

	public List<Coa_04_SubAccount02> getSubAccount02s() {
		return subAccount02s;
	}

	public void setSubAccount02s(List<Coa_04_SubAccount02> subAccount02s) {
		this.subAccount02s = subAccount02s;
	}

	public int getGroupCoaNumber() {
		return groupCoaNumber;
	}

	public void setGroupCoaNumber(int groupCoaNumber) {
		this.groupCoaNumber = groupCoaNumber;
	}

	public int getTypeCoaNumber() {
		return typeCoaNumber;
	}

	public void setTypeCoaNumber(int typeCoaNumber) {
		this.typeCoaNumber = typeCoaNumber;
	}
	
}
