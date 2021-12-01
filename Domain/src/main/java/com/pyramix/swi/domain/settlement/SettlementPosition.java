package com.pyramix.swi.domain.settlement;

public enum SettlementPosition {
	zero(0), plus(1), minus(2);
	
	private int value;
	
	SettlementPosition(int value) {
		setValue(value);
	}

	public String toString(int value) {
		switch (value) {
			case 0: return "zero";
			case 1: return "plus";	
			case 2: return "minus";
			default:
				return null;
		}
	}

	public SettlementPosition toSettlementPosition(int value) {
		switch (value) {
			case 0: return SettlementPosition.zero; 
			case 1: return SettlementPosition.plus;
			case 2: return SettlementPosition.minus;
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
