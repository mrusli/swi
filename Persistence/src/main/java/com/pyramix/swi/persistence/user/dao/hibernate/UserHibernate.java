package com.pyramix.swi.persistence.user.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.user.dao.UserDao;

public class UserHibernate implements UserDao {

	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	
	
	@Override
	public User findUserById(Long id) {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(User.class);
		
		try {
			return (User) criteria.add(Restrictions.idEq(id)).uniqueResult();
		} finally {
			session.close();
		}
	}

	@Override
	public User findUserByUsername(String username) {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("user_name", username));
		
		try {
			return (User) criteria.uniqueResult();
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsers() {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(User.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {			
			return new ArrayList<User>(criteria.list());
		} finally {
			session.close();
		}
	}

	@Override
	public Long save(User user) throws Exception {
		Long id = new Long(-1);
		
		Session session = getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		
		try {
			id = (Long) session.save(user);
			
			tx.commit();

			return id;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

	@Override
	public void update(User user) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		
		try {
			session.update(user);
			
			tx.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsersByCompany(Company defaultCompany) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("company", defaultCompany));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<User>(criteria.list()); 
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public User findUserCompanyByProxy(long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(User.class);
		User user = (User) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(user.getCompany());
			
			return user;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
}
