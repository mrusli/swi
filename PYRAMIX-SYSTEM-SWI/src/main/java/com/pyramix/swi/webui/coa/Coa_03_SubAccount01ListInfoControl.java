package com.pyramix.swi.webui.coa;

import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_03_SubAccount01ListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8887913015699054090L;

	private Coa_01_AccountTypeDao 	coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao 	coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao 	coa_03_SubAccount01Dao;
	
	private Label formTitleLabel;
	private Tabbox accountSelectionTabbox;
	private Listbox coa_03_SubAccount01Listbox;
	
	private List<Coa_03_SubAccount01> subAccount01List;
	
	private final int TAB_INDEX_MASTER = 0;
	
	public void onCreate$coa_03_SubAccount01ListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Chart of Account - SubAccount-01");
		
		// set default tab
		accountSelectionTabbox.setSelectedIndex(TAB_INDEX_MASTER);

		// load the COA
		loadCoa_03_SubAccount01ListInfo(accountSelectionTabbox.getSelectedIndex()); 
	}
	
	public void onSelect$accountSelectionTabbox(Event event) throws Exception {
		// set the selected tab index
		accountSelectionTabbox.setSelectedIndex(
				accountSelectionTabbox.getSelectedIndex());
		// load the COA
		loadCoa_03_SubAccount01ListInfo(
				accountSelectionTabbox.getSelectedIndex());
	}
	
	private void loadCoa_03_SubAccount01ListInfo(int selectedIndex) throws Exception {
		setSubAccount01List(
				getCoa_03_SubAccount01Dao().findCoa_03_SubAccount01_By_AccountType(selectedIndex));
		coa_03_SubAccount01Listbox.setModel(
				new ListModelList<Coa_03_SubAccount01>(getSubAccount01List()));
		coa_03_SubAccount01Listbox.setItemRenderer(
				getCoa_03_SubAccount01ListitemRenderer());
	}

	private ListitemRenderer<Coa_03_SubAccount01> getCoa_03_SubAccount01ListitemRenderer() {
		
		return new ListitemRenderer<Coa_03_SubAccount01>() {
			
			@Override
			public void render(Listitem item, Coa_03_SubAccount01 subAccount01, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = initCoa03_SubAccount01Format(new Listcell(), subAccount01);
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(subAccount01.getSubAccount01Name());
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), subAccount01);
				lc.setParent(item);
			}

			private Listcell initCoa03_SubAccount01Format(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				String subAccount01Format = subAccount01.getTypeCoaNumber()+"."+
						subAccount01.getGroupCoaNumber()+
						subAccount01.getGroupCoaNumber();
				
				listcell.setLabel(subAccount01Format);
				
				return listcell;
			}

			private Listcell initEditButton(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> dataList = new HashMap<String, Object>();
						dataList.put("selectedCoa_Account", Coa_AccountSelect.SubAccount01);
						dataList.put("accountSubAcoount01", subAccount01);
						
						Window coaDialogWin = 
								(Window) Executions.createComponents("/coa/Coa_05_MasterEditDialog.zul", null, dataList);
						
						coaDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

								// update
								getCoa_03_SubAccount01Dao().update(coaSaveData.getSubAcc01ForUpdate());
								
								// re-load
								loadCoa_03_SubAccount01ListInfo(accountSelectionTabbox.getSelectedIndex());								
							}
							
						});
						
						coaDialogWin.doModal();
					}

				});
				editButton.setParent(listcell);

				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		HashMap<String, Object> dataList = new HashMap<String, Object>();
		dataList.put("selectedTab", accountSelectionTabbox.getSelectedIndex());
		dataList.put("selectedCoa_Account", Coa_AccountSelect.SubAccount01);

		Window masterDialogWin = 
				(Window) execution.createComponents("/coa/Coa_05_MasterAddDialog.zul", null, dataList);
		
		masterDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

				// save
				getCoa_03_SubAccount01Dao().save(coaSaveData.getSubAcc01ToSave());

				// re-load
				loadCoa_03_SubAccount01ListInfo(accountSelectionTabbox.getSelectedIndex());				
			}

		});
		
		masterDialogWin.doModal();		
	}
	
	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}
	
	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}
	
	public Coa_02_AccountGroupDao getCoa_02_AccountGroupDao() {
		return coa_02_AccountGroupDao;
	}
	
	public void setCoa_02_AccountGroupDao(Coa_02_AccountGroupDao coa_02_AccountGroupDao) {
		this.coa_02_AccountGroupDao = coa_02_AccountGroupDao;
	}
	
	public Coa_03_SubAccount01Dao getCoa_03_SubAccount01Dao() {
		return coa_03_SubAccount01Dao;
	}
	
	public void setCoa_03_SubAccount01Dao(Coa_03_SubAccount01Dao coa_03_SubAccount01Dao) {
		this.coa_03_SubAccount01Dao = coa_03_SubAccount01Dao;
	}

	public List<Coa_03_SubAccount01> getSubAccount01List() {
		return subAccount01List;
	}

	public void setSubAccount01List(List<Coa_03_SubAccount01> subAccount01List) {
		this.subAccount01List = subAccount01List;
	}

	
}
