package com.pyramix.swi.domain.organization;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.BigDecimalBridge;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.receivable.CustomerReceivable;

@Entity
@Indexed
@Table(name = "customer", schema = SchemaUtil.SCHEMA_COMMON)
public class Customer extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8954667316618415622L;
	
	@Column(name = "organization_type")
	@Enumerated(EnumType.ORDINAL)
	private CompanyType companyType;

	@Fields({
		@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
	})
	@FieldBridge(impl = BigDecimalBridge.class)
	@Column(name = "organization_legal_name")
	private String companyLegalName;
	
	@Column(name = "organization_display_name")
	private String companyDisplayName;
	
	@Column(name = "contact_person")
	private String contactPerson;
	
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
	
	@Column(name = "extension")
	private String extension;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "fax")
	private String fax;
	
	@Column(name = "note")
	private String note;

	@Column(name = "active")
	@Type(type="true_false")
	private boolean active;

	// receivable_id_fk
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "receivable_id_fk")
	private CustomerReceivable customerReceivable;

	@Override
	public String toString() {
		return "Customer [companyType=" + companyType + ", companyLegalName=" + companyLegalName
				+ ", companyDisplayName=" + companyDisplayName + ", contactPerson=" + contactPerson + ", address01="
				+ address01 + ", address02=" + address02 + ", City=" + City + ", postalCode=" + postalCode + ", phone="
				+ phone + ", extension=" + extension + ", email=" + email + ", fax=" + fax + ", note=" + note
				+ ", active=" + active + "]";
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

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public CustomerReceivable getCustomerReceivable() {
		return customerReceivable;
	}

	public void setCustomerReceivable(CustomerReceivable customerReceivable) {
		this.customerReceivable = customerReceivable;
	}
	
}
