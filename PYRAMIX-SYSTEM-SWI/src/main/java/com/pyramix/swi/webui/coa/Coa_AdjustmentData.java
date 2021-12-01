package com.pyramix.swi.webui.coa;

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_AdjustmentData {

	private Coa_Adjustment coaAdjustment;
	
	private PageMode pageMode;

	public Coa_Adjustment getCoaAdjustment() {
		return coaAdjustment;
	}

	public void setCoaAdjustment(Coa_Adjustment coaAdjustment) {
		this.coaAdjustment = coaAdjustment;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
}
