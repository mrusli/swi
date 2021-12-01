package com.pyramix.swi.webui.bank;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.bank.BankAccountOwnerType;
import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class BankDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1823454780377576749L;

	private CompanyDao companyDao;
	
	private Window bankDialogWin;
	private Combobox companyCombobox, tipePemilikRekCombobox;
	private Textbox bankNameTextbox, cabangNameTextbox, nomorRekeningTextbox,
		nomorCoaTextbox, individualTextbox;
	
	private Bank bank;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setBank(
			(Bank) arg.get("bank"));
	}
	
	public void onCreate$bankDialogWin(Event event) throws Exception {
		bankDialogWin.setTitle(
				getBank().getId().compareTo(Long.MIN_VALUE)==0 ?
				"Tambah (Add) Rekening Bank" : "Rubah (Edit) Rekening Bank");
		
		// setup account owner type
		setupAccountOwnerType();

		// setup company combobox
		setupCompanyCombobox();
		
		// set bank info
		setBankInfo();
	}

	private void setupAccountOwnerType() {
		Comboitem comboitem;
		for (BankAccountOwnerType ownerType: BankAccountOwnerType.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(ownerType.toString());
			comboitem.setValue(ownerType);
			comboitem.setParent(tipePemilikRekCombobox);
		}
	}
	
	private void setupCompanyCombobox() throws Exception {
		List<Company> companyList = getCompanyDao().findAllCompany();
		
		Comboitem comboitem;
		for (Company company : companyList) {
			comboitem = new Comboitem();
			comboitem.setLabel(company.getCompanyType()+". "+
					company.getCompanyLegalName()+" ("+
					company.getCompanyDisplayName()+")");
			comboitem.setValue(company);
			comboitem.setParent(companyCombobox);
		}			
		
		if (getBank().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			
			// default to perusahaan
			individualTextbox.setVisible(false);
			companyCombobox.setVisible(true);
						
			return;
		}
		
		if (getBank().getAccountOwnerType().compareTo(BankAccountOwnerType.PRIBADI)==0) {
			// pribadi
			companyCombobox.setVisible(false);
			individualTextbox.setVisible(true);
		} else {
			// perusahaan
			individualTextbox.setVisible(false);
			companyCombobox.setVisible(true);
		}

	}

	private void setBankInfo() {
		if (getBank().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			bankNameTextbox.setValue(""); 
			cabangNameTextbox.setValue("");
			nomorRekeningTextbox.setValue("");
			tipePemilikRekCombobox.setSelectedIndex(0);
			companyCombobox.setSelectedIndex(0);
			individualTextbox.setValue("");
			nomorCoaTextbox.setValue("");
		} else {
			// edit
			bankNameTextbox.setValue(getBank().getNamaBank());
			cabangNameTextbox.setValue(getBank().getNamaCabang());
			nomorRekeningTextbox.setValue(getBank().getNomorRekening());
			for (Comboitem comboitem : tipePemilikRekCombobox.getItems()) {
				if (comboitem.getValue().equals(getBank().getAccountOwnerType())) {
					tipePemilikRekCombobox.setSelectedItem(comboitem);
				}
			}
			for (Comboitem comboitem : companyCombobox.getItems()) {
				if (comboitem.getValue().equals(getBank().getCompany())) {
					companyCombobox.setSelectedItem(comboitem);
				}
			}
			individualTextbox.setValue(getBank().getAccountOwnerName());
			nomorCoaTextbox.setValue(getBank().getCoaMater().getMasterCoaName()+" ["+
					getBank().getCoaMater().getMasterCoaComp()+"]");
			nomorCoaTextbox.setAttribute("masterCoa", getBank().getCoaMater());
		}
	}
	
	public void onSelect$tipePemilikRekCombobox(Event event) throws Exception {
		int selIndex = tipePemilikRekCombobox.getSelectedIndex();
		
		if (selIndex==0) {
			// perusahaan
			individualTextbox.setVisible(false);
			companyCombobox.setVisible(true);			
		} else {
			// pribadi
			companyCombobox.setVisible(false);
			individualTextbox.setVisible(true);
		}
	}
	
	public void onClick$masterCoaSelectButton(Event event) throws Exception {
		Window masterCoaWin = (Window) Executions.createComponents(
				"/coa/Coa_05_MasterListDialog.zul", null, null);
		
		masterCoaWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
				
				// set coa number (No Akun)
				nomorCoaTextbox.setValue(masterCoa.getMasterCoaName()+" ["+
						masterCoa.getMasterCoaComp()+"]");
				
				// set coa object
				nomorCoaTextbox.setAttribute("masterCoa", masterCoa);
			}
		});
		
		masterCoaWin.doModal();
		
	}

	public void onClick$saveButton(Event event) throws Exception {
		Bank modBank = getModifiedBankInfo();
		
		Events.sendEvent(Events.ON_OK, bankDialogWin, modBank);
		
		bankDialogWin.detach();
	}
	
	private Bank getModifiedBankInfo() {
		Bank modBank = getBank();
		modBank.setNamaBank(bankNameTextbox.getValue());
		modBank.setNamaCabang(cabangNameTextbox.getValue());
		modBank.setNomorRekening(nomorRekeningTextbox.getValue());
		modBank.setAccountOwnerType(tipePemilikRekCombobox.getSelectedItem().getValue());
		if (tipePemilikRekCombobox.getSelectedItem().getValue().equals(BankAccountOwnerType.PERUSAHAAN)) {
			modBank.setCompany(companyCombobox.getSelectedItem().getValue());
			modBank.setAccountOwnerName(null);
		} else {
			modBank.setAccountOwnerName(individualTextbox.getValue());
			modBank.setCompany(null);
		}
		modBank.setCoaMater((Coa_05_Master) nomorCoaTextbox.getAttribute("masterCoa"));

		return modBank;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		
		bankDialogWin.detach();
	}
	
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	
}
