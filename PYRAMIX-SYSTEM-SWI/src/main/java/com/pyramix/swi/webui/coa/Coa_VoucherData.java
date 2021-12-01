package com.pyramix.swi.webui.coa;

import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_VoucherData {

	private Coa_Voucher coaVoucher;
	
	private PageMode pageMode;

	/**
	 * @return the coaVoucher
	 */
	public Coa_Voucher getCoaVoucher() {
		return coaVoucher;
	}

	/**
	 * @param coaVoucher the coaVoucher to set
	 */
	public void setCoaVoucher(Coa_Voucher coaVoucher) {
		this.coaVoucher = coaVoucher;
	}

	/**
	 * @return the pageMode
	 */
	public PageMode getPageMode() {
		return pageMode;
	}

	/**
	 * @param pageMode the pageMode to set
	 */
	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
