package com.pyramix.swi.domain.receivable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;

@Entity
@Table(name = "receivable", schema = SchemaUtil.SCHEMA_COMMON)
public class CustomerReceivable extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5688939382713778960L;

	//  `last_update` date DEFAULT NULL,
	@Column(name = "last_update")
	@Temporal(TemporalType.DATE)
	private Date lastUpdate;
	
	//  `earliest_due` date DEFAULT NULL,
	@Column(name = "earliest_due")
	@Temporal(TemporalType.DATE)
	private Date earliestDue;
	
	//  `latest_due` date DEFAULT NULL,
	@Column(name = "latest_due")
	@Temporal(TemporalType.DATE)
	private Date latestDue;
	
	//  `total_receivable` decimal(19,2) DEFAULT NULL,
	@Column(name = "total_receivable")
	private BigDecimal totalReceivable;
	
	//  `total_ppn_receivable` decimal(19,2) DEFAULT NULL,
	@Column(name = "total_ppn_receivable")
	private BigDecimal totalPpnReceivable;
	
	//  `payment_complete` char(1) DEFAULT NULL,
	@Column(name = "payment_complete")
	@Type(type = "true_false")
	private boolean paymentComplete;
	
	//  `amount_paid` decimal(19,2) DEFAULT NULL,
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	//  `amount_remaining` decimal(19,2) DEFAULT NULL,
	@Column(name = "amount_remaining")
	private BigDecimal amountRemaining;
	
	//  `note` varchar(255) DEFAULT NULL,  
	@Column(name = "note")
	private String note;
	
	//  `customer_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;
	
	//  `receivable_number_id_fk` bigint(20) DEFAULT NULL,  
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "receivable_number_id_fk")
	private DocumentSerialNumber documentSerialNumber;

	// receivable_join_receivable_activity
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "receivable_join_receivable_activity",
			joinColumns = @JoinColumn(name = "id_receivable"),
			inverseJoinColumns = @JoinColumn(name = "id_receivable_activity"))
	private List<CustomerReceivableActivity> customerReceivableActivities;
	
	@Override
	public String toString() {
		return "CustomerReceivable [lastUpdate=" + lastUpdate + ", earliestDue=" + earliestDue + ", latestDue="
				+ latestDue + ", totalReceivable=" + totalReceivable + ", totalPpnReceivable=" + totalPpnReceivable
				+ ", paymentComplete=" + paymentComplete + ", amountPaid=" + amountPaid + ", amountRemaining="
				+ amountRemaining + ", note=" + note + ", documentSerialNumber=" + documentSerialNumber
				+ ", customerReceivableActivities=" + customerReceivableActivities + "]";
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getEarliestDue() {
		return earliestDue;
	}

	public void setEarliestDue(Date earliestDue) {
		this.earliestDue = earliestDue;
	}

	public Date getLatestDue() {
		return latestDue;
	}

	public void setLatestDue(Date latestDue) {
		this.latestDue = latestDue;
	}

	public BigDecimal getTotalReceivable() {
		return totalReceivable;
	}

	public void setTotalReceivable(BigDecimal totalReceivable) {
		this.totalReceivable = totalReceivable;
	}

	public BigDecimal getTotalPpnReceivable() {
		return totalPpnReceivable;
	}

	public void setTotalPpnReceivable(BigDecimal totalPpnReceivable) {
		this.totalPpnReceivable = totalPpnReceivable;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public BigDecimal getAmountRemaining() {
		return amountRemaining;
	}

	public void setAmountRemaining(BigDecimal amountRemaining) {
		this.amountRemaining = amountRemaining;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DocumentSerialNumber getDocumentSerialNumber() {
		return documentSerialNumber;
	}

	public void setDocumentSerialNumber(DocumentSerialNumber documentSerialNumber) {
		this.documentSerialNumber = documentSerialNumber;
	}

	public List<CustomerReceivableActivity> getCustomerReceivableActivities() {
		return customerReceivableActivities;
	}

	public void setCustomerReceivableActivities(List<CustomerReceivableActivity> customerReceivableActivities) {
		this.customerReceivableActivities = customerReceivableActivities;
	}
}
