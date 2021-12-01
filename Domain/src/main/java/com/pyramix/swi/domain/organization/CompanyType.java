package com.pyramix.swi.domain.organization;

public enum CompanyType {
	PT(0), PD(1), CV(2), TOKO(3), BANK(4), PRV(5);
	
	private int value;

	CompanyType(int value) {
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
			case 0: return "PT";
			case 1: return "PD";
			case 2: return "CV";
			case 3: return "TOKO";
			case 4: return "BANK";
			case 5: return "PRV";
			default:
				return null;
		}
	}
	
	public static CompanyType toCompanyType(int value) {
		switch (value) {
			case 0: return CompanyType.PT;
			case 1: return CompanyType.PD;
			case 2: return CompanyType.CV;
			case 3: return CompanyType.TOKO;
			case 4: return CompanyType.BANK;
			case 5: return CompanyType.PRV;
			default:
				return null;
		}
	}
}
