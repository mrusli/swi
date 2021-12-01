package com.pyramix.swi.persistence.gl.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.EndingBalance;

public interface EndingBalanceDao {

	public EndingBalance findEndingBalanceById(long id) throws Exception;
	
	public List<EndingBalance> findAllEndingBalance() throws Exception;
	
	/**
	 * Guarantees return only ONE row because the combination of masterCoa and endingDate is made uniqe in the table.
	 * i.e., table rejects multiple coa with the same date
	 * 
	 * @param masterCoa
	 * @param endingDate
	 * @return EndingBalance
	 * @throws Exception
	 */
	public EndingBalance findEndingBalanceByMasterCoa_EndingDate(Coa_05_Master masterCoa, Date endingDate) throws Exception;
	
	/**
	 * Select unique CoaMasters with ending balance that had been saved into the table
	 * -- we need this information to select the Ending Balance dates and amount by CoaMaster
	 * 
	 * @return List of Coa_05_Master
	 * @throws Exception
	 */
	public List<Coa_05_Master> findUniqueCoaMasterEndingBalance() throws Exception;
	
	public List<EndingBalance> findAllEndingBalance_By_CoaMaster(Coa_05_Master coaMaster) throws Exception;
	
	public Long save(EndingBalance endingBalance) throws Exception;
	
	public void update(EndingBalance endingBalance) throws Exception;

	public List<EndingBalance> findAllEndingBalance_OrderBy_CoaMaster_EndingDate() throws Exception;
}
