package com.pyramix.swi.webui.coa;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class Coa_05_MasterListDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1451832110929263642L;

	private Coa_05_MasterDao coa_05_MasterDao;
	
	private Window coa_05_MasterListDialogWin;
	private Tabbox coaSelectionTabbox;
	private Listbox coa_05_MasterListbox;
	
	private List<Coa_05_Master> coa_05_MasterList;
	
	public void onCreate$coa_05_MasterListDialogWin(Event event) throws Exception {
		coa_05_MasterListDialogWin.setTitle("Pilih Akun");
		
		setCoa_05_MasterList(
				getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp());
		coa_05_MasterListbox.setModel(
				new ListModelList<Coa_05_Master>(getCoa_05_MasterList()));
		coa_05_MasterListbox.setItemRenderer(getCoa_05_MasterListitemRenderer());
	}

	public void onSelect$coaSelectionTabbox(Event event) throws Exception {
		int selIndex = coaSelectionTabbox.getSelectedIndex();
		switch (selIndex) {
		case 0: // semua
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp());			
			break;
		case 1: // Aktifa
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(1));
			break;
		case 2: // Liabilities
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(2));			
			break;
		case 3: // Equity
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(3));			
			break;
		case 4: // Revenue
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(4));			
			break;
		case 5: // Expenses
			setCoa_05_MasterList(
					getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(5));			
			break;
		default:
			break;
		}
		coa_05_MasterListbox.setModel(
				new ListModelList<Coa_05_Master>(getCoa_05_MasterList()));
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
				lc = new Listcell(master.getMasterCoaName());
				lc.setParent(item);
				
				item.setValue(master);
			}
		};
	}

	public void onClick$selectButton(Event event) throws Exception {
		Coa_05_Master master = coa_05_MasterListbox.getSelectedItem().getValue();
		
		if (master==null) {
			throw new Exception("COA belum terpilih.  Pilih COA sebelum klik tombol.");
		}
		
		Events.sendEvent(Events.ON_SELECT, coa_05_MasterListDialogWin, master);
		
		coa_05_MasterListDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		
		Events.sendEvent(Events.ON_CANCEL, coa_05_MasterListDialogWin, null);
		
		coa_05_MasterListDialogWin.detach();
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public List<Coa_05_Master> getCoa_05_MasterList() {
		return coa_05_MasterList;
	}

	public void setCoa_05_MasterList(List<Coa_05_Master> coa_05_MasterList) {
		this.coa_05_MasterList = coa_05_MasterList;
	}
	
}
