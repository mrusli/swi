package com.pyramix.swi.webui.inventory;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryCodeDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4075035697425974765L;
	
	private InventoryDao inventoryDao;

	private Window inventoryCodeDialog;
	private Textbox searchCodeTextbox;
	private Listbox codeListbox;
	
	private List<InventoryCode> inventoryCodeList;
	
	public void onCreate$inventoryCodeDialog(Event event) throws Exception {
		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();

		// list
		listInventoryCode();
	}

	private void listInventoryCode() throws Exception {
		// list all inventory code
		setInventoryCodeList(getInventoryDao().findAllInventoryCode());
		
		codeListbox.setModel(new ListModelList<InventoryCode>(getInventoryCodeList()));
		codeListbox.setItemRenderer(getCodeListitemRencerer());
	}
	
	private ListitemRenderer<InventoryCode> getCodeListitemRencerer() {
		
		return new ListitemRenderer<InventoryCode>() {
			
			@Override
			public void render(Listitem item, InventoryCode inventoryCode, int index) throws Exception {
				Listcell lc;
				
				lc = new Listcell(inventoryCode.getProductCode());
				lc.setParent(item);
				
				item.setValue(inventoryCode);
			}
		};
	}

	private void setSearchTextboxEventListener() {
		
		searchCodeTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				// index
				// getInventoryDao().createIndexer();
				
				try {
					setInventoryCodeList(
							getInventoryDao().searchInventoryCode(searchCodeTextbox.getValue()));
					
					codeListbox.setModel(new ListModelList<InventoryCode>(getInventoryCodeList()));
					codeListbox.setItemRenderer(getCodeListitemRencerer());
					
				} catch (Exception e) {
					// list all
					listInventoryCode();
					
				}
			}
		});
		
	}

	public void onClick$searchCodeButton(Event event) throws Exception {
		// index
		getInventoryDao().createIndexer();
		
		try {
			setInventoryCodeList(
					getInventoryDao().searchInventoryCode(searchCodeTextbox.getValue()));
			
			codeListbox.setModel(new ListModelList<InventoryCode>(getInventoryCodeList()));
			codeListbox.setItemRenderer(getCodeListitemRencerer());
			
		} catch (Exception e) {
			// list all
			listInventoryCode();
			
		}		
	}
	
	public void onClick$selectButton(Event event) throws Exception {
		InventoryCode selCode = null;
		
		if (codeListbox.getSelectedItem() == null) {
			throw new Exception("Belum memilih Kode Inventory. Klik Kode Inventory sebelum klik tombol Pilih.");
		} else {
			selCode = codeListbox.getSelectedItem().getValue();
		}
		
		Events.sendEvent(Events.ON_SELECT, inventoryCodeDialog, selCode);
		inventoryCodeDialog.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		Events.sendEvent(Events.ON_CANCEL, inventoryCodeDialog, null);
		inventoryCodeDialog.detach();
	}
	
	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public List<InventoryCode> getInventoryCodeList() {
		return inventoryCodeList;
	}

	public void setInventoryCodeList(List<InventoryCode> inventoryCodeList) {
		this.inventoryCodeList = inventoryCodeList;
	}

}
