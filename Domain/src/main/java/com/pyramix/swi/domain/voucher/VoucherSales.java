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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;

/**
 * @author rusli
 *
 * Dari CustomerOrder -> Posting -> VoucherSales:
 * - Tunai / Cash : update COA : 
 * 		DB  Petty Cash  ( COA 1.211.0001 )
 * 		CR  Penjualan Tunai  ( COA  4.111.0001 )
 * 
 * 	 PPN:
 * 		CR  Penjualan - PPN (COA  4.111.0005)	
 * 
 * - Giro / Bank / Kredit / Hutang : update COA :
 *		DB  Piutang Langganan (COA 1.241.003 ) #44
 *		CR  Penjualan Kredit  ( COA 4.111.0002 ) #42
 *
 *	 PPN:
 *		CR  Penjualan - PPN (COA  4.111.0005) #43	
 *
 * Produce: 
 *  
 * - Account Receivables
 * 		penjualan kredit ONLY
 *		- by customer order number
 *		- by customer name (alphabetically)
 * 
 */
@Entity
@Table(name = "voucher_sales", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherSales extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1192902258175537331L;

	// `the_sum_of` decimal(19,2) DEFAULT NULL,
	@Column(name = "the_sum_of")
	private BigDecimal theSumOf;
	
	// `transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	
	// `payment_type` int(11) DEFAULT NULL,
	@Column(name = "payment_type")
	@Enumerated(EnumType.ORDINAL)
	private PaymentType paymentType;
	
	// `jumlah_hari` int(11) DEFAULT NULL,
	@Column(name = "jumlah_hari")
	private int jumlahHari;
	
	//	`use_ppn` char(1) DEFAULT NULL,
	@Column(name = "use_ppn")
	@Type(type = "true_false")
	private boolean usePpn;
	
	// ppn_amount decimal(19,2)
	@Column(name = "ppn_amount")
	private BigDecimal ppnAmount;
	
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
	
	// if tunai -- payment is complete -- Lunas
	// -- else depends on the settlement
	@Column(name = "payment_complete")
	@Type(type = "true_false")
	private boolean paymentComplete;
	
	// this usually amount to 0 (zero)
	// -- if there're some numbers, this voucherSales has not been fully paid
	// -- paymentComplete = false
	// -- will come up again in the settlement process -- unless payment complete is true
	// remaining_amount_to_settle decimal(19,2)
	@Column(name = "remaining_amount_to_settle")
	private BigDecimal remainingAmount;
	
	// `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;
	
	// `posting_voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
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

	// voucher_sales_join_customer_order
	@OneToOne(cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_sales_join_customer_order",
			joinColumns = @JoinColumn(name = "id_voucher_sales", nullable=true),
			inverseJoinColumns = @JoinColumn(name = "id_customer_order", nullable=true))
	private CustomerOrder customerOrder;

	// voucher_sales_join_debitcredit
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "voucher_sales_join_debitcredit",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_debitcredit"))
	private List<VoucherSalesDebitCredit> voucherSalesDebitCredits;
	
	// 19/01/2019 - NOTE: NOT USED !!!
	// voucher_sales_join_receivable_activity
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_sales_join_receivable_activity",
			joinColumns = @JoinColumn(name = "id_voucher_sales"),
			inverseJoinColumns = @JoinColumn(name = "id_receivable_activity"))
	private CustomerReceivableActivity customerReceivableActivity;

	// voucher_status int(11)
	@Column(name = "voucher_status")
	@Enumerated(EnumType.ORDINAL)	
	private DocumentStatus voucherStatus;

	//	`voucher_sales_join_general_ledger`
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_sales_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_general_ledger"))
	private List<GeneralLedger> generalLedgers;

	//	batal_date date
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;
	
	@Override
	public String toString() {
		return "VoucherSales [id="+getId()+
				", theSumOf="+getTheSumOf()+
				", transactionDate="+getTransactionDate()+
				", paymentType="+getPaymentType().toString()+
				", jumlahHari="+getJumlahHari()+
				", usePpn="+isUsePpn()+
				", ppnAmount="+getPpnAmount()+
				", transactionDescription="+getTransactionDescription()+
				", documentRef="+getDocumentRef()+
				", createDate="+getCreateDate()+
				", modifiedDate="+getModifiedDate()+
				", checkDate="+getCheckDate()+
				", flowStatus="+getFlowStatus()+
				", voucherType="+getVoucherType().toString()+
				", paymentComplete="+isPaymentComplete()+
				", remainingAmount="+getRemainingAmount()+
				", postingDate="+getPostingDate()+
				", voucherNumber="+getVoucherNumber().getSerialComp()+
				", voucherSalesDebitCredits="+getVoucherSalesDebitCredits()+
				", voucherStatus="+getVoucherStatus().toString()+
				"]";
	}
	
	// NOTE: MUST go thru Settlement Process prior to Creating VoucherGiroReceipt
	//
	// voucher_sales_join_voucher_giro_receipt
	// @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	// @JoinTable(name = "voucher_sales_join_voucher_giro_receipt",
	//		joinColumns = @JoinColumn(name = "id_voucher_sales"),
	//		inverseJoinColumns = @JoinColumn(name = "id_voucher_giro_receipt"))
	// private List<VoucherGiroReceipt> voucherGiroReceipts;
	
	// NOTE: [Thu 18/01/2018 08:41] Kalo pembelian tunai , tidak perlu voucher pembayaran 
	//
	// voucher_sales_join_voucher_payment
	// @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	// @JoinTable(name = "voucher_sales_join_voucher_payment",
	//	joinColumns = @JoinColumn(name = "id_voucher_sales"),
	//	inverseJoinColumns = @JoinColumn(name = "id_voucher_payment"))	
	// private VoucherPayment voucherPayment;
	
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

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public int getJumlahHari() {
		return jumlahHari;
	}

	public void setJumlahHari(int jumlahHari) {
		this.jumlahHari = jumlahHari;
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

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public boolean isUsePpn() {
		return usePpn;
	}

	public void setUsePpn(boolean usePpn) {
		this.usePpn = usePpn;
	}

	public List<VoucherSalesDebitCredit> getVoucherSalesDebitCredits() {
		return voucherSalesDebitCredits;
	}

	public void setVoucherSalesDebitCredits(List<VoucherSalesDebitCredit> voucherSalesDebitCredits) {
		this.voucherSalesDebitCredits = voucherSalesDebitCredits;
	}

	public BigDecimal getPpnAmount() {
		return ppnAmount;
	}

	public void setPpnAmount(BigDecimal ppnAmount) {
		this.ppnAmount = ppnAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public CustomerReceivableActivity getCustomerReceivableActivity() {
		return customerReceivableActivity;
	}

	public void setCustomerReceivableActivity(CustomerReceivableActivity customerReceivableActivity) {
		this.customerReceivableActivity = customerReceivableActivity;
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
