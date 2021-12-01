package com.pyramix.swi.persistence.settlement.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;

public class SettlementHibernate extends DaoHibernate implements SettlementDao {

	@Override
	public Settlement findSettlementById(long id) throws Exception {
		
		return (Settlement) super.findById(Settlement.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Settlement> findAllSettlement() throws Exception {
		
		return new ArrayList<Settlement>(super.findAll(Settlement.class));
	}

	@Override
	public long save(Settlement settlement) throws Exception {
		
		return super.save(settlement);
	}

	@Override
	public void update(Settlement settlement) throws Exception {
		
		super.update(settlement);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Settlement> findAllSettlement_OrderBy_SettlementDate(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		criteria.addOrder(desc ? Order.desc("settlementDate") : Order.asc("settlementDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<Settlement>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Settlement> findSettlement_By_SettlementDate(boolean desc, Date startDate, Date endDate)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		criteria.add(Restrictions.between("settlementDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("settlementDate") : Order.asc("settlementDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<Settlement>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}
	
	
	@Override
	public Settlement findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		// 		VoucherPayment payment = (VoucherPayment) criteria.add(Restrictions.idEq(id)).uniqueResult();
		Settlement settlement = (Settlement) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlement.getCustomer());
			
			return settlement;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@Override
	public Settlement findVoucherGiroReceiptByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		Settlement settlement = (Settlement) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlement.getVoucherGiroReceipt());
			
			return settlement;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SettlementDetail> findSettlementDetail_By_CustomerOrderId(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(SettlementDetail.class);
		criteria.createAlias("customerOrder", "customerOrder");
		criteria.add(Restrictions.eq("customerOrder.id", id));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<SettlementDetail>(criteria.list());

		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@Override
	public SettlementDetail getSettlementByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(SettlementDetail.class);
		SettlementDetail settlementDetail = (SettlementDetail) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlementDetail.getSettlement());
			
			return settlementDetail;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@Override
	public Settlement findVoucherPaymentByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		Settlement settlement = (Settlement) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlement.getVoucherPayment());
			
			return settlement;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Settlement getSettlementUserCreateByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		Settlement settlement = (Settlement) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlement.getUserCreate());
			
			return settlement;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Settlement findCustomerReceivableByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Settlement.class);
		Settlement settlement = (Settlement) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlement.getCustomerReceivable());
			
			return settlement;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	@Override
	public SettlementDetail findSettlementFromSettlementDetailByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(SettlementDetail.class);
		SettlementDetail settlementDetail = (SettlementDetail) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(settlementDetail.getSettlement());
			
			return settlementDetail;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
}
