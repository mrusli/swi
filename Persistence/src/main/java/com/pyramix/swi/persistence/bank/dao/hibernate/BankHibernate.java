package com.pyramix.swi.persistence.bank.dao.hibernate;

import java.util.List;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.persistence.bank.dao.BankDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class BankHibernate extends DaoHibernate implements BankDao {

	@Override
	public Bank findBankById(long id) throws Exception {

		return (Bank) super.findById(Bank.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Bank> findAllBank() throws Exception {

		return super.findAll(Bank.class);
	}

	@Override
	public Long save(Bank bank) throws Exception {

		return super.save(bank);
	}

	@Override
	public void update(Bank bank) throws Exception {

		super.update(bank);
	}

}
