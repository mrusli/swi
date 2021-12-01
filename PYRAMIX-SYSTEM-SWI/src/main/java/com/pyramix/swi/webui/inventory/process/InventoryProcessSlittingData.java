package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;

import com.pyramix.swi.domain.organization.Customer;

public class InventoryProcessSlittingData {

	private BigDecimal slittingWidth;
	
	private int slittingLine;
	
	private BigDecimal totalLineWeight;
	
	private Customer slittingForCustomer;

	public BigDecimal getSlittingWidth() {
		return slittingWidth;
	}

	public void setSlittingWidth(BigDecimal slittingWidth) {
		this.slittingWidth = slittingWidth;
	}

	public int getSlittingLine() {
		return slittingLine;
	}

	public void setSlittingLine(int slittingLine) {
		this.slittingLine = slittingLine;
	}

	public BigDecimal getTotalLineWeight() {
		return totalLineWeight;
	}

	public void setTotalLineWeight(BigDecimal totalLineWeight) {
		this.totalLineWeight = totalLineWeight;
	}

	public Customer getSlittingForCustomer() {
		return slittingForCustomer;
	}

	public void setSlittingForCustomer(Customer slittingForCustomer) {
		this.slittingForCustomer = slittingForCustomer;
	}
	
}
