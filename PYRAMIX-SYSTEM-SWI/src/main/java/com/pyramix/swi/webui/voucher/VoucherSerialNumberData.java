package com.pyramix.swi.webui.voucher;

import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherSerialNumberData {

	private VoucherSerialNumber voucherSerialNumber;
	
	private PageMode pageMode;

	public VoucherSerialNumber getVoucherSerialNumber() {
		return voucherSerialNumber;
	}

	public void setVoucherSerialNumber(VoucherSerialNumber voucherSerialNumber) {
		this.voucherSerialNumber = voucherSerialNumber;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
