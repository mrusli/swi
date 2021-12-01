package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;

public interface Coa_02_AccountGroupDao {

	public Coa_02_AccountGroup findCoa_02_AccountGroupById(long id) throws Exception;
	
	public List<Coa_02_AccountGroup> findAllCoa_02_AccountGroup() throws Exception;
	
	public long save(Coa_02_AccountGroup accountGroup) throws Exception;
	
	public void update(Coa_02_AccountGroup accountGroup) throws Exception;

	public List<Coa_02_AccountGroup> findCoa_02_AccountGroupByAccountType(Coa_01_AccountType selAccountType) throws Exception;

	public List<Coa_02_AccountGroup> findCoa_02_AccountGroup_By_AccountType(int selAccountType) throws Exception;

	public Coa_02_AccountGroup findCoa_01_AccountType_ByProxy(long id) throws Exception;
}
