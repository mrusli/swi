package com.pyramix.swi.domain.gl;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherType;

/**
 * @author rusli
 *
 * General Ledger - to record all the postings from:
 * - VoucherJournal
 * - VoucherSales
 * - VoucherPayment
 * - VoucherGiroReceipt
 *
 */
@Entity
@Table(name = "general_ledger", schema = SchemaUtil.SCHEMA_COMMON)
public class GeneralLedger extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4471558414054324774L;

	//  `coa_master_id_fk` bigint(20) DEFAULT NULL,
	//  CONSTRAINT `fk_coa_id_04` FOREIGN KEY (`coa_master_id_fk`) REFERENCES `mm_coa_05_master` (`id`),
	@OneToOne
	@JoinColumn(name = "coa_master_id_fk")
	private Coa_05_Master masterCoa;

	//  `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)	
	private Date postingDate;
	
	//  `posting_voucher_no_id_fk` bigint(20) DEFAULT NULL,
	//  CONSTRAINT `fk_posting_voucher_no_04` FOREIGN KEY (`posting_voucher_no_id_fk`) REFERENCES `voucher_serial_number` (`id`),
	@OneToOne
	@JoinColumn(name = "posting_voucher_no_id_fk")
	private VoucherSerialNumber postingVoucherNumber;
	
	//  `credit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "credit_amount")
	private BigDecimal creditAmount;
	
	//  `debit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "debit_amount")
	private BigDecimal debitAmount;
	
	//  `dbcr_description` varchar(255) DEFAULT NULL,
	@Column(name = "dbcr_description")
	private String dbcrDescription;
	
	//  `transaction_description` varchar(255) DEFAULT NULL,
	@Column(name = "transaction_description")
	private String transactionDescription;
	
	//  `document_ref` varchar(255) DEFAULT NULL,
	@Column(name = "document_ref")
	private String documentRef;
	
	//  `transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	
	//  `voucher_type` int(11) DEFAULT NULL,
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	//  `voucher_no_id_fk` bigint(20) DEFAULT NULL,
	//  CONSTRAINT `fk_voucher_no_04` FOREIGN KEY (`voucher_no_id_fk`) REFERENCES `voucher_serial_number` (`id`)
	@OneToOne
	@JoinColumn(name = "voucher_no_id_fk")	
	private VoucherSerialNumber voucherNumber;

	//	`voucher_journal_join_general_ledger`
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_journal_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_general_ledger"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher"))
	private VoucherJournal voucherJournal;
	
	//	`voucher_sales_join_general_ledger`
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_sales_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_general_ledger"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher"))
	private VoucherSales voucherSales;
	
	//	`voucher_giro_receipt_join_general_ledger`
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_giro_receipt_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_general_ledger"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher"))
	private VoucherGiroReceipt voucherGiroReceipt;
	
	// `voucher_payment_join_general_ledger`
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_payment_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_general_ledger"),
			inverseJoinColumns = @JoinColumn(name = "id_voucher"))
	private VoucherPayment voucherPayment;
	
	@Override
	public String toString() {
		
		return "GeneralLedger[id="+getId()+
				", masterCoa="+getMasterCoa()+
				", postingDate="+getPostingDate()+
				", postingVoucherNumber="+getPostingVoucherNumber()+
				", creditAmount="+getCreditAmount()+
				", debitAmount="+getDebitAmount()+
				", dbcrDescription="+getDbcrDescription()+
				", transactionDescription="+getTransactionDescription()+
				", documentRef="+getDocumentRef()+
				", transactionDate="+getTransactionDate()+
				", voucherType="+getVoucherType()+
				", voucherNumber="+getVoucherNumber();
	}
	
	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
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

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getDbcrDescription() {
		return dbcrDescription;
	}

	public void setDbcrDescription(String dbcrDescription) {
		this.dbcrDescription = dbcrDescription;
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

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public VoucherSerialNumber getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(VoucherSerialNumber voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public VoucherJournal getVoucherJournal() {
		return voucherJournal;
	}

	public void setVoucherJournal(VoucherJournal voucherJournal) {
		this.voucherJournal = voucherJournal;
	}

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
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

}
