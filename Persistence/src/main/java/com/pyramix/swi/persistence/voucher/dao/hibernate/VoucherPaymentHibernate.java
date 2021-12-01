package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;

public class VoucherPaymentHibernate extends DaoHibernate implements VoucherPaymentDao {

	@Override
	public VoucherPayment findVoucherPaymentById(Long id) throws Exception {

		return (VoucherPayment) super.findById(VoucherPayment.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherPayment> findAllVoucherPayment() throws Exception {

		return new ArrayList<VoucherPayment>(super.findAll(VoucherPayment.class));
	}

	@Override
	public Long save(VoucherPayment voucherPayment) throws Exception {
		
		return super.save(voucherPayment);
	}

	@Override
	public void update(VoucherPayment voucherPayment) throws Exception {
		
		super.update(voucherPayment);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherPayment> findAllVoucherPayment_OrderBy_TransactionDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherPayment>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherPayment> findAllVoucherPayment_By_TransactionDate(Date startDate, Date endDate, boolean desc)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		criteria.add(Restrictions.between("transactionDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherPayment>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}	
	
	@Override
	public VoucherPayment findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(payment.getCustomer());
			
			return payment;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public VoucherPayment findGiroByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(payment.getGiro());
			
			return payment;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public VoucherPayment findSettlementByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(payment.getSettlement());
			
			return payment;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public VoucherPayment findPostingVoucherNumberByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(payment.getPostingVoucherNumber());
			
			return payment;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}

	@Override
	public VoucherPayment findUserCreateByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherPayment.class);
		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(payment.getUserCreate());
			
			return payment;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
}
