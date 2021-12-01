package com.pyramix.swi.domain.faktur;

public enum FakturType {
	penjualan(1), manual(2);
	
	private int value;

	private FakturType(int value) {
		this.setValue(value);
	}

	public String toString(int value) {
		switch (value) {
			case 1: return "penjualan";
			case 2: return "manual";
			default:
				return null;
		}
	}
	
	public FakturType toFakturType(int value) {
		switch (value) {
			case 1: return FakturType.penjualan;
			case 2: return FakturType.manual;
			default:
				return null;
		}
	}
	
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	
}
