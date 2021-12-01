package com.pyramix.swi.webui.coa;

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

import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.persistence.coa.dao.Coa_VoucherDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_VoucherListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6447138479491097381L;

	private Coa_VoucherDao coa_VoucherDao;

	private Label formTitleLabel;
	private Listbox coa_VoucherListbox;
	
	private List<Coa_Voucher> coaVoucherList;
	
	public void onCreate$coa_VoucherListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Voucher COA");
		
		// load and display
		displayCoaVoucherListInfo();
	}

	private void displayCoaVoucherListInfo() throws Exception {
		setCoaVoucherList(
				getCoa_VoucherDao().findAllCoaVoucher());
		coa_VoucherListbox.setModel(
				new ListModelList<Coa_Voucher>(getCoaVoucherList()));
		coa_VoucherListbox.setItemRenderer(
				getCoaVoucherListitemRenderer());
	}
	
	private ListitemRenderer<Coa_Voucher> getCoaVoucherListitemRenderer() {

		return new ListitemRenderer<Coa_Voucher>() {
			
			@Override
			public void render(Listitem item, Coa_Voucher coaVoucher, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(coaVoucher.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(coaVoucher.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Tipe Voucher
				lc = new Listcell(coaVoucher.getVoucherType().toString());
				lc.setParent(item);
				
				// Akun Debit/Kredit
				lc = new Listcell(coaVoucher.isDebitAccount() ? "Debit" : "Kredit");
				lc.setParent(item);
				
				// COA Aktif
				lc = new Listcell(coaVoucher.isActive() ? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				// edit
				lc = initEditButton(new Listcell(), coaVoucher);
				lc.setParent(item);
			}

			private Listcell initEditButton(Listcell listcell, Coa_Voucher coaVoucher) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						Coa_VoucherData coaVoucherData = new Coa_VoucherData();
						coaVoucherData.setCoaVoucher(coaVoucher);
						coaVoucherData.setPageMode(PageMode.EDIT);
						
						Map<String, Coa_VoucherData> arg = 
								Collections.singletonMap("coaVoucherData", coaVoucherData);
						
						Window coaVoucherEditWin = 
								(Window) Executions.createComponents("/coa/Coa_VoucherDialog.zul", null, arg);
						
						coaVoucherEditWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// update
								getCoa_VoucherDao().update((Coa_Voucher) event.getData());
								
								// load and update
								displayCoaVoucherListInfo();
							}
						});
						
						coaVoucherEditWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		Coa_VoucherData coaVoucherData = new Coa_VoucherData();
		coaVoucherData.setCoaVoucher(new Coa_Voucher());
		coaVoucherData.setPageMode(PageMode.NEW);
		
		Map<String, Coa_VoucherData> arg = 
				Collections.singletonMap("coaVoucherData", coaVoucherData);
		
		Window coaVoucherEditWin = 
				(Window) Executions.createComponents("/coa/Coa_VoucherDialog.zul", null, arg);
		
		coaVoucherEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// update
				getCoa_VoucherDao().save((Coa_Voucher) event.getData());
				
				// load and update
				displayCoaVoucherListInfo();
			}
		});
		
		coaVoucherEditWin.doModal();		
	}
	
	public Coa_VoucherDao getCoa_VoucherDao() {
		return coa_VoucherDao;
	}

	public void setCoa_VoucherDao(Coa_VoucherDao coa_VoucherDao) {
		this.coa_VoucherDao = coa_VoucherDao;
	}

	public List<Coa_Voucher> getCoaVoucherList() {
		return coaVoucherList;
	}

	public void setCoaVoucherList(List<Coa_Voucher> coaVoucherList) {
		this.coaVoucherList = coaVoucherList;
	}
	
}
