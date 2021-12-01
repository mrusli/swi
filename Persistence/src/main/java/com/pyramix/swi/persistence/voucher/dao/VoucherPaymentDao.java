package com.pyramix.swi.persistence.voucher.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.voucher.VoucherPayment;

public interface VoucherPaymentDao {

	public VoucherPayment findVoucherPaymentById(Long id) throws Exception;
	
	public List<VoucherPayment> findAllVoucherPayment() throws Exception;

	/**
	 * selects all VoucherPayment order by TransactionDate attribute.
	 * 
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return List<VoucherPayment>
	 * 
	 * @throws Exception
	 */
	public List<VoucherPayment> findAllVoucherPayment_OrderBy_TransactionDate(boolean desc) throws Exception;
	
	/**
	 * selects VoucherPayment based on the startDate and endDate of the TransactionDate attribute.
	 * to select VoucherPayment for today, pass the same date for the start and end dates.
	 * 
	 * @param startDate - start date
	 * @param endDate - end date
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return List<VoucherPayment>
	 * 
	 * @throws Exception
	 */
	public List<VoucherPayment> findAllVoucherPayment_By_TransactionDate(Date startDate, Date endDate, boolean desc) throws Exception;

	public Long save(VoucherPayment voucherPayment) throws Exception;
	
	public void update(VoucherPayment voucherPayment) throws Exception;

	public VoucherPayment findCustomerByProxy(long id) throws Exception;

	public VoucherPayment findGiroByProxy(long id) throws Exception;

	public VoucherPayment findSettlementByProxy(long id) throws Exception;

	public VoucherPayment findPostingVoucherNumberByProxy(long id) throws Exception;

	public VoucherPayment findUserCreateByProxy(Long id) throws Exception;

	
}
