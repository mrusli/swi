package com.pyramix.swi.webui.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryListInfoDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5214878453987504716L;

	private Window inventoryListInfoDialogWin;
	private Label formTitleLabel, infoResultlabel;
	private Listbox inventoryListbox;
	private Textbox searchTextbox;
	private Tab allTab, coilTab, petianTab, lembaranTab;
	private Tabbox packingTabbox;
	
	private InventoryDao inventoryDao;

	private List<Inventory> inventoryList, inventoryFilterList;
	private InventoryListInfoType InventoryListInfoType;
	private InventoryData inventoryData;
	private List<Inventory> selectedInventory;
	// controlled by the tab (coil, etc.)
	private InventoryPacking[] selectedPackingType = { };
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setInventoryData(
				(InventoryData) arg.get("inventoryData"));
		
	}

	public void onCreate$inventoryListInfoDialogWin(Event event) throws Exception {
		setInventoryListInfoType(	
				getInventoryData().getInventoryListInfoType());

		setSelectedInventory(
				getInventoryData().getSelectedInventoryList());
		
		formTitleLabel.setValue("Pilih Inventory");
		
		// inventory listbox -- if empty
		inventoryListbox.setEmptyMessage("Tidak ada - Kosong");

		// init filter list
		setInventoryFilterList(new ArrayList<Inventory>());
		
		// tabbox
		setInventoryListInfoDisplay();
		
		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();
		
		// load
		loadInventoryByPackingType();		
				
	}
	
	private void setInventoryListInfoDisplay() {
		switch (getInventoryListInfoType()) {
			case Process:
				// select coil tab
				coilTab.setVisible(true);
				coilTab.setSelected(true);
				break;
			case Transfer:
				// select all tab
				allTab.setVisible(true);
				allTab.setSelected(true);
				// coil
				coilTab.setVisible(true);
				// petian
				petianTab.setVisible(true);
				break;
			case BukaPeti:
				// petian
				petianTab.setVisible(true);
				petianTab.setSelected(true);
				break;
			case CustomerOrder:
				// select all tab
				allTab.setVisible(false);
				// allTab.setSelected(true);
				// coil
				coilTab.setVisible(true);
				coilTab.setSelected(true);
				// petian
				petianTab.setVisible(true);
				// lembaran
				lembaranTab.setVisible(true);
				break;
			default:
				break;
		}
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
	
	@SuppressWarnings("static-access")
	protected void searchInventory() throws Exception {
		if (searchTextbox.getValue().isEmpty()) { 
			loadInventoryByPackingType();
			
			// reset to 1st tab
			packingTabbox.setSelectedIndex(0);
			
			return; 
		}
								
		InventoryPacking[] searchSelPackingType = { };
		
		switch (packingTabbox.getSelectedIndex()) {
		case 0:	// semua
			searchSelPackingType = new InventoryPacking[3];
			searchSelPackingType[0] = InventoryPacking.coil;
			searchSelPackingType[1] = InventoryPacking.petian;
			searchSelPackingType[2] = InventoryPacking.lembaran;
			break;
		case 1: // coil
			searchSelPackingType = new InventoryPacking[1];
			searchSelPackingType[0] = InventoryPacking.coil;
			break;
		case 2: // petian
			searchSelPackingType = new InventoryPacking[1];
			searchSelPackingType[0] = InventoryPacking.petian;				
			break;
		case 3: // lembaran
			searchSelPackingType = new InventoryPacking[1];
			searchSelPackingType[0] = InventoryPacking.lembaran;				
			break;
		default:
			break;
		}

		if ((getInventoryListInfoType().compareTo(InventoryListInfoType.Process)==0) ||
			(getInventoryListInfoType().compareTo(InventoryListInfoType.BukaPeti)==0) || 
			(getInventoryListInfoType().compareTo(InventoryListInfoType.Transfer)==0)) {
			// search with location
			InventoryLocation[] selectedInventoryLocation = { };
			selectedInventoryLocation = new InventoryLocation[1];
			selectedInventoryLocation[0] = getInventoryData().getInventoryLocation();
			
			setInventoryList(
					getInventoryDao().searchInventory(searchTextbox.getValue(), searchSelPackingType, selectedInventoryLocation));			
		} else {
			// search
			setInventoryList(
					getInventoryDao().searchInventory(searchTextbox.getValue(), searchSelPackingType));
		}
		
		// remove selected inventory
		removeSelectedInventory();
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
	}

	private void loadInventoryByPackingType() throws Exception {
		// clear search text
		searchTextbox.setValue("");
				
		// load all inventory -- according to the 
		//   list info type (e.g., process, bukapeti, etc.)
		if ((getInventoryListInfoType().compareTo(com.pyramix.swi.webui.inventory.InventoryListInfoType.Process)==0) ||
			(getInventoryListInfoType().compareTo(com.pyramix.swi.webui.inventory.InventoryListInfoType.BukaPeti)==0) ||
			(getInventoryListInfoType().compareTo(com.pyramix.swi.webui.inventory.InventoryListInfoType.Transfer)==0)) {
			// need location as well
			InventoryLocation inventoryLocation = 
					getInventoryData().getInventoryLocation();
			setInventoryList(
					getInventoryDao().findAllInventoryByPackingAndLocation(
							InventoryStatus.ready, 
							getInventoryPackingType(), 
							inventoryLocation));
		} else {
			setInventoryList(
					getInventoryDao().findAllInventoryByStatus(InventoryStatus.ready));
		}

		// remove selected inventory
		removeSelectedInventory();
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
	}

	private void removeSelectedInventory() {
		if (!getSelectedInventory().isEmpty()) {
			for (Inventory selInventory : getSelectedInventory()) {
				for (Inventory inventory : getInventoryList()) {
					if (selInventory.getId().compareTo(inventory.getId())==0) {
						getInventoryList().remove(inventory);
						
						break;
					}
				}
				
			}
		}
	}
	
	private InventoryPacking[] getInventoryPackingType() {
		
		switch (getInventoryListInfoType()) {
		case Process: 
			InventoryPacking[] packingTypeForProcess = new InventoryPacking[1];
			packingTypeForProcess[0] = InventoryPacking.coil;

			// for search
			selectedPackingType = new InventoryPacking[1];
			selectedPackingType[0] = InventoryPacking.coil;

			return packingTypeForProcess;			
		case Transfer:
			InventoryPacking[] packingTypeForTransfer = new InventoryPacking[2];
			packingTypeForTransfer[0] = InventoryPacking.coil;
			packingTypeForTransfer[1] = InventoryPacking.petian;
			
			// for search
			selectedPackingType = new InventoryPacking[2];
			selectedPackingType[0] = InventoryPacking.coil;
			selectedPackingType[1] = InventoryPacking.petian;
			
			return packingTypeForTransfer;
		case BukaPeti:
			InventoryPacking[] packingTypeForBukaPeti = new InventoryPacking[1];
			packingTypeForBukaPeti[0] = InventoryPacking.petian;
			
			// for search
			selectedPackingType = new InventoryPacking[1];
			selectedPackingType[0] = InventoryPacking.petian;
			
			return packingTypeForBukaPeti;
		case CustomerOrder:
			InventoryPacking[] packingTypeForOrder = new InventoryPacking[3];
			packingTypeForOrder[0] = InventoryPacking.coil;
			packingTypeForOrder[1] = InventoryPacking.petian;
			packingTypeForOrder[2] = InventoryPacking.lembaran;
			
			// for search
			selectedPackingType = new InventoryPacking[3];
			selectedPackingType[0] = InventoryPacking.coil;
			selectedPackingType[1] = InventoryPacking.petian;
			selectedPackingType[2] = InventoryPacking.lembaran;
			
			return packingTypeForOrder; 
		default:
			return null;
		}
	}
		
	private ListitemRenderer<Inventory> getInventoryListitemRenderer() {

		return new ListitemRenderer<Inventory>() {
			
			@Override
			public void render(Listitem item, Inventory inventory, int index) throws Exception {
				Listcell lc;

				// Status
				lc = initStatus(new Listcell(), inventory);
				lc.setParent(item);
				
				// code
				lc = initInventoryCode(new Listcell(), inventory.getInventoryCode());
				lc.setParent(item);

				// specification
				lc = new Listcell();
				lc.setLabel(
						getFormatedFloatLocal(inventory.getThickness())+" x "+
						// String.valueOf(inventory.getThickness())+" x "+
						getFormatedFloatLocal(inventory.getWidth())+" x "+
						// String.valueOf(inventory.getWidth())+" x "+
						(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(inventory.getLength())));
								// getFormatedDecimal(inventory.getLength())));
				lc.setParent(item);				

				// qty (s/l)
				lc = new Listcell();
				lc.setLabel(getFormatedInteger(inventory.getSheetQuantity()));
				lc.setParent(item);
				
				// qty (kg)
				lc = new Listcell();
				lc.setLabel(getFormatedFloatLocal(inventory.getWeightQuantity()));
						// getFormatedDecimal(inventory.getWeightQuantity()));
				lc.setParent(item);

				// packing
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryPacking().toString());
				lc.setParent(item);
				
				// location
				lc = new Listcell();
				lc.setLabel(inventory.getInventoryLocation().toString());
				lc.setParent(item);
				
				// coil no.
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getMarking());
				lc.setParent(item);
				
				// contract no.
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getContractNumber());
				lc.setParent(item);
				
				// received date.
				lc = new Listcell();
				lc.setLabel(dateToStringDisplay(asLocalDate(inventory.getReceiveDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// note
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getNote());
				lc.setParent(item);
				
				item.setValue(inventory);
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

	public void onClick$allTab(Event event) throws Exception {
		// check if checkbox is empty?
		if (searchTextbox.getValue().isEmpty()) {
			// load and display
			loadInventoryByPackingType();			
		} else {
			searchInventory();
		}		
	}

	/**
	 * Filter search result
	 * 
	 * @param inventoryPacking
	 * @return
	 */
	public List<Inventory> filterInventoryDisplay(InventoryPacking inventoryPacking) {
		// init the filter list
		getInventoryFilterList().clear();
		
		// filter search result
		for (Inventory inventory : getInventoryList()) {
			// check for coil inventory
			if (inventory.getInventoryPacking().equals(inventoryPacking)) {
				getInventoryFilterList().add(inventory);
			}
		}
		
		return getInventoryFilterList();
	}		
	
	public void onClick$coilTab(Event event) throws Exception {
		
		InventoryPacking[] packingType = { InventoryPacking.coil };
		
		if ((getInventoryListInfoType().compareTo(com.pyramix.swi.webui.inventory.InventoryListInfoType.CustomerOrder)==0)) {
			if (searchTextbox.getValue().isEmpty()) {
				
				// load coil inventory
				setInventoryList(
						getInventoryDao().findAllInventoryByPacking(InventoryStatus.ready, packingType));

				// remove selected inventory
				removeSelectedInventory();
				
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());
				
			} else {
				// set the model -- filter the inventory list for COIL
				// inventoryListbox.setModel(
				//		new ListModelList<Inventory>(filterInventoryDisplay(InventoryPacking.coil)));
				// render
				// inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
				
				// info result
				// displayInfoResult(getInventoryFilterList());
				searchInventory();
			}
		} else {
			// set the model -- filter the inventory list for COIL
			inventoryListbox.setModel(
					new ListModelList<Inventory>(filterInventoryDisplay(InventoryPacking.coil)));
			// render
			inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
			// info result
			displayInfoResult(getInventoryFilterList());			
		}
	}

	public void onClick$petianTab(Event event) throws Exception {

		InventoryPacking[] packingType = { InventoryPacking.petian };
		
		if ((getInventoryListInfoType().compareTo(com.pyramix.swi.webui.inventory.InventoryListInfoType.CustomerOrder)==0)) {

			if (searchTextbox.getValue().isEmpty()) {
				// load petian inventory
				setInventoryList(
						getInventoryDao().findAllInventoryByPacking(InventoryStatus.ready, packingType));
				
				// remove selected inventory
				removeSelectedInventory();
				
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(
						getInventoryListitemRenderer());			
				// info result
				displayInfoResult(getInventoryList());
			} else {
				// set the model -- filter the inventory list for PETIAN			
				// inventoryListbox.setModel(
				//		new ListModelList<Inventory>(filterInventoryDisplay(InventoryPacking.petian)));
				// render
				// inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
				
				// info result
				// displayInfoResult(getInventoryFilterList());
				searchInventory();
			}
		} else {
			// set the model -- filter the inventory list for PETIAN			
			inventoryListbox.setModel(
					new ListModelList<Inventory>(filterInventoryDisplay(InventoryPacking.petian)));
			// render
			inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
			// info result
			displayInfoResult(getInventoryFilterList());			
		}
		
	}

	public void onClick$lembaranTab(Event event) throws Exception {
		if (searchTextbox.getValue().isEmpty()) {
			// load lembaran inventory
			setInventoryList(
					getInventoryDao().findAllInventoryOfLembaranPacking(InventoryStatus.ready));
			
			// remove selected inventory
			removeSelectedInventory();
			
			// set the model
			inventoryListbox.setModel(
					new ListModelList<Inventory>(getInventoryList()));
			// render
			inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
			// info result
			displayInfoResult(getInventoryList());			
		} else {
			// set the model -- filter the inventory list for LEMBARAN			
			// inventoryListbox.setModel(
			//		new ListModelList<Inventory>(filterInventoryDisplay(InventoryPacking.lembaran)));
			// render
			// inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
			// info result
			// displayInfoResult(getInventoryFilterList());
			searchInventory();
		}

	}
	
	private void displayInfoResult(List<Inventory> inventoryList2) {
		BigDecimal totalKg = BigDecimal.ZERO;
		int totalItem = 0;
		for (Inventory inventory : inventoryList) {
			BigDecimal qtyKg = inventory.getWeightQuantity();
			
			totalKg = totalKg.add(qtyKg);
			totalItem = totalItem + 1;
		}
		
		BigDecimal roundTotal = totalKg.divide(new BigDecimal(1000), 0, RoundingMode.HALF_UP);
		
		infoResultlabel.setValue("Total: "+String.valueOf(totalItem)+" item - "+
				getFormatedInteger(roundTotal)+"MT");
		
	}

	public void onClick$selectButton(Event event) throws Exception {
		Inventory selInventory = 
				inventoryListbox.getSelectedItem().getValue();
		
		// send ON_OK event
		Events.sendEvent(
				Events.ON_OK, inventoryListInfoDialogWin, selInventory);
		
		// detach
		inventoryListInfoDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryListInfoDialogWin.detach();
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

	public InventoryListInfoType getInventoryListInfoType() {
		return InventoryListInfoType;
	}

	public void setInventoryListInfoType(InventoryListInfoType inventoryListInfoType) {
		InventoryListInfoType = inventoryListInfoType;
	}

	public List<Inventory> getInventoryFilterList() {
		return inventoryFilterList;
	}

	public void setInventoryFilterList(List<Inventory> inventoryFilterList) {
		this.inventoryFilterList = inventoryFilterList;
	}

	public InventoryData getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(InventoryData inventoryData) {
		this.inventoryData = inventoryData;
	}

	public List<Inventory> getSelectedInventory() {
		return selectedInventory;
	}

	public void setSelectedInventory(List<Inventory> selectedInventory) {
		this.selectedInventory = selectedInventory;
	}
}
