package com.pyramix.swi.persistence.employeecommissions.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.employeecommissions.dao.EmployeeCommissionsDao;

public class EmployeeCommissionsHibernate extends DaoHibernate implements EmployeeCommissionsDao {

	@Override
	public EmployeeCommissions findEmployeeCommissionsById(long id) throws Exception {
		
		return (EmployeeCommissions) super.findById(EmployeeCommissions.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeCommissions> findAllEmployeeCommissions() throws Exception {
		
		return new ArrayList<EmployeeCommissions>(super.findAll(EmployeeCommissions.class));
	}

	@Override
	public long save(EmployeeCommissions employeeCommissions) throws Exception {
		
		return super.save(employeeCommissions);
	}

	@Override
	public void update(EmployeeCommissions employeeCommissions) throws Exception {
		
		super.update(employeeCommissions);
	}

	@Override
	public EmployeeCommissions findCustomerOrderByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		EmployeeCommissions employeeCommissions = 
				(EmployeeCommissions) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(employeeCommissions.getCustomerOrder());
			
			return employeeCommissions;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeCommissions> findAllEmployeeCommissions_OrderBy_CustomerOrderDate(boolean descending) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.addOrder(descending ? Order.desc("customerOrder.orderDate") : Order.asc("customerOrder.orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<EmployeeCommissions>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId(long id, boolean descending) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("employee", "employee");
		criteria.add(Restrictions.eq("employee.id", id));
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.addOrder(descending ? Order.desc("customerOrder.orderDate") : Order.asc("customerOrder.orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<EmployeeCommissions>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

}
