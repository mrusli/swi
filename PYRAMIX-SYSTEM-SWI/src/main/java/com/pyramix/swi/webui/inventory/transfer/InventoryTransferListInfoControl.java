package com.pyramix.swi.webui.inventory.transfer;

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
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.inventory.transfer.dao.InventoryTransferDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryTransferListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2497112113857166134L;

	private InventoryTransferDao inventoryTransferDao;
	private InventoryDao inventoryDao;
	
	private Label formTitleLabel, infoTransferlabel;
	private Listbox transferListbox;
	private Tabbox transferPeriodTabbox;
	
	private List<InventoryTransfer> inventoryTransferList;

	private static final int WORK_DAY_WEEK		= 6;
	private static final int DEFAULT_TAB_INDEX 	= 0;
	
	public void onCreate$inventoryTransferListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory - Transfer");
		
		listBySelection(DEFAULT_TAB_INDEX);
	}
	
	public void onSelect$transferPeriodTabbox(Event event) throws Exception {
		int selIndex = transferPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;

		switch (selIndex) {
		case 0: // Semua
			// load
			loadInventoryTransferList();			
			// list
			listInventoryTransfer();
			
			break;
		case 1: // Hari ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());

			// list by date
			loadInventoryTransferListByDate(startDate, endDate);
			// list
			listInventoryTransfer();
			
			break;
		case 2: // Minngu ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));

			// list by date
			loadInventoryTransferListByDate(startDate, endDate);
			// list
			listInventoryTransfer();
			
			break;
		case 3: // Bulan ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));

			// list by date
			loadInventoryTransferListByDate(startDate, endDate);
			// list
			listInventoryTransfer();

			break;
		default:
			break;
		}
	}
	
	private void loadInventoryTransferListByDate(Date startDate, Date endDate) throws Exception {
		// set desc to true
		boolean desc = true;
		
		// load
		setInventoryTransferList(
				getInventoryTransferDao().findAllInventoryTransfer_By_DateRange(desc, startDate, endDate));
	}

	private void loadInventoryTransferList() throws Exception {
		// set desc to true
		boolean desc = true;
		
		// load
		setInventoryTransferList(
				getInventoryTransferDao().findAllInventoryTransfer(desc));
	}

	private void listInventoryTransfer() {
		// Total: 21 Transfer
		infoTransferlabel.setValue("Total: "+getInventoryTransferList().size()+" Transfer");
		
		transferListbox.setModel(
				new ListModelList<InventoryTransfer>(getInventoryTransferList()));
		transferListbox.setItemRenderer(getInventoryTransferListitemRenderer());
	}

	private ListitemRenderer<InventoryTransfer> getInventoryTransferListitemRenderer() {
		
		return new ListitemRenderer<InventoryTransfer>() {
			
			@Override
			public void render(Listitem item, InventoryTransfer transfer, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Order
				lc = new Listcell(dateToStringDisplay(asLocalDate(transfer.getOrderDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Transfer-No.
				lc = new Listcell(transfer.getTransferNumber().getSerialComp());
				lc.setParent(item);
				
				InventoryTransfer inventoryTransferByProxy = getInventoryTransferDao().getInventoryTransferFromCoByProxy(transfer.getId());
				
				// Transfer-Dari
				lc = new Listcell(inventoryTransferByProxy.getTransferFromCo()==null ?
						"" : inventoryTransferByProxy.getTransferFromCo().getCompanyDisplayName());
				lc.setParent(item);
				
				// Tiba-Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(transfer.getCompleteDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Status
				lc = new Listcell(transfer.getInventoryTransferStatus().toString());
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(transfer.getNote());
				lc.setParent(item);
				
				// User
				lc = new Listcell();
				lc.setParent(item);
				
				// Edit / View - depnding on the status
				lc = (transfer.getInventoryTransferStatus().equals(InventoryTransferStatus.proses)) || 
						(transfer.getInventoryTransferStatus().equals(InventoryTransferStatus.selesai)) ?
							initViewTransfer(new Listcell(), transfer) :
							initEditTransfer(new Listcell(), transfer);
				lc.setParent(item);
				
				// Print
				lc = initPrintTransfer(new Listcell(), transfer);
				lc.setParent(item);
				
				// Penyelesaian
				lc = initCompleteTransfer(new Listcell(), transfer);
				lc.setParent(item);
				
			}

			private Listcell initViewTransfer(Listcell listcell, InventoryTransfer transfer) {
				Button viewButton = new Button();

				viewButton.setLabel("View");
				viewButton.setClass("inventoryEditButton");
				viewButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryTransfer> args =
							Collections.singletonMap("inventoryTransfer", transfer);
						
						Window viewTransferWin = 
								(Window) Executions.createComponents("/inventory/transfer/InventoryTransferViewDialog.zul", null, args);
						
						viewTransferWin.doModal();
						
					}
				});
				viewButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initEditTransfer(Listcell listcell, InventoryTransfer transfer) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryTransfer> args =
								Collections.singletonMap("inventoryTransfer", transfer);
						
						Window editTransferWin = 
								(Window) Executions.createComponents(
										"/inventory/transfer/InventoryTransferEditDialog.zul", null, args);
						
						editTransferWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryTransfer inventoryTransfer = 
										(InventoryTransfer) event.getData();
								
								// update
								getInventoryTransferDao().update(inventoryTransfer);
								
								// current selection
								int selIndex = transferPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						editTransferWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initPrintTransfer(Listcell listcell, InventoryTransfer transfer) {
				Button printButton = new Button();

				printButton.setLabel("Print");
				printButton.setClass("inventoryEditButton");
				printButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryTransfer> args = 
								Collections.singletonMap("inventoryTransfer", transfer);
						
						Window inventoryTransferPrintWin = 
								(Window) Executions.createComponents("/inventory/transfer/print/TransferPrint.zul", null, args);
						
						inventoryTransferPrintWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryTransfer inventoryTransfer = 
										(InventoryTransfer) event.getData();
								// update
								getInventoryTransferDao().update(inventoryTransfer);
								
								// current selection
								int selIndex = transferPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						inventoryTransferPrintWin.doModal();						
					}
				});
				printButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initCompleteTransfer(Listcell listcell, InventoryTransfer transfer) {
				Button completeButton = new Button();

				completeButton.setLabel("Penyelesaian");
				completeButton.setClass("inventoryEditButton");
				completeButton.setDisabled(transfer.getInventoryTransferStatus().equals(InventoryTransferStatus.selesai));
				completeButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryTransfer> args = 
								Collections.singletonMap("inventoryTransfer", transfer);
								
						Window inventoryTransferCompletedWin =
								(Window) Executions.createComponents("/inventory/transfer/TransferCompletedDialog.zul", null, args);
						
						inventoryTransferCompletedWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryTransfer inventoryTransfer = (InventoryTransfer) event.getData();
								
								// update
								getInventoryTransferDao().update(inventoryTransfer);

								// current selection
								int selIndex = transferPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						inventoryTransferCompletedWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@SuppressWarnings("unchecked")
							@Override
							public void onEvent(Event event) throws Exception {
								List<InventoryTransferEndProduct> endProductList = 
										(List<InventoryTransferEndProduct>) event.getData();
								
								for (InventoryTransferEndProduct endProduct : endProductList) {
									Inventory inventory = getInventoryDao().findInventoryById(endProduct.getInventoryIdRef());
									
									inventory.setMarking(endProduct.getMarking());
									inventory.setThickness(endProduct.getThickness());
									inventory.setWidth(endProduct.getWidth());
									inventory.setLength(endProduct.getLength());
									inventory.setSheetQuantity(endProduct.getSheetQuantity());
									inventory.setWeightQuantity(endProduct.getWeightQuantity());
									// change the status to ready
									inventory.setInventoryStatus(InventoryStatus.ready);	
									
									inventory.setInventoryLocation(endProduct.getInventoryLocation());
									
									getInventoryDao().update(inventory);
								}
							}
						});
						
						inventoryTransferCompletedWin.doModal();
								
					}
				});
				completeButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addTransferButton(Event event) throws Exception {
		Map<String, InventoryTransfer> args =
				Collections.singletonMap("inventoryTransfer", new InventoryTransfer());
		
		Window transferDialogWin = 
				(Window) Executions.createComponents("/inventory/transfer/InventoryTransferDialog.zul", null, args);
		
		transferDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				InventoryTransfer inventoryTransfer = (InventoryTransfer) event.getData();
				
				// save
				getInventoryTransferDao().save(inventoryTransfer);
				
				// current selection
				int selIndex = transferPeriodTabbox.getSelectedIndex();
				
				// list according to selection
				listBySelection(selIndex);
			}
		});
		
		transferDialogWin.doModal();
	}
	
	public InventoryTransferDao getInventoryTransferDao() {
		return inventoryTransferDao;
	}

	public void setInventoryTransferDao(InventoryTransferDao inventoryTransferDao) {
		this.inventoryTransferDao = inventoryTransferDao;
	}

	public List<InventoryTransfer> getInventoryTransferList() {
		return inventoryTransferList;
	}

	public void setInventoryTransferList(List<InventoryTransfer> inventoryTransferList) {
		this.inventoryTransferList = inventoryTransferList;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
}
