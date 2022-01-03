package com.pyramix.swi.domain.voucher;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;

@Entity
@Table(name = "giro", schema = SchemaUtil.SCHEMA_COMMON)
public class Giro extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1389530097399116488L;

	// `giro_received_date` date DEFAULT NULL,
	@Column(name = "giro_received_date")
	@Temporal(TemporalType.DATE)
	private Date giroReceivedDate;
	
	// `giro_number` varchar(255) DEFAULT NULL,
	@Column(name = "giro_number")
	private String giroNumber;

	// `giro_bank` varchar(255) DEFAULT NULL,
	@Column(name = "giro_bank")
	private String giroBank;

	// `giro_date` date DEFAULT NULL,
	@Column(name = "giro_date")
	@Temporal(TemporalType.DATE)
	private Date giroDate;

	// `giro_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "giro_amount")
	private BigDecimal giroAmount;
	
	// `paid` char(1) DEFAULT NULL,
	@Column(name = "paid")
	@Type(type = "true_false")
	private boolean paid;
	
	// `paid_giro_date` date DEFAULT NULL,
	@Column(name = "paid_giro_date")
	@Temporal(TemporalType.DATE)
	private Date paidGiroDate;
	
	// `customer_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;

	// giro_join_voucher_giro_receipt
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "giro_join_voucher_giro_receipt",
			joinColumns = @JoinColumn(name = "id_giro"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_giro_receipt"))
	private VoucherGiroReceipt voucherGiroReceipt;
	
	// giro_join_voucher_payment
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "giro_join_voucher_payment",
			joinColumns = @JoinColumn(name = "id_giro"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_payment"))
	private VoucherPayment voucherPayment;
	
	// giro_status int(11)
	@Column(name = "giro_status")
	@Enumerated(EnumType.ORDINAL)
	private DocumentStatus giroStatus;
	
	//	batal_date date 
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;
	
	@Override
	public String toString() {
		return "Giro [id="+getId()+
				", giroNumber="+getGiroNumber()+
				", giroBank="+getGiroBank()+
				", giroDate="+getGiroDate()+
				", giroStatus="+getGiroStatus().toString()+
				"]";
	}
	
	public String getGiroNumber() {
		return giroNumber;
	}

	public void setGiroNumber(String giroNumber) {
		this.giroNumber = giroNumber;
	}

	public String getGiroBank() {
		return giroBank;
	}

	public void setGiroBank(String giroBank) {
		this.giroBank = giroBank;
	}

	public Date getGiroDate() {
		return giroDate;
	}

	public void setGiroDate(Date giroDate) {
		this.giroDate = giroDate;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}

	public Date getGiroReceivedDate() {
		return giroReceivedDate;
	}

	public void setGiroReceivedDate(Date giroReceivedDate) {
		this.giroReceivedDate = giroReceivedDate;
	}

	public BigDecimal getGiroAmount() {
		return giroAmount;
	}

	public void setGiroAmount(BigDecimal giroAmount) {
		this.giroAmount = giroAmount;
	}

	public Date getPaidGiroDate() {
		return paidGiroDate;
	}

	public void setPaidGiroDate(Date paidGiroDate) {
		this.paidGiroDate = paidGiroDate;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public DocumentStatus getGiroStatus() {
		return giroStatus;
	}

	public void setGiroStatus(DocumentStatus giroStatus) {
		this.giroStatus = giroStatus;
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
