package com.pyramix.swi.webui.receivables;

import java.math.BigDecimal;
import java.time.LocalDate;
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
	private Listfooter totalPiutangListfooter, totalPiutangLanggananListfooter,
		totalPembelianListfooter, totalPembayaranListfooter;
	
	private List<CustomerReceivable> customerReceivableList;
	private CustomerReceivable selectedReceivable;
	// private BigDecimal totalPiutangLangganan;

	private final Logger log = Logger.getLogger(CustomerReceivablesListInfoControl.class);
	
	public void onCreate$customerReceivablesListInfoWin(Event event) throws Exception {
		log.info("Opening CustomerReceivableListInfo");
		
		formTitleLabel.setValue("Piutang Langganan (Customer)");

		log.info("Getting all the customer receivables where the latest due date is not null");
		setCustomerReceivableList(
				getCustomerReceivableDao().findCustomerReceivablePendingPayment());
		
		log.info("Updating customer receivables from the receivable activities");
		
		// update customerReceivable -- calculate the totalReceivable
		// -- NEED TO DO THIS : everytime user opens this page, the totalReceivable MUST BE UPDATED
		for (CustomerReceivable receivable : getCustomerReceivableList()) {
			Customer customer = getCustomerByProxy(receivable.getId());
			
			log.info("Updating Receivable for: "+
					customer.getCompanyType().toString()+"."+
					customer.getCompanyLegalName());
			
			BigDecimal totalOrder = // getTotalOrder(receivable.getCustomerReceivableActivities());
					getTotalOrder(receivable.getCustomer());
			log.info("Total Order: "+toLocalFormat(totalOrder));
			
			BigDecimal totalPayment = // getTotalPayment(receivable.getCustomerReceivableActivities());
					getTotalPayment(receivable.getCustomer());
			log.info("Total Payment: "+toLocalFormat(totalPayment));
			
			BigDecimal piutangLangganan = totalOrder.subtract(totalPayment);
			log.info("Total Receivable: "+toLocalFormat(piutangLangganan));
			
			if (receivable.getTotalReceivable().compareTo(piutangLangganan)==0) {
				// no changes to receivable activities. no need to update
				log.info("No changes to receivable activies.  No need to update into database.");
			} else {				
				// setTotalReceivable
				receivable.setTotalReceivable(piutangLangganan);
				// update
				log.info("Update into database");
				getCustomerReceivableDao().update(receivable);
			}
			
		}
		
		// clear the CustomerReceivable list
		getCustomerReceivableList().clear();
		
		log.info("Getting customer receivables where totalReceivable is greater than zero");
		setCustomerReceivableList(
				getCustomerReceivableDao().findCustomerReceivableWithNonZeroTotalReceivable());
		
		log.info("List customer receivables");
		customerReceivableListInfo();

		log.info("Getting the total customer receivables");
		BigDecimal totalReceivable = getCustomerReceivableDao().sumTotalReceivables();
		
		log.info("Total Receivable: "+toLocalFormat(totalReceivable));
		totalPiutangLanggananListfooter.setLabel(toLocalFormat(totalReceivable));
		
		customerReceivableActityListInfo(0);
	}

	private void customerReceivableListInfo() {
		customerReceivablesListbox.setModel(
				new ListModelList<CustomerReceivable>(getCustomerReceivableList()));
		customerReceivablesListbox.setItemRenderer(
				getCustomerReceivablesListitemRenderer());
	}

	private ListitemRenderer<CustomerReceivable> getCustomerReceivablesListitemRenderer() {
		
		// totalPiutangLangganan = BigDecimal.ZERO;
		
		return new ListitemRenderer<CustomerReceivable>() {
			
			@Override
			public void render(Listitem item, CustomerReceivable receivable, int index) throws Exception {
				Listcell lc;
				
				Customer customer = getCustomerByProxy(receivable.getId());
				
				// nama perusahaan
				lc = new Listcell(customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				BigDecimal totalOrder = // getTotalOrder(receivable.getCustomerReceivableActivities());
						getTotalOrder(customer);
				BigDecimal totalPayment = // getTotalPayment(receivable.getCustomerReceivableActivities());
						getTotalPayment(customer);
				BigDecimal piutangLangganan = totalOrder.subtract(totalPayment);
				
				// total piutang
				lc = new Listcell(toLocalFormat(piutangLangganan));
				lc.setParent(item);
				
				// totalPiutangLangganan = totalPiutangLangganan.add(piutangLangganan);
			}
		};
	}
	
	private void customerReceivableActityListInfo(int index) throws Exception {
		// CustomerReceivable selectedReceivable = getCustomerReceivableList().get(index);
		log.info("CustomerReceivable list size :"+getCustomerReceivableList().size());
		
		if (getCustomerReceivableList().size()>0) {
			CustomerReceivable customerRecivable = getCustomerReceivableList().get(index);
			
			setSelectedReceivable(customerRecivable);
			
			// display the company name
			Customer customerByProxy = getCustomerByProxy(getSelectedReceivable().getId());
			log.info("Set the Selected Receivable for :"+
					customerByProxy.getCompanyType().toString()+"."+
					customerByProxy.getCompanyLegalName());

			companyName.setValue("NP-"+formatTo4DigitWithLeadingZeo(getSelectedReceivable().getId().intValue())+" : "+
					customerByProxy.getCompanyType()+". "+
					customerByProxy.getCompanyLegalName());
			
			List<CustomerReceivableActivity> receivableActivities = getSelectedReceivable().getCustomerReceivableActivities();
			// using lambda expression
			// ref: https://stackoverflow.com/questions/33326657/sorting-multiple-attribute-with-lambda-expressions
			// sort according to transaction as it happened in CustomerOrder - 05/10/2021 - Mnt Setting nomor muda di paling atas
			receivableActivities.sort((o1, o2) -> {
				int cmp = Long.compare(o1.getCustomerOrderId(), o2.getCustomerOrderId());
				
				return cmp;
			});
			// reverse order - latest paymentDueDate first
			// ref: https://stackoverflow.com/questions/5894818/how-to-sort-arraylistlong-in-decreasing-order
			// Collections.reverse(receivableActivities);
			receivableActivityListbox.setModel(
					new ListModelList<CustomerReceivableActivity>(receivableActivities));
			receivableActivityListbox.setItemRenderer(
					getReceivableActivityListitemRenderer());
						
			log.info("Sum of AmountSales");
			BigDecimal totalOrder = // getCustomerReceivableDao().sumAmountSalesReceivableActivities(getSelectedReceivable().getCustomer());
					getTotalOrder(getSelectedReceivable().getCustomer());
			BigDecimal totalOrderPpn =
					getTotalorderPpn(getSelectedReceivable().getCustomer());
			log.info("Total AmountSales for :"+
					customerByProxy.getCompanyType().toString()+"."+
					customerByProxy.getCompanyLegalName()+
					" = "+
					toLocalFormat(totalOrder));
			log.info("Total AmountSales PPN for :"+
					customerByProxy.getCompanyType().toString()+"."+
					customerByProxy.getCompanyLegalName()+
					" = "+
					toLocalFormat(totalOrderPpn));
			
			log.info("Sum of AmountPaid");
			BigDecimal totalPayment = // getCustomerReceivableDao().sumAmountPaymentReceivableActivities(getSelectedReceivable().getCustomer());
					getTotalPayment(getSelectedReceivable().getCustomer());
			log.info("Total AmountPaid for :"+
					customerByProxy.getCompanyType().toString()+"."+
					customerByProxy.getCompanyLegalName()+
					" = "+
					toLocalFormat(totalPayment));

			BigDecimal totalOwing = getTotalOwing(receivableActivities); 
			log.info("TotalOwing="+toLocalFormat(totalOwing));
			
			totalPembelianListfooter.setLabel(toLocalFormat(totalOrder));
			totalPembayaranListfooter.setLabel(toLocalFormat(totalPayment));
			totalPiutangListfooter.setLabel(toLocalFormat(totalOwing));
		}
	}

	private BigDecimal getTotalOwing(List<CustomerReceivableActivity> receivableActivities) {
		BigDecimal totalOwing = BigDecimal.ZERO;
		
		for (CustomerReceivableActivity activity : receivableActivities) {
			if (activity.getReceivableStatus().compareTo(DocumentStatus.BATAL)==0) {
				continue;
			}
			if (activity.getPaymentDate()==null) {
				totalOwing = totalOwing.add(activity.getAmountSales());
			}
		}
		return totalOwing;
	}
	
	private BigDecimal getTotalOrder(Customer customerReceivable) throws Exception {		
		BigDecimal total = 
				getCustomerReceivableDao().sumAmountSalesReceivableActivities(customerReceivable);
		
		return total;
	}

	private BigDecimal getTotalorderPpn(Customer customerReceivable) throws Exception {
		BigDecimal total =
				getCustomerReceivableDao().sumAmountSalesPpnReceivableActivities(customerReceivable);
		
		return total;
	}
	
/*	private BigDecimal getTotalOrder(List<CustomerReceivableActivity> receivableActivities) {
		
		BigDecimal total = BigDecimal.ZERO;
		
		for (CustomerReceivableActivity activity : receivableActivities) {
			
			if (activity.getReceivableStatus().equals(DocumentStatus.NORMAL)) {
				total = total.add(activity.getAmountSales());				
			}
		}
		
		return total;
	}
*/

	private BigDecimal getTotalPayment(Customer customerReceivable) throws Exception {
		
		BigDecimal total =
				getCustomerReceivableDao().sumAmountPaymentReceivableActivities(customerReceivable);
		
		return total;
	}
	
/*	private BigDecimal getTotalPaymen(List<CustomerReceivableActivity> receivableActivities) {

		BigDecimal total = BigDecimal.ZERO;
		
		for (CustomerReceivableActivity activity : receivableActivities) {
			
			if (activity.getReceivableStatus().equals(DocumentStatus.NORMAL)) {
				total = total.add(activity.getAmountPaid());				
			}
		}
		
		return total;
	}
*/	
	
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
					label.setValue("PPN 10%");
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
					ppn = subTotal.multiply(new BigDecimal(0.1));
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
					label.setValue(activity.isPaymentComplete() ? "Lunas" : " - ");
					label.setParent(vbox);
				}

				// if the status of receivableActivity is 'BATAL', change the backgroud color to red
				// if (activity.getReceivableStatus().equals(DocumentStatus.BATAL)) {
				// 	item.setClass("red-background");
				// }
			}
		};
	}

	public void onSelect$customerReceivablesListbox(Event event) throws Exception {
		// System.out.println(customerReceivablesListbox.getSelectedIndex());
		
		int selIndex = customerReceivablesListbox.getSelectedIndex();
		
		customerReceivableActityListInfo(selIndex);
	}

	public void onClick$printCompanyReceivableButton(Event event) throws Exception {
		
		Map<String, CustomerReceivable> arg = 
				Collections.singletonMap("selectedReceivable", getSelectedReceivable());
		
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

	public CustomerReceivable getSelectedReceivable() {
		return selectedReceivable;
	}

	public void setSelectedReceivable(CustomerReceivable selectedReceivable) {
		this.selectedReceivable = selectedReceivable;
	}
	
}
