package com.pyramix.swi.webui.receivables.print;

import java.math.BigDecimal;

public class CustomerReceivableDetail {

	private String sheetQuantity;

	private String weightQuantity;

	private String inventoryCode;

	private String description;

	private String sellingPrice;

	private String sellingSubTotal;

	private String quantityUnit;
	
	private BigDecimal bdSellingSubTotal;
	
	public String getSheetQuantity() {
		return sheetQuantity;
	}

	public void setSheetQuantity(String sheetQuantity) {
		this.sheetQuantity = sheetQuantity;
	}

	public String getWeightQuantity() {
		return weightQuantity;
	}

	public void setWeightQuantity(String weightQuantity) {
		this.weightQuantity = weightQuantity;
	}

	public String getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getSellingSubTotal() {
		return sellingSubTotal;
	}

	public void setSellingSubTotal(String sellingSubTotal) {
		this.sellingSubTotal = sellingSubTotal;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public BigDecimal getBdSellingSubTotal() {
		return bdSellingSubTotal;
	}

	public void setBdSellingSubTotal(BigDecimal bdSellingSubTotal) {
		this.bdSellingSubTotal = bdSellingSubTotal;
	}
	
}
