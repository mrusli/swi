package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_Receivables;
import com.pyramix.swi.persistence.coa.dao.Coa_ReceivablesDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_ReceivablesHibernate extends DaoHibernate implements Coa_ReceivablesDao {

	@Override
	public Coa_Receivables findCoaReceivablesById(long id) throws Exception {

		return (Coa_Receivables) super.findById(Coa_Receivables.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Receivables> findAllCoaReceivables() throws Exception {

		return new ArrayList<Coa_Receivables>(super.findAll(Coa_Receivables.class));
	}

	@Override
	public long save(Coa_Receivables coaReceivables) throws Exception {

		return super.save(coaReceivables);
	}

	@Override
	public void update(Coa_Receivables coaReceivables) throws Exception {

		super.update(coaReceivables);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Receivables> findAllActiveCoaReceivables() throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(Coa_Receivables.class);
		criteria.add(Restrictions.eq("active", true));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<Coa_Receivables>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

}
