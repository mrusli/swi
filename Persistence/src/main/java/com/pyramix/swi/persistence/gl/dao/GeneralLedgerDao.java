package com.pyramix.swi.persistence.gl.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.GeneralLedger;

public interface GeneralLedgerDao {

	public GeneralLedger findGeneralLedgerById(long id) throws Exception;
	
	public List<GeneralLedger> findAllGeneralLedger() throws Exception;
	
	/**
	 * NOT by postingDate yet
	 * 
	 * @param masterCoa
	 * @param postingDate
	 * @return
	 * @throws Exception
	 */
	public List<GeneralLedger> findAllGeneralLedgerByMasterCoa_PostingDate(Coa_05_Master masterCoa, Date postingDate) throws Exception;
	
	public Long save(GeneralLedger generalLedger) throws Exception;
	
	public void update(GeneralLedger generalLedger) throws Exception;
	
}
