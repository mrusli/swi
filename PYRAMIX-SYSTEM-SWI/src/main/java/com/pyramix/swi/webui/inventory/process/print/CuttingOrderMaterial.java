package com.pyramix.swi.webui.inventory.process.print;

import java.util.List;

public class CuttingOrderMaterial {

	private String inventoryCodeName;
	
	private String inventoryMarking;
	
	private String inventoryQtyKg;
	
	private String inventorySpec;
	
	private List<CuttingOrderProduct> cuttingOrderProducts;

	public String getInventoryCodeName() {
		return inventoryCodeName;
	}

	public void setInventoryCodeName(String inventoryCodeName) {
		this.inventoryCodeName = inventoryCodeName;
	}

	public String getInventoryMarking() {
		return inventoryMarking;
	}

	public void setInventoryMarking(String inventoryMarking) {
		this.inventoryMarking = inventoryMarking;
	}

	public List<CuttingOrderProduct> getCuttingOrderProducts() {
		return cuttingOrderProducts;
	}

	public void setCuttingOrderProducts(List<CuttingOrderProduct> cuttingOrderProducts) {
		this.cuttingOrderProducts = cuttingOrderProducts;
	}

	public String getInventoryQtyKg() {
		return inventoryQtyKg;
	}

	public void setInventoryQtyKg(String inventoryQtyKg) {
		this.inventoryQtyKg = inventoryQtyKg;
	}

	public String getInventorySpec() {
		return inventorySpec;
	}

	public void setInventorySpec(String inventorySpec) {
		this.inventorySpec = inventorySpec;
	}
	
}
