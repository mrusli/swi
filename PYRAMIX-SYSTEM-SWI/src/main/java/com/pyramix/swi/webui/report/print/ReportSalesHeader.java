package com.pyramix.swi.webui.report.print;

import java.util.List;

public class ReportSalesHeader {

	private Long id;
	
	private String customerOrderNo;
	
	private String suratJalanNo;
	
	private String namaCustomer;
	
	private String namaSales;
	
	private String pembayaran;
	
	private String orderSubTotal;
	
	private String orderPpnTotal;
	
	private String orderTotal;

	private List<ReportSalesDetail> salesDetails;
	
	public String toString() {
		return "headerData[id="+getId()+", namaCustomer="+getNamaCustomer()+", salesDetails="+getSalesDetails()+"]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNamaCustomer() {
		return namaCustomer;
	}

	public void setNamaCustomer(String namaCustomer) {
		this.namaCustomer = namaCustomer;
	}

	public List<ReportSalesDetail> getSalesDetails() {
		return salesDetails;
	}

	public void setSalesDetails(List<ReportSalesDetail> salesDetails) {
		this.salesDetails = salesDetails;
	}

	public String getCustomerOrderNo() {
		return customerOrderNo;
	}

	public void setCustomerOrderNo(String customerOrderNo) {
		this.customerOrderNo = customerOrderNo;
	}

	public String getNamaSales() {
		return namaSales;
	}

	public void setNamaSales(String namaSales) {
		this.namaSales = namaSales;
	}

	public String getPembayaran() {
		return pembayaran;
	}

	public void setPembayaran(String pembayaran) {
		this.pembayaran = pembayaran;
	}

	public String getSuratJalanNo() {
		return suratJalanNo;
	}

	public void setSuratJalanNo(String suratJalanNo) {
		this.suratJalanNo = suratJalanNo;
	}

	public String getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public String getOrderSubTotal() {
		return orderSubTotal;
	}

	public void setOrderSubTotal(String orderSubTotal) {
		this.orderSubTotal = orderSubTotal;
	}

	public String getOrderPpnTotal() {
		return orderPpnTotal;
	}

	public void setOrderPpnTotal(String orderPpnTotal) {
		this.orderPpnTotal = orderPpnTotal;
	}
	
	
}
