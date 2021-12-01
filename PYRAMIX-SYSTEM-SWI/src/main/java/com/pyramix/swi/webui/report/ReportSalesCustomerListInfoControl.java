package com.pyramix.swi.webui.report;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
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
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesCustomerListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6125532679695713736L;

	private CustomerReceivableDao customerReceivableDao;
	private CustomerOrderDao customerOrderDao;
	private SettlementDao settlementDao;
	
	private Label formTitleLabel;
	private Listbox customerReceivablesListbox, receivableActivityListbox;
	
	private List<CustomerReceivable> customerReceivableList;
	
	private final Logger log = Logger.getLogger(ReportSalesCustomerListInfoControl.class);
	
	public void onCreate$reportSalesCustomerListInfoWin(Event event) throws Exception {
		log.info("Opening ReportSalesCustomerListInfo");
		
		formTitleLabel.setValue("Laporan Penjualan Langganan");
		
		setCustomerReceivableList(
				getCustomerReceivableDao().findAllTransactionalCustomerReceivable(true));
		
		customerReceivablesListbox.setModel(
				new ListModelList<CustomerReceivable>(getCustomerReceivableList()));
		customerReceivablesListbox.setItemRenderer(getCustomerReceivablesListitemRenderer());
	}

	private ListitemRenderer<CustomerReceivable> getCustomerReceivablesListitemRenderer() {
		
		return new ListitemRenderer<CustomerReceivable>() {
			
			@Override
			public void render(Listitem item, CustomerReceivable customerReceivable, int index) throws Exception {
				Listcell lc;
				
				// Nama Perusahaan
				CustomerReceivable receivableByProxy = 
						getCustomerReceivableDao().findCustomerByProxy(customerReceivable.getId());
				lc = new Listcell(receivableByProxy.getCustomer().getCompanyType().toString()+"."+
						receivableByProxy.getCustomer().getCompanyLegalName());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				// Total Penjualan
				BigDecimal totalSales = 
						getCustomerReceivableDao().sumAmountSalesReceivableActivities(
								receivableByProxy.getCustomer());
				lc = new Listcell(toLocalFormat(totalSales));
				lc.setParent(item);
				
				item.setValue(customerReceivable);
			}
		};
	}
	
	public void onSelect$customerReceivablesListbox(Event event) throws Exception {
		CustomerReceivable customerReceivable = 
				customerReceivablesListbox.getSelectedItem().getValue();
		
		CustomerReceivable receivableByProxy = 
				getCustomerReceivableDao().findCustomerByProxy(customerReceivable.getId());
		String customerName = receivableByProxy.getCustomer().getCompanyType().toString()+"."+
				receivableByProxy.getCustomer().getCompanyLegalName();
		
		log.info(customerName+" selected: "+
				customerReceivable.getCustomerReceivableActivities().toString());
		
		List<CustomerReceivableActivity> receivableActivities = customerReceivable.getCustomerReceivableActivities();
		// using lambda expression
		// ref: https://www.baeldung.com/java-8-sort-lambda
		// sort according to transaction as it happened in CustomerOrder - Mnt Setting nomor muda di paling atas
		Comparator<CustomerReceivableActivity> comparator = (o1, o2) -> {
			int cmp = Long.compare(o1.getCustomerOrderId(), o2.getCustomerOrderId());
			
			return cmp;
		};
		// receivableActivities.sort(comparator.reversed());
		receivableActivities.sort(comparator);
		
		receivableActivityListbox.setModel(
				new ListModelList<CustomerReceivableActivity>(receivableActivities));
		receivableActivityListbox.setItemRenderer(getCustomerReceivableActivityListitemRenderer());
	}
	
	private ListitemRenderer<CustomerReceivableActivity> getCustomerReceivableActivityListitemRenderer() {
		
		return new ListitemRenderer<CustomerReceivableActivity>() {
			
			@Override
			public void render(Listitem item, CustomerReceivableActivity recvActivity, int index) throws Exception {
				Listcell lc;
				
				CustomerOrder customerOrder = 
						getCustomerOrderDao().findCustomerOrderById(recvActivity.getCustomerOrderId());
				
				// Order-No.
				lc = initOrderNumber(new Listcell(), customerOrder);
				lc.setParent(item);
				
				// CustomerOrder button
				lc = initCustomerOrderButton(new Listcell(), customerOrder, recvActivity);
				lc.setParent(item);
				
				// Tgl.Penjualan
				if (recvActivity.getActivityType().compareTo(ActivityType.PEMBAYARAN)==0) {
					lc = new Listcell(" - ");
				} else {
					lc = new Listcell(dateToStringDisplay(
							asLocalDate(recvActivity.getSalesDate()), getShortDateFormat()));
				}
				lc.setParent(item);
				
				// Jumlah-Penjualan
				lc = new Listcell(toLocalFormat(recvActivity.getAmountSales()));
				lc.setParent(item);

				// Tgl.Pembayaran
				lc = new Listcell(recvActivity.getPaymentDate()==null ? " - " :
						dateToStringDisplay(
								asLocalDate(recvActivity.getPaymentDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Jumlah-Pembayaran
				lc = new Listcell(toLocalFormat(recvActivity.getAmountPaid()));
				lc.setParent(item);
				
				// Settlement button
				lc = initSettlementButton(new Listcell(), customerOrder, recvActivity);
				lc.setParent(item);
								
				// Status-Penjualan
				lc = new Listcell(recvActivity.getReceivableStatus().toString());
				lc.setParent(item);
				
				// Aktifitas
				lc = new Listcell(recvActivity.getActivityType().toString());
				lc.setParent(item);
				
			}
			
			protected Listcell initOrderNumber(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				// orderNumber
				Label orderNumberLabel = new Label();
				orderNumberLabel.setValue(
						customerOrder.getDocumentSerialNumber().getSerialComp());
				orderNumberLabel.setWidth("120px");
				orderNumberLabel.setStyle("font-size: 1em; padding-right: 8px;");
				orderNumberLabel.setParent(listcell);
				
				return listcell;
			}
			
			protected Listcell initCustomerOrderButton(Listcell listcell, CustomerOrder customerOrder, CustomerReceivableActivity recvActivity) throws Exception {
				// button to view customerOrder - SuratJalan - D/O - Faktur
				if ((recvActivity.getAmountSales().compareTo(BigDecimal.ZERO)==0) || (recvActivity.getReceivableStatus().compareTo(DocumentStatus.BATAL)==0)) {
					// do nothing
				} else {
					Button orderButton = new Button();
					orderButton.setLabel("...");
					orderButton.setSclass("inventoryEditButton");
					orderButton.setParent(listcell);
					orderButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						
						@Override
						public void onEvent(Event arg0) throws Exception {
							log.info("display customer order no: "+
									customerOrder.getDocumentSerialNumber().getSerialComp());
							
							Map<String, CustomerOrder> arg = Collections.singletonMap("customerOrder", customerOrder);
							
							Window customerOrderWin = 
									(Window) Executions.createComponents("/report/ReportSalesCustomerOrderDialog.zul", null, arg);
							
							customerOrderWin.doModal();
						}
						
					});					
				}
				
				return listcell;
			}
			
			protected Listcell initSettlementButton(Listcell listcell, CustomerOrder customerOrder, CustomerReceivableActivity recvActivity) throws Exception {
				if ((recvActivity.getAmountPaid().compareTo(BigDecimal.ZERO)==0) || recvActivity.getReceivableStatus().compareTo(DocumentStatus.BATAL)==0) {
					// no settlement button
				} else {					
					// button to view settlement - settlementdetail by customerOrder
					Button settlementButton = new Button();
					settlementButton.setDisabled(true);
					settlementButton.setLabel("...");
					settlementButton.setSclass("inventoryEditButton");
					settlementButton.setParent(listcell);
					settlementButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						
						public void onEvent(Event arg0) throws Exception {
							log.info("findSettlementDetail using customerOrderId: "+customerOrder.getId());
							List<SettlementDetail> settlementDetailList =
									getSettlementDao().findSettlementDetail_By_CustomerOrderId(customerOrder.getId());
							
							if (settlementDetailList.isEmpty()) {
								// do nothing -- should NOT come in here anymore, 
								// because when the paidAmount is zero, the list is empty
								
								log.info("settlementDetailList is empty...No payment yet...");
							} else {
								
							}												
						}
						
					});
				}
				
				return listcell;
			}
		};
	}
	
	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}

	public List<CustomerReceivable> getCustomerReceivableList() {
		return customerReceivableList;
	}

	public void setCustomerReceivableList(List<CustomerReceivable> customerReceivableList) {
		this.customerReceivableList = customerReceivableList;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}
}
