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
@Table(name = "mm_coa_04_subaccount02", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_04_SubAccount02 extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7165841416551882132L;

	//  `subaccount02_name` varchar(255) DEFAULT NULL,
	@Column(name = "subaccount02_name")
	private String subAccount02Name;
	
	//	typecoa_no int(11)
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//	groupcoa_no int(11)
	@Column(name = "groupcoa_no")
	private int groupCoaNumber;	
	
	//	subaccount01coa_no int(11)
	@Column(name = "subaccount01coa_no")
	private int subaccount01CoaNumber;
	
	//  `subaccount02_no` int(11) DEFAULT NULL,
	@Column(name = "subaccount02_no")
	private int subAccount02Number;
	
	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.DATE)
	private Date lastModified;
	
	//	mm_coa_04_subaccount02_join_mm_coa_05
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_04_subaccount02_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_04"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_05"))
	private List<Coa_05_Master> masters;
	
	//	mm_coa_03_subaccount01_join_mm_coa_04
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_03_subaccount01_join_mm_coa_04",
			joinColumns = @JoinColumn(name = "id_mm_coa_04"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_03"))
	private Coa_03_SubAccount01 subAccount01;
	
	@Override
	public String toString() {
		return "Coa_04_SubAccount02 [id="+getId()+", Name="+getSubAccount02Name()+", Number="+getSubAccount02Number()+"]";
	}
	
	public String getSubAccount02Name() {
		return subAccount02Name;
	}

	public void setSubAccount02Name(String subAccount02Name) {
		this.subAccount02Name = subAccount02Name;
	}

	public int getSubAccount02Number() {
		return subAccount02Number;
	}

	public void setSubAccount02Number(int subAccount02Number) {
		this.subAccount02Number = subAccount02Number;
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

	public List<Coa_05_Master> getMasters() {
		return masters;
	}

	public void setMasters(List<Coa_05_Master> masters) {
		this.masters = masters;
	}

	public Coa_03_SubAccount01 getSubAccount01() {
		return subAccount01;
	}

	public void setSubAccount01(Coa_03_SubAccount01 subAccount01) {
		this.subAccount01 = subAccount01;
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
}
