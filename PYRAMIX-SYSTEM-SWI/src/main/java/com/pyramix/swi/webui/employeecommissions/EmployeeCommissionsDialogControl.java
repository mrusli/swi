package com.pyramix.swi.webui.employeecommissions;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import com.pyramix.swi.webui.common.GFCBaseController;

public class EmployeeCommissionsDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4469478911489591266L;

	private Window employeeCommissionsDialogWin;
	
	public void onCreate$employeeCommissionsDialogWin(Event event) throws Exception {
		employeeCommissionsDialogWin.setTitle("Merubah (Edit) Komisi Penjualan Karyawan");
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		employeeCommissionsDialogWin.detach();
	}
}
