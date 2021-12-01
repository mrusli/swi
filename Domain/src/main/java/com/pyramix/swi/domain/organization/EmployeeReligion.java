package com.pyramix.swi.domain.organization;

public enum EmployeeReligion {
	Islam(0), Kristen(1), Katolik(2), Buddha(3), Hindu(4), LainLain(5);

	private int value;
	
	private EmployeeReligion(int value) {
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
			case 0: return "Islam";
			case 1: return "Kristen";
			case 2: return "Katolik";
			case 3: return "Buddha";
			case 4: return "Hindu";
			case 5: return "LainLain";
			
			default:
				return null;
		}
	}
	
	public EmployeeReligion toEmployeeReligion(int value) {
		switch (value) {
			case 0: return EmployeeReligion.Islam;
			case 1: return EmployeeReligion.Kristen;
			case 2: return EmployeeReligion.Katolik;
			case 3: return EmployeeReligion.Buddha;
			case 4: return EmployeeReligion.Hindu;
			case 5: return EmployeeReligion.LainLain;
	
			default:
				return null;
		}		
	}
}
