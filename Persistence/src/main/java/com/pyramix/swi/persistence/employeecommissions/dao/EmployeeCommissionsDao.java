package com.pyramix.swi.persistence.employeecommissions.dao;

import java.util.List;

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
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId(long id, boolean descending) throws Exception;
}
