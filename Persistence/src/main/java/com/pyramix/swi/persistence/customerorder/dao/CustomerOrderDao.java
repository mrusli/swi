package com.pyramix.swi.persistence.customerorder.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;

public interface CustomerOrderDao {

	public CustomerOrder findCustomerOrderById(Long id) throws Exception;
	
	public List<CustomerOrder> findAllCustomerOrder() throws Exception;

	/**
	 * select all CustomerOrder order by orderDate attribute.
	 * 
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return {@link List}CustomerOrder
	 * 
	 * @throws Exception
	 */
	public List<CustomerOrder> findAllCustomerOrder_OrderBy_OrderDate(boolean desc, boolean checkTransaction, boolean usePpn) throws Exception;
	
	public List<Customer> findUniqueCustomer(boolean checkTransaction, boolean usePpn, Date startDate, Date endDate) throws Exception;
	
	/**
	 * select CustomerOrder by startDate and endDate (date range) on orderDate attribute.
	 * 
	 * @param startDate - start date (for today date, use the same date as the end date)
	 * @param endDate - end date
	 * @param desc - determines the order. Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return {@link List}CustomerOrder
	 * 
	 * @throws Exception
	 */
	public List<CustomerOrder> findCustomerOrder_By_OrderDate(Date startDate, Date endDate, boolean desc, boolean checkTransaction, boolean usePpn) throws Exception;	
	
	public Long save(CustomerOrder customerOrder) throws Exception;
	
	public void update(CustomerOrder customerOrder) throws Exception;
	
	public CustomerOrder findCustomerByProxy(long id) throws Exception;
	
	public CustomerOrder findVoucherSalesByProxy(long id) throws Exception;

	public CustomerOrder findEmployeeCommissionsByProxy(long id) throws Exception;

	public CustomerOrder findSuratJalanByProxy(long id) throws Exception;	

	public CustomerOrder findCustomerReceivableByProxy(long id) throws Exception;
	
	public List<CustomerOrder> findNonPostingCustomerOrder() throws Exception;

	/**
	 * select the CustomerOrder by the boolean value of the Order - descending or ascending.  Passing a
	 * <code>true</code> value returns a list of CustomerOrder order by Customer name in descending order.  Passing a 
	 * <code>false</code> value returns otherwise.
	 * 
	 * @param desc - <code>true</code> - returns customer order by customer name in descending order.  <code>false</code> - returns
	 * customerorder list in ascending order.
	 * @return - list of {@link CustomerOrder}
	 * @throws Exception
	 */
	public List<CustomerOrder> findAllCustomerOrder_OrderBy_Customer(boolean desc) throws Exception;

	/**
	 * select the CustomerOrder by the boolean value of the paymentComplete parameter.
	 *  
	 * @param paymentComplete - <code>true</code> - select all CustomerOrder where the payment has been completed (lunas).
	 * - <code>false</code> - select all CustomerOrder where the payment has not been completed (cicil / not lunas).
	 * 
	 * @return list of {@link CustomerOrder}
	 * @throws Exception
	 */
	public List<CustomerOrder> findAllCustomerOrder_By_PaymentComplete(boolean paymentComplete) throws Exception;

	/**
	 * select the {@link CustomerOrder} based on the {@link Customer} and the boolean value of paymentComplete parameter.
	 * 
	 * @param customer - the customer to base on for the select
	 * @param paymentComplete - <code>true</code> - select all CustomerOrder where the payment has been completed (lunas).
	 * - <code>false</code> - select all CustomerOrder where the payment has not been completed (cicil / not lunas).
	 * 
	 * @return list of {@link CustomerOrder}
	 * @throws Exception
	 */
	public List<CustomerOrder> findAllCustomerOrder_By_CustomerPaymentComplete(Customer customer, boolean paymentComplete) throws Exception;	
	
	public List<CustomerOrder> findAllCustomerOrder_By_SuratJalanNotNull() throws Exception;

	/**
	 * Specific for Report ONLY - SuratJalan is NOT NULL
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<CustomerOrder> findAllCustomerOrder_By_DateRange(Date startDate, Date endDate) throws Exception;

	public List<CustomerOrder> findAllCustomerOrder_By_DateRange_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception;
	
	/**
	 * Get the inventory object by proxy.
	 * 
	 * @param id
	 * @return
	 */
	public CustomerOrderProduct findCustomerOrderProductInventoryByProxy(long id);
	
	
	
	public BigDecimal sumTotalOrderPaymentTypeTunai(Date startDate, Date endDate) throws Exception;
	
	
	
	public BigDecimal sumTotalOrderPaymentTypeAll(Date startDate, Date endDate) throws Exception;
	
	
	
	public BigDecimal sumTotalOrderPaymentTypeTunai_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception;
	
	
	
	public BigDecimal sumTotalOrderPaymentTypeAll_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception;
	
	
	
	public CustomerOrder findEmployeeSalesByProxy(long id) throws Exception;
	
	

	public List<CustomerOrder> findSelectedCustomerOrder_By_Filter(boolean desc, boolean checkTransaction,
			boolean usePpn, Customer customer, Date startDate, Date endDate) throws Exception;

	public List<CustomerOrder> findAllCustomerOrder_By_Filter(boolean listByDescendingOrder, boolean checkTransaction,
			boolean usePpn, Date startDate, Date endDate);
}
