package com.pyramix.swi.webui.gl.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageHandler;

public class GeneralLedgerPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8959433012617765927L;

	private Iframe iframe;
	private PageHandler handler = new PageHandler();

	private List<GeneralLedger> allGLList;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setAllGLList(
				(List<GeneralLedger>) arg.get("allGLList"));
	}

	public void onCreate$generalLedgerPrintWin(Event event) throws Exception {
		// transfer the allGLList to headerData list
		List<GeneralLedgerHeader> glHeaderList = new ArrayList<GeneralLedgerHeader>();

		for (GeneralLedger gl : getAllGLList()) {
			GeneralLedgerHeader glHeader = new GeneralLedgerHeader();
			glHeader.setVoucherNo(gl.getVoucherNumber()==null ? " " : gl.getVoucherNumber().getSerialComp());
			glHeader.setTransactionInfo(gl.getDbcrDescription());
			glHeader.setDebitAmount(gl.getDebitAmount()==null ? " " : toLocalFormat(gl.getDebitAmount()));
			glHeader.setCreditAmount(gl.getCreditAmount()==null ? " " : toLocalFormat(gl.getCreditAmount()));
			
			glHeaderList.add(glHeader);
		}
		
		// dataField and dataList to pass data into the JasperReport
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		HashMap<String, Object> dataList = new HashMap<String, Object>();

		dataList.put("headerData", glHeaderList);
		dataField.put("printedDate", dateToStringDisplay(getLocalDate(), getLongDateFormat()));

		iframe.setContent(handler.generateReportAMedia(dataField, dataList, 
				"/gl/print/GeneralLedgerPrint.jasper", 
				"LaporanJurnal-"+
				dateToStringDisplay(getLocalDate(), getEmphYearMonthShort())));
	}
	
	public List<GeneralLedger> getAllGLList() {
		return allGLList;
	}

	public void setAllGLList(List<GeneralLedger> allGLList) {
		this.allGLList = allGLList;
	}
	
	
}
