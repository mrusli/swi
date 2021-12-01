package com.pyramix.swi.persistence.suratjalan.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;

public class SuratJalanHibernate extends DaoHibernate implements SuratJalanDao {

	@Override
	public SuratJalan findSuratJalanById(long id) throws Exception {
		
		return (SuratJalan) super.findById(SuratJalan.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SuratJalan> findAllSuratJalan() throws Exception {
		
		return new ArrayList<SuratJalan>(super.findAll(SuratJalan.class));
	}

	@Override
	public long save(SuratJalan suratJalan) throws Exception {
		
		return super.save(suratJalan);
	}

	@Override
	public void update(SuratJalan suratJalan) throws Exception {
		
		super.update(suratJalan);
	}

	@Override
	public SuratJalan findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		SuratJalan suratJalan = (SuratJalan) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(suratJalan.getCustomer());
			
			return suratJalan;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
		
	}

	@Override
	public SuratJalan findEmployeeCommissionsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		SuratJalan suratJalan = (SuratJalan) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(suratJalan.getEmployeeCommissions());
			
			return suratJalan;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@Override
	public SuratJalan findDeliveryOrderByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		SuratJalan suratJalan = (SuratJalan) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(suratJalan.getDeliveryOrder());
			
			return suratJalan;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@Override
	public SuratJalan findFakturByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		SuratJalan suratJalan = (SuratJalan) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(suratJalan.getFaktur());
			
			return suratJalan;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SuratJalan> findAllSuratJalan_OrderBy_SuratJalanDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		criteria.addOrder(desc ? Order.desc("suratJalanDate") : Order.asc("suratJalanDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<SuratJalan>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SuratJalan> findAllSuratJalan_By_SuratJalanDate(Date startDate, Date endDate, boolean desc)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SuratJalan.class);
		criteria.add(Restrictions.between("suratJalanDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("suratJalanDate") : Order.asc("suratJalanDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<SuratJalan>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

}
