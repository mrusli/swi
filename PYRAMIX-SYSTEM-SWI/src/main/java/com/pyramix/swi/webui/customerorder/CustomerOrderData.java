package com.pyramix.swi.webui.customerorder;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.webui.common.PageMode;

public class CustomerOrderData {

	private PageMode pageMode;

	private CustomerOrder customerOrder;

	private CustomerOrderDao customerOrderDao;
	
	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}
}
