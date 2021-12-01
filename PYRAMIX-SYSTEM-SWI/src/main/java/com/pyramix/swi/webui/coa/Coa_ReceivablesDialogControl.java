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
import com.pyramix.swi.domain.coa.Coa_Receivables;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_ReceivablesDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4504890610821093652L;
	
	private Window coa_ReceivablesDialogWin;
	private Textbox coaNumberTextbox, coaNameTextbox;
	private Combobox dbcrCombobox;
	private Checkbox activeCheckbox;
	
	private Coa_ReceivablesData coa_ReceivablesData;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setCoa_ReceivablesData( 
				(Coa_ReceivablesData) arg.get("coaReceivablesData"));
	}

	public void onCreate$coa_ReceivablesDialogWin(Event event) throws Exception {
		// dbcrCombobox
		setupDbCrCombobox();
		
		// set pageMode
		setPageMode(getCoa_ReceivablesData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			coa_ReceivablesDialogWin.setTitle("Edit (Rubah) COA Piutang");
			break;
		case NEW:
			coa_ReceivablesDialogWin.setTitle("Create (Membuat) COA Piutang");
			break;
		default:
			break;
		}
		
		setCoaReceivablesInfo();
	}

	private void setupDbCrCombobox() throws Exception {
		Comboitem debitItem = new Comboitem();
		debitItem.setLabel("Debit");
		debitItem.setValue(true);
		debitItem.setParent(dbcrCombobox);
		
		Comboitem creditItem = new Comboitem();
		creditItem.setLabel("Kredit");
		creditItem.setValue(false);
		creditItem.setParent(dbcrCombobox);		
	}
	
	private void setCoaReceivablesInfo() throws Exception {
		Coa_Receivables receivables = getCoa_ReceivablesData().getCoaReceivables();
		
		if (receivables.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			
			coaNumberTextbox.setValue("");
			coaNameTextbox.setValue("");
			dbcrCombobox.setSelectedIndex(0);
			activeCheckbox.setChecked(true);			
		} else {
			// edit
			
			coaNumberTextbox.setValue(receivables.getMasterCoa().getMasterCoaComp());
			coaNumberTextbox.setAttribute("coa_05_Master", receivables.getMasterCoa());
			coaNameTextbox.setValue(receivables.getMasterCoa().getMasterCoaName());
			if (receivables.isDebitAccount()) {
				dbcrCombobox.setSelectedIndex(0);
			} else {
				dbcrCombobox.setSelectedIndex(1);
			}
			activeCheckbox.setChecked(receivables.isActive());
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
		Coa_Receivables userModifiedReceivables = getCoa_ReceivablesData().getCoaReceivables();
		userModifiedReceivables.setDebitAccount(dbcrCombobox.getSelectedItem().getValue());
		userModifiedReceivables.setCreateDate(asDate(getLocalDate()));
		userModifiedReceivables.setLastModified(asDate(getLocalDate()));
		userModifiedReceivables.setActive(activeCheckbox.isChecked());
		userModifiedReceivables.setMasterCoa((Coa_05_Master) coaNumberTextbox.getAttribute("coa_05_Master"));
		
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ? 
				Events.ON_CHANGE : Events.ON_OK, 
					coa_ReceivablesDialogWin, userModifiedReceivables);
		
		coa_ReceivablesDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		
		coa_ReceivablesDialogWin.detach();
	}

	public Coa_ReceivablesData getCoa_ReceivablesData() {
		return coa_ReceivablesData;
	}

	public void setCoa_ReceivablesData(Coa_ReceivablesData coa_ReceivablesData) {
		this.coa_ReceivablesData = coa_ReceivablesData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

}
