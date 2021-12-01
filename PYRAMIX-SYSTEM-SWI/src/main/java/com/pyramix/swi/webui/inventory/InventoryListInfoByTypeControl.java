package com.pyramix.swi.webui.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.InventoryType;
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
	private Textbox searchTextbox;
	private Tabbox inventoryTypeTabbox;
	
	private List<Inventory> inventoryList, inventoryFilterList;
	private List<InventoryType> sortedInventoryTypeList;
	// controlled by the tab (coil, etc.)
	// private InventoryPacking[] selectedPackingType = { };

	// @SuppressWarnings("rawtypes")
	// private EventQueue eq;
	
	private final Logger log = Logger.getLogger(InventoryListInfoByTypeControl.class);
	
	public void onCreate$inventoryListInfoByTypeWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory");
		
		// inventory listbox -- if empty
		inventoryListbox.setEmptyMessage("Tidak ada - Kosong");
        
		// setupInventoryTypeTabs();
		
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

		// testing only
		sortedInventoryTypeList = getInventoryTypeFromInventory();

		
		// load and display
		loadAllInventory();
		
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
		sortedInventoryTypeList = getInventoryTypeFromInventory();
		
		for (InventoryType inventoryType : sortedInventoryTypeList) {
			// log.info(inventoryType.toString());
			
			tab = new Tab(inventoryType.getProductType());
			tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
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
				
			});
			tab.setParent(tabs);			
		}
		
		tabs.setParent(inventoryTypeTabbox);
	}

	private List<InventoryType> getInventoryTypeFromInventory() throws Exception {
		// distinct inventoryCode in inventory list
        List<InventoryCode> inventoryCodeList = inventoryDao.findDistinctInventoryCode();
        inventoryCodeList.forEach(invtCode -> log.info(invtCode.toString()));
        
        
        Set<InventoryType> inventoryTypeSet = new HashSet<InventoryType>();

        // use each inventoryCode to get the inventoryType
        for (InventoryCode inventoryCode : inventoryCodeList) {
        	// log.info(inventoryCode.getId()+"-"+inventoryCode.getInventoryType());
        	
        	inventoryTypeSet.add(inventoryCode.getInventoryType());
        }
        
        // https://studiofreya.com/java/how-to-sort-a-set-in-java-example/
        List<InventoryType> sortedType = inventoryTypeSet.stream().collect(Collectors.toList());
        Collections.sort(sortedType, 
        		(invtType01, invtType02) -> invtType01.getProductType().compareTo(invtType02.getProductType()));
        
        return sortedType;
	}

	private void loadAllInventory() throws Exception {
		// clear search text
		searchTextbox.setValue("");
		
		// load all inventory
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
				
			return; 
		}

		InventoryPacking[] selectedPackingType = { };
			
		// search
		setInventoryList(
				getInventoryDao().searchInventory(searchTextbox.getValue(), selectedPackingType));
		
		// set the model
		inventoryListbox.setModel(
				new ListModelList<Inventory>(getInventoryList()));
		// render
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
			
		// info result
		displayInfoResult(getInventoryList());		

/*		try {
			
		} catch (Exception e) {
			Messagebox.show("Pencarian Inventory mengalami kesalahan (error). "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);	
		}
*/	
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
		
		BigDecimal roundTotal = totalKg.divide(new BigDecimal(1000), 0, RoundingMode.HALF_UP);
		
		infoResultlabel.setValue("Total: "+String.valueOf(totalItem)+" item - "+
				getFormatedInteger(roundTotal)+"MT");
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
