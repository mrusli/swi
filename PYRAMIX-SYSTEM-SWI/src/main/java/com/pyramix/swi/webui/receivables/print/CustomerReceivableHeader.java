package com.pyramix.swi.webui.receivables.print;

import java.util.List;

public class CustomerReceivableHeader {

	private String orderNo;
	
	private String orderDate;
	
	private String dueDate;
	
	private String totalOrder;
	
	private String paymentDate;
	
	private String paymentAmount;
	
	private String remainingAmount;
	
	private List<CustomerReceivableDetail> customerReceivableDetails;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(String totalOrder) {
		this.totalOrder = totalOrder;
	}

	public List<CustomerReceivableDetail> getCustomerReceivableDetails() {
		return customerReceivableDetails;
	}

	public void setCustomerReceivableDetails(List<CustomerReceivableDetail> customerReceivableDetails) {
		this.customerReceivableDetails = customerReceivableDetails;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(String remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	
}
