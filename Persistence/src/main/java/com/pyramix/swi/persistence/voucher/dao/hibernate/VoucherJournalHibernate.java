package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.VoucherJournalDao;

public class VoucherJournalHibernate extends DaoHibernate implements VoucherJournalDao {

	@Override
	public VoucherJournal findVoucherJournalById(long id) throws Exception {
		
		return (VoucherJournal) super.findById(VoucherJournal.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherJournal> findAllVoucherJournal() throws Exception {
		
		return new ArrayList<VoucherJournal>(super.findAll(VoucherJournal.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherJournal> findAllVoucherJournal_OrderBy_TransactionDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(VoucherJournal.class);
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<VoucherJournal>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherJournal> findAllVoucherJournal_OrderBy_TransactionDate(Date startDate, Date endDate, boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherJournal.class);
		criteria.add(Restrictions.between("transactionDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherJournal>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Long save(VoucherJournal voucherJOurnal) throws Exception {

		return super.save(voucherJOurnal);
	}

	@Override
	public void update(VoucherJournal voucherJournal) throws Exception {
		
		super.update(voucherJournal);
	}

	@Override
	public VoucherJournal getVoucherJournalUserCreateByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherJournal.class);
		VoucherJournal voucherJournalByProxy = (VoucherJournal) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherJournalByProxy.getUserCreate());
			
			return voucherJournalByProxy;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}
	
	public VoucherJournal findVoucherJournalGeneralLedgerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherJournal.class);
		VoucherJournal voucherJournalByProxy = (VoucherJournal) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherJournalByProxy.getGeneralLedgers());
			
			return voucherJournalByProxy;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

}
