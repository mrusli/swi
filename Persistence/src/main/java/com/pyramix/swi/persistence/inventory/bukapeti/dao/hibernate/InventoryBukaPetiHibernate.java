package com.pyramix.swi.persistence.inventory.bukapeti.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;

public class InventoryBukaPetiHibernate extends DaoHibernate implements InventoryBukaPetiDao {

	@Override
	public InventoryBukaPeti findInventoryBukapetiById(long id) throws Exception {
		
		return (InventoryBukaPeti) super.findById(InventoryBukaPeti.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryBukaPeti> findAllInventoryBukapeti() throws Exception {
		
		return new ArrayList<InventoryBukaPeti>(super.findAll(InventoryBukaPeti.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryBukaPeti> findAllInventoryBukapeti(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryBukaPeti.class);
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<InventoryBukaPeti>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryBukaPeti> findAllInventoryBukapeti_By_DateRange(boolean desc, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPeti.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<InventoryBukaPeti>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
	
	@Override
	public Long save(InventoryBukaPeti inventoryBukapeti) throws Exception {
		
		return super.save(inventoryBukapeti);
	}

	@Override
	public void update(InventoryBukaPeti inventoryBukapeti) throws Exception {
		
		super.update(inventoryBukapeti);
	}

	@Override
	public InventoryBukaPeti getBukapetiMaterialsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPeti.class);
		InventoryBukaPeti inventoryBukapeti = 
				(InventoryBukaPeti) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryBukapeti.getBukapetiMaterialList());
			
			return inventoryBukapeti;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public InventoryBukaPeti getInventoryRefByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPeti.class);
		InventoryBukaPeti inventoryBukapeti = 
				(InventoryBukaPeti) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryBukapeti.getInventoryList());
			
			return inventoryBukapeti;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public InventoryBukaPeti getBukaPetiEndProductByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPeti.class);
		InventoryBukaPeti inventoryBukapeti = 
				(InventoryBukaPeti) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryBukapeti.getBukapetiEndProduct());
			
			return inventoryBukapeti;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}	
	
	@Override
	public InventoryBukaPetiMaterial getBukapetiProductsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPetiMaterial.class);
		InventoryBukaPetiMaterial inventoryBukapetiMaterial = 
				(InventoryBukaPetiMaterial) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryBukapetiMaterial.getBukapetiProductList());
			
			return inventoryBukapetiMaterial;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public InventoryBukaPetiMaterial getInventoryBukapetiByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(InventoryBukaPetiMaterial.class);
		InventoryBukaPetiMaterial inventoryBukapetiMaterial = 
				(InventoryBukaPetiMaterial) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(inventoryBukapetiMaterial.getInventoryBukapeti());
			
			return inventoryBukapetiMaterial;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
}
