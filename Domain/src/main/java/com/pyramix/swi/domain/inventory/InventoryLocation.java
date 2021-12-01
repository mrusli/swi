package com.pyramix.swi.domain.inventory;

public enum InventoryLocation {
	swi(0), sunter(1), karawang(2);
	
	private int value;
	
	InventoryLocation(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static String toString(int value) {
		switch (value) {
			case 0: return "swi";
			case 1: return "sunter";	
			case 2: return "karawang";
			
			default:
				return "undefined";
		}
	}

	public static InventoryLocation toInventoryLocation(int value) {
		switch (value) {
		case 0: return InventoryLocation.swi;
		case 1: return InventoryLocation.sunter;
		case 2: return InventoryLocation.karawang;
		default:
			return null;
		}
	}
}
