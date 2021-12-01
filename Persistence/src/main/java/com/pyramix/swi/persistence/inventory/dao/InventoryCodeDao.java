package com.pyramix.swi.persistence.inventory.dao;

import java.util.List;

import com.pyramix.swi.domain.inventory.InventoryCode;

public interface InventoryCodeDao {

	public InventoryCode findInventoryCodeById(Long id) throws Exception;
	
	public List<InventoryCode> findAllInventoryCode() throws Exception;
	
	public Long save(InventoryCode inventoryCode) throws Exception;
	
	public void update(InventoryCode inventoryCode) throws Exception;

}
