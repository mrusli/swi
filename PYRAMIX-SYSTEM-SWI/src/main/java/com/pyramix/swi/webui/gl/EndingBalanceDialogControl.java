package com.pyramix.swi.webui.gl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.EndingBalance;
import com.pyramix.swi.webui.common.GFCBaseController;

public class EndingBalanceDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6495855329976434583L;

	private Window endingBalanceDialogWin;
	private Datebox endingDatebox;
	private Textbox coaMasterTextbox, coaMasterNameTextbox, endingAmountTextbox;
	
	private EndingBalance endingBalance;
	private BigDecimal totalValue;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setEndingBalance(
				(EndingBalance) arg.get("endingBalance"));
	}
	
	public void onCreate$endingBalanceDialogWin(Event event) throws Exception {
		endingBalanceDialogWin.setTitle(getEndingBalance().getId().equals(Long.MIN_VALUE) ?
				"Tambah (Add) Ending Balance" : "Rubah (Edit) Ending Balance");
		
		// set datebox
		endingDatebox.setLocale(getLocale());
		endingDatebox.setFormat(getLongDateFormat());
		
		// set info
		setEndingBalanceInfo();
	}

	private void setEndingBalanceInfo() {
		if (getEndingBalance().getId().equals(Long.MIN_VALUE)) {
			// new
			coaMasterTextbox.setValue(""); 
			coaMasterNameTextbox.setValue("");
			endingDatebox.setValue(asDate(getLocalDate()));
			endingAmountTextbox.setValue("");
			setTotalValue(BigDecimal.ZERO);
		} else {
			// edit
			coaMasterTextbox.setValue(getEndingBalance().getMasterCoa().getMasterCoaComp());
			coaMasterTextbox.setAttribute("masterCoa", getEndingBalance().getMasterCoa());
			coaMasterNameTextbox.setValue(getEndingBalance().getMasterCoa().getMasterCoaName());
			endingDatebox.setValue(getEndingBalance().getEndingDate());
			endingAmountTextbox.setValue(toLocalFormat(getEndingBalance().getEndingAmount()));
			setTotalValue(getEndingBalance().getEndingAmount());
		}
	}

	public void onClick$selectCoaMasterButton(Event event) throws Exception {
		Window masterCoaWin = (Window) Executions.createComponents(
				"/coa/Coa_05_MasterListDialog.zul", null, null);
		
		masterCoaWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
				
				// set coa number (No Akun) and object
				coaMasterTextbox.setValue(masterCoa.getMasterCoaComp());
				coaMasterTextbox.setAttribute("masterCoa", masterCoa);
				
				// set coa name
				coaMasterNameTextbox.setValue(masterCoa.getMasterCoaName());
			}
		});
		
		masterCoaWin.doModal();
		
	}
	
	public void onClick$editAmountButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getEndingBalance().getId().equals(Long.MIN_VALUE) ?
						BigDecimal.ZERO : getTotalValue());
		
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				endingAmountTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalValue((BigDecimal) event.getData());				
			}
		});
		
		totalOrderDialogWin.doModal();		
		
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		EndingBalance userModEndBalance = getUserModEndingBalance();
		
		Events.sendEvent(Events.ON_OK, endingBalanceDialogWin, userModEndBalance);
		
		endingBalanceDialogWin.detach();
	}
	
	private EndingBalance getUserModEndingBalance() {
		EndingBalance endBal = getEndingBalance();
		
		endBal.setMasterCoa((Coa_05_Master) coaMasterTextbox.getAttribute("masterCoa"));
		endBal.setEndingDate(endingDatebox.getValue());
		endBal.setEndingAmount(getTotalValue());
		
		return endBal;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		endingBalanceDialogWin.detach();
	}
	
	public EndingBalance getEndingBalance() {
		return endingBalance;
	}

	public void setEndingBalance(EndingBalance endingBalance) {
		this.endingBalance = endingBalance;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	
	
}
