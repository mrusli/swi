package com.pyramix.swi.domain.inventory.bukapeti;

public enum InventoryBukaPetiStatus {

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
