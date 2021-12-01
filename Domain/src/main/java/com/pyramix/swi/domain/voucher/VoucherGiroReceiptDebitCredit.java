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

@Entity
@Table(name = "voucher_giro_receipt_debitcredit", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherGiroReceiptDebitCredit extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7797732938294218266L;

	// credit_amount decimal(19,2)
	@Column(name = "credit_amount")
	private BigDecimal creditAmount;
	
	// debit_amount decimal(19,2)
	@Column(name = "debit_amount")
	private BigDecimal debitAmount;
	
	// dbcr_description varchar(255)
	@Column(name = "dbcr_description")
	private String dbcrDescription;
	
	// coa_id_fk bigint(20)
	@OneToOne
	@JoinColumn(name = "coa_id_fk")
	private Coa_05_Master masterCoa;

	@Override
	public String toString() {
		
		return "VoucherGiroReceiptDebitCredit[id="+getId()+
				", creditAmount="+getCreditAmount()+
				", debitAmount="+getDebitAmount()+
				", dbcrDescription="+getDbcrDescription()+
				", masterCoa="+getMasterCoa().getMasterCoaComp()+
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
