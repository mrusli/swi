package com.pyramix.swi.domain.kendaraan;

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

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Employee;

@Entity
@Table(name = "kendaraan", schema = SchemaUtil.SCHEMA_COMMON)
public class Kendaraan extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6702374035290445529L;

	//  `stnk_expire_date` date DEFAULT NULL,
	@Column(name = "stnk_expire_date")
	@Temporal(TemporalType.DATE)
	private Date stnkExpiryDate;
	
	//  `jenis_kendaraan` int(11) DEFAULT NULL,
	@Column(name = "jenis_kendaraan")
	@Enumerated(EnumType.ORDINAL)
	private Jenis jenisKendaraan;
	
	//  `merk` int(11) DEFAULT NULL,
	@Column(name = "merk")
	@Enumerated(EnumType.ORDINAL)
	private Merk merkKendaraan;
	
	//	KEY `fk_golongan_kendaraan` (`golongan_kendaraan`),
	//	CONSTRAINT `const_golongan_kendaraan` FOREIGN KEY (`golongan_kendaraan`) REFERENCES `kendaraan_golongan` (`id`)
	//	`golongan_kendaraan_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "golongan_kendaraan_id_fk")
	private KendaraanGolongan golonganKendaraan;
	
	//   UNIQUE KEY `no_polisi` (`no_polisi`),
	//  `no_polisi` varchar(255) DEFAULT NULL,
	@Column(name = "no_polisi")
	private String nomorPolisi;
	
	//  `is_status_active` char(1) DEFAULT NULL,
	@Column(name = "is_status_active")
	@Type(type = "true_false")
	private boolean active;
	
	//	`tahun_pembuatan` date DEFAULT NULL,
	@Column(name = "tahun_pembuatan")
	@Temporal(TemporalType.DATE)
	private Date tahunPembuatan;
	
	//  `tipe` varchar(255) DEFAULT NULL,
	@Column(name = "tipe")
	private String tipeKendaraan;
	
	//  `warna` varchar(255) DEFAULT NULL,
	@Column(name = "warna")
	private String warna;

	// note varchar(255)
	@Column(name = "note")
	private String note;
	
	// `employee_join_kendaraan` (
	//		  `id_employee` bigint(20) NOT NULL,
	//		  `id_kendaraan` bigint(20) NOT NULL,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "employee_join_kendaraan",
			joinColumns = @JoinColumn(name = "id_kendaraan"),
			inverseJoinColumns = @JoinColumn(name = "id_employee"))
	private Employee employee;
	
	@Override
	public String toString() {
		
		return "Kendaraan[id="+getId()+
				", stnkExpiryDate="+getStnkExpiryDate()+
				", jenisKendaraan="+getJenisKendaraan().toString()+
				", merkKendaraan="+getMerkKendaraan().toString()+
				", golonganKendaraan="+getGolonganKendaraan().getGolongan()+
				", nomorPolisi="+getNomorPolisi()+
				", active="+isActive()+
				", tahunPembuatan="+getTahunPembuatan()+
				", tipeKendaraan="+getTipeKendaraan()+
				", warna="+getWarna()+
				"]";
	}
	
	public Date getStnkExpiryDate() {
		return stnkExpiryDate;
	}

	public void setStnkExpiryDate(Date stnkExpiryDate) {
		this.stnkExpiryDate = stnkExpiryDate;
	}

	public Jenis getJenisKendaraan() {
		return jenisKendaraan;
	}

	public void setJenisKendaraan(Jenis jenisKendaraan) {
		this.jenisKendaraan = jenisKendaraan;
	}

	public Merk getMerkKendaraan() {
		return merkKendaraan;
	}

	public void setMerkKendaraan(Merk merkKendaraan) {
		this.merkKendaraan = merkKendaraan;
	}

	public String getNomorPolisi() {
		return nomorPolisi;
	}

	public void setNomorPolisi(String nomorPolisi) {
		this.nomorPolisi = nomorPolisi;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getTahunPembuatan() {
		return tahunPembuatan;
	}

	public void setTahunPembuatan(Date tahunPembuatan) {
		this.tahunPembuatan = tahunPembuatan;
	}

	public String getTipeKendaraan() {
		return tipeKendaraan;
	}

	public void setTipeKendaraan(String tipeKendaraan) {
		this.tipeKendaraan = tipeKendaraan;
	}

	public String getWarna() {
		return warna;
	}

	public void setWarna(String warna) {
		this.warna = warna;
	}

	public KendaraanGolongan getGolonganKendaraan() {
		return golonganKendaraan;
	}

	public void setGolonganKendaraan(KendaraanGolongan golonganKendaraan) {
		this.golonganKendaraan = golonganKendaraan;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
