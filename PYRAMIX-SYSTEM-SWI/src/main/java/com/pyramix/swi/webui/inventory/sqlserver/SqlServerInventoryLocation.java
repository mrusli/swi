package com.pyramix.swi.webui.inventory.sqlserver;

import com.pyramix.swi.domain.inventory.InventoryLocation;

public enum SqlServerInventoryLocation {
	SWI, STR, KRG;
	
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
