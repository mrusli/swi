package com.pyramix.swi.persistence.customer.dao;

import java.util.List;

import com.pyramix.swi.domain.organization.Customer;

public interface CustomerDao {

	public Customer findCustomerById(long id) throws Exception;
	
	public List<Customer> findAllCustomer() throws Exception;
	
	public Long save(Customer customer) throws Exception;
	
	public void update(Customer customer) throws Exception;
	
	public void createIndexer() throws Exception;
	
	public List<Customer> searchActiveCustomer(String searchStr) throws Exception;

	public List<Customer> findAllCustomer_OrderBy_CompanyName(boolean desc) throws Exception;
	
	public List<Customer> findActiveCustomer_OrderBy_CompanyName(boolean desc) throws Exception;

	public List<Customer> findCustomer_OfAlphabet(String alphaGroupDisp) throws Exception;

	public Customer findCustomerReceivableByProxy(long id) throws Exception;
	
}
