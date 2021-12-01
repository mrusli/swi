package com.pyramix.swi.webui.inventory.bukapeti;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiStatus;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryBukaPetiListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5280737723619974891L;

	private InventoryBukaPetiDao inventoryBukapetiDao;
	private InventoryDao inventoryDao;
	
	private Label formTitleLabel;
	private Listbox bukapetiListbox;
	private Tabbox bukapetiPeriodTabbox;
	
	private List<InventoryBukaPeti> inventoryBukapetiList;
	
	private static final int WORK_DAY_WEEK		= 6;
	private static final int DEFAULT_TAB_INDEX 	= 0;
	
	public void onCreate$inventoryBukapetiListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory - Buka Peti");

		// selected tab
		bukapetiPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list
		listBySelection(DEFAULT_TAB_INDEX);
	}

	public void onSelect$bukapetiPeriodTabbox(Event event) throws Exception {
		// selected tab
		int selIndex = bukapetiPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // Semua
			// load
			loadInventoryBukapetiList();			
			// list
			listInventoryBukapeti();
			
			break;
		case 1: // Hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());

			// load
			loadInventoryBukapetiListByDate(startDate, endDate);			
			// list
			listInventoryBukapeti();
			
			break;
		case 2: // Minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));

			// load
			loadInventoryBukapetiListByDate(startDate, endDate);			
			// list
			listInventoryBukapeti();

			break;
		case 3: // Bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));

			// load
			loadInventoryBukapetiListByDate(startDate, endDate);			
			// list
			listInventoryBukapeti();

			break;
		default:
			break;
		}
	}

	private void loadInventoryBukapetiList() throws Exception {
		// set descending
		boolean desc = true;
		
		setInventoryBukapetiList(
				getInventoryBukapetiDao().findAllInventoryBukapeti(desc));
	}

	private void loadInventoryBukapetiListByDate(Date startDate, Date endDate) throws Exception {
		// set desc to true
		boolean desc = true;

		setInventoryBukapetiList(
				getInventoryBukapetiDao().findAllInventoryBukapeti_By_DateRange(desc, startDate, endDate));
	}

	private void listInventoryBukapeti() {
		bukapetiListbox.setModel(
				new ListModelList<InventoryBukaPeti>(getInventoryBukapetiList()));
		bukapetiListbox.setItemRenderer(getInventoryBukapetiListitemRenderer());
	}

	private ListitemRenderer<InventoryBukaPeti> getInventoryBukapetiListitemRenderer() {

		return new ListitemRenderer<InventoryBukaPeti>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPeti bukapeti, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Order
				lc = new Listcell(dateToStringDisplay(asLocalDate(bukapeti.getOrderDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// BukaPeti-No.
				lc = new Listcell(bukapeti.getBukapetiNumber().getSerialComp());
				lc.setParent(item);
				
				// Selesai-Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(bukapeti.getCompleteDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Status
				lc = new Listcell(bukapeti.getInventoryBukapetiStatus().toString());
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(bukapeti.getNote());
				lc.setParent(item);
				
				// User
				lc = new Listcell();
				lc.setParent(item);
				
				// Edit / View - depending on the status
				lc = (bukapeti.getInventoryBukapetiStatus().compareTo(InventoryBukaPetiStatus.proses)==0) || 
					 (bukapeti.getInventoryBukapetiStatus().compareTo(InventoryBukaPetiStatus.selesai)==0) ?
						initViewBukapeti(new Listcell(), bukapeti) :
						initEditBukapeti(new Listcell(), bukapeti);
				lc.setParent(item);
				
				// Print
				lc = initPrintBukapeti(new Listcell(), bukapeti);
				lc.setParent(item);
				
				// Selesai
				lc = initCompleteBukapeti(new Listcell(), bukapeti);
				lc.setParent(item);
			}

			private Listcell initViewBukapeti(Listcell listcell, InventoryBukaPeti bukapeti) {
				Button viewButton = new Button();

				viewButton.setLabel("View");
				viewButton.setClass("inventoryEditButton");
				viewButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryBukaPeti> args =
								Collections.singletonMap("inventoryBukapeti", bukapeti);
						
						Window viewInventoryBukapetiWin =
								(Window) Executions.createComponents("/inventory/bukapeti/InventoryBukaPetiViewDialog.zul", null, args);
						
						viewInventoryBukapetiWin.doModal();
					}
				});
				viewButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initEditBukapeti(Listcell listcell, InventoryBukaPeti bukapeti) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryBukaPeti> args =
								Collections.singletonMap("inventoryBukapeti", bukapeti);
						
						Window editInventoryBukapetiWin = 
								(Window) Executions.createComponents("/inventory/bukapeti/InventoryBukaPetiEditDialog.zul", null, args);
						
						editInventoryBukapetiWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryBukaPeti inventoryBukapeti = 
										(InventoryBukaPeti) event.getData();
								
								// update
								getInventoryBukapetiDao().update(inventoryBukapeti);
								
								// current selection
								int selIndex = bukapetiPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						editInventoryBukapetiWin.doModal();
					}
				});
				editButton.setParent(listcell);

				return listcell;
			}

			private Listcell initPrintBukapeti(Listcell listcell, InventoryBukaPeti bukapeti) {
				Button printButton = new Button();

				printButton.setLabel("Print");
				printButton.setClass("inventoryEditButton");
				printButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryBukaPeti> args =
								Collections.singletonMap("inventoryBukapeti", bukapeti);
						
						Window printInventoryBukapetiWin =
								(Window) Executions.createComponents("/inventory/bukapeti/print/BukaPetiPrint.zul", null, args);
						
						printInventoryBukapetiWin.doModal();
						
						if (bukapeti.getInventoryBukapetiStatus().compareTo(InventoryBukaPetiStatus.selesai)==0) {
							// do nothing
						} else {
							// set - change status to 'proses'
							bukapeti.setInventoryBukapetiStatus(InventoryBukaPetiStatus.proses);
							// update
							getInventoryBukapetiDao().update(bukapeti);
							// re-load
							loadInventoryBukapetiList();
							// list
							listInventoryBukapeti();
						}
					}
				});
				printButton.setParent(listcell);

				return listcell;
			}

			private Listcell initCompleteBukapeti(Listcell listcell, InventoryBukaPeti bukapeti) {
				Button completeButton = new Button();

				completeButton.setLabel("Penyelesaian");
				completeButton.setClass("inventoryEditButton");
				completeButton.setDisabled(bukapeti.getInventoryBukapetiStatus().compareTo(InventoryBukaPetiStatus.selesai)==0);
				completeButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryBukaPeti> args =
								Collections.singletonMap("inventoryBukapeti", bukapeti);

						Window penyelesaianInventoryBukapetiWin =
								(Window) Executions.createComponents("/inventory/bukapeti/BukaPetiCompletedDialog.zul", null, args);
						
						penyelesaianInventoryBukapetiWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryBukaPeti inventoryBukapeti = 
										(InventoryBukaPeti) event.getData();
								
								// update
								getInventoryBukapetiDao().update(inventoryBukapeti);

								// current selection
								int selIndex = bukapetiPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						penyelesaianInventoryBukapetiWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@SuppressWarnings("unchecked")
							@Override
							public void onEvent(Event event) throws Exception {
								List<Inventory> inventoryList = (List<Inventory>) event.getData();								

								// save or update -- depending on the code and size
								getInventoryDao().saveOrUpdate(inventoryList);
								
								// re-index -- because indexer is set to normal
								getInventoryDao().createIndexer(); 
								
							}
						});
						
						penyelesaianInventoryBukapetiWin.doModal();
						
					}
				});
				completeButton.setParent(listcell);

				return listcell;
			}

		};
	}

	public void onClick$addBukapetiButton(Event event) throws Exception {
		Map<String, InventoryBukaPeti> args = 
				Collections.singletonMap("inventoryBukapeti", new InventoryBukaPeti());
		
		Window inventoryBukapetiCreateWin =
				(Window) Executions.createComponents("/inventory/bukapeti/InventoryBukaPetiDialog.zul", null, args);
		
		inventoryBukapetiCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				InventoryBukaPeti inventoryBukapeti = (InventoryBukaPeti) event.getData();
				
				// save
				getInventoryBukapetiDao().save(inventoryBukapeti);
				
				// current selection
				int selIndex = bukapetiPeriodTabbox.getSelectedIndex();
				
				// list according to selection
				listBySelection(selIndex);
			}
		});
		
		inventoryBukapetiCreateWin.doModal();
	}
	
	public List<InventoryBukaPeti> getInventoryBukapetiList() {
		return inventoryBukapetiList;
	}

	public void setInventoryBukapetiList(List<InventoryBukaPeti> inventoryBukapetiList) {
		this.inventoryBukapetiList = inventoryBukapetiList;
	}

	public InventoryBukaPetiDao getInventoryBukapetiDao() {
		return inventoryBukapetiDao;
	}

	public void setInventoryBukapetiDao(InventoryBukaPetiDao inventoryBukapetiDao) {
		this.inventoryBukapetiDao = inventoryBukapetiDao;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

}
