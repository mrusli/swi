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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.user.User;

/**
 * @author rusli
 *
 * Dipergunakan HANYA customer memberikan giro gantung
 * - setelah mempergunakan VoucherGiroReceipt, maka:
 * 	 	- COA Giro Ditangan terupdate
 *			DB	Giro Ditangan (1.212.0001)
 *			CR	Piutang Langganan (COA 1.241003 )
 *			DB/CR COA lain2 tergantung dr Settlement
 * 		- Tambah di giro list
 * 
 * Laporan pencairan giro:
 * - giro cair hari ini, minggu ini, bulan ini
 * - giro cair dgn tanggal 
 *
 * Pada hari giro cair, maka user mempergunakan VoucherPayment - pilih giro
 *
 * SEMUA Kegiatan Pembayaran HARUS melalui proses Settlement:
 * - Apabile customer melakukan transfer bank, maka user langsung mempergunakan VoucherPayment - pilih bank
 * - Apabila customer melakukan pembayaran tunai, maka user langsung mempergunakan VoucherPayment - pilih tunai
 * 
 * Apabila giro tdk dpt dicairkan / tukar giro:
 * - user tulis note - giro ditolak / ditukar pd tgl...
 * - user melakukan perubahan di VoucherGiroReceipt dengan giro yg baru
 * - sistem melakukan perubaha giro secara otomatis
 */

@Entity
@Table(name = "voucher_giro_receipt", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherGiroReceipt extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -607499353082848194L;

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

	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	// `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;
	
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
	
	// `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;
	
	// `posting_voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "posting_voucher_no_id_fk")
	private VoucherSerialNumber postingVoucherNumber;

	// `voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "voucher_no_id_fk")
	private VoucherSerialNumber voucherNumber;

	// `customer_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;
	
	// NOTE: NOT USED (as of 2018-01-29) --> MUST go thru Settlement before 
	// voucher_sales_join_voucher_giro_receipt
	// NOT THIS --> voucher_giro_receipt_join_customer_order
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_sales_join_voucher_giro_receipt",
			joinColumns = @JoinColumn(name = "id_voucher_giro_receipt"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_sales"))
	private VoucherSales voucherSales;

	// voucher_giro_receipt_join_settlement
	@OneToOne(cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_giro_receipt_join_settlement",
			joinColumns = @JoinColumn(name = "id_voucher_giro_receipt"),
			inverseJoinColumns = @JoinColumn(name = "id_settlement")) 
	private Settlement settlement;
	
	// voucher_giro_receipt_join_voucher_payment
	// @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	// @JoinTable(name = "voucher_giro_receipt_join_voucher_payment",
	//		joinColumns = @JoinColumn(name = "id_voucher_giro_receipt"),
	//		inverseJoinColumns = @JoinColumn(name = "id_voucher_payment"))
	// private VoucherPayment voucherPayment;

	// voucher_giro_receipt_join_giro 
	// -- MUST set to Eager due to update during VoucherPayment
	// -- can not use proxy during update !!!
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "voucher_giro_receipt_join_giro",
			joinColumns = @JoinColumn(name = "id_voucher_giro_receipt"),
			inverseJoinColumns = @JoinColumn(name = "id_giro"))	
	private Giro giro;

	// voucher_giro_receipt_join_debitcredit
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "voucher_giro_receipt_join_debitcredit",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_debitcredit"))
	@OrderBy("id")
	private List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCredits;
	
	//	voucher_status int(11)
	@Column(name = "voucher_status")
	private DocumentStatus voucherStatus;
	
	//	`voucher_giro_receipt_join_general_ledger`
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_giro_receipt_join_general_ledger",
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
	
	@Override
	public String toString() {
		return "VoucherGiroReceip[id="+getId()+
				", theSumOf="+getTheSumOf()+
				", transDate="+getTransactionDate()+
				", giro="+getGiro()+
				", voucherGiroReceiptDebitCredits="+getVoucherGiroReceiptDebitCredits()+
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

/*	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}
*/
	public Giro getGiro() {
		return giro;
	}

	public void setGiro(Giro giro) {
		this.giro = giro;
	}

	public List<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDebitCredits() {
		return voucherGiroReceiptDebitCredits;
	}

	public void setVoucherGiroReceiptDebitCredits(List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCredits) {
		this.voucherGiroReceiptDebitCredits = voucherGiroReceiptDebitCredits;
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


	
}
