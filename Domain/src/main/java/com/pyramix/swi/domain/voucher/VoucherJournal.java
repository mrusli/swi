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
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.user.User;

/**
 * @author rusli
 * 
 * VoucherJournal - to record general transactions and petty cash only
 * -- other transactions MUST use corresponding vouchers
 *
 */
@Entity
@Table(name = "voucher_journal", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherJournal extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -947724105584937037L;

	//  `the_sum_of` decimal(19,2) DEFAULT NULL,
	@Column(name = "the_sum_of")	
	private BigDecimal theSumOf;

	//  `transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;

	//  `transaction_description` varchar(255) DEFAULT NULL,
	@Column(name = "transaction_description")
	private String transactionDescription;

	//  `document_ref` varchar(255) DEFAULT NULL,
	@Column(name = "document_ref")
	private String documentRef;

	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;

	//  `modified_date` datetime DEFAULT NULL,
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	//  `check_date` datetime DEFAULT NULL,
	@Column(name = "check_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkDate;

	//  `flow_status` int(11) DEFAULT NULL,
	@Column(name = "flow_status")
	@Enumerated(EnumType.ORDINAL)
	private VoucherStatus flowStatus;

	//  `voucher_type` int(11) DEFAULT NULL,
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;

	//  `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;

	//  `posting_voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "posting_voucher_no_id_fk")
	private VoucherSerialNumber  postingVoucherNumber;

	//  `voucher_no_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "voucher_no_id_fk")	
	private VoucherSerialNumber voucherNumber;

	//  `voucher_status` int(11) DEFAULT NULL,
	@Column(name = "voucher_status")
	@Enumerated(EnumType.ORDINAL)	
	private DocumentStatus voucherStatus;

	//	voucher_journal_join_debitcredit
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "voucher_journal_join_debitcredit",
		joinColumns = @JoinColumn(name = "id_voucher"),
		inverseJoinColumns = @JoinColumn(name = "id_debitcredit"))	
	private List<VoucherJournalDebitCredit> voucherJournalDebitCredits;

	//	`voucher_journal_join_general_ledger`
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "voucher_journal_join_general_ledger",
			joinColumns = @JoinColumn(name = "id_voucher"),
			inverseJoinColumns = @JoinColumn(name = "id_general_ledger"))
	private List<GeneralLedger> generalLedgers;
	
	// userCreate MUST fecth LAZY !!! 
	// - if not, a userCreate with more than one UserRoles causes the voucherJournalDebitCredits to fetch N+1
	// - for example, if the userCreate has USER_ROLE and MANAGER_ROLE, hibernate fetch voucherJournalDebitCredits twice !!!
	
	//	user_create_id_fk bigint(20)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_create_id_fk")
	private User userCreate;
	
	//	allow_edit char(1)
	@Column(name = "allow_edit")
	@Type(type="true_false")
	private boolean allowEdit;
	
	@Override
	public String toString() {

		return "VoucherJournal[id="+getId()+
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
				", voucherNumber="+getVoucherNumber().getSerialComp()+
				", voucherJournalDebitCredits="+getVoucherJournalDebitCredits()+
				", voucherStatus="+getVoucherStatus().toString()+
				", allowEdit="+isAllowEdit()+
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

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
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

	public DocumentStatus getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(DocumentStatus voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public List<VoucherJournalDebitCredit> getVoucherJournalDebitCredits() {
		return voucherJournalDebitCredits;
	}

	public void setVoucherJournalDebitCredits(List<VoucherJournalDebitCredit> voucherJournalDebitCredits) {
		this.voucherJournalDebitCredits = voucherJournalDebitCredits;
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

	public boolean isAllowEdit() {
		return allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

}
