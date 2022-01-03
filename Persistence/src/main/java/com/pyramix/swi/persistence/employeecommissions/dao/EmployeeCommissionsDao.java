package com.pyramix.swi.persistence.employeecommissions.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.EmployeeCommissions;

public interface EmployeeCommissionsDao {

	/**
	 * find by EmployeeCommissions Id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public EmployeeCommissions findEmployeeCommissionsById(long id) throws Exception;
	
	/**
	 * list all EmployeeCommissions
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions() throws Exception;
	
	/**
	 * save EmployeeCommissions
	 * 
	 * @param employeeCommissions
	 * @return
	 * @throws Exception
	 */
	public long save(EmployeeCommissions employeeCommissions) throws Exception;
	
	/**
	 * update EmployeeCommissions
	 * 
	 * @param employeeCommissions
	 * @throws Exception
	 */
	public void update(EmployeeCommissions employeeCommissions) throws Exception;

	/**
	 * list EmployeeCommissions Order By CustomerOrderDate in descending order (true) or ascending order (false)
	 * 
	 * @param descending
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_OrderBy_CustomerOrderDate(boolean descending) throws Exception;
	
	/**
	 * list EmployeeCommissions Order By CustomerOrderDate in descending order (true) or ascending order (false)
	 * by dates
	 * 
	 * @param descending
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_Date_OrderBy_CustomerOrderDate(
			boolean descending, Date startDate, Date endDate) throws Exception;	
	
	// proxy
	
	/**
	 * access the CustomerOrder of this EmployeeCommissions
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public EmployeeCommissions findCustomerOrderByProxy(long id) throws Exception;

	/**
	 * find employee commissions for a specific sales employee (use the employee id)
	 * 
	 * @param employeeId
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId(long employeeId, boolean descending) throws Exception;

	/**
	 * find employee commissions for a specific sales employee (use the employee id)
	 * by dates
	 * 
	 * @param employeeId
	 * @param startDate
	 * @param endDate
	 * @param descending
	 * @return
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId_By_Date(long employeeId, 
			Date startDate, Date endDate, boolean descending);
	
	
	/**
	 * find employee commissions by customer and dates
	 * 
	 * @param descending
	 * @param customer
	 * @param startDate
	 * @param endDate
	 * @return {@link List} {@link EmployeeCommissions}
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_Customer_By_Date_OrderBy_CustomerOrderDate(
			boolean descending, Customer customer, Date startDate, Date endDate) throws Exception;

	/**
	 * @param employeeId
	 * @param descending
	 * @param customer
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId_By_Customer_By_Date_OrderBy_CustomerOrderDate(
			long employeeId, boolean descending, Customer customer, Date startDate, Date endDate) throws Exception;


}
