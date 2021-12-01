package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;

public interface Coa_03_SubAccount01Dao {

	public Coa_03_SubAccount01 findCoa_03_SubAccount01ById(long id) throws Exception;
	
	public List<Coa_03_SubAccount01> findAllCoa_03SubAccount01() throws Exception;
	
	public long save(Coa_03_SubAccount01 subAccount01) throws Exception;
	
	public void update(Coa_03_SubAccount01 subAccount01) throws Exception;

	public List<Coa_03_SubAccount01> findCoa_03_SubAccount01ByAccountGroup(
			Coa_02_AccountGroup accountGroup) throws Exception;

	public List<Coa_03_SubAccount01> findCoa_03_SubAccount01_By_AccountType(int selAccountType) throws Exception;

	public Coa_03_SubAccount01 findCoa_02_AccountGroup_ByProxy(long id) throws Exception;
}
