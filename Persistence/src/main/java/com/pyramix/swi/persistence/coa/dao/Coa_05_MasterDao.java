package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.domain.coa.Coa_05_Master;

public interface Coa_05_MasterDao {

	public Coa_05_Master findCoa_05_MasterById(long id) throws Exception;
	
	public List<Coa_05_Master> findAllCoa_05_Master() throws Exception;
	
	public long save(Coa_05_Master master) throws Exception;
	
	public void update(Coa_05_Master master) throws Exception;
	
	public List<Coa_05_Master> findCoa_05_Master_By_AccountType(int accountTypeNo) throws Exception;

	public List<Coa_05_Master> findCoa_05_MasterBySubAccount02(Coa_04_SubAccount02 subAccount02) throws Exception;

	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_by_AccountType(int coaAccountType) throws Exception;

	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp() throws Exception;
	
	public Coa_05_Master findCoa_04_SubAccount02_ByProxy(long id) throws Exception;
}
