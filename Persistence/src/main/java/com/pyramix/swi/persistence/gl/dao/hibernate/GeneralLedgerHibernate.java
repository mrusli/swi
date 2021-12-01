package com.pyramix.swi.persistence.gl.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.gl.dao.GeneralLedgerDao;

public class GeneralLedgerHibernate extends DaoHibernate implements GeneralLedgerDao {

	@Override
	public GeneralLedger findGeneralLedgerById(long id) throws Exception {
		
		return (GeneralLedger) super.findById(GeneralLedger.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralLedger> findAllGeneralLedger() throws Exception {
		
		return new ArrayList<GeneralLedger>(super.findAll(GeneralLedger.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralLedger> findAllGeneralLedgerByMasterCoa_PostingDate(Coa_05_Master masterCoa, Date postingDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(GeneralLedger.class);
		criteria.add(Restrictions.eq("masterCoa", masterCoa));
		criteria.add(Restrictions.eq("postingDate", postingDate));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<GeneralLedger>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Long save(GeneralLedger generalLedger) throws Exception {
		
		return super.save(generalLedger);
	}

	@Override
	public void update(GeneralLedger generalLedger) throws Exception {

		super.update(generalLedger);
	}

}
