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

import com.pyramix.swi.domain.coa.Coa_Receivables;
import com.pyramix.swi.persistence.coa.dao.Coa_ReceivablesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class Coa_ReceivablesListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4379540170386409448L;

	private Coa_ReceivablesDao coa_ReceivablesDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox coa_ReceivablesMasterListbox;
	
	private List<Coa_Receivables> coaReceivablesList;
	
	public void onCreate$coa_ReceivablesListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("COA untuk Piutang");
		
		// load
		displayCoa_ReceivablesListInfo();
	}
	
	public void displayCoa_ReceivablesListInfo() throws Exception {
		setCoaReceivablesList(
				getCoa_ReceivablesDao().findAllCoaReceivables());
		
		coa_ReceivablesMasterListbox.setModel(
				new ListModelList<Coa_Receivables>(getCoaReceivablesList()));
		coa_ReceivablesMasterListbox.setItemRenderer(
				getCoa_ReceivablesListitemRenderer());
	}

	private ListitemRenderer<Coa_Receivables> getCoa_ReceivablesListitemRenderer() {
		
		return new ListitemRenderer<Coa_Receivables>() {
			
			public void render(Listitem item, Coa_Receivables receivables, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(receivables.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(receivables.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Akun Debit/Kredit
				lc = new Listcell(receivables.isDebitAccount() ? "Debit" : "Kredit");
				lc.setParent(item);
				
				// COA Aktif
				lc = new Listcell(receivables.isActive() ? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				//
				lc = initEditButton(new Listcell(), receivables);
				lc.setParent(item);
				
			}
			
			private Listcell initEditButton(Listcell listcell, Coa_Receivables receivables) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						Coa_ReceivablesData receivablesData = new Coa_ReceivablesData();
						receivablesData.setCoaReceivables(receivables);
						receivablesData.setPageMode(PageMode.EDIT);
						
						Map<String, Coa_ReceivablesData> arg =
								Collections.singletonMap("coaReceivablesData", receivablesData);
						
						Window coaReceivablesWin = (Window) Executions.createComponents("/coa/Coa_ReceivablesDialog.zul", null, arg);

						coaReceivablesWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								// update
								getCoa_ReceivablesDao().update((Coa_Receivables) event.getData());
								
								// reload
								displayCoa_ReceivablesListInfo();
							}
							
						});
						
						coaReceivablesWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
			
		};
	}
	
	public void onAfterRender$coa_ReceivablesMasterListbox(Event event) throws Exception {
		int itemCount = coa_ReceivablesMasterListbox.getItemCount();
		
		infoResultlabel.setValue("Total: "+itemCount+" Items");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		Coa_ReceivablesData receivablesData = new Coa_ReceivablesData();
		receivablesData.setCoaReceivables(new Coa_Receivables());
		receivablesData.setPageMode(PageMode.NEW);
		
		Map<String, Coa_ReceivablesData> arg = 
				Collections.singletonMap("coaReceivablesData", receivablesData);
		
		Window coaReceivablesWin = (Window) Executions.createComponents("/coa/Coa_ReceivablesDialog.zul", null, arg);
		
		coaReceivablesWin.addEventListener(Events.ON_OK, new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// save
				getCoa_ReceivablesDao().save((Coa_Receivables) event.getData());
				
				// reload
				displayCoa_ReceivablesListInfo();
			}
			
		});
		
		coaReceivablesWin.doModal();
	}
	
	public List<Coa_Receivables> getCoaReceivablesList() {
		return coaReceivablesList;
	}

	public void setCoaReceivablesList(List<Coa_Receivables> coaReceivablesList) {
		this.coaReceivablesList = coaReceivablesList;
	}

	public Coa_ReceivablesDao getCoa_ReceivablesDao() {
		return coa_ReceivablesDao;
	}

	public void setCoa_ReceivablesDao(Coa_ReceivablesDao coa_ReceivablesDao) {
		this.coa_ReceivablesDao = coa_ReceivablesDao;
	}
}
