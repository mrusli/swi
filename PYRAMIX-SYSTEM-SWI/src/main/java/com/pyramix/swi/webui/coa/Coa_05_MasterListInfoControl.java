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

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.swi.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_05_MasterListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5523822500342057573L;

	private Coa_01_AccountTypeDao 	coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao 	coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao 	coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao 	coa_04_SubAccount02Dao;
	private Coa_05_MasterDao 		coa_05_MasterDao;
	
	private Label formTitleLabel;
	private Listbox coa_05_MasterListbox;
	private Tabbox accountSelectionTabbox;
	
	private List<Coa_05_Master> masterList;
	
	private final int TAB_INDEX_MASTER = 0;
	
	public void onCreate$coa_05_MasterListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Chart of Account");
				
		// set default tab
		accountSelectionTabbox.setSelectedIndex(TAB_INDEX_MASTER);
		
		// load the COA
		loadCoa_05_MasterListInfo(accountSelectionTabbox.getSelectedIndex());
	}

	public void onSelect$accountSelectionTabbox(Event event) throws Exception {
		// set the selected tab index
		accountSelectionTabbox.setSelectedIndex(
				accountSelectionTabbox.getSelectedIndex());
		// load the COA
		loadCoa_05_MasterListInfo(accountSelectionTabbox.getSelectedIndex());
	}
	
	private void loadCoa_05_MasterListInfo(int selectedTabIndex) throws Exception {
		setMasterList(
				// getCoa_05_MasterDao().findAllCoa_05_Master());
				getCoa_05_MasterDao().findCoa_05_Master_By_AccountType(selectedTabIndex));
		coa_05_MasterListbox.setModel(
				new ListModelList<Coa_05_Master>(getMasterList()));		
		coa_05_MasterListbox.setItemRenderer(getCoa_05_MasterListitemRenderer());		
	}

	private ListitemRenderer<Coa_05_Master> getCoa_05_MasterListitemRenderer() {

		return new ListitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Listitem item, Coa_05_Master master, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(master.getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(
						// master.getSubAccount02().getSubAccount01().getAccountGroup().getAccountType().getAccountTypeName()+" - "+
						// master.getSubAccount02().getSubAccount01().getAccountGroup().getAccountGroupName()+" - "+
						// master.getSubAccount02().getSubAccount01().getSubAccount01Name()+" - "+
						// master.getSubAccount02().getSubAccount02Name()+" - "+
						master.getMasterCoaName());
				lc.setParent(item);
												
				// Aktif
				lc = new Listcell(master.isActive() ? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), master);
				lc.setParent(item);					
			}

			private Listcell initEditButton(Listcell listcell, Coa_05_Master master) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> dataList = new HashMap<String, Object>();
						dataList.put("selectedCoa_Account", Coa_AccountSelect.AccountMaster);
						dataList.put("accountMaster", master);
						
						// Map<String, Coa_05_Master> arg = Collections.singletonMap("accountMaster", master);
						
						Window coaDialogWin = 
								(Window) Executions.createComponents("/coa/Coa_05_MasterEditDialog.zul", null, dataList);
						
						coaDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();
								
								// update
								getCoa_05_MasterDao().update(coaSaveData.getAccMasterForUpdate());
								
								// load
								loadCoa_05_MasterListInfo(accountSelectionTabbox.getSelectedIndex());
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
		dataList.put("selectedCoa_Account", Coa_AccountSelect.AccountMaster);
		
		Window masterDialogWin = 
				(Window) execution.createComponents("/coa/Coa_05_MasterAddDialog.zul", null, dataList);
		
		masterDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();
				
				switch (coaSaveData.getDialogAccountSelect()) {
				case AccountGroup:
					// getCoa_05_MasterDao().save(coaSaveData.getAccMasterToSave());
					getCoa_02_AccountGroupDao().save(coaSaveData.getAccGroupToSave());
					break;
				case SubAccount01:
					// getCoa_05_MasterDao().save(coaSaveData.getAccMasterToSave());
					getCoa_03_SubAccount01Dao().save(coaSaveData.getSubAcc01ToSave());
					break;
				case SubAccount02:
					// getCoa_05_MasterDao().save(coaSaveData.getAccMasterToSave());
					getCoa_04_SubAccount02Dao().save(coaSaveData.getSubAcc02ToSave());
					break;
				case AccountMaster:
					getCoa_05_MasterDao().save(coaSaveData.getAccMasterToSave());
					break;
				default:
					break;
				}
				
			}
		});
		
		masterDialogWin.doModal();
	}
	
	public List<Coa_05_Master> getMasterList() {
		return masterList;
	}

	public void setMasterList(List<Coa_05_Master> masterList) {
		this.masterList = masterList;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
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
