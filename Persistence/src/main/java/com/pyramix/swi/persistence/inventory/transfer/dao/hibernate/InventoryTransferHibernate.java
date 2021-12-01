package com.pyramix.swi.persistence.inventory.transfer.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.transfer.dao.InventoryTransferDao;

public class InventoryTransferHibernate extends DaoHibernate implements InventoryTransferDao {

	@Override
	public InventoryTransfer findInventoryTransferById(long id) throws Exception {
		
		return (InventoryTransfer) super.findById(InventoryTransfer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryTransfer> findAllInventoryTransfer() throws Exception {
		
		return new ArrayList<InventoryTransfer>(super.findAll(InventoryTransfer.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryTransfer> findAllInventoryTransfer(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryTransfer.class);
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {

			return new ArrayList<InventoryTransfer>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryTransfer> findAllInventoryTransfer_By_DateRange(boolean desc, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryTransfer.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<InventoryTransfer>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}	
	
	@Override
	public Long save(InventoryTransfer inventoryTransfer) throws Exception {
		
		return super.save(inventoryTransfer);
	}

	@Override
	public void update(InventoryTransfer inventoryTransfer) throws Exception {
		
		super.update(inventoryTransfer);
	}

	@Override
	public InventoryTransfer getInventoryTransferMaterialByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryTransfer.class);
		InventoryTransfer inventoryTransfer = 
				(InventoryTransfer) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryTransfer.getTransferMaterialList());
			
			return inventoryTransfer;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@Override
	public InventoryTransfer getInventoryTransferEndProductByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryTransfer.class);
		InventoryTransfer inventoryTransfer = 
				(InventoryTransfer) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryTransfer.getTransferEndProductList());
			
			return inventoryTransfer;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public InventoryTransfer getInventoryTransferFromCoByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryTransfer.class);
		InventoryTransfer inventoryTransfer = 
				(InventoryTransfer) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryTransfer.getTransferFromCo());
			
			return inventoryTransfer;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

}
