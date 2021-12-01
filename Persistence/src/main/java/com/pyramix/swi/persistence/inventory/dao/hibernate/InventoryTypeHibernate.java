package com.pyramix.swi.persistence.inventory.dao.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.dao.InventoryTypeDao;

public class InventoryTypeHibernate extends DaoHibernate implements InventoryTypeDao {

	@Override
	public InventoryType findInventoryTypeById(long id) throws Exception {

		return (InventoryType) super.findById(InventoryType.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryType> findAllInventoryType() throws Exception {

		return super.findAll(InventoryType.class);
	}

	@Override
	public long save(InventoryType inventoryType) throws Exception {
		
		return super.save(inventoryType);
	}

	@Override
	public void update(InventoryType inventoryType) throws Exception {

		super.update(inventoryType);
	}

	@Override
	public void createIndexer() throws Exception {
		Session sessionFullText = getSessionFactory().openSession();
		
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFullText);

		try {
			
			fullTextSession.createIndexer().startAndWait();
			
		} catch (InterruptedException e) {			
			throw new Exception(e.getMessage());
			
		} finally {
			sessionFullText.close();
		}		
	}

}
