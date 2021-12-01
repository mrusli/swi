package com.pyramix.swi.webui.customer;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.webui.common.PageMode;

public class CustomerData {

	private Customer customer;
	
	private PageMode pageMode;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
