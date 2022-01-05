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
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherPaymentControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1114533525867280409L;

	private VoucherPaymentDao voucherPaymentDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox voucherPaymentListbox;
	private Tabbox voucherPaymentPeriodTabbox;
	
	private List<VoucherPayment> voucherPaymentList;
	// private BigDecimal totalPaymentVal;
	private int paymentCount;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX = 1;
	
	private final Logger log = Logger.getLogger(VoucherPaymentControl.class);
	
	public void onCreate$voucherPaymentListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Voucher Payment / Pembayaran");

		voucherPaymentListbox.setEmptyMessage("Tidak Ada");
		
		// set the tab
		voucherPaymentPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list by selection
		listBySelection(DEFAULT_TAB_INDEX);
		
		// list
		// listAllVoucherPayment();
		
		// load
		// loadVoucherPaymentListInfo();
	}

	public void onSelect$voucherPaymentPeriodTabbox(Event event) throws Exception {
		int selIndex = voucherPaymentPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}

	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // semua
			listAllVoucherPayment();
			
			loadVoucherPaymentListInfo();
			break;
		case 1: // hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			// list by transactionDate
			listVoucherPaymentByTransactionDate(startDate, endDate);
			
			loadVoucherPaymentListInfo();
			break;
		case 2: // minggu-ini	
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			// list by transactionDate
			listVoucherPaymentByTransactionDate(startDate, endDate);
			
			loadVoucherPaymentListInfo();			
			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			// list by transactionDate
			listVoucherPaymentByTransactionDate(startDate, endDate);
			
			loadVoucherPaymentListInfo();
			break;
		default:
			break;
		}
	}

	private void listAllVoucherPayment() throws Exception {
		setVoucherPaymentList(
				// getVoucherPaymentDao().findAllVoucherPayment());
				getVoucherPaymentDao().findAllVoucherPayment_OrderBy_TransactionDate(true));
		
		paymentCount = getVoucherPaymentList().size();
	}

	private void listVoucherPaymentByTransactionDate(Date startDate, Date endDate) throws Exception {
		setVoucherPaymentList(
				getVoucherPaymentDao().findAllVoucherPayment_By_TransactionDate(startDate, endDate, true));
		
		paymentCount = getVoucherPaymentList().size();
	}	
	
	private void loadVoucherPaymentListInfo() throws Exception {
		voucherPaymentListbox.setModel(
				new ListModelList<VoucherPayment>(getVoucherPaymentList()));
		voucherPaymentListbox.setItemRenderer(
				getPaymentListitemRenderer());
	}

	private ListitemRenderer<VoucherPayment> getPaymentListitemRenderer() {
		
		// totalPaymentVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherPayment>() {
			
			@Override
			public void render(Listitem item, VoucherPayment payment, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Pembayaran
				String paymentDate = dateToStringDisplay(
						asLocalDate(payment.getTransactionDate()), getLongDateFormat());
				
				lc = new Listcell(paymentDate);
				lc.setParent(item);
				
				// No.Voucher
				lc = new Listcell(payment.getVoucherNumber().getSerialComp());
				lc.setParent(item);
				
				// Customer
				Customer customer = getCustomerByProxy(payment.getId());
				
				lc = new Listcell(customer != null ?
						customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName().toString() : 
							"WARNING: NO CUSTOMER");
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nominal (Rp.)				
				String nominal = toLocalFormat(payment.getTheSumOf());
				
				lc = new Listcell(nominal);
				lc.setParent(item);
				
				// Dibayar Dgn and Ref.
				// lc = initVoucherGiroReceiptInfo(new Listcell(), payment);
				lc = initVoucherReference(new Listcell(), payment);
				lc.setParent(item);

				// Pembayarn Info
				lc = new Listcell(payment.getTransactionDescription());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
								
				// User
				lc = initUserCreate(new Listcell(), payment);
				lc.setParent(item);
				
				// Status
				lc = initVoucherPaymentStatus(new Listcell(), payment);
				lc.setParent(item);
				
				// edit
				lc = initEditButton(new Listcell(), payment);
				lc.setParent(item);
				
				// totalPaymentVal = totalPaymentVal.add(payment.getTheSumOf());

				// if the status of voucherPayment is 'BATAL', change the backgroud color to red
				// if (payment.getVoucherStatus().equals(DocumentStatus.BATAL)) {
				//	item.setClass("red-background");
				// }
			}			
						
			private Listcell initVoucherReference(Listcell listcell, VoucherPayment payment) throws Exception {
				Label postingLabel = new Label();

				if (payment.getGiro() != null) {
					// Giro giroByProxy = getGiroByProxy(payment.getId());
					
					postingLabel.setValue(payment.getPaidBy().toString());
					postingLabel.setStyle("font-size: 1em; padding-right: 11px");
					postingLabel.setParent(listcell);

					Button postingButton = new Button();
					postingButton.setLabel("...");
					postingButton.setClass("inventoryEditButton");
					postingButton.setParent(listcell);
					postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							VoucherPayment voucherPaymentGiroByProxy = 
									getVoucherPaymentDao().findGiroByProxy(payment.getId());
							Giro giro = voucherPaymentGiroByProxy.getGiro();
							log.info(giro.toString());						
						}

					});

				} else {
					postingLabel.setValue("no ref");
					postingLabel.setWidth("60px");
					postingLabel.setStyle("font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, VoucherPayment payment) throws Exception {
				VoucherPayment paymentByProxy = getVoucherPaymentDao().findUserCreateByProxy(payment.getId());
				
				// userCreate - username
				Label label = new Label();
				label.setValue(paymentByProxy.getUserCreate()==null ? "" : 
					paymentByProxy.getUserCreate().getUser_name());
				label.setParent(listcell);				

				return listcell;
			}

			private Listcell initVoucherPaymentStatus(Listcell listcell, VoucherPayment payment) {
				if (payment.getVoucherStatus().equals(DocumentStatus.BATAL)) {
					Label label = new Label();
					label.setValue("Batal");
					label.setSclass("badge badge-red");
					label.setParent(listcell);
				}
				
				return listcell;
			}			
			
			private Listcell initEditButton(Listcell listcell, VoucherPayment payment) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						VoucherPaymentData data = new VoucherPaymentData();
						data.setPageMode(PageMode.VIEW);
						data.setVoucherPayment(payment);
						// data.setVoucherGiroReceipt(null);
						
						Map<String, VoucherPaymentData> arg = 
							Collections.singletonMap("voucherPaymentData", data);
						
						Window voucherPaymentDialogWin = 
								(Window) Executions.createComponents("/voucher/VoucherPaymentDialog.zul", null, arg);
												
						voucherPaymentDialogWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

		};
	}

	public void onAfterRender$voucherPaymentListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+paymentCount+" Voucher");
	}

	protected Giro getGiroByProxy(long id) throws Exception {
		VoucherPayment voucherPayment = getVoucherPaymentDao().findGiroByProxy(id);
		
		return voucherPayment.getGiro();
	}

	protected Settlement getSettlementByProxy(long id) throws Exception {
		VoucherPayment voucherPayment = getVoucherPaymentDao().findSettlementByProxy(id);
		
		return voucherPayment.getSettlement();
	}	
	
	protected Customer getCustomerByProxy(Long id) throws Exception {
		VoucherPayment voucherPayment = getVoucherPaymentDao().findCustomerByProxy(id);
		
		return voucherPayment.getCustomer();
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}

	public List<VoucherPayment> getVoucherPaymentList() {
		return voucherPaymentList;
	}

	public void setVoucherPaymentList(List<VoucherPayment> voucherPaymentList) {
		this.voucherPaymentList = voucherPaymentList;
	}
}
