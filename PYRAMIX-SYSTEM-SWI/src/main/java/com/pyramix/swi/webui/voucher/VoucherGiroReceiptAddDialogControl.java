package com.pyramix.swi.webui.voucher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.domain.coa.Coa_Receivables;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherGiroReceiptDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.coa.dao.Coa_AdjustmentDao;
import com.pyramix.swi.persistence.coa.dao.Coa_ReceivablesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherGiroReceiptAddDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4861830969427657305L;

	private Coa_05_MasterDao coa_05_MasterDao;
	private Coa_AdjustmentDao coa_AdjustmentDao;
	private SerialNumberGenerator serialNumberGenerator;
	private Coa_ReceivablesDao coa_ReceivablesDao;
	
	private Window voucherGiroReceiptAddDialogWin;
	private Datebox transactionDatebox, giroDateDatebox;
	private Textbox voucherNoCompTextbox, voucherNoPostTextbox, customerTextbox, noGiroTextbox, 
		giroBankTextbox, theSumOfTextbox, descriptionTextbox, referenceTextbox;
	private Label voucherTypeLabel;
	private Combobox voucherStatusCombobox;
	private Listbox voucherDbcrListbox;
	private Label infoDebitCreditlabel;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private VoucherGiroReceipt voucherGiroReceipt;
	private List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCreditList;
	private BigDecimal totalOrderValue, adjustmentAmount, totalDebitVal, totalCreditVal;
	
	private final VoucherType 	RECEIPT_GIRO_VOUCHER 		= VoucherType.RECEIPT_GIRO;
	private final VoucherStatus DEFAULT_FLOW_STATUS 		= VoucherStatus.Posted;	
	private final Long 			DEBIT_ACCOUNT				= new Long(45);
	private final Long 			CREDIT_ACCOUNT				= new Long(44);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setVoucherGiroReceipt(
				(VoucherGiroReceipt) arg.get("voucherGiroReceipt"));
	}
	
	public void onCreate$voucherGiroReceiptAddDialogWin(Event event) throws Exception {
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
		giroDateDatebox.setLocale(getLocale());
		giroDateDatebox.setFormat(getLongDateFormat());
		
		voucherGiroReceiptAddDialogWin.setTitle("Membuat (Tambah) Voucher Giro Receipt");

		// load info
		setVoucherGiroReceiptInfo();
	}
	
	private void setVoucherGiroReceiptInfo() {
		voucherNoCompTextbox.setValue(null);
		voucherNoPostTextbox.setValue(null);
		transactionDatebox.setValue(asDate(getLocalDate()));
		voucherTypeLabel.setValue(RECEIPT_GIRO_VOUCHER.toString());
		voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
		customerTextbox.setValue("");
		customerTextbox.setAttribute("customer", null);
		noGiroTextbox.setValue("");
		giroBankTextbox.setValue("");
		giroDateDatebox.setValue(null);
		theSumOfTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
		setTotalOrderValue(BigDecimal.ZERO);
		descriptionTextbox.setValue("");
		referenceTextbox.setValue("");
		
		// just for completion
		setAdjustmentAmount(BigDecimal.ZERO);
	}

	public void onClick$customerButton(Event event) throws Exception {
 		Window selectCustomerWin = 
				(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);
		
		selectCustomerWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Customer selCustomer = (Customer) event.getData();
				
				customerTextbox.setValue(selCustomer.getCompanyType()+". "+
						selCustomer.getCompanyLegalName());
				customerTextbox.setAttribute("customer", selCustomer);
			}
		});
		
		selectCustomerWin.doModal();
		
	}
	
	public void onClick$totalOrderButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getTotalOrderValue().compareTo(BigDecimal.ZERO)==0 ?
						BigDecimal.ZERO : getTotalOrderValue());
		
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				theSumOfTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalOrderValue((BigDecimal) event.getData());				
			}
		});
		
		totalOrderDialogWin.doModal();		
	}

	public void onClick$addDebitCreditButton(Event event) throws Exception {
		if (noGiroTextbox.getValue().isEmpty()) {
			throw new Exception("No Giro Belum diisi.");
		}
		
		if (giroBankTextbox.getValue().isEmpty()) {
			throw new Exception("Informasi Bank Belum diisi.");
		}
		
		if (giroDateDatebox.getValue()==null) {
			throw new Exception("Tanggal Giro Belum diisi.");
		}
		
		if (customerTextbox.getAttribute("customer")==null) {
			throw new Exception("Customer belum dipilih.");
		}

		if (getTotalOrderValue().compareTo(BigDecimal.ZERO)==0) {
			throw new Exception("Jumlah belum diisi.");
		}
		
		String transDescrip = "Penerimaan Giro dari "+customerTextbox.getValue();
		String transRef = "Giro No."+noGiroTextbox.getValue()+
				" "+giroBankTextbox.getValue()+
				"Jatuh Tempo "+
					dateToStringDisplay(asLocalDate(giroDateDatebox.getValue()), getLongDateFormat());
		
		descriptionTextbox.setValue(transDescrip);
		referenceTextbox.setValue(transRef);

		// * DB	Giro Ditangan (1.212.0001) - OK
		VoucherGiroReceiptDebitCredit debitAccount = new VoucherGiroReceiptDebitCredit();
		debitAccount.setDebitAmount(getTotalOrderValue());
		debitAccount.setCreditAmount(BigDecimal.ZERO);
		debitAccount.setDbcrDescription(transDescrip);
		debitAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(DEBIT_ACCOUNT));
		
		// * CR	Piutang Langganan (COA 1.241.003 ) - seharusny bisa diganti2 dgn:
		//		- Piutang Langganan
		//		- Piutang Sunter
		//		- Piutang Karawang
		VoucherGiroReceiptDebitCredit creditAccount = new VoucherGiroReceiptDebitCredit();
		creditAccount.setDebitAmount(BigDecimal.ZERO);
		creditAccount.setCreditAmount(getTotalOrderValue());
		creditAccount.setDbcrDescription("Pembayaran Piutang "+customerTextbox.getValue());
		creditAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(CREDIT_ACCOUNT));

		// adjustment account, in case needed
		VoucherGiroReceiptDebitCredit adjustmentAccount = null;
		
		if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==-1) {
			// Skenario #2 - posisi minus
			// - posisi  Debet - COA:
			// Disc penjualan 	:   41110003 - id=47 - default
			// Retur Penjualan	:   41110004

			// System.out.println("posisi minus: "+getAdjustmentAmount());
			
			adjustmentAccount = new VoucherGiroReceiptDebitCredit();
			adjustmentAccount.setDebitAmount(getAdjustmentAmount().multiply(new BigDecimal(-1)));
			// Adjust the debit account
			// -- should be substract, adjustmentAmount is negative, so just add
			debitAccount.setDebitAmount(getTotalOrderValue().add(getAdjustmentAmount()));
			adjustmentAccount.setCreditAmount(BigDecimal.ZERO);
			adjustmentAccount.setDbcrDescription("Adjustment");
			adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(47)));
			
		} else if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==0) {
			// - posting total pembayaran - TANPA adjustment
			
		} else {
			// Skenario #3 - posisi plus
			// - posisi Kredit
			// Pembayaran dimuka	:   44110002
			// Pendapatan dll		:   44110001 - id=49 - default
			// Meterai				:   52410004
						
			adjustmentAccount = new VoucherGiroReceiptDebitCredit();
			adjustmentAccount.setCreditAmount(getAdjustmentAmount());
			// Adjust the credit account
			creditAccount.setCreditAmount(getTotalOrderValue().subtract(getAdjustmentAmount()));
			adjustmentAccount.setDebitAmount(BigDecimal.ZERO);
			adjustmentAccount.setDbcrDescription("Adjustment");
			adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(49)));
		}		
				
		// list
		setVoucherGiroReceiptDebitCreditList(new ArrayList<VoucherGiroReceiptDebitCredit>());
		
		// add
		getVoucherGiroReceiptDebitCreditList().add(debitAccount);
		getVoucherGiroReceiptDebitCreditList().add(creditAccount);
		if (adjustmentAccount!=null) {
			getVoucherGiroReceiptDebitCreditList().add(adjustmentAccount);			
		}
		// display
		setVoucherGiroReceiptDebitCreditInfo(
				getVoucherGiroReceiptDebitCreditList());
	
		 
	}
	
	private void setVoucherGiroReceiptDebitCreditInfo(
			List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDbCrList) {
		
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherGiroReceiptDebitCredit>(voucherGiroReceiptDbCrList));
		voucherDbcrListbox.setItemRenderer(
				getVoucherGiroReceiptDebitCreditListitemRenderer());		
	}

	private ListitemRenderer<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDebitCreditListitemRenderer() {

		totalDebitVal = BigDecimal.ZERO; 
		totalCreditVal = BigDecimal.ZERO;

		return new ListitemRenderer<VoucherGiroReceiptDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherGiroReceiptDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = initCoaNumber(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);				
				
				// Keterangan
				lc = initDescription(new Listcell(), voucherDbCr.getDbcrDescription(), index);
						// new Listcell(voucherDbCr.getDbcrDescription());
				lc.setParent(item);
				
				// Debit
				lc = new Listcell(toLocalFormat(voucherDbCr.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit
				lc = new Listcell(toLocalFormat(voucherDbCr.getCreditAmount()));
				lc.setParent(item);
				
				totalDebitVal = totalDebitVal.add(voucherDbCr.getDebitAmount()); 
				totalCreditVal = totalCreditVal.add(voucherDbCr.getCreditAmount());

				
			}

			private Listcell initCoaNumber(Listcell listcell, VoucherGiroReceiptDebitCredit voucherDbCr, int index) throws Exception {
				Combobox coaNumberCombobox = new Combobox();
				Comboitem comboitem;
				if (index==0) {
					coaNumberCombobox.setValue(voucherDbCr.getMasterCoa().getMasterCoaComp());
					coaNumberCombobox.setWidth("165px");
					// coaNumberCombobox.setDisabled((index<2) || (getPageMode().compareTo(PageMode.VIEW) == 0));
					coaNumberCombobox.setParent(listcell);					
				}
				if (index==1) {
					// * 19/05/2020 - by Email (from Lena)
					// * CR	Piutang Langganan (COA 1.241.003 ) - seharusny bisa diganti2 dgn:
					//		- Piutang Langganan
					//		- Piutang Sunter
					//		- Piutang Karawang
					
					// find all COA Piutang
					List<Coa_Receivables> coaReceivablesList = getCoa_ReceivablesDao().findAllActiveCoaReceivables();
					for (Coa_Receivables coa_Receivables : coaReceivablesList) {
						comboitem = new Comboitem();
						comboitem.setLabel(coa_Receivables.getMasterCoa().getMasterCoaComp());
						comboitem.setValue(coa_Receivables);
						comboitem.setParent(coaNumberCombobox);						
					}
					// event
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Combobox combobox = (Combobox) event.getTarget();
							Comboitem comboitem = combobox.getSelectedItem();
							Coa_Receivables coa_Receivables = comboitem.getValue();
							
							Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
							Listcell lc = (Listcell) item.getChildren().get(1);
							lc.setLabel(coa_Receivables.getMasterCoa().getMasterCoaName());
						}
					});
					
					coaNumberCombobox.setSelectedItem(coaNumberCombobox.getItemAtIndex(0));
					coaNumberCombobox.setWidth("165px");
					coaNumberCombobox.setParent(listcell);					
				}				
				if (index>1) {
					// adjustment account ONLY
					if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
						// DEBIT accounts
						List<Coa_Adjustment> coaDebitAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(true);
						for (Coa_Adjustment coa_Adjustment : coaDebitAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment);
							comboitem.setParent(coaNumberCombobox);
						}
					} else {
						// CREDIT accounts
						List<Coa_Adjustment> coaCreditAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(false);
						for (Coa_Adjustment coa_Adjustment : coaCreditAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment);
							comboitem.setParent(coaNumberCombobox);
						}
					}
					// event
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Combobox combobox = (Combobox) event.getTarget();
							Comboitem comboitem = combobox.getSelectedItem();
							Coa_Adjustment coa_Adjustment = comboitem.getValue();
							
							Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
							Listcell lc = (Listcell) item.getChildren().get(1);
							lc.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaName());
						}
					});
					
					// coaNumberCombobox.setValue(voucherDbCr.getMasterCoa().getMasterCoaComp());
					coaNumberCombobox.setSelectedItem(coaNumberCombobox.getItemAtIndex(0));
					coaNumberCombobox.setWidth("165px");
					// coaNumberCombobox.setDisabled((index<2) || (getPageMode().compareTo(PageMode.VIEW) == 0));
					coaNumberCombobox.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initDescription(Listcell listcell, String dbcrDescription, int index) {
				if (index>1) {
					Textbox descripTextbox = new Textbox();
					
					descripTextbox.setWidth("310px");
					descripTextbox.setValue(dbcrDescription);
					// descripTextbox.setDisabled(getPageMode().compareTo(PageMode.VIEW) == 0);
					descripTextbox.setParent(listcell);					
				} else {
					listcell.setLabel(dbcrDescription);
				}
				
				return listcell;
			}
		};
	}

	public void onAfterRender$voucherDbcrListbox(Event event) throws Exception {
		if (getVoucherGiroReceiptDebitCreditList()==null) {
			infoDebitCreditlabel.setValue("Debit/Kredit: 0 items");
			
			totalDebitListfooter.setLabel(toLocalFormat(BigDecimal.ZERO)); 
			totalCreditListfooter.setLabel(toLocalFormat(BigDecimal.ZERO));			
		} else {
			int dbcrCount = getVoucherGiroReceiptDebitCreditList().size();
			infoDebitCreditlabel.setValue("Debit/Kredit: "+dbcrCount+" items");
			
			totalDebitListfooter.setLabel(toLocalFormat(totalDebitVal)); 
			totalCreditListfooter.setLabel(toLocalFormat(totalCreditVal));
		}
	}

	public void onClick$saveButton(Event event) throws Exception {
		if (getVoucherGiroReceiptDebitCreditList()==null) {
			throw new Exception("Debit / Kredit Harus Dilengkapi sebelum Disimpan.");
		}

		VoucherGiroReceipt receipt = getUserUpdateVoucherGiroReceipt(
				getVoucherGiroReceipt());

		// send a new receipt back
		Events.sendEvent(Events.ON_OK, voucherGiroReceiptAddDialogWin, receipt);
		
		voucherGiroReceiptAddDialogWin.detach();
	}
	
	private VoucherGiroReceipt getUserUpdateVoucherGiroReceipt(VoucherGiroReceipt receipt) throws Exception {
		Date defaultDate = transactionDatebox.getValue();

		receipt.setTheSumOf(getTotalOrderValue());
		receipt.setTransactionDate(defaultDate);
		receipt.setTransactionDescription(descriptionTextbox.getValue());
		receipt.setDocumentRef(referenceTextbox.getValue());
		receipt.setCreateDate(defaultDate);
		receipt.setModifiedDate(asDate(getLocalDate()));
		receipt.setCheckDate(defaultDate);
		receipt.setFlowStatus(DEFAULT_FLOW_STATUS);
		receipt.setVoucherType(RECEIPT_GIRO_VOUCHER);
		receipt.setPostingDate(defaultDate);
		
		receipt.setGiroNumber(noGiroTextbox.getValue());
		receipt.setGiroBank(giroBankTextbox.getValue());
		receipt.setGiroDate(giroDateDatebox.getValue());
		// receipt.setNote(); <-- not avail in the class
		receipt.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		receipt.setVoucherStatus(DocumentStatus.NORMAL);
		receipt.setVoucherGiroReceiptDebitCredits(getUserUpdateDebitCredit());

		if (receipt.getId().compareTo(Long.MIN_VALUE)==0) {
			// create the giro
			receipt.setGiro(getGiroInfo(new Giro()));			
			// create the link for giro - voucherGiroReceipt
			receipt.getGiro().setVoucherGiroReceipt(receipt);
			// create the number
			receipt.setVoucherNumber(addVoucherNumber(RECEIPT_GIRO_VOUCHER, defaultDate));
			
			// post directly
			receipt.setPostingDate(asDate(getLocalDate()));
			receipt.setPostingVoucherNumber(addVoucherNumber(VoucherType.POSTING_RECEIPTGIRO, asDate(getLocalDate())));
			receipt.setGeneralLedgers(createGeneralLedgersFromVoucherGiroReceipt(receipt));
		} else {
			// receipt.setGiro(getGiroInfo(getGiro()));
		}

		return receipt;
	}

	private List<GeneralLedger> createGeneralLedgersFromVoucherGiroReceipt(VoucherGiroReceipt receipt) {
		// use the dbcr to create generalledger		
		List<VoucherGiroReceiptDebitCredit> dbcrList = getVoucherGiroReceiptDebitCreditList();
		// multipe generalledgers are created, so need a new list to pass back to the caller
		List<GeneralLedger> generalLedgerList = new ArrayList<GeneralLedger>();

		for (VoucherGiroReceiptDebitCredit dbcr : dbcrList) {
			GeneralLedger gl = new GeneralLedger();
			
			gl.setMasterCoa(dbcr.getMasterCoa());
			gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingVoucherNumber(receipt.getPostingVoucherNumber());
			gl.setCreditAmount(dbcr.getCreditAmount());
			gl.setDebitAmount(dbcr.getDebitAmount());
			gl.setDbcrDescription(dbcr.getDbcrDescription());
			gl.setTransactionDescription(receipt.getTransactionDescription());
			gl.setDocumentRef(receipt.getDocumentRef());
			gl.setTransactionDate(receipt.getTransactionDate());
			gl.setVoucherType(receipt.getVoucherType());
			gl.setVoucherNumber(receipt.getVoucherNumber());

			// add
			generalLedgerList.add(gl);
		}
		
		return generalLedgerList;
	}	
	
	private Giro getGiroInfo(Giro giro) {
		giro.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		giro.setGiroAmount(getTotalOrderValue());
		giro.setGiroBank(giroBankTextbox.getValue());
		giro.setGiroDate(giroDateDatebox.getValue());
		giro.setGiroNumber(noGiroTextbox.getValue());
		giro.setGiroReceivedDate(transactionDatebox.getValue());
		giro.setGiroStatus(DocumentStatus.NORMAL);
		giro.setPaid(false);
		
		return giro;
	}

	private VoucherSerialNumber addVoucherNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber();
		voucherSerialNumber.setVoucherType(voucherType);
		voucherSerialNumber.setSerialDate(currentDate);
		voucherSerialNumber.setSerialNo(serialNum);
		voucherSerialNumber.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), 
				currentDate, serialNum));
		
		return voucherSerialNumber;
	}

	private List<VoucherGiroReceiptDebitCredit> getUserUpdateDebitCredit() {
		List<VoucherGiroReceiptDebitCredit> dbcrList = getVoucherGiroReceiptDebitCreditList();
		
		for (int i = 0; i < dbcrList.size(); i++) {
			
			if (i>1) {
				Listitem item = voucherDbcrListbox.getItemAtIndex(i);
				Combobox combobox = (Combobox) item.getChildren().get(0).getFirstChild();
				Coa_Adjustment coa_Adjustment = combobox.getSelectedItem().getValue();
				
				Textbox textbox = (Textbox) item.getChildren().get(2).getFirstChild();
				
				VoucherGiroReceiptDebitCredit receiptDbCr = dbcrList.get(i);
				receiptDbCr.setMasterCoa(coa_Adjustment.getMasterCoa());
				receiptDbCr.setDbcrDescription(textbox.getValue());
			}
			
		}
		
		return dbcrList;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		voucherGiroReceiptAddDialogWin.detach();
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}

	public BigDecimal getTotalOrderValue() {
		return totalOrderValue;
	}

	public void setTotalOrderValue(BigDecimal totalOrderValue) {
		this.totalOrderValue = totalOrderValue;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public List<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDebitCreditList() {
		return voucherGiroReceiptDebitCreditList;
	}

	public void setVoucherGiroReceiptDebitCreditList(List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCreditList) {
		this.voucherGiroReceiptDebitCreditList = voucherGiroReceiptDebitCreditList;
	}

	public Coa_AdjustmentDao getCoa_AdjustmentDao() {
		return coa_AdjustmentDao;
	}

	public void setCoa_AdjustmentDao(Coa_AdjustmentDao coa_AdjustmentDao) {
		this.coa_AdjustmentDao = coa_AdjustmentDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public Coa_ReceivablesDao getCoa_ReceivablesDao() {
		return coa_ReceivablesDao;
	}

	public void setCoa_ReceivablesDao(Coa_ReceivablesDao coa_ReceivablesDao) {
		this.coa_ReceivablesDao = coa_ReceivablesDao;
	}
}
