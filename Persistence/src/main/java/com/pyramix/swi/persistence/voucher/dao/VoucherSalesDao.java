package com.pyramix.swi.persistence.voucher.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.voucher.VoucherSales;

public interface VoucherSalesDao {

	public VoucherSales findVoucherById(Long id) throws Exception;
	
	// save and update
	
	public Long save(VoucherSales voucherSales) throws Exception;
	
	public void update(VoucherSales voucherSales) throws Exception;
	
	// list
	
	public List<VoucherSales> findAllVoucher() throws Exception;
	
	/**
	 * select all VoucherSales order by transactionDate attribute.
	 * 
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return {@link List}<VoucherSales>
	 * 
	 * @throws Exception
	 */
	public List<VoucherSales> findAllVoucher_OrderBy_TransactionDate(boolean desc) throws Exception;
	
	/**
	 * select VoucherSales based on startDate and endDate against the transactionDate attribute. The list is
	 * ordered by transactionDate.
	 *
	 * @param startDate - start date (for today's date, use the same date as the endDate)
	 * @param endDate - end date
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return {@link List}<VoucherSales>
	 * 
	 * @throws Exception
	 */
	public List<VoucherSales> findVoucher_By_TransactionDate(Date startDate, Date endDate, boolean desc) throws Exception;
	
	// proxy
	
	public VoucherSales findCustomerByProxy(Long id) throws Exception;

	public VoucherSales findCustomerOrderByProxy(long id) throws Exception;

	public VoucherSales findGeneralLedgerByProxy(long id) throws Exception;
	
	//
	
	public List<VoucherSales> findAllVoucher_OrderBy_Customer(boolean desc) throws Exception;


}
