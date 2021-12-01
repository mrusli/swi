package com.pyramix.swi.persistence.inventory.dao;

import java.util.List;

import com.pyramix.swi.domain.inventory.InventoryType;

public interface InventoryTypeDao {

	public InventoryType findInventoryTypeById(long id) throws Exception;
	
	public List<InventoryType> findAllInventoryType() throws Exception;
	
	public long save(InventoryType inventoryType) throws Exception;
	
	public void update(InventoryType inventoryType) throws Exception;

	public void createIndexer() throws Exception;

}
