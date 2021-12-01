package com.pyramix.swi.webui.voucher;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherSerialNumberDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2228767240456478884L;
	
	private SerialNumberGenerator serialNumberGenerator;
	
	private Window voucherSerialNumberDialogWin;
	private Combobox voucherTypeCombobox;
	private Datebox serialDatebox;
	private Intbox serialNumberIntbox;
	private Textbox serialCompTextbox;
	
	private VoucherSerialNumberData voucherSerialNumberData;
	private VoucherSerialNumber voucherSerialNumber;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setVoucherSerialNumberData(
				(VoucherSerialNumberData) arg.get("voucherSerialNumberData"));
	}

	public void onCreate$voucherSerialNumberDialogWin(Event event) throws Exception {
		serialDatebox.setFormat(getShortDateFormat());
		
		setVoucherSerialNumber(
				getVoucherSerialNumberData().getVoucherSerialNumber());
		setPageMode(
				getVoucherSerialNumberData().getPageMode());
		
		setupVoucherTypeCombobox();
		
		voucherSerialNumberDialogWin.setTitle("Edit Voucher Serial Number");
		
		// load
		setVoucherSerialNumberInfo();
	}

	private void setupVoucherTypeCombobox() {
		Comboitem item;
		
		for (VoucherType type : VoucherType.values()) {
			item = new Comboitem();
			item.setLabel(type.toString());
			item.setValue(type);
			item.setParent(voucherTypeCombobox);
		}
	}

	private void setVoucherSerialNumberInfo() {
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			for (Comboitem item : voucherTypeCombobox.getItems()) {
				if (getVoucherSerialNumber().getVoucherType().compareTo(item.getValue())==0) {
					voucherTypeCombobox.setSelectedItem(item);
				}
			}
			serialDatebox.setValue(getVoucherSerialNumber().getSerialDate());
			serialNumberIntbox.setValue(getVoucherSerialNumber().getSerialNo());
			serialCompTextbox.setValue(getVoucherSerialNumber().getSerialComp());			
		} else {
			voucherTypeCombobox.setValue("");
			serialDatebox.setValue(asDate(getLocalDate()));
			serialNumberIntbox.setValue(null);
			serialCompTextbox.setValue(null);
		}
		
	}

	public void onClick$generateCompButton(Event event) throws Exception {
		VoucherType voucherType = voucherTypeCombobox.getSelectedItem().getValue();
		
		String 	vouCode 	= voucherType.toCode(voucherType.getValue());
		Date 	currentDate	= serialDatebox.getValue();
		int		serialNum	= 0;
		
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			serialCompTextbox.setValue(formatSerialComp(vouCode, currentDate, serialNumberIntbox.getValue()));			
		} else {
			serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);

			serialNumberIntbox.setValue(serialNum);
			serialCompTextbox.setValue(formatSerialComp(vouCode, currentDate, serialNum));			
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		VoucherSerialNumber userModifiedSerNum = getVoucherSerialNumber();
		userModifiedSerNum.setVoucherType(voucherTypeCombobox.getSelectedItem().getValue());
		userModifiedSerNum.setSerialDate(serialDatebox.getValue());
		userModifiedSerNum.setSerialNo(serialNumberIntbox.getValue());
		userModifiedSerNum.setSerialComp(serialCompTextbox.getValue());
		
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ?
				Events.ON_CHANGE : Events.ON_OK, voucherSerialNumberDialogWin, userModifiedSerNum);
		
		voucherSerialNumberDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		voucherSerialNumberDialogWin.detach();
	}
	
	public VoucherSerialNumberData getVoucherSerialNumberData() {
		return voucherSerialNumberData;
	}

	public void setVoucherSerialNumberData(VoucherSerialNumberData voucherSerialNumberData) {
		this.voucherSerialNumberData = voucherSerialNumberData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public VoucherSerialNumber getVoucherSerialNumber() {
		return voucherSerialNumber;
	}

	public void setVoucherSerialNumber(VoucherSerialNumber voucherSerialNumber) {
		this.voucherSerialNumber = voucherSerialNumber;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

}
