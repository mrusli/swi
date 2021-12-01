package com.pyramix.swi.persistence.inventory.bukapeti.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;

public interface InventoryBukaPetiDao {

	public InventoryBukaPeti findInventoryBukapetiById(long id) throws Exception;
	
	public List<InventoryBukaPeti> findAllInventoryBukapeti() throws Exception;
	
	/**
	 * list all InventoryBukaPeti order (sorted) by orderDate.
	 * 
	 * @param desc :
	 * 	true - list InventoryBukaPeti by the latest orderDate first
	 * 	false - list InventoryBukaPeti by the earliest orderDate (default) first
	 * @return {@link List} {@link InventoryBukaPeti}
	 * @throws Exception
	 */
	public List<InventoryBukaPeti> findAllInventoryBukapeti(boolean desc) throws Exception;

	/**
	 * list InventoryBukaPeti by date range.  passing a <code>true</code> value for the desc parameter, returns the list in
	 * ascending order (the latest orderDate listed first)

	 * @param desc
	 * 	<code>true</code> - list InventoryBukaPeti by the latest orderDate first
	 * 	<code>false</code> - list InventoryBukaPeti by the earliest orderDate (default) first
	 * @param startDate
	 * @param endDate
	 * @return {@link List} {@link InventoryTransfer}
	 * @throws Exception
	 */
	public List<InventoryBukaPeti> findAllInventoryBukapeti_By_DateRange(boolean desc, Date startDate, Date endDate) throws Exception;
	
	public Long save(InventoryBukaPeti inventoryBukapeti) throws Exception;
	
	public void update(InventoryBukaPeti inventoryBukapeti) throws Exception;

	public InventoryBukaPeti getBukapetiMaterialsByProxy(long id) throws Exception;
	
	public InventoryBukaPeti getInventoryRefByProxy(long id) throws Exception;

	public InventoryBukaPeti getBukaPetiEndProductByProxy(long id) throws Exception;	
	
	public InventoryBukaPetiMaterial getBukapetiProductsByProxy(long id) throws Exception;

	public InventoryBukaPetiMaterial getInventoryBukapetiByProxy(long id) throws Exception;



}
