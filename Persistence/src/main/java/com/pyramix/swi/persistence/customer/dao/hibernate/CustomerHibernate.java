package com.pyramix.swi.persistence.customer.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;

public class CustomerHibernate extends DaoHibernate implements CustomerDao {

	@Override
	public Customer findCustomerById(long id) throws Exception {

		return (Customer) super.findById(Customer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> findAllCustomer() throws Exception {

		return new ArrayList<Customer>(super.findAll(Customer.class));
	}

	@Override
	public Long save(Customer customer) throws Exception {

		return super.save(customer);
	}

	@Override
	public void update(Customer customer) throws Exception {

		super.update(customer);
	}

	@Override
	public void createIndexer() throws Exception {
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			fullTextSession.createIndexer().startAndWait();
		} catch (InterruptedException e) {			
			throw new Exception(e.getMessage());
			
		} finally {
			sessionFullText.close();
			
		}				
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> searchActiveCustomer(String searchStr) throws Exception {
		Transaction tx = null;
		Session sessionCriteria = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Customer.class).get();
			
			Query qry = qb.keyword()
					.onField("companyLegalName")
					.matching(searchStr)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, Customer.class);
			
			sessionCriteria = getSessionFactory().openSession();
			
			Criteria criteria = sessionCriteria.createCriteria(Customer.class);
			criteria.add(Restrictions.eq("active", true));

			fullTextQuery.setCriteriaQuery(criteria);
			
			return fullTextQuery.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			sessionCriteria.close();
			tx.commit();
			sessionFullText.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> findActiveCustomer_OrderBy_CompanyName(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Customer.class);
		criteria.add(Restrictions.eq("active", true));
		criteria.addOrder(desc ? Order.desc("companyLegalName") : Order.asc("companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<Customer>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> findAllCustomer_OrderBy_CompanyName(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Customer.class);
		criteria.addOrder(desc ? Order.desc("companyLegalName") : Order.asc("companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<Customer>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> findCustomer_OfAlphabet(String alphaGroupDisp) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Customer.class);
		Disjunction or = Restrictions.disjunction();
		for(int i=0; i<alphaGroupDisp.length(); i++) {
			or.add(Restrictions.like("companyLegalName", alphaGroupDisp.charAt(i)+"%"));
		}
		criteria.add(or);
		criteria.addOrder(Order.asc("companyLegalName"));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {

			return new ArrayList<Customer>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Customer findCustomerReceivableByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Customer.class);
		Customer customer = (Customer) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(customer.getCustomerReceivable());
			
			return customer;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}
}
