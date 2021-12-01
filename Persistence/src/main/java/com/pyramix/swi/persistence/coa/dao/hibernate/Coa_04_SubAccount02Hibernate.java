package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_04_SubAccount02Hibernate extends DaoHibernate implements Coa_04_SubAccount02Dao {

	@Override
	public Coa_04_SubAccount02 findCoa_04_SubAccount02ById(long id) throws Exception {

		return (Coa_04_SubAccount02) super.findById(Coa_04_SubAccount02.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_04_SubAccount02> findAllCoa_04_SubAccount02() throws Exception {

		return new ArrayList<Coa_04_SubAccount02>(super.findAll(Coa_04_SubAccount02.class));
	}

	@Override
	public long save(Coa_04_SubAccount02 subAccount02) throws Exception {
		
		return super.save(subAccount02);
	}

	@Override
	public void update(Coa_04_SubAccount02 subAccount02) throws Exception {

		super.update(subAccount02);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_04_SubAccount02> findCoa_04_SubAccount02BySubAccount01(Coa_03_SubAccount01 subAccount01)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_04_SubAccount02.class);
		criteria.add(Restrictions.eq("subAccount01", subAccount01));
		criteria.addOrder(Order.asc("subAccount02Number"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Coa_04_SubAccount02>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Coa_04_SubAccount02 findAccountMastersByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_04_SubAccount02.class);
		Coa_04_SubAccount02 subAcc02 = 
				(Coa_04_SubAccount02) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(subAcc02.getMasters());
			
			return subAcc02;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_04_SubAccount02> findCoa_04_SubAccount02_By_AccountType(int selAccountType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_04_SubAccount02.class);
		if (selAccountType>0) {
			criteria.add(Restrictions.eq("typeCoaNumber", selAccountType));			
		}
		criteria.addOrder(Order.asc("typeCoaNumber"));
		criteria.addOrder(Order.asc("groupCoaNumber"));
		criteria.addOrder(Order.asc("subaccount01CoaNumber"));
		criteria.addOrder(Order.asc("subAccount02Number"));
		
		try {
			
			return new ArrayList<Coa_04_SubAccount02>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Coa_04_SubAccount02 findCoa_03_SubAccount01_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_04_SubAccount02.class);
		Coa_04_SubAccount02 subAccount02 = (Coa_04_SubAccount02) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(subAccount02.getSubAccount01());
			
			return subAccount02;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
