package com.pyramix.swi.webui.serial;

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

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class DocumentSerialNumberDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4268554277342066372L;
	
	private SerialNumberGenerator serialNumberGenerator;
	
	private Window documentSerialNumberDialogWin;
	private Combobox documentTypeCombobox;
	private Datebox serialDatebox;
	private Intbox serialNumberIntbox;
	private Textbox serialCompTextbox;
	
	private DocumentSerialNumberData documentSerialNumberData;
	private DocumentSerialNumber documentSerialNumber;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setDocumentSerialNumberData(
				(DocumentSerialNumberData) arg.get("documentSerialNumberData"));		
	}

	public void onCreate$documentSerialNumberDialogWin(Event event) throws Exception {
		setDocumentSerialNumber(
				getDocumentSerialNumberData().getDocumentSerialNumber());
		
		setPageMode(
				getDocumentSerialNumberData().getPageMode());
		
		documentSerialNumberDialogWin.setTitle("Edit Document Serial Number");
		
		serialDatebox.setFormat(getShortDateFormat());
		
		setupDocumentTypeCombobox();
		
		// display
		setDocumentSerialNumberInfo();
	}

	private void setupDocumentTypeCombobox() {
		Comboitem item;
		
		for (DocumentType type : DocumentType.values()) {
			item = new Comboitem();
			item.setLabel(type.toString());
			item.setValue(type);
			item.setParent(documentTypeCombobox);
		}
	}

	private void setDocumentSerialNumberInfo() {
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			for (Comboitem item : documentTypeCombobox.getItems()) {
				if (getDocumentSerialNumber().getDocumentType().equals(item.getValue())) {
					documentTypeCombobox.setSelectedItem(item);
				}
			}
			serialDatebox.setValue(getDocumentSerialNumber().getSerialDate());
			serialNumberIntbox.setValue(getDocumentSerialNumber().getSerialNo());
			serialCompTextbox.setValue(getDocumentSerialNumber().getSerialComp());
		} else {
			documentTypeCombobox.setValue("");
			serialDatebox.setValue(asDate(getLocalDate()));
			serialNumberIntbox.setValue(null);
			serialCompTextbox.setValue("");
		}
	}
	
	public void onClick$generateCompButton(Event event) throws Exception {
		DocumentType docType 	= documentTypeCombobox.getSelectedItem().getValue();

		String 	code 		= docType.toCode(docType.getValue());
		Date 	currentDate	= serialDatebox.getValue();
		int 	serialNum 	= getSerialNumberGenerator().getSerialNumber(docType, currentDate);		

		serialNumberIntbox.setValue(serialNum);

		serialCompTextbox.setValue(formatSerialComp(code, currentDate, serialNum));		
		
	}	

	public void onClick$saveButton(Event event) throws Exception {
		DocumentSerialNumber userModifiedSerNum = getDocumentSerialNumber();
		
		userModifiedSerNum.setDocumentType(documentTypeCombobox.getSelectedItem().getValue());
		userModifiedSerNum.setSerialDate(serialDatebox.getValue());
		userModifiedSerNum.setSerialNo(serialNumberIntbox.getValue());
		userModifiedSerNum.setSerialComp(serialCompTextbox.getValue());
		
		Events.sendEvent(
				getPageMode().compareTo(PageMode.EDIT)==0 ? Events.ON_CHANGE : Events.ON_OK, 
						documentSerialNumberDialogWin, userModifiedSerNum);
		
		documentSerialNumberDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		documentSerialNumberDialogWin.detach();
	}

	public DocumentSerialNumber getDocumentSerialNumber() {
		return documentSerialNumber;
	}

	public void setDocumentSerialNumber(DocumentSerialNumber documentSerialNumber) {
		this.documentSerialNumber = documentSerialNumber;
	}

	public DocumentSerialNumberData getDocumentSerialNumberData() {
		return documentSerialNumberData;
	}

	public void setDocumentSerialNumberData(DocumentSerialNumberData documentSerialNumberData) {
		this.documentSerialNumberData = documentSerialNumberData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}


}
