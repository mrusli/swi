package com.pyramix.swi.webui.customerorder;

import java.util.List;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.organization.Customer;

public class CustomerOrderListData {
	
	private Customer selectedCustomer;
	
	private List<CustomerOrder> selectedCustomerOrder;

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public List<CustomerOrder> getSelectedCustomerOrder() {
		return selectedCustomerOrder;
	}

	public void setSelectedCustomerOrder(List<CustomerOrder> selectedCustomerOrder) {
		this.selectedCustomerOrder = selectedCustomerOrder;
	}
	
}
