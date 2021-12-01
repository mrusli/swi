package com.pyramix.swi.persistence.inventory.dao.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;

public class InventoryHibernate extends DaoHibernate implements InventoryDao {
	
	private final Logger log = Logger.getLogger(InventoryHibernate.class);
	
	@Override
	public Inventory findInventoryById(Long id) throws Exception {
		
		return (Inventory) super.findById(Inventory.class, id);
	}

	@Override
	public void createIndexer() throws Exception {
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			
			fullTextSession.createIndexer().startAndWait();
			
		} catch (InterruptedException e) {			
			throw new Exception(e.getMessage());
			
		} finally {
			sessionFullText.close();
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> searchInventory(String searchString) throws Exception {
		Transaction tx = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Inventory.class).get();
			
			Query qry = qb.keyword()
					.onFields("inventoryCode.productCode", "thickness")
					.matching(searchString)
					.createQuery();
			
			// create fullTextQuery
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, Inventory.class);
			Sort sort = new Sort(
					SortField.FIELD_SCORE,
					new SortField("inventoryCode.productCode", SortField.Type.STRING, false),
					new SortField("sortThickness", SortField.Type.STRING, false));
					// new SortField("sortWidth", SortField.Type.STRING, false),
					// new SortField("sortMarking", SortField.Type.STRING, false));
			fullTextQuery.setSort(sort);
												
