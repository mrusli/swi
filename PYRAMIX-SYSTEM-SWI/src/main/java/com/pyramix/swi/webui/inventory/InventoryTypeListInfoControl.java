package com.pyramix.swi.webui.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.persistence.inventory.dao.InventoryTypeDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class InventoryTypeListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7863810985451906869L;

	private InventoryTypeDao inventoryTypeDao;
	
	private Label formTitleLabel, infoInventoryTypeLabel, infoInventoryCodelabel;
	private Listbox inventoryTypeListbox, inventoryCodeListbox;
	
	private List<InventoryType> inventoryTypeList;
	private int inventoryTypeCount;
	private int inventoryCodeCount;
	private InventoryType selectedInventoryType;
	
	private final Logger log = Logger.getLogger(InventoryTypeListInfoControl.class);
	
	public void onCreate$inventoryTypeListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory Type");
		inventoryCodeListbox.setEmptyMessage("Tidak ada");
		
		// list
		listAllInventoryType();
		
		// load
		loadInventoryTypeListInfo();
		
		// list inventoryCode
		listInventoryCode();
	}

	private void listAllInventoryType() throws Exception {
		setInventoryTypeList(getInventoryTypeDao().findAllInventoryType());

		// count
		inventoryTypeCount = getInventoryTypeList().size();
	}

	private void loadInventoryTypeListInfo() {
		// set model and render
		inventoryTypeListbox.setModel(
				new ListModelList<InventoryType>(getInventoryTypeList()));
		inventoryTypeListbox.setItemRenderer(getInventoryTypeListitemRenderer());
	}	
	
	private void listInventoryCode() {
		// select the 1st item (if list not empty)
		if (!getInventoryTypeList().isEmpty()) {
			InventoryType inventoryType = getInventoryTypeList().get(0);

			// list
			listInventoryCodeOfInventoryType(inventoryType);

			// count
			inventoryCodeCount = inventoryType.getInventoryCodes().size();			
			
			// info
			infoInventoryCodelabel.setValue("Inventory Type: "+inventoryType.getProductType()+" - "+inventoryCodeCount+" item Inventory Code");
		}
	}

	
	private ListitemRenderer<InventoryType> getInventoryTypeListitemRenderer() {
		
		return new ListitemRenderer<InventoryType>() {
			
			@Override
			public void render(Listitem item, InventoryType inventoryType, int index) throws Exception {
				Listcell lc;
				
				// Tipe Barang
				lc = new Listcell(inventoryType.getProductType());
				lc.setParent(item);
				
				// Berat Jenis
				lc = new Listcell(getFormatedFloatLocal(inventoryType.getDensity()));
				lc.setParent(item);

				// Penjelasan
				lc = new Listcell(inventoryType.getProductDescription());
				lc.setParent(item);
				
				// edit
				lc = initEditInventoryType(new Listcell(), inventoryType);
				lc.setParent(item);
				
				item.setValue(inventoryType);
			}

			private Listcell initEditInventoryType(Listcell listcell, InventoryType inventoryType) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						InventoryTypeData inventoryTypeData = new InventoryTypeData();
						inventoryTypeData.setPageMode(PageMode.EDIT);
						inventoryTypeData.setInventoryType(inventoryType);
						
						Map<String, InventoryTypeData> args = 
								Collections.singletonMap("inventoryTypeData", inventoryTypeData);
						
						Window inventoryTypeWin = (Window) Executions.createComponents(
								"/inventory/InventoryTypeDialogType.zul", null, args);
						
						inventoryTypeWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								getInventoryTypeDao().update((InventoryType) event.getData());
								
								// list
								listAllInventoryType();
								
								// load
								loadInventoryTypeListInfo();								
							}
						
						});
						
						inventoryTypeWin.doModal();
					}

				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$inventoryTypeListbox(Event event) throws Exception {
		infoInventoryTypeLabel.setValue("Total: "+inventoryTypeCount+" type");
	}
	
	public void onSelect$inventoryTypeListbox(Event event) throws Exception {
		setSelectedInventoryType(inventoryTypeListbox.getSelectedItem().getValue());
		
		// count
		inventoryCodeCount = getSelectedInventoryType().getInventoryCodes().size();

		// list
		listInventoryCodeOfInventoryType(getSelectedInventoryType());
		
		// info
		infoInventoryCodelabel.setValue("Inventory Type: "+getSelectedInventoryType().getProductType()+" - "+inventoryCodeCount+" item Inventory Code");
	}

	private void listInventoryCodeOfInventoryType(InventoryType inventoryType) {
		// set model and render
		inventoryCodeListbox.setModel(
				new ListModelList<InventoryCode>(inventoryType.getInventoryCodes()));
		inventoryCodeListbox.setItemRenderer(
				getInventoryCodeListitemRenderer());
	}
	
	private ListitemRenderer<InventoryCode> getInventoryCodeListitemRenderer() {
		
		return new ListitemRenderer<InventoryCode>() {
			
			@Override
			public void render(Listitem item, InventoryCode inventoryCode, int index) throws Exception {
				Listcell lc;
				
				lc = new Listcell(inventoryCode.getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				lc = initEdit(new Listcell(), inventoryCode);
				lc.setParent(item);
			}

			private Listcell initEdit(Listcell listcell, InventoryCode inventoryCode) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryCode> args = Collections.singletonMap("inventoryCode", inventoryCode);
						
						Window inventoryCodeWin = (Window) Executions.createComponents(
								"/inventory/InventoryTypeDialogCode.zul", null, args);
						
						inventoryCodeWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								getInventoryTypeDao().update(getSelectedInventoryType());

								listAllInventoryType();
								listInventoryCodeOfInventoryType(getSelectedInventoryType());
							}
						});
						
						inventoryCodeWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$newInventoryTypeButton(Event event) throws Exception {
		InventoryTypeData inventoryTypeData = new InventoryTypeData();
		inventoryTypeData.setPageMode(PageMode.NEW);
		inventoryTypeData.setInventoryType(new InventoryType());
		
		Map<String, InventoryTypeData> args = Collections.singletonMap("inventoryTypeData", inventoryTypeData);
		
		Window inventoryTypeWin = (Window) Executions.createComponents(
				"/inventory/InventoryTypeDialogType.zul", null, args);
		
		inventoryTypeWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				getInventoryTypeDao().save((InventoryType) event.getData());
				
				// list
				listAllInventoryType();
				
				// load
				loadInventoryTypeListInfo();
			}
		});
		
		inventoryTypeWin.doModal();
	}
	
	public void onClick$newInventoryCodeButton(Event event) throws Exception {
		Map<String, InventoryCode> args = Collections.singletonMap("inventoryCode", new InventoryCode());
		
		Window inventoryCodeWin = (Window) Executions.createComponents("/inventory/InventoryTypeDialogCode.zul", null, args);

		inventoryCodeWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {	
				InventoryCode inventoryCode = (InventoryCode) event.getData();
				log.info(inventoryCode.toString());
				
				InventoryType inventoryType = getSelectedInventoryType();
				
				// use inventoryCode list
				List<InventoryCode> inventoryCodeList = inventoryType.getInventoryCodes();
				inventoryCodeList.forEach(code -> log.info(code.toString()));
				// add
				inventoryCodeList.add(inventoryCode);
				// assign inventoryCodeList to inventoryType
				inventoryType.setInventoryCodes(inventoryCodeList);
				// update
				getInventoryTypeDao().update(inventoryType);
				// list inventoryType
				listAllInventoryType();
				// list selected inventoryType
				listInventoryCodeOfInventoryType(inventoryType);
			}
		});
		
		inventoryCodeWin.doModal();
	}
	
	public InventoryTypeDao getInventoryTypeDao() {
		return inventoryTypeDao;
	}

	public void setInventoryTypeDao(InventoryTypeDao inventoryTypeDao) {
		this.inventoryTypeDao = inventoryTypeDao;
	}

	public List<InventoryType> getInventoryTypeList() {
		return inventoryTypeList;
	}

	public void setInventoryTypeList(List<InventoryType> inventoryTypeList) {
		this.inventoryTypeList = inventoryTypeList;
	}

	public InventoryType getSelectedInventoryType() {
		return selectedInventoryType;
	}

	public void setSelectedInventoryType(InventoryType selectedInventoryType) {
		this.selectedInventoryType = selectedInventoryType;
	}

}
