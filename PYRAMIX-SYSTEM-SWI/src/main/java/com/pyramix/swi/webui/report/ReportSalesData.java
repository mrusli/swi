package com.pyramix.swi.webui.report;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.customerorder.CustomerOrder;

public class ReportSalesData {

	private List<CustomerOrder> customerOrders;
	
	private Date startDate, endDate;

	public List<CustomerOrder> getCustomerOrders() {
		return customerOrders;
	}

	public void setCustomerOrders(List<CustomerOrder> customerOrders) {
		this.customerOrders = customerOrders;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
