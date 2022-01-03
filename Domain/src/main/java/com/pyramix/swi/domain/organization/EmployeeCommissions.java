package com.pyramix.swi.domain.organization;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.serial.DocumentStatus;

@Entity
@Table(name = "employee_commissions", schema=SchemaUtil.SCHEMA_COMMON)
public class EmployeeCommissions extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4624344900813336426L;

	// commission_amount decimal(19,2)
	@Column(name = "commission_amount")
	private BigDecimal commissionAmount;
	
	// commission_percent decimal(19,2)
	@Column(name = "commission_percent")
	private BigDecimal commissionPercent;
	
	// employee_id_fk bigint(20)
	@OneToOne
	@JoinColumn(name = "employee_id_fk")
	private Employee employee;

	// employee_commissions_join_customer_order
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "employee_commissions_join_customer_order",
			joinColumns = @JoinColumn(name = "id_employee_commissions"),
			inverseJoinColumns = @JoinColumn(name = "id_customer_order"))
	private CustomerOrder customerOrder;

	// commission_status int(11)
	@Column(name = "commission_status")
	private DocumentStatus commissionStatus;

	//	batal_date date
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;

	@Override
	public String toString() {
		return "EmployeeCommissions [id="+getId()+
				", commissionAmount="+getCommissionAmount()+
				", commissionPercent="+getCommissionPercent()+
				", employee="+getEmployee().getName()+
				", commissionStatus="+getCommissionStatus().toString()+
				"]";
	}
	
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public BigDecimal getCommissionPercent() {
		return commissionPercent;
	}

	public void setCommissionPercent(BigDecimal commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public DocumentStatus getCommissionStatus() {
		return commissionStatus;
	}

	public void setCommissionStatus(DocumentStatus commissionStatus) {
		this.commissionStatus = commissionStatus;
	}

	public Date getBatalDate() {
		return batalDate;
	}

	public void setBatalDate(Date batalDate) {
		this.batalDate = batalDate;
	}

	public String getBatalNote() {
		return batalNote;
	}

	public void setBatalNote(String batalNote) {
		this.batalNote = batalNote;
	}
	
}
