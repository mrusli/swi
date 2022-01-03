package com.pyramix.swi.domain.voucher;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.user.User;

/**
 * @author rusli
 *
 * VoucherPayment dipergukan:
 * - Pada hari giro cair: pilih pembayaran dgn giro: pilih nama customer: pilih giro yg diberikan
 * 		CR	Giro Ditangan (1.212.0001)
 * 		DB	Bank - BCA - SWI Asemka (1.221.0005)
 * 
 * - Customer membayar dengan transfer bank: pilih nama customer: pilih customer order: pilih pembayaran dgn bank: isi informasi bank dan jumlah
 * 		CR	Piutang Langganan (COA 1.241.003 )
 * 		DB	Bank - BCA - SWI Asemka (1.221.0005)
 * 
 * - Customer membayar dengan tunai: pilih nama customer: pilih customer order: pilih pembayaran tunai: isi jumlah
 * 		CR	Piutang Langganan (COA 1.241003 )
 * 		DB	Petty Cash (1.211.0001)
 * 
 * Pembayaran Cicilan: 
 * - setiap pembayaran user HARUS membuat VoucherPayment
 * - setiap penerimaan giro-gantung user HARUS membuat VoucherGiroReceipt
 *
 */
@Entity
@Table(name = "voucher_payment", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherPayment extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8788935753759479292L;

	// `the_sum_of` decimal(19,2) DEFAULT NULL,
	@Column(name = "the_sum_of")
	private BigDecimal theSumOf;

	// `transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;

	// `transaction_description` varchar(255) DEFAULT NULL,
	@Column(name = "transaction_description")
	private String transactionDescription;

	// `document_ref` varchar(255) DEFAULT NULL,
	@Column(name = "document_ref")
	private String documentRef;

	// `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;

	// `modified_date` datetime DEFAULT NULL,
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	// `check_date` datetime DEFAULT NULL,
	@Column(name = "check_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkDate;

	// `flow_status` int(11) DEFAULT NULL,
	@Column(name = "flow_status")
	@Enumerated(EnumType.ORDINAL)
	private VoucherStatus flowStatus;

	// `voucher_type` int(11) DEFAULT NULL,
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	// `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;

	// `paid_by` int(11) DEFAULT NULL,
	@Column(name = "paid_by")
	@Enumerated(EnumType.ORDINAL)
	private PaymentType paidBy; 

	// `paid_by_note` varchar(255) DEFAULT NULL,
	@Column(name = "paid_by_note")
	private String paidByNote;
	
	// `posting_voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "posting_voucher_no_id_fk")
	private VoucherSerialNumber  postingVoucherNumber;
	
	// `voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "voucher_no_id_fk")
	private VoucherSerialNumber voucherNumber;
	
	// `customer_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;

	// `giro_id_fk` bigint(20) DEFAULT NULL,
	// one-to-one to giro use @JoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "giro_id_fk")
	private Giro giro;
	
	// -- NOT USED -- NOT DIRECTLY -- MUST use GiroListInfo
	// voucher_payment_join_voucher_giro_receipt
	// @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	// @JoinTable(name = "voucher_payment_join_voucher_giro_receipt",
	//		joinColumns = @JoinColumn(name = "id_voucher_payment"),
	//		inverseJoinColumns = @JoinColumn(name = "id_voucher_giro_receipt"))
	// private VoucherGiroReceipt voucherGiroReceipt;	

	// -- NOT USED -- NOT DIRECTLY -- MUST go through Settlement
	// voucher_payment_join_voucher_sales
	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinTable(name = "voucher_payment_join_voucher_sales",
	//		joinColumns = @JoinColumn(name = "id_voucher_payment"),
	//		inverseJoinColumns = @JoinColumn(name = "id_voucher_sales"))
	// private VoucherSales voucherSales;

	// voucher_payment_join_settlement
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_payment_join_settlement",
			joinColumns = @JoinColumn(name = "id_voucher_payment"),
			inverseJoinColumns = @JoinColumn(name = "id_settlement"))
	private Settlement settlement;
	
	// voucher_payment_join_debitcredit
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "voucher_payment_join_debitcredit",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_debitcredit"))
	@OrderBy("id")
	private List<VoucherPaymentDebitCredit> voucherPaymentDebitCredits;

	// 	voucher_status int(11)
	@Column(name = "voucher_status")
	@Enumerated(EnumType.ORDINAL)
	private DocumentStatus voucherStatus;

	//	`voucher_payment_join_general_ledger`
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_payment_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_general_ledger"))
	private List<GeneralLedger> generalLedgers;
	
 	// userCreate MUST fecth LAZY !!! 
	// - if not, a userCreate with more than one UserRoles causes the voucherGiroReceipt to fetch N+1
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
		
		return "VoucherPayment[id="+getId()+
				", theSumOf="+getTheSumOf()+
				", transactionDate="+getTransactionDate()+
				", transactionDescription="+getTransactionDescription()+
				", documentRef="+getDocumentRef()+
				", createDate="+getCreateDate()+
				", modifiedDate="+getModifiedDate()+
				", checkDate="+getCheckDate()+
				", flowStatus="+getFlowStatus().toString()+
				", voucherType="+getVoucherType().toString()+
				", postingDate="+getPostingDate()+
				", paidBy="+getPaidBy().toString()+
				", paidByNote="+getPaidByNote()+
				", voucherNumber="+getVoucherNumber().getSerialComp()+
				", voucherPaymentDebitCredits="+getVoucherPaymentDebitCredits()+
				", voucherStatus="+getVoucherStatus().toString()+
				"]";
	}
	
	public BigDecimal getTheSumOf() {
		return theSumOf;
	}

	public void setTheSumOf(BigDecimal theSumOf) {
		this.theSumOf = theSumOf;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
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

	public VoucherStatus getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(VoucherStatus flowStatus) {
		this.flowStatus = flowStatus;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public PaymentType getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(PaymentType paidBy) {
		this.paidBy = paidBy;
	}

	public Giro getGiro() {
		return giro;
	}

	public void setGiro(Giro giro) {
		this.giro = giro;
	}

	public String getPaidByNote() {
		return paidByNote;
	}

	public void setPaidByNote(String paidByNote) {
		this.paidByNote = paidByNote;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<VoucherPaymentDebitCredit> getVoucherPaymentDebitCredits() {
		return voucherPaymentDebitCredits;
	}

	public void setVoucherPaymentDebitCredits(List<VoucherPaymentDebitCredit> voucherPaymentDebitCredits) {
		this.voucherPaymentDebitCredits = voucherPaymentDebitCredits;
	}

	public VoucherSerialNumber getPostingVoucherNumber() {
		return postingVoucherNumber;
	}

	public void setPostingVoucherNumber(VoucherSerialNumber postingVoucherNumber) {
		this.postingVoucherNumber = postingVoucherNumber;
	}

	public VoucherSerialNumber getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(VoucherSerialNumber voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public DocumentStatus getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(DocumentStatus voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public List<GeneralLedger> getGeneralLedgers() {
		return generalLedgers;
	}

	public void setGeneralLedgers(List<GeneralLedger> generalLedgers) {
		this.generalLedgers = generalLedgers;
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
