package com.pyramix.swi.webui.coa;

public enum Coa_AccountSelect {
	AccountGroup(0), SubAccount01(1), SubAccount02(2), AccountMaster(3), AccountType(4);
	
	private int value;
	
	Coa_AccountSelect(int value) {
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
