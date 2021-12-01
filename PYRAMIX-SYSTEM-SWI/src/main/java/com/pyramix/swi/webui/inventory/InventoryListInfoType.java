package com.pyramix.swi.webui.inventory;

public enum InventoryListInfoType {
	Process(0), Transfer(1), BukaPeti(2), CustomerOrder(3);
	
	private int value;
	
	InventoryListInfoType(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
