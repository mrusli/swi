package com.pyramix.swi.persistence.receivableactivity.dao.hibernate;

import java.util.List;

import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.receivableactivity.dao.CustomerReceivableActivityDao;

public class CustomerReceivableActivityHibernate extends DaoHibernate implements CustomerReceivableActivityDao {

	@Override
	public CustomerReceivableActivity findCustomerReceivableActivityById(long id) throws Exception {
		
		return (CustomerReceivableActivity) super.findById(CustomerReceivableActivity.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerReceivableActivity> findAllCustomerReceivableActivity() throws Exception {
		
		return super.findAll(CustomerReceivableActivity.class);
	}

	@Override
	public Long save(CustomerReceivableActivity customerReceivableActivity) throws Exception {
		
		return super.save(customerReceivableActivity);
	}

	@Override
	public void update(CustomerReceivableActivity customerReceivableActivity) throws Exception {
		
		super.update(customerReceivableActivity);
	}

	@Override
	public void delete(CustomerReceivableActivity customerReceivableActivity) throws Exception {
		
		super.delete(customerReceivableActivity);
	}

}
