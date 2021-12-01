package com.pyramix.swi.webui.user;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;

import com.pyramix.swi.webui.common.GFCBaseController;

public class ProfileWindowControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 336250552133134321L;

	private Label formTitleLabel;
	
	public void onCreate$profileWindow(Event event) throws Exception {
		formTitleLabel.setValue("User Profile");
	}
	
}
