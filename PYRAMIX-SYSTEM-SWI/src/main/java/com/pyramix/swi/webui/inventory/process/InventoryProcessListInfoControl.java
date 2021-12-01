package com.pyramix.swi.webui.inventory.process;

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
import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
// import com.pyramix.swi.domain.inventory.process.completed.InventoryProcessCompleted;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.inventory.process.print.CuttingOrderData;

public class InventoryProcessListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8365282398568020647L;

	private InventoryProcessDao inventoryProcessDao;
	private InventoryDao inventoryDao;
	
	private Label formTitleLabel, infoProcesslabel;
	private Listbox processListbox;
	private Tabbox processPeriodTabbox;
	
	private List<InventoryProcess> inventoryProcessList;
	
	private static final int WORK_DAY_WEEK		= 6;
	private static final int DEFAULT_TAB_INDEX 	= 1;

	public void onCreate$inventoryProcessListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Inventory - Proses");

		processPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list according to default tab
		listBySelection(processPeriodTabbox.getSelectedIndex());
	}

	public void onSelect$processPeriodTabbox(Event event) throws Exception {
		// selected index
		int selIndex = processPeriodTabbox.getSelectedIndex();
		
		// list according to selected index
		listBySelection(selIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;

		switch (selIndex) {
		case 0: // semua
			// load
			loadInventoryProcessList();			
			// list
			listInventoryProcess();
			
			break;
		case 1: // Hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());

			// load
			loadInventoryProcessByDate(startDate, endDate);
			// list
			listInventoryProcess();
			
			break;
		case 2: // Minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			// load
			loadInventoryProcessByDate(startDate, endDate);
			// list
			listInventoryProcess();

			break;
		case 3: // Bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			// load
			loadInventoryProcessByDate(startDate, endDate);
			// list
			listInventoryProcess();
			
			break;
		default:
			break;
		}
	}

	private void loadInventoryProcessByDate(Date startDate, Date endDate) {
		// display in desceding order (latest one first)
		boolean desc = true;

		setInventoryProcessList(
				getInventoryProcessDao().findAllInventoryProcess_By_DateRange(desc, startDate, endDate));
	}

	private void loadInventoryProcessList() throws Exception {
		// display in desceding order (latest one first)
		boolean desc = true;
		
		setInventoryProcessList(
				getInventoryProcessDao().findAllInventoryProcess(desc));
	}

	private void listInventoryProcess() {
		processListbox.setModel(
				new ListModelList<InventoryProcess>(getInventoryProcessList()));
		processListbox.setItemRenderer(
				getInventoryProcessListitemRenderer());
		
		updateInfoProcessLabel(processListbox.getItems().size());
	}

	private void updateInfoProcessLabel(int processNum) {
		infoProcesslabel.setValue("Total: "+processNum+" Proses");
	}	
	
	private ListitemRenderer<InventoryProcess> getInventoryProcessListitemRenderer() {
		
		return new ListitemRenderer<InventoryProcess>() {
			
			@Override
			public void render(Listitem item, InventoryProcess inventoryProcess, int index) throws Exception {
				Listcell lc;
				
				// Tgl
				lc = new Listcell(dateToStringDisplay(asLocalDate(inventoryProcess.getOrderDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Proses-No.
				lc = new Listcell(inventoryProcess.getProcessNumber().getSerialComp());
				lc.setParent(item);
				
				// Selesai-Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(inventoryProcess.getCompleteDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Lokasi-Proses
				InventoryProcess inventoryProcessByProxy = 
						getInventoryProcessDao().getProcessedByCoByProxy(inventoryProcess.getId());
				
				lc = new Listcell(inventoryProcessByProxy .getProcessedByCo().getCompanyDisplayName());
				lc.setParent(item);
				
				// Status
				lc = new Listcell(inventoryProcess.getProcessStatus().toString());
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(inventoryProcess.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// User
				lc = new Listcell();
				lc.setParent(item);
				
				// Edit
				lc = (inventoryProcess.getProcessStatus().compareTo(InventoryProcessStatus.proses)==0) || 
					 (inventoryProcess.getProcessStatus().compareTo(InventoryProcessStatus.selesai)==0) ? 
						initView(new Listcell(), inventoryProcess):
						initEdit(new Listcell(), inventoryProcess);
				lc.setParent(item);
				
				// print
				lc = initPrint(new Listcell(), inventoryProcess);
				lc.setParent(item);
				
				// selesai
				lc = initProcessCompleted(new Listcell(), inventoryProcess);
				lc.setParent(item);
			}

			private Listcell initView(Listcell listcell, InventoryProcess inventoryProcess) {
				Button editButton = new Button();

				editButton.setLabel("View");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						InventoryProcessData inventoryProcessData = new InventoryProcessData();
						inventoryProcessData.setInventoryProcess(inventoryProcess);
						inventoryProcessData.setPageMode(PageMode.VIEW);
						
						Map<String, InventoryProcessData> args = 
								Collections.singletonMap("inventoryProcessData", inventoryProcessData);
						
						Window inventoryProcessViewWin = 
								(Window) Executions.createComponents("/inventory/process/InventoryProcessViewDialog.zul", null, args);
						
						inventoryProcessViewWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initEdit(Listcell listcell, InventoryProcess inventoryProcess) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						InventoryProcessData inventoryProcessData = new InventoryProcessData();
						inventoryProcessData.setInventoryProcess(inventoryProcess);
						inventoryProcessData.setPageMode(PageMode.EDIT);
						
						Map<String, InventoryProcessData> args = 
								Collections.singletonMap("inventoryProcessData", inventoryProcessData);
						
						Window inventoryProcessEditWin = 
								(Window) Executions.createComponents("/inventory/process/InventoryProcessEditDialog.zul", null, args);
						
						inventoryProcessEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryProcess inventoryProcess = (InventoryProcess) event.getData();
								
								// update
								getInventoryProcessDao().update(inventoryProcess);
								
								// current selection
								int selIndex = processPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						inventoryProcessEditWin.addEventListener(Events.ON_CANCEL, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// current selection
								int selIndex = processPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						inventoryProcessEditWin.doModal();
					}

				});
				editButton.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initPrint(Listcell listcell, InventoryProcess inventoryProcess) {
				Button editButton = new Button();

				editButton.setLabel("Print");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						CuttingOrderData cuttingOrderData = new CuttingOrderData();
						cuttingOrderData.setInventoryProcess(inventoryProcess);
						
						Map<String, CuttingOrderData> args = 
								Collections.singletonMap("cuttingOrderData", cuttingOrderData);
						
						Window cuttingOrderPrintWin = 
								(Window) Executions.createComponents("/inventory/process/print/CuttingOrderPrint.zul", null, args);
						
						cuttingOrderPrintWin.doModal();
						
						// set - change status to 'proses'
						// inventoryProcess.setProcessStatus(InventoryProcessStatus.proses);
						// update
						getInventoryProcessDao().update(inventoryProcess);

						// current selection
						int selIndex = processPeriodTabbox.getSelectedIndex();
						// list according to selection
						listBySelection(selIndex);
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initProcessCompleted(Listcell listcell, InventoryProcess inventoryProcess) {
				Button completedButton = new Button();

				completedButton.setLabel("Penyelesaian");
				completedButton.setClass("inventoryEditButton");
				// completedButton.setDisabled(
				//		inventoryProcess.getProcessStatus().compareTo(InventoryProcessStatus.proses)!=0);
				completedButton.setVisible(
						!(inventoryProcess.getProcessStatus().compareTo(InventoryProcessStatus.selesai)==0));
						
				completedButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						ProcessCompletedData processCompletedData = new ProcessCompletedData();
						processCompletedData.setInventoryProcess(inventoryProcess);
						processCompletedData.setPageMode(PageMode.NEW);							
						
						Map<String, ProcessCompletedData> args =
								Collections.singletonMap("processCompletedData", processCompletedData);
						
						Window completedDialogWin = (Window) Executions.createComponents(
							"/inventory/process/ProcessCompletedDialog.zul", null, args);

						completedDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryProcess inventoryProcess = (InventoryProcess) event.getData();
								
								// update
								getInventoryProcessDao().update(inventoryProcess);
								
								// current selection
								int selIndex = processPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);
							}
						});
						
						completedDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@SuppressWarnings("unchecked")
							@Override
							public void onEvent(Event event) throws Exception {
								List<Inventory> inventoryList = (List<Inventory>) event.getData();

								// save
								getInventoryDao().save(inventoryList);
								
								// re-index -- because indexer is set to normal
								getInventoryDao().createIndexer(); 

								// current selection
								int selIndex = processPeriodTabbox.getSelectedIndex();
								
								// list according to selection
								listBySelection(selIndex);								
								
							}
						});
						
						completedDialogWin.doModal();
					}
				});
				completedButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addProcessButton(Event event) throws Exception {
		InventoryProcessData inventoryProcessData = new InventoryProcessData();
		inventoryProcessData.setInventoryProcess(new InventoryProcess());
		inventoryProcessData.setPageMode(PageMode.NEW);
		
		Map<String, InventoryProcessData> args = 
				Collections.singletonMap("inventoryProcessData", inventoryProcessData);
		
		Window inventoryProcessCreateWin = 
				(Window) Executions.createComponents("/inventory/process/InventoryProcessDialog.zul", null, args);
		
		inventoryProcessCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				InventoryProcess inventoryProcess = (InventoryProcess) event.getData();
				// save
				getInventoryProcessDao().save(inventoryProcess);				
				// current selection
				int selIndex = processPeriodTabbox.getSelectedIndex();				
				// list according to selection
				listBySelection(selIndex);
			}
		});
		
		inventoryProcessCreateWin.doModal();
	}
	
	
	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}

	public List<InventoryProcess> getInventoryProcessList() {
		return inventoryProcessList;
	}

	public void setInventoryProcessList(List<InventoryProcess> inventoryProcessList) {
		this.inventoryProcessList = inventoryProcessList;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
}
