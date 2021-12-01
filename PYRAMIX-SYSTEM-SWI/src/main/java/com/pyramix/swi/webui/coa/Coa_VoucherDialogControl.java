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
import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_VoucherDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5068749440200270291L;

	private Window coa_VoucherDialogWin;
	private Textbox coaNumberTextbox, coaNameTextbox;  
	private Combobox dbcrCombobox, voucherTypeCombobox;
	private Checkbox activeCheckbox;
	
	private Coa_VoucherData coaVoucherData;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCoaVoucherData(
				(Coa_VoucherData) arg.get("coaVoucherData"));
	}

	public void onCreate$coa_VoucherDialogWin(Event event) throws Exception {
		setPageMode(getCoaVoucherData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			coa_VoucherDialogWin.setTitle("Merubah (Edit) Voucher COA");			
			break;
		case NEW:
			coa_VoucherDialogWin.setTitle("Membuat (Tambah) Voucher COA");
			break;
		default:
			break;
		}
		
		setupComboboxes();
		
		displayCoaVoucherInfo();
		
	}
	
	private void setupComboboxes() {
		Comboitem debitItem = new Comboitem();
		debitItem.setLabel("Debit");
		debitItem.setValue(true);
		debitItem.setParent(dbcrCombobox);
		
		Comboitem creditItem = new Comboitem();
		creditItem.setLabel("Kredit");
		creditItem.setValue(false);
		creditItem.setParent(dbcrCombobox);
		
		Comboitem comboitem;
		
		for (VoucherType voucherType : VoucherType.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(voucherType.toString());
			comboitem.setValue(voucherType);
			comboitem.setParent(voucherTypeCombobox);
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
	
	private void displayCoaVoucherInfo() {
		Coa_Voucher coaVoucher = getCoaVoucherData().getCoaVoucher();
				
		if (coaVoucher.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			coaNumberTextbox.setValue("");
			coaNumberTextbox.setAttribute("coa_05_Master", null);
			coaNameTextbox.setValue("");
			dbcrCombobox.setSelectedIndex(0);
			activeCheckbox.setChecked(true);

		} else {
			// edit
			coaNumberTextbox.setValue(coaVoucher.getMasterCoa().getMasterCoaComp());
			coaNumberTextbox.setAttribute("coa_05_Master", coaVoucher.getMasterCoa());
			coaNameTextbox.setValue(coaVoucher.getMasterCoa().getMasterCoaName());			
			if (coaVoucher.isDebitAccount()) {
				dbcrCombobox.setSelectedIndex(0);				
			} else {
				dbcrCombobox.setSelectedIndex(1);
			}
			for (Comboitem item : voucherTypeCombobox.getItems()) {
				if (item.getValue().equals(coaVoucher.getVoucherType())) {
					voucherTypeCombobox.setSelectedItem(item);
				}
			}
			activeCheckbox.setChecked(coaVoucher.isActive());
		}
		
	}

	public void onClick$saveButton(Event event) throws Exception {
		Coa_Voucher userModCoaVoucher = getCoaVoucherData().getCoaVoucher();
		userModCoaVoucher.setActive(activeCheckbox.isChecked());
		userModCoaVoucher.setCreateDate(asDate(getLocalDate()));
		userModCoaVoucher.setLastModified(asDate(getLocalDate()));
		userModCoaVoucher.setDebitAccount(dbcrCombobox.getSelectedItem().getValue());
		userModCoaVoucher.setVoucherType(voucherTypeCombobox.getSelectedItem().getValue());
		userModCoaVoucher.setMasterCoa((Coa_05_Master) coaNumberTextbox.getAttribute("coa_05_Master"));
		
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ?
				Events.ON_CHANGE : Events.ON_OK, coa_VoucherDialogWin, userModCoaVoucher);
		
		coa_VoucherDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		coa_VoucherDialogWin.detach();
	}

	/**
	 * @return the coaVoucherData
	 */
	public Coa_VoucherData getCoaVoucherData() {
		return coaVoucherData;
	}

	/**
	 * @param coaVoucherData the coaVoucherData to set
	 */
	public void setCoaVoucherData(Coa_VoucherData coaVoucherData) {
		this.coaVoucherData = coaVoucherData;
	}

	/**
	 * @return the pageMode
	 */
	public PageMode getPageMode() {
		return pageMode;
	}

	/**
	 * @param pageMode the pageMode to set
	 */
	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
}
