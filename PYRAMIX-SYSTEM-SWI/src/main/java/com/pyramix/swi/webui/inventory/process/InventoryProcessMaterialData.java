package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.util.List;

import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.organization.Customer;

public class InventoryProcessMaterialData {

	private InventoryProcessMaterial processMaterial;
	private BigDecimal materialRemainQuantity;

	// product data -- mainly for cut-to-length ONLY
	private BigDecimal shearingLength;
	private int shearingSheetQuantity;
	private BigDecimal shearingQuantityWeight;
	private Customer shearingCustomer;
	
	// product -- result of processing
	private List<InventoryProcessProduct> processProductList;
	
	private int processCount;
	
	public InventoryProcessMaterial getProcessMaterial() {
		return processMaterial;
	}

	public void setProcessMaterial(InventoryProcessMaterial processMaterial) {
		this.processMaterial = processMaterial;
	}

	public BigDecimal getMaterialRemainQuantity() {
		return materialRemainQuantity;
	}

	public void setMaterialRemainQuantity(BigDecimal materialRemainQuantity) {
		this.materialRemainQuantity = materialRemainQuantity;
	}

	public BigDecimal getShearingLength() {
		return shearingLength;
	}

	public void setShearingLength(BigDecimal shearingLength) {
		this.shearingLength = shearingLength;
	}

	public int getShearingSheetQuantity() {
		return shearingSheetQuantity;
	}

	public void setShearingSheetQuantity(int shearingSheetQuantity) {
		this.shearingSheetQuantity = shearingSheetQuantity;
	}

	public BigDecimal getShearingQuantityWeight() {
		return shearingQuantityWeight;
	}

	public void setShearingQuantityWeight(BigDecimal shearingQuantityWeight) {
		this.shearingQuantityWeight = shearingQuantityWeight;
	}

	public Customer getShearingCustomer() {
		return shearingCustomer;
	}

	public void setShearingCustomer(Customer shearingCustomer) {
		this.shearingCustomer = shearingCustomer;
	}

	public List<InventoryProcessProduct> getProcessProductList() {
		return processProductList;
	}

	public void setProcessProductList(List<InventoryProcessProduct> processProductList) {
		this.processProductList = processProductList;
	}

	public int getProcessCount() {
		return processCount;
	}

	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}
	
}
