package com.pyramix.swi.domain.serial;

public enum DocumentType {
	PROCESS_ORDER(0), TRANSFER_ORDER(1), BUKAPETI_ORDER(2), DELIVERY_ORDER(3), 
		CUSTOMER_ORDER(4), SURATAJALAN(5), FAKTUR(6), CUSTOMER(7), EMPLOYEE(8),
			SETTLEMENT(9), RECEIVABLE(10), NON_PPN_ORDER(11), NON_PPN_SURATJALAN(12),
				NON_PPN_FAKTUR(13), PROCESS_ORDER_COMPLETE(14);
	
	private int value;
	
	DocumentType(int value) {
		setValue(value);
	}

	public String toCode(int value) {
		switch (value) {
			case 0: return "PR";
			case 1: return "TR";
			case 2: return "BP";
			case 3: return "DO";
			case 4: return "CO";
			case 5: return "SJ";
			case 6: return "FK";
			case 7: return "CR";
			case 8: return "EE";
			case 9: return "SM";
			case 10: return "RV";
			case 11: return "NP";
			case 12: return "NS";
			case 13: return "NF";
			case 14: return "PC";
			default:
				return null;
		}
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
