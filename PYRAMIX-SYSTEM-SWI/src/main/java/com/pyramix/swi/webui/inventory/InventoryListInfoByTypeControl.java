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
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryListInfoByTypeControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9073184601907289134L;

	private InventoryDao inventoryDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox inventoryListbox;
	// private Textbox searchTextbox;
	private Tabbox inventoryTypeTabbox;
	private Combobox packingCombobox;
	
	private List<Inventory> inventoryList, inventoryFilterList;
	// collect only inventoryCode with weight > 0
	private List<InventoryWeight> sortedInventoryWeightList;
	// controlled by the tab (coil, etc.)
	// private InventoryPacking[] selectedPackingType = { };

	// @SuppressWarnings("rawtypes")
	// private EventQueue eq;
	
	private final Logger log = Logger.getLogger(InventoryListInfoByTypeControl.class);
	
	public void onCreate$inventoryListInfoByTypeWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory");
		
		// inventory listbox -- if empty
		inventoryListbox.setEmptyMessage("Tidak ada - Kosong");
        
		setupInventoryTypeTabs();
		
		// setup packing selection
		setupPackingSelection();
		
		// init filter list
		setInventoryFilterList(new ArrayList<Inventory>());
		
		// eq = EventQueues.lookup("interactive", EventQueues.APPLICATION, true);
		// eq.subscribe(new EventListener<Event>() {

		//	@Override
		//	public void onEvent(Event arg0) throws Exception {
				// notify
		//		Clients.showNotification("Perubahan data Inventory. Layar ter-update.", "info", null, "bottom_right", 0);
				
				// update
		//		loadAllInventory();			
		//	}
		// });
		
		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();

		InventoryCode selInventoryCode = 
				(InventoryCode) inventoryTypeTabbox.getSelectedTab().getAttribute("inventoryCode");
		
		// load and display
		loadAllInventory(selInventoryCode);
		
	}

	private void setupInventoryTypeTabs() throws Exception {
		Tab tab;
		
		// create the tabs
		Tabs tabs = new Tabs();
		
		// all
/*		tab = new Tab("Semua");
		tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				setInventoryList(
						getInventoryDao().findAllInventoryByStatus(InventoryStatus.ready));
				// set the model
				inventoryListbox.setModel(
						new ListModelList<Inventory>(getInventoryList()));
				// render
				inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
				
				// info result
				displayInfoResult(getInventoryList());
			}
		});
		tab.setParent(tabs);
*/		
		sortedInventoryWeightList = getInventoryWeightFromInventory();
		
		for (InventoryWeight inventoryWeight : sortedInventoryWeightList) {
			// log.info(inventoryType.toString());
			
			tab = new Tab(inventoryWeight.getInventoryCode().getProductCode());
			tab.setAttribute("inventoryCode", inventoryWeight.getInventoryCode());
			tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					packingCombobox.setSelectedIndex(0);
					
					setInventoryList(
						getInventoryDao().findAllInventoryByInventoryCode(inventoryWeight.getInventoryCode(), InventoryStatus.ready));

					// set the model
					inventoryListbox.setModel(
							new ListModelList<Inventory>(getInventoryList()));
					// render
					inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
					
					// info result
					displayInfoResult(getInventoryList());
				}
				
			});
			tab.setParent(tabs);			
		}
		
		tabs.setParent(inventoryTypeTabbox);
	}	
	
	private List<InventoryWeight> getInventoryWeightFromInventory() throws Exception {
		// distinct inventoryCode in inventory list
        List<InventoryCode> inventoryCodeList = getInventoryDao().findDistinctInventoryCode();
        // inventoryCodeList.forEach(invtCode -> log.info(invtCode.toString()));
        
        List<InventoryWeight> inventoryWeights = new ArrayList<InventoryWeight>();
        
       	for (InventoryCode inventoryCode : inventoryCodeList) {
           	List<Inventory> inventoryListByCode =
           			getInventoryDao().findAllInventoryByInventoryCode(inventoryCode, InventoryStatus.ready);
			
           	BigDecimal invtWeightTotal = BigDecimal.ZERO;
           	for (Inventory inventory : inventoryListByCode) {
           		invtWeightTotal = invtWeightTotal.add(inventory.getWeightQuantity());
           	}
           	
           	if (invtWeightTotal.compareTo(BigDecimal.ZERO)==0) {
           		// do nothing
			} else {
				// String weightTotalString = toLocalFormatWithDecimal(invtWeightTotal, 3, "###.###.###,000");
				// log.info("Total Weight for: "+inventoryCode.getProductCode()+" is: "+weightTotalString);
				
				InventoryWeight inventoryWeight = new InventoryWeight();
				inventoryWeight.setInventoryCode(inventoryCode);
				inventoryWeight.setTotalWeight(invtWeightTotal);
				
				inventoryWeights.add(inventoryWeight);
			}
		}       	
       	
       	inventoryWeights.sort((o1, o2) -> {
       		return o2.getTotalWeight().compareTo(o1.getTotalWeight());
       	});
       	    	
       	inventoryWeights.forEach(inventoryWeight -> log.info(inventoryWeight.toString()));
        
        return inventoryWeights;
	}

	private void setupPackingSelection() {
		Comboitem comboitem;
		
		// all - semua
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setValue(null);
		comboitem.setParent(packingCombobox);	
		
		for (InventoryPacking inventoryPacking : InventoryPacking.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(inventoryPacking.name());
			comboitem.setValue(inventoryPacking);
			comboitem.setParent(packingCombobox);				
		}
		
		packingCombobox.setSelectedIndex(0);
	}

	public void onSelect$packingCombobox(Event event) throws Exception {
		InventoryPacking selInventoryPacking = packingCombobox.getSelectedItem().getValue();
		InventoryCode selInventoryCode = 
				(InventoryCode) inventoryTypeTabbox.getSelectedTab().getAttribute("inventoryCode");
		
		if (selInventoryPacking == null) {
			loadAllInventory(selInventoryCode);
		} else {
			loadAllInventoryByInventoryPacking(selInventoryCode, selInventoryPacking);
		}	
	}
	
	private void loadAllInventory(InventoryCode inventoryCode) throws Exception {
		// clear search text
		// searchTextbox.setValue("");
		
		// load all inventory
		setInventoryList(
				getInventoryDao().findAllInventoryByInventoryCode(inventoryCode, InventoryStatus.ready));
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
	}
	
	private void loadAllInventoryByInventoryPacking(InventoryCode inventoryCode, InventoryPacking inventoryPacking) throws Exception {
		// clear search text
		// searchTextbox.setValue("");
		
		// load all inventory
		setInventoryList(
				getInventoryDao().findAllInventoryByInventoryCodePacking(InventoryStatus.ready, inventoryCode, inventoryPacking));
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
		
	}
		
	private void setSearchTextboxEventListener() {

		// when user press 'Enter' key
		// searchTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {

		//	public void onEvent(Event arg0) throws Exception {
		//		searchInventory();
		//	}
		// });		
	}

	public void onClick$searchButton(Event event) throws Exception {
		searchInventory();
	}
	
	private void searchInventory() throws WrongValueException, Exception {
		// InventoryCode selInventoryCode = 
		//		(InventoryCode) inventoryTypeTabbox.getSelectedTab().getAttribute("inventoryCode");
		
		// if (searchTextbox.getValue().isEmpty()) { 
		//	loadAllInventory(selInventoryCode);
				
		//	return; 
		// }

		// InventoryPacking[] selectedPackingType = { };
			
		// search
		// setInventoryList(
		//		getInventoryDao().searchInventory(selInventoryCode.getProductCode()+" "+searchTextbox.getValue(), selectedPackingType));
		
		// set the model
		// inventoryListbox.setModel(
		//		new ListModelList<Inventory>(getInventoryList()));
		// render
		// inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
		// info result
		// displayInfoResult(getInventoryList());		

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
				
				// code
				lc = initInventoryCode(new Listcell(), inventory.getInventoryCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);

				// specification
				lc = new Listcell();
				lc.setLabel(getFormatedFloatLocal(inventory.getThickness())+" x "+
						getFormatedFloatLocal(inventory.getWidth())+" x "+
						(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : getFormatedFloatLocal(inventory.getLength())));
				lc.setParent(item);				

				// qty (s/l)
				lc = new Listcell();
				lc.setLabel(getFormatedInteger(inventory.getSheetQuantity()));
				lc.setParent(item);
				
				// qty (kg)
				lc = new Listcell();
				lc.setLabel(getFormatedFloatLocal(inventory.getWeightQuantity()));
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
				lc.setLabel(inventory.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// contract no.
				lc = new Listcell();
				lc.setStyle("white-space: nowrap;");
				lc.setLabel(inventory.getContractNumber());
				lc.setParent(item);
				
				// Tgl.Penerimaan
				lc = new Listcell();
				lc.setLabel(dateToStringDisplay(asLocalDate(inventory.getReceiveDate()), getLongDateFormat()));
				lc.setParent(item);
/*								
				// Process
				String processInfo = null;
				if (inventory.getInventoryProcess()!=null) {
					Inventory inventoryByProxy = getInventoryDao().getInventoryProcessByProxy(inventory.getId());
					
					processInfo = inventoryByProxy.getInventoryProcess().getProcessNumber().getSerialComp();
				}
				lc = new Listcell(processInfo);
				lc.setParent(item);
				
				// BukaPeti
				String bukapetiInfo = null;
				if (inventory.getInventoryBukapeti()!=null) {
					Inventory inventoryByProxy = getInventoryDao().getInventoryBukaPetiByProxy(inventory.getId());
					
					bukapetiInfo = inventoryByProxy.getInventoryBukapeti().getBukapetiNumber().getSerialComp();
				}
				lc = new Listcell(bukapetiInfo);
				lc.setParent(item);

*/				// note
				lc = new Listcell();
				lc.setLabel(inventory.getNote());
				lc.setStyle("white-space:nowrap;");
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
						
						inventoryEditDialog.doModal();
					}
					
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

		};
	}

