package com.pyramix.swi.domain.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.organization.Company;

@Entity
@Table(name = "settings" )
public class Settings extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8524987155226300074L;

	//  `name` varchar(255) DEFAULT NULL,
	@Column(name = "name")
	private String settingName;
	
	//  `organization_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_organization_id_02` (`organization_id_fk`),
	//  CONSTRAINT `fk_organization_02` FOREIGN KEY (`organization_id_fk`) REFERENCES `organization` (`id`)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organization_id_fk")
	private Company selectedCompany;

	//  `trans_ppn_bank_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_trans_ppn_bank_id_01` (`trans_ppn_bank_id_fk`),
	//  CONSTRAINT `fk_trans_ppn_bank_01` FOREIGN KEY (`trans_ppn_bank_id_fk`) REFERENCES `bank` (`id`)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_ppn_bank_id_fk")
	private Bank ppnTransactionBank;
	
	//  `trans_non_ppn_bank_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_trans_non_ppn_bank_id_01` (`trans_non_ppn_bank_id_fk`),
	//  CONSTRAINT `fk_trans_non_ppn_bank_01` FOREIGN KEY (`trans_non_ppn_bank_id_fk`) REFERENCES `bank` (`id`),
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_non_ppn_bank_id_fk")
	private Bank nonPpnTransactionBank;
	
	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	
	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public Bank getPpnTransactionBank() {
		return ppnTransactionBank;
	}

	public void setPpnTransactionBank(Bank ppnTransactionBank) {
		this.ppnTransactionBank = ppnTransactionBank;
	}

	public Bank getNonPpnTransactionBank() {
		return nonPpnTransactionBank;
	}

	public void setNonPpnTransactionBank(Bank nonPpnTransactionBank) {
		this.nonPpnTransactionBank = nonPpnTransactionBank;
	}	
}
