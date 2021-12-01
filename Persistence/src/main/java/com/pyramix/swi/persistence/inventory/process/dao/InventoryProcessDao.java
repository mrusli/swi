package com.pyramix.swi.persistence.inventory.process.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;

public interface InventoryProcessDao {

	public InventoryProcess findInventoryProcessById(long id) throws Exception;
	
	public List<InventoryProcess> findAllInventoryProcess() throws Exception;
	
	/**
	 * list all InventoryProcess order (sorted) by orderDate.
	 * 
	 * @param desc :
	 * 	true - list InventoryProcess by the latest orderDate first
	 * 	false - list InventoryProcess by the earliest orderDate (default) first
	 * @return {@link List} {@link InventoryProcess}
	 * @throws Exception
	 */
	public List<InventoryProcess> findAllInventoryProcess(boolean desc) throws Exception;

	/**
	 * list InventoryProcess by date range.  Passing a <code>true</code> value for the desc parameter, returns the list in
	 * ascending order (the latest orderDate listed first)
	 * 
	 * @param desc
	 * 	<code>true</code> - list InventoryProcess by the latest orderDate first
	 * 	<code>false</code> - list InventoryProcess by the earliest orderDate (default) first
	 * @param startDate
	 * @param endDate
	 * @return {@link List} {@link InventoryProcess}
	 */
	public List<InventoryProcess> findAllInventoryProcess_By_DateRange(boolean desc, Date startDate, Date endDate);	
	
	public Long save(InventoryProcess inventoryProcess) throws Exception;
	
	public void update(InventoryProcess inventoryProcess) throws Exception;

	public InventoryProcess getProcessedByCoByProxy(long id) throws Exception;

	public InventoryProcess getProcessMaterialsByProxy(long id) throws Exception;

	public InventoryProcess getInventoryRefByProxy(long id) throws Exception;

	public InventoryProcessMaterial getProcessProductsByProxy(long id) throws Exception;

	public InventoryProcessProduct getProcessProductsCustomerByProxy(long id) throws Exception;

	public InventoryProcessMaterial getInventoryProcessByProxy(long id);


}