			// list
			return fullTextQuery.list();

		} catch (Exception e) {
			throw new Exception(e);			
		} finally {
			tx.commit();
			fullTextSession.close();			
		}
		
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> searchInventory(String searchString, InventoryPacking[] selectedPackingType) throws Exception {
		Transaction tx = null;
		Session sessionCriteria = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Inventory.class).get();
			
			Query qry = qb.keyword()
					.onFields("inventoryCode.productCode", "thickness")
					.matching(searchString)
					.createQuery();
			
			// create fullTextQuery
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, Inventory.class);
			Sort sort = new Sort(
					SortField.FIELD_SCORE,
					new SortField("inventoryCode.productCode", SortField.Type.STRING, false),
					new SortField("thickness", SortField.Type.STRING, false));
					// new SortField("sortWidth", SortField.Type.STRING, false));
					// new SortField("sortMarking", SortField.Type.STRING, false));
			fullTextQuery.setSort(sort);
			
			sessionCriteria = getSessionFactory().openSession();
			
			Criteria criteria = sessionCriteria.createCriteria(Inventory.class);
			// criteria.createAlias("inventoryCode", "inventory");
			criteria.add(Restrictions.eq("inventoryStatus", InventoryStatus.ready));
			criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
			criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));		
			
			if (selectedPackingType.length>0) {
				Disjunction or = Restrictions.disjunction();
				
				for (InventoryPacking inventoryPacking : selectedPackingType) {
					or.add(Restrictions.eq("inventoryPacking", inventoryPacking));
				}
				
				criteria.add(or);				
			}

			fullTextQuery.setCriteriaQuery(criteria);
			
			// list
			return fullTextQuery.list();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			sessionCriteria.close();
			tx.commit();
			fullTextSession.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> searchInventory(String searchString, InventoryPacking[] selectedPackingType, InventoryLocation[] selectedLocation) throws Exception {
		Transaction tx = null;
		Session sessionCriteria = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Inventory.class).get();
			
			Query qry = qb.keyword()
					.onFields("inventoryCode.productCode", "thickness")
					.matching(searchString)
					.createQuery();
			
			// create fullTextQuery
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, Inventory.class);
			Sort sort = new Sort(
					SortField.FIELD_SCORE,
					new SortField("inventoryCode.productCode", SortField.Type.STRING, false),
					new SortField("sortThickness", SortField.Type.STRING, false));
					// new SortField("sortWidth", SortField.Type.STRING, false),
					// new SortField("sortMarking", SortField.Type.STRING, false));
			fullTextQuery.setSort(sort);
									
			sessionCriteria = getSessionFactory().openSession();
			
			Criteria criteria = sessionCriteria.createCriteria(Inventory.class);
			criteria.add(Restrictions.eq("inventoryStatus", InventoryStatus.ready));
			criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
			criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));		
			
			// criteria.add(Restrictions.eq("inventoryPacking", packing));
			Disjunction orPacking = Restrictions.disjunction();
			for (InventoryPacking inventoryPacking : selectedPackingType) {
				orPacking.add(Restrictions.eq("inventoryPacking", inventoryPacking));
				
			}
			criteria.add(orPacking);
			
			Disjunction orLocation = Restrictions.disjunction();
			for (InventoryLocation inventoryLocation : selectedLocation) {
				orLocation.add(Restrictions.eq("inventoryLocation", inventoryLocation));
			}
			criteria.add(orLocation);
			
			fullTextQuery.setCriteriaQuery(criteria);
			
			// list
			return fullTextQuery.list();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			sessionCriteria.close();
			tx.commit();
			fullTextSession.close();
		}
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryByStatus(InventoryStatus inventoryStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eq("inventoryStatus", inventoryStatus));
		criteria.add(Restrictions.eq("inventoryStatus", InventoryStatus.ready));
		criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
		criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventory() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findInventoryByProductType(InventoryType inventoryType, InventoryStatus inventoryStatus) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.createAlias("inventoryCode", "code");
		criteria.add(Restrictions.eq("code.inventoryType", inventoryType));
		criteria.add(Restrictions.eq("inventoryStatus", inventoryStatus));
		criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
		criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));
		criteria.addOrder(Order.asc("code.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryCode> findAllInventoryCode() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryCode.class);
		criteria.addOrder(Order.asc("productCode"));
		
		try {
			
			return new ArrayList<InventoryCode>(criteria.list());
			
		} finally {
			session.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryCode> searchInventoryCode(String searchString) throws Exception {
		Transaction tx = null;
		Session sessionFullText = super.getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			tx = fullTextSession.beginTransaction();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(InventoryCode.class).get();
			
			Query qry = qb.keyword()
					.onField("productCode")
					.matching(searchString)
					.createQuery();
			
			// create fullTextQuery
			FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(qry, InventoryCode.class);

			// list
			return fullTextQuery.list();

		} catch (Exception e) {
			throw new Exception(e);

		} finally {
			tx.commit();
			sessionFullText.close();
		}
	}

	@Override
	public void save(List<Inventory> inventoryList) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		
		try {
			
			for (Inventory inventory : inventoryList) {
				session.save(inventory);
			}
			
			tx.commit();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
		
	}

	@Override
	public void update(Inventory updatedInventory) throws Exception {
		
		super.update(updatedInventory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryByPacking(InventoryPacking[] packingType) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
		criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));
		
		Disjunction or = Restrictions.disjunction();
		
		for (InventoryPacking inventoryPacking : packingType) {
			or.add(Restrictions.eq("inventoryPacking", inventoryPacking));
		}
		
		criteria.add(or);
		
		// order by
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryByPacking(InventoryStatus status, InventoryPacking[] packingType) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eq("inventoryStatus", status));
		criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
		criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));
		
		Disjunction or = Restrictions.disjunction();
		
		for (InventoryPacking inventoryPacking : packingType) {
			or.add(Restrictions.eq("inventoryPacking", inventoryPacking));
		}
		
		criteria.add(or);
		
		// order by
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryOfLembaranPacking() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eq("inventoryPacking", InventoryPacking.lembaran));
				
		// order by
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryOfLembaranPacking(InventoryStatus status) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eq("inventoryPacking", InventoryPacking.lembaran));
		criteria.add(Restrictions.eq("inventoryStatus", status));	
				
		// order by
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}		
	
	@Override
	public InventoryCode findInventoryCodeById(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryCode.class);
		
		try {
			
			return (InventoryCode) criteria.add(Restrictions.idEq(id)).uniqueResult();
			
		} finally {
			session.close();
		}
	}

	@Override
	public InventoryType findInventoryTypeById(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(InventoryType.class);

		try {
			
			return (InventoryType) criteria.add(Restrictions.idEq(id)).uniqueResult();
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryCode> findDistinctInventoryCode() throws Exception {
		Session session = super.getSessionFactory().openSession();

		// projections
		// https://stackoverflow.com/questions/12032748/hibernate-criteria-projection-distinct
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("inventoryCode")));
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.setProjection(Projections.distinct(projectionList));
		// criteria.createAlias("inventoryCode", "inventoryProduct");
		// criteria.addOrder(Order.asc("inventoryProduct.productCode"));
		
		try {
			
			return new ArrayList<InventoryCode>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryCode> findDistinctInventoryCode(InventoryPacking packing) throws Exception {
		Session session = super.getSessionFactory().openSession();

		// projections
		// https://stackoverflow.com/questions/12032748/hibernate-criteria-projection-distinct
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("inventoryCode")));
		
		Criteria criteria = session.createCriteria(Inventory.class);
		
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.add(Restrictions.eq("inventoryPacking", packing));
		// criteria.createAlias("inventoryCode", "inventory");
		// criteria.addOrder(Order.asc("inventory.productCode"));
		
		try {
			
			return new ArrayList<InventoryCode>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> findDistinctThickness() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("thickness")));
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.addOrder(Order.asc("thickness"));
		
		try {
			
			return new ArrayList<BigDecimal>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> findDistinctThickness(InventoryCode selectedCode) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("thickness")));
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.createAlias("inventoryCode", "inventoryCodeId");
		criteria.add(Restrictions.eq("inventoryCodeId.id", selectedCode.getId()));
		criteria.addOrder(Order.asc("thickness"));
		
		try {
			
			return new ArrayList<BigDecimal>(criteria.list());
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findAllInventoryByPackingAndLocation(InventoryStatus status,
			InventoryPacking[] packingType, InventoryLocation inventoryLocation) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.add(Restrictions.eq("inventoryStatus", status));
		criteria.add(Restrictions.eq("inventoryLocation", inventoryLocation));
		criteria.add(Restrictions.eqOrIsNull("inventoryProcess", null));
		criteria.add(Restrictions.eqOrIsNull("inventoryBukapeti", null));
		
		Disjunction or = Restrictions.disjunction();
		
		for (InventoryPacking inventoryPacking : packingType) {
			or.add(Restrictions.eq("inventoryPacking", inventoryPacking));
		}
		
		criteria.add(or);
		
		// order by
		criteria.createAlias("inventoryCode", "inventory");
		criteria.addOrder(Order.asc("inventory.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> filterInventory(InventoryStatus status, InventoryCode code, 
			BigDecimal thickness, InventoryLocation location) throws Exception {
		
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.createAlias("inventoryCode", "inventoryCodeId");
		// status
		if (status != null) {
			criteria.add(Restrictions.eq("inventoryStatus", status));			
		}
		// code
		if (code != null) {
			criteria.add(Restrictions.eq("inventoryCodeId.id", code.getId()));			
		}
		// thickness
		if (thickness != null) {
			criteria.add(Restrictions.eq("thickness", thickness));			
		}
		// location
		if (location != null) {
			criteria.add(Restrictions.eq("inventoryLocation", location));			
		}
		
		// order by
		criteria.addOrder(Order.asc("inventoryCodeId.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> filterInventory(InventoryPacking packing, InventoryStatus status, 
			InventoryCode code, BigDecimal thickness, InventoryLocation location) throws Exception {
		
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		criteria.createAlias("inventoryCode", "inventoryCodeId");
		criteria.add(Restrictions.eq("inventoryPacking", packing));
		// status
		if (status != null) {
			criteria.add(Restrictions.eq("inventoryStatus", status));			
		}
		// code
		if (code != null) {
			criteria.add(Restrictions.eq("inventoryCodeId.id", code.getId()));			
		}
		// thickness
		if (thickness != null) {
			criteria.add(Restrictions.eq("thickness", thickness));			
		}
		// location
		if (location != null) {
			criteria.add(Restrictions.eq("inventoryLocation", location));			
		}
		
		// order by
		criteria.addOrder(Order.asc("inventoryCodeId.productCode"));
		criteria.addOrder(Order.asc("thickness"));
		criteria.addOrder(Order.asc("width"));
		criteria.addOrder(Order.asc("length"));
		criteria.addOrder(Order.asc("marking"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<Inventory>(criteria.list());
			
		} finally {
			session.close();
		}		
	}
	
	@Override
	public Inventory getInventoryProcessByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		Inventory inventoryByProxy = (Inventory) criteria.add(Restrictions.idEq(id)).uniqueResult();
		try {
			Hibernate.initialize(inventoryByProxy.getInventoryProcess());
			
			return inventoryByProxy;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Inventory getInventoryBukaPetiByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		Inventory inventoryByProxy = (Inventory) criteria.add(Restrictions.idEq(id)).uniqueResult();
		try {
			Hibernate.initialize(inventoryByProxy.getInventoryBukapeti());
			
			return inventoryByProxy;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}

	}

	@Override
	public void saveOrUpdate(List<Inventory> inventoryList) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		try { 
			for (Inventory inventory : inventoryList) {
				Criteria criteria = session.createCriteria(Inventory.class);

				Transaction tx = session.beginTransaction();
				
				criteria.add(Restrictions.eq("inventoryLocation", InventoryLocation.swi));
				criteria.add(Restrictions.eq("inventoryPacking", InventoryPacking.lembaran));
				criteria.add(Restrictions.eq("inventoryCode", inventory.getInventoryCode()));
				criteria.add(Restrictions.eq("thickness", inventory.getThickness()));
				criteria.add(Restrictions.eq("width", inventory.getWidth()));
				criteria.add(Restrictions.eq("length", inventory.getLength()));
				
				log.info("InventoryHibernate: saveOrUpdate: "+criteria.list());
				
				if (criteria.list().isEmpty()) {
					log.info("save");
					// save
					session.save(inventory);
					
				} else {
					log.info("update");
					
					// update
					Inventory dbInventory = (Inventory) criteria.list().get(0);
					int dbShtQty = dbInventory.getSheetQuantity();
					BigDecimal dbKgQty = dbInventory.getWeightQuantity();
					
					dbInventory.setSheetQuantity(dbShtQty+inventory.getSheetQuantity());
					dbInventory.setWeightQuantity(dbKgQty.add(inventory.getWeightQuantity()));
					dbInventory.setMarking(inventory.getMarking());
					dbInventory.setContractNumber(inventory.getContractNumber());
					dbInventory.setLcNumber(inventory.getLcNumber());
					dbInventory.setReceiveDate(inventory.getReceiveDate());
					dbInventory.setNote(inventory.getNote());
					dbInventory.setInventoryStatus(inventory.getInventoryStatus());
					
					session.update(dbInventory);
				}
				
				tx.commit();
			}			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public Inventory getInventoryTransferByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Inventory.class);
		Inventory inventoryByProxy = (Inventory) criteria.add(Restrictions.idEq(id)).uniqueResult();
		try {
			Hibernate.initialize(inventoryByProxy.getInventoryTransfer());
			
			return inventoryByProxy;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}


}
