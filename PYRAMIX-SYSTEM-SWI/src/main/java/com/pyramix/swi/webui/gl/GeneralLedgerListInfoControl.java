package com.pyramix.swi.webui.gl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.EndingBalance;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.gl.dao.EndingBalanceDao;
import com.pyramix.swi.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class GeneralLedgerListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7118552965008073032L;

	private GeneralLedgerDao generalLedgerDao;
	private Coa_05_MasterDao coa_05_MasterDao;
	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private EndingBalanceDao endingBalanceDao;
	
	private Label formTitleLabel;
	private Combobox coaTypeCombobox;
	private Listbox masterCoaListbox, generalLedgerListbox;
	private Datebox startDatebox, endDatebox;
		
	private final int AKTIFA	= 1;
	private List<GeneralLedger> allGLList = new ArrayList<GeneralLedger>();
	
	// temp: for testing ONLY
	private final long DAY_DIFF	= new Long(0);
	
	public void onCreate$generalLedgerListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Aktivitas Junal (General Ledger)");
		
		// startDate - today's date
		startDatebox.setLocale(getLocale());
		startDatebox.setFormat(getShortDateFormat());
		startDatebox.setValue(asDate(minusDate(DAY_DIFF, getLocalDate())));
		// endDate - today's date
		endDatebox.setLocale(getLocale());
		endDatebox.setFormat(getShortDateFormat());
		endDatebox.setValue(asDate(minusDate(DAY_DIFF, getLocalDate())));
		
		// account type
		setupAccountTypeCombobox();
		
		// default to 'Aktifa'
		coaTypeCombobox.setSelectedIndex(0);
		
		// 'Aktifa' masterCoa list
		setupMasterCoaListbox(AKTIFA);
	}
	
	private void setupAccountTypeCombobox() throws Exception {
		List<Coa_01_AccountType> accountTypeList =
				coa_01_AccountTypeDao.findAllCoa_01_AccountType();
		
		Comboitem comboitem;
		
		for (Coa_01_AccountType accType : accountTypeList) {
			comboitem = new Comboitem();
			comboitem.setValue(accType);
			comboitem.setLabel(accType.getAccountTypeName());
			comboitem.setParent(coaTypeCombobox);
		}
	}

	public void onSelect$coaTypeCombobox(Event event) throws Exception {
		Comboitem selItem = coaTypeCombobox.getSelectedItem();
		
		Coa_01_AccountType accountType = selItem.getValue();
		setupMasterCoaListbox(accountType.getAccountTypeNumber());
	}
	
	private void setupMasterCoaListbox(int accountTypeNumber) throws Exception {
		List<Coa_05_Master> coaMasterList = 
				getCoa_05_MasterDao().find_ActiveOnly_Coa_05_Master_by_AccountType(accountTypeNumber);
					// findAllCoa_05_Master_by_AccountType(accountTypeNumber);
				
		masterCoaListbox.setModel(new ListModelList<Coa_05_Master>(coaMasterList));
		masterCoaListbox.setItemRenderer(getMasterCoaListitemRenderer());
	}	
	
	private ListitemRenderer<Coa_05_Master> getMasterCoaListitemRenderer() {
		
		return new ListitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Listitem item, Coa_05_Master coaMaster, int index) throws Exception {
				Listcell lc;
				
				// checkbox
				lc = initCheckbox(new Listcell(), coaMaster);
				lc.setParent(item);
				
				// No.COA
				lc = new Listcell(coaMaster.getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(coaMaster.getMasterCoaName());
				lc.setParent(item);
				
				item.setValue(coaMaster);
			}

			private Listcell initCheckbox(Listcell listcell, Coa_05_Master coaMaster) {
				Checkbox checkbox = new Checkbox();
				checkbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$executeChooseDateButton(Event event) throws Exception {
		// clear the list - in case it had been populated
		allGLList.clear();
		// for holding the masterCoa selected by user
		List<Coa_05_Master> selCoaMaster = new ArrayList<Coa_05_Master>();
		
		for(Listitem item : masterCoaListbox.getItems()) {
			Component comp = item.getChildren().get(0).getFirstChild();
			Checkbox checkbox;
			if (comp!=null) {
				// checkbox
				checkbox = (Checkbox) comp;
				if (checkbox.isChecked()) {
					Coa_05_Master coaMaster = item.getValue();
					
					selCoaMaster.add(coaMaster);
					// System.out.println(coaMaster.getMasterCoaName());
				}
			}
		}
		
		Date startDate = startDatebox.getValue();
		Date endDate = endDatebox.getValue();
		
		long days = dayDiff(asLocalDate(startDate), asLocalDate(endDate));
		
		BigDecimal beginingBal	= BigDecimal.ZERO;
		BigDecimal totalDebitAmount		= BigDecimal.ZERO;
		BigDecimal totalCreditAmount	= BigDecimal.ZERO;
		BigDecimal closingAmount		= BigDecimal.ZERO;
		
		for (int i=0; i<=days; i++) {

			for (Coa_05_Master coaMaster : selCoaMaster) {
				allGLList.add(displayCoaNumber(coaMaster));
				
				// get the beginning balance for this coaMaster with the date
				Date prevDayDate = asDate(minusDate(new Long(1), asLocalDate(startDate)));
				
				beginingBal = findCarriedForwardBalanceAmount(coaMaster, prevDayDate);
				allGLList.add(balanceCarriedForward(beginingBal, prevDayDate));
				
				List<GeneralLedger> glList = 
						getGeneralLedgerDao().findAllGeneralLedgerByMasterCoa_PostingDate(coaMaster, startDate);
			
				totalDebitAmount	= accumulateTotalDebitAmount(glList);
				totalCreditAmount	= accumulateTotalCreditAmount(glList);
				
				// calc closing balance
				totalDebitAmount = beginingBal.add(totalDebitAmount);
				closingAmount = totalDebitAmount.subtract(totalCreditAmount);
				
				// System.out.println(totalDebitAmount);
				// System.out.println(totalCreditAmount.add(closingAmount));
				
				allGLList.addAll(glList);
				// closing balance for today's date
				allGLList.add(closingBalance(closingAmount, coaMaster, startDate));
				
				// balance debit / credit
				allGLList.add(endingDebitCredit(totalDebitAmount, totalCreditAmount.add(closingAmount)));
			}
			
			startDate = asDate(addDate(1, asLocalDate(startDate)));
		}
		
		displayGL(allGLList);
	}

	private GeneralLedger displayCoaNumber(Coa_05_Master coaMaster) {
		GeneralLedger gl = new GeneralLedger();
		gl.setDbcrDescription("Account No.: "+coaMaster.getMasterCoaComp()+
				" - "+coaMaster.getMasterCoaName());
		gl.setDebitAmount(null);
		gl.setCreditAmount(null);
		
		return gl;
	}

	private BigDecimal findCarriedForwardBalanceAmount(Coa_05_Master coaMaster, Date balanceDate) throws Exception {
		EndingBalance endingBalance = 
				getEndingBalanceDao().findEndingBalanceByMasterCoa_EndingDate(coaMaster, balanceDate);
		
		// System.out.println(endingBalance);
		
		return endingBalance==null? BigDecimal.ZERO : endingBalance.getEndingAmount();
	}

	private BigDecimal accumulateTotalDebitAmount(List<GeneralLedger> glList) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		for (GeneralLedger gl : glList) {
			totalAmount = totalAmount.add(gl.getDebitAmount());
		}
		
		return totalAmount;
	}

	private BigDecimal accumulateTotalCreditAmount(List<GeneralLedger> glList) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		for (GeneralLedger gl : glList) {
			totalAmount = totalAmount.add(gl.getCreditAmount());
		}
		
		return totalAmount;
	}
	
	private GeneralLedger balanceCarriedForward(BigDecimal carriedForwardAmount, Date balDate) throws Exception {		
		GeneralLedger gl = new GeneralLedger();
		gl.setDbcrDescription("Balance Carried Forward ("+dateToStringDB(asLocalDate(balDate), getShortDateFormat())+")");
		gl.setDebitAmount(carriedForwardAmount);
		gl.setCreditAmount(BigDecimal.ZERO);
		
		return gl;
	}

	private GeneralLedger closingBalance(BigDecimal closingAmount, Coa_05_Master coaMaster, Date closingDate) throws Exception {
		GeneralLedger gl = new GeneralLedger();
		gl.setDbcrDescription("Closing Balance ("+dateToStringDB(asLocalDate(closingDate), getShortDateFormat())+")");
		gl.setDebitAmount(BigDecimal.ZERO);
		gl.setCreditAmount(closingAmount);
		
		// check is this coaMaster and closingDate in the table?
		EndingBalance endingBalance = getEndingBalanceDao().findEndingBalanceByMasterCoa_EndingDate(coaMaster, closingDate);
		
		if (endingBalance == null) {
			// create a new endingBalance for this closingDate and save
			EndingBalance toDateEndBalance = new EndingBalance();
			toDateEndBalance.setMasterCoa(coaMaster);
			toDateEndBalance.setEndingDate(closingDate);
			toDateEndBalance.setEndingAmount(closingAmount);
			getEndingBalanceDao().save(toDateEndBalance);
		} else {
			// set the endingBalance amount and update 
			endingBalance.setEndingAmount(closingAmount);
			getEndingBalanceDao().update(endingBalance);
		}
		
		return gl;
	}	

	private GeneralLedger endingDebitCredit(BigDecimal totalDebitAmount, BigDecimal totalCreditAmount) {
		GeneralLedger gl = new GeneralLedger();
		gl.setDbcrDescription("");
		gl.setDebitAmount(totalDebitAmount);
		gl.setCreditAmount(totalCreditAmount);
		
		return gl;
	}
	
	private void displayGL(List<GeneralLedger> allGLList) {
		generalLedgerListbox.setModel(
				new ListModelList<GeneralLedger>(allGLList));
		generalLedgerListbox.setItemRenderer(getGeneralLedgerListitemRenderer());
		
	}

	private ListitemRenderer<GeneralLedger> getGeneralLedgerListitemRenderer() {

		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				// Voucher No.
				lc = new Listcell(gl.getVoucherNumber()==null ? " " : gl.getVoucherNumber().getSerialComp());
				lc.setParent(item);
				
				// Transaksi Info
				lc = new Listcell(gl.getDbcrDescription());
				lc.setParent(item);
				
				// Debit (Rp.)
				lc = new Listcell(gl.getDebitAmount()==null ? " " : toLocalFormat(gl.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit (Rp.)
				lc = new Listcell(gl.getCreditAmount()==null ? " " : toLocalFormat(gl.getCreditAmount()));
				lc.setParent(item);
				
			}
		};
	}

	public void onClick$printGeneralLedgerButton(Event event) throws Exception {
		Map<String, List<GeneralLedger>> arg = 
				Collections.singletonMap("allGLList", allGLList);
		
		Window reportGLPrintWin = 
				(Window) Executions.createComponents("/gl/print/GeneralLedgerPrint.zul", null, arg);

		reportGLPrintWin.doModal();
	}
	
	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
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

	public EndingBalanceDao getEndingBalanceDao() {
		return endingBalanceDao;
	}

	public void setEndingBalanceDao(EndingBalanceDao endingBalanceDao) {
		this.endingBalanceDao = endingBalanceDao;
	}
	
}
