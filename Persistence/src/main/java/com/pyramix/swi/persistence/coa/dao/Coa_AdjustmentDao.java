package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;

public interface Coa_AdjustmentDao {

	public Coa_Adjustment findCoaAdjustmentById(long id) throws Exception;
	
	public List<Coa_Adjustment> findAllCoaAdjustment() throws Exception;
	
	public long save(Coa_Adjustment coaAdjustment) throws Exception;
	
	public void update(Coa_Adjustment coaAdjustment) throws Exception;

	/**
	 * Coa for adjustment in either in {@link VoucherGiroReceipt} or {@link VoucherPayment}.  Adjustments are needed
	 * for customer payments that are more or less than the required amount.  If payment is more than the required 
	 * amount, pass a false parameter, true otherwise.  This methods returns a list of <b>active</b> coa required for adjustment.
	 * 
	 * The list of coa adjustments are managed by its own ListInfo.
	 * 
	 * @param debitAccount - true for all the debit accounts.  false for all the credit accounts.
	 * @return list of {@link Coa_Adjustment}
	 * @throws Exception
	 */
	public List<Coa_Adjustment> findCoaAdjustmentByDebitAccount(boolean debitAccount) throws Exception;
	
}
