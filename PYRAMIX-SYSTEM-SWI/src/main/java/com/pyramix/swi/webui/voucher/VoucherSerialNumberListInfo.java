package com.pyramix.swi.webui.voucher;

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

import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.persistence.voucher.dao.VoucherSerialNumberDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherSerialNumberListInfo extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -529350572942092480L;

	private VoucherSerialNumberDao voucherSerialNumberDao;
	
	private Label formTitleLabel;
	private Listbox voucherSerialNumberListbox;
	
	private List<VoucherSerialNumber> voucherSerialNumberList;
	
	public void onCreate$voucherSerialNumberListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Voucher Serial Number");
		
		// load and list
		listVoucherSerialNumber();
	}

	private void listVoucherSerialNumber() throws Exception {
		setVoucherSerialNumberList(
				getVoucherSerialNumberDao().findAllVoucherSerialNumber());
				
		voucherSerialNumberListbox.setModel(
				new ListModelList<VoucherSerialNumber>(getVoucherSerialNumberList()));
		voucherSerialNumberListbox.setItemRenderer(getVoucherSerialNumberListitemRenderer());
		
	}

	private ListitemRenderer<VoucherSerialNumber> getVoucherSerialNumberListitemRenderer() {
		
		return new ListitemRenderer<VoucherSerialNumber>() {
			
			@Override
			public void render(Listitem item, VoucherSerialNumber serialNum, int index) throws Exception {
				Listcell lc;
				
				// Voucher Type
				lc = new Listcell(serialNum.getVoucherType().toString());
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
				lc = initEditVoucherSerialNumber(new Listcell(), serialNum);
				lc.setParent(item);
				
			}

			private Listcell initEditVoucherSerialNumber(Listcell listcell, VoucherSerialNumber serialNum) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						VoucherSerialNumberData voucherSerNumData = new VoucherSerialNumberData();
						voucherSerNumData.setVoucherSerialNumber(serialNum);
						voucherSerNumData.setPageMode(PageMode.EDIT);
						
						Map<String, VoucherSerialNumberData> arg = 
								Collections.singletonMap("voucherSerialNumberData", voucherSerNumData);
						
						Window editVoucherSerialNumberWin = 
								(Window) Executions.createComponents("/voucher/VoucherSerialNumberDialog.zul", null, arg);
						
						editVoucherSerialNumberWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								getVoucherSerialNumberDao().update((VoucherSerialNumber) event.getData());
								
								// load
								listVoucherSerialNumber();
								
							}
						});
						
						editVoucherSerialNumberWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		VoucherSerialNumberData voucherSerNumData = new VoucherSerialNumberData();
		voucherSerNumData.setVoucherSerialNumber(new VoucherSerialNumber());
		voucherSerNumData.setPageMode(PageMode.NEW);
		
		Map<String, VoucherSerialNumberData> arg = 
				Collections.singletonMap("voucherSerialNumberData", voucherSerNumData);
		
		Window addVoucherSerialNumberWin = 
				(Window) Executions.createComponents("/voucher/VoucherSerialNumberDialog.zul", null, arg);
		
		addVoucherSerialNumberWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// save
				getVoucherSerialNumberDao().save((VoucherSerialNumber) event.getData());
				// load
				listVoucherSerialNumber();
			}
		});
		
		addVoucherSerialNumberWin.doModal();
		
	}
	
	public VoucherSerialNumberDao getVoucherSerialNumberDao() {
		return voucherSerialNumberDao;
	}

	public void setVoucherSerialNumberDao(VoucherSerialNumberDao voucherSerialNumberDao) {
		this.voucherSerialNumberDao = voucherSerialNumberDao;
	}

	public List<VoucherSerialNumber> getVoucherSerialNumberList() {
		return voucherSerialNumberList;
	}

	public void setVoucherSerialNumberList(List<VoucherSerialNumber> voucherSerialNumberList) {
		this.voucherSerialNumberList = voucherSerialNumberList;
	}

}
