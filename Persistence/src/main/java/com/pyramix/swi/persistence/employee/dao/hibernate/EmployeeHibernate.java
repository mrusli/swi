package com.pyramix.swi.persistence.employee.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeType;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;

public class EmployeeHibernate extends DaoHibernate implements EmployeeDao {

	@Override
	public Employee findEmployeeById(long id) throws Exception {
		
		return (Employee) super.findById(Employee.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> findAllEmployees() throws Exception {
		
		return new ArrayList<Employee>(super.findAll(Employee.class));
	}

	@Override
	public long save(Employee employee) throws Exception {
		
		return super.save(employee);
	}

	@Override
	public void update(Employee employee) throws Exception {
		
		super.update(employee);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> findAllEmployees_Receiving_Commission(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.add(Restrictions.eq("commission", true));
		criteria.addOrder(desc ? Order.desc("name") : Order.asc("name"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Employee>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@Override
	public Employee findCompanyByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		Employee employee = (Employee) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(employee.getCompany());
			
			return employee;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> findAllEmployeesByEmployeeType(EmployeeType employeeType, boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.add(Restrictions.eq("employeeType", employeeType));
		criteria.addOrder(desc ? Order.desc("name") : Order.asc("name"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Employee>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@Override
	public Employee findEmployeeKenadaraanByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		Employee employee = (Employee) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(employee.getEmployeeKendaraanList());
			
			return employee;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> findAllEmployeesBySelectedCompany(Company selectedCompany) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.add(Restrictions.eq("company", selectedCompany));
		criteria.addOrder(Order.asc("name"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Employee>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@Override
	public Employee findEmployeeLoginUserByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		Employee employee = (Employee) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(employee.getUser());
			
			return employee;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> findActiveEmployee(String name, Company company) throws Exception {
		Transaction tx = null;
		Session sessionCriteria = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();

			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Employee.class).get();
			
			Query qry = qb.keyword()
					.onField("name")
					.matching(name)
					.createQuery();
			
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, Employee.class);
			
			sessionCriteria = getSessionFactory().openSession();
			
			Criteria criteria = sessionCriteria.createCriteria(Employee.class);
			criteria.add(Restrictions.eq("active", true));
			criteria.add(Restrictions.eq("company", company));

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
	public List<Employee> findActiveEmployee_OrderBy_EmployeeName(boolean desc, Company company) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.addOrder(desc ? Order.desc("name") : Order.asc("name"));
		criteria.add(Restrictions.eq("active", true));
		criteria.add(Restrictions.eq("company", company));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<Employee>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
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

	@Override
	public Employee findEmployeeByName(String name) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.add(Restrictions.eq("name", name));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			
			return criteria.list().isEmpty() ? null : (Employee) criteria.list().get(0);
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
