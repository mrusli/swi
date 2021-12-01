package com.pyramix.swi.persistence.gl.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.EndingBalance;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.gl.dao.EndingBalanceDao;

public class EndingBalanceHibernate extends DaoHibernate implements EndingBalanceDao {

	@Override
	public EndingBalance findEndingBalanceById(long id) throws Exception {

		return (EndingBalance) super.findById(EndingBalance.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EndingBalance> findAllEndingBalance() throws Exception {
		
		return new ArrayList<EndingBalance>(super.findAll(EndingBalance.class));
	}

	@Override
	public EndingBalance findEndingBalanceByMasterCoa_EndingDate(Coa_05_Master masterCoa, Date endingDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EndingBalance.class);
		criteria.add(Restrictions.eq("masterCoa", masterCoa));
		criteria.add(Restrictions.eq("endingDate", endingDate));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
		
			return (EndingBalance) criteria.uniqueResult();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> findUniqueCoaMasterEndingBalance() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EndingBalance.class);
		criteria.setProjection(Projections.distinct(Projections.property("masterCoa")));
		criteria.createAlias("masterCoa", "masterCoa");
		// criteria.addOrder(Order.asc("masterCoa.masterCoaComp"));
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
	public List<EndingBalance> findAllEndingBalance_By_CoaMaster(Coa_05_Master coaMaster) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EndingBalance.class);
		criteria.add(Restrictions.eq("masterCoa", coaMaster));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<EndingBalance>(criteria.list());
		
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EndingBalance> findAllEndingBalance_OrderBy_CoaMaster_EndingDate() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(EndingBalance.class);
		criteria.createAlias("masterCoa", "masterCoa");
		criteria.addOrder(Order.asc("masterCoa.masterCoaComp"));
		criteria.addOrder(Order.desc("endingDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<EndingBalance>(criteria.list());

		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Long save(EndingBalance endingBalance) throws Exception {

		return super.save(endingBalance);
	}

	@Override
	public void update(EndingBalance endingBalance) throws Exception {
		
		super.update(endingBalance);
	}
}
