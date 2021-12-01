package com.pyramix.swi.domain.deliveryorder;

public enum DeliveryOrderType {
	pindah(0), penjualan(1), manual(2);
	
	private int value;
	
	DeliveryOrderType(int value) {
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
			case 0:	return "pindah";
			case 1: return "penjualan";
			case 2: return "manual";
			default:
				return null;
		}
	}
	
	public DeliveryOrderType toDeliveryOrderType(int value) {
		switch (value) {
			case 0: return DeliveryOrderType.pindah;
			case 1: return DeliveryOrderType.penjualan;
			case 2: return DeliveryOrderType.manual;
			default:
				return null;
		}
	}
}
