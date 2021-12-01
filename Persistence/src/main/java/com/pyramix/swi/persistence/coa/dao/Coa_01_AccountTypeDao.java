package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;

public interface Coa_01_AccountTypeDao {

	public Coa_01_AccountType findCoa_01_AccountTypeById(long id) throws Exception;
	
	public List<Coa_01_AccountType> findAllCoa_01_AccountType() throws Exception;
	
	public long save(Coa_01_AccountType accountType) throws Exception;
	
	public void update(Coa_01_AccountType accountType) throws Exception;
}
