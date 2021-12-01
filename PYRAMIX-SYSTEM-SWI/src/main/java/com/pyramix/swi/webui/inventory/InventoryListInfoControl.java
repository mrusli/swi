package com.pyramix.swi.webui.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiStatus;
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9073184601907289134L;

	private InventoryDao inventoryDao;
	
	private Label formTitleLabel, infoResultlabel, statusLabel;
	private Listbox inventoryListbox;
	private Textbox searchTextbox;
	private Tab allTab;
	private Tabbox packingTabbox;
	private Combobox statusCombobox, kodeCombobox, thicknessCombobox,
		locationCombobox;
	
	private List<Inventory> inventoryList, inventoryFilterList;

	// packing defaulted to index#1 : SEMUA
	private final int DEFAULT_TAB_INDEX = 0;
	private final Logger log = Logger.getLogger(InventoryListInfoControl.class);	
	
	public void onCreate$inventoryListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory");
		
		// inventory listbox -- if empty
		inventoryListbox.setEmptyMessage("Tidak ada - Kosong");
		
		// init filter list
		setInventoryFilterList(new ArrayList<Inventory>());
		
		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();
		
		// setup the status combobox
		setupStatusCombobox();
		
		// setup the location combobox
		setupLocationCombobox();
		
		// set tab
		packingTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// load and display
		listBySelection(DEFAULT_TAB_INDEX);

