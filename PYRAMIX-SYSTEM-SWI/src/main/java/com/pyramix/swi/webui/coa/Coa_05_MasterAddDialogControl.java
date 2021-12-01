package com.pyramix.swi.webui.coa;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.swi.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_05_MasterAddDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7798643663401996573L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	private Coa_05_MasterDao coa_05_MasterDao;
	
	private Window coa_05_MasterAddDialogWin;
	private Grid editGrid, editCOAGrid;
	private Combobox accTypeCombobox, accGroupCombobox, subAcc01Combobox, 
		subAcc02Combobox, masterCombobox;
	private Button accGroupButton, subAcc01Button, subAcc02Button, masterButton,
		generateCoaButton, cancelAddButton, saveButton;
	private Textbox accTypeNameTextbox, accGroupNameTextbox, subAccount01NameTextbox,
		subAccount02NameTextbox, accMasterNameTextbox, resultingCoaTextbox;
	private Intbox accTypeNoIntbox, accGroupNoIntbox, subAccount01NoIntbox, 
		subAccount02NoIntbox, accMasterNoIntbox;
	private Checkbox creditAccCheckbox, accActiveCheckbox;
	
	private List<Coa_01_AccountType> accountTypeList;
	private Coa_AccountSelect dialogAccountSelect;
	private int selectedTab;
	private Coa_AccountSelect selectedCoa_Account;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSelectedTab(
				(Integer)Executions.getCurrent().getArg().get("selectedTab"));
		setSelectedCoa_Account(
				(Coa_AccountSelect) Executions.getCurrent().getArg().get("selectedCoa_Account"));
	}

	public void onCreate$coa_05_MasterAddDialogWin(Event event) throws Exception {
		coa_05_MasterAddDialogWin.setTitle("Membuat (Tambah) COA");
		
		setEnableAddForSelectedCoa_Account();
		
		setAccountTypeList(
				getCoa_01_AccountTypeDao().findAllCoa_01_AccountType());

		// select ALL Coa_01_AccountType
		listCoa_01_AccountType();
		// pick the 1st item to display
		Coa_01_AccountType selAccountType = getSelectedAccountType(getSelectedTab()); 
		//		(Coa_01_AccountType) accTypeCombobox.getModel().getElementAt(0);
		accTypeCombobox.setValue(
				selAccountType.getAccountTypeNumber() + "-" + selAccountType.getAccountTypeName());
		
		// select Coa_02_AccountGroup from the selAccountType
		listCoa_02_AccountGroupByAccountType(selAccountType);
		// pick the 1st item to display
		Coa_02_AccountGroup selAccountGroup = getSelectedAccountGroup();
		//		(Coa_02_AccountGroup) accGroupCombobox.getModel().getElementAt(0);
		accGroupCombobox.setValue(
				selAccountGroup.getAccountGroupNumber() + "-" + selAccountGroup.getAccountGroupName());
		
		// select Coa_03_SubAccount01
		listCoa_03_SubAccount01ByAccountGroup(selAccountGroup);
		// pick the 1st item to display
		Coa_03_SubAccount01 selSubAccount01 = getSelectedSubAccount01();
		//		(Coa_03_SubAccount01) subAcc01Combobox.getModel().getElementAt(0);
		subAcc01Combobox.setValue(
				selSubAccount01.getSubAccount01Number() + "-" + selSubAccount01.getSubAccount01Name());
		
		// select Coa_04_SubAccount02
		listCoa_04_SubAccount02BySubAccount01(selSubAccount01);
		Coa_04_SubAccount02 selSubAccount02 = getSelectedSubAccount02();
		//		(Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(0);
		subAcc02Combobox.setValue(
				selSubAccount02.getSubAccount02Number() + "-" + selSubAccount02.getSubAccount02Name());
		
		// select Coa_05_Master
		listCoa_05_MasterBySubAccount02(selSubAccount02);
		Coa_05_Master selMaster = getSelectedAccountMaster();
		//		(Coa_05_Master) masterCombobox.getModel().getElementAt(0);
		masterCombobox.setValue(
				selMaster.getMasterCoaNumber() + "-" + selMaster.getMasterCoaName());
	}

	private void setEnableAddForSelectedCoa_Account() {
		accGroupButton.setDisabled(!(getSelectedCoa_Account().compareTo(Coa_AccountSelect.AccountGroup)==0));
		subAcc01Button.setDisabled(!(getSelectedCoa_Account().compareTo(Coa_AccountSelect.SubAccount01)==0)); 
		subAcc02Button.setDisabled(!(getSelectedCoa_Account().compareTo(Coa_AccountSelect.SubAccount02)==0));
		masterButton.setDisabled(!(getSelectedCoa_Account().compareTo(Coa_AccountSelect.AccountMaster)==0));
	}

	private void listCoa_01_AccountType() {
		accTypeCombobox.setModel(
				new ListModelList<Coa_01_AccountType>(getAccountTypeList()));
		accTypeCombobox.setItemRenderer(
				getAccTypeComboitemRenderer());
		
	}
	
	private ComboitemRenderer<Coa_01_AccountType> getAccTypeComboitemRenderer() {

		return new ComboitemRenderer<Coa_01_AccountType>() {
			
			@Override
			public void render(Comboitem item, Coa_01_AccountType accountType, int index) throws Exception {
				item.setLabel(accountType.getAccountTypeNumber() + "-" + accountType.getAccountTypeName());
				item.setValue(accountType);
			}
		};
	}

	private void listCoa_02_AccountGroupByAccountType(Coa_01_AccountType accountType) throws Exception {
		// select Coa_02_AccountGroup from the selAccountType
		accGroupCombobox.setModel(new ListModelList<Coa_02_AccountGroup>(
				getCoa_02_AccountGroupDao().findCoa_02_AccountGroupByAccountType(accountType)));
		accGroupCombobox.setItemRenderer(
				getAccGroupComboitemRenderer());
	}

	
	private ComboitemRenderer<Coa_02_AccountGroup> getAccGroupComboitemRenderer() {
		
		return new ComboitemRenderer<Coa_02_AccountGroup>() {
			
			@Override
			public void render(Comboitem item, Coa_02_AccountGroup accountGroup, int index) throws Exception {
				item.setLabel(accountGroup.getAccountGroupNumber() + "-" + accountGroup.getAccountGroupName());
				item.setValue(accountGroup);
			}
		};
	}
	
	private void listCoa_03_SubAccount01ByAccountGroup(Coa_02_AccountGroup accountGroup) throws Exception {
		subAcc01Combobox.setModel(new ListModelList<Coa_03_SubAccount01>(
				getCoa_03_SubAccount01Dao().findCoa_03_SubAccount01ByAccountGroup(accountGroup)));
		subAcc01Combobox.setItemRenderer(
				getSubAccount01ComboitemRenderer());
	}
	
	private ComboitemRenderer<Coa_03_SubAccount01> getSubAccount01ComboitemRenderer() {
		
		return new ComboitemRenderer<Coa_03_SubAccount01>() {
			
			@Override
			public void render(Comboitem item, Coa_03_SubAccount01 subAccount01, int arg2) throws Exception {
				item.setLabel(subAccount01.getSubAccount01Number() + "-" + subAccount01.getSubAccount01Name());
				item.setValue(subAccount01);
			}
		};
	}

	private void listCoa_04_SubAccount02BySubAccount01(Coa_03_SubAccount01 subAccount01) throws Exception {
		subAcc02Combobox.setModel(new ListModelList<Coa_04_SubAccount02>(
				getCoa_04_SubAccount02Dao().findCoa_04_SubAccount02BySubAccount01(subAccount01)));
		subAcc02Combobox.setItemRenderer(
				getSubAccount02ComboitemRenderer());
	}	
	
	private ComboitemRenderer<Coa_04_SubAccount02> getSubAccount02ComboitemRenderer() {

		return new ComboitemRenderer<Coa_04_SubAccount02>() {
			
			@Override
			public void render(Comboitem item, Coa_04_SubAccount02 subAccount02, int index) throws Exception {
				item.setLabel(subAccount02.getSubAccount02Number() + "-" + subAccount02.getSubAccount02Name());
				item.setValue(subAccount02);
			}
		};
	}

	private void listCoa_05_MasterBySubAccount02(Coa_04_SubAccount02 subAccount02) throws Exception {
		masterCombobox.setModel(new ListModelList<Coa_05_Master>(
				getCoa_05_MasterDao().findCoa_05_MasterBySubAccount02(subAccount02)));
		masterCombobox.setItemRenderer(
				getMasterComboitemRenderer());
	}	
	
	private ComboitemRenderer<Coa_05_Master> getMasterComboitemRenderer() {
		
		return new ComboitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Comboitem item, Coa_05_Master master, int index) throws Exception {
				item.setLabel(master.getMasterCoaNumber() + "-" + master.getMasterCoaName());
				item.setValue(master);
			}
		};
	}

	public void onSelect$accTypeCombobox(Event event) throws Exception {
		Coa_01_AccountType accType = 
				accTypeCombobox.getSelectedItem().getValue();
		
		// select Coa_02_AccountGroup
		listCoa_02_AccountGroupByAccountType(accType);
		// pick the 1st item to display
		Coa_02_AccountGroup selAccountGroup = getSelectedAccountGroup();
		//		(Coa_02_AccountGroup) accGroupCombobox.getModel().getElementAt(0);
		accGroupCombobox.setValue(selAccountGroup == null ? " " :
				selAccountGroup.getAccountGroupNumber() + "-" + selAccountGroup.getAccountGroupName());
		
		// select Coa_03_SubAccount01
		listCoa_03_SubAccount01ByAccountGroup(selAccountGroup);
		// pick the 1st item to display
		Coa_03_SubAccount01 selSubAccount01 = getSelectedSubAccount01(); 
		//		(Coa_03_SubAccount01) subAcc01Combobox.getModel().getElementAt(0);
		subAcc01Combobox.setValue(selSubAccount01 == null ? " " :
				selSubAccount01.getSubAccount01Number() + "-" + selSubAccount01.getSubAccount01Name());			

		// select Coa_04_SubAccount02
		listCoa_04_SubAccount02BySubAccount01(selSubAccount01);
		// pick the 1st item to display
		Coa_04_SubAccount02 selSubAccount02 = getSelectedSubAccount02();
		//		(Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(0);
		subAcc02Combobox.setValue(selSubAccount02 == null ? " " :
				selSubAccount02.getSubAccount02Number() + "-" + selSubAccount02.getSubAccount02Name());
		
		// select Coa_05_Master
		listCoa_05_MasterBySubAccount02(selSubAccount02);
		// pick the 1st item to display
		Coa_05_Master selMaster = getSelectedAccountMaster();
		//		(Coa_05_Master) masterCombobox.getModel().getElementAt(0);
		masterCombobox.setValue(selMaster == null ?	" " : 
				selMaster.getMasterCoaNumber()+ "-" + selMaster.getMasterCoaName());
	}
	
	public void onSelect$accGroupCombobox(Event event) throws Exception {
		Coa_02_AccountGroup selAccountGroup =
				accGroupCombobox.getSelectedItem().getValue();
		
		// select Coa_03_SubAccount01
		listCoa_03_SubAccount01ByAccountGroup(selAccountGroup);
		// pick the 1st item to display
		Coa_03_SubAccount01 selSubAccount01 = getSelectedSubAccount01();
		//		(Coa_03_SubAccount01) subAcc01Combobox.getModel().getElementAt(0);
		subAcc01Combobox.setValue(selSubAccount01 == null ? " " :
				selSubAccount01.getSubAccount01Number() + "-" + selSubAccount01.getSubAccount01Name());

		// select Coa_04_SubAccount02
		listCoa_04_SubAccount02BySubAccount01(selSubAccount01);
		// pick the 1st item to display
		Coa_04_SubAccount02 selSubAccount02 = getSelectedSubAccount02();
		//		(Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(0);
		subAcc02Combobox.setValue(selSubAccount02 == null ? " " :
				selSubAccount02.getSubAccount02Number() + "-" + selSubAccount02.getSubAccount02Name());
		
		// select Coa_05_Master
		listCoa_05_MasterBySubAccount02(selSubAccount02);
		// pick the 1st item to display
		Coa_05_Master selMaster = getSelectedAccountMaster();
		//		(Coa_05_Master) masterCombobox.getModel().getElementAt(0);
		masterCombobox.setValue(selMaster == null ? " " :
				selMaster.getMasterCoaNumber() + "-" + selMaster.getMasterCoaName());
		
	}
	
	public void onSelect$subAcc01Combobox(Event event) throws Exception {
		Coa_03_SubAccount01 selSubAccount01 =
				subAcc01Combobox.getSelectedItem().getValue();
		
		// select Coa_04_SubAccount02
		listCoa_04_SubAccount02BySubAccount01(selSubAccount01);
		// pick the 1st item to display
		Coa_04_SubAccount02 selSubAccount02 = getSelectedSubAccount02();
		//		(Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(0);
		subAcc02Combobox.setValue(selSubAccount02 == null ? " " :
				selSubAccount02.getSubAccount02Number() + "-" + selSubAccount02.getSubAccount02Name());
		
		// select Coa_05_Master
		listCoa_05_MasterBySubAccount02(selSubAccount02);
		// pick the 1st item to display
		Coa_05_Master selMaster = getSelectedAccountMaster();
		//		(Coa_05_Master) masterCombobox.getModel().getElementAt(0);
		masterCombobox.setValue(selMaster == null ? " " :
				selMaster.getMasterCoaNumber() + "-" + selMaster.getMasterCoaName());		
	}
	
	public void onSelect$subAcc02Combobox(Event event) throws Exception {
		Coa_04_SubAccount02 selSubAccount02 =
				subAcc02Combobox.getSelectedItem().getValue();

		// select Coa_05_Master
		listCoa_05_MasterBySubAccount02(selSubAccount02);
		// pick the 1st item to display
		Coa_05_Master selMaster = getSelectedAccountMaster();
		//		(Coa_05_Master) masterCombobox.getModel().getElementAt(0);
		masterCombobox.setValue(selMaster == null ? " " :
				selMaster.getMasterCoaNumber() + "-" + selMaster.getMasterCoaName());
	}
	
	/**
	 * Click to add a new account group
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$accGroupButton(Event event) throws Exception {
		// check if account type not selected or blank
		if ((accTypeCombobox.getSelectedItem().getValue()==null) ||
				(accTypeCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Type");
		}
		
		// disable all the accounts selections
		disableAccountSelectionsCombobox(true);

		editGrid.setVisible(true);
		// user ONLY adds account group
		// -- no need to generate COA
		generateCoaButton.setVisible(false);
		// but user can save the account group
		// -- without the rest of the accounts
		saveButton.setVisible(true);
		
		Coa_01_AccountType selAccType = 
				accTypeCombobox.getSelectedItem().getValue();
		
		accTypeNameTextbox.setValue(selAccType.getAccountTypeName());
		accTypeNoIntbox.setValue(selAccType.getAccountTypeNumber());
		
		accGroupNameTextbox.setValue("");
		accGroupNameTextbox.setDisabled(false);
		// get a new AccountGroup number
		accGroupNoIntbox.setValue(getNewIncrementAccGroup());
		accGroupNoIntbox.setDisabled(false);
		
		// reset all other account names and disable them
		subAccount01NameTextbox.setValue("");
		subAccount01NameTextbox.setDisabled(true);
		subAccount02NameTextbox.setValue("");
		subAccount02NameTextbox.setDisabled(true);
		accMasterNameTextbox.setValue("");
		accMasterNameTextbox.setDisabled(true);
	
		// set to 0 (zero) and disable the account numbers
		subAccount01NoIntbox.setValue(0);
		subAccount01NoIntbox.setDisabled(true);
		subAccount02NoIntbox.setValue(0);
		subAccount02NoIntbox.setDisabled(true);
		accMasterNoIntbox.setValue(0);
		accMasterNoIntbox.setDisabled(true);
		
		// allow user to cancel this add
		cancelAddButton.setVisible(true);
		
		disableAllAddButton(true);
		setDialogAccountSelect(Coa_AccountSelect.AccountGroup);
	}
	
	/**
	 * Click to add a new subAccount01
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$subAcc01Button(Event event) throws Exception {
		// check if account type not selected or blank
		if ((accTypeCombobox.getSelectedItem().getValue()==null) ||
				(accTypeCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Type");
		}

		// check if account group not selected or blank
		if ((accGroupCombobox.getSelectedItem().getValue()==null) ||
				(accGroupCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Group");
		}
		
		// disable all the accounts selections
		disableAccountSelectionsCombobox(true);

		editGrid.setVisible(true);
		// user ONLY adds subAccount01
		// -- no need to generate COA
		generateCoaButton.setVisible(false);
		// but user can save the subAccount01
		// -- without the rest of the accounts
		saveButton.setVisible(true);
		
		Coa_01_AccountType selAccType =  
				accTypeCombobox.getSelectedItem().getValue();
		Coa_02_AccountGroup selAccGroup = 
				accGroupCombobox.getSelectedItem().getValue();
		
		// account Type
		accTypeNameTextbox.setValue(selAccType.getAccountTypeName());
		accTypeNoIntbox.setValue(selAccType.getAccountTypeNumber());
		
		// display and disable the account Group name and number
		accGroupNameTextbox.setValue(selAccGroup.getAccountGroupName());
		accGroupNameTextbox.setDisabled(true);
		accGroupNoIntbox.setValue(selAccGroup.getAccountGroupNumber());
		accGroupNoIntbox.setDisabled(true);
		
		subAccount01NameTextbox.setValue("");
		subAccount01NameTextbox.setDisabled(false);
		// get a new subAccount01 number
		subAccount01NoIntbox.setValue(getNewIncrementSubAccount01());
		subAccount01NoIntbox.setDisabled(false);
		
		// reset all other account names and disable them
		subAccount02NameTextbox.setValue("");
		subAccount02NameTextbox.setDisabled(true);
		accMasterNameTextbox.setValue("");
		accMasterNameTextbox.setDisabled(true);		
		
		// set to 0 (zero) and disable the account numbers
		subAccount02NoIntbox.setValue(0);
		subAccount02NoIntbox.setDisabled(true);
		accMasterNoIntbox.setValue(0);
		accMasterNoIntbox.setDisabled(true);
		
		// allow user to cancel this add
		cancelAddButton.setVisible(true);
		
		disableAllAddButton(true);
		setDialogAccountSelect(Coa_AccountSelect.SubAccount01);
	}
	
	/**
	 * Click to add a new subAccount02
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$subAcc02Button(Event event) throws Exception {
		// check if account type not selected or blank
		if ((accTypeCombobox.getSelectedItem().getValue()==null) ||
				(accTypeCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Type");
		}

		// check if account group not selected or blank
		if ((accGroupCombobox.getSelectedItem().getValue()==null) ||
				(accGroupCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Group");
		}

		// check if subAccount01 not selected or blank
		if ((subAcc01Combobox.getSelectedItem().getValue()==null) ||
				(subAcc01Combobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun SubAccount-01");
		}
		
		// disable all the accounts selections
		disableAccountSelectionsCombobox(true);

		editGrid.setVisible(true);
		// user ONLY adds subAccount02
		// -- no need to generate COA
		generateCoaButton.setVisible(false);
		// but user can save the subAccount01
		// -- without the rest of the accounts
		saveButton.setVisible(true);
		
		Coa_01_AccountType selAccType = accTypeCombobox.getSelectedItem().getValue();
		Coa_02_AccountGroup selAccGroup = accGroupCombobox.getSelectedItem().getValue();
		Coa_03_SubAccount01 selSubAcc01 = subAcc01Combobox.getSelectedItem().getValue();
		
		// account Type
		accTypeNameTextbox.setValue(selAccType.getAccountTypeName());
		accTypeNoIntbox.setValue(selAccType.getAccountTypeNumber());
		
		// display and disable the account Group name and number
		accGroupNameTextbox.setValue(selAccGroup.getAccountGroupName());
		accGroupNameTextbox.setDisabled(true);
		accGroupNoIntbox.setValue(selAccGroup.getAccountGroupNumber());
		accGroupNoIntbox.setDisabled(true);
		
		// display and disable the subAccount01 name and number
		subAccount01NameTextbox.setValue(selSubAcc01.getSubAccount01Name());
		subAccount01NameTextbox.setDisabled(true);
		subAccount01NoIntbox.setValue(selSubAcc01.getSubAccount01Number());
		subAccount01NoIntbox.setDisabled(true);
		
		subAccount02NameTextbox.setValue("");
		subAccount02NameTextbox.setDisabled(false);
		// get a new subAccount02 number
		subAccount02NoIntbox.setValue(getNewIncrementSubAccount02());
		subAccount02NoIntbox.setDisabled(false);
		
		// reset all other account names and disable them
		accMasterNameTextbox.setValue("");
		accMasterNameTextbox.setDisabled(true);

		// set to 0 (zero) and disable the account number
		accMasterNoIntbox.setValue(0);
		accMasterNoIntbox.setDisabled(true);
		
		// allow user to cancel this add
		cancelAddButton.setVisible(true);
		
		disableAllAddButton(true);
		setDialogAccountSelect(Coa_AccountSelect.SubAccount02);
	}
	
	/**
	 * Click to add a new account master
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$masterButton(Event event) throws Exception {
		// check if account type not selected or blank
		if ((accTypeCombobox.getSelectedItem().getValue()==null) ||
				(accTypeCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Type");
		}

		// check if account group not selected or blank
		if ((accGroupCombobox.getSelectedItem().getValue()==null) ||
				(accGroupCombobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun Group");
		}

		// check if subAccount01 not selected or blank
		if ((subAcc01Combobox.getSelectedItem().getValue()==null) ||
				(subAcc01Combobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun SubAccount-01");
		}

		// check if subAccount02 not selected or blank
		if ((subAcc02Combobox.getSelectedItem().getValue()==null) ||
				(subAcc02Combobox.getModel().getSize()==0)) {
			throw new Exception("Belum memilih Akun SubAccount-02");
		}
		
		// disable all the accounts selections
		disableAccountSelectionsCombobox(true);
		
		editGrid.setVisible(true);
		// user MUST COMPLETE COA
		// generateCoaButton.setVisible(true);
		editCOAGrid.setVisible(true);
		// save the account master
		saveButton.setVisible(true);

		Coa_01_AccountType selAccType	= accTypeCombobox.getSelectedItem().getValue();
		Coa_02_AccountGroup selAccGroup = accGroupCombobox.getSelectedItem().getValue();
		Coa_03_SubAccount01 selSubAcc01 = subAcc01Combobox.getSelectedItem().getValue();
		Coa_04_SubAccount02 selSubAcc02 = subAcc02Combobox.getSelectedItem().getValue();
		
		// account Type
		accTypeNameTextbox.setValue(selAccType.getAccountTypeName());
		accTypeNoIntbox.setValue(selAccType.getAccountTypeNumber());

		// display and disable the account Group name and number
		accGroupNameTextbox.setValue(selAccGroup.getAccountGroupName());
		accGroupNameTextbox.setDisabled(true);
		accGroupNoIntbox.setValue(selAccGroup.getAccountGroupNumber());
		accGroupNoIntbox.setDisabled(true);
		
		// display and disable the subAccount01 name and number
		subAccount01NameTextbox.setValue(selSubAcc01.getSubAccount01Name());
		subAccount01NameTextbox.setDisabled(true);
		subAccount01NoIntbox.setValue(selSubAcc01.getSubAccount01Number());
		subAccount01NoIntbox.setDisabled(true);

		// display and disable the subAccount01 name and number
		subAccount02NameTextbox.setValue(selSubAcc02.getSubAccount02Name());
		subAccount02NameTextbox.setDisabled(true);
		subAccount02NoIntbox.setValue(selSubAcc02.getSubAccount02Number());
		subAccount02NoIntbox.setDisabled(true);
		
		accMasterNameTextbox.setValue("");
		accMasterNameTextbox.setDisabled(false);
		// get a new master account number
		accMasterNoIntbox.setValue(getNewIncrementMasterAccount());
		accMasterNoIntbox.setDisabled(false);
		// generate the COA number
		generateCOA();
		// allow user to cancel this add
		cancelAddButton.setVisible(true);
		
		disableAllAddButton(true);
		setDialogAccountSelect(Coa_AccountSelect.AccountMaster);
	}
	
	private void disableAccountSelectionsCombobox(boolean disabled) {
		accTypeCombobox.setDisabled(disabled);
		accGroupCombobox.setDisabled(disabled);
		subAcc01Combobox.setDisabled(disabled);
		subAcc02Combobox.setDisabled(disabled);
		masterCombobox.setDisabled(disabled);
	}

	public void onClick$cancelAddButton(Event event) throws Exception {
		// hide the edit grid
		editGrid.setVisible(false);
		// -- when user clicks on the master account Add button
		editCOAGrid.setVisible(false);
		
		// enable the account selection comboboxes
		disableAccountSelectionsCombobox(false);
		
		// hide the Cancel Add button
		cancelAddButton.setVisible(false);
		
		// enable all the account Add button
		disableAllAddButton(false);
	}
	
	public void generateCOA() {
		resultingCoaTextbox.setValue(
				formatCoaNumber(accTypeNoIntbox.getValue(), 
						accGroupNoIntbox.getValue(), 
						subAccount01NoIntbox.getValue(), 
						subAccount02NoIntbox.getValue(), 
						accMasterNoIntbox.getValue()));		
	}
		
	public void onClick$saveButton(Event event) throws Exception {
		Coa_SaveData coaSaveData;
		int typecoa_no, groupcoa_no, subaccount01coa_no, subaccount02coa_no;
		
		switch (getDialogAccountSelect()) {
		case AccountGroup:
			// is the account group selected?
			if (accGroupNameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun group belum diisi.");
			}					
			// get the typecoa_no
			typecoa_no = accTypeNoIntbox.getValue();
			
			coaSaveData = new Coa_SaveData();
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.AccountGroup);
			// coaSaveData.setAccMasterToSave(
			// 		getUserModifiedCoa_02_AccoungGroup(
			//			new Coa_02_AccountGroup(), new Coa_03_SubAccount01(), new Coa_04_SubAccount02(), new Coa_05_Master()));
			coaSaveData.setAccGroupToSave(getUserModifiedCoa_02_AccoungGroup(new Coa_02_AccountGroup(), typecoa_no));
			
			Events.sendEvent(Events.ON_OK, coa_05_MasterAddDialogWin, coaSaveData);
			
			break;
		case SubAccount01:
			// is the account group selected?
			if (accGroupNameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun group belum diisi.");
			}		
			// is the subAccount01 selected?
			if (subAccount01NameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun SubAccount-01 belum diisi.");
			}				
			// get the typecoa_no and groupcoa_no
			typecoa_no = accTypeNoIntbox.getValue();
			groupcoa_no = accGroupNoIntbox.getValue();
			
			coaSaveData = new Coa_SaveData();
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.SubAccount01);
			// coaSaveData.setAccMasterToSave(
			//		getUserModifiedCoa_03_SubAccount01(
			//				new Coa_03_SubAccount01(), new Coa_04_SubAccount02(), new Coa_05_Master()));
			coaSaveData.setSubAcc01ToSave(getUserModifiedCoa_03_SubAccount01(new Coa_03_SubAccount01(), typecoa_no, groupcoa_no));
			
			Events.sendEvent(Events.ON_OK, coa_05_MasterAddDialogWin, coaSaveData);
			
			break;
		case SubAccount02:						
			// is the account group selected?
			if (accGroupNameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun group belum diisi.");
			}		
			// is the subAccount01 selected?
			if (subAccount01NameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun SubAccount-01 belum diisi.");
			}				
			// is the subAccount02 selected?
			if (subAccount02NameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun SubAccount-02 belum diisi.");
			}						

			// get the typecoa_no, groupcoa_no and subaccount01coa_no
			typecoa_no = accTypeNoIntbox.getValue();
			groupcoa_no = accGroupNoIntbox.getValue();
			subaccount01coa_no = subAccount01NoIntbox.getValue();
			
			coaSaveData = new Coa_SaveData();
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.SubAccount02);
			// coaSaveData.setAccMasterToSave(
			//		getUserModifiedCoa_04_SubAccount02(
			//			new Coa_04_SubAccount02(), new Coa_05_Master()));
			coaSaveData.setSubAcc02ToSave(getUserModifiedCoa_04_SubAccount02(new Coa_04_SubAccount02(), typecoa_no, groupcoa_no, subaccount01coa_no));
			
			Events.sendEvent(Events.ON_OK, coa_05_MasterAddDialogWin, coaSaveData);
			
			break;
		case AccountMaster:			
			// is the account group selected?
			if (accGroupNameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun group belum diisi.");
			}		
			// is the subAccount01 selected?
			if (subAccount01NameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun SubAccount-01 belum diisi.");
			}				
			// is the subAccount02 selected?
			if (subAccount02NameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun SubAccount-02 belum diisi.");
			}						
			// is the subAccount02 selected?
			if (accMasterNameTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak dapat disimpan.  Nama akun Master belum diisi.");
			}						
			// get the typecoa_no, groupcoa_no, subaccount01coa_no and subaccount02coa_no
			typecoa_no = accTypeNoIntbox.getValue();
			groupcoa_no = accGroupNoIntbox.getValue();
			subaccount01coa_no = subAccount01NoIntbox.getValue();
			subaccount02coa_no = subAccount02NoIntbox.getValue();
			
			coaSaveData = new Coa_SaveData();
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.AccountMaster);
			coaSaveData.setAccMasterToSave(getUserModifiedCoa_05_Master(new Coa_05_Master(), typecoa_no, groupcoa_no, subaccount01coa_no, subaccount02coa_no));
			
			Events.sendEvent(Events.ON_OK, coa_05_MasterAddDialogWin, coaSaveData);
			
			break;
		default:
			break;
		}
		
		coa_05_MasterAddDialogWin.detach();
	}
	
	private Coa_02_AccountGroup getUserModifiedCoa_02_AccoungGroup(Coa_02_AccountGroup accGroup, int typecoa_no) {

		accGroup.setAccountGroupName(accGroupNameTextbox.getValue());
		accGroup.setTypeCoaNumber(typecoa_no);
		accGroup.setAccountGroupNumber(accGroupNoIntbox.getValue());
		accGroup.setCreateDate(asDate(getLocalDate()));
		accGroup.setLastModified(asDate(getLocalDate()));
		// Coa_01_AccountType accType = accTypeCombobox.getSelectedItem().getValue()==null ?
		//		getSelectedAccountType() : accTypeCombobox.getSelectedItem().getValue();
		accGroup.setAccountType(accTypeCombobox.getSelectedItem().getValue());

		// subAcc01.setSubAccount01Name(subAccount01NameTextbox.getValue());
		// subAcc01.setSubAccount01Number(subAccount01NoIntbox.getValue());
		// subAcc01.setCreateDate(asDate(getLocalDate()));
		// subAcc01.setLastModified(asDate(getLocalDate()));
		// subAcc01.setAccountGroup(accGroup);
		
		// subAcc02.setSubAccount02Name(subAccount02NameTextbox.getValue());
		// subAcc02.setSubAccount02Number(subAccount02NoIntbox.getValue());
		// subAcc02.setCreateDate(asDate(getLocalDate()));
		// subAcc02.setLastModified(asDate(getLocalDate()));
		// subAcc02.setSubAccount01(subAcc01);
		
		// accMaster.setMasterCoaName(accMasterNameTextbox.getValue());
//		accMaster.setTypeCoaNumber(typeCoaNumber);
		// accMaster.setMasterCoaNumber(accMasterNoIntbox.getValue());
		// accMaster.setMasterCoaComp(resultingCoaTextbox.getValue());
		// accMaster.setCreateDate(asDate(getLocalDate()));
		// accMaster.setLastModified(asDate(getLocalDate()));
		// accMaster.setCreditAccount(creditAccCheckbox.isChecked());
		// accMaster.setActive(accActiveCheckbox.isChecked());
		// accMaster.setSubAccount02(subAcc02);

		return accGroup;
	}
	
	private Coa_03_SubAccount01 getUserModifiedCoa_03_SubAccount01(Coa_03_SubAccount01 subAcc01, int typecoa_no, int groupcoa_no) {
		
		subAcc01.setSubAccount01Name(subAccount01NameTextbox.getValue());
		subAcc01.setTypeCoaNumber(typecoa_no);
		subAcc01.setGroupCoaNumber(groupcoa_no);
		subAcc01.setSubAccount01Number(subAccount01NoIntbox.getValue());
		subAcc01.setCreateDate(asDate(getLocalDate()));
		subAcc01.setLastModified(asDate(getLocalDate()));
		// Coa_02_AccountGroup accGroup = accGroupCombobox.getSelectedItem().getValue()==null ?
		//		getSelectedAccountGroup() : accGroupCombobox.getSelectedItem().getValue();
		subAcc01.setAccountGroup(accGroupCombobox.getSelectedItem().getValue());
		
		// subAcc02.setSubAccount02Name(subAccount02NameTextbox.getValue());
		// subAcc02.setSubAccount02Number(subAccount02NoIntbox.getValue());
		// subAcc02.setCreateDate(asDate(getLocalDate()));
		// subAcc02.setLastModified(asDate(getLocalDate()));
		// subAcc02.setSubAccount01(subAcc01);
		
		// accMaster.setMasterCoaName(accMasterNameTextbox.getValue());
		// accMaster.setMasterCoaNumber(accMasterNoIntbox.getValue());
		// accMaster.setMasterCoaComp(resultingCoaTextbox.getValue());
		// accMaster.setCreateDate(asDate(getLocalDate()));
		// accMaster.setLastModified(asDate(getLocalDate()));
		// accMaster.setCreditAccount(creditAccCheckbox.isChecked());
		// accMaster.setActive(accActiveCheckbox.isChecked());
		// accMaster.setSubAccount02(subAcc02);
		
		return subAcc01;
	}


	private Coa_04_SubAccount02 getUserModifiedCoa_04_SubAccount02(Coa_04_SubAccount02 subAcc02, int typecoa_no, int groupcoa_no, int subaccount01coa_no) {

		subAcc02.setSubAccount02Name(subAccount02NameTextbox.getValue());
		subAcc02.setTypeCoaNumber(typecoa_no);
		subAcc02.setGroupCoaNumber(groupcoa_no);
		subAcc02.setSubaccount01CoaNumber(subaccount01coa_no);
		subAcc02.setSubAccount02Number(subAccount02NoIntbox.getValue());
		subAcc02.setCreateDate(asDate(getLocalDate()));
		subAcc02.setLastModified(asDate(getLocalDate()));
		// Coa_03_SubAccount01 subAcc01 = subAcc01Combobox.getSelectedItem().getValue()==null?
		//		getSelectedSubAccount01() : subAcc01Combobox.getSelectedItem().getValue();
		subAcc02.setSubAccount01(subAcc01Combobox.getSelectedItem().getValue());
		
		// masterAcc.setMasterCoaName(accMasterNameTextbox.getValue());
		// masterAcc.setMasterCoaNumber(accMasterNoIntbox.getValue());
		// masterAcc.setMasterCoaComp(resultingCoaTextbox.getValue());
		// masterAcc.setCreateDate(asDate(getLocalDate()));
		// masterAcc.setLastModified(asDate(getLocalDate()));
		// masterAcc.setCreditAccount(creditAccCheckbox.isChecked());
		// masterAcc.setActive(accActiveCheckbox.isChecked());
		// masterAcc.setSubAccount02(subAcc02);
		
		return subAcc02;
	}

	private Coa_05_Master getUserModifiedCoa_05_Master(Coa_05_Master accMaster, int typecoa_no, int groupcoa_no, int subaccount01coa_no, int subaccount02coa_no) {
		accMaster.setMasterCoaName(accMasterNameTextbox.getValue());
		accMaster.setTypeCoaNumber(typecoa_no);
		accMaster.setGroupCoaNumber(groupcoa_no);
		accMaster.setSubaccount01CoaNumber(subaccount01coa_no);
		accMaster.setSubaccount02CoaNumber(subaccount02coa_no);
		accMaster.setMasterCoaNumber(accMasterNoIntbox.getValue());
		accMaster.setMasterCoaComp(resultingCoaTextbox.getValue());
		accMaster.setCreateDate(asDate(getLocalDate()));
		accMaster.setLastModified(asDate(getLocalDate()));
		accMaster.setCreditAccount(creditAccCheckbox.isChecked());
		accMaster.setActive(accActiveCheckbox.isChecked());			
		// Coa_04_SubAccount02 subAcc02 = subAcc02Combobox.getSelectedItem().getValue()==null ?
		//		getSelectedSubAccount02() : subAcc02Combobox.getSelectedItem().getValue();
		accMaster.setSubAccount02(subAcc02Combobox.getSelectedItem().getValue());

		return accMaster;
	}

	@SuppressWarnings("unused")
	private List<Coa_05_Master> getAccountMastersByProxy(Long id) throws Exception {
		Coa_04_SubAccount02 subAcc02 = getCoa_04_SubAccount02Dao().findAccountMastersByProxy(id);
		
		return subAcc02.getMasters();
	}

	public void onClick$cancelButton(Event event) throws Exception {
		coa_05_MasterAddDialogWin.detach();
	}

	private void disableAllAddButton(boolean disabled) {
		accGroupButton.setDisabled(disabled); 
		subAcc01Button.setDisabled(disabled);
		subAcc02Button.setDisabled(disabled);
		masterButton.setDisabled(disabled);
	}
		
	private Coa_01_AccountType getSelectedAccountType(int selectedTab) {

		return accTypeCombobox.getModel().getSize() == 0 ?
				null : (Coa_01_AccountType) accTypeCombobox.getModel().getElementAt(selectedTab>0 ? selectedTab-1 : 0);
	}
	
	private Coa_02_AccountGroup getSelectedAccountGroup() {
		
		return accGroupCombobox.getModel().getSize() == 0 ?
				null : (Coa_02_AccountGroup) accGroupCombobox.getModel().getElementAt(0);
	}
	
	private Coa_03_SubAccount01 getSelectedSubAccount01() {
		
		return subAcc01Combobox.getModel().getSize() == 0 ?
				null : (Coa_03_SubAccount01) subAcc01Combobox.getModel().getElementAt(0);
	}
	
	private Coa_04_SubAccount02 getSelectedSubAccount02() {
		
		return subAcc02Combobox.getModel().getSize() == 0 ?
				null : (Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(0);
	}
	
	private Coa_05_Master getSelectedAccountMaster() {
		
		return masterCombobox.getModel().getSize() == 0 ?
				null : (Coa_05_Master) masterCombobox.getModel().getElementAt(0);
	}
	
	private int getNewIncrementAccGroup() {
		int numberIncrement = 1;
		int lastIndex = accGroupCombobox.getModel().getSize()-1;
		
		// account Group exist previously
		if (lastIndex>-1) {
			Coa_02_AccountGroup accGroup = 
					(Coa_02_AccountGroup) accGroupCombobox.getModel().getElementAt(lastIndex);
			
			numberIncrement = accGroup.getAccountGroupNumber()+1;
		}
		
		return numberIncrement;
	}
	
	private int getNewIncrementSubAccount01() {
		int numberIncrement = 1;
		int lastIndex = subAcc01Combobox.getModel().getSize()-1;
		
		// SubAccount01 exist previously
		if (lastIndex>-1) {
			Coa_03_SubAccount01 subAccount01 =
					(Coa_03_SubAccount01) subAcc01Combobox.getModel().getElementAt(lastIndex);

			numberIncrement = subAccount01.getSubAccount01Number()+1;
		}
		
		return numberIncrement;
	}
	
	private int getNewIncrementSubAccount02() {
		int numberIncrement = 1;
		int lastIndex = subAcc02Combobox.getModel().getSize()-1;
		
		// SubAccount02 exist previously
		if (lastIndex>-1) {
			Coa_04_SubAccount02 subAccount02 =
					(Coa_04_SubAccount02) subAcc02Combobox.getModel().getElementAt(lastIndex);

			numberIncrement = subAccount02.getSubAccount02Number()+1;
		}
		
		return numberIncrement;
	}
	
	private int getNewIncrementMasterAccount() {
		int numberIncrement = 1;
		int lastIndex = masterCombobox.getModel().getSize()-1;
		
		// account Master exist prevously
		if (lastIndex>-1) {
			Coa_05_Master accMaster =
					(Coa_05_Master) masterCombobox.getModel().getElementAt(lastIndex);

			numberIncrement = accMaster.getMasterCoaNumber()+1;
		}
		
		return numberIncrement;
	}
	
	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}

	public List<Coa_01_AccountType> getAccountTypeList() {
		return accountTypeList;
	}

	public void setAccountTypeList(List<Coa_01_AccountType> accountTypeList) {
		this.accountTypeList = accountTypeList;
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

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public Coa_AccountSelect getDialogAccountSelect() {
		return dialogAccountSelect;
	}

	public void setDialogAccountSelect(Coa_AccountSelect dialogAccountSelect) {
		this.dialogAccountSelect = dialogAccountSelect;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Coa_AccountSelect getSelectedCoa_Account() {
		return selectedCoa_Account;
	}

	public void setSelectedCoa_Account(Coa_AccountSelect selectedCoa_Account) {
		this.selectedCoa_Account = selectedCoa_Account;
	}
}
