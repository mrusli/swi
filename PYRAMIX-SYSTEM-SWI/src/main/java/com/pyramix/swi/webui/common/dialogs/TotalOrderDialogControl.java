package com.pyramix.swi.webui.common.dialogs;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.webui.common.GFCBaseController;

public class TotalOrderDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7519523733266675489L;

	private Window totalOrderDialog;
	private Decimalbox totalOrderDecimalbox;
	
	private BigDecimal totalOrder;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setTotalOrder((BigDecimal) arg.get("totalOrder"));
	}

	public void onCreate$totalOrderDialog(Event event) throws Exception {
		// totalOrderDialog.setTitle("Merubah Total Order");
		
		totalOrderDecimalbox.setLocale(getLocale());
		totalOrderDecimalbox.setValue(getTotalOrder());
	}
	
	public void onClick$changeButton(Event event) throws Exception {
		Events.sendEvent(Events.ON_CHANGE, totalOrderDialog, totalOrderDecimalbox.getValue());
		
		totalOrderDialog.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		totalOrderDialog.detach();
	}

	public BigDecimal getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(BigDecimal totalOrder) {
		this.totalOrder = totalOrder;
	}
}
