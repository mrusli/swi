package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.persistence.coa.dao.Coa_AdjustmentDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_AdjustmentHibernate extends DaoHibernate implements Coa_AdjustmentDao {

	@Override
	public Coa_Adjustment findCoaAdjustmentById(long id) throws Exception {
		
		return (Coa_Adjustment) super.findById(Coa_Adjustment.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Adjustment> findAllCoaAdjustment() throws Exception {
		
		return new ArrayList<Coa_Adjustment>(super.findAll(Coa_Adjustment.class));
	}

	@Override
	public long save(Coa_Adjustment coaAdjustment) throws Exception {
		
		return super.save(coaAdjustment);
	}

	@Override
	public void update(Coa_Adjustment coaAdjustment) throws Exception {

		super.update(coaAdjustment);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Adjustment> findCoaAdjustmentByDebitAccount(boolean debitAccount) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_Adjustment.class);
		criteria.add(Restrictions.eq("debitAccount", debitAccount));
		criteria.add(Restrictions.eq("active", true));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Coa_Adjustment>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

}
