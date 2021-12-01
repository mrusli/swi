package com.pyramix.swi.domain.organization;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "organization", schema = SchemaUtil.SCHEMA_COMMON)
public class Company extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1492927197651912586L;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private CompanyType companyType;
	
	@Column(name = "legal_name")
	private String companyLegalName;
	
	@Column(name = "display_name")
	private String companyDisplayName;

	@Column(name = "address_01")
	private String address01;
	
	@Column(name = "address_02")
	private String address02;

	@Column(name = "city")
	private String City;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;
	
	@Column(name = "fax")
	private String fax;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "organization_join_bank",
			joinColumns = @JoinColumn(name = "id_organization"),
			inverseJoinColumns = @JoinColumn(name = "id_bank"))
	private List<Bank> banks;
	
	@Override
	public String toString() {
		return "Company [companyType=" + companyType + ", companyLegalName=" + companyLegalName
				+ ", companyDisplayName=" + companyDisplayName + ", address01=" + address01 + ", address02=" + address02
				+ ", City=" + City + ", postalCode=" + postalCode + ", phone=" + phone + ", email=" + email + ", fax="
				+ fax + "]";
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public void setCompanyType(CompanyType companyType) {
		this.companyType = companyType;
	}

	public String getCompanyLegalName() {
		return companyLegalName;
	}

	public void setCompanyLegalName(String companyLegalName) {
		this.companyLegalName = companyLegalName;
	}

	public String getCompanyDisplayName() {
		return companyDisplayName;
	}

	public void setCompanyDisplayName(String companyDisplayName) {
		this.companyDisplayName = companyDisplayName;
	}

	public String getAddress01() {
		return address01;
	}

	public void setAddress01(String address01) {
		this.address01 = address01;
	}

	public String getAddress02() {
		return address02;
	}

	public void setAddress02(String address02) {
		this.address02 = address02;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Override
	public boolean equals(Object o) {
	    if(o == null) {
	        return false;
	    } else if (!(o instanceof Company)) {
	        return false;
	    } else {
	    	return ((Company)o).getId().equals(this.getId());
	    }
	}

	public List<Bank> getBanks() {
		return banks;
	}

	public void setBanks(List<Bank> banks) {
		this.banks = banks;
	}
	
}
