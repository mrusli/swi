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
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_01_AccountTypeListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7321954412152396643L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;

	private Label formTitleLabel;
	private Listbox coa_01_AccountTypeListbox;
	
	private List<Coa_01_AccountType> accountTypeList;
	
	public void onCreate$coa_01_AccountTypeListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Chart of Account - Account-Type");
		
		// load the COA
		loadCoa_01_AccountTypeListInfo(); 

	}
	
	private void loadCoa_01_AccountTypeListInfo() throws Exception {
		setAccountTypeList(getCoa_01_AccountTypeDao().findAllCoa_01_AccountType());
		
		coa_01_AccountTypeListbox.setModel(
				new ListModelList<Coa_01_AccountType>(getAccountTypeList()));
		coa_01_AccountTypeListbox.setItemRenderer(getCoa_01_AccountTypeListitemRenderer());
	}

	private ListitemRenderer<Coa_01_AccountType> getCoa_01_AccountTypeListitemRenderer() {
		
		return new ListitemRenderer<Coa_01_AccountType>() {
			
			@Override
			public void render(Listitem item, Coa_01_AccountType accountType, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(accountType.getAccountTypeNumber()+".");
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(accountType.getAccountTypeName());
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), accountType);
				lc.setParent(item);

			}

			private Listcell initEditButton(Listcell listcell, Coa_01_AccountType accountType) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> dataList = new HashMap<String, Object>();
						dataList.put("selectedCoa_Account", Coa_AccountSelect.AccountType);
						dataList.put("accountType", accountType);
						
						Window coaDialogWin = 
								(Window) Executions.createComponents("/coa/Coa_05_MasterEditDialog.zul", null, dataList);
						
						coaDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Coa_SaveData coaSaveData = (Coa_SaveData) event.getData();

								// update
								getCoa_01_AccountTypeDao().update(coaSaveData.getAccTypeForUpdate());
								
								// re-load
								loadCoa_01_AccountTypeListInfo();								
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
}