/*	public void onClick$besiPutihTab(Event event) throws Exception {
		// Plat Besi Putih (CC) id#1
		Long id = new Long(1);
		
		InventoryType inventoryType = 
				getInventoryDao().findInventoryTypeById(id);
		
		setInventoryList(
				getInventoryDao().findInventoryByProductType(inventoryType, InventoryStatus.ready));
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
		
		// info result
		displayInfoResult(getInventoryList());
	}
*/	
/*	public void onClick$newButton(Event event) throws Exception {
		// clear search text
		searchTextbox.setValue("");
		
		Window inventoryAddDialog = (Window) Executions.createComponents(
				"/inventory/InventoryAddDialog.zul", null, null);
 
		inventoryAddDialog.doModal();
	}		
*/
	private void displayInfoResult(List<Inventory> inventoryList) {
		BigDecimal totalKg = BigDecimal.ZERO;
		int totalItem = 0;
		for (Inventory inventory : inventoryList) {
			BigDecimal qtyKg = inventory.getWeightQuantity();
			
			totalKg = totalKg.add(qtyKg);
			totalItem = totalItem + 1;
		}
						
		BigDecimal totalMT = totalKg.divide(new BigDecimal(1000), 3, RoundingMode.CEILING);
		log.info("Total: "+totalMT+" MT");
		
		infoResultlabel.setValue("Total: "+String.valueOf(totalItem)+" item - "+
				toLocalFormatWithDecimal(totalMT, 3, "###.###.###,000")+"MT");
		
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

class InventoryWeight {
	private InventoryCode inventoryCode;
	private InventoryPacking inventoryPacking;
	private BigDecimal totalWeight;
	
	@Override
	public String toString() {
		return "InventoryWeight [inventoryCode=" + inventoryCode.getProductCode() + ", inventoryPacking=" + inventoryPacking
				+ ", totalWeight=" + totalWeight + "]";
	}
	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}
	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}
	public InventoryPacking getInventoryPacking() {
		return inventoryPacking;
	}
	public void setInventoryPacking(InventoryPacking inventoryPacking) {
		this.inventoryPacking = inventoryPacking;
	}
	public BigDecimal getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}
}
