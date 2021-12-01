package com.pyramix.swi.domain.bank;

public enum BankAccountOwnerType {
	PERUSAHAAN(0), PRIBADI(1);
	
	private int value;
	
	BankAccountOwnerType(int value) {
		this.setValue(value);
	}
	
	public String toString(int value) {
		switch (value) {
			case 0: return "PERUSAHAAN";			
			case 1: return "PRIBADI";
			default:
				return null;
		}
	}

	public BankAccountOwnerType toBankAccountOwnerType(int value) {
		switch (value) {
			case 0: return PERUSAHAAN;
			case 1: return PRIBADI;
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
