package com.pyramix.swi.domain.settlement;

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

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;

/**
 * SEMUA pembayaran harus melalui proses Settlement:
 * - Menandakan CustomerOrder sudah dibayarkan (lunas)
 * - Menandakan CustomerOrder dibayarkan setengah (cicilan)
 * - Menandakan CustomerOrder belum dibayarkan
 * 
 * Setelah melalui proses Setelement, maka:
 * - Pembayaran dilakukan dengan
 * 		- Giro: mengisi VoucherGiroReceipt - update Giro gantung (Giro)
 * 		- Bank / Cash: mengisi VoucherPayment - (pembayaran langsung cair / lunas)
 * 
 * @author mrusli
 *
 */
@Entity
@Table(name = "settlement", schema=SchemaUtil.SCHEMA_COMMON)
public class Settlement extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231519L;

	// amount_paid decimal(19,2)
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	// settlement_date date
	@Column(name = "settlement_date")
	@Temporal(TemporalType.DATE)
	private Date settlementDate;
	
	// settlement_description varchar(255)
	@Column(name = "settlement_description")
	private String settlementDescription;

	// settlement_position char(1)
	@Column(name = "settlement_position")
	@Enumerated(EnumType.ORDINAL)
	private SettlementPosition settlementPosition;
	
	// `posting_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "posting_amount")
	private BigDecimal postingAmount;
	
	// document_ref varchar(255)
	@Column(name = "document_ref")
	private String documentRef;
	
	// create_date date 
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	// modified_date datetime 
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	// check_date datetime 
	@Column(name = "check_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkDate;
	
	// note varchar(255) 
	@Column(name = "note")
	private String note;
	
	// settlement_no_id_fk bigint(20)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.EAGER)
	@JoinColumn(name = "settlement_no_id_fk")
	private DocumentSerialNumber settlementNumber;
	
	// customer_id_fk bigint(20)
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;

	// settlement_join_voucher_giro_receipt
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval=true, fetch=FetchType.LAZY)
	@JoinTable(name = "settlement_join_voucher_giro_receipt",
			joinColumns = @JoinColumn(name = "id_settlement"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_giro_receipt"))
	private VoucherGiroReceipt voucherGiroReceipt;

	// settlement_join_voucher_payment
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval=true, fetch=FetchType.LAZY)
	@JoinTable(name = "settlement_join_voucher_payment",
			joinColumns = @JoinColumn(name = "id_settlement"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_payment"))
	private VoucherPayment voucherPayment;
	
	// settlement_join_detail
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "settlement_join_detail",
			joinColumns = @JoinColumn(name = "id_settlement"),
			inverseJoinColumns = @JoinColumn(name = "id_settlement_detail"))
	private List<SettlementDetail> settlementDetails;
	
	// settlement_join_receivable
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "settlement_join_receivable",
			joinColumns = @JoinColumn(name = "id_settlement"),
			inverseJoinColumns = @JoinColumn(name = "id_receivable"))
	private CustomerReceivable customerReceivable;

	// settlement_status int(11)
	@Column(name = "settlement_status")
	@Enumerated(EnumType.ORDINAL)
	private DocumentStatus settlementStatus;
	
	// userCreate MUST fecth LAZY !!! 
	// - if not, a userCreate with more than one UserRoles causes the settlement to fetch N+1
	// - for example, if the userCreate has USER_ROLE and MANAGER_ROLE, hibernate fetch voucherJournalDebitCredits twice !!!

	// user_create_id_fk bigint(20)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_create_id_fk")
	private User userCreate;
	
	//	batal_date date
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;
	
	@Override
	public String toString() {
		
		return "Settlement [id="+getId()+
				", amountPaid="+getAmountPaid()+
				", settlementDate="+getSettlementDate()+
				", settlementDescription="+getSettlementDescription()+
				", postingAmount="+getPostingAmount()+
				", documentRef="+getDocumentRef()+
				", createDate="+getCreateDate()+
				", modifiedDate="+getModifiedDate()+
				", checkDate="+getCheckDate()+
				", note="+getNote()+
				", settlementNumber="+getSettlementNumber().getSerialComp()+
				", settlementDetails="+getSettlementDetails()+
				", settlementStatus="+getSettlementStatus().toString()+
				"]";
	}
	
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getSettlementDescription() {
		return settlementDescription;
	}

	public void setSettlementDescription(String settlementDescription) {
		this.settlementDescription = settlementDescription;
	}

	public String getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(String documentRef) {
		this.documentRef = documentRef;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DocumentSerialNumber getSettlementNumber() {
		return settlementNumber;
	}

	public void setSettlementNumber(DocumentSerialNumber settlementNumber) {
		this.settlementNumber = settlementNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<SettlementDetail> getSettlementDetails() {
		return settlementDetails;
	}

	public void setSettlementDetails(List<SettlementDetail> settlementDetails) {
		this.settlementDetails = settlementDetails;
	}

	public SettlementPosition getSettlementPosition() {
		return settlementPosition;
	}

	public void setSettlementPosition(SettlementPosition settlementPosition) {
		this.settlementPosition = settlementPosition;
	}

	public BigDecimal getPostingAmount() {
		return postingAmount;
	}

	public void setPostingAmount(BigDecimal postingAmount) {
		this.postingAmount = postingAmount;
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public CustomerReceivable getCustomerReceivable() {
		return customerReceivable;
	}

	public void setCustomerReceivable(CustomerReceivable customerReceivable) {
		this.customerReceivable = customerReceivable;
	}

	public DocumentStatus getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(DocumentStatus settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public User getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
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
