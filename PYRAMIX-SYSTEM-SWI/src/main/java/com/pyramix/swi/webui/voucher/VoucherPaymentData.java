package com.pyramix.swi.webui.voucher;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherPaymentData {

	private PageMode pageMode;
	
	private VoucherPayment voucherPayment;
		
	private Settlement settlement;
	
	private Giro giro;

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public Giro getGiro() {
		return giro;
	}

	public void setGiro(Giro giro) {
		this.giro = giro;
	}
	
}
