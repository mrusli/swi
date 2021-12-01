package com.pyramix.swi.domain.voucher;

public enum VoucherType {
	GENERAL(0), PETTYCASH(1), RECEIPT_GIRO(2), SALES_CASH(3), SALES_CREDIT(4), PAYMENT_CASH(5), PAYMENT_GIRO(6), 
		PAYMENT_BANK(7), BATAL(8), POSTING_GENERAL(9), POSTING_PETTYCASH(10), POSTING_SALESCASH(11), POSTING_SALESCREDIT(12),
			POSTING_RECEIPTGIRO(13), POSTING_PAYMENTBANK(14), POSTING_PAYMENTGIRO(15), POSTING_PAYMENTCASH(16);
	
	private int value;

	VoucherType(int value) {
		this.setValue(value);
	}
	
	public String toCode(int value) {
		switch (value) {
			case 0: return "JV";
			case 1: return "PC";
			case 2: return "RG";
			case 3: return "SO";
			case 4: return "SC";
			case 5: return "PC";
			case 6: return "PG";
			case 7: return "PB";
			case 8: return "BT";
			// posting voucher codes
			case 9:  return "PJV";
			case 10: return "PPC";
			case 11: return "PSO";
			case 12: return "PSC";
			case 13: return "PRG";
			case 14: return "PPB";
			case 15: return "PPG";
			case 16: return "PPC";
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
