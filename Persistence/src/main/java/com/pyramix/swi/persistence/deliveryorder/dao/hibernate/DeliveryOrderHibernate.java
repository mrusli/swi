package com.pyramix.swi.persistence.deliveryorder.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;

public class DeliveryOrderHibernate extends DaoHibernate implements DeliveryOrderDao {

	@Override
	public DeliveryOrder findDeliveryOrderById(Long id) throws Exception {

		return (DeliveryOrder) super.findById(DeliveryOrder.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrder> findAllDeliveryOrder() throws Exception {
		
		return new ArrayList<DeliveryOrder>(super.findAll(DeliveryOrder.class));
	}

	@Override
	public Long save(DeliveryOrder deliveryOrder) throws Exception {
		
		return super.save(deliveryOrder);
	}

	@Override
	public void update(DeliveryOrder deliveryOrder) throws Exception {
		
		super.update(deliveryOrder);
	}

	@Override
	public DeliveryOrder findDeliveryOrderProductsByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		DeliveryOrder deliveryOrder = (DeliveryOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(deliveryOrder.getDeliveryOrderProducts());
			
			return deliveryOrder;	
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public DeliveryOrderProduct findInventoryCodeByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(DeliveryOrderProduct.class);
		DeliveryOrderProduct deliveryOrderProduct = 
				(DeliveryOrderProduct) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(deliveryOrderProduct.getInventoryCode());
			
			return deliveryOrderProduct;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@Override
	public DeliveryOrder findCompanyByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		DeliveryOrder deliveryOrder = 
				(DeliveryOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(deliveryOrder.getCompany());
			
			return deliveryOrder;	
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrder> findAllDeliveryOrder_OrderBy_DeliveryOrderDate(boolean desc, long locationId) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		criteria.addOrder(desc ? Order.desc("deliveryOrderDate") : Order.asc("deliveryOrderDate"));
		if (Long.compare(locationId, 0L)>0) {
			// with location criteria matching the company id
			criteria.add(Restrictions.eq("company.id", locationId));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<DeliveryOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrder> findAllDeliveryOrder_By_DeliveryOrderDate(Date startDate, Date endDate, boolean desc, long locationId) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		criteria.add(Restrictions.between("deliveryOrderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("deliveryOrderDate") : Order.asc("deliveryOrderDate"));
		if (Long.compare(locationId, 0L)>0) {
			// with location criteria matching the company id
			criteria.add(Restrictions.eq("company.id", locationId));
		}		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<DeliveryOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public DeliveryOrder findSuratJalanByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		DeliveryOrder deliveryOrder = (DeliveryOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			
			Hibernate.initialize(deliveryOrder.getSuratJalan());
			
			return deliveryOrder;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public DeliveryOrder findUserCreateByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(DeliveryOrder.class);
		DeliveryOrder deliveryOrder = (DeliveryOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			
			Hibernate.initialize(deliveryOrder.getUserCreate());
			
			return deliveryOrder;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
}
