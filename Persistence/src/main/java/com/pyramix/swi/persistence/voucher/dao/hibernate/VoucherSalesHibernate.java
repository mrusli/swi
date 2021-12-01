package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;

public class VoucherSalesHibernate extends DaoHibernate implements VoucherSalesDao {

	@Override
	public VoucherSales findVoucherById(Long id) throws Exception {
		
		return (VoucherSales) super.findById(VoucherSales.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSales> findAllVoucher() throws Exception {
		
		return new ArrayList<VoucherSales>(super.findAll(VoucherSales.class));
	}

	@Override
	public Long save(VoucherSales voucherSales) throws Exception {

		return super.save(voucherSales);
	}

	@Override
	public void update(VoucherSales voucherSales) throws Exception {

		super.update(voucherSales);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSales> findAllVoucher_OrderBy_TransactionDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<VoucherSales>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSales> findVoucher_By_TransactionDate(Date startDate, Date endDate, boolean desc)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		criteria.add(Restrictions.between("transactionDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("transactionDate") : Order.asc("transactionDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<VoucherSales>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}	
	
	@Override
	public VoucherSales findCustomerByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		VoucherSales voucherSales = (VoucherSales) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
		
			Hibernate.initialize(voucherSales.getCustomer());
			
			return voucherSales;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
		
	}

	@Override
	public VoucherSales findCustomerOrderByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		VoucherSales voucherSales = (VoucherSales) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
		
			Hibernate.initialize(voucherSales.getCustomerOrder());
			
			return voucherSales;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
		
	}	

	@Override
	public VoucherSales findGeneralLedgerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		VoucherSales voucherSales = (VoucherSales) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
		
			Hibernate.initialize(voucherSales.getGeneralLedgers());
			
			return voucherSales;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSales> findAllVoucher_OrderBy_Customer(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSales.class);
		criteria.createAlias("customer", "customerName");
		criteria.addOrder(desc ? Order.desc("customerName.companyLegalName") : Order.asc("customerName.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<VoucherSales>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
}