/*
 * 		eq = EventQueues.lookup("interactive", EventQueues.APPLICATION, true);
		eq.subscribe(new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// update
				loadAllInventory();			
				
				// -- event.getName() returns the event name -- in this case Events.ON_CHANGE (onChange) 
				// System.out.println(event.getName().equals(Events.ON_CHANGE));
				
				// -- event.getData() returns the data included in the published event
				// -- in this case it's the updated Inventory data
				// System.out.println(event.getData());
				
				if (event.getName().equals(Events.ON_CHANGE)) {
					// Inventory updatedInventory = (Inventory) event.getData();
					
				}				
				
				// notify
				Clients.showNotification("Perubahan data Inventory. Layar ter-update.", "info", null, "bottom_right", 0);
			}
		});
*/		
	}	
	
	private void setupStatusCombobox() {
		Comboitem comboitem;
		
		// all
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setStyle("font-weight: bold;");
		comboitem.setValue(null);
		comboitem.setParent(statusCombobox);
		
		for (InventoryStatus status : InventoryStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(status.toString());
			comboitem.setValue(status);
			comboitem.setParent(statusCombobox);
		}
		
		// semua
		statusCombobox.setSelectedIndex(0);
	}
		
	private void setupLocationCombobox() {
		Comboitem comboitem;
		
		// all
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setStyle("font-weight: bold;");
		comboitem.setValue(null);
		comboitem.setParent(locationCombobox);
		
		for (InventoryLocation location : InventoryLocation.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(location.toString());
			comboitem.setValue(location);
			comboitem.setParent(locationCombobox);
		}
		
		// semua
		locationCombobox.setSelectedIndex(0);
	}
	
	private void setSearchTextboxEventListener() {
		
		searchTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				searchInventory();
			}
		});		
	}

	public void onClick$searchButton(Event event) throws Exception {
		
		searchInventory();
		
	}
	
	private void searchInventory() throws WrongValueException, Exception {
		
		if (searchTextbox.getValue().isEmpty()) { 
			loadAllInventory();
				
			// reset to 'all' tab
			allTab.setSelected(true);
				
			return; 
		}
			
		if (packingTabbox.getSelectedIndex()==0) {
			// semua
			// search
			setInventoryList(
					getInventoryDao().searchInventory(searchTextbox.getValue()));
				
			inventoryListbox.setModel(
					new ListModelList<Inventory>(getInventoryList()));
			inventoryListbox.setItemRenderer(getAllInventoryListitemRenderer());
		} else {
			// packing-type
			// search
			InventoryPacking[] selectedPackingType = null;
				
			switch (packingTabbox.getSelectedIndex()) {
				case 0:
					selectedPackingType = new InventoryPacking[0];
					
					break;
				case 1:
					selectedPackingType = new InventoryPacking[1];
					selectedPackingType[0] = InventoryPacking.coil;
					break;
				case 2:
					selectedPackingType = new InventoryPacking[1];
					selectedPackingType[0] = InventoryPacking.petian;					
					break;
				case 3:
					selectedPackingType = new InventoryPacking[1];
					selectedPackingType[0] = InventoryPacking.lembaran;					
					break;
				default:
					break;
			}
				
			setInventoryList(
					getInventoryDao().searchInventory(searchTextbox.getValue(), selectedPackingType));

			// set the model
			inventoryListbox.setModel(
					new ListModelList<Inventory>(getInventoryList()));
			// render
			inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		}
			
		// info result
		displayInfoResult(getInventoryList());		
	}
	
	// ALL - with status
	private ListitemRenderer<Inventory> getAllInventoryListitemRenderer() {

		return new ListitemRenderer<Inventory>() {

			@Override
			public void render(Listitem item, Inventory inventory, int index) throws Exception {
				Listcell lc;
				
				// Status
				lc = initStatus(new Listcell(), inventory);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = initInventoryCode(new Listcell(), inventory.getInventoryCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell();
				lc.setLabel(
						getFormatedFloatLocal(inventory.getThickness())+" x "+
						// String.valueOf(inventory.getThickness())+" x "+
						getFormatedFloatLocal(inventory.getWidth())+" x "+
						// String.valueOf(inventory.getWidth())+" x "+
						(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								// getFormatedDecimal(inventory.getLength())));
								getFormatedFloatLocal(inventory.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);				
				
				// Qty
				lc = new Listcell();
				lc.setLabel(getFormatedInteger(inventory.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell();
				lc.setLabel(getFormatedFloatLocal(inventory.getWeightQuantity()));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Lokasi
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryLocation().toString());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell();
				lc.setLabel(inventory.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// No.Kontrak
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getContractNumber());
				lc.setParent(item);
				
				// Tgl.Penerimaan
				lc = new Listcell();
				lc.setLabel(dateToStringDisplay(asLocalDate(inventory.getReceiveDate()), getLongDateFormat()));
				lc.setParent(item);
								
				// Catatan
				lc = new Listcell();
				lc.setLabel(inventory.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);				

				// edit button
				lc = initInventoryEdit(new Listcell(), inventory);
				lc.setParent(item);
				
			}

			private Listcell initStatus(Listcell listcell, Inventory inventory) throws Exception {				
				Label primaryLabel = new Label();
				String transferStatusStr = "";
				if (inventory.getInventoryStatus().equals(InventoryStatus.ready)) {
					// check whether it's being transfered - ready[TM]
					if (inventory.getInventoryTransfer()!=null) {
						Inventory inventoryTransferByProxy =
								getInventoryDao().getInventoryTransferByProxy(inventory.getId());
						InventoryTransferStatus transferStatus =
								inventoryTransferByProxy.getInventoryTransfer().getInventoryTransferStatus();
						// check whether it's 'selesai'
						if (transferStatus.equals(InventoryTransferStatus.selesai)) {
							primaryLabel.setValue("ready");
							primaryLabel.setStyle("color:blue;");							
						} else {
							transferStatusStr = " [T"+
									transferStatus.toCode(transferStatus.ordinal())+
									"]";
							primaryLabel.setValue("ready"+transferStatusStr);
							primaryLabel.setStyle("color:blue;");
						}
					} else {
						primaryLabel.setValue("ready");
						primaryLabel.setStyle("color:blue;");
					}
				} else {
					primaryLabel.setValue(inventory.getInventoryStatus().toString());
					primaryLabel.setStyle("color:red;");
				}
				primaryLabel.setParent(listcell);
				
				Label processLabel = new Label();
				String processStatusStr = "";
				if ((inventory.getInventoryStatus().equals(InventoryStatus.process))&&(inventory.getInventoryProcess()!=null)) {
					// get process status
					Inventory inventoryProcessByProxy = 
							getInventoryDao().getInventoryProcessByProxy(inventory.getId());
					InventoryProcessStatus processStatus = 
							inventoryProcessByProxy.getInventoryProcess().getProcessStatus();
					processStatusStr = " ["+
							processStatus.toCode(processStatus.ordinal())+
							"]";
				} 
				processLabel.setValue(processStatusStr);
				processLabel.setParent(listcell);
				
				Label bukapetiLabel = new Label();
				String bukapetiStatusStr = "";
				if ((inventory.getInventoryStatus().equals(InventoryStatus.bukapeti))&&(inventory.getInventoryBukapeti()!=null)) {
					// get bukapeti status
					Inventory inventoryBukapetiByProxy =
							getInventoryDao().getInventoryBukaPetiByProxy(inventory.getId());
					InventoryBukaPetiStatus bukapetiStatus =
							inventoryBukapetiByProxy.getInventoryBukapeti().getInventoryBukapetiStatus();
					bukapetiStatusStr = " ["+
							bukapetiStatus.toCode(bukapetiStatus.ordinal())+
							"]";
				}
				bukapetiLabel.setValue(bukapetiStatusStr);
				bukapetiLabel.setParent(listcell);
				
				return listcell;
			}

			private Listcell initInventoryCode(Listcell listcell, InventoryCode inventoryCode) {
				if (inventoryCode == null) {
					listcell.setLabel("UNKNOWN");
					listcell.setStyle("background-color: red;");
					
					return listcell;
				} else {
					listcell.setLabel(inventoryCode.getProductCode());
					listcell.setStyle("white-space: nowrap;");
				}
				
				return listcell;
			}
		};
	}

	private ListitemRenderer<Inventory> getInventoryListitemRenderer() {

		return new ListitemRenderer<Inventory>() {
			
			@Override
			public void render(Listitem item, Inventory inventory, int index) throws Exception {
				Listcell lc;
				
				// Status
				lc = initStatus(new Listcell(), inventory);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = initInventoryCode(new Listcell(), inventory.getInventoryCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);

				// Spesifikasi
				lc = new Listcell();
				lc.setLabel(
						getFormatedFloatLocal(inventory.getThickness())+" x "+
						// String.valueOf(inventory.getThickness())+" x "+
						getFormatedFloatLocal(inventory.getWidth())+" x "+
						// String.valueOf(inventory.getWidth())+" x "+
						(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								// getFormatedDecimal(inventory.getLength())));
								getFormatedFloatLocal(inventory.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);				

				// Qty
				lc = new Listcell();
				lc.setLabel(getFormatedInteger(inventory.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell();
				lc.setLabel(getFormatedFloatLocal(inventory.getWeightQuantity()));
						//getFormatedDecimal(inventory.getWeightQuantity()));
				lc.setParent(item);

				// Packing
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Lokasi
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryLocation().toString());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell();
				lc.setLabel(inventory.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// No.Kontrak
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getContractNumber());
				lc.setParent(item);
				
				// Tgl.Penerimaan
				lc = new Listcell();
				lc.setLabel(dateToStringDisplay(asLocalDate(inventory.getReceiveDate()), getLongDateFormat()));
				lc.setParent(item);
								
				// Catatan
				lc = new Listcell();
				lc.setLabel(inventory.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);				

				// edit button
				lc = initInventoryEdit(new Listcell(), inventory);
				lc.setParent(item);
				
			}

			private Listcell initStatus(Listcell listcell, Inventory inventory) throws Exception {
				Label primaryLabel = new Label();
				String transferStatusStr = "";
				if (inventory.getInventoryStatus().equals(InventoryStatus.ready)) {
					// check whether it's being transfered - ready[TM]
					if (inventory.getInventoryTransfer()!=null) {
						Inventory inventoryTransferByProxy =
								getInventoryDao().getInventoryTransferByProxy(inventory.getId());
						InventoryTransferStatus transferStatus =
								inventoryTransferByProxy.getInventoryTransfer().getInventoryTransferStatus();
						// check whether it's 'selesai'
						if (transferStatus.equals(InventoryTransferStatus.selesai)) {
							primaryLabel.setValue("ready");
							primaryLabel.setStyle("color:blue;");							
						} else {
							transferStatusStr = " [T"+
									transferStatus.toCode(transferStatus.ordinal())+
									"]";
							primaryLabel.setValue("ready"+transferStatusStr);
							primaryLabel.setStyle("color:blue;");
						}
					} else {
						primaryLabel.setValue("ready");
						primaryLabel.setStyle("color:blue;");
					}
							
				} else {
					primaryLabel.setValue(inventory.getInventoryStatus().toString());
					primaryLabel.setStyle("color:red;");
				}
				primaryLabel.setParent(listcell);
				
				return listcell;
			}

			private Listcell initInventoryCode(Listcell listcell, InventoryCode inventoryCode) {
				if (inventoryCode == null) {
					listcell.setLabel("UNKNOWN");
					listcell.setStyle("background-color: red;");
					
					return listcell;
				} else {
					listcell.setLabel(inventoryCode.getProductCode());
					listcell.setStyle("white-space: nowrap;");
				}
				
				return listcell;
			}
			
		};
	}

	private Listcell initInventoryEdit(Listcell listcell, Inventory inventory) {
		Button editButton = new Button();
		
		editButton.setLabel("...");
		editButton.setClass("inventoryEditButton");
		editButton.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Map<String, Inventory> arguments = 
						Collections.singletonMap("inventoryToEdit", inventory);

				Window inventoryEditDialog = 
						(Window) Executions.createComponents(
								"/inventory/InventoryEditDialog.zul", null, arguments);
				
				inventoryEditDialog.addEventListener(Events.ON_OK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Inventory editedInventory = (Inventory) event.getData();
						
						// save / update the changes into DB
						getInventoryDao().update(editedInventory);

						// re-index -- because indexer is set to normal
						getInventoryDao().createIndexer();			

						// which packing tab is selected?
						int selIndex = packingTabbox.getSelectedIndex();
						
						// list according to the selected packing tab
						listBySelection(selIndex);
						
						// notify -- notif for 10 seconds
						Clients.showNotification("Perubahan Inventory sukses tersimpan.", "info", null, "bottom_right", 10000);
						
					}
				});
				
				inventoryEditDialog.doModal();
			}
			
		});
		editButton.setParent(listcell);
		
		return listcell;
	}	
	
	public void onClick$newButton(Event event) throws Exception {
		// clear search text
		searchTextbox.setValue("");
		
		Window inventoryAddDialog = (Window) Executions.createComponents(
				"/inventory/InventoryAddDialog.zul", null, null);
 
		inventoryAddDialog.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onEvent(Event event) throws Exception {
				List<Inventory> inventoryList = (List<Inventory>) event.getData();
				
				// save
				getInventoryDao().save(inventoryList);
				
				// re-index -- because indexer is set to normal
				getInventoryDao().createIndexer();

				// which packing tab is selected?
				int selIndex = packingTabbox.getSelectedIndex();
				
				// list according to the selected packing tab
				listBySelection(selIndex);

				// notify
				Clients.showNotification("Penambahan Inventory sukses tersimpan.", "info", null, "bottom_right", 10000);
			}
		});
		
		inventoryAddDialog.doModal();
	}
	
	public void onSelect$packingTabbox(Event event) throws Exception {
		int selIndex = packingTabbox.getSelectedIndex();
		List<InventoryCode> inventoryCodeList = null;
		
		// reset to 'semua'
		statusCombobox.setSelectedIndex(0);
		kodeCombobox.setSelectedIndex(0);
		thicknessCombobox.setSelectedIndex(0);
		locationCombobox.setSelectedIndex(0);
		
		switch (selIndex) {
		case 0:
			// semua
			log.info("Find ALL Distinct InventoryCode");
			inventoryCodeList = getInventoryDao().findDistinctInventoryCode();
			
			break;
		case 1:
			// coil
			// query distinct inventoryCode based on the selected packing type
			log.info("Find Distinct InventoryCode based on packing type : " + InventoryPacking.coil.toString());
			inventoryCodeList = getInventoryDao().findDistinctInventoryCode(InventoryPacking.coil);
			
			break;
		case 2:
			// petian
			// query distinct inventoryCode based on the selected packing type
			log.info("Find Distinct InventoryCode based on packing type : " + InventoryPacking.petian.toString());
			inventoryCodeList = getInventoryDao().findDistinctInventoryCode(InventoryPacking.petian);
			
			break;
		case 3:
			// lembaran
			// query distinct inventoryCode based on the selected packing type
			log.info("Find Distinct InventoryCode based on packing type : " + InventoryPacking.lembaran.toString());
			inventoryCodeList = getInventoryDao().findDistinctInventoryCode(InventoryPacking.lembaran);
			
			break;
		default:
			break;
		}
		
		if (selIndex > 0) {
			// clear combo list
			kodeCombobox.getItems().clear();
			
			// all InventoryCode
			Comboitem comboitem;
			
			comboitem = new Comboitem();
			comboitem.setLabel("Semua");
			comboitem.setStyle("font-weight: bold;");
			comboitem.setValue(null);
			comboitem.setParent(kodeCombobox);
			
			// distinct Kode
			for (InventoryCode inventoryCode : inventoryCodeList) {
				comboitem = new Comboitem();
				comboitem.setLabel(inventoryCode.getProductCode());
				comboitem.setValue(inventoryCode);
				comboitem.setParent(kodeCombobox);
			}			
		}
		
		listBySelection(selIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		InventoryPacking[] packingType = { };
		
		switch (selIndex) {
		case 0: // Semua
			statusLabel.setVisible(true);
			statusCombobox.setVisible(true);
			
			// check if checkbox is empty?
			if (searchTextbox.getValue().isEmpty()) {
				// load and display
				loadAllInventory();				
			} else {
				searchInventory();
			}
			
			break;
		case 1: // Coil
			packingType = new InventoryPacking[]{ InventoryPacking.coil };
			
			statusLabel.setVisible(true);
			statusCombobox.setVisible(true);
			
			// check if textbox is empty?
			if (searchTextbox.getValue().isEmpty()) {
				// load coil inventory
				setInventoryList(
						getInventoryDao().findAllInventoryByPacking(packingType));
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(getInventoryListitemRenderer());				
				// info result
				displayInfoResult(getInventoryList());			
			} else {
				searchInventory();
			}
			
			break;
		case 2: // Petian
			packingType = new InventoryPacking[]{ InventoryPacking.petian };
			
			statusLabel.setVisible(true);
			statusCombobox.setVisible(true);

			if (searchTextbox.getValue().isEmpty()) {
				// load petian inventory
				setInventoryList(
						getInventoryDao().findAllInventoryByPacking(packingType));
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getInventoryListitemRenderer());			
				// info result
				displayInfoResult(getInventoryList());
			} else {
				searchInventory();
			}			
			break;
		case 3: // Lembaran
			statusLabel.setVisible(true);
			statusCombobox.setVisible(true);		
			
			if (searchTextbox.getValue().isEmpty()) {
				// load lembaran inventory
				setInventoryList(
						getInventoryDao().findAllInventoryOfLembaranPacking());
				
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());			
			} else {
				searchInventory();
			}
			
			break;
		default:
			break;
		}
	}
	
	private void loadAllInventory() throws Exception {
		// clear search text
		searchTextbox.setValue("");
		
		log.info("Load all inventory into the list");
		
		// load all inventory
		setInventoryList(
				getInventoryDao().findAllInventory());

		// clear
		kodeCombobox.getItems().clear();
		
		// all InventoryCode
		Comboitem comboitem;

		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setStyle("font-weight: bold;");
		comboitem.setValue(null);
		comboitem.setParent(kodeCombobox);
		
		// find distinct Kode
		log.info("Find Distinct InventoryCode...");
		List<InventoryCode> inventoryCodeList = getInventoryDao().findDistinctInventoryCode();
		for (InventoryCode inventoryCode : inventoryCodeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(inventoryCode.getProductCode());
			comboitem.setValue(inventoryCode);
			comboitem.setParent(kodeCombobox);
		}
		
		// semua
		kodeCombobox.setSelectedIndex(0);
		
		// clear
		thicknessCombobox.getItems().clear();
		
		// all Thickness
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setStyle("font-weight: bold;");
		comboitem.setValue(null);
		comboitem.setParent(thicknessCombobox);
		
		// find distinct Ketebalan
		log.info("Find Distinct Thickness...");
		List<BigDecimal> inventoryThicknessList = getInventoryDao().findDistinctThickness();
		for (BigDecimal thickness : inventoryThicknessList) {
			comboitem = new Comboitem();
			comboitem.setLabel(thickness.toString());
			comboitem.setValue(thickness);
			comboitem.setParent(thicknessCombobox);
		}
		
		// semua
		thicknessCombobox.setSelectedIndex(0);
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(
				getAllInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
	}	

	private void displayInfoResult(List<Inventory> inventoryList) {
		BigDecimal totalKg = BigDecimal.ZERO;
		int totalItem = 0;
		for (Inventory inventory : inventoryList) {
			BigDecimal qtyKg = inventory.getWeightQuantity();
			
			totalKg = totalKg.add(qtyKg);
			totalItem = totalItem + 1;
			
			log.info(inventory.getInventoryCode().getProductCode()+" "+
					inventory.getThickness()+"x"+inventory.getWidth()+"x"+inventory.getLength()+" "+
					inventory.getWeightQuantity()+" Kg.");
		}
		
		log.info("Total: "+totalKg+" Kg.");
		
		// '29/11 - need exact total "###.###.###,00"
		BigDecimal totalMT = totalKg.divide(new BigDecimal(1000), 3, RoundingMode.CEILING);
		log.info("Total: "+totalMT+" MT");
		
		infoResultlabel.setValue("Total: "+String.valueOf(totalItem)+" item - "+
				toLocalFormatWithDecimal(totalMT, 3, "###.###.###,000")+"MT");
	}
	
	public void onSelect$kodeCombobox(Event event) throws Exception {
		InventoryCode selCode = kodeCombobox.getSelectedItem().getValue();
		
		// query distinct thickness based on the selected inventoryCode
		log.info("query distinct thickness based on the selected inventoryCode :" + selCode.getProductCode());
		List<BigDecimal> thicknessList = getInventoryDao().findDistinctThickness(selCode);
		// clear the combobox
		thicknessCombobox.getItems().clear();
		
		Comboitem comboitem;
		
		// all Thickness
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setStyle("font-weight: bold;");
		comboitem.setValue(null);
		comboitem.setParent(thicknessCombobox);
		
		for (BigDecimal thickness : thicknessList) {
			comboitem = new Comboitem();
			comboitem.setLabel(thickness.toString());
			comboitem.setValue(thickness);
			comboitem.setParent(thicknessCombobox);
		}
	}
	
	// public void onSelect$statusCombobox(Event event) throws Exception {
		// status tertenu
		// InventoryStatus selStatus = statusCombobox.getSelectedItem().getValue();
		
		// check -- any search string?
		///if (searchTextbox.getValue().isEmpty()) { 
			// re-load all inventory needed for filter
			// setInventoryList(
			//		getInventoryDao().findAllInventory());
		//} else {
		//	setInventoryList(
		//			getInventoryDao().searchInventory(searchTextbox.getValue()));
		//}

		//if (statusCombobox.getSelectedIndex()==0) {
			// do nothing
		//} else {			
			// filter
		//	setInventoryList(filterInventoryDisplay(selStatus));
		//}
		
		// set the model
		// inventoryListbox.setModel(
		//		new ListModelList<Inventory>(getInventoryList()));
		// render
		// inventoryListbox.setItemRenderer(
		//		getAllInventoryListitemRenderer());
		
		// info result
		// displayInfoResult(getInventoryList());
	// }
	
	
	public void onClick$filterButton(Event event) throws Exception {
		// selected status
		InventoryStatus inventoryStatus = statusCombobox.getSelectedItem().getValue();
		// selected inventory code
		InventoryCode inventoryCode = kodeCombobox.getSelectedItem().getValue();
		// selected thickness
		BigDecimal thickness = thicknessCombobox.getSelectedItem().getValue();
		// selected location
		InventoryLocation inventoryLocation = locationCombobox.getSelectedItem().getValue();
		
		// selected packing tab (Coil, Petian atau Lembaran)
		int selIndex = packingTabbox.getSelectedIndex();
		
		switch (selIndex) {
			case 0:
				// semua
				setInventoryList(getInventoryDao().filterInventory(inventoryStatus, inventoryCode, thickness, inventoryLocation));
				
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getAllInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());		
				
				break;
			case 1:
				// coil
				setInventoryList(getInventoryDao().filterInventory(InventoryPacking.coil, inventoryStatus, inventoryCode, thickness, inventoryLocation));

				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getAllInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());		

				break;
			case 2:
				// petian
				setInventoryList(getInventoryDao().filterInventory(InventoryPacking.petian, inventoryStatus, inventoryCode, thickness, inventoryLocation));

				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getAllInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());		
				
				break;
			case 3:
				// lembaran
				setInventoryList(getInventoryDao().filterInventory(InventoryPacking.lembaran, inventoryStatus, inventoryCode, thickness, inventoryLocation));

				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getAllInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());	
				
				break;
			default:
				break;
		}
		
	}
	
	public void onClick$resetButton(Event event) throws Exception {
		// reset kode
		kodeCombobox.getItems().clear();
		// reset thickness
		thicknessCombobox.getItems().clear();
		
		// reset search
		searchTextbox.setValue("");
		
		// reset to 'semua'
		packingTabbox.setSelectedIndex(0);
		// re-display
		listBySelection(0);
		
	}
	
	@SuppressWarnings("unused")
	private void addToInventoryFilterList(InventoryStatus inventoryStatus) {	
		// filter search result
		for (Inventory inventory : getInventoryList()) {
			// check for status inventory
			if (inventory.getInventoryStatus().equals(inventoryStatus)) {
				getInventoryFilterList().add(inventory);
			}
		}
	}		
	
	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public List<Inventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

	public List<Inventory> getInventoryFilterList() {
		return inventoryFilterList;
	}

	public void setInventoryFilterList(List<Inventory> inventoryFilterList) {
		this.inventoryFilterList = inventoryFilterList;
	}
}
