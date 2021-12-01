package com.pyramix.swi.persistence.inventory.process.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
// import com.pyramix.swi.domain.inventory.process.completed.InventoryProcessCompleted;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;

public class InventoryProcessHibernate extends DaoHibernate implements InventoryProcessDao {

	@Override
	public InventoryProcess findInventoryProcessById(long id) throws Exception {
		
		return (InventoryProcess) super.findById(InventoryProcess.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryProcess> findAllInventoryProcess() throws Exception {

		return new ArrayList<InventoryProcess>(super.findAll(InventoryProcess.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryProcess> findAllInventoryProcess(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcess.class);
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {

			return new ArrayList<InventoryProcess>(criteria.list());
	
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryProcess> findAllInventoryProcess_By_DateRange(boolean desc, Date startDate, Date endDate) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcess.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<InventoryProcess>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}		
	}
	
	@Override
	public Long save(InventoryProcess inventoryProcess) throws Exception {
		
		return super.save(inventoryProcess);
	}

	@Override
	public void update(InventoryProcess inventoryProcess) throws Exception {
		
		super.update(inventoryProcess);
	}

	@Override
	public InventoryProcess getProcessedByCoByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcess.class);
		InventoryProcess inventoryProcess = 
				(InventoryProcess) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcess.getProcessedByCo());
			
			return inventoryProcess;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();	
		}
	}

	@Override
	public InventoryProcess getProcessMaterialsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcess.class);
		InventoryProcess inventoryProcess = 
				(InventoryProcess) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcess.getProcessMaterials());
			
			return inventoryProcess;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();	
		}
	}

	@Override
	public InventoryProcess getInventoryRefByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcess.class);
		InventoryProcess inventoryProcess = 
				(InventoryProcess) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcess.getInventoryList());
			
			return inventoryProcess;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();	
		}
	}

	@Override
	public InventoryProcessMaterial getProcessProductsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryProcessMaterial.class);
		InventoryProcessMaterial inventoryProcessMaterial = 
				(InventoryProcessMaterial) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcessMaterial.getProcessProducts());
			
			return inventoryProcessMaterial;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();	
		}
	}

	@Override
	public InventoryProcessProduct getProcessProductsCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryProcessProduct.class);
		InventoryProcessProduct inventoryProcessProduct =
				(InventoryProcessProduct) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcessProduct.getCustomer());
			
			return inventoryProcessProduct;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public InventoryProcessMaterial getInventoryProcessByProxy(long id) {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryProcessMaterial.class);
		InventoryProcessMaterial inventoryProcessMaterial = 
				(InventoryProcessMaterial) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryProcessMaterial.getInventoryProcess());
			
			return inventoryProcessMaterial;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
}
