package com.pyramix.swi.persistence.inventory.transfer.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;

public interface InventoryTransferDao {

	public InventoryTransfer findInventoryTransferById(long id) throws Exception;
	
	public List<InventoryTransfer> findAllInventoryTransfer() throws Exception;
	
	/**
	 * list all InventoryTransfer order (sorted) by orderDate.
	 * 
	 * @param desc :
	 * 	true - list InventoryTransfer by the latest orderDate first
	 * 	false - list InventoryTransfer by the earliest orderDate (default) first
	 * @return {@link List} {@link InventoryTransfer}
	 * @throws Exception
	 */
	public List<InventoryTransfer> findAllInventoryTransfer(boolean desc) throws Exception;
	
	/**
	 * list InventoryTransfer by date range.  passing a <code>true</code> value for the desc parameter, returns the list in
	 * ascending order (the latest orderDate listed first)
	 * 
	 * @param desc
	 * 	<code>true</code> - list InventoryTransfer by the latest orderDate first
	 * 	<code>false</code> - list InventoryTransfer by the earliest orderDate (default) first
	 * @param startDate
	 * @param endDate
	 * @return {@link List} {@link InventoryTransfer}
	 * @throws Exception
	 */
	public List<InventoryTransfer> findAllInventoryTransfer_By_DateRange(boolean desc, Date startDate, Date endDate) throws Exception;
	
	public Long save(InventoryTransfer inventoryTransfer) throws Exception;
	
	public void update(InventoryTransfer inventoryTransfer) throws Exception;
	
	public InventoryTransfer getInventoryTransferMaterialByProxy(long id) throws Exception;
	
	public InventoryTransfer getInventoryTransferEndProductByProxy(long id) throws Exception;

	public InventoryTransfer getInventoryTransferFromCoByProxy(long id) throws Exception;

}
