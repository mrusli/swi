package com.pyramix.swi.domain.customerorder;

public enum PaymentType {
	tunai(0), giro(1), bank(2);
	
	private int value;

	PaymentType(int value) {
		this.setValue(value);
	}

	public String toString(int value) {
		switch (value) {
			case 0: return "tunai";
			case 1: return "giro";
			case 2: return "bank";
			default:
				return null;
		}
	}
	
	public PaymentType toPaymentType(int value) {
		switch (value) {
			case 0: return PaymentType.tunai;
			case 1: return PaymentType.giro;
			case 2:	return PaymentType.bank;
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
