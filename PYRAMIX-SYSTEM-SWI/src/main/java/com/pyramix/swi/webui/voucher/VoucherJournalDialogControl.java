package com.pyramix.swi.webui.voucher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherJournalDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherJournalDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1722418434305803777L;

	private Coa_05_MasterDao coa_05_MasterDao;
	private SerialNumberGenerator serialNumberGenerator;
	private VoucherJournalDao voucherJournalDao;
	
	private Window voucherJournalDialogWin;
	private Datebox transactionDatebox;
	private Textbox voucherNoCompTextbox, voucherNoPostTextbox, theSumOfTextbox,
		descriptionTextbox, referenceTextbox;
	private Label infoDebitCreditlabel; 
	private Combobox voucherTypeCombobox, voucherStatusCombobox;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private VoucherJournal voucherJournal;
	private BigDecimal totalVoucherValue, totalDebitVal, totalCreditVal;
	private List<VoucherJournalDebitCredit> voucherJournalDebitCreditList;
	
	private final VoucherStatus DEFAULT_FLOW_STATUS 		= VoucherStatus.Posted;	
	
	private final VoucherType GENERAL_VOUCHER 		= VoucherType.GENERAL;
	private final VoucherType PETTYCASH_VOUCHER 	= VoucherType.PETTYCASH;
	
	// for pettycash, credit to this account (using ID from the master coa table)
	private final long PETTYCASH_COA_ID		= new Long(4); 
	private final Logger log = Logger.getLogger(VoucherJournalDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setVoucherJournal(
				(VoucherJournal) arg.get("voucherJournal"));
	}

	public void onCreate$voucherJournalDialogWin(Event event) throws Exception {
		if (getVoucherJournal().getId().compareTo(Long.MIN_VALUE)==0) {
			voucherJournalDialogWin.setTitle("Membuat (Tambah) Voucher Umum dan PettyCash");			
		} else {
			voucherJournalDialogWin.setTitle("Merubah (Edit) Voucher Umum dan PettyCash");
		}

		// format datebox to locale and format
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());

		// voucher type combobox for 
		setupVoucherTypeCombobox();
				
		// load info
		setVoucherJournalInfo();		
	}

	private void setupVoucherTypeCombobox() {
		Comboitem comboitem;
		
		// GENERAL_VOUCHER
		comboitem = new Comboitem();
		comboitem.setLabel(GENERAL_VOUCHER.name());
		comboitem.setValue(GENERAL_VOUCHER);
		comboitem.setParent(voucherTypeCombobox);
		
		// PETTYCASH_VOUCHER
		comboitem = new Comboitem();
		comboitem.setLabel(PETTYCASH_VOUCHER.name());
		comboitem.setValue(PETTYCASH_VOUCHER);
		comboitem.setParent(voucherTypeCombobox);
		
	}

	private void setVoucherJournalInfo() {
		if (getVoucherJournal().getId().compareTo(Long.MIN_VALUE)==0) {
			voucherNoCompTextbox.setValue(null);
			voucherNoPostTextbox.setValue(null);
			transactionDatebox.setValue(asDate(getLocalDate()));
			voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
			// default to GENERAL VOUCHER
			voucherTypeCombobox.setSelectedIndex(0);
			theSumOfTextbox.setValue(toLocalFormatWithDecimal(BigDecimal.ZERO));
			setTotalVoucherValue(BigDecimal.ZERO);
			descriptionTextbox.setValue("");
			referenceTextbox.setValue("");			
		} else {
			voucherNoCompTextbox.setValue(getVoucherJournal().getVoucherNumber().getSerialComp());
			voucherNoPostTextbox.setValue(getVoucherJournal().getPostingVoucherNumber()==null ? 
					"" : getVoucherJournal().getPostingVoucherNumber().getSerialComp());
			transactionDatebox.setValue(getVoucherJournal().getTransactionDate());
			voucherStatusCombobox.setValue(getVoucherJournal().getFlowStatus().toString());
			for (Comboitem comboitem : voucherTypeCombobox.getItems()) {
				if (getVoucherJournal().getVoucherType().equals(comboitem.getValue())) {
					voucherTypeCombobox.setSelectedItem(comboitem);
				}
			}
			setTotalVoucherValue(getVoucherJournal().getTheSumOf());
			theSumOfTextbox.setValue(toLocalFormatWithDecimal(getTotalVoucherValue()));
			descriptionTextbox.setValue(getVoucherJournal().getTransactionDescription());
			referenceTextbox.setValue(getVoucherJournal().getDocumentRef());			
			
			// dbcr
			setVoucherJournalDebitCreditList(getVoucherJournal().getVoucherJournalDebitCredits());
			setVoucherJournalDebitCreditInfo();
		}
	}

	public void onClick$totalOrderButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getTotalVoucherValue().compareTo(BigDecimal.ZERO)==0 ?
						BigDecimal.ZERO : getTotalVoucherValue());
		
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// theSumOfTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				theSumOfTextbox.setValue(toLocalFormatWithDecimal((BigDecimal) event.getData()));
				setTotalVoucherValue((BigDecimal) event.getData());				
			}
		});
		
		totalOrderDialogWin.doModal();		
	}
	
	public void onClick$addDebitCreditButton(Event event) throws Exception {
		
		// clear the list
		// getVoucherJournalDebitCreditList().clear();
		
		if (getTotalVoucherValue().compareTo(BigDecimal.ZERO)==0) {
			throw new Exception("Belum isi jumlah.");
		}
		if (descriptionTextbox.getValue().isEmpty()) {
			throw new Exception("Belum isi penjelasan.");
		}

		VoucherJournalDebitCredit debitAccount;
		VoucherJournalDebitCredit creditAccount;
		
		// transaction description
		String transDescription = descriptionTextbox.getValue();
		
		
		if (getVoucherJournal().getId().compareTo(Long.MIN_VALUE)==0) {
			// init dbcrlist
			setVoucherJournalDebitCreditList(
					new ArrayList<VoucherJournalDebitCredit>());			
		} else {
			// remove previous dbcr
			getVoucherJournalDebitCreditList().clear();
		}
		
		// check the voucher type: GENERAL or PETTYCASH
		VoucherType selVoucherType = voucherTypeCombobox.getSelectedItem().getValue();
		if (selVoucherType.equals(GENERAL_VOUCHER)) {
			// setup debit account
			debitAccount = new VoucherJournalDebitCredit();
			debitAccount.setDebitAmount(getTotalVoucherValue());
			debitAccount.setCreditAmount(BigDecimal.ZERO);
			debitAccount.setDbcrDescription(transDescription);
			debitAccount.setMasterCoa(null);
			// setup credit account
			creditAccount = new VoucherJournalDebitCredit();
			creditAccount.setDebitAmount(BigDecimal.ZERO);
			creditAccount.setCreditAmount(getTotalVoucherValue());
			creditAccount.setDbcrDescription(transDescription);
			creditAccount.setMasterCoa(null);			
		} else {
			// setup credit account
			creditAccount = new VoucherJournalDebitCredit();
			creditAccount.setDebitAmount(BigDecimal.ZERO);
			creditAccount.setCreditAmount(getTotalVoucherValue());
			creditAccount.setDbcrDescription(transDescription);
			creditAccount.setMasterCoa(
					getCoa_05_MasterDao().findCoa_05_MasterById(PETTYCASH_COA_ID));
			// setup debit account
			debitAccount = new VoucherJournalDebitCredit();
			debitAccount.setDebitAmount(getTotalVoucherValue());
			debitAccount.setCreditAmount(BigDecimal.ZERO);
			debitAccount.setDbcrDescription(transDescription);
			debitAccount.setMasterCoa(null);
		}
		
		// add to the list
		getVoucherJournalDebitCreditList().add(debitAccount);
		getVoucherJournalDebitCreditList().add(creditAccount);

		// display
		setVoucherJournalDebitCreditInfo();
	}
	
	private void setVoucherJournalDebitCreditInfo() {
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherJournalDebitCredit>(
						getVoucherJournalDebitCreditList()));
		voucherDbcrListbox.setItemRenderer(
				getVoucherDbcrListitemRenderer());
	}

	private ListitemRenderer<VoucherJournalDebitCredit> getVoucherDbcrListitemRenderer() {
		
		totalDebitVal = BigDecimal.ZERO;
		totalCreditVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherJournalDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherJournalDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = initCoaNumber(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(voucherDbCr.getMasterCoa()==null ? "" : 
					voucherDbCr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(voucherDbCr.getDbcrDescription());
				lc.setParent(item);
				
				// Debit
				//lc = new Listcell(toLocalFormat(voucherDbCr.getDebitAmount()));
				lc = new Listcell(toLocalFormatWithDecimal(voucherDbCr.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit
				// lc = new Listcell(toLocalFormat(voucherDbCr.getCreditAmount()));
				lc = new Listcell(toLocalFormatWithDecimal(voucherDbCr.getCreditAmount()));
				lc.setParent(item);				
				
				totalDebitVal = totalDebitVal.add(voucherDbCr.getDebitAmount());
				totalCreditVal = totalCreditVal.add(voucherDbCr.getCreditAmount());
			}

			private Listcell initCoaNumber(Listcell listcell, VoucherJournalDebitCredit voucherDbCr, int index) {
				Textbox coaNumberTextbox = new Textbox();
				coaNumberTextbox.setValue(voucherDbCr.getMasterCoa()==null ? 
						"" : voucherDbCr.getMasterCoa().getMasterCoaComp());
				coaNumberTextbox.setWidth("120px");
				coaNumberTextbox.setReadonly(true);
				coaNumberTextbox.setAttribute("masterCoa", voucherDbCr.getMasterCoa());
				coaNumberTextbox.setParent(listcell);
				
				Button coaSelButton = new Button();
				coaSelButton.setLabel("...");
				coaSelButton.setClass("inventoryEditButton");
				coaSelButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Window masterCoaWin = (Window) Executions.createComponents(
								"/coa/Coa_05_MasterListDialog.zul", null, null);
						
						masterCoaWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
								
								// set coa number (No Akun)
								coaNumberTextbox.setValue(masterCoa.getMasterCoaComp());
								
								// set coa object
								coaNumberTextbox.setAttribute("masterCoa", masterCoa);
								
								// set coa name (Nama Akun)
								Listitem listitem = voucherDbcrListbox.getItemAtIndex(index);
								Listcell accNameListcell = (Listcell) listitem.getChildren().get(1);
								accNameListcell.setLabel(masterCoa.getMasterCoaName());
							}
						});
						
						masterCoaWin.doModal();
					}
				});
				coaSelButton.setDisabled(voucherDbCr.getMasterCoa()!=null);
				coaSelButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$voucherDbcrListbox(Event event) throws Exception {
		if (getVoucherJournalDebitCreditList()==null) {
			infoDebitCreditlabel.setValue("Debit/Kredit: 0 items");
			
			totalDebitListfooter.setLabel(toLocalFormatWithDecimal(BigDecimal.ZERO)); 
			totalCreditListfooter.setLabel(toLocalFormatWithDecimal(BigDecimal.ZERO));			
		} else {
			int dbCrCount = getVoucherJournalDebitCreditList().size();
			infoDebitCreditlabel.setValue("Debit/Kredit: "+dbCrCount+" items");
			
			totalDebitListfooter.setLabel(toLocalFormatWithDecimal(totalDebitVal)); 
			totalCreditListfooter.setLabel(toLocalFormatWithDecimal(totalCreditVal));
		}
		
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		if (getVoucherJournalDebitCreditList()==null) {			
			throw new Exception("Debit / Kredit Harus Dilengkapi sebelum Disimpan.");
		}
		if (getVoucherJournal().getId().compareTo(Long.MIN_VALUE)!=0) {
			
			// edit
			if (getTotalVoucherValue().compareTo(totalCreditVal)!=0 || 
					getTotalVoucherValue().compareTo(totalDebitVal)!=0) {
			
				// dbcr values not the same as the totalVoucher value
				throw new Exception("Total debit / credit tidak sesuai dengan nominal voucher.");
			}
			
		}
		
		VoucherJournal modVoucherJournal = getUserModVoucherJournal(getVoucherJournal());
		
		Events.sendEvent(Events.ON_OK, voucherJournalDialogWin, modVoucherJournal);
		
		voucherJournalDialogWin.detach();
	}
	
	private VoucherJournal getUserModVoucherJournal(VoucherJournal voucherJournal) throws Exception {
		
		Date defaultDate = transactionDatebox.getValue();
		VoucherType selVoucherType = voucherTypeCombobox.getSelectedItem().getValue();
		
		voucherJournal.setTheSumOf(getTotalVoucherValue());
		voucherJournal.setTransactionDate(defaultDate);
		voucherJournal.setTransactionDescription(descriptionTextbox.getValue());
		voucherJournal.setDocumentRef(referenceTextbox.getValue());
		voucherJournal.setFlowStatus(DEFAULT_FLOW_STATUS);
		voucherJournal.setVoucherType(selVoucherType);
		voucherJournal.setVoucherStatus(DocumentStatus.NORMAL);
		voucherJournal.setVoucherJournalDebitCredits(getUserUpdatedVoucherJournalDebitCreditList());
		voucherJournal.setAllowEdit(false);
		// posting immediately
		voucherJournal.setPostingDate(defaultDate);
		voucherJournal.setPostingVoucherNumber(
				addVoucherNumber(selVoucherType.compareTo(VoucherType.GENERAL)==0 ?
						VoucherType.POSTING_GENERAL : VoucherType.POSTING_PETTYCASH, asDate(getLocalDate())));
		if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			voucherJournal.setCreateDate(defaultDate);
			voucherJournal.setModifiedDate(defaultDate);
			voucherJournal.setCheckDate(defaultDate);			
			voucherJournal.setVoucherNumber(addVoucherNumber(selVoucherType, defaultDate));
			// create new GL
			voucherJournal.setGeneralLedgers(createGeneralLedgersFromVoucherJournal(voucherJournal));
		} else {
			// edit
			voucherJournal.setModifiedDate(asDate(getLocalDate()));
			voucherJournal.setCheckDate(asDate(getLocalDate()));
			// update existing GL
			voucherJournal.setGeneralLedgers(updateGeneralLedgersFromVoucherJournal(voucherJournal));
		}
		
		return voucherJournal;
	}

	private List<GeneralLedger> createGeneralLedgersFromVoucherJournal(VoucherJournal voucherJournal) {
		log.info("create GL from voucherjournal");
		// use the dbcr to create generalledger
		List<VoucherJournalDebitCredit> dbcrList = getVoucherJournalDebitCreditList();
		dbcrList.forEach(dbcr -> log.info(dbcr.toString()));
		// multipe generalledgers are created, so need a new list to pass back to the caller
		List<GeneralLedger> generalLedgerList = new ArrayList<GeneralLedger>();
		
		for (VoucherJournalDebitCredit dbcr : dbcrList) {
			GeneralLedger gl = new GeneralLedger();
			
			log.info(dbcr.getMasterCoa().toString());
			gl.setMasterCoa(dbcr.getMasterCoa());
			// 30/07/2021 - postingDate must be the same as the transactionDate
			// gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingDate(voucherJournal.getTransactionDate());
			gl.setPostingVoucherNumber(voucherJournal.getPostingVoucherNumber());
			gl.setCreditAmount(dbcr.getCreditAmount());
			gl.setDebitAmount(dbcr.getDebitAmount());
			gl.setDbcrDescription(dbcr.getDbcrDescription());
			gl.setTransactionDescription(voucherJournal.getTransactionDescription());
			gl.setDocumentRef(voucherJournal.getDocumentRef());
			gl.setTransactionDate(voucherJournal.getTransactionDate());
			gl.setVoucherType(voucherJournal.getVoucherType());
			gl.setVoucherNumber(voucherJournal.getVoucherNumber());
			
			// add
			generalLedgerList.add(gl);
		}
		
		return generalLedgerList;
	}

	private List<GeneralLedger> updateGeneralLedgersFromVoucherJournal(VoucherJournal voucherJournal) throws Exception {
		// use the dbcr to create generalledger
		List<VoucherJournalDebitCredit> dbcrList = getVoucherJournalDebitCreditList();
		// get existing gl - use proxy
		VoucherJournal voucherJournalGLByProxy =
				getVoucherJournalDao().findVoucherJournalGeneralLedgerByProxy(voucherJournal.getId());
		// clear the list
		voucherJournalGLByProxy.getGeneralLedgers().clear();
		// 
		for (VoucherJournalDebitCredit dbcr : dbcrList) {
			GeneralLedger gl = new GeneralLedger();
			
			gl.setMasterCoa(dbcr.getMasterCoa());
			// 30/07/2021 - postingDate must be the same as the transactionDate
			// gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingDate(voucherJournal.getTransactionDate());
			gl.setPostingVoucherNumber(voucherJournal.getPostingVoucherNumber());
			gl.setCreditAmount(dbcr.getCreditAmount());
			gl.setDebitAmount(dbcr.getDebitAmount());
			gl.setDbcrDescription(dbcr.getDbcrDescription());
			gl.setTransactionDescription(voucherJournal.getTransactionDescription());
			gl.setDocumentRef(voucherJournal.getDocumentRef());
			gl.setTransactionDate(voucherJournal.getTransactionDate());
			gl.setVoucherType(voucherJournal.getVoucherType());
			gl.setVoucherNumber(voucherJournal.getVoucherNumber());
			
			// add
			voucherJournalGLByProxy.getGeneralLedgers().add(gl);
		}
		log.info("Updated GL:");
		voucherJournalGLByProxy.getGeneralLedgers().forEach(gl -> log.info(gl.toString()));
		
		return voucherJournalGLByProxy.getGeneralLedgers();
	}
	
	private List<VoucherJournalDebitCredit> getUserUpdatedVoucherJournalDebitCreditList() {
		List<VoucherJournalDebitCredit> dbcrList = getVoucherJournalDebitCreditList();
		
		for (int i=0; i<dbcrList.size(); i++) {
			Listitem item = voucherDbcrListbox.getItemAtIndex(i);
			Textbox coaNumberTextbox = 
					(Textbox) item.getChildren().get(0).getFirstChild();
			Coa_05_Master masterCoa = 
					(Coa_05_Master) coaNumberTextbox.getAttribute("masterCoa");
			
			VoucherJournalDebitCredit dbcr = dbcrList.get(i);
			dbcr.setMasterCoa(masterCoa);
		}
		
		return dbcrList;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		voucherJournalDialogWin.detach();
	}
	
	private VoucherSerialNumber addVoucherNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerNum = new VoucherSerialNumber();
		voucherSerNum.setVoucherType(voucherType);
		voucherSerNum.setSerialDate(currentDate);
		voucherSerNum.setSerialNo(serialNum);
		voucherSerNum.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), currentDate, serialNum));
		
		return voucherSerNum;
	}
	
	
	public VoucherJournal getVoucherJournal() {
		return voucherJournal;
	}

	public void setVoucherJournal(VoucherJournal voucherJournal) {
		this.voucherJournal = voucherJournal;
	}

	public BigDecimal getTotalVoucherValue() {
		return totalVoucherValue;
	}

	public void setTotalVoucherValue(BigDecimal totalVoucherValue) {
		this.totalVoucherValue = totalVoucherValue;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public List<VoucherJournalDebitCredit> getVoucherJournalDebitCreditList() {
		return voucherJournalDebitCreditList;
	}

	public void setVoucherJournalDebitCreditList(List<VoucherJournalDebitCredit> voucherJournalDebitCreditList) {
		this.voucherJournalDebitCreditList = voucherJournalDebitCreditList;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public VoucherJournalDao getVoucherJournalDao() {
		return voucherJournalDao;
	}

	public void setVoucherJournalDao(VoucherJournalDao voucherJournalDao) {
		this.voucherJournalDao = voucherJournalDao;
	}
}
