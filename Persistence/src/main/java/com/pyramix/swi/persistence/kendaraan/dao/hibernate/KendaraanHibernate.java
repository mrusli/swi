package com.pyramix.swi.persistence.kendaraan.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanDao;

public class KendaraanHibernate extends DaoHibernate implements KendaraanDao {

	@Override
	public Kendaraan findKendaraanById(long id) throws Exception {
		
		return (Kendaraan) super.findById(Kendaraan.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kendaraan> findAllKendaraan() throws Exception {
		
		return new ArrayList<Kendaraan>(super.findAll(Kendaraan.class));
	}

	
	
	@Override
	public Long save(Kendaraan kendaraan) throws Exception {
		
		return super.save(kendaraan);
	}

	@Override
	public void update(Kendaraan kendaraan) throws Exception {
		
		super.update(kendaraan);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Kendaraan> findAllActiveKendaraan() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kendaraan.class);
		criteria.add(Restrictions.eq("active", true));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
		
			return new ArrayList<Kendaraan>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Kendaraan findEmployeeKendaraanByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Kendaraan.class);
		Kendaraan kendaraan = (Kendaraan) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(kendaraan.getEmployee());
			
			return kendaraan;

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

}
