package com.pyramix.swi.webui.inventory.sqlserver;

import com.pyramix.swi.domain.inventory.InventoryPacking;

public enum SqlServerInventoryShape {
	STK, CRT, SHT;
	
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
