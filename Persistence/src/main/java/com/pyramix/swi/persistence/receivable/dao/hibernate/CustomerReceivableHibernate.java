package com.pyramix.swi.persistence.receivable.dao.hibernate;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;

public class CustomerReceivableHibernate extends DaoHibernate implements CustomerReceivableDao {
	
	@Override
	public CustomerReceivable findCustomerReceivableById(long id) throws Exception {

		return (CustomerReceivable) super.findById(CustomerReceivable.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerReceivable> findAllCustomerReceivable() throws Exception {

		return super.findAll(CustomerReceivable.class);
	}

	@Override
	public long save(CustomerReceivable customerReceivable) throws Exception {

		return super.save(customerReceivable);
	}

	@Override
	public void update(CustomerReceivable customerReceivable) throws Exception {

		super.update(customerReceivable);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerReceivable> findCustomerReceivablePendingPayment() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.add(Restrictions.isNotNull("latestDue"));
		criteria.createAlias("customer", "customer");
		criteria.addOrder(Order.asc("customer.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
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
	public List<CustomerReceivable> findCustomerReceivableWithNonZeroTotalReceivable() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.add(Restrictions.gt("totalReceivable", BigDecimal.ZERO));
		criteria.createAlias("customer", "customer");
		criteria.addOrder(Order.asc("customer.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	@Override
	public BigDecimal sumTotalReceivables() throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.setProjection(Projections.sum("totalReceivable"));
		
		if (criteria.list().get(0)==null) {
			return BigDecimal.ZERO;
		}
		
		return (BigDecimal)criteria.list().get(0);
	}
	
	@Override
	public CustomerReceivable findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		CustomerReceivable receivable = 
				(CustomerReceivable) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(receivable.getCustomer());
			
			return receivable;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public BigDecimal sumAmountSalesReceivableActivities(Customer selectedCustomerReceivable) throws Exception {		
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.createAlias("customer", "customerId");
		criteria.add(Restrictions.eq("customerId.id", selectedCustomerReceivable.getId()));
		criteria.createAlias("customerReceivableActivities", "receivableActivities");
		criteria.add(Restrictions.eq("receivableActivities.receivableStatus", DocumentStatus.NORMAL));
		criteria.setProjection(Projections.sum("receivableActivities.amountSales"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}
			
			return (BigDecimal)criteria.list().get(0);
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@Override
	public BigDecimal sumAmountSalesPpnReceivableActivities(Customer selectedCustomerReceivable) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.createAlias("customer", "customerId");
		criteria.add(Restrictions.eq("customerId.id", selectedCustomerReceivable.getId()));
		criteria.createAlias("customerReceivableActivities", "receivableActivities");
		criteria.add(Restrictions.eq("receivableActivities.receivableStatus", DocumentStatus.NORMAL));
		criteria.setProjection(Projections.sum("receivableActivities.amountSalesPpn"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}
			
			return (BigDecimal)criteria.list().get(0);
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@Override
	public BigDecimal sumAmountPaymentReceivableActivities(Customer selectedCustomerReceivable) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.createAlias("customer", "customerId");
		criteria.add(Restrictions.eq("customerId.id", selectedCustomerReceivable.getId()));
		criteria.createAlias("customerReceivableActivities", "receivableActivities");
		criteria.add(Restrictions.eq("receivableActivities.receivableStatus", DocumentStatus.NORMAL));
		criteria.setProjection(Projections.sum("receivableActivities.amountPaid"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}
			
			return (BigDecimal)criteria.list().get(0);
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerReceivable> findAllTransactionalCustomerReceivable(boolean asc) throws Exception {
		Session session = super.getSessionFactory().openSession();
	
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.createAlias("customerReceivableActivities", "receivableActivities");
		criteria.add(Restrictions.isNotNull("receivableActivities.amountSales"));
		criteria.createAlias("customer", "customerId");
		criteria.addOrder(Order.asc("customerId.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
/*** NOT USED
 * 
 * 	@Override
	public boolean customerNotExist(Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.add(Restrictions.eq("customer", customer));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list().isEmpty();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
*/

/*** NOT USED
 * 
 * 	@Override
	public CustomerReceivable findCustomerReceivableByCustomer(Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerReceivable.class);
		criteria.add(Restrictions.eq("customer", customer));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return (CustomerReceivable) criteria.list().get(0);
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
*/

}
