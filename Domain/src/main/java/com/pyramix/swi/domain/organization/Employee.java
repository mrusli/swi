package com.pyramix.swi.domain.organization;

import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.domain.user.User;

@Entity
@Indexed
@Table(name = "employee" )
public class Employee extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4522916333505643645L;

	//  `employee_type` int(11) NOT NULL,
	@Column(name = "employee_type")
	@Enumerated(EnumType.ORDINAL)
	private EmployeeType employeeType;
	
	//	`organization_id_fk`  bigint(20) DEFAULT NULL,    --> to organization
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id_fk")
	private Company company;

	//  `user_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_user_id_01` (`user_id_fk`),
	//  CONSTRAINT `fk_user_01` FOREIGN KEY (`user_id_fk`) REFERENCES `employee` (`id`)
	@OneToOne(cascade= { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id_fk")
	private User user;
	
	//	`active` CHAR(1) DEFAULT NULL,
	@Column(name = "active")
	@Type(type = "true_false")
	private boolean active;
	
	// `employee_admitted` date DEFAULT NULL,
	@Column(name = "employee_admitted")
	@Temporal(TemporalType.DATE)
	private Date admitted;
	
	// `employee_resigned` date DEFAULT NULL,
	@Column(name = "employee_resigned")
	@Temporal(TemporalType.DATE)
	private Date resigned;

	//	`commission` CHAR(1) DEFAULT NULL,
	@Column(name = "commission")
	@Type(type = "true_false")
	private boolean commission;
	
	//	`commission_percent` decimal(19,2) DEFAULT NULL,
	@Column(name = "commission_percent")
	private BigDecimal commissionPercent;
	
	// `name` varchar(255) DEFAULT NULL,
	@Fields({
		@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
	})
	@FieldBridge(impl = BigDecimalBridge.class)
	@Column(name = "name")
	private String name;
	
	// `ktp_id_no` varchar(255) DEFAULT NULL,
	@Column(name = "ktp_id_no")
	private String ktpNumber;
	
	// `gender` int(11) DEFAULT NULL,
	@Column(name = "gender")
	@Enumerated(EnumType.ORDINAL)
	private EmployeeGender gender;
	
	// `religion` int(11) DEFAULT NULL,
	@Column(name = "religion")
	@Enumerated(EnumType.ORDINAL)
	private EmployeeReligion religion;
	
	// `address` varchar(255) DEFAULT NULL,
	@Column(name = "address")
	private String address;
	
	// `city` varchar(255) DEFAULT NULL,
	@Column(name = "city")
	private String city;
	
	// `postal_code` varchar(255) NOT NULL,
	@Column(name = "postal_code")
	private String postalCode;
	
	// `phone` varchar(255) DEFAULT NULL,
	@Column(name = "phone")
	private String phone;
	
	// `email` varchar(255) DEFAULT NULL,
	@Column(name = "email")
	private String email;
	
	// `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String catatan;

	// `employee_join_kendaraan` (
	//		  `id_employee` bigint(20) NOT NULL,
	//		  `id_kendaraan` bigint(20) NOT NULL,
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "employee_join_kendaraan",
			joinColumns = @JoinColumn(name = "id_employee"),
			inverseJoinColumns = @JoinColumn(name = "id_kendaraan"))
	private List<Kendaraan> employeeKendaraanList;
	
	@Override
	public String toString() {
		
		return "Employee[id="+getId()+
				", name="+getName()+
				", employeeType="+getEmployeeType().toString()+
				", recv.commission="+isCommission()+"]";
	}
	
	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getAdmitted() {
		return admitted;
	}

	public void setAdmitted(Date admitted) {
		this.admitted = admitted;
	}

	public Date getResigned() {
		return resigned;
	}

	public void setResigned(Date resigned) {
		this.resigned = resigned;
	}

	public boolean isCommission() {
		return commission;
	}

	public void setCommission(boolean commission) {
		this.commission = commission;
	}

	public BigDecimal getCommissionPercent() {
		return commissionPercent;
	}

	public void setCommissionPercent(BigDecimal commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKtpNumber() {
		return ktpNumber;
	}

	public void setKtpNumber(String ktpNumber) {
		this.ktpNumber = ktpNumber;
	}

	public EmployeeGender getGender() {
		return gender;
	}

	public void setGender(EmployeeGender gender) {
		this.gender = gender;
	}

	public EmployeeReligion getReligion() {
		return religion;
	}

	public void setReligion(EmployeeReligion religion) {
		this.religion = religion;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getCatatan() {
		return catatan;
	}

	public void setCatatan(String catatan) {
		this.catatan = catatan;
	}

	public List<Kendaraan> getEmployeeKendaraanList() {
		return employeeKendaraanList;
	}

	public void setEmployeeKendaraanList(List<Kendaraan> employeeKendaraanList) {
		this.employeeKendaraanList = employeeKendaraanList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
