package com.pyramix.swi.webui.report.print;

import java.math.BigDecimal;

public class ReportSalesDetail {

	private Long idDetail;
	
	private String quantity;
	
	private String code;
	
	private String description;

	private String price;
	
	private String subTotal;
	
	private BigDecimal bdSubTotal;
	
	public String toString() {
		return "detailData=[id="+getIdDetail()+", description="+getDescription()+"]";
	}
	
	public Long getIdDetail() {
		return idDetail;
	}

	public void setIdDetail(Long idDetail) {
		this.idDetail = idDetail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getBdSubTotal() {
		return bdSubTotal;
	}

	public void setBdSubTotal(BigDecimal bdSubTotal) {
		this.bdSubTotal = bdSubTotal;
	}

}
