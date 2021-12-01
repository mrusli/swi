package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;

public class GiroHibernate extends DaoHibernate implements GiroDao {

	@Override
	public Giro findGiroById(Long id) throws Exception {

		return (Giro) super.findById(Giro.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Giro> findAllGiro() throws Exception {

		return new ArrayList<Giro>(super.findAll(Giro.class));
	}

	@Override
	public Long save(Giro giro) throws Exception {
		
		return super.save(giro);
	}

	@Override
	public void update(Giro giro) throws Exception {

		super.update(giro);
	}

	@Override
	public Giro findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		Giro giro = (Giro) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(giro.getCustomer());
			
			return giro;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@Override
	public Giro findVoucherGiroReceiptByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		Giro giro = (Giro) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(giro.getVoucherGiroReceipt());
			
			return giro;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	

	@Override
	public Giro findVoucherPaymentByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		Giro giro = (Giro) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(giro.getVoucherPayment());
			
			return giro;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Giro> findAllGiro_OrderBy_ReceivedDate(boolean desc, boolean paidStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		criteria.add(Restrictions.eq("paid", paidStatus));
		criteria.addOrder(desc ? Order.desc("giroReceivedDate") : Order.asc("giroReceivedDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Giro>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Giro> findAllGiro_OrderBy_GiroDueDate(boolean desc, boolean paidStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		criteria.add(Restrictions.eq("paid", paidStatus));
		criteria.addOrder(desc ? Order.desc("giroDate") : Order.asc("giroDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Giro>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Giro> findAllGiro_OrderBy_PaidGiroDate(boolean desc, boolean paidStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		criteria.add(Restrictions.eq("paid", paidStatus));
		criteria.addOrder(desc ? Order.desc("paidGiroDate") : Order.asc("paidGiroDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Giro>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Giro> findGiro_By_DueGiroDate(Date startDate, Date endDate, boolean paidStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Giro.class);
		criteria.add(Restrictions.eq("paid", paidStatus));
		criteria.add(Restrictions.between("giroDate", startDate, endDate));
		criteria.addOrder(Order.desc("giroDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Giro>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}
}
