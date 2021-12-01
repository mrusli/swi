package com.pyramix.swi.webui.inventory;

import java.util.List;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryLocation;

public class InventoryData {

	private InventoryListInfoType inventoryListInfoType;
	
	private InventoryLocation inventoryLocation;
	
	private List<Inventory> selectedInventoryList;
	
	/**
	 * @return the inventoryListInfoType
	 */
	public InventoryListInfoType getInventoryListInfoType() {
		return inventoryListInfoType;
	}

	/**
	 * @param inventoryListInfoType the inventoryListInfoType to set
	 */
	public void setInventoryListInfoType(InventoryListInfoType inventoryListInfoType) {
		this.inventoryListInfoType = inventoryListInfoType;
	}

	/**
	 * @return the selectedInventoryList
	 */
	public List<Inventory> getSelectedInventoryList() {
		return selectedInventoryList;
	}

	/**
	 * @param selectedInventoryList the selectedInventoryList to set
	 */
	public void setSelectedInventoryList(List<Inventory> selectedInventoryList) {
		this.selectedInventoryList = selectedInventoryList;
	}

	public InventoryLocation getInventoryLocation() {
		return inventoryLocation;
	}

	public void setInventoryLocation(InventoryLocation inventoryLocation) {
		this.inventoryLocation = inventoryLocation;
	}

	
}
