package com.pyramix.swi.webui.serial;

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.webui.common.PageMode;

public class DocumentSerialNumberData {

	private DocumentSerialNumber documentSerialNumber;
	
	private PageMode pageMode;

	public DocumentSerialNumber getDocumentSerialNumber() {
		return documentSerialNumber;
	}

	public void setDocumentSerialNumber(DocumentSerialNumber documentSerialNumber) {
		this.documentSerialNumber = documentSerialNumber;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
