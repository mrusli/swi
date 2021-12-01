package com.pyramix.swi.domain.coa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "mm_coa_receivables", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_Receivables extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6209780740862545196L;
	
	//  `debit_account` char(1) DEFAULT NULL,
	@Column(name = "debit_account")
	@Type(type = "true_false")	
	private boolean debitAccount;
	
	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;

	//  `last_modified` datetime DEFAULT NULL,
	@Column(name = "last_modified")
	@Temporal(TemporalType.DATE)
	private Date lastModified;

	//  `active` char(1) DEFAULT NULL,
	@Column(name = "active")
	@Type(type = "true_false")
	private boolean active;

	//  `master_coa_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "master_coa_id_fk")
	private Coa_05_Master masterCoa;
	
	@Override
	public String toString() {
		return "Coa_Receivables [debitAccount=" + debitAccount + ", createDate=" + createDate + ", lastModified="
				+ lastModified + ", active=" + active + "]";
	}

	public boolean isDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(boolean debitAccount) {
		this.debitAccount = debitAccount;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
	}


	

}
