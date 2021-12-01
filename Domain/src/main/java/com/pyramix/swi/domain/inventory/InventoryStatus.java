package com.pyramix.swi.domain.inventory;

public enum InventoryStatus {
	incoming(0), process(1), ready(2), sold(3), bukapeti(4), transfer(5);

	private int value;
	
	InventoryStatus(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
