package com.pyramix.swi.webui.gl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.EndingBalance;
import com.pyramix.swi.persistence.gl.dao.EndingBalanceDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class EndingBalanceListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5365814031818274176L;

	private EndingBalanceDao endingBalanceDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox endingBalanceListbox;
	private Combobox masterCoaCombobox;

	private List<EndingBalance> endingBalanceList;
	
	public void onCreate$endingBalanceListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Ending Balance");
		
		// setup masterCoa (unique) from EndingBalance table
		setupMasterCoaList();
		
		// default to 'all'
		masterCoaCombobox.setSelectedIndex(0);
		
		// load all endingBalance
		loadAllEndingBalance();
		
		// list endingBalance
		listEndingBalanceListInfo();
	}

	private void setupMasterCoaList() throws Exception {
		Comboitem comboitem;
		
		// get the coaMaster list
		List<Coa_05_Master> coaMasterList = getEndingBalanceDao().findUniqueCoaMasterEndingBalance();
		
		// all
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setValue(null);
		comboitem.setParent(masterCoaCombobox);
		
		// other masterCoa
		for (Coa_05_Master coaMaster : coaMasterList) {
			comboitem = new Comboitem();
			comboitem.setLabel(coaMaster.getMasterCoaComp()+" - "+
					coaMaster.getMasterCoaName());
			comboitem.setValue(coaMaster);
			comboitem.setParent(masterCoaCombobox);			
		}
	}

	private void loadAllEndingBalance() throws Exception {
		setEndingBalanceList(
				getEndingBalanceDao().findAllEndingBalance_OrderBy_CoaMaster_EndingDate());
		
		// Total: 8 Ending Balance
		infoResultlabel.setValue("Total: "+getEndingBalanceList().size()+" Ending Balance.");
	}
	
	public void onSelect$masterCoaCombobox(Event event) throws Exception {
		Coa_05_Master selCoaMaster = masterCoaCombobox.getSelectedItem().getValue();
		
		if (selCoaMaster==null) {
			// display all
			setEndingBalanceList(
					getEndingBalanceDao().findAllEndingBalance_OrderBy_CoaMaster_EndingDate());			
		} else {
			// display selected
			setEndingBalanceList(
					getEndingBalanceDao().findAllEndingBalance_By_CoaMaster(selCoaMaster));			
		}
		
		// Total: 8 Ending Balance
		infoResultlabel.setValue("Total: "+getEndingBalanceList().size()+" Ending Balance.");

		listEndingBalanceListInfo();
	}
	
	private void listEndingBalanceListInfo() {
		endingBalanceListbox.setModel(
				new ListModelList<EndingBalance>(getEndingBalanceList()));
		endingBalanceListbox.setItemRenderer(getEndingBalanceListitemRenderer());
	}	
	
	private ListitemRenderer<EndingBalance> getEndingBalanceListitemRenderer() {
		
		return new ListitemRenderer<EndingBalance>() {
			
			@Override
			public void render(Listitem item, EndingBalance endBalance, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = new Listcell(endBalance.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(endBalance.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Tanggal
				lc = new Listcell(dateToStringDB(asLocalDate(endBalance.getEndingDate())));
				lc.setParent(item);
				
				// Nominal (Rp.)
				lc = new Listcell(toLocalFormat(endBalance.getEndingAmount()));
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), endBalance);
				lc.setParent(item);
			}

			private Listcell initEdit(Listcell listcell, EndingBalance endBalance) {
				Button button = new Button();
				button.setLabel("Edit");
				button.setClass("inventoryEditButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, EndingBalance> arg = 
								Collections.singletonMap("endingBalance", endBalance);
						
						Window editEndingBalanceWin = 
								(Window) Executions.createComponents(
										"/gl/EndingBalanceDialog.zul", null, arg);
						
						editEndingBalanceWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								EndingBalance endingBalance = (EndingBalance) event.getData();
								
								// update
								getEndingBalanceDao().update(endingBalance);
								
								// default to 'all'
								masterCoaCombobox.setSelectedIndex(0);
								
								// load all endingBalance
								loadAllEndingBalance();
								
								// list endingBalance
								listEndingBalanceListInfo();								
							}
						});

						editEndingBalanceWin.doModal();
					}
				});
				
				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		Map<String, EndingBalance> arg = 
				Collections.singletonMap("endingBalance", new EndingBalance());
		
		Window addEndingBalanceWin = (Window) Executions.createComponents(
				"/gl/EndingBalanceDialog.zul", null, arg);
		
		addEndingBalanceWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				EndingBalance endingBalance = (EndingBalance) event.getData();
				
				// save
				getEndingBalanceDao().save(endingBalance);
				
				// default to 'all'
				masterCoaCombobox.setSelectedIndex(0);
				
				// load all endingBalance
				loadAllEndingBalance();
				
				// list endingBalance
				listEndingBalanceListInfo();								
				
			}
		});
		
		addEndingBalanceWin.doModal();
	}
	
	public EndingBalanceDao getEndingBalanceDao() {
		return endingBalanceDao;
	}

	public void setEndingBalanceDao(EndingBalanceDao endingBalanceDao) {
		this.endingBalanceDao = endingBalanceDao;
	}

	public List<EndingBalance> getEndingBalanceList() {
		return endingBalanceList;
	}

	public void setEndingBalanceList(List<EndingBalance> endingBalanceList) {
		this.endingBalanceList = endingBalanceList;
	}
	
}
