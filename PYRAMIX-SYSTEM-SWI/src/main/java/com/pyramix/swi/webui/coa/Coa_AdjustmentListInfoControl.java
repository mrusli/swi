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

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.persistence.coa.dao.Coa_AdjustmentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_AdjustmentListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7438818123764053991L;

	private Coa_AdjustmentDao coa_AdjustmentDao;

	private Label formTitleLabel, infoResultlabel;
	private Listbox coa_AdjustmentMasterListbox;
	
	private List<Coa_Adjustment> coaAdjustmentList;
	
	public void onCreate$coa_AdjustmentListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("COA untuk Adjustment");
		
		// load
		displayCoa_AdjustmentListInfo();
		
	}

	private void displayCoa_AdjustmentListInfo() throws Exception {
		setCoaAdjustmentList(
				getCoa_AdjustmentDao().findAllCoaAdjustment());
		
		coa_AdjustmentMasterListbox.setModel(
				new ListModelList<Coa_Adjustment>(getCoaAdjustmentList()));
		coa_AdjustmentMasterListbox.setItemRenderer(
				getCoa_AdjustmentListitemRenderer());
	}

	private ListitemRenderer<Coa_Adjustment> getCoa_AdjustmentListitemRenderer() {
		
		return new ListitemRenderer<Coa_Adjustment>() {
			
			@Override
			public void render(Listitem item, Coa_Adjustment adjustment, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(adjustment.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(adjustment.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Akun Debit/Kredit
				lc = new Listcell(adjustment.isDebitAccount() ? "Debit" : "Kredit");
				lc.setParent(item);
				
				// Aktif
				lc = new Listcell(adjustment.isActive() ? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), adjustment);
				lc.setParent(item);
			}

			private Listcell initEditButton(Listcell listcell, Coa_Adjustment adjustment) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						Coa_AdjustmentData adjustmentData = new Coa_AdjustmentData();
						adjustmentData.setCoaAdjustment(adjustment);
						adjustmentData.setPageMode(PageMode.EDIT);
						
						Map<String, Coa_AdjustmentData> arg = 
								Collections.singletonMap("coaAdjustmentData", adjustmentData);
						
						Window coaAdjustmentWin = (Window) Executions.createComponents("/coa/Coa_AdjustmentDialog.zul", null, arg);
						
						coaAdjustmentWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								// update
								getCoa_AdjustmentDao().update((Coa_Adjustment) event.getData());
								
								// reload
								displayCoa_AdjustmentListInfo();
							}
						});
						
						coaAdjustmentWin.doModal();
					}

				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$coa_ReceivablesMasterListbox(Event event) throws Exception {
		int itemCount = coa_AdjustmentMasterListbox.getItemCount();
		
		infoResultlabel.setValue("Total: "+itemCount+" Items");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		Coa_AdjustmentData adjustmentData = new Coa_AdjustmentData();
		adjustmentData.setCoaAdjustment(new Coa_Adjustment());
		adjustmentData.setPageMode(PageMode.NEW);
		
		Map<String, Coa_AdjustmentData> arg = 
				Collections.singletonMap("coaAdjustmentData", adjustmentData);
		
		Window coaAdjustmentWin = (Window) Executions.createComponents("/coa/Coa_AdjustmentDialog.zul", null, arg);
		
		coaAdjustmentWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// save
				getCoa_AdjustmentDao().save((Coa_Adjustment) event.getData());
				
				// reload
				displayCoa_AdjustmentListInfo();
			}
		});
		
		coaAdjustmentWin.doModal();		
	}
	
	public Coa_AdjustmentDao getCoa_AdjustmentDao() {
		return coa_AdjustmentDao;
	}

	public void setCoa_AdjustmentDao(Coa_AdjustmentDao coa_AdjustmentDao) {
		this.coa_AdjustmentDao = coa_AdjustmentDao;
	}

	public List<Coa_Adjustment> getCoaAdjustmentList() {
		return coaAdjustmentList;
	}

	public void setCoaAdjustmentList(List<Coa_Adjustment> coaAdjustmentList) {
		this.coaAdjustmentList = coaAdjustmentList;
	}
}
