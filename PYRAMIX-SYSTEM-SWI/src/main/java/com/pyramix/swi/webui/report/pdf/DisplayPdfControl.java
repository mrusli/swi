package com.pyramix.swi.webui.report.pdf;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

import com.pyramix.swi.webui.common.GFCBaseController;

public class DisplayPdfControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1063697833554535598L;

	public void onCreate$displayPdfWin(Event event) throws Exception {
		
		Clients.evalJavaScript("displayPdf();");
		
	}
}
