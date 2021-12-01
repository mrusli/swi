package com.pyramix.swi.webui.giro;

import java.math.BigDecimal;
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
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.voucher.VoucherGiroReceiptData;
import com.pyramix.swi.webui.voucher.VoucherPaymentData;

public class GiroListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8482667346248011937L;
	
	private GiroDao giroDao;
	private VoucherPaymentDao voucherPaymentDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox giroListbox;
	private Combobox paidStatusCombobox;
	private Tabbox voucherPaymentPeriodTabbox;
	
	private List<Giro> giroList;
	private BigDecimal totalAmount;
	private int giroCount;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX = 1;
	
	private final Logger log = Logger.getLogger(GiroListInfoControl.class);
	
	public void onCreate$giroListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Informasi Giro");

		// eq = EventQueues.lookup("giro", EventQueues.APPLICATION, true);
		// eq.subscribe(new EventListener<Event>() {

		//	@Override
		//	public void onEvent(Event event) throws Exception {
				// giro belum terpakai
		//		paidStatusCombobox.setSelectedIndex(0);
				// list
		//		listAllGiroInfo(false);
				// notify
		//		Clients.showNotification("Terima Giro.", "info", null, "bottom_right", 0);				
		//	}

		// });
		
		// empty message -- in case no data is displayed
		giroListbox.setEmptyMessage("Tidak ada");
		
		// combobox -- set selected 'Giro Belum Terpakai...'
		setupPaidStatusCombobox();
		
		// list giro with PaidStatus == false
		// -- Giro not paid yet
		// listAllGiroInfo(false);
		
		// set tab
		voucherPaymentPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list by selection -- set to DEFAULT_TAB_INDEX
		listBySelection(DEFAULT_TAB_INDEX);
	}

	private void setupPaidStatusCombobox() {
		Comboitem item01 = new Comboitem();
		item01.setLabel("Belum Terpakai");
		item01.setParent(paidStatusCombobox);
		
		Comboitem item02 = new Comboitem();
		item02.setLabel("Sudah Terpakai (Cair)");
		item02.setParent(paidStatusCombobox);
		
		paidStatusCombobox.setSelectedIndex(0);
	}

	private void listAllGiroInfo(boolean paidStatus) throws Exception {
		setGiroList(getGiroDao().findAllGiro_OrderBy_GiroDueDate(true, paidStatus));
				// getGiroDao().findAllGiro_OrderBy_ReceivedDate(true, paidStatus));
		
		giroCount = getGiroList().size();
		
		displayList();		
	}

	private void listPaidGiroInfo(boolean paidStatus) throws Exception {
		setGiroList(getGiroDao().findAllGiro_OrderBy_PaidGiroDate(true, paidStatus));

		giroCount = getGiroList().size();
		
		displayList();		
	}
	
	private void listGiroInfoByDate(Date startDate, Date endDate, boolean paidStatus) throws Exception {
		setGiroList(getGiroDao().findGiro_By_DueGiroDate(startDate, endDate, paidStatus));
		
		giroCount = getGiroList().size();
		
		displayList();
	}
	
	private void displayList() {
		giroListbox.setModel(
				new ListModelList<Giro>(getGiroList()));
		giroListbox.setItemRenderer(getGiroListitemRenderer());
	}
	
	private ListitemRenderer<Giro> getGiroListitemRenderer() {
		
		totalAmount = BigDecimal.ZERO;
		
		return new ListitemRenderer<Giro>() {
						
			@Override
			public void render(Listitem item, Giro giro, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Penerimaan-Giro
				String tglPenerimaan = dateToStringDisplay(
						asLocalDate(giro.getGiroReceivedDate()), getLongDateFormat());
				lc = new Listcell(tglPenerimaan);
				lc.setParent(item);
				
				// Diterima Dari
				Customer customerByProxy = getCustomerByProxy(giro.getId());
				
				lc = new Listcell(customerByProxy != null ?
						customerByProxy.getCompanyType().toString()+". "+
						customerByProxy.getCompanyLegalName().toString() : 
							"WARNING: NO CUSTOMER");
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nominal (Rp.)
				String nominal = toLocalFormat(giro.getGiroAmount());
				
				lc = new Listcell(nominal);
				lc.setParent(item);
				
				// Tgl.Pencairan-Giro
				lc = initPencairanGiro(new Listcell(), giro);
				lc.setParent(item);

				// Posting/Voucher
				lc = initVoucherPaymentPosting(new Listcell(), giro);
				lc.setParent(item);
				
				// No.Giro
				lc = new Listcell(giro.getGiroNumber());
				lc.setParent(item);
				
				// Bank
				lc = new Listcell(giro.getGiroBank());
				lc.setParent(item);
				
				// Voucher-GiroReceipt
				lc = initVoucherGiroReceipt(new Listcell(), giro);
				lc.setParent(item);
				
				// Edit Giro
				lc = initGiroEdit(new Listcell(), giro);
				lc.setParent(item);
				
				totalAmount = totalAmount.add(giro.getGiroAmount());

				// if the status of giro is 'BATAL', change the backgroud color to red
				if (giro.getGiroStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");
				}
			}
			
			private Listcell initVoucherPaymentPosting(Listcell listcell, Giro giro) throws Exception {
				Label postingLabel;
				Button postingButton;

				if (giro.getVoucherPayment() == null) {
					postingLabel = new Label();
					postingLabel.setValue("Belum Posting");
					postingLabel.setStyle("color: red; font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);
					
					postingButton = new Button();
					postingButton.setLabel("Posting");
					postingButton.setClass("inventoryEditButton");
					postingButton.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							VoucherPaymentData data = new VoucherPaymentData();
							data.setPageMode(PageMode.NEW);
							data.setGiro(giro);
							
							Map<String, VoucherPaymentData> arg = 
									Collections.singletonMap("voucherPaymentData", data);

							Window voucherPaymentDialogWin = 
									(Window) Executions.createComponents("/voucher/VoucherPaymentDialog.zul", null, arg);

							voucherPaymentDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

								@Override
								public void onEvent(Event event) throws Exception {
									VoucherPayment payment = (VoucherPayment) event.getData();
									
									// update the current giro
									giro.setVoucherPayment(payment);

									giro.setPaid(true);
									giro.setPaidGiroDate(payment.getTransactionDate());
									
									// update
									getGiroDao().update(giro);
									
									// list giro with PaidStatus == false
									// -- Giro not paid yet
									listAllGiroInfo(false);									
								}
							});							
							voucherPaymentDialogWin.doModal();
						}
					});
					postingButton.setParent(listcell);
				} else {
					VoucherPayment voucherPaymentByProxy = getVoucherPaymentByProxy(giro.getId());

					postingLabel = new Label();
					postingLabel.setValue(voucherPaymentByProxy.getVoucherNumber().getSerialComp());
					postingLabel.setStyle("font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);

					postingButton = new Button();
					postingButton.setLabel("...");
					postingButton.setClass("inventoryEditButton");
					postingButton.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							VoucherPaymentData data = new VoucherPaymentData();
							data.setPageMode(PageMode.VIEW);
							data.setVoucherPayment(voucherPaymentByProxy);
							data.setGiro(giro);

							Map<String, VoucherPaymentData> arg =
									Collections.singletonMap("voucherPaymentData", data);
								
							Window voucherPaymentDialogWin = 
									(Window) Executions.createComponents("/voucher/VoucherPaymentDialog.zul", null, arg);
								
							voucherPaymentDialogWin.doModal();
						}

					});
					postingButton.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initPencairanGiro(Listcell listcell, Giro giro) {
				String pencairanDate;
				// System.out.println(giro.getId());
				// System.out.println(giro.getGiroDate());
				if (giro.isPaid()) {
					pencairanDate = dateToStringDisplay(
							asLocalDate(giro.getPaidGiroDate()), getLongDateFormat());

					listcell.setLabel("Lunas : "+pencairanDate);					
				} else if (giro.getGiroDate().compareTo(asDate(getLocalDate()))<0) {
					pencairanDate = dateToStringDisplay(
						asLocalDate(giro.getGiroDate()), getLongDateFormat());
 
					listcell.setLabel("Belum Cair : "+pencairanDate);
					listcell.setStyle("color: red; font-size: 1em; padding-right: 5px");
				} else {
					pencairanDate = dateToStringDisplay(
							asLocalDate(giro.getGiroDate()), getLongDateFormat());
					listcell.setLabel(pencairanDate);
				}
				
				return listcell;
			}
			
			private Listcell initVoucherGiroReceipt(Listcell listcell, Giro giro) throws Exception {
				VoucherGiroReceipt receiptByProxy = getVoucherGiroReceiptByProxy(giro.getId());
				
				Label postingLabel = new Label();
				postingLabel.setValue(receiptByProxy.getVoucherNumber().getSerialComp());
				postingLabel.setStyle("font-size: 1em; padding-right: 5px");
				postingLabel.setParent(listcell);
				
				Button postingButton = new Button();
				postingButton.setLabel("...");
				postingButton.setClass("inventoryEditButton");
				postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						VoucherGiroReceiptData receiptData = new VoucherGiroReceiptData();
						receiptData.setVoucherGiroReceipt(receiptByProxy);
						receiptData.setVoucherSales(null);
						receiptData.setPageMode(PageMode.VIEW);
						
						Map<String, VoucherGiroReceiptData> arg = 
								Collections.singletonMap("voucherGiroReceiptData", receiptData);
									
						Window receiptDialogWin =
								(Window) Executions.createComponents(
									"/voucher/VoucherGiroReceiptDialog.zul", null, arg);

						
						
						receiptDialogWin.doModal();							
						
					}

				});
				postingButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initGiroEdit(Listcell listcell, Giro giro) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.setParent(listcell);
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("edit button clicked...");
						log.info(giro.toString());
						
						Map<String, Giro> arg = 
								Collections.singletonMap("giro", giro);						
						
						Window giroDialogWin =
								(Window) Executions.createComponents(
									"/giro/GiroDialog.zul", null, arg);
							
						giroDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Giro giro = (Giro) event.getData();
								
								log.info(giro.toString());
								
								// update
								getGiroDao().update(giro);
								
								// selected period
								int selIndex = voucherPaymentPeriodTabbox.getSelectedIndex();
								// re-list
								listBySelection(selIndex);
							}
							
						});
						
						giroDialogWin.doModal();							
					
					}
					
				});
				
				return listcell;
			}
		};	
	}

	public void onAfterRender$giroListbox(Event event) throws Exception {
		String totalGiroCount = getFormatedInteger(giroCount);
		String totalGiroAmount = toLocalFormat(totalAmount);
		
		infoResultlabel.setValue("Total: "+totalGiroCount+" giro - Rp."+totalGiroAmount);		
	}
	
	public void onSelect$paidStatusCombobox(Event event) throws Exception {		
		// selected combobox
		int selIndex = paidStatusCombobox.getSelectedIndex();
				
		// semua
		if (selIndex==0) {
			// belum terpakai -- orderBy giroDate
			listAllGiroInfo(false);
		} else {
			// sudah terpakai -- orderBy paidGiroDate
			listPaidGiroInfo(true);
		}
	}

	public void onSelect$voucherPaymentPeriodTabbox(Event event) throws Exception {		
		int selIndex = voucherPaymentPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}

	private void listBySelection(int selIndex) throws Exception {
		// reset paidStatusCombobox -- belum terpakai(0) 
		paidStatusCombobox.setSelectedIndex(0);
		boolean paidStatus = false;
		
		Date startDate, endDate;

		switch (selIndex) {
		case 0: // semua			
			// reset paidStatusCombobox -- belum terpakai(0) 
			paidStatusCombobox.setSelectedIndex(0);
			
			// belum terpakai -- orderBy giroDate
			listAllGiroInfo(paidStatus);
			
			paidStatusCombobox.setDisabled(false);
			
			break;
		case 1: // hari-ini
			
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			listGiroInfoByDate(startDate, endDate, paidStatus);
			
			paidStatusCombobox.setDisabled(true);
			
			break;
		case 2: // minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			listGiroInfoByDate(startDate, endDate, paidStatus);

			paidStatusCombobox.setDisabled(true);

			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			listGiroInfoByDate(startDate, endDate, paidStatus);

			paidStatusCombobox.setDisabled(true);

			break;
		default:
			break;
		}
	}
	
	protected VoucherGiroReceipt getVoucherGiroReceiptByProxy(long id) throws Exception {
		Giro giro = getGiroDao().findVoucherGiroReceiptByProxy(id);
		
		return giro.getVoucherGiroReceipt();
	}

	protected Customer getCustomerByProxy(long id) throws Exception {
		Giro giro = getGiroDao().findCustomerByProxy(id);
		
		return giro.getCustomer();
	}

	protected VoucherPayment getVoucherPaymentByProxy(long id) throws Exception {
		Giro giro = getGiroDao().findVoucherPaymentByProxy(id);
		
		return giro.getVoucherPayment();
	}

	public GiroDao getGiroDao() {
		return giroDao;
	}

	public void setGiroDao(GiroDao giroDao) {
		this.giroDao = giroDao;
	}

	public List<Giro> getGiroList() {
		return giroList;
	}

	public void setGiroList(List<Giro> giroList) {
		this.giroList = giroList;
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}
	
}
