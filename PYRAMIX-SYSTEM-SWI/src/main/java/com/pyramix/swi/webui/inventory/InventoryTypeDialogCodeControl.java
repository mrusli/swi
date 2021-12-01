package com.pyramix.swi.webui.inventory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.persistence.inventory.dao.InventoryCodeDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryTypeDialogCodeControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -254261018437383511L;

	private InventoryCodeDao inventoryCodeDao;
	
	private Window inventoryTypeDialogCodeWin;
	private Textbox codeNameTextbox;

	private InventoryCode inventoryCode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryCode(
				(InventoryCode) arg.get("inventoryCode"));
	}	
	
	public void onCreate$inventoryTypeDialogCodeWin(Event event) throws Exception {
		if (getInventoryCode().getId().compareTo(Long.MIN_VALUE)==0) {
			inventoryTypeDialogCodeWin.setTitle("Menambah (Add) Kode Barang");
			codeNameTextbox.setValue("");
		} else {
			inventoryTypeDialogCodeWin.setTitle("Merubah (Edit) Kode Barang");
			codeNameTextbox.setValue(getInventoryCode().getProductCode());
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		if (codeNameTextbox.getValue().isEmpty()) {
			throw new Exception("Kode Barang Harus diisi sebelum disimpan.");
		}
		
		InventoryCode inventoryCode = getUserModifiedInventoryCode(getInventoryCode());
		
		Events.sendEvent(Events.ON_OK, inventoryTypeDialogCodeWin, inventoryCode);
		
		inventoryTypeDialogCodeWin.detach();
	}
	
	public InventoryCode getUserModifiedInventoryCode(InventoryCode inventoryCode) {
		
		inventoryCode.setProductCode(codeNameTextbox.getValue());
		
		return inventoryCode;
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTypeDialogCodeWin.detach();
	}

	public InventoryCodeDao getInventoryCodeDao() {
		return inventoryCodeDao;
	}

	public void setInventoryCodeDao(InventoryCodeDao inventoryCodeDao) {
		this.inventoryCodeDao = inventoryCodeDao;
	}

	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

}
