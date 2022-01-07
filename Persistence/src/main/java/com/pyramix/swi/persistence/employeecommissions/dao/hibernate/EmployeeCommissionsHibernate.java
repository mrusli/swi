package com.pyramix.swi.persistence.employeecommissions.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
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
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_Date_OrderBy_CustomerOrderDate(boolean descending,
			Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.between("customerOrder.orderDate", startDate, endDate));
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
	
	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public List<Customer> findUniqueCustomer_By_Date(Employee employeeSales, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("customerOrder.customer")));

		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.setProjection(projectionList);
		if (employeeSales!=null) {
			criteria.add(Restrictions.eq("employee", employeeSales));			
		}
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.between("customerOrder.orderDate", startDate, endDate));
		// criteria.createAlias("customerOrder.customer", "customer");
		// criteria.addOrder(Order.asc(("customer.companyLegalName")));
		criteria.setResultTransformer(new ResultTransformer() {
			
			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				Customer customerObject = (Customer) tuple[0];
				
				Customer customer = new Customer();
				customer.setId(customerObject.getId());
				customer.setVersion(customerObject.getVersion());
				customer.setCompanyType(customerObject.getCompanyType());
				customer.setCompanyLegalName(customerObject.getCompanyLegalName());
				customer.setCompanyDisplayName(customerObject.getCompanyDisplayName());				
				customer.setContactPerson(customerObject.getContactPerson());
				customer.setAddress01(customerObject.getAddress01());
				customer.setAddress02(customerObject.getAddress02());
				customer.setCity(customerObject.getCity());
				customer.setPostalCode(customerObject.getPostalCode());
				customer.setPhone(customerObject.getPhone());
				customer.setExtension(customerObject.getExtension());
				customer.setEmail(customerObject.getEmail());
				customer.setFax(customerObject.getFax());
				customer.setNote(customerObject.getNote());
				customer.setActive(customerObject.isActive());
				customer.setCustomerReceivable(customerObject.getCustomerReceivable());	
				
				return customer;
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public List<Customer> transformList(List collection) {

				return collection;
			}
		});
		
		try {
			
			return criteria.list();

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}

	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId(long employeeId, boolean descending) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("employee", "employee");
		criteria.add(Restrictions.eq("employee.id", employeeId));
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
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId_By_Date(long employeeId, Date startDate,
			Date endDate, boolean descending) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("employee", "employee");
		criteria.add(Restrictions.eq("employee.id", employeeId));
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.between("customerOrder.orderDate", startDate, endDate));
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
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_Customer_By_Date_OrderBy_CustomerOrderDate(
			boolean descending, Customer customer, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.eq("customerOrder.customer", customer));
		criteria.add(Restrictions.between("customerOrder.orderDate", startDate, endDate));
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
	public List<EmployeeCommissions> findAllEmployeeCommissions_By_EmployeeId_By_Customer_By_Date_OrderBy_CustomerOrderDate(
			long employeeId, boolean descending, Customer customer, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EmployeeCommissions.class);
		criteria.createAlias("employee", "employee");
		criteria.add(Restrictions.eq("employee.id", employeeId));
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.eq("customerOrder.customer", customer));
		criteria.add(Restrictions.between("customerOrder.orderDate", startDate, endDate));		
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
