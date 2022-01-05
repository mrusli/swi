package com.pyramix.swi.webui.voucher;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherSalesControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6225623056578363005L;

	private VoucherSalesDao voucherSalesDao;
	private CustomerOrderDao customerOrderDao;
	
	private Window voucherSalesListInfoWin;
	private Label formTitleLabel, infoVoucherSaleslabel;
	private Listbox voucherSalesListbox;
	private Tabbox voucherSalesPeriodTabbox;
	
	private List<VoucherSales> voucherSalesList;
	private int voucherSalesCount;
	// private BigDecimal totalVoucherSalesValue;
	
	// display / list today's VoucherSales
	private final int DEFAULT_TAB_INDEX = 1;
	private final int WORK_DAY_WEEK		= 6;
	
	@SuppressWarnings("rawtypes")
	private EventQueue eq;
	
	@SuppressWarnings("unchecked")
	public void onCreate$voucherSalesListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Voucher - Sales");

		eq = EventQueues.lookup("voucherSales", EventQueues.APPLICATION, true);
		eq.subscribe(new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the selected index
				int selIndexTab = voucherSalesPeriodTabbox.getSelectedIndex();
				
				// list
				listBySelection(selIndexTab);
				
				// load
				// loadVoucherSalesListInfo();
				// notify
				// Clients.showNotification("Penambahan Voucher Sales.", "info", null, "bottom_right", 0);
			}
		});
		// set today's list
		voucherSalesPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list
		listBySelection(DEFAULT_TAB_INDEX);
		
		// list
		// listAllVoucherSales();
		// load
		// loadVoucherSalesListInfo();
	}

	public void onSelect$voucherSalesPeriodTabbox(Event event) throws Exception {
		int selIndex = voucherSalesPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // Semua
			listAllVoucherSales();
			
			loadVoucherSalesListInfo();
			break;
		case 1: // Hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			// list by date
			listVoucherSalesByDate(startDate, endDate);
			
			loadVoucherSalesListInfo();
			break;
		case 2: // Minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			// list by date
			listVoucherSalesByDate(startDate, endDate);
			
			loadVoucherSalesListInfo();			
			break;
		case 3: // Bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			// list by date
			listVoucherSalesByDate(startDate, endDate);
			
			loadVoucherSalesListInfo();			
			break;
		default:
			break;
		}
		
	}

	private void listVoucherSalesByDate(Date startDate, Date endDate) throws Exception {
		setVoucherSalesList(
				getVoucherSalesDao().findVoucher_By_TransactionDate(startDate, endDate, true));
		
		voucherSalesCount = getVoucherSalesList().size();
		
		// set the batal button to visible depending on voucherSalesCount
		// batalButton.setDisabled(voucherSalesCount==0);
	}

	private void listAllVoucherSales() throws Exception {
		setVoucherSalesList(
				// getVoucherSalesDao().findAllVoucher());
				getVoucherSalesDao().findAllVoucher_OrderBy_TransactionDate(true));
		
		voucherSalesCount = getVoucherSalesList().size();
		
		// set the batal button to visible depending on voucherSalesCount
		// batalButton.setDisabled(voucherSalesCount==0);
	}	
	
	private void loadVoucherSalesListInfo() {		
		voucherSalesListbox.setModel(
				new ListModelList<VoucherSales>(getVoucherSalesList()));
		voucherSalesListbox.setItemRenderer(
				getVoucherListitemRenderer());
		
	}

	private ListitemRenderer<VoucherSales> getVoucherListitemRenderer() {

		// totalVoucherSalesValue = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherSales>() {
			
			@Override
			public void render(Listitem item, VoucherSales voucherSales, int index) throws Exception {
				Listcell lc;
				
				// Tgl.
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(voucherSales.getCreateDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// No.Voucher
				lc = new Listcell(voucherSales.getVoucherNumber().getSerialComp());
				lc.setParent(item);				
				
				// Customer
				Customer customer = getCustomerByProxy(voucherSales.getId());
				
				lc = new Listcell(customer==null ? "tunai" :
						customer.getCompanyType()+". "+
						customer.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Pembayaran
				String pembayaran = voucherSales.getPaymentType().equals(PaymentType.tunai) ?
						voucherSales.getPaymentType().toString() : 
							voucherSales.getPaymentType().toString() + " - "+
								getFormatedInteger(voucherSales.getJumlahHari()) + " Hari";
				
				lc = new Listcell(pembayaran);
				lc.setParent(item);
				
				// Total (Rp.)
				lc = new Listcell(toLocalFormat(voucherSales.getTheSumOf()));
				lc.setParent(item);
								
				// No.CustomerOrder
				CustomerOrder customerOrderByProxy = getCustomerOrderByProxy(voucherSales.getId());
				lc = new Listcell(customerOrderByProxy.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// No.SuratJalan
				SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrderByProxy.getId());
				lc = new Listcell(suratJalanByProxy == null ? " - " : 
					suratJalanByProxy.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);
				
				// User
				lc = new Listcell();
				lc.setParent(item);

				// edit button
				lc = initEditButton(new Listcell(), voucherSales);
				lc.setParent(item);

				// if (voucherSales.getVoucherStatus().equals(DocumentStatus.NORMAL)) {
				//	totalVoucherSalesValue = totalVoucherSalesValue.add(voucherSales.getTheSumOf());
				// }
				
				item.setValue(voucherSales);
				
				// if the status of voucherSales is 'BATAL', change the backgroud color to red
				if (voucherSales.getVoucherStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");
				}
			}


			private Listcell initEditButton(Listcell listcell, VoucherSales voucherSales) throws Exception {
				Button editButton = new Button();
				
				if (voucherSales.getPaymentType().equals(PaymentType.tunai)) {
					editButton.setLabel("...");
							// voucherSales.isPaymentComplete() ?
							// "View" : "Edit");
					editButton.setClass("inventoryEditButton");
					editButton.addEventListener("onClick", new EventListener<Event>() {
						
						@Override
						public void onEvent(Event event) throws Exception {
							VoucherSalesData data = new VoucherSalesData();
							data.setPageMode(PageMode.VIEW);
									// voucherSales.isPaymentComplete() ?
									// PageMode.VIEW : PageMode.EDIT);
							data.setVoucherSales(voucherSales);
							
							Map<String, VoucherSalesData> arg = 
									Collections.singletonMap("voucherSalesData", data);
							
							Window voucherSalesDialogWin = 
									(Window) Executions.createComponents("/voucher/VoucherSalesDialog.zul", voucherSalesListInfoWin, arg);
							
							voucherSalesDialogWin.doModal();

/*
 * 							voucherSalesDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									// UPDATE VoucherSales -- use VoucherSalesDao
									getVoucherSalesDao().update((VoucherSales) event.getData());
									// load
									loadVoucherSalesListInfo();
								}
							});
*/														
						}
					});
					editButton.setParent(listcell);
					
				} else {
					// payment non-tunai - check whether got VoucherGiroReceipt yet
					// voucherSales.getVoucherGiroReceipts().isEmpty() ?
					editButton.setLabel("...");
							//isVoucherReceiptsEmptyByProxy(voucherSales.getId()) ?
							//"Edit" : "View");
					editButton.setClass("inventoryEditButton");
					editButton.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							VoucherSalesData data = new VoucherSalesData();
							data.setPageMode(PageMode.VIEW);
									// isVoucherReceiptsEmptyByProxy(voucherSales.getId()) ?
									// PageMode.EDIT : PageMode.VIEW);
							data.setVoucherSales(voucherSales);
							
							Map<String, VoucherSalesData> arg = 
									Collections.singletonMap("voucherSalesData", data);
							
							Window voucherSalesDialogWin = 
									(Window) Executions.createComponents("/voucher/VoucherSalesDialog.zul", voucherSalesListInfoWin, arg);
							
							voucherSalesDialogWin.doModal();
/*							
 * 							voucherSalesDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									// UPDATE VoucherSales -- use VoucherSalesDao
									getVoucherSalesDao().update((VoucherSales) event.getData());
									
									// load
									loadVoucherSalesListInfo();
								}
							});
*/							
							
						}
						
					});
					editButton.setParent(listcell);
				} 
								
				return listcell;
			}
		};
	}
	
	public void onAfterRender$voucherSalesListbox(Event event) throws Exception {
		infoVoucherSaleslabel.setValue("Total: "+voucherSalesCount+" voucher");
			// +" voucher - Rp."+toLocalFormat(totalVoucherSalesValue));
	}

	public void onClick$batalButton(Event event) throws Exception {
		if (voucherSalesListbox.getSelectedItem()==null) {
			throw new Exception ("Belum memilih Voucher Sales untuk dibatalkan.");
		} else {
			VoucherSales voucherSales =
					voucherSalesListbox.getSelectedItem().getValue();
			
			Map<String, VoucherSales> arg = 
					Collections.singletonMap("voucherSales", voucherSales);
			
			Window voucherSalesBatalWin = 
					(Window) Executions.createComponents("/voucher/VoucherSalesBatalDialog.zul", null, arg);
			
			voucherSalesBatalWin.doModal();
		}
	}
	
	
	public void onClick$addButton(Event event) throws Exception {
		VoucherSalesData data = new VoucherSalesData();
		data.setPageMode(PageMode.NEW);
		data.setVoucherSales(null);
		
		Map<String, VoucherSalesData> arg = 
				Collections.singletonMap("voucherSalesData", data);
		
		Window voucherSalesDialogWin = 
				(Window) Executions.createComponents("/voucher/VoucherSalesDialog.zul", null, arg);
		
		voucherSalesDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// SAVE - VoucherSales that has NO CustomerOrder
				// -- use VoucherSalesDao
				System.out.println("ON_OK");
			}
		});
		
		voucherSalesDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// UPDATE - CustomerOrder with VoucherSales attached
				// -- use CustomerOrderDao
				System.out.println("ON_CHANGE");
			}
		});
		
		voucherSalesDialogWin.doModal();
	}
	
	protected Customer getCustomerByProxy(Long id) throws Exception {
		VoucherSales voucherSales = getVoucherSalesDao().findCustomerByProxy(id);
		
		return voucherSales.getCustomer();
	}

	protected CustomerOrder getCustomerOrderByProxy(long id) throws Exception {
		VoucherSales voucherSales = getVoucherSalesDao().findCustomerOrderByProxy(id);
		
		return voucherSales.getCustomerOrder();
	}

	protected SuratJalan getSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
	}
	public List<VoucherSales> getVoucherSalesList() {
		return voucherSalesList;
	}

	public void setVoucherSalesList(List<VoucherSales> voucherSalesList) {
		this.voucherSalesList = voucherSalesList;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}


}
