package com.pyramix.swi.persistence.receivableactivity.dao;

import java.util.List;

import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;

public interface CustomerReceivableActivityDao {

	public CustomerReceivableActivity findCustomerReceivableActivityById(long id) throws Exception;
	
	public List<CustomerReceivableActivity> findAllCustomerReceivableActivity() throws Exception;
	
	public Long save(CustomerReceivableActivity customerReceivableActivity) throws Exception;
	
	public void update(CustomerReceivableActivity customerReceivableActivity) throws Exception;
	
	public void delete(CustomerReceivableActivity customerReceivableActivity) throws Exception;
	
}
