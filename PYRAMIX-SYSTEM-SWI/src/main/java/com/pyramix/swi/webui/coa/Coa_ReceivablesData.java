package com.pyramix.swi.webui.coa;

import com.pyramix.swi.domain.coa.Coa_Receivables;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_ReceivablesData {

	private Coa_Receivables coaReceivables;
	
	private PageMode pageMode;

	public Coa_Receivables getCoaReceivables() {
		return coaReceivables;
	}

	public void setCoaReceivables(Coa_Receivables coaReceivables) {
		this.coaReceivables = coaReceivables;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
