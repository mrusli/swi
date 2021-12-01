package com.pyramix.swi.persistence.inventory.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.InventoryType;

public interface InventoryDao {

	public Inventory findInventoryById(Long id) throws Exception;
	
	public void createIndexer() throws Exception;

	public List<Inventory> searchInventory(String searchString) throws Exception;

	public List<Inventory> searchInventory(String searchString, InventoryPacking[] selectedPackingType) throws Exception;

	public List<Inventory> searchInventory(String searchString, InventoryPacking[] selectedPackingType, InventoryLocation[] selectedLocation) throws Exception;
	
	public List<Inventory> findAllInventoryByStatus(InventoryStatus inventoryStatus) throws Exception;

	public List<Inventory> findAllInventory() throws Exception;
	
	public List<Inventory> findInventoryByProductType(InventoryType inventoryType, InventoryStatus inventoryStatus) throws Exception;
	
	public List<Inventory> findAllInventoryByPacking(InventoryPacking[] packingType) throws Exception;
	
	public List<Inventory> findAllInventoryByPacking(InventoryStatus status, InventoryPacking[] packingType) throws Exception;

	public List<Inventory> findAllInventoryOfLembaranPacking() throws Exception;
	
	public List<Inventory> findAllInventoryOfLembaranPacking(InventoryStatus status) throws Exception;
	
	public List<InventoryCode> findAllInventoryCode() throws Exception;
	
	public InventoryCode findInventoryCodeById(Long id) throws Exception;
	
	public List<InventoryCode> searchInventoryCode(String searchString) throws Exception;

	public void save(List<Inventory> inventoryList) throws Exception;

	public void update(Inventory updatedInventory) throws Exception;

	public InventoryType findInventoryTypeById(Long id) throws Exception;

	public List<InventoryCode> findDistinctInventoryCode() throws Exception;

	public List<InventoryCode> findDistinctInventoryCode(InventoryPacking packing) throws Exception;
	
	public List<BigDecimal> findDistinctThickness() throws Exception;
	
	public List<BigDecimal> findDistinctThickness(InventoryCode selectedCode) throws Exception;
	
	public List<Inventory> findAllInventoryByPackingAndLocation(InventoryStatus ready,
			InventoryPacking[] inventoryPackingType, InventoryLocation inventoryLocation) throws Exception;

	public List<Inventory> filterInventory(InventoryStatus status, InventoryCode code, 
			BigDecimal thickness, InventoryLocation location) throws Exception;
	
	public List<Inventory> filterInventory(InventoryPacking packing, InventoryStatus status, 
			InventoryCode code, BigDecimal thickness, InventoryLocation location) throws Exception;	
	
	public Inventory getInventoryProcessByProxy(long id) throws Exception;

	public Inventory getInventoryBukaPetiByProxy(long id) throws Exception;

	public Inventory getInventoryTransferByProxy(long id) throws Exception;

	/**
	 * For use with BukaPeti ONLY
	 * Depending on the inventory code and size (thickness, width, length), if the query returns an empty list,
	 * this function saves (add a new row) in the inventory table.  Else, the matched row found (based on the
	 * criteria / query), will be updated: sheetQty, weightQty, marking, contractNo, LCNo, receivedDate, note, status
	 * 
	 * @param inventoryList - the list of Inventory to be saved / updated
	 * @throws Exception
	 */
	public void saveOrUpdate(List<Inventory> inventoryList) throws Exception;
	
}
