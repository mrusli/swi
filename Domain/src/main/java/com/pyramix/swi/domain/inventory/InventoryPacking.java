package com.pyramix.swi.domain.inventory;

public enum InventoryPacking {
	coil(0), petian(1), lembaran(2);

	private int value;
	
	InventoryPacking(int value) {
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
			case 0: return "Coil"; 
			case 1: return "Petian";	
			case 2: return "Lembaran";	
			default:
				return "undefined";
		}
	}
	
	public static InventoryPacking toInventoryPacking(int value) {
		switch (value) {
		case 0: return InventoryPacking.coil;
		case 1: return InventoryPacking.petian;
		case 2: return InventoryPacking.lembaran;
		default:
			return null;
		}
	}
}
