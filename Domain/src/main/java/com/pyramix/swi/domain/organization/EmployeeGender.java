package com.pyramix.swi.domain.organization;

public enum EmployeeGender {
	Pria(0), Wanita(1);
	
	private int value;
	
	private EmployeeGender(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString(int value) {
		switch (value) {
			case 0:	return "Pria";
			case 1: return "Wanita";
	
			default:
				return null;
		}
	}
	
	public EmployeeGender toEmployeeGender(int value) {
		switch (value) {
			case 0:	return EmployeeGender.Pria;
			case 1: return EmployeeGender.Wanita;
	
			default:
				return null;
		}		
	}
}
