package com.pyramix.swi.webui.coa;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
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

public class Coa_05_MasterEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4562663166670516130L;

	private Coa_05_MasterDao coa_05_MasterDao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao;
	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	
	private Window coa_05_MasterEditDialogWin;
	private Textbox accTypeNameTextbox, accGroupNameTextbox, subAccount01NameTextbox, 
		subAccount02NameTextbox, accMasterNameTextbox, resultingCoaTextbox;
	private Intbox accTypeNoIntbox, accGroupNoIntbox, subAccount01NoIntbox, 
		subAccount02NoIntbox, accMasterNoIntbox; 
	private Checkbox creditAccCheckbox, accActiveCheckbox;
	private Grid editCOAGrid;
	
	private Coa_AccountSelect selectedCoa_Account;
	private Coa_05_Master accMaster;
	private Coa_04_SubAccount02 accSubAccount02;
	private Coa_03_SubAccount01 accSubAccount01;
	private Coa_02_AccountGroup accGroup;
	private Coa_01_AccountType accType;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSelectedCoa_Account((Coa_AccountSelect) arg.get("selectedCoa_Account"));
		switch (getSelectedCoa_Account()) {
		case AccountMaster:
			setAccMaster((Coa_05_Master) arg.get("accountMaster"));			
			break;
		case SubAccount02:
			setAccSubAccount02((Coa_04_SubAccount02) arg.get("accountSubAccount02"));
			break;
		case SubAccount01:
			setAccSubAccount01((Coa_03_SubAccount01) arg.get("accountSubAcoount01"));
			break;
		case AccountGroup:
			setAccGroup((Coa_02_AccountGroup) arg.get("accountGroup"));
			break;
		case AccountType:
			setAccType((Coa_01_AccountType) arg.get("accountType"));
			break;
		default:
			break;
		}
	}

	public void onCreate$coa_05_MasterEditDialogWin(Event event) throws Exception {
		coa_05_MasterEditDialogWin.setTitle("Merubah (Edit) COA");
		
		if (getSelectedCoa_Account().compareTo(Coa_AccountSelect.AccountMaster)==0) {
			accMasterNameTextbox.setValue(getAccMaster().getMasterCoaName());
			accMasterNoIntbox.setValue(getAccMaster().getMasterCoaNumber());
			resultingCoaTextbox.setValue(getAccMaster().getMasterCoaComp());
			creditAccCheckbox.setChecked(getAccMaster().isCreditAccount()); 
			accActiveCheckbox.setChecked(getAccMaster().isActive());
			
			Coa_05_Master accMasterByProxy = getCoa_05_MasterDao().findCoa_04_SubAccount02_ByProxy(getAccMaster().getId());			
			Coa_04_SubAccount02 subAcc02ByProxy = accMasterByProxy.getSubAccount02(); 
			
			subAccount02NameTextbox.setValue(subAcc02ByProxy.getSubAccount02Name());
			subAccount02NameTextbox.setDisabled(true);
			subAccount02NoIntbox.setValue(subAcc02ByProxy.getSubAccount02Number());
			subAccount02NoIntbox.setDisabled(true);
			
			Coa_04_SubAccount02 accSubAccount02ByProxy = getCoa_04_SubAccount02Dao().findCoa_03_SubAccount01_ByProxy(subAcc02ByProxy.getId());

			Coa_03_SubAccount01 subAcc01ByProxy = accSubAccount02ByProxy.getSubAccount01();
			subAccount01NameTextbox.setValue(subAcc01ByProxy.getSubAccount01Name());
			subAccount01NameTextbox.setDisabled(true);
			subAccount01NoIntbox.setValue(subAcc01ByProxy.getSubAccount01Number());
			subAccount01NoIntbox.setDisabled(true);
			
			Coa_03_SubAccount01 accSubAccount01ByProxy = getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(subAcc01ByProxy.getId());

			Coa_02_AccountGroup accountGroupByProxy = accSubAccount01ByProxy.getAccountGroup();
			accGroupNameTextbox.setValue(accountGroupByProxy.getAccountGroupName());
			accGroupNameTextbox.setDisabled(true);
			accGroupNoIntbox.setValue(accountGroupByProxy.getAccountGroupNumber());
			accGroupNoIntbox.setDisabled(true);
			
			Coa_02_AccountGroup accGroupByProxy = getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroupByProxy.getId());

			Coa_01_AccountType accountTypeByProxy = accGroupByProxy.getAccountType();
			accTypeNameTextbox.setValue(accountTypeByProxy.getAccountTypeName());
			accTypeNameTextbox.setDisabled(true);
			accTypeNoIntbox.setValue(accountTypeByProxy.getAccountTypeNumber());
			accTypeNoIntbox.setDisabled(true);
		}
		
		if (getSelectedCoa_Account().compareTo(Coa_AccountSelect.SubAccount02)==0) {
			accMasterNameTextbox.setDisabled(true);
			accMasterNoIntbox.setDisabled(true);
			
			editCOAGrid.setVisible(false);

			subAccount02NameTextbox.setValue(getAccSubAccount02().getSubAccount02Name());
			subAccount02NoIntbox.setValue(getAccSubAccount02().getSubAccount02Number());
			
			Coa_04_SubAccount02 accSubAccount02ByProxy = getCoa_04_SubAccount02Dao().findCoa_03_SubAccount01_ByProxy(getAccSubAccount02().getId());
			
			Coa_03_SubAccount01 subAcc01ByProxy = accSubAccount02ByProxy.getSubAccount01();
			subAccount01NameTextbox.setValue(subAcc01ByProxy.getSubAccount01Name());
			subAccount01NameTextbox.setDisabled(true);
			subAccount01NoIntbox.setValue(subAcc01ByProxy.getSubAccount01Number());
			subAccount01NoIntbox.setDisabled(true);
			
			Coa_03_SubAccount01 accSubAccount01ByProxy = getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(subAcc01ByProxy.getId());

			Coa_02_AccountGroup accountGroupByProxy = accSubAccount01ByProxy.getAccountGroup();
			accGroupNameTextbox.setValue(accountGroupByProxy.getAccountGroupName());
			accGroupNameTextbox.setDisabled(true);
			accGroupNoIntbox.setValue(accountGroupByProxy.getAccountGroupNumber());
			accGroupNoIntbox.setDisabled(true);
			
			Coa_02_AccountGroup accGroupByProxy = getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroupByProxy.getId());

			Coa_01_AccountType accountTypeByProxy = accGroupByProxy.getAccountType();
			accTypeNameTextbox.setValue(accountTypeByProxy.getAccountTypeName());
			accTypeNameTextbox.setDisabled(true);
			accTypeNoIntbox.setValue(accountTypeByProxy.getAccountTypeNumber());
			accTypeNoIntbox.setDisabled(true);
		}
		
		if (getSelectedCoa_Account().compareTo(Coa_AccountSelect.SubAccount01)==0) {
			accMasterNameTextbox.setDisabled(true);
			accMasterNoIntbox.setDisabled(true);
			
			editCOAGrid.setVisible(false);

			subAccount02NameTextbox.setDisabled(true);
			subAccount02NoIntbox.setDisabled(true);
			
			subAccount01NameTextbox.setValue(getAccSubAccount01().getSubAccount01Name());
			subAccount01NoIntbox.setValue(getAccSubAccount01().getSubAccount01Number());

			Coa_03_SubAccount01 accSubAccount01ByProxy = getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(getAccSubAccount01().getId());

			Coa_02_AccountGroup accountGroupByProxy = accSubAccount01ByProxy.getAccountGroup();
			accGroupNameTextbox.setValue(accountGroupByProxy.getAccountGroupName());
			accGroupNameTextbox.setDisabled(true);
			accGroupNoIntbox.setValue(accountGroupByProxy.getAccountGroupNumber());
			accGroupNoIntbox.setDisabled(true);
			
			Coa_02_AccountGroup accGroupByProxy = getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroupByProxy.getId());

			Coa_01_AccountType accountTypeByProxy = accGroupByProxy.getAccountType();
			accTypeNameTextbox.setValue(accountTypeByProxy.getAccountTypeName());
			accTypeNameTextbox.setDisabled(true);
			accTypeNoIntbox.setValue(accountTypeByProxy.getAccountTypeNumber());
			accTypeNoIntbox.setDisabled(true);
		}

		if (getSelectedCoa_Account().compareTo(Coa_AccountSelect.AccountGroup)==0) {
			accMasterNameTextbox.setDisabled(true);
			accMasterNoIntbox.setDisabled(true);
			
			editCOAGrid.setVisible(false);

			subAccount02NameTextbox.setDisabled(true);
			subAccount02NoIntbox.setDisabled(true);

			subAccount01NameTextbox.setDisabled(true);
			subAccount01NoIntbox.setDisabled(true);
			
			accGroupNameTextbox.setValue(getAccGroup().getAccountGroupName());
			accGroupNoIntbox.setValue(getAccGroup().getAccountGroupNumber());

			Coa_02_AccountGroup accGroupByProxy = getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(getAccGroup().getId());

			Coa_01_AccountType accountTypeByProxy = accGroupByProxy.getAccountType();
			accTypeNameTextbox.setValue(accountTypeByProxy.getAccountTypeName());
			accTypeNameTextbox.setDisabled(true);
			accTypeNoIntbox.setValue(accountTypeByProxy.getAccountTypeNumber());
			accTypeNoIntbox.setDisabled(true);
		}
		
		if (getSelectedCoa_Account().compareTo(Coa_AccountSelect.AccountType)==0) {
			accMasterNameTextbox.setDisabled(true);
			accMasterNoIntbox.setDisabled(true);
			
			editCOAGrid.setVisible(false);

			subAccount02NameTextbox.setDisabled(true);
			subAccount02NoIntbox.setDisabled(true);

			subAccount01NameTextbox.setDisabled(true);
			subAccount01NoIntbox.setDisabled(true);

			accGroupNameTextbox.setDisabled(true);
			accGroupNoIntbox.setDisabled(true);

			accTypeNameTextbox.setValue(getAccType().getAccountTypeName());
			accTypeNoIntbox.setValue(getAccType().getAccountTypeNumber());			
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		Coa_SaveData coaSaveData = null;
		
		switch (getSelectedCoa_Account()) {
		case AccountMaster:
			Coa_05_Master updatedAccMaster = getAccMaster();

			updatedAccMaster.setMasterCoaName(accMasterNameTextbox.getValue());
			updatedAccMaster.setMasterCoaNumber(accMasterNoIntbox.getValue());
			updatedAccMaster.setMasterCoaComp(resultingCoaTextbox.getValue());
			updatedAccMaster.setLastModified(asDateTime(getLocalDateTime()));
			updatedAccMaster.setCreditAccount(creditAccCheckbox.isChecked());
			updatedAccMaster.setActive(accActiveCheckbox.isChecked());

			coaSaveData = new Coa_SaveData();
			coaSaveData.setAccMasterForUpdate(getAccMaster());
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.AccountMaster);
			
			break;
		case SubAccount02:
			Coa_04_SubAccount02 updatedSubAcc02 = getAccSubAccount02();
			
			updatedSubAcc02.setSubAccount02Name(subAccount02NameTextbox.getValue());
			updatedSubAcc02.setSubAccount02Number(subAccount02NoIntbox.getValue());
			updatedSubAcc02.setLastModified(asDateTime(getLocalDateTime()));
			
			coaSaveData = new Coa_SaveData();
			coaSaveData.setSubAcc02ForUpdate(updatedSubAcc02);
			coaSaveData.setDialogAccountSelect(Coa_AccountSelect.SubAccount02);
			
			break;
		case SubAccount01:
			
			break;
		case AccountGroup:
			
			break;
		case AccountType:
			
			break;
		default:
			break;
		}
			
		Events.sendEvent(Events.ON_CHANGE, coa_05_MasterEditDialogWin, coaSaveData);

		
		// Coa_03_SubAccount01 updatedSubAcc01 = updatedSubAcc02.getSubAccount01();
		
		// updatedSubAcc01.setSubAccount01Name(subAccount01NameTextbox.getValue());
		// updatedSubAcc01.setSubAccount01Number(subAccount01NoIntbox.getValue());
		
		// Coa_02_AccountGroup updatedAccGroup = updatedSubAcc01.getAccountGroup();
		
		// updatedAccGroup.setAccountGroupName(accGroupNameTextbox.getValue());
		// updatedAccGroup.setAccountGroupNumber(accGroupNoIntbox.getValue());
		
		coa_05_MasterEditDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		coa_05_MasterEditDialogWin.detach();
	}
	
	public Coa_05_Master getAccMaster() {
		return accMaster;
	}

	public void setAccMaster(Coa_05_Master accMaster) {
		this.accMaster = accMaster;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public Coa_04_SubAccount02Dao getCoa_04_SubAccount02Dao() {
		return coa_04_SubAccount02Dao;
	}

	public void setCoa_04_SubAccount02Dao(Coa_04_SubAccount02Dao coa_04_SubAccount02Dao) {
		this.coa_04_SubAccount02Dao = coa_04_SubAccount02Dao;
	}

	public Coa_03_SubAccount01Dao getCoa_03_SubAccount01Dao() {
		return coa_03_SubAccount01Dao;
	}

	public void setCoa_03_SubAccount01Dao(Coa_03_SubAccount01Dao coa_03_SubAccount01Dao) {
		this.coa_03_SubAccount01Dao = coa_03_SubAccount01Dao;
	}

	public Coa_02_AccountGroupDao getCoa_02_AccountGroupDao() {
		return coa_02_AccountGroupDao;
	}

	public void setCoa_02_AccountGroupDao(Coa_02_AccountGroupDao coa_02_AccountGroupDao) {
		this.coa_02_AccountGroupDao = coa_02_AccountGroupDao;
	}

	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}

	public Coa_AccountSelect getSelectedCoa_Account() {
		return selectedCoa_Account;
	}

	public void setSelectedCoa_Account(Coa_AccountSelect selectedCoa_Account) {
		this.selectedCoa_Account = selectedCoa_Account;
	}

	public Coa_04_SubAccount02 getAccSubAccount02() {
		return accSubAccount02;
	}

	public void setAccSubAccount02(Coa_04_SubAccount02 accSubAccount02) {
		this.accSubAccount02 = accSubAccount02;
	}

	public Coa_03_SubAccount01 getAccSubAccount01() {
		return accSubAccount01;
	}

	public void setAccSubAccount01(Coa_03_SubAccount01 accSubAccount01) {
		this.accSubAccount01 = accSubAccount01;
	}

	public Coa_02_AccountGroup getAccGroup() {
		return accGroup;
	}

	public void setAccGroup(Coa_02_AccountGroup accGroup) {
		this.accGroup = accGroup;
	}

	public Coa_01_AccountType getAccType() {
		return accType;
	}

	public void setAccType(Coa_01_AccountType accType) {
		this.accType = accType;
	}

}
