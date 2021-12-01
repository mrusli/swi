package com.pyramix.swi.domain.voucher;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

/**
 * @author rusli
 *
 * VoucherJournalDebitCredit - debitcredit for VoucherJournal
 * -- pettycash transaction -- always use 'Petty Cash' account (id=4)
 * -- other transactions -- use other accounts 
 *
 */
@Entity
@Table(name = "voucher_journal_debitcredit", schema=SchemaUtil.SCHEMA_COMMON)
public class VoucherJournalDebitCredit extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2881753362158534830L;

	//  `credit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "credit_amount")
	private BigDecimal creditAmount;

	//  `debit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "debit_amount")
	private BigDecimal debitAmount;

	//  `dbcr_description` varchar(255) DEFAULT NULL,
	@Column(name = "dbcr_description")
	private String dbcrDescription;

	//  `coa_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne
	@JoinColumn(name = "coa_id_fk")
	private Coa_05_Master masterCoa;
	
	@Override
	public String toString() {
		
		return "VoucherJournalDebitCredit[id="+getId()+
				", creditAmount="+getCreditAmount()+
				", debitAmount="+getDebitAmount()+
				", dbcrDescription="+getDbcrDescription()+
				"]";
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

	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
	}

}
