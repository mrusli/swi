package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;

public class VoucherGiroReceiptHibernate extends DaoHibernate implements VoucherGiroReceiptDao {

	@Override
	public VoucherGiroReceipt findVoucherGiroReceiptById(Long id) throws Exception {

		return (VoucherGiroReceipt) super.findById(VoucherGiroReceipt.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt() throws Exception {
		
		return new ArrayList<VoucherGiroReceipt>(super.findAll(VoucherGiroReceipt.class));
	}

	@Override
	public Long save(VoucherGiroReceipt voucherGiroReceipt) throws Exception {
		
		return super.save(voucherGiroReceipt);
	}

	@Override
	public void update(VoucherGiroReceipt voucherGiroReceipt) throws Exception {
		
		super.update(voucherGiroReceipt);
	}

	@Override
	public VoucherGiroReceipt findCustomerByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		
		VoucherGiroReceipt receipt = 
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(receipt.getCustomer());
			
			return receipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_OrderBy_TransactionDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherGiroReceipt>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_By_TransactionDate(Date startDate, Date endDate,
			boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		criteria.add(Restrictions.between("transactionDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherGiroReceipt>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherGiroReceipt> findAllVoucherGiroReceipt_By_GiroDate(Date startDate, Date endDate, boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		criteria.add(Restrictions.between("giroDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("giroDate") : Order.asc("giroDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}

	
/*	@Override
	public VoucherGiroReceipt findVoucherPaymentByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		VoucherGiroReceipt voucherGiroReceipt = 
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherGiroReceipt.getVoucherPayment());
			
			return voucherGiroReceipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}*/

	@Override
	public VoucherGiroReceipt findGiroByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		VoucherGiroReceipt voucherGiroReceipt =
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherGiroReceipt.getGiro());
			
			return voucherGiroReceipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@Override
	public VoucherGiroReceipt findUserCreateByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		VoucherGiroReceipt voucherGiroReceipt =
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherGiroReceipt.getUserCreate());
			
			return voucherGiroReceipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public VoucherGiroReceipt findPostingVoucherNumberByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		VoucherGiroReceipt voucherGiroReceipt =
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherGiroReceipt.getPostingVoucherNumber());
			
			return voucherGiroReceipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}

	}

	@Override
	public VoucherGiroReceipt findGeneralLedgerByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherGiroReceipt.class);
		VoucherGiroReceipt voucherGiroReceipt =
				(VoucherGiroReceipt) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(voucherGiroReceipt.getGeneralLedgers());
			
			return voucherGiroReceipt;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}

	}
}
