package com.pyramix.swi.persistence.customerorder.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;

public class CustomerOrderProductHibernate extends DaoHibernate implements CustomerOrderProductDao {

	@Override
	public CustomerOrderProduct findInventoryCodeByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrderProduct.class);
		CustomerOrderProduct product = 	
				(CustomerOrderProduct) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(product.getInventoryCode());
			
			return product;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	public CustomerOrderProduct findInventoryByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrderProduct.class);
		CustomerOrderProduct product = 	
				(CustomerOrderProduct) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(product.getInventory());
			
			return product;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}
}
