package com.pyramix.swi.persistence.suratjalan.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.suratjalan.SuratJalan;

public interface SuratJalanDao {

	public SuratJalan findSuratJalanById(long id) throws Exception;
	
	public List<SuratJalan> findAllSuratJalan() throws Exception;
	
	public long save(SuratJalan suratJalan) throws Exception;
	
	public void update(SuratJalan suratJalan) throws Exception;

	public SuratJalan findCustomerByProxy(long id) throws Exception;

	public SuratJalan findEmployeeCommissionsByProxy(long id) throws Exception;

	public SuratJalan findDeliveryOrderByProxy(long id) throws Exception;

	public SuratJalan findFakturByProxy(long id) throws Exception;

	/**
	 * select all SuratJalan order by the SuratJalan date.  Use the desc parameter
	 * to list the SuratJalan either ascending (true) or ascending (false).
	 * 
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @return {@link List}<SuratJalan>
	 * @throws Exception
	 */
	public List<SuratJalan> findAllSuratJalan_OrderBy_SuratJalanDate(boolean desc) throws Exception;

	/**
	 * select all SuratJalan by the SuratJalan date using the startDate and endDate parameter.  Use 
	 * the desc parameter to list the SuratJalan either ascending (true) or ascending (false).
	 * 
	 * @param startDate - start date (for today's date, use the same date as the endDate)
	 * @param endDate - end date
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @return {@link List}<SuratJalan>
	 * @throws Exception
	 */
	public List<SuratJalan> findAllSuratJalan_By_SuratJalanDate(Date startDate, Date endDate, boolean desc) throws Exception;
}
