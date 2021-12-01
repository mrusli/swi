package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;

public interface Coa_04_SubAccount02Dao {

	public Coa_04_SubAccount02 findCoa_04_SubAccount02ById(long id) throws Exception;
	
	public List<Coa_04_SubAccount02> findAllCoa_04_SubAccount02() throws Exception;
	
	public long save(Coa_04_SubAccount02 subAccount02) throws Exception;
	
	public void update(Coa_04_SubAccount02 subAccount02) throws Exception;

	public List<Coa_04_SubAccount02> findCoa_04_SubAccount02BySubAccount01(
			Coa_03_SubAccount01 subAccount01) throws Exception;

	public Coa_04_SubAccount02 findAccountMastersByProxy(Long id) throws Exception;

	public List<Coa_04_SubAccount02> findCoa_04_SubAccount02_By_AccountType(int selAccountType) throws Exception;

	public Coa_04_SubAccount02 findCoa_03_SubAccount01_ByProxy(long id) throws Exception;;
}
