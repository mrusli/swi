package com.pyramix.swi.persistence.coa.dao;

import java.util.List;

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;

public interface Coa_VoucherDao {

	public Coa_Voucher findCoaVoucherById(long id) throws Exception;
	
	public List<Coa_Voucher> findAllCoaVoucher() throws Exception;
	
	public long save(Coa_Voucher coaVoucher) throws Exception;
	
	public void update(Coa_Voucher coaVoucher) throws Exception;


	/**
	 * Coa for Voucher is meant for {@link VoucherPayment} or other vouchers that require user
	 * selection of coa when debit/credit are created.  Coa_Vouchers are needed
	 * for payment either via {@link VoucherGiroReceipt} or directly into {@link VoucherPayment}.  
	 * If payment is of type Bank, then user will be able to select the Coa_Vouchers to which
	 * coa the payment is made. 
	 * 
	 * The list of coa vouchers are managed by its own ListInfo.
	 * 
	 * @param voucherType - pass the required VoucherType according to PaymentType.
	 * @param debitAccount - true for all the debit accounts.  false for all the credit accounts.
	 * @return list of {@link Coa_Adjustment}
	 * @throws Exception
	 */
	public List<Coa_Voucher> findCoaVoucherByDebitAccount(boolean debitAccount) throws Exception;
	
}
