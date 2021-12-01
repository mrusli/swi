package com.pyramix.swi.domain.inventory.transfer;

public enum InventoryTransferStatus {

	permohonan, proses, selesai, batal;

	public String toCode(int value) {
		switch (value) {
			case 0: return "M";
			case 1: return "P";	
			case 2: return "S";
			case 3: return "B";
			default:
				return null;
		}
	}
	
}
