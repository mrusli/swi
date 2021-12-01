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

import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_02_AccountGroupListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8517022778887234991L;

	private Coa_01_AccountTypeDao 	coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao 	coa_02_AccountGroupDao;
	
	private Label formTitleLabel;
	private Tabbox accountSelectionTabbox;
	private Listbox coa_02_AccountGroupListbox;
	
	private List<Coa_02_AccountGroup> accountGroupList;
	
	private final int TAB_INDEX_MASTER = 0;
	
	public void onCreate$coa_02_AccountGroupListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Chart of Account - Account-Group");
		
		// set default tab
		accountSelectionTabbox.setSelectedIndex(TAB_INDEX_MASTER);

		// load the COA
		loadCoa_02_AccountGroupListInfo(accountSelectionTabbox.getSelectedIndex()); 
	}
	
	public void onSelect$accountSelectionTabbox(Event event) throws Exception {
		// set the selected tab index
		accountSelectionTabbox.setSelectedIndex(
				accountSelectionTabbox.getSelectedIndex());
		// load the COA
		loadCoa_02_AccountGroupListInfo(
				accountSelectionTabbox.getSelectedIndex()); 		
	}
	
	private void loadCoa_02_AccountGroupListInfo(int selectedIndex) throws Exception {
		setAccountGroupList(
				getCoa_02_AccountGroupDao().findCoa_02_AccountGroup_By_AccountType(selectedIndex));
		coa_02_AccountGroupListbox.setModel(
				new ListModelList<Coa_02_AccountGroup>(getAccountGroupList()));
		coa_02_AccountGroupListbox.setItemRenderer(getCoa_02_AccountGroupListitemRenderer());
	}

	private ListitemRenderer<Coa_02_AccountGroup> getCoa_02_AccountGroupListitemRenderer() {
		
		return new ListitemRenderer<Coa_02_AccountGroup>() {
			
			@Override
			public void render(Listitem item, Coa_02_AccountGroup accountGroup, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = initCoa02_AccountGroupFormat(new Listcell(), accountGroup);
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(accountGroup.getAccountGroupName());
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), accountGroup);
				lc.setParent(item);
				
			}

			private Listcell initCoa02_AccountGroupFormat(Listcell listcell, Coa_02_AccountGroup accountGroup) {
				String accountGroupFormat = accountGroup.getTypeCoaNumber()+"."+
						accountGroup.getAccountGroupNumber();
				
				listcell.setLabel(accountGroupFormat);
				
				return listcell;
			}

			private Listcell initEditButton(Listcell listcell, Coa_02_AccountGroup accountGroup) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> dataList = new HashMap<String, Object>();
						dataList.put("selectedCoa_Account", Coa_AccountSelect.AccountGroup);
						dataList.put("accountGroup", accountGroup);
						
						Window coaDialogWin = 
								(Window) Executions.createComponents("/coa/Coa_05_MasterEditDialog.zul", null, dataList);
						
						coaDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

								// update
								getCoa_02_AccountGroupDao().update(coaSaveData.getAccGroupForUpdate());
								
								// re-load
								loadCoa_02_AccountGroupListInfo(accountSelectionTabbox.getSelectedIndex());								
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
		dataList.put("selectedCoa_Account", Coa_AccountSelect.AccountGroup);

		Window masterDialogWin = 
				(Window) execution.createComponents("/coa/Coa_05_MasterAddDialog.zul", null, dataList);
		
		masterDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

				// save
				getCoa_02_AccountGroupDao().save(coaSaveData.getAccGroupToSave());

				// re-load
				loadCoa_02_AccountGroupListInfo(accountSelectionTabbox.getSelectedIndex());				
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

	public List<Coa_02_AccountGroup> getAccountGroupList() {
		return accountGroupList;
	}

	public void setAccountGroupList(List<Coa_02_AccountGroup> accountGroupList) {
		this.accountGroupList = accountGroupList;
	}
}
