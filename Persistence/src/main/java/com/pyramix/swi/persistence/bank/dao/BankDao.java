package com.pyramix.swi.persistence.bank.dao;

import java.util.List;

import com.pyramix.swi.domain.bank.Bank;

public interface BankDao {

	/**
	 * @param id
	 * @return Bank
	 * @throws Exception
	 */
	public Bank findBankById(long id) throws Exception;
	
	/**
	 * @return List<Bank>
	 * @throws Exception
	 */
	public List<Bank> findAllBank() throws Exception;
	
	/**
	 * @param bank
	 * @return Long
	 * @throws Exception
	 */
	public Long save(Bank bank) throws Exception;
	
	/**
	 * @param bank
	 * @throws Exception
	 */
	public void update(Bank bank) throws Exception;
}
