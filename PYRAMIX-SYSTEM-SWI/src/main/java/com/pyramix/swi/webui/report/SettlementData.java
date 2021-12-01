package com.pyramix.swi.webui.report;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;

public class SettlementData {

	private Settlement settlement;
	
	private VoucherPayment voucherPayment;
	
	private VoucherGiroReceipt voucherGiroReceipt;

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}
	
}
