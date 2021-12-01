package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_Receivables;

public interface Coa_ReceivablesDao {

	public Coa_Receivables findCoaReceivablesById(long id) throws Exception;
	
	public List<Coa_Receivables> findAllCoaReceivables() throws Exception;
	
	public long save(Coa_Receivables coaReceivables) throws Exception;
	
	public void update(Coa_Receivables coaReceivables) throws Exception;

	/**
	 * Find all ACTIVE Coa_Receivables
	 * 
	 * @return List<Coa_Receivables>
	 * @throws Exception
	 */
	public List<Coa_Receivables> findAllActiveCoaReceivables() throws Exception;
}
