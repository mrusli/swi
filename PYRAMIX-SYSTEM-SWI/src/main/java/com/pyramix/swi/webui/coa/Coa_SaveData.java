package com.pyramix.swi.webui.coa;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.domain.coa.Coa_05_Master;

public class Coa_SaveData {

	private Coa_AccountSelect dialogAccountSelect;
	
	private Coa_01_AccountType accTypeForUpdate;
	
	private Coa_02_AccountGroup accGroupForUpdate;
	private Coa_02_AccountGroup accGroupToSave;
	
	private Coa_03_SubAccount01 subAcc01ForUpdate;
	private Coa_03_SubAccount01 subAcc01ToSave;
	
	private Coa_04_SubAccount02 subAcc02ForUpdate;
	private Coa_04_SubAccount02 subAcc02ToSave;
	
	private Coa_05_Master accMasterForUpdate;
	private Coa_05_Master accMasterToSave;

	public Coa_AccountSelect getDialogAccountSelect() {
		return dialogAccountSelect;
	}

	public void setDialogAccountSelect(Coa_AccountSelect dialogAccountSelect) {
		this.dialogAccountSelect = dialogAccountSelect;
	}

	public Coa_01_AccountType getAccTypeForUpdate() {
		return accTypeForUpdate;
	}

	public void setAccTypeForUpdate(Coa_01_AccountType accTypeForUpdate) {
		this.accTypeForUpdate = accTypeForUpdate;
	}

	public Coa_02_AccountGroup getAccGroupForUpdate() {
		return accGroupForUpdate;
	}

	public void setAccGroupForUpdate(Coa_02_AccountGroup accGroupForUpdate) {
		this.accGroupForUpdate = accGroupForUpdate;
	}

	public Coa_03_SubAccount01 getSubAcc01ForUpdate() {
		return subAcc01ForUpdate;
	}

	public void setSubAcc01ForUpdate(Coa_03_SubAccount01 subAcc01ForUpdate) {
		this.subAcc01ForUpdate = subAcc01ForUpdate;
	}

	public Coa_05_Master getAccMasterToSave() {
		return accMasterToSave;
	}

	public void setAccMasterToSave(Coa_05_Master accMasterToSave) {
		this.accMasterToSave = accMasterToSave;
	}

	public Coa_04_SubAccount02 getSubAcc02ToSave() {
		return subAcc02ToSave;
	}

	public void setSubAcc02ToSave(Coa_04_SubAccount02 subAcc02ToSave) {
		this.subAcc02ToSave = subAcc02ToSave;
	}

	public Coa_02_AccountGroup getAccGroupToSave() {
		return accGroupToSave;
	}

	public void setAccGroupToSave(Coa_02_AccountGroup accGroupToSave) {
		this.accGroupToSave = accGroupToSave;
	}

	public Coa_03_SubAccount01 getSubAcc01ToSave() {
		return subAcc01ToSave;
	}

	public void setSubAcc01ToSave(Coa_03_SubAccount01 subAcc01ToSave) {
		this.subAcc01ToSave = subAcc01ToSave;
	}

	public Coa_04_SubAccount02 getSubAcc02ForUpdate() {
		return subAcc02ForUpdate;
	}

	public void setSubAcc02ForUpdate(Coa_04_SubAccount02 subAcc02ForUpdate) {
		this.subAcc02ForUpdate = subAcc02ForUpdate;
	}

	public Coa_05_Master getAccMasterForUpdate() {
		return accMasterForUpdate;
	}

	public void setAccMasterForUpdate(Coa_05_Master accMasterForUpdate) {
		this.accMasterForUpdate = accMasterForUpdate;
	}
}
