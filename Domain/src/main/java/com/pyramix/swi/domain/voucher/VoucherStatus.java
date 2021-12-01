package com.pyramix.swi.domain.voucher;

public enum VoucherStatus {
	Submitted(0), Checked(1), Posted(2), Rejected(3);
	
	private int value;
	
	VoucherStatus(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public String toCode(int value) {
		switch (value) {
			case 0: return "s";
			case 1: return "c";
			case 2: return "p";
			case 3: return "r";
		default:
			return null;
		}		
	}
}
