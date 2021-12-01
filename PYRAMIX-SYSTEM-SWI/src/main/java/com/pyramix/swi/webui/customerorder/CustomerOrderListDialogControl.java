package com.pyramix.swi.webui.customerorder;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerOrderListDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3297792536190029795L;

	private CustomerOrderDao customerOrderDao;
	
	private Window customerOrderListDialogWin;
	private Listbox customerOrderListbox;
	
	private List<CustomerOrder> customerOrderList;

	private Customer selectedCustomer;
	private CustomerOrderListData customerOrderListData;
	private List<CustomerOrder> selectedCustomerOrder;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrderListData(
				(CustomerOrderListData) arg.get("customerOrderListData"));
		
	}

	public void onCreate$customerOrderListDialogWin(Event event) throws Exception {
		setSelectedCustomer(
				getCustomerOrderListData().getSelectedCustomer());

		setSelectedCustomerOrder(
				getCustomerOrderListData().getSelectedCustomerOrder());

		customerOrderListDialogWin.setTitle("Pilih Customer Order: "+
				getSelectedCustomer().getCompanyType()+". "+
				getSelectedCustomer().getCompanyLegalName());
		
		setCustomerOrderList(
				getCustomerOrderDao().findAllCustomerOrder_By_CustomerPaymentComplete(
						getSelectedCustomer(), false));
		
		if (!getSelectedCustomerOrder().isEmpty()) {
			for (CustomerOrder selCustomerOrder : getSelectedCustomerOrder()) {
				for (CustomerOrder customerOrder : getCustomerOrderList()) {
					if (selCustomerOrder.getId().compareTo(customerOrder.getId())==0) {
						getCustomerOrderList().remove(customerOrder);
						
						break;
					}
				}
			}
		}
		
		customerOrderListbox.setModel(
				new ListModelList<CustomerOrder>(getCustomerOrderList()));
		customerOrderListbox.setItemRenderer(getCustomerOrderListitemRenderer());
	}
	
	private ListitemRenderer<CustomerOrder> getCustomerOrderListitemRenderer() {
		
		return new ListitemRenderer<CustomerOrder>() {
			
			@Override
			public void render(Listitem item, CustomerOrder order, int index) throws Exception {
				Listcell lc;
				
				// Customer
				// Customer customer = getCustomerByProxy(order.getId());
				
				// lc = new Listcell(customer!=null ?
				//		customer.getCompanyType()+". "+
				//		customer.getCompanyLegalName() :
				//			"tunai");
				//lc.setStyle("white-space: nowrap;");
				//lc.setParent(item);
				
				// No.CustomerOrder
				lc = new Listcell(order.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.Order
				lc = new Listcell(dateToStringDisplay(asLocalDate(order.getOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// No.SuratJalan
				SuratJalan suratJalanByProxy = getSuratJalanByProxy(order.getId());
				
				lc = new Listcell(suratJalanByProxy.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);

				// Tgl.SuratJalan
				lc = new Listcell(dateToStringDisplay(asLocalDate(suratJalanByProxy.getSuratJalanDate()),getShortDateFormat()));
				lc.setParent(item);
				
				// Nominal (Rp.)
				lc = new Listcell(toLocalFormat(order.getTotalOrder()));
				lc.setParent(item);
				
				// Jumlah Pembayaran
				lc = new Listcell(toLocalFormat(order.getAmountPaid()));
				lc.setParent(item);
								
				item.setValue(order);
				
				// if the status of customerOrder is 'BATAL', change the backgroud color to red
				if (order.getOrderStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");					
				}

			}
		};
	}
	
	public void onClick$selectButton(Event event) throws Exception {
		CustomerOrder selOrder = customerOrderListbox.getSelectedItem().getValue();
		
		Events.sendEvent(Events.ON_SELECT, customerOrderListDialogWin, selOrder);
		
		customerOrderListDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		customerOrderListDialogWin.detach();
	}

	protected Customer getCustomerByProxy(Long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id);
		
		return customerOrder.getCustomer();
	}	
	
	protected SuratJalan getSuratJalanByProxy(Long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
	}
	
	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public List<CustomerOrder> getCustomerOrderList() {
		return customerOrderList;
	}

	public void setCustomerOrderList(List<CustomerOrder> customerOrderList) {
		this.customerOrderList = customerOrderList;
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public CustomerOrderListData getCustomerOrderListData() {
		return customerOrderListData;
	}

	public void setCustomerOrderListData(CustomerOrderListData customerOrderListData) {
		this.customerOrderListData = customerOrderListData;
	}

	public List<CustomerOrder> getSelectedCustomerOrder() {
		return selectedCustomerOrder;
	}

	public void setSelectedCustomerOrder(List<CustomerOrder> selectedCustomerOrder) {
		this.selectedCustomerOrder = selectedCustomerOrder;
	}
}
