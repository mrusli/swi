package com.pyramix.swi.webui.voucher;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherGiroReceiptControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4485856587623282544L;

	private VoucherGiroReceiptDao voucherGiroReceiptDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox voucherGiroReceiptListbox;
	private Tabbox voucherGiroReceiptPeriodTabbox;
	private Datebox startDatebox, endDatebox;
	private Combobox dateTypeSelectCombobox;
	
	private List<VoucherGiroReceipt> voucherGiroReceiptList;
	// private BigDecimal totalReceiptVal;
	private int receiptCount;
	
	private final int WORK_DAY_WEEK 	= 6;	
	private final int DEFAULT_TAB_INDEX = 1;
	
	private final Logger log = Logger.getLogger(VoucherGiroReceiptControl.class);
	
	public void onCreate$voucherGiroReceiptListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Voucher Giro Receipt");

		voucherGiroReceiptListbox.setEmptyMessage("Tidak ada");
		
		// set the tab
		voucherGiroReceiptPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// date type selection combobox
		setupDateTypeSelection();
		
		// locale
		startDatebox.setLocale(getLocale());
		startDatebox.setFormat(getLongDateFormat());
		endDatebox.setLocale(getLocale());
		endDatebox.setFormat(getLongDateFormat());
		
		// list by tab
		listBySelection(DEFAULT_TAB_INDEX);
		
		// list
		// listAllVoucherGiroReceipt();
		
		// load
		// loadVoucherGiroReceiptListInfo();
	}

	private void setupDateTypeSelection() {
		Comboitem comboitem;
		// tgl Penerimaan Giro
		comboitem = new Comboitem();
		comboitem.setLabel("Penerimaan-Giro");
		comboitem.setParent(dateTypeSelectCombobox);
		// tgl Giro Jatuh Tempo
		comboitem = new Comboitem();
		comboitem.setLabel("Jth-Tempo");
		comboitem.setParent(dateTypeSelectCombobox);
		
		dateTypeSelectCombobox.setSelectedIndex(0);
	}

	private void listAllVoucherGiroReceipt() throws Exception {
		setVoucherGiroReceiptList(
				// getVoucherGiroReceiptDao().findAllVoucherGiroReceipt());
				getVoucherGiroReceiptDao().findAllVoucherGiroReceipt_OrderBy_TransactionDate(false));
		
		receiptCount = getVoucherGiroReceiptList().size();
	}	
	
	/**
	 * selects VoucherGiroReceipt by TransactionDate attribute (start and end date)
	 * 
	 * @param startDate
	 * @param endDate
	 * @param desc
	 * @throws Exception
	 */
	private void listAllVoucherGiroReceiptByDate(Date startDate, Date endDate, boolean desc) throws Exception {
		setVoucherGiroReceiptList(
				getVoucherGiroReceiptDao().findAllVoucherGiroReceipt_By_TransactionDate(startDate, endDate, false));
		
		receiptCount = getVoucherGiroReceiptList().size();
	}
	
	/**
	 * selects VoucherGiroReceipt by GiroDate attribute (start and end date)
	 * 
	 * @param startDate
	 * @param endDate
	 * @param desc
	 * @throws Exception
	 */
	private void listAllVoucherGiroReceiptByGiroDate(Date startDate, Date endDate, boolean desc) throws Exception {
		setVoucherGiroReceiptList(
				getVoucherGiroReceiptDao().findAllVoucherGiroReceipt_By_GiroDate(startDate, endDate, false));
	}
	
	private void loadVoucherGiroReceiptListInfo() throws Exception {
		voucherGiroReceiptListbox.setModel(
				new ListModelList<VoucherGiroReceipt>(getVoucherGiroReceiptList()));
		voucherGiroReceiptListbox.setItemRenderer(
				getVoucherGiroReceiptListitemRenderer());
	}

	public void onSelect$dateTypeSelectCombobox(Event event) throws Exception {
		int selIndex = dateTypeSelectCombobox.getSelectedIndex();
		int lastIndex;
		switch (selIndex) {
		case 0: // tgl.Penerimaan-Giro
			listAllVoucherGiroReceipt();
			
			VoucherGiroReceipt giroLatest = getVoucherGiroReceiptList().get(0);
			lastIndex = getVoucherGiroReceiptList().size()-1;
			VoucherGiroReceipt giroEarliest = getVoucherGiroReceiptList().get(lastIndex);
			startDatebox.setValue(giroEarliest.getTransactionDate());
			endDatebox.setValue(giroLatest.getTransactionDate());
			
			break;
		case 1: // tgl.Jatuh-Tempo-Giro
			listAllVoucherGiroReceipt();
			
			// sort based on giro due
			getVoucherGiroReceiptList().sort((o1,o2) -> {
				return o1.getGiroDate().compareTo(o2.getGiroDate());
			});
			
			getVoucherGiroReceiptList().forEach(giro -> log.info(giro.getGiroDate()));
			
			VoucherGiroReceipt giroDueEarliest = getVoucherGiroReceiptList().get(0);
			lastIndex = getVoucherGiroReceiptList().size()-1;
			VoucherGiroReceipt giroDueLatest = getVoucherGiroReceiptList().get(lastIndex);
			startDatebox.setValue(giroDueEarliest.getGiroDate());
			endDatebox.setValue(giroDueLatest.getGiroDate());
			
			break;
		default:
			break;
		}
		// load
		loadVoucherGiroReceiptListInfo();			

	}
	
	public void onSelect$voucherGiroReceiptPeriodTabbox(Event event) throws Exception {
		int selIndex = voucherGiroReceiptPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}
	
	public void onClick$filterButton(Event event) throws Exception {
		int selIndex = dateTypeSelectCombobox.getSelectedIndex();
		Date startDate = startDatebox.getValue();
		Date endDate = endDatebox.getValue();
		boolean descendingOrder = true;
		
		// list by date
		listAllVoucherGiroReceiptByDate(startDate, endDate, descendingOrder);			

		switch (selIndex) {
		case 0: // tgl.Penerimaan-Giro
			// list by date
			listAllVoucherGiroReceiptByDate(startDate, endDate, descendingOrder);			
			
			// sort based on transaction-date
			getVoucherGiroReceiptList().sort((o1,o2) -> {
				return o1.getTransactionDate().compareTo(o2.getTransactionDate());
			});
			
			break;
		case 1: // tgl.Jatuh-Tempo-Giro
			// list by giroDate
			listAllVoucherGiroReceiptByGiroDate(startDate, endDate, descendingOrder);			

			// sort based on giro due
			getVoucherGiroReceiptList().sort((o1,o2) -> {
				return o1.getGiroDate().compareTo(o2.getGiroDate());
			});
			
			break;
		default:
			break;
		}
		
		// load
		loadVoucherGiroReceiptListInfo();
	}
	
	public void onClick$resetButton(Event event) throws Exception {
		int selIndex = voucherGiroReceiptPeriodTabbox.getSelectedIndex();
		
		// list by tab
		listBySelection(selIndex);

	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;
		VoucherGiroReceipt giroEarliest, giroLatest;
		int lastIndex;
		
		switch (selIndex) {
		case 0: // semua
			// list all
			listAllVoucherGiroReceipt();
			
			if (getVoucherGiroReceiptList().isEmpty()) {
				startDatebox.setDisabled(true);
				endDatebox.setDisabled(true);
			} else {
				startDatebox.setDisabled(false);
				endDatebox.setDisabled(false);

				giroEarliest = getVoucherGiroReceiptList().get(0);
				lastIndex = getVoucherGiroReceiptList().size()-1;
				giroLatest = getVoucherGiroReceiptList().get(lastIndex);
				startDatebox.setValue(giroEarliest.getTransactionDate());
				endDatebox.setValue(giroLatest.getTransactionDate());
			}
			// load
			loadVoucherGiroReceiptListInfo();			
			break;
		case 1: // hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			// list by date
			listAllVoucherGiroReceiptByDate(startDate, endDate, true);

			if (getVoucherGiroReceiptList().isEmpty()) {
				startDatebox.setDisabled(true);
				endDatebox.setDisabled(true);
			} else {
				startDatebox.setDisabled(false);
				endDatebox.setDisabled(false);
				
				giroEarliest = getVoucherGiroReceiptList().get(0);
				lastIndex = getVoucherGiroReceiptList().size()-1;
				giroLatest = getVoucherGiroReceiptList().get(lastIndex);
				startDatebox.setValue(giroEarliest.getTransactionDate());
				endDatebox.setValue(giroLatest.getTransactionDate());
			}
			
			// load
			loadVoucherGiroReceiptListInfo();
			break;
		case 2: // minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			// list by date
			listAllVoucherGiroReceiptByDate(startDate, endDate, true);


			if (getVoucherGiroReceiptList().isEmpty()) {
				startDatebox.setDisabled(true);
				endDatebox.setDisabled(true);
			} else {
				giroEarliest = getVoucherGiroReceiptList().get(0);
				lastIndex = getVoucherGiroReceiptList().size()-1;
				giroLatest = getVoucherGiroReceiptList().get(lastIndex);
				startDatebox.setValue(giroEarliest.getTransactionDate());
				endDatebox.setValue(giroLatest.getTransactionDate());
			}
			// load
			loadVoucherGiroReceiptListInfo();
			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			// list by date
			listAllVoucherGiroReceiptByDate(startDate, endDate, true);

			if (getVoucherGiroReceiptList().isEmpty()) {
				startDatebox.setDisabled(true);
				endDatebox.setDisabled(true);
			} else {
				giroEarliest = getVoucherGiroReceiptList().get(0);
				lastIndex = getVoucherGiroReceiptList().size()-1;
				giroLatest = getVoucherGiroReceiptList().get(lastIndex);
				startDatebox.setValue(giroEarliest.getTransactionDate());
				endDatebox.setValue(giroLatest.getTransactionDate());
			}
			
			// load
			loadVoucherGiroReceiptListInfo();
			break;
		default:
			break;
		}
	}
	
	private ListitemRenderer<VoucherGiroReceipt> getVoucherGiroReceiptListitemRenderer() {
		
		// totalReceiptVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherGiroReceipt>() {
			
			@Override
			public void render(Listitem item, VoucherGiroReceipt receipt, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Penerimaan Giro
				String transDate = dateToStringDisplay(
						asLocalDate(receipt.getTransactionDate()), getLongDateFormat());
				
				lc = new Listcell(transDate);
				lc.setParent(item);
								
				// No.Voucher
				lc = new Listcell(receipt.getVoucherNumber().getSerialComp());
				lc.setParent(item);
				
				// Customer
				Customer customer = getCustomerByProxy(receipt.getId());
				
				lc = new Listcell(customer != null ?
						customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName().toString() : 
							"WARNING: NO CUSTOMER");
				lc.setStyle("White-space: nowrap;");
				lc.setParent(item);
				
				// Giro Info - bank and giro number
				String giroInfo = receipt.getGiroBank()+" - No.:"+receipt.getGiroNumber();
				
				lc = new Listcell(giroInfo);
				lc.setStyle("White-space: nowrap;");
				lc.setParent(item);
				
				// Tgl.Jth-Tempo Giro
				lc = new Listcell(dateToStringDisplay(
						asLocalDate(receipt.getGiroDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Nominal (Rp.)
				String nominal = toLocalFormat(receipt.getTheSumOf());
				
				lc = new Listcell(nominal);
				lc.setParent(item);
				
				// NOTE: create VoucherPayment in Giro list
				// lc = initPosting(new Listcell(), receipt);
				// lc.setParent(item);
								
				// User
				lc = initUserCreate(new Listcell(), receipt);
				lc.setParent(item);
				
				// edit
				lc = initEditButton(new Listcell(), receipt);
				lc.setParent(item);
				
				// totalReceiptVal = totalReceiptVal.add(receipt.getTheSumOf());
				
				// if the status of receipt is 'BATAL', change the backgroud color to red
				if (receipt.getVoucherStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");
				}
			}
			
			
			private Listcell initUserCreate(Listcell listcell, VoucherGiroReceipt receipt) throws Exception {
				// userCreate by proxy
				VoucherGiroReceipt giroReceiptByProxy = 
						getVoucherGiroReceiptDao().findUserCreateByProxy(receipt.getId());
				
				listcell.setLabel(giroReceiptByProxy.getUserCreate()==null ? "" : 
					giroReceiptByProxy.getUserCreate().getUser_name());	
					
				return listcell;
			}


			private Listcell initEditButton(Listcell listcell, VoucherGiroReceipt receipt) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						VoucherGiroReceiptData receiptData = new VoucherGiroReceiptData();
						receiptData.setPageMode(PageMode.VIEW);
								// receipt.getVoucherStatus().equals(DocumentStatus.BATAL) ?
								// PageMode.VIEW : PageMode.EDIT);
						receiptData.setVoucherGiroReceipt(receipt);
						
						Map<String, VoucherGiroReceiptData> arg = 
								Collections.singletonMap("voucherGiroReceiptData", receiptData);
						
						Window receiptDialogWin =
							(Window) Executions.createComponents(
								"/voucher/VoucherGiroReceiptDialog.zul", null, arg);
						
						receiptDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// update
								getVoucherGiroReceiptDao().update((VoucherGiroReceipt) event.getData());
								
								// load
								loadVoucherGiroReceiptListInfo();
							}
						});
						
						receiptDialogWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

		};
	}

	public void onAfterRender$voucherGiroReceiptListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+receiptCount);
				//+" receipt - Rp."+toLocalFormat(totalReceiptVal));
	}
	
	public void onClick$addButton(Event event) throws Exception {
		
		Map<String, VoucherGiroReceipt> arg = 
				Collections.singletonMap("voucherGiroReceipt", new VoucherGiroReceipt());
					
		// display the VoucherGiroReceiptDialog -- new
		Window receiptDialogWin =
				(Window) Executions.createComponents(
					"/voucher/VoucherGiroReceiptAddDialog.zul", null, arg);
		
		receiptDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				VoucherGiroReceipt giroReceipt = (VoucherGiroReceipt) event.getData();
				
				// save
				getVoucherGiroReceiptDao().save(giroReceipt);
				
				//
				listAllVoucherGiroReceipt();

				//
				listBySelection(voucherGiroReceiptPeriodTabbox.getSelectedIndex());
				
			}
		});
		
		receiptDialogWin.doModal();		
	}
	
	
	
	public Customer getCustomerByProxy(Long id) throws Exception {
		VoucherGiroReceipt voucherGiroReceipt = getVoucherGiroReceiptDao().findCustomerByProxy(id);
		
		return voucherGiroReceipt.getCustomer();
	}
	
	public VoucherGiroReceiptDao getVoucherGiroReceiptDao() {
		return voucherGiroReceiptDao;
	}

	public void setVoucherGiroReceiptDao(VoucherGiroReceiptDao voucherGiroReceiptDao) {
		this.voucherGiroReceiptDao = voucherGiroReceiptDao;
	}

	public List<VoucherGiroReceipt> getVoucherGiroReceiptList() {
		return voucherGiroReceiptList;
	}

	public void setVoucherGiroReceiptList(List<VoucherGiroReceipt> voucherGiroReceiptList) {
		this.voucherGiroReceiptList = voucherGiroReceiptList;
	}
}
