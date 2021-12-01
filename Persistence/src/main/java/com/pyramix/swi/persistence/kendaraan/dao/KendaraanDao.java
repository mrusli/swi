package com.pyramix.swi.persistence.kendaraan.dao;

import java.util.List;

import com.pyramix.swi.domain.kendaraan.Kendaraan;

public interface KendaraanDao {

	/**
	 * find Kendaraan by Id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Kendaraan findKendaraanById(long id) throws Exception;
	
	/**
	 * find all Kendaraan
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Kendaraan> findAllKendaraan() throws Exception;
	
	/**
	 * save a new Kendaraan
	 * 
	 * @param kendaraan
	 * @return
	 * @throws Exception
	 */
	public Long save(Kendaraan kendaraan) throws Exception;
	
	/**
	 * update an existing Kendaraan
	 * 
	 * @param kendaraan
	 * @throws Exception
	 */
	public void update(Kendaraan kendaraan) throws Exception;
	
	/**
	 * find all active Kendaraan
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Kendaraan> findAllActiveKendaraan() throws Exception;

	
	/**
	 * find the Employee of this Kendaraan by Proxy
	 * 
	 * @param id
	 * @throws Exception
	 */
	public Kendaraan findEmployeeKendaraanByProxy(long id) throws Exception;
}
