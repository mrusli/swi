package com.pyramix.swi.webui.inventory.process;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.webui.common.PageMode;

public class ProcessCompletedData {

	private PageMode pageMode;
		
	private InventoryProcess inventoryProcess;

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}
	
}
