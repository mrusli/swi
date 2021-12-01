package com.pyramix.swi.domain.kendaraan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "kendaraan_golongan", schema = SchemaUtil.SCHEMA_COMMON)
public class KendaraanGolongan extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7746192047668747508L;

	//  `golongan` int(11) DEFAULT NULL,
	@Column(name = "golongan")
	@Enumerated(EnumType.ORDINAL)
	private Golongan golongan;
	
	//  `penjelasan` varchar(255) DEFAULT NULL,
	@Column(name = "penjelasan")
	private String penjelasan;

	public Golongan getGolongan() {
		return golongan;
	}

	public void setGolongan(Golongan golongan) {
		this.golongan = golongan;
	}

	public String getPenjelasan() {
		return penjelasan;
	}

	public void setPenjelasan(String penjelasan) {
		this.penjelasan = penjelasan;
	}
	
	
}
