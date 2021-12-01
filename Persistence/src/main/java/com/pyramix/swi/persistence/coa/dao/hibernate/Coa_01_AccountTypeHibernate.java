package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_01_AccountTypeHibernate extends DaoHibernate implements Coa_01_AccountTypeDao {

	@Override
	public Coa_01_AccountType findCoa_01_AccountTypeById(long id) throws Exception {

		return (Coa_01_AccountType) super.findById(Coa_01_AccountType.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_01_AccountType> findAllCoa_01_AccountType() throws Exception {

		return new ArrayList<Coa_01_AccountType>(super.findAll(Coa_01_AccountType.class));
	}

	@Override
	public long save(Coa_01_AccountType accountType) throws Exception {
		
		return super.save(accountType);
	}

	@Override
	public void update(Coa_01_AccountType accountType) throws Exception {

		super.update(accountType);
	}
	
}