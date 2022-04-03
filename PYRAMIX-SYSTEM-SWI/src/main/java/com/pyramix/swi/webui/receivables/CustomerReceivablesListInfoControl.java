package com.pyramix.swi.webui.receivables;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerReceivablesListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6248330183974619742L;
	
	private CustomerReceivableDao customerReceivableDao;
	private CustomerOrderDao customerOrderDao;
	
	private Label formTitleLabel, companyName;
	private Listbox customerReceivablesListbox, receivableActivityListbox;
	private Listfooter totalPiutangLanggananListfooter, totalPiutangListfooter;
	
	private List<CustomerReceivable> customerReceivableList;

	private static final LocalDate END_OF_MARCH_2022 = LocalDate.of(2022, Month.MARCH, 31);
	
	private final Logger log = Logger.getLogger(CustomerReceivablesListInfoControl.class);
	
	public void onCreate$customerReceivablesListInfoWin(Event event) throws Exception {
		log.info("Opening CustomerReceivableListInfo");
		
		formTitleLabel.setValue("Piutang Langganan (Customer)");
		// all receivables
		setCustomerReceivableList(
				getCustomerReceivableDao().findAllCustomerReceivable());
		
		List<CustomerReceivable> customerReceivables = new ArrayList<CustomerReceivable>();
		
		// only receivables that're not fully paid
		BigDecimal totalReceivable = BigDecimal.ZERO;
		for(CustomerReceivable receivable : getCustomerReceivableList()) {
			BigDecimal amountOwed = getTotalOwing(receivable.getCustomerReceivableActivities());
			if (amountOwed.compareTo(BigDecimal.ZERO)==1) {
				customerReceivables.add(receivable);
			}
			
			totalReceivable = totalReceivable.add(amountOwed);
		}
		customerReceivables.sort((o1, o2) -> {
			Customer o1_customer = null, o2_customer = null;
			try {
				CustomerReceivable o1_receivable = getCustomerReceivableDao().findCustomerByProxy(o1.getId());
				o1_customer = o1_receivable.getCustomer();
				CustomerReceivable o2_receivable = getCustomerReceivableDao().findCustomerByProxy(o2.getId());
				o2_customer = o2_receivable.getCustomer();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return o1_customer.getCompanyLegalName().compareTo(o2_customer.getCompanyLegalName());
			
		});
		// list all receivables
		customerReceivableListInfo(customerReceivables);
		// display total receivables to date
		totalPiutangLanggananListfooter.setLabel(toLocalFormat(totalReceivable));
		// display activities
		if (customerReceivables.isEmpty()) {
			return;
		}
		customerReceivableActityListInfo(customerReceivables.get(0));
	}

	private void customerReceivableListInfo(List<CustomerReceivable> customerReceivables) {
		customerReceivablesListbox.setModel(
				new ListModelList<CustomerReceivable>(customerReceivables));
		customerReceivablesListbox.setItemRenderer(
				getCustomerReceivablesListitemRenderer());
	}

	private ListitemRenderer<CustomerReceivable> getCustomerReceivablesListitemRenderer() {
		
		return new ListitemRenderer<CustomerReceivable>() {
			
			@Override
			public void render(Listitem item, CustomerReceivable receivable, int index) throws Exception {
				Listcell lc;
				
				CustomerReceivable receivableCustomerByProxy = getCustomerReceivableDao().findCustomerByProxy(receivable.getId());
				Customer customer = receivableCustomerByProxy.getCustomer();
				
				// nama perusahaan
				lc = new Listcell(customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				BigDecimal piutangLangganan =
						getTotalOwing(receivable.getCustomerReceivableActivities());				
				
				// total piutang
				lc = new Listcell(toLocalFormat(piutangLangganan));
				lc.setParent(item);
				
				log.info(customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName()+" - Piutang: "+
						toLocalFormat(piutangLangganan));

				item.setValue(receivable);
			}
		};
	}

	public void onSelect$customerReceivablesListbox(Event event) throws Exception {	
		CustomerReceivable selReceivable = customerReceivablesListbox.getSelectedItem().getValue();
		
		customerReceivableActityListInfo(selReceivable);
	}

	private void customerReceivableActityListInfo(CustomerReceivable receivable) throws Exception {		
		CustomerReceivable receivableCustomerByProxy = getCustomerReceivableDao().findCustomerByProxy(receivable.getId());
		Customer customer = receivableCustomerByProxy.getCustomer();
		log.info("Set the Selected Receivable for :"+
				customer.getCompanyType().toString()+"."+
				customer.getCompanyLegalName());

		// display the company name
		companyName.setValue("NP-"+formatTo4DigitWithLeadingZeo(receivable.getId().intValue())+" : "+
				customer.getCompanyType()+". "+
				customer.getCompanyLegalName());

		List<CustomerReceivableActivity> receivableActivities = receivable.getCustomerReceivableActivities();
		// using lambda expression
		// ref: https://stackoverflow.com/questions/33326657/sorting-multiple-attribute-with-lambda-expressions
		// sort according to transaction as it happened in CustomerOrder - 05/10/2021 - Mnt Setting nomor muda di paling atas
		receivableActivities.sort((o1, o2) -> {
			int cmp = Long.compare(o1.getCustomerOrderId(), o2.getCustomerOrderId());
			
			return cmp;
		});
		// list activities
		receivableActivityListbox.setModel(
				new ListModelList<CustomerReceivableActivity>(receivableActivities));
		receivableActivityListbox.setItemRenderer(
				getReceivableActivityListitemRenderer());
	
		BigDecimal totalOwing = getTotalOwing(receivableActivities); 
		log.info("TotalOwing="+toLocalFormat(totalOwing));
		
		// display total receivable
		totalPiutangListfooter.setLabel(toLocalFormat(totalOwing));
	}

	private BigDecimal getTotalOwing(List<CustomerReceivableActivity> receivableActivities) {
		BigDecimal totalOwing = BigDecimal.ZERO;
		
		for (CustomerReceivableActivity activity : receivableActivities) {
			if (activity.getReceivableStatus().compareTo(DocumentStatus.BATAL)==0) {
				continue;
			}
			if (!activity.isPaymentComplete()) {
				totalOwing = totalOwing.add(activity.getAmountSales().subtract(activity.getAmountPaid()));
			}
		}
		return totalOwing;
	}
	
	private ListitemRenderer<CustomerReceivableActivity> getReceivableActivityListitemRenderer() {
		
		return new ListitemRenderer<CustomerReceivableActivity>() {
			
			@Override
			public void render(Listitem item, CustomerReceivableActivity activity, int index) throws Exception {
				Listcell lc;
				
				Label label;
				Vbox vbox;
				
				CustomerOrder customerOrder = 
						getCustomerOrderDao().findCustomerOrderById(activity.getCustomerOrderId());
				List<CustomerOrderProduct> productList =
						customerOrder.getCustomerOrderProducts();
				
				// Order-No.
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(customerOrder.getDocumentSerialNumber().getSerialComp());
				// 
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(sht)
					label = new Label();
					label.setValue("Qty[Sht] ");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty (sht)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(Integer.toString(product.getSheetQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);					
					}
				}
				
				// Tgl.Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(dateToStringDisplay(
						asLocalDate(customerOrder.getOrderDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(kg)
					label = new Label();
					label.setValue("Qty[Kg]");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty(kg)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormatWithDecimal(product.getWeightQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);
					}
				}
				
				// Tgl.Jatuh-Tempo
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				LocalDate paymentDueDate = addDate(customerOrder.getCreditDay(), 
						asLocalDate(customerOrder.getOrderDate()));
				label.setValue(dateToStringDisplay(paymentDueDate, getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: kode
					label = new Label();
					label.setValue("Kode");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: kode
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getUserModInventoryCode());
						label.setStyle("white-space:nowrap;");
						label.setParent(vbox);					
					}
				}
				
				// Total-Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountSales()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: spesifikasi
					label = new Label();
					label.setValue("Spesifikasi");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: spesifikasi
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getDescription());
						label.setParent(vbox);
					}
				}
				
				// Tgl.Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getPaymentDate()==null? " - " :
						dateToStringDisplay(asLocalDate(activity.getPaymentDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: harga(rp.)
					label = new Label();
					label.setValue("Harga(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: harga
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingPrice())+(product.isByKg()? "/KG":"/SHT"));
						label.setStyle("float:right;");
						label.setParent(vbox);
					}
					// subTotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setParent(vbox);
					// ppn
					label = new Label();
					if (customerOrder.getOrderDate().before(asDate(END_OF_MARCH_2022))) {
						label.setValue("PPN 10%");						
					} else {
						label.setValue("PPN 11%");
					}
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue("Total(Rp.)");
					label.setParent(vbox);
				}
				
				// Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountPaid()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: subtotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					BigDecimal subTotal = BigDecimal.ZERO;
					BigDecimal ppn = BigDecimal.ZERO;
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingSubTotal()));
						label.setStyle("float:right;");
						label.setParent(vbox);
						
						subTotal = subTotal.add(product.getSellingSubTotal());
					}
					// subTotal
					label = new Label();
					label.setStyle("border-top: 2px dotted red;float:right;");
					label.setValue(toLocalFormat(subTotal)); // "999.999.999,-"
					label.setParent(vbox);
					ppn = subTotal.multiply(new BigDecimal(0.11));
					// ppn
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(ppn):"-");
					label.setStyle("float:right");
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(subTotal.add(ppn)):toLocalFormat(subTotal));
					label.setStyle("float:right");
					label.setParent(vbox);
				}
				
				// Sisa
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getRemainingAmount()));
				label.setParent(vbox);
				
				
				// Status
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				if (activity.getReceivableStatus().equals(DocumentStatus.BATAL)) {
					label.setValue("Batal");
					label.setSclass("badge badge-red");
					label.setParent(vbox);					
				} else {
					label.setValue(activity.isPaymentComplete() ? "Lunas" : "-");
					label.setParent(vbox);
				}

			}
		};
	}

	public void onClick$printCompanyReceivableButton(Event event) throws Exception {
		CustomerReceivable selReceivable = customerReceivablesListbox.getSelectedItem().getValue();

		Map<String, CustomerReceivable> arg = 
				Collections.singletonMap("selectedReceivable", selReceivable);
		
		Window reportReceivablesPrintWin = 
				(Window) Executions.createComponents("/receivables/print/CustomerReceivablesPrint.zul", null, arg);
		
		reportReceivablesPrintWin.doModal();
		
	}
	
	protected Customer getCustomerByProxy(long id) throws Exception {
		CustomerReceivable receivable = getCustomerReceivableDao().findCustomerByProxy(id);
		
		return receivable.getCustomer();
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
	
}
