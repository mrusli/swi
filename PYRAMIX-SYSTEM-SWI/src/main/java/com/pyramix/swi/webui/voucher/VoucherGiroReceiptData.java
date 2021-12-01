package com.pyramix.swi.webui.voucher;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherGiroReceiptData {
	
	private PageMode pageMode;
	
	private VoucherGiroReceipt voucherGiroReceipt;

	// NOTE: NOT USED (as of 2018-01-29) --> must go thru Settlement
	private VoucherSales voucherSales;
	
	private Settlement settlement;
	
	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}
}
