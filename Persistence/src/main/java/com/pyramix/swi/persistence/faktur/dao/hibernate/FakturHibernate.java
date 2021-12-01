package com.pyramix.swi.persistence.faktur.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;

public class FakturHibernate extends DaoHibernate implements FakturDao {

	@Override
	public Faktur findFakturById(long id) throws Exception {

		return (Faktur) super.findById(Faktur.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Faktur> findAllFaktur() throws Exception {
		
		return new ArrayList<Faktur>(super.findAll(Faktur.class));
	}

	@Override
	public Long save(Faktur faktur) throws Exception {
		
		return super.save(faktur);
	}

	@Override
	public void update(Faktur faktur) throws Exception {

		super.update(faktur);
	}

	@Override
	public Faktur findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Faktur.class);
		Faktur faktur = 
				(Faktur) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(faktur.getCustomer());
			
			return faktur;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Faktur> findAllFaktur_OrderBy_FakturDate(boolean desc) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Faktur.class);
		criteria.addOrder(desc ? Order.desc("fakturDate") : Order.asc("fakturDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<Faktur>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Faktur> findAllFaktur_OrderBy_FakturDate(Date startDate, Date endDate, boolean desc) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Faktur.class);
		criteria.addOrder(desc ? Order.desc("fakturDate") : Order.asc("fakturDate"));
		criteria.add(Restrictions.between("fakturDate", startDate, endDate));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Faktur>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

	@Override
	public Faktur findUserByProxy(long id) {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(Faktur.class);
		Faktur faktur = 
				(Faktur) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(faktur.getUserCreate());
			
			return faktur;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

	@Override
	public Faktur findSuratJalanByProxy(long id) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Faktur.class);
		Faktur faktur = 
				(Faktur) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(faktur.getSuratJalan());
			
			return faktur;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

}
