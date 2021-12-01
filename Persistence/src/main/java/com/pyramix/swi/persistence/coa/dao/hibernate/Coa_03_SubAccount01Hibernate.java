package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_03_SubAccount01Hibernate extends DaoHibernate implements Coa_03_SubAccount01Dao {

	@Override
	public Coa_03_SubAccount01 findCoa_03_SubAccount01ById(long id) throws Exception {
		
		return (Coa_03_SubAccount01) super.findById(Coa_03_SubAccount01.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_03_SubAccount01> findAllCoa_03SubAccount01() throws Exception {
		
		return new ArrayList<Coa_03_SubAccount01>(super.findAll(Coa_03_SubAccount01.class));
	}

	@Override
	public long save(Coa_03_SubAccount01 subAccount01) throws Exception {
		
		return super.save(subAccount01);
	}

	@Override
	public void update(Coa_03_SubAccount01 subAccount01) throws Exception {

		super.update(subAccount01);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_03_SubAccount01> findCoa_03_SubAccount01ByAccountGroup(Coa_02_AccountGroup accountGroup)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_03_SubAccount01.class);
		criteria.add(Restrictions.eq("accountGroup", accountGroup));
		criteria.addOrder(Order.asc("subAccount01Number"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Coa_03_SubAccount01>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_03_SubAccount01> findCoa_03_SubAccount01_By_AccountType(int selAccountType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_03_SubAccount01.class);
		if (selAccountType>0) {
			criteria.add(Restrictions.eq("typeCoaNumber", selAccountType));
		}
		criteria.addOrder(Order.asc("typeCoaNumber"));
		criteria.addOrder(Order.asc("groupCoaNumber"));
		criteria.addOrder(Order.asc("subAccount01Number"));
		
		try {
			
			return new ArrayList<Coa_03_SubAccount01>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Coa_03_SubAccount01 findCoa_02_AccountGroup_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_03_SubAccount01.class);
		Coa_03_SubAccount01 subAccount01 = (Coa_03_SubAccount01) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(subAccount01.getAccountGroup());
			
			return subAccount01;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
