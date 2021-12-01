package com.pyramix.swi.persistence.kendaraan.dao;

import java.util.List;

import com.pyramix.swi.domain.kendaraan.KendaraanGolongan;

public interface KendaraanGolonganDao {

	public KendaraanGolongan findKendaraanGolonganById(long id) throws Exception;
	
	public List<KendaraanGolongan> findAllKendaraanGolongan() throws Exception;
	
	public Long save(KendaraanGolongan kendaraanGolongan) throws Exception;
	
	public void update(KendaraanGolongan kendaraanGolongan) throws Exception;

	public void delete(KendaraanGolongan golongan) throws Exception;
	
}
