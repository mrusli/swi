package com.pyramix.swi.persistence.employee.dao;

import java.util.List;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeType;

public interface EmployeeDao {

	/**
	 * find employee by Id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Employee findEmployeeById(long id) throws Exception;
	
	/**
	 * find all employees
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Employee> findAllEmployees() throws Exception;
	
	/**
	 * save a new employee
	 * 
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	public long save(Employee employee) throws Exception;
	
	/**
	 * update an existing employee
	 * 
	 * @param employee
	 * @throws Exception
	 */
	public void update(Employee employee) throws Exception;
	
	/**
	 * find all employees that receive commission
	 * 
	 * @param desc - determine the descending (true) or ascending (false) order of the employeeName list
	 * @return
	 * @throws Exception
	 */
	public List<Employee> findAllEmployees_Receiving_Commission(boolean desc) throws Exception;
	
	/**
	 * company by proxy 
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Employee findCompanyByProxy(long id) throws Exception;

	/**
	 * find all employees by an enumerated type EmployeeType.  Determine whether the list of employee names
	 * descending (true) or ascending (false).
	 * 
	 * @param employeeType - an enumerated type of EmployeeType
	 * @param desc - determine the descending (true) or ascending (false) order of the employeeName list
	 * @return
	 * @throws Exception
	 */
	public List<Employee> findAllEmployeesByEmployeeType(EmployeeType employeeType, boolean desc) throws Exception;
	
	
	/**
	 * employeeKendaraanList by proxy
	 * 
	 * @param id - employee id
	 * @return
	 * @throws Exception
	 */
	public Employee findEmployeeKenadaraanByProxy(long id) throws Exception;
	
	/**
	 * find all employees given the company object -- return the employees for the selected company
	 * 
	 * @param Company
	 * @return List<Employee>
	 * @throws Exception
	 */
	public List<Employee> findAllEmployeesBySelectedCompany(Company selectedCompany) throws Exception;

	/**
	 * employee login user (User) by proxy
	 * 
	 * @param id
	 * @return Employee
	 * @throws Exception
	 */
	public Employee findEmployeeLoginUserByProxy(long id) throws Exception;

	/**
	 * find active employee for the selected company
	 * 
	 * @param name
	 * @param company 
	 * @throws Exception
	 */
	public List<Employee> findActiveEmployee(String name, Company company) throws Exception;

	/**
	 * find active employee order by employee name (either descending or ascending) for the selected company
	 * 
	 * @param desc
	 * @param company 
	 */
	public List<Employee> findActiveEmployee_OrderBy_EmployeeName(boolean desc, Company company) throws Exception;

	public void createIndexer() throws Exception;

	/**
	 * find employee by name
	 * 
	 * @param name
	 * @return Employee
	 * @throws Exception
	 */
	public Employee findEmployeeByName(String name) throws Exception;
}
