package com.pyramix.swi.persistence.receivable.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;

public interface CustomerReceivableDao {

	public CustomerReceivable findCustomerReceivableById(long id) throws Exception;
	
	public List<CustomerReceivable> findAllCustomerReceivable() throws Exception;
	
	public long save(CustomerReceivable customerReceivable) throws Exception;
	
	public void update(CustomerReceivable customerReceivable) throws Exception;

	/**
	 * Not all CustomerReceivables will be returned.  ONLY CustomerReceivables containing
	 * 'latestDueDate' will be will returned.  During VoucherSales posting, the system calculates
	 * the 'dueDate' for the transaction and update the 'latestDueDate' for the
	 * CustomerReceivable.
	 * 
	 * @return {@link List} of CustomerReceivable
	 * @throws Exception
	 */
	public List<CustomerReceivable> findCustomerReceivablePendingPayment() throws Exception;

	
	
	public List<CustomerReceivable> findCustomerReceivableWithNonZeroTotalReceivable() throws Exception;
	
	
	
	public BigDecimal sumTotalReceivables() throws Exception;
	

	
	public BigDecimal sumAmountSalesReceivableActivities(Customer selectedCustomerReceivable) throws Exception;
	

	public BigDecimal sumAmountSalesPpnReceivableActivities(Customer selectedCustomerReceivable) throws Exception;

	
	public BigDecimal sumAmountPaymentReceivableActivities(Customer selectedCustomerReceivable) throws Exception;
	
	
	
	public CustomerReceivable findCustomerByProxy(long id) throws Exception;
	
	
	
	public List<CustomerReceivable> findAllTransactionalCustomerReceivable(boolean asc) throws Exception;

	
	
	
	// *** NOT USED
	// public boolean customerNotExist(Customer customer) throws Exception;

	// *** NOT USED
	// public CustomerReceivable findCustomerReceivableByCustomer(Customer customer) throws Exception;
}
