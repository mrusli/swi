package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_05_MasterHibernate extends DaoHibernate implements Coa_05_MasterDao {

	@Override
	public Coa_05_Master findCoa_05_MasterById(long id) throws Exception {

		return (Coa_05_Master) super.findById(Coa_05_Master.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> findAllCoa_05_Master() throws Exception {

		return new ArrayList<Coa_05_Master>(super.findAll(Coa_05_Master.class));
	}

	@Override
	public long save(Coa_05_Master master) throws Exception {
		
		return super.save(master);
	}

	@Override
	public void update(Coa_05_Master master) throws Exception {

		super.update(master);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> findCoa_05_MasterBySubAccount02(Coa_04_SubAccount02 subAccount02) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_05_Master.class);
		criteria.add(Restrictions.eq("subAccount02", subAccount02));
		criteria.addOrder(Order.asc("masterCoaNumber"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Coa_05_Master>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_by_AccountType(int coaAccountType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_05_Master.class);
		criteria.add(Restrictions.eq("typeCoaNumber", coaAccountType));
		criteria.add(Restrictions.eq("active", true));
		criteria.addOrder(Order.asc("masterCoaComp"));
		// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Coa_05_Master>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_05_Master.class);
		criteria.add(Restrictions.eq("active", true));
		criteria.addOrder(Order.asc("masterCoaComp"));
		
		try {
			
			return new ArrayList<Coa_05_Master>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> findCoa_05_Master_By_AccountType(int accountTypeNo) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_05_Master.class);
		if (accountTypeNo>0) {
			criteria.add(Restrictions.eq("typeCoaNumber", accountTypeNo));			
		}
		criteria.addOrder(Order.asc("masterCoaComp"));
		
		try {
			
			return new ArrayList<Coa_05_Master>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}

	}

	@Override
	public Coa_05_Master findCoa_04_SubAccount02_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(Coa_05_Master.class);
		Coa_05_Master accountMaster = (Coa_05_Master) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(accountMaster.getSubAccount02());
			
			return accountMaster;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
}
