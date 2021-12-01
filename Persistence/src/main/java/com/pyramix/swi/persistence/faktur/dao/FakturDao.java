package com.pyramix.swi.persistence.faktur.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.faktur.Faktur;

public interface FakturDao {

	public Faktur findFakturById(long id) throws Exception;
	
	public List<Faktur> findAllFaktur() throws Exception;
	
	public Long save(Faktur faktur) throws Exception;
	
	public void update(Faktur faktur) throws Exception;
	
	/**
	 * Re-initialize faktur with customer attribute with id of
	 * the specific faktur
	 * 
	 * @param id - faktur id
	 * @return Faktur
	 * @throws Exception
	 */
	public Faktur findCustomerByProxy(long id) throws Exception;

	/**
	 * select all Faktur order by the Faktur date.  Use the desc parameter
	 * to list the Faktur either descending (true) or ascending (false).
	 * 
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @return {@link List}<Faktur>
	 */
	public List<Faktur> findAllFaktur_OrderBy_FakturDate(boolean desc);

	/**
	 * select all Faktur by the Faktur date using the startDate and endDate parameter.  Use 
	 * the desc parameter to list the Faktur either descending (true) or ascending (false).
	 * 
	 * @param startDate - start date (for today's date, use the same date as the endDate)
	 * @param endDate - end date
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @return {@link List}<Faktur>
	 */
	public List<Faktur> findAllFaktur_OrderBy_FakturDate(Date startDate, Date endDate, boolean desc);

	/**
	 * Re-initialize faktur with user attribute with id of
	 * the specific faktur
	 * 
	 * @param id - faktur id
	 * @return Faktur
	 */
	public Faktur findUserByProxy(long id);

	/**
	 * Re-initialize faktur with suratJalan attribute with id of
	 * the specific faktur
	 * 
	 * @param id - faktur id
	 * @return Faktur
	 */
	public Faktur findSuratJalanByProxy(long id);
	
}
