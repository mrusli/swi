package com.pyramix.swi.persistence.voucher.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;

public interface VoucherGiroReceiptDao {

	public VoucherGiroReceipt findVoucherGiroReceiptById(Long id) throws Exception;
	
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt() throws Exception;

	/**
	 * selects all VoucherGiroReceipt order by TransactionDate attribute
	 * 
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 *  
	 * @return List<VoucherGiroReceipt>
	 * 
	 * @throws Exception
	 */
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_OrderBy_TransactionDate(boolean desc) throws Exception;

	/**
	 * selects VoucherGiroReceipt by TransactionDate attribute (start and end date)
	 * 
	 * @param startDate - the start date
	 * @param endDate - the end date (today only, use the same date for the start and end dates)
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return List<VoucherGiroReceipt>
	 * 
	 * @throws Exception
	 */
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_By_TransactionDate(Date startDate, Date endDate,
			boolean desc) throws Exception;	
	
	/**
	 * selects VoucherGiroReceipt by GiroDate attribute (start and end date)
	 * 
	 * @param startDate - the start date
	 * @param endDate - the end date (today only, use the same date for the start and end dates)
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return {@link List} of {@link VoucherGiroReceipt}
	 * 
	 * @throws Exception
	 */
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_By_GiroDate(Date startDate, Date endDate, boolean desc) throws Exception;

	public Long save(VoucherGiroReceipt voucherGiroReceipt) throws Exception;
	
	public void update(VoucherGiroReceipt voucherGiroReceipt) throws Exception;

	public VoucherGiroReceipt findCustomerByProxy(Long id) throws Exception;

	// public VoucherGiroReceipt findVoucherPaymentByProxy(Long id) throws Exception;

	public VoucherGiroReceipt findGiroByProxy(Long id) throws Exception;

	public VoucherGiroReceipt findUserCreateByProxy(Long id) throws Exception;

	public VoucherGiroReceipt findPostingVoucherNumberByProxy(Long id) throws Exception;


}
