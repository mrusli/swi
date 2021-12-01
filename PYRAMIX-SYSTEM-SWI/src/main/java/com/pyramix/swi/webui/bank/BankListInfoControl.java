package com.pyramix.swi.webui.bank;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.bank.BankAccountOwnerType;
import com.pyramix.swi.persistence.bank.dao.BankDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class BankListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6078361622592173060L;

	private BankDao bankDao;
	
	private Label formTitleLabel, infoBanklabel;
	private Listbox bankListbox;
	
	private List<Bank> bankList;
	
	public void onCreate$bankListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Rekening Bank");
		
		// load
		loadBankList();
		
		// list
		listBankListInfo();
	}

	private void loadBankList() throws Exception {
		setBankList(
				getBankDao().findAllBank());
	}

	private void listBankListInfo() {
		bankListbox.setModel(
				new ListModelList<Bank>(getBankList()));
		bankListbox.setItemRenderer(getBankListitemRenderer());
		
	}

	private ListitemRenderer<Bank> getBankListitemRenderer() {

		return new ListitemRenderer<Bank>() {
			
			@Override
			public void render(Listitem item, Bank bank, int index) throws Exception {
				Listcell lc;
				
				// Nama Bank
				lc = new Listcell(bank.getNamaBank());
				lc.setParent(item);
				
				// Cabang
				lc = new Listcell(bank.getNamaCabang());
				lc.setParent(item);
				
				// No Rekening
				lc = new Listcell(bank.getNomorRekening());
				lc.setParent(item);
				
				// Pemilik Rekening
				if (bank.getAccountOwnerType().compareTo(BankAccountOwnerType.PERUSAHAAN)==0) {
					lc = new Listcell(bank.getCompany() == null? " " :
						bank.getCompany().getCompanyType()+". "+
						bank.getCompany().getCompanyLegalName()+" ("+
						bank.getCompany().getCompanyDisplayName()+ ")");					
				} else {
					lc = new Listcell(bank.getAccountOwnerName());
				}
				lc.setParent(item);
				
				// No COA
				lc = new Listcell(bank.getCoaMater().getMasterCoaName()+" ["+
						bank.getCoaMater().getMasterCoaComp()+"]");
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), bank);
				lc.setParent(item);
				
			}

			private Listcell initEdit(Listcell listcell, Bank bank) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.setParent(listcell);
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, Bank> args = Collections.singletonMap("bank", bank);
						
						Window editBankWindow = (Window) Executions.createComponents(
								"/bank/BankDialog.zul", null, args);
						
						editBankWindow.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Bank bank = (Bank) event.getData();
								
								// update
								getBankDao().update(bank);
								
								// re-load
								loadBankList();
								
								// re-list
								listBankListInfo();
							}
						});
						
						editBankWindow.doModal();
					}
				});
				return listcell;
			}
		};
	}

	public void onAfterRender$bankListbox(Event event) throws Exception {
		infoBanklabel.setValue("Total: "+bankListbox.getItemCount()+" Rekening Bank");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		Map<String, Bank> args = Collections.singletonMap("bank", new Bank());
		
		Window editBankWindow = (Window) Executions.createComponents(
				"/bank/BankDialog.zul", null, args);
		
		editBankWindow.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Bank bank = (Bank) event.getData();
				
				// save
				getBankDao().save(bank);
				
				// re-load
				loadBankList();
				
				// re-list
				listBankListInfo();
			}
		});
		
		editBankWindow.doModal();

	}
	
	public BankDao getBankDao() {
		return bankDao;
	}

	public void setBankDao(BankDao bankDao) {
		this.bankDao = bankDao;
	}

	public List<Bank> getBankList() {
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}
}
