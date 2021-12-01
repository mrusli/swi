package com.pyramix.swi.webui.settings;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.persistence.bank.dao.BankDao;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class SettingsListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4661001081740792106L;

	private SettingsDao settingsDao;
	private CompanyDao companyDao;
	private BankDao bankDao;
	
	private Label formTitleLabel;
	private Combobox organizationLegalNameCombobox, bankAccountPpnCombobox,
		bankAccountNonPpnCombobox;
	
	private Settings settings;
	
	private final long DEFAULT_COMPANY_ID	= 1L;
	
	public void onCreate$settingsListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Settings");
		
		// company list in combobox
		setupOrganizationsCombobox();
		
		// bank list in combobox
		setupBankCombobox();
		
		// set current settings
		setSettings(
				getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID));
		
		// display
		displaySettingsInfo();
	}
	
	private void setupOrganizationsCombobox() throws Exception {
		List<Company> companyList = getCompanyDao().findAllCompany();
		
		for (Company company : companyList) {
			Comboitem item = new Comboitem();
			
			item.setLabel(company.getCompanyType()+". "+
					company.getCompanyLegalName()+" ("+
					company.getCompanyDisplayName()+")");
			item.setValue(company);
			item.setParent(organizationLegalNameCombobox);
		}
	}

	private void setupBankCombobox() throws Exception {
		List<Bank> bankList = getBankDao().findAllBank();
		
		// for ppn transaction - perusahan
		for (Bank bank : bankList) {
			Comboitem comboitem = new Comboitem();
			
			if (bank.getCompany()==null) {
				// do nothing
			} else {
				comboitem.setLabel(bank.getNamaBank()+" ["+
						bank.getNomorRekening()+"]"+" a/n."+
						bank.getCompany().getCompanyType()+". "+
						bank.getCompany().getCompanyLegalName()+" ("+
						bank.getCompany().getCompanyDisplayName()+")");
				comboitem.setValue(bank);
				comboitem.setParent(bankAccountPpnCombobox);			
			}	
		}
		
		// for non-ppn transaction - pribadi
		for (Bank bank : bankList) {
			Comboitem comboitem = new Comboitem();
			
			if (bank.getAccountOwnerName()==null) {
				// do nothing
			} else {
				comboitem.setLabel(bank.getNamaBank()+" ["+
						bank.getNomorRekening()+"]"+" a/n."+
						bank.getAccountOwnerName());
				comboitem.setValue(bank);
				comboitem.setParent(bankAccountNonPpnCombobox);			
			}
		}

	}

	private void displaySettingsInfo() {
		// selected company
		List<Comboitem> companyItems = organizationLegalNameCombobox.getItems();
		for (Comboitem comboitem : companyItems) {
			if (comboitem.getValue().equals(getSettings().getSelectedCompany())) {
				organizationLegalNameCombobox.setSelectedItem(comboitem);
				
				break;
			}
		}
		
		// selected ppn transaction bank
		List<Comboitem> ppnTransBankComboitems = bankAccountPpnCombobox.getItems(); 
		for (Comboitem comboitem : ppnTransBankComboitems) {
			if (comboitem.getValue().equals(getSettings().getPpnTransactionBank())) {
				bankAccountPpnCombobox.setSelectedItem(comboitem);
				
				break;
			}
		}
		
		// selected non-ppn transaction bank
		List<Comboitem> nonPpnTransBankComboitems = bankAccountNonPpnCombobox.getItems();
		for (Comboitem comboitem : nonPpnTransBankComboitems) {
			if (comboitem.getValue().equals(getSettings().getNonPpnTransactionBank())) {
				bankAccountNonPpnCombobox.setSelectedItem(comboitem);
			}
		}
	}

	public void onClick$saveButton(Event event) throws Exception {
		// set the selected company
		getSettings().setSelectedCompany(
				organizationLegalNameCombobox.getSelectedItem().getValue());
		// set the ppn transaction bank
		getSettings().setPpnTransactionBank(
				bankAccountPpnCombobox.getSelectedItem().getValue());
		// set the non ppn transaction bank
		getSettings().setNonPpnTransactionBank(
				bankAccountNonPpnCombobox.getSelectedItem().getValue());
		
		// update
		getSettingsDao().update(getSettings());
		
		// notify user 
		Clients.showNotification("Perubahan Settings Sudah Disimpan", 
				"info", null, "bottom_right", 0);
	}
	
	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public BankDao getBankDao() {
		return bankDao;
	}

	public void setBankDao(BankDao bankDao) {
		this.bankDao = bankDao;
	}
	
	
	
}
