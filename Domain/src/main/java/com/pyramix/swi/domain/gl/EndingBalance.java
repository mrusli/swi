package com.pyramix.swi.domain.gl;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "ending_balance", schema = SchemaUtil.SCHEMA_COMMON)
public class EndingBalance extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1464669082455299353L;

	//  `coa_master_id_fk` bigint(20) DEFAULT NULL,
	//  CONSTRAINT `fk_coa_id_06` FOREIGN KEY (`coa_master_id_fk`) REFERENCES `mm_coa_05_master` (`id`)
	@OneToOne
	@JoinColumn(name = "coa_master_id_fk")
	private Coa_05_Master masterCoa;

	//  `ending_date` date DEFAULT NULL,
	@Column(name = "ending_date")
	@Temporal(TemporalType.DATE)		
	private Date endingDate;

	//  `ending_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "ending_amount")
	private BigDecimal endingAmount;

	@Override
	public String toString() {
		return "EndingBalance[id="+getId()+
				", masterCoa="+getMasterCoa()+
				", endingDate="+getEndingDate()+
				", endingAmount="+getEndingAmount()+
				"]";
	}
	
	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
	}

	public Date getEndingDate() {
		return endingDate;
	}

	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}

	public BigDecimal getEndingAmount() {
		return endingAmount;
	}

	public void setEndingAmount(BigDecimal endingAmount) {
		this.endingAmount = endingAmount;
	}

}
