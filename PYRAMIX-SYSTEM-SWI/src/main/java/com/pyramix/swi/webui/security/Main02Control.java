package com.pyramix.swi.webui.security;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Include;

import com.pyramix.swi.webui.common.GFCBaseController;

public class Main02Control extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1600091552700393059L;
	
	private Include mainInclude;

	public void onClick$homeMenuitem(Event event) throws Exception {
		mainInclude.setSrc(null);
	}	
	
	public void onClick$inventoryMenuitem(Event event) throws Exception {
		mainInclude.setSrc("/inventory/InventoryListInfo.zul");
	}
	

}
