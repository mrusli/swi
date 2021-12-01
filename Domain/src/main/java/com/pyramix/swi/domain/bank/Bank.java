package com.pyramix.swi.domain.bank;

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

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Company;

/**
 * 
 * @author rusli
 *
 */
@Entity
@Table(name = "bank", schema = SchemaUtil.SCHEMA_COMMON)
public class Bank extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 293975163634775464L;

	//  `nama_bank` varchar(255) DEFAULT NULL,
	@Column(name = "nama_bank")
	private String namaBank;
	
	//  `nama_cabang` varchar(255) DEFAULT NULL,
	@Column(name = "nama_cabang")
	private String namaCabang;
	
	//  `nomor_rekening` varchar(255) DEFAULT NULL,
	@Column(name = "nomor_rekening")
	private String nomorRekening;
	
	//  `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinTable(name = "organization_join_bank",
			joinColumns = @JoinColumn(name = "id_bank"),
			inverseJoinColumns = @JoinColumn(name = "id_organization"))
	private Company company;
	
	//  `mm_coa_05_master_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mm_coa_05_master_id_fk")
	private Coa_05_Master coaMater;
	
	//  `tipe_pemilik_rekening` int(11) DEFAULT NULL,
	@Column(name = "tipe_pemilik_rekening")
	@Enumerated(EnumType.ORDINAL)
	private BankAccountOwnerType accountOwnerType;
	
	//  `nama_pemilik_rekening` varchar(225) DEFAULT NULL,
	@Column(name = "nama_pemilik_rekening")
	private String accountOwnerName;
	
	@Override
	public String toString() {
		return "Bank [namaBank=" + namaBank + ", namaCabang=" + namaCabang + ", nomorRekening=" + nomorRekening
				+ ", note=" + note + "]";
	}

	public String getNamaBank() {
		return namaBank;
	}

	public void setNamaBank(String namaBank) {
		this.namaBank = namaBank;
	}

	public String getNamaCabang() {
		return namaCabang;
	}

	public void setNamaCabang(String namaCabang) {
		this.namaCabang = namaCabang;
	}

	public String getNomorRekening() {
		return nomorRekening;
	}

	public void setNomorRekening(String nomorRekening) {
		this.nomorRekening = nomorRekening;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Coa_05_Master getCoaMater() {
		return coaMater;
	}

	public void setCoaMater(Coa_05_Master coaMater) {
		this.coaMater = coaMater;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	@Override
	public boolean equals(Object o) {
	    if(o == null) {
	        return false;
	    } else if (!(o instanceof Bank)) {
	        return false;
	    } else {
	    	return ((Bank)o).getId().equals(this.getId());
	    }
	}

	public BankAccountOwnerType getAccountOwnerType() {
		return accountOwnerType;
	}

	public void setAccountOwnerType(BankAccountOwnerType accountOwnerType) {
		this.accountOwnerType = accountOwnerType;
	}

	public String getAccountOwnerName() {
		return accountOwnerName;
	}

	public void setAccountOwnerName(String accountOwnerName) {
		this.accountOwnerName = accountOwnerName;
	}
}
