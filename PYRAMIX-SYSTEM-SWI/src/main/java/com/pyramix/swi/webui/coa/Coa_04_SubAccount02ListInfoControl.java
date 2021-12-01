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

import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.swi.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_04_SubAccount02ListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6858374457744606536L;

	private Coa_01_AccountTypeDao 	coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao 	coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao 	coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao 	coa_04_SubAccount02Dao;	
	
	private Label formTitleLabel;
	private Tabbox accountSelectionTabbox;
	private Listbox coa_04_SubAccount02Listbox;
	
	private List<Coa_04_SubAccount02> subAccount02List;
	
	private final int TAB_INDEX_MASTER = 0;
	
	public void onCreate$coa_04_SubAccount02ListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Chart of Account - SubAccount-02");
		
		// set default tab
		accountSelectionTabbox.setSelectedIndex(TAB_INDEX_MASTER);

		// load the COA
		loadCoa_04_SubAccount02ListInfo(accountSelectionTabbox.getSelectedIndex());
	}

	public void onSelect$accountSelectionTabbox(Event event) throws Exception {
		// set the selected tab index
		accountSelectionTabbox.setSelectedIndex(
				accountSelectionTabbox.getSelectedIndex());
		// load the COA
		loadCoa_04_SubAccount02ListInfo(accountSelectionTabbox.getSelectedIndex());
	}
	
	private void loadCoa_04_SubAccount02ListInfo(int selectedIndex) throws Exception {
		setSubAccount02List(
				getCoa_04_SubAccount02Dao().findCoa_04_SubAccount02_By_AccountType(selectedIndex));		
		coa_04_SubAccount02Listbox.setModel(
				new ListModelList<Coa_04_SubAccount02>(getSubAccount02List()));
		coa_04_SubAccount02Listbox.setItemRenderer(
				getCoa_04_SubAccount02ListItemRenderer());
	}

	private ListitemRenderer<Coa_04_SubAccount02> getCoa_04_SubAccount02ListItemRenderer() {
		
		return new ListitemRenderer<Coa_04_SubAccount02>() {
			
			@Override
			public void render(Listitem item, Coa_04_SubAccount02 subAccount02, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = initCoa_04_SubAccount02Format(new Listcell(), subAccount02);
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(subAccount02.getSubAccount02Name());
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), subAccount02);
				lc.setParent(item);
			}

			private Listcell initCoa_04_SubAccount02Format(Listcell listcell, Coa_04_SubAccount02 subAccount02) {

				String subAccount02Format = subAccount02.getTypeCoaNumber()+"."+
						subAccount02.getGroupCoaNumber()+
						subAccount02.getSubaccount01CoaNumber()+
						subAccount02.getSubAccount02Number();
				listcell.setLabel(subAccount02Format);
				
				return listcell;
			}

			private Listcell initEditButton(Listcell listcell, Coa_04_SubAccount02 subAccount02) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> dataList = new HashMap<String, Object>();
						dataList.put("selectedCoa_Account", Coa_AccountSelect.SubAccount02);
						dataList.put("accountSubAccount02", subAccount02);
						
						Window coaDialogWin = 
								(Window) Executions.createComponents("/coa/Coa_05_MasterEditDialog.zul", null, dataList);
						
						coaDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

								// update
								getCoa_04_SubAccount02Dao().update(coaSaveData.getSubAcc02ForUpdate());
								
								// re-load
								loadCoa_04_SubAccount02ListInfo(accountSelectionTabbox.getSelectedIndex());								
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
		dataList.put("selectedCoa_Account", Coa_AccountSelect.SubAccount02);

		Window masterDialogWin = 
				(Window) execution.createComponents("/coa/Coa_05_MasterAddDialog.zul", null, dataList);
		
		masterDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

				// save
				getCoa_04_SubAccount02Dao().save(coaSaveData.getSubAcc02ToSave());

				// re-load
				loadCoa_04_SubAccount02ListInfo(accountSelectionTabbox.getSelectedIndex());				
			}

		});
		
		masterDialogWin.doModal();
	}
	
	public List<Coa_04_SubAccount02> getSubAccount02List() {
		return subAccount02List;
	}

	public void setSubAccount02List(List<Coa_04_SubAccount02> subAccount02List) {
		this.subAccount02List = subAccount02List;
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

	public Coa_04_SubAccount02Dao getCoa_04_SubAccount02Dao() {
		return coa_04_SubAccount02Dao;
	}

	public void setCoa_04_SubAccount02Dao(Coa_04_SubAccount02Dao coa_04_SubAccount02Dao) {
		this.coa_04_SubAccount02Dao = coa_04_SubAccount02Dao;
	}
}
