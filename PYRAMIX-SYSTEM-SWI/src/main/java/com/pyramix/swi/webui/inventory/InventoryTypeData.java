package com.pyramix.swi.webui.inventory;

import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.webui.common.PageMode;

public class InventoryTypeData {

	private PageMode pageMode;
	
	private InventoryType inventoryType;

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}
	
}
