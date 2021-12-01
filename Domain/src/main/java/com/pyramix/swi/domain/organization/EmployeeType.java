package com.pyramix.swi.domain.organization;

public enum EmployeeType {
	Direktur(0), Manager(1), Sales(2), Supervisor(3), Umum(4), Supir(5);
	
	private int value;
	
	EmployeeType(int value) {
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
			case 0:	return "Direktur";
			case 1:	return "Manager";
			case 2:	return "Sales";
			case 3:	return "Supervisor";
			case 4:	return "Umum";
			case 5: return "Supir";
			
			default:
				return null;
		}
	}
	
	public EmployeeType toEmployeeType(int value) {
		switch (value) {
			case 0:	return EmployeeType.Direktur;
			case 1:	return EmployeeType.Manager;
			case 2:	return EmployeeType.Sales;
			case 3:	return EmployeeType.Supervisor;
			case 4:	return EmployeeType.Umum;
			case 5: return EmployeeType.Supir;
			
			default:
				return null;
		}
	}
}
