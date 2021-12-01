package com.pyramix.swi.domain.customerorder;

public enum ProductType {
	barang(0), jasa_pengiriman(1), jasa_packing(2), jasa_pemotongan(3), lain_lain(4);
	
	private int value;
	
	ProductType(int value) {
		setValue(value);
	}

	public String toString(int value) {
		switch (value) {
			case 0: return "barang";
			case 1: return "jasa_pengiriman";
			case 2: return "jasa_packing";
			case 3: return "jasa_pemotongan";
			case 4: return "lain_lain";
			default:
				return null;
		}
	}
	
	public ProductType toProductType(int value) {
		switch (value) {
			case 0: return ProductType.barang;
			case 1: return ProductType.jasa_pengiriman;
			case 2: return ProductType.jasa_packing;
			case 3: return ProductType.jasa_pemotongan;
			case 4: return ProductType.lain_lain;
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
