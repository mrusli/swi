package com.pyramix.swi.webui.coa;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_AdjustmentDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4410496234786463128L;

	private Window coa_AdjustmentDialogWin;
	private Textbox coaNumberTextbox, coaNameTextbox;
	private Combobox dbcrCombobox;
	private Checkbox activeCheckbox;
	
	private Coa_AdjustmentData coa_AdjustmentData;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCoa_AdjustmentData(
				(Coa_AdjustmentData) arg.get("coaAdjustmentData"));
	}
	
	public void onCreate$coa_AdjustmentDialogWin(Event event) throws Exception {
		// dbcrCombobox
		setupDbCrCombobox();
		
		setPageMode(getCoa_AdjustmentData().getPageMode());
		
		switch (getPageMode()) {
			case EDIT:
				coa_AdjustmentDialogWin.setTitle("Edit (Rubah) COA Adjustment"); 
				break;
			case NEW:
				coa_AdjustmentDialogWin.setTitle("Create (Membuat) COA Adjustment"); 
				break;
			default:
				break;
		}
		
		setCoa_AdjustmentInfo();
		
	}
	
	private void setupDbCrCombobox() {
		Comboitem debitItem = new Comboitem();
		debitItem.setLabel("Debit");
		debitItem.setValue(true);
		debitItem.setParent(dbcrCombobox);
		
		Comboitem creditItem = new Comboitem();
		creditItem.setLabel("Kredit");
		creditItem.setValue(false);
		creditItem.setParent(dbcrCombobox);
	}

	private void setCoa_AdjustmentInfo() {
		Coa_Adjustment adjustment = getCoa_AdjustmentData().getCoaAdjustment();
		
		if (adjustment.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			
			coaNumberTextbox.setValue("");
			coaNameTextbox.setValue("");
			dbcrCombobox.setSelectedIndex(0);
			activeCheckbox.setChecked(true);
		} else {
			// edit
			
			coaNumberTextbox.setValue(adjustment.getMasterCoa().getMasterCoaComp());
			coaNumberTextbox.setAttribute("coa_05_Master", adjustment.getMasterCoa());
			coaNameTextbox.setValue(adjustment.getMasterCoa().getMasterCoaName());
			if (adjustment.isDebitAccount()) {
				dbcrCombobox.setSelectedIndex(0);
			} else {
				dbcrCombobox.setSelectedIndex(1);
			}
			activeCheckbox.setChecked(adjustment.isActive());
		}
		
	}
	
	public void onClick$coaSelectButton(Event event) throws Exception {
		Window coaSelectWin = 
				(Window) Executions.createComponents("/coa/Coa_05_MasterListDialog.zul", null, null);
		
		coaSelectWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_05_Master master = (Coa_05_Master) event.getData();
				
				coaNumberTextbox.setValue(master.getMasterCoaComp());
				coaNumberTextbox.setAttribute("coa_05_Master", master);
				coaNameTextbox.setValue(master.getMasterCoaName());
			}
		});
		
		coaSelectWin.doModal();
	}
		
	public void onClick$saveButton(Event event) throws Exception {
		Coa_Adjustment userModifiedAdjustment = getCoa_AdjustmentData().getCoaAdjustment();
		userModifiedAdjustment.setDebitAccount(dbcrCombobox.getSelectedItem().getValue());
		userModifiedAdjustment.setCreateDate(asDate(getLocalDate()));
		userModifiedAdjustment.setLastModified(asDate(getLocalDate()));
		userModifiedAdjustment.setActive(activeCheckbox.isChecked());
		userModifiedAdjustment.setMasterCoa((Coa_05_Master) coaNumberTextbox.getAttribute("coa_05_Master"));
		
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ? 
			Events.ON_CHANGE : Events.ON_OK, 
				coa_AdjustmentDialogWin, userModifiedAdjustment);
		
		coa_AdjustmentDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		coa_AdjustmentDialogWin.detach();
	}

	public Coa_AdjustmentData getCoa_AdjustmentData() {
		return coa_AdjustmentData;
	}

	public void setCoa_AdjustmentData(Coa_AdjustmentData coa_AdjustmentData) {
		this.coa_AdjustmentData = coa_AdjustmentData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
