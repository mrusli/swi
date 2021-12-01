package com.pyramix.swi.webui.serial;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.persistence.serial.dao.DocumentSerialNumberDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class DocumentSerialNumberListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6321067380840367732L;

	private DocumentSerialNumberDao documentSerialNumberDao;
	
	private Label formTitleLabel;
	private Listbox documentSerialNumberListbox;
	
	private List<DocumentSerialNumber> documentSerialNumberList;
	
	public void onCreate$documentSerialNumberListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Document Serial Number");
		
		// load
		loadDocumentSerialNumber();
	}

	private void loadDocumentSerialNumber() throws Exception {
		
		setDocumentSerialNumberList(
				getDocumentSerialNumberDao().findAllDocumentSerialNumber());
		
		documentSerialNumberListbox.setModel(
				new ListModelList<DocumentSerialNumber>(getDocumentSerialNumberList()));
		documentSerialNumberListbox.setItemRenderer(getDocumentSerialNumberListitemRenderer());
	}

	private ListitemRenderer<DocumentSerialNumber> getDocumentSerialNumberListitemRenderer() {
		
		return new ListitemRenderer<DocumentSerialNumber>() {
			
			@Override
			public void render(Listitem item, DocumentSerialNumber serialNum, int index) throws Exception {
				Listcell lc;
				
				// Document Type
				lc = new Listcell(serialNum.getDocumentType().toString());
				lc.setParent(item);
				
				// Date
				lc = new Listcell(dateToStringDisplay(asLocalDate(serialNum.getSerialDate()), getShortDateFormat()));
				lc.setParent(item);				
				
				// Serial-No
				lc = new Listcell(String.valueOf(serialNum.getSerialNo()));
				lc.setParent(item);
				
				// Serial-Comp
				lc = new Listcell(serialNum.getSerialComp());
				lc.setParent(item);
				
				// edit
				lc = initEditDocumentSerialNumber(new Listcell(), serialNum);
				lc.setParent(item);

			}

			private Listcell initEditDocumentSerialNumber(Listcell listcell, DocumentSerialNumber serialNum) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						DocumentSerialNumberData serialNumData = new DocumentSerialNumberData();
						serialNumData.setDocumentSerialNumber(serialNum);
						serialNumData.setPageMode(PageMode.EDIT);
						
						Map<String, DocumentSerialNumberData> arg =
								Collections.singletonMap("documentSerialNumberData", serialNumData);
						
						Window documentSerialNumberWin = 
								(Window) Executions.createComponents("/serial/DocumentSerialNumberDialog.zul", null, arg);
						
						documentSerialNumberWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// update
								getDocumentSerialNumberDao().update((DocumentSerialNumber) event.getData());
								// display
								loadDocumentSerialNumber();
							}
						});
						
						documentSerialNumberWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		DocumentSerialNumberData serialNumData = new DocumentSerialNumberData();
		serialNumData.setDocumentSerialNumber(new DocumentSerialNumber());
		serialNumData.setPageMode(PageMode.NEW);
		
		Map<String, DocumentSerialNumberData> arg =
				Collections.singletonMap("documentSerialNumberData", serialNumData);
		
		Window documentSerialNumberWin = 
				(Window) Executions.createComponents("/serial/DocumentSerialNumberDialog.zul", null, arg);
		
		documentSerialNumberWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// update
				getDocumentSerialNumberDao().save((DocumentSerialNumber) event.getData());
				// display
				loadDocumentSerialNumber();
			}
		});
		
		documentSerialNumberWin.doModal();		
	}
	
	public DocumentSerialNumberDao getDocumentSerialNumberDao() {
		return documentSerialNumberDao;
	}

	public void setDocumentSerialNumberDao(DocumentSerialNumberDao documentSerialNumberDao) {
		this.documentSerialNumberDao = documentSerialNumberDao;
	}

	public List<DocumentSerialNumber> getDocumentSerialNumberList() {
		return documentSerialNumberList;
	}

	public void setDocumentSerialNumberList(List<DocumentSerialNumber> documentSerialNumberList) {
		this.documentSerialNumberList = documentSerialNumberList;
	}
}
