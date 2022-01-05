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
import org.zkoss.zul.Grid;
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
import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherPaymentDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.coa.dao.Coa_AdjustmentDao;
import com.pyramix.swi.persistence.coa.dao.Coa_VoucherDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherPaymentDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240407835632613835L;

	private VoucherPaymentDao voucherPaymentDao;
	private Coa_05_MasterDao coa_05_MasterDao;
	private SerialNumberGenerator serialNumberGenerator;
	private SettlementDao settlementDao;
	private Coa_VoucherDao coa_VoucherDao;
	private GiroDao giroDao;
	private Coa_AdjustmentDao coa_AdjustmentDao;
	
	private Window voucherPaymentDialogWin;	
	private PageMode pageMode;
	private String requestPath;
	private Button voucherGiroReceiptSelectButton, saveButton, cancelButton, 
		createDebitCreditButton, checkButton;
	private Grid createFromGrid;
	private Textbox voucherNoCompTextbox, voucherNoPostTextbox, referenceNumberTextbox, paidByInfoTextbox, 
		theSumOfTextbox, customerTextbox, descriptionTextbox, referenceTextbox;
	private Datebox transactionDatebox;
	private Combobox paidByCombobox, voucherStatusCombobox;
	private Label voucherGiroReceiptLabel, paidByLabel, voucherTypeLabel, infoDbcrlabel;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private VoucherPaymentData paymentData;
	private VoucherPayment voucherPayment;
	private BigDecimal totalOrderValue, adjustmentAmount;
	// private List<VoucherPaymentDebitCredit> voucherPaymentDebitCreditList;
	private ListModelList<VoucherPaymentDebitCredit> debitCreditJournalList;
	
	// NOTE: VoucherGiroReceipt not posting for VoucherPayment
	// -- it's done by Giro [April 2018]
	// private final String VOUCHER_GIRO_RECEIPT_PATH		= "/voucher/VoucherGiroReceiptListInfo.zul";
	
	private final String SETTLEMENT_PATH				= "/settlement/SettlementListInfo.zul";
	private final String GIRO_PATH						= "/giro/GiroListInfo.zul";
	private final VoucherStatus DEFAULT_FLOW_STATUS 	= VoucherStatus.Posted;
	
	private final Logger log = Logger.getLogger(VoucherPaymentDialogControl.class);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setPaymentData(
				(VoucherPaymentData) arg.get("voucherPaymentData"));
		
		setRequestPath(
				Executions.getCurrent().getDesktop().getRequestPath());
	}

	public void onCreate$voucherPaymentDialogWin(Event event) throws Exception {
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
		
		setPageMode(
			getPaymentData().getPageMode());

		createDebitCreditButton.setDisabled(getPageMode().compareTo(PageMode.NEW)!=0); 
		
		switch (getPageMode()) {
			case EDIT:
				voucherPaymentDialogWin.setTitle("Merubah (Edit) Voucher Payment / Pembayaran");
				break;
			case NEW:
				voucherPaymentDialogWin.setTitle("Membuat (Tambah) Voucher Payment / Pembayaran");
				break;
			case VIEW:
				voucherPaymentDialogWin.setTitle("Melihat (View) Voucher Payment / Pembayaran");
				break;
			default:
				break;
		}
		
		// paymentType enum
		setupPaymentTypeCombobox();
		
		// voucherStatus enum
		setVoucherStatusCombobox();
		
/*		NO DIRECT VIEW FROM VoucherSales
 * 		-- this was meant for the 'tunai' VoucherSales
 * 		-- 'tunai' VoucherSales do not need VoucherPayment
 * 		-- [Thu 18/01/2018 08:41] Kalo pembelian tunai , tidak perlu voucher pembayaran
 * 
 * 		if (getRequestPath().matches(VOUCHER_SALES_PATH)) {
			// view ONLY
			createFromGrid.setVisible(false);

			setVoucherPayment(
					getPaymentData().getVoucherPayment());
			
		} 
*/		
		
		if (getRequestPath().matches(SETTLEMENT_PATH)) {
			// view and new
			createFromGrid.setVisible(
					getPageMode().compareTo(PageMode.NEW)==0 ? true : false);
			
			voucherGiroReceiptLabel.setValue(
					"Membuat Voucher Pembayaran Dari Settlement No: ");

			referenceNumberTextbox.setValue(
					getPageMode().compareTo(PageMode.NEW)==0 ?
					getPaymentData().getSettlement().getSettlementNumber().getSerialComp() : "");
			
			// no need to select -- put VoucherGiroReceipt info into the textbox
			voucherGiroReceiptSelectButton.setVisible(false);

			// Remove Giro from the combobox
			// -- user DO NOT select giro as the payment type
			// -- to use giro user choose Voucher Giro Receipt
			for (Comboitem item : paidByCombobox.getItems()) {
				if (item.getValue().equals(PaymentType.giro)) {
					paidByCombobox.removeItemAt(item.getIndex());
				}
			}
			
			// Defaulted to payment by Bank
			paidByLabel.setVisible(true);
			paidByLabel.setValue("Bank Info");

			// paidByInfoTextbox - refer to payment by Bank
			paidByInfoTextbox.setVisible(true);
			
			if (getPageMode().compareTo(PageMode.NEW)==0) {
				paidByInfoTextbox.setValue("");
				paidByInfoTextbox.setPlaceholder("Isi nama Bank dan proses transfer lainnya");				
			} else {
				paidByInfoTextbox.setValue(getPaymentData().getVoucherPayment().getPaidByNote());
			}
			
			setVoucherPayment(getPageMode().compareTo(PageMode.NEW)==0 ?
				settlementToVoucherPayment(new VoucherPayment(), getPaymentData().getSettlement()) :
					getPaymentData().getVoucherPayment());
			
		} else if (getRequestPath().matches(GIRO_PATH)) {
			// view and new
			createFromGrid.setVisible(
					getPageMode().compareTo(PageMode.NEW)==0 ? true : false);
			
			voucherGiroReceiptLabel.setValue(
					"Membuat Voucher Pembayaran Dari Giro No: ");

			referenceNumberTextbox.setValue(
					getPaymentData().getGiro().getGiroNumber()+" - "+
					getPaymentData().getGiro().getGiroBank());

			// no need to select
			voucherGiroReceiptSelectButton.setDisabled(true);

			// -- defaulted to Giro ONLY
			paidByCombobox.setSelectedIndex(1);
			paidByCombobox.setDisabled(true);
			
			// Defaulted to payment by Bank
			paidByLabel.setVisible(false);
			paidByLabel.setValue("");

			paidByInfoTextbox.setVisible(false);
			paidByInfoTextbox.setValue("");
			paidByInfoTextbox.setPlaceholder("");

			setVoucherPayment(getPageMode().compareTo(PageMode.NEW)==0 ?
					giroToVoucherPayment(new VoucherPayment(), getPaymentData().getGiro()) :
						getPaymentData().getVoucherPayment());

		} else {
			// from VoucherPaymentListInfo
			// -- view ONLY
			createFromGrid.setVisible(false);
			
			paidByLabel.setVisible(true);
			paidByLabel.setValue("Bank Info");

			setVoucherPayment(getPaymentData().getVoucherPayment());
		}
		
		setReadOnly();
		
		// load info
		setVoucherPaymentInfo();
	}

	private void setupPaymentTypeCombobox() {
		Comboitem item;
		
		for (PaymentType type : PaymentType.values()) {
			item = new Comboitem();
			item.setLabel(type.toString());
			item.setValue(type);
			item.setParent(paidByCombobox);
		}
	}

	private void setVoucherStatusCombobox() {
		Comboitem item;
		
		for (VoucherStatus status : VoucherStatus.values()) {
			item = new Comboitem();
			item.setLabel(status.toString());
			item.setValue(status);
			item.setParent(voucherStatusCombobox);
		}
	}

	public void onSelect$paidByCombobox(Event event) throws Exception {
		PaymentType type = paidByCombobox.getSelectedItem().getValue();
		
		switch (type) {
		case bank:
			paidByLabel.setVisible(true);
			paidByLabel.setValue("Bank Info");
			
			voucherTypeLabel.setValue(VoucherType.PAYMENT_BANK.toString());
			voucherTypeLabel.setAttribute("voucherType", VoucherType.PAYMENT_BANK);
			
			paidByInfoTextbox.setVisible(true);
			paidByInfoTextbox.setValue("");
			paidByInfoTextbox.setPlaceholder("Isi nama Bank dan proses transfer lainnya");
			break;
		case giro:
			paidByLabel.setVisible(true);
			paidByLabel.setValue("Giro Info");

			voucherTypeLabel.setValue(VoucherType.PAYMENT_GIRO.toString());
			voucherTypeLabel.setAttribute("voucherType", VoucherType.PAYMENT_GIRO);
			
			paidByInfoTextbox.setVisible(true);
			break;
		case tunai:
			paidByLabel.setVisible(false);
			
			voucherTypeLabel.setValue(VoucherType.PAYMENT_CASH.toString());
			voucherTypeLabel.setAttribute("voucherType", VoucherType.PAYMENT_CASH);
			
			paidByInfoTextbox.setVisible(false);
			break;
		default:
			break;
		}
	}

	private VoucherPayment settlementToVoucherPayment(VoucherPayment payment, Settlement settlement) throws Exception {
		payment.setCreateDate(asDate(getLocalDate()));
		// since it's from Settlement -- paidBy is ONLY Bank or Cash -- we defaulted it to Bank
		payment.setPaidBy(PaymentType.bank);
		payment.setPaidByNote("");
		// since it's from Settlement -- voucherType could be VoucherType.PAYMENT_BANK or VoucherType.PAYMENT_CASH
		// -- we defaulted it to Bank
		payment.setVoucherType(VoucherType.PAYMENT_BANK);
		payment.setFlowStatus(DEFAULT_FLOW_STATUS);
		// since it's from Settlement -- has customer (non direct payment / tunai)
		Customer customer = getCustomerFromSettlementByProxy(settlement.getId());
		payment.setCustomer(customer);
		payment.setTheSumOf(settlement.getAmountPaid());
		setTotalOrderValue(settlement.getAmountPaid());
		
		// adjustment -- for debit/credit account
		setAdjustmentAmount(settlement.getPostingAmount());

		String customerName = customer.getCompanyType()+". "+customer.getCompanyLegalName();
		
		payment.setTransactionDescription("Pembayaran dari "+customerName);
				// settlement.getSettlementDescription());
		payment.setDocumentRef("Settlement No:"+
				settlement.getSettlementNumber().getSerialComp()+" dari "+
				customerName);
		
		// since it's from Settlement -- has NO giro
		payment.setGiro(null);
		
		// link back
		payment.setSettlement(settlement);
		
		// user MUST create the Debit/Credit
		payment.setVoucherPaymentDebitCredits(null);
		
		return payment;
	}	

	/**
	 * Call to giroToVoucherPayment came from GiroListInfo.zul.  When the giro is cleared in the bank,
	 * user creates a VoucherPayment (using giro) to settle Giro on hand (giro di tangan) with the 
	 * bank.
	 * 
	 * @param payment
	 * @param giro
	 * @return
	 * @throws Exception
	 */
	private VoucherPayment giroToVoucherPayment(VoucherPayment payment, Giro giro) throws Exception {
		payment.setCreateDate(asDate(getLocalDate()));
		// since it's from Giro -- paidBy is defaulted to PaymentType.giro
		payment.setPaidBy(PaymentType.giro);
		payment.setPaidByNote("Pencairan Giro No:"+giro.getGiroNumber()+
			", Bank:"+giro.getGiroBank()+
			", Tgl:"+dateToStringDisplay(asLocalDate(giro.getGiroDate()), getShortDateFormat()));
		// since it's from Giro -- voucherType is defaulted to VoucherType.PAYMENT_GIRO
		payment.setVoucherType(VoucherType.PAYMENT_GIRO);
		payment.setFlowStatus(DEFAULT_FLOW_STATUS);
		// since it's from Giro -- has customer (non-tunai)
		Customer customer = getCustomerFromGiroByProxy(giro.getId());
		payment.setCustomer(customer);
		payment.setTheSumOf(giro.getGiroAmount());
		setTotalOrderValue(giro.getGiroAmount());
		
		String customerName = customer.getCompanyType()+". "+customer.getCompanyLegalName();
		
		payment.setTransactionDescription("Pencairan Giro "+customerName+" - "+
				giro.getGiroNumber()+" dari Bank "+giro.getGiroBank());
		payment.setDocumentRef("Giro No:"+giro.getGiroNumber()+
				" dari "+customerName);
		
		// link back
		payment.setGiro(giro);

		// user MUST create the Debit/Credit
		payment.setVoucherPaymentDebitCredits(null);
		
		return payment;
	}
	
	private void setVoucherPaymentInfo() throws Exception {
		voucherNoCompTextbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ? "" :
			getVoucherPayment().getVoucherNumber().getSerialComp());
		
		VoucherSerialNumber postingVoucherSerialNumber = getPageMode().compareTo(PageMode.NEW)==0 ? null :
			getPostingVoucherNumberByProxy(getVoucherPayment().getId());
		
		voucherNoPostTextbox.setValue(postingVoucherSerialNumber==null ? "" :
			postingVoucherSerialNumber.getSerialComp());
		
		transactionDatebox.setValue(getVoucherPayment().getCreateDate());
		transactionDatebox.setDisabled(getPageMode().compareTo(PageMode.NEW) != 0);
		// setSelected paidByCombobox
		for (Comboitem item : paidByCombobox.getItems()) {
			if (getVoucherPayment().getPaidBy().equals(item.getValue())) {
				paidByCombobox.setSelectedItem(item);
			}
		}
		// paidBy: tunai - hide the paidByInfo
		if (paidByCombobox.getSelectedItem().getValue().equals(PaymentType.tunai)) {
			paidByLabel.setVisible(false);
			paidByInfoTextbox.setVisible(false);
		}
		// voucherType - use the attribute to hold the enum type
		voucherTypeLabel.setValue(getVoucherPayment().getVoucherType()==null ? null : 
			getVoucherPayment().getVoucherType().toString());
		voucherTypeLabel.setAttribute("voucherType", getVoucherPayment().getVoucherType());
		// setSelected voucherStatusCombobox
		for (Comboitem item : voucherStatusCombobox.getItems()) {
			if (getVoucherPayment().getFlowStatus().equals(item.getValue())) {
				voucherStatusCombobox.setSelectedItem(item);
			}
		}
		// customerTextbox
		// if (paidByCombobox.getSelectedItem().getValue().equals(PaymentType.tunai)) {
		//	customerTextbox.setValue("tunai");
		//	customerTextbox.setAttribute("customer", null);
		//} else 
		if (getVoucherPayment().getId()==Long.MIN_VALUE) {
			// new
			customerTextbox.setValue(getVoucherPayment().getCustomer().getCompanyType()+". "+
					getVoucherPayment().getCustomer().getCompanyLegalName());
			customerTextbox.setAttribute("customer", getVoucherPayment().getCustomer());
		} else {
			// edit or View -- voucherPayment from VoucherPaymentListInfo as well as Settlement -- need to use proxy
			Customer customer = getCustomerFromVoucherPaymentByProxy(getVoucherPayment().getId());
			
			customerTextbox.setValue(customer.getCompanyType()+". "+
					customer.getCompanyLegalName());
			customerTextbox.setAttribute("customer", customer);
		}		
		paidByInfoTextbox.setValue(getVoucherPayment().getPaidByNote());
		// sumOfTextbox - accompanied by totalOrderValue
		
		descriptionTextbox.setValue(getVoucherPayment().getTransactionDescription());
		referenceTextbox.setValue(getVoucherPayment().getDocumentRef());
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {			
			theSumOfTextbox.setValue(toLocalFormat(getTotalOrderValue()));
		} else {
			theSumOfTextbox.setValue(toLocalFormat(getVoucherPayment().getTheSumOf()));
			setTotalOrderValue(getVoucherPayment().getTheSumOf());
			
			// display dbcr
			setDebitCreditJournalList(
					new ListModelList<VoucherPaymentDebitCredit>(
							getVoucherPayment().getVoucherPaymentDebitCredits()));			
			setVoucherPaymentDebitCreditInfo();
		}
	}
	
	public void onClick$createDebitCreditButton(Event event) throws Exception {
		// * - Pada hari giro cair: pilih pembayaran dgn giro: pilih nama customer: pilih giro yg diberikan
		// * 		CR	Giro Ditangan (1.212.0001) #45
		// * 		DB	Bank - BCA - SWI Asemka (1.221.0005) #46
		// * 
		// * - Customer membayar dengan transfer bank: pilih nama customer: pilih customer order: pilih pembayaran dgn bank: isi informasi bank dan jumlah
		// * 		CR	Piutang Langganan (COA 1.241.003 ) #44
		// * 		DB	Bank - BCA - SWI Asemka (1.221.0005) #46
		// * 
		// * - Customer membayar dengan tunai: pilih nama customer: pilih customer order: pilih pembayaran tunai: isi jumlah
		// * 		CR	Piutang Langganan (COA 1.241003 ) #44	- jumlah pembelian
		// * 		DB	Petty Cash (1.211.0001) #4				- jumlah pembayaran
		VoucherPaymentDebitCredit debitAccount = new VoucherPaymentDebitCredit();
		debitAccount.setDebitAmount(getTotalOrderValue());
		debitAccount.setCreditAmount(BigDecimal.ZERO);
		debitAccount.setDbcrDescription("Pembayaran dengan");
		
		VoucherPaymentDebitCredit creditAccount = new VoucherPaymentDebitCredit();
		creditAccount.setDebitAmount(BigDecimal.ZERO);
		creditAccount.setCreditAmount(getTotalOrderValue());
		creditAccount.setDbcrDescription("Pembayaran dengan");
		
		if (paidByCombobox.getSelectedItem().getValue().equals(PaymentType.giro)) {
			// * - pencairan giro
			debitAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(46)));
			// giroToVoucherPayment -- voucherPayment --> discriptionTextbox
			debitAccount.setDbcrDescription(descriptionTextbox.getValue());
			creditAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(45)));
			// giroToVoucherPayment -- voucherPayment --> discriptionTextbox
			creditAccount.setDbcrDescription(descriptionTextbox.getValue());
			
			// no need to check for adjustment because it's taken care of in VoucherGiroReceipt
			
		} else if (paidByCombobox.getSelectedItem().getValue().equals(PaymentType.bank)) {
			// * - pembayaran transfer
			// * 		CR	Piutang Langganan (COA 1.241.003 ) #44	- jumlah pembelian (total amountToSettle)
			// * 		DB	Bank-BCA-SWI Asemka (1.221.0005) #46	- jumlah pembayaran
			// CR
			creditAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(44)));
			creditAccount.setDbcrDescription(descriptionTextbox.getValue());
			creditAccount.setDebitAmount(BigDecimal.ZERO);
			creditAccount.setCreditAmount(getTotalAmountToSettle(getPaymentData().getSettlement().getSettlementDetails()));
			// DB
			debitAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(46)));
			debitAccount.setDbcrDescription(descriptionTextbox.getValue());
			debitAccount.setDebitAmount(getTotalOrderValue());
			debitAccount.setCreditAmount(BigDecimal.ZERO);			
		} else {
			// * - pembayaran tunai
			// * 		CR	Piutang Langganan (COA 1.241003 ) #44	- jumlah pembelian (total amountToSettle)
			// * 		DB	Petty Cash (1.211.0001) #4				- jumlah pembayaran
			// CR
			creditAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(44)));
			creditAccount.setDbcrDescription(descriptionTextbox.getValue());
			creditAccount.setDebitAmount(BigDecimal.ZERO);
			creditAccount.setCreditAmount(getTotalAmountToSettle(getPaymentData().getSettlement().getSettlementDetails()));
			// DB
			debitAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(4)));
			debitAccount.setDbcrDescription(descriptionTextbox.getValue());
			debitAccount.setDebitAmount(getTotalOrderValue());
			debitAccount.setCreditAmount(BigDecimal.ZERO);
		}		

		// adjustment account, in case needed
		VoucherPaymentDebitCredit adjustmentAccount = null;

		// check whether adjustment accoung is needed - for payment type other than giro
		// - such as Bank or Tunai
		if (!paidByCombobox.getSelectedItem().getValue().equals(PaymentType.giro)) {
			if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==-1) {
				// Skenario #2 - posisi settlement: minus - kekurangan pembayaran
				// - posisi  Debit (Debit Account) - COA:
				// Disc penjualan 	:   41110003 - id=47 - default
				// Retur Penjualan	:   41110004
				adjustmentAccount = new VoucherPaymentDebitCredit();
				adjustmentAccount.setDebitAmount(getAdjustmentAmount().multiply(new BigDecimal(-1)));
				// Adjust the debit account
				// -- should be substract, adjustmentAmount is negative, so just add
				// debitAccount.setDebitAmount(getTotalOrderValue().add(getAdjustmentAmount()));
				adjustmentAccount.setCreditAmount(BigDecimal.ZERO);
				adjustmentAccount.setDbcrDescription(descriptionTextbox.getValue());
				adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(47)));
				
			} else if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==0) {
				// - posting total pembayaran - TANPA adjustment				
				
			} else {
				// Skenario #3 - posisi settlement: plus - kelebihan pembayaran
				// - posisi Kredit (Credit Account) - COA:
				// Pembayaran dimuka	:   44110002
				// Pendapatan dll		:   44110001 - id=49 - default
				// Meterai				:   52150001
				adjustmentAccount = new VoucherPaymentDebitCredit();
				adjustmentAccount.setCreditAmount(getAdjustmentAmount());
				// Adjust the credit account
				// creditAccount.setCreditAmount(getTotalOrderValue().subtract(getAdjustmentAmount()));
				adjustmentAccount.setDebitAmount(BigDecimal.ZERO);
				adjustmentAccount.setDbcrDescription(descriptionTextbox.getValue());
				adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(49)));

			}
			
		}
		
		setDebitCreditJournalList(new ListModelList<VoucherPaymentDebitCredit>());
		
		// index#0
		// getVoucherPaymentDebitCreditList().add(creditAccount);
		getDebitCreditJournalList().add(creditAccount);
		// index#1 -- allow user to change the coa
		// getVoucherPaymentDebitCreditList().add(debitAccount);
		getDebitCreditJournalList().add(debitAccount);
		// index#2 -- if adjustment is needed
		if (adjustmentAccount!=null) {
			// getVoucherPaymentDebitCreditList().add(adjustmentAccount);
			getDebitCreditJournalList().add(adjustmentAccount);
		}
		
		// display
		setVoucherPaymentDebitCreditInfo();
	}
		
		
	private BigDecimal getTotalAmountToSettle(List<SettlementDetail> settlementDetails) {
		
		BigDecimal totalAmountToSettle = BigDecimal.ZERO;
		
		for (SettlementDetail settlementDetail : settlementDetails) {
			totalAmountToSettle = totalAmountToSettle.add(settlementDetail.getAmountToSettle());
		}
		
		return totalAmountToSettle;
	}

	private void setVoucherPaymentDebitCreditInfo() {
		// model
		voucherDbcrListbox.setModel(getDebitCreditJournalList());
		// render
		voucherDbcrListbox.setItemRenderer(
				getVoucherPaymentDebitCreditListitemRenderer());		
	}	
	
	private ListitemRenderer<VoucherPaymentDebitCredit> getVoucherPaymentDebitCreditListitemRenderer() {
		
		return new ListitemRenderer<VoucherPaymentDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherPaymentDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = initCoaNumber(new Listcell(), voucherDbCr, index);
				// new Listcell(voucherDbCr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama Akun
				if (voucherDbCr.getMasterCoa()==null) {
					lc = new Listcell();
				} else {
					lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaName());					
				}
				lc.setParent(item);
								
				// Keterangan
				lc = initDescription(new Listcell(), voucherDbCr);
				lc.setParent(item);
				
				// Debit
				lc = initDebit(new Listcell(), voucherDbCr);
				lc.setParent(item);
				
				// Kredit
				lc = initKredit(new Listcell(), voucherDbCr);
				lc.setParent(item);
				
				// remove row
				lc = initRemoveRow(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
				// add row
				lc = initAddRow(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
			}

			private Listcell initCoaNumber(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr, int index) throws Exception {
				Combobox coaNumberCombobox = new Combobox();
				
				VoucherType voucherType =
						(VoucherType) voucherTypeLabel.getAttribute("voucherType");
				
				if (getPageMode().equals(PageMode.VIEW)) {
					listcell.setLabel(voucherDbCr.getMasterCoa().getMasterCoaComp());
					
					return listcell;
				}
				
				if (index==0) {
					Comboitem comboitem = new Comboitem();
					comboitem.setValue(voucherDbCr.getMasterCoa());
					comboitem.setLabel(voucherDbCr.getMasterCoa().getMasterCoaComp());
					comboitem.setParent(coaNumberCombobox);
					
					// coaNumberCombobox.setValue(voucherDbCr.getMasterCoa().getMasterCoaComp());
					coaNumberCombobox.setSelectedIndex(0);
					coaNumberCombobox.setWidth("165px");
					// 19/08/2021
					// coaNumberCombobox.setDisabled(true);
					coaNumberCombobox.setParent(listcell);					

				} else if ((index==1) && 
						((voucherType.compareTo(VoucherType.PAYMENT_BANK)==0 || 
						(voucherType.compareTo(VoucherType.PAYMENT_GIRO)==0)))) {
					// debit account
					Comboitem comboitem;
					List<Coa_Voucher> coaVoucherList = getCoa_VoucherDao().findCoaVoucherByDebitAccount(true);
					for (Coa_Voucher coa_Voucher : coaVoucherList) {
						comboitem = new Comboitem();
						comboitem.setLabel(coa_Voucher.getMasterCoa().getMasterCoaComp());
						comboitem.setValue(coa_Voucher.getMasterCoa());
						comboitem.setParent(coaNumberCombobox);
					}
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Combobox combobox = (Combobox) event.getTarget();
							Comboitem comboitem = combobox.getSelectedItem();
							Coa_05_Master coaMaster = comboitem.getValue();
							
							Listitem item = voucherDbcrListbox.getItemAtIndex(index);
							Listcell lc = (Listcell) item.getChildren().get(1);
							lc.setLabel(coaMaster.getMasterCoaName());
						}
					});
					// coaNumberCombobox.setValue(voucherDbCr.getMasterCoa().getMasterCoaComp());
					coaNumberCombobox.setSelectedIndex(0);
					coaNumberCombobox.setWidth("165px");
					// 19/08/2021
					// coaNumberCombobox.setDisabled(getPageMode().compareTo(PageMode.NEW) != 0);
					coaNumberCombobox.setParent(listcell);
				} else if (index==2) {
					// adjustment
					
					Comboitem comboitem;
					
					if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
						// DEBIT accounts
						List<Coa_Adjustment> coaDebitAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(true);
						for (Coa_Adjustment coa_Adjustment : coaDebitAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment.getMasterCoa());
							comboitem.setParent(coaNumberCombobox);
						}
						
					} else {
						// CREDIT accounts
						List<Coa_Adjustment> coaCreditAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(false);
						for (Coa_Adjustment coa_Adjustment : coaCreditAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment.getMasterCoa());
							comboitem.setParent(coaNumberCombobox);
						}

					}
					// event
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Combobox combobox = (Combobox) event.getTarget();
							Comboitem comboitem = combobox.getSelectedItem();							
							// Coa_Adjustment coa_Adjustment = comboitem.getValue();
							Coa_05_Master coa_Master = comboitem.getValue();
							
							Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
							Listcell lc = (Listcell) item.getChildren().get(1);
							lc.setLabel(coa_Master.getMasterCoaName());
						}
					});
					coaNumberCombobox.setSelectedIndex(0);
					coaNumberCombobox.setWidth("165px");
					// 19/08/2021
					// coaNumberCombobox.setDisabled(getPageMode().compareTo(PageMode.NEW) != 0);
					coaNumberCombobox.setParent(listcell);										
					
				} else {
					Comboitem comboitem;
					
					if (voucherDbCr.getMasterCoa()!=null) {
						comboitem = new Comboitem();
						comboitem.setLabel(voucherDbCr.getMasterCoa().getMasterCoaComp());
						comboitem.setValue(voucherDbCr.getMasterCoa());
						comboitem.setParent(coaNumberCombobox);
						
						coaNumberCombobox.setSelectedIndex(0);
						coaNumberCombobox.setWidth("165px");
						coaNumberCombobox.setParent(listcell);

						// No other Coa Selections provided once it has a predefined Coa
						return listcell;
					}
					
					comboitem = new Comboitem();
					comboitem.setLabel("--Pilih Akun--");
					comboitem.setValue(null);
					comboitem.setParent(coaNumberCombobox);
					
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
						
						@Override
						public void onEvent(Event event) throws Exception {
							log.info("display list of COA");

							if (coaNumberCombobox.getSelectedIndex()>0) {
								Comboitem selItem = coaNumberCombobox.getSelectedItem();
								Coa_05_Master selCoa = selItem.getValue();
								
								// set coa name (Nama Akun)
								Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
								Listcell lc = (Listcell) item.getChildren().get(1);
								lc.setLabel(selCoa.getMasterCoaName());
								
								// DONE!!!
								return;
							}
							
							Window masterCoaWin = (Window) Executions.createComponents(
									"/coa/Coa_05_MasterListDialog.zul", null, null);
							
							masterCoaWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
									
									for (Comboitem accItem : coaNumberCombobox.getItems()) {
										Coa_05_Master accCoa = accItem.getValue();
										
										if (accCoa==null) {
											
											// do nothing and loop again
											continue;
										}
										
										if (masterCoa.getMasterCoaComp().compareTo(accCoa.getMasterCoaComp())==0) {
											coaNumberCombobox.setSelectedItem(accItem);
											
											// it's the same as previous COA - DONE!!!
											return;
										}
									}
									
									Comboitem comboitem = new Comboitem();
									comboitem.setLabel(masterCoa.getMasterCoaComp());
									comboitem.setValue(masterCoa);
									comboitem.setParent(coaNumberCombobox);

									// selection
									coaNumberCombobox.setSelectedItem(comboitem);
									
									// set coa name (Nama Akun)
									Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
									Listcell lc = (Listcell) item.getChildren().get(1);
									lc.setLabel(masterCoa.getMasterCoaName());

								}
							});
							
							masterCoaWin.addEventListener(Events.ON_CANCEL, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									// null the selection
									coaNumberCombobox.setSelectedItem(null);
								}
								
							});
							
							masterCoaWin.doModal();														
						}
						
					});
					
					coaNumberCombobox.setWidth("165px");
					coaNumberCombobox.setParent(listcell);
				}
				
				return listcell;
			}
			
			private Listcell initDescription(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr) throws Exception {
				if (getPageMode().equals(PageMode.VIEW)) {
					listcell.setLabel(voucherDbCr.getDbcrDescription());
					
					return listcell;
				}
				
				Textbox textbox = new Textbox();
				textbox.setWidth("310px");
				textbox.setValue(voucherDbCr.getDbcrDescription());
				textbox.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initDebit(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr) throws Exception {
				Textbox textbox = new Textbox();
				textbox.setWidth("95px");
				textbox.setValue(toLocalFormat(voucherDbCr.getDebitAmount()));
				textbox.setAttribute("debitAmount", voucherDbCr.getDebitAmount());
				textbox.setStyle("text-align: right");
				textbox.setDisabled(true);
				textbox.setParent(listcell);
				if (getPageMode().equals(PageMode.VIEW)) {
					
					return listcell;
				}				
				
				Button button = new Button();
				button.setWidth("25px");
				button.setLabel("...");
				button.setSclass("selectButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, BigDecimal> arg = 
								Collections.singletonMap("totalOrder", voucherDbCr.getDebitAmount());
						
						Window totalOrderDialogWin = 
								(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
						
						totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								BigDecimal amount = (BigDecimal) event.getData();
								textbox.setValue(toLocalFormat(amount));
								textbox.setAttribute("debitAmount", amount);	
							}
						});
						
						totalOrderDialogWin.doModal();
					}
					
				});
				
				return listcell;
			}
			
			private Listcell initKredit(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr) throws Exception {
				Textbox textbox = new Textbox();
				textbox.setWidth("95px");
				textbox.setValue(toLocalFormat(voucherDbCr.getCreditAmount()));
				textbox.setAttribute("creditAmount", voucherDbCr.getCreditAmount());
				textbox.setStyle("text-align: right");
				textbox.setDisabled(true);
				textbox.setParent(listcell);
				if (getPageMode().equals(PageMode.VIEW)) {
					
					return listcell;
				}

				Button button = new Button();
				button.setWidth("25px");
				button.setLabel("...");
				button.setSclass("selectButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, BigDecimal> arg = 
								Collections.singletonMap("totalOrder", voucherDbCr.getCreditAmount());
						
						Window totalOrderDialogWin = 
								(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
						
						totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								BigDecimal amount = (BigDecimal) event.getData();
								textbox.setValue(toLocalFormat(amount));
								textbox.setAttribute("creditAmount", amount);	
							}
						});
						
						totalOrderDialogWin.doModal();
					}
					
				});
				
				return listcell;
			}
			
			private Listcell initRemoveRow(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr, int index) throws Exception {
				if (getPageMode().equals(PageMode.VIEW)) {

					return listcell;
				}
				
				
				Button button = new Button();
				button.setLabel("-");
				button.setWidth("25px");
				button.setSclass("selectButton");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Remove item at Index="+index);
						getDebitCreditJournalList().remove(index);
						// render
						// voucherDbcrListbox.setItemRenderer(
						//		getVoucherPaymentDebitCreditListitemRenderer());
						if (index>0) {
							Listitem item = voucherDbcrListbox.getItemAtIndex(index-1);							
							Listcell lc = (Listcell) item.getChildren().get(6);
							Button addButton = (Button) lc.getFirstChild();
							addButton.setVisible(true);							
						}

					}
					
				});
				
				return listcell;
			}
			
			private Listcell initAddRow(Listcell listcell, VoucherPaymentDebitCredit voucherDbCr, int index) {
				if (getPageMode().equals(PageMode.VIEW)) {

					return listcell;
				}
				
				Button button = new Button();
				button.setLabel("+");
				button.setWidth("25px");
				button.setSclass("selectButton");
				button.setParent(listcell);
				boolean isVisible = (getDebitCreditJournalList().size()-1)==index;
				button.setVisible(isVisible);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						getDebitCreditJournalList().add(createJournalItem());
						// render
						// voucherDbcrListbox.setItemRenderer(
						//		getVoucherPaymentDebitCreditListitemRenderer());		
						button.setVisible(false);
					}

					private VoucherPaymentDebitCredit createJournalItem() {
						VoucherPaymentDebitCredit journal = new VoucherPaymentDebitCredit();
						journal.setCreditAmount(BigDecimal.ZERO);
						journal.setDebitAmount(BigDecimal.ZERO);
						journal.setDbcrDescription(null);
						journal.setMasterCoa(null);
						
						return journal;
					}
					
				});
				
				return listcell;
			}
		};
	}
	
	public void onAfterRender$voucherDbcrListbox(Event event) throws Exception {
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			int dbCrCount = getDebitCreditJournalList().size();
			infoDbcrlabel.setValue("Debit/Kredit: "+dbCrCount+" items");

			return;
		}
		
		//init total
		BigDecimal totalDebitAmount = BigDecimal.ZERO;
		BigDecimal totalCreditAmount = BigDecimal.ZERO;

		if (getDebitCreditJournalList().isEmpty()) {
			// getVoucherPaymentDebitCreditList()==null
			infoDbcrlabel.setValue("Debit/Kredit: 0 items");
			
		} else {
			int dbCrCount = getDebitCreditJournalList().size();
			infoDbcrlabel.setValue("Debit/Kredit: "+dbCrCount+" items");
			
			// accumulate debit credit amount
			for (int j = 0; j < voucherDbcrListbox.getItems().size(); j++) {
				Listitem item = voucherDbcrListbox.getItems().get(j);
				
				Listcell lc;
				
				// debit - textbox.setAttribute("debitAmount", voucherDbCr.getDebitAmount());
				lc = (Listcell) item.getChildren().get(3);
				Textbox amountDebitTextbox = (Textbox) lc.getFirstChild();
				BigDecimal amountDebit = (BigDecimal) amountDebitTextbox.getAttribute("debitAmount");
				totalDebitAmount = totalDebitAmount.add(amountDebit);
				
				// credit - textbox.setAttribute("creditAmount", amount);
				lc = (Listcell) item.getChildren().get(4);
				Textbox amountCreditTextbox = (Textbox) lc.getFirstChild();
				BigDecimal amountCredit = (BigDecimal) amountCreditTextbox.getAttribute("creditAmount");
				totalCreditAmount = totalCreditAmount.add(amountCredit);
			}

		}

		totalDebitListfooter.setLabel(toLocalFormat(totalDebitAmount));
		totalCreditListfooter.setLabel(toLocalFormat(totalCreditAmount));
	}
	
	public void onClick$totalOrderButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getVoucherPayment()==null ? null : getVoucherPayment().getTheSumOf());
		
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
	
	private void setReadOnly() {
		if (getPageMode().compareTo(PageMode.EDIT)==0 || getPageMode().compareTo(PageMode.NEW)==0) {
			
			// button
			checkButton.setVisible(true);
			saveButton.setVisible(false);
			cancelButton.setLabel("Cancel");
		} else {
			paidByCombobox.setDisabled(true);
			paidByInfoTextbox.setDisabled(true);
			descriptionTextbox.setDisabled(true);
			referenceTextbox.setDisabled(true);
			
			// button
			checkButton.setVisible(false);
			saveButton.setVisible(false);
			cancelButton.setLabel("Tutup");
		}
	}
	
	public void onClick$checkButton(Event event) throws Exception {
		if (getDebitCreditJournalList()==null) {
			throw new Exception("Belum jurnal");
		}
		if (getDebitCreditJournalList().isEmpty()) {
			throw new Exception("Tidak ada jurnal");
		}
		log.info("Journals added to Voucher");
		// check journal completion
		for (int i = 0; i < voucherDbcrListbox.getItems().size(); i++) {
			Listitem item = voucherDbcrListbox.getItemAtIndex(i);
			
			Listcell lc;
			
			// account-no
			lc = (Listcell) item.getChildren().get(0);
			Combobox coaCombobox = (Combobox) lc.getFirstChild();
			if (coaCombobox.getSelectedItem()==null) {
				throw new Exception("Nomor akun belum terpilih - Baris no: "+(i+1));
			}
			// description
			lc = (Listcell) item.getChildren().get(2);
			Textbox descripTextbox = (Textbox) lc.getFirstChild();
			if (descripTextbox.getValue().isEmpty()) {
				throw new Exception("Tidak ada penjelasan jurnal - Baris no: "+(i+1));
			}			
		}		
		log.info("Journal Account number and Journal Description completed");
		//init total
		BigDecimal totalDebitAmount = BigDecimal.ZERO;
		BigDecimal totalCreditAmount = BigDecimal.ZERO;
		// accumulate debit credit amount
		for (int j = 0; j < voucherDbcrListbox.getItems().size(); j++) {
			Listitem item = voucherDbcrListbox.getItems().get(j);
			
			Listcell lc;
			
			// debit - textbox.setAttribute("debitAmount", voucherDbCr.getDebitAmount());
			lc = (Listcell) item.getChildren().get(3);
			Textbox amountDebitTextbox = (Textbox) lc.getFirstChild();
			BigDecimal amountDebit = (BigDecimal) amountDebitTextbox.getAttribute("debitAmount");
			totalDebitAmount = totalDebitAmount.add(amountDebit);
			
			// credit - textbox.setAttribute("creditAmount", amount);
			lc = (Listcell) item.getChildren().get(4);
			Textbox amountCreditTextbox = (Textbox) lc.getFirstChild();
			BigDecimal amountCredit = (BigDecimal) amountCreditTextbox.getAttribute("creditAmount");
			totalCreditAmount = totalCreditAmount.add(amountCredit);
		}
		totalDebitListfooter.setLabel(toLocalFormat(totalDebitAmount));
		totalCreditListfooter.setLabel(toLocalFormat(totalCreditAmount));
		
		
		// if ( !(totalDebitAmount.compareTo(getTotalOrderValue())==0) || !(totalCreditAmount.compareTo(getTotalOrderValue())==0) ) {
		//  	throw new Exception("Jumlah Debit / Credit Tidak Sama dengan Jumlah Voucher");
		// }
		// check journal balance
		if (totalDebitAmount.compareTo(totalCreditAmount)!=0) {
			throw new Exception("Jurnal Tidak Balance");
		}
		log.info("Journal is Balance");
		// set readonly
		transactionDatebox.setDisabled(true);
		paidByCombobox.setDisabled(true);
		paidByInfoTextbox.setDisabled(true);
		descriptionTextbox.setDisabled(true);
		referenceTextbox.setDisabled(true);
		createDebitCreditButton.setDisabled(true);
		for (int j = 0; j < voucherDbcrListbox.getItems().size(); j++) {
			Listitem item = voucherDbcrListbox.getItems().get(j);
			
			Listcell lc;
			
			lc = (Listcell) item.getChildren().get(0);
			Combobox coaCombobox = (Combobox) lc.getFirstChild();
			coaCombobox.setDisabled(true);

			lc = (Listcell) item.getChildren().get(2);
			Textbox descripTextbox = (Textbox) lc.getFirstChild();
			descripTextbox.setDisabled(true);
			
			lc = (Listcell) item.getChildren().get(3);
			Button debitButton = (Button) lc.getLastChild();
			debitButton.setDisabled(true);
			
			lc = (Listcell) item.getChildren().get(4);
			Button creditButton = (Button) lc.getLastChild();
			creditButton.setDisabled(true);
			
			lc = (Listcell) item.getChildren().get(5);
			Button minButton = (Button) lc.getFirstChild();
			minButton.setVisible(false);
			
			lc = (Listcell) item.getChildren().get(6);
			Button addButton = (Button) lc.getFirstChild();
			addButton.setVisible(false);
		}
		
		// enable save
		checkButton.setVisible(false);
		saveButton.setVisible(true);
	}

	public void onClick$saveButton(Event event) throws Exception {	
		VoucherPayment userModVoucherPayment = getUserUpdatedVoucherPayment(getVoucherPayment());
		
		// send event - depending on the pageMode
		Events.sendEvent(getPageMode().compareTo(PageMode.NEW)==0?
		 		Events.ON_OK : Events.ON_CHANGE, voucherPaymentDialogWin, 
				userModVoucherPayment);
		
		voucherPaymentDialogWin.detach();
	}
	
	private VoucherPayment getUserUpdatedVoucherPayment(VoucherPayment payment) throws Exception {
		
		Date defaultDate = transactionDatebox.getValue();
		
		payment.setTheSumOf(getTotalOrderValue());
		payment.setTransactionDate(defaultDate);
		payment.setTransactionDescription(descriptionTextbox.getValue());
		payment.setDocumentRef(referenceTextbox.getValue());
		payment.setCreateDate(defaultDate);
		payment.setModifiedDate(asDate(getLocalDate()));
		payment.setCheckDate(defaultDate);
		payment.setFlowStatus(DEFAULT_FLOW_STATUS);
		payment.setVoucherType((VoucherType) voucherTypeLabel.getAttribute("voucherType"));
		payment.setPaidBy(paidByCombobox.getSelectedItem().getValue());
		payment.setPaidByNote(paidByInfoTextbox.getValue());
		payment.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		payment.setVoucherStatus(DocumentStatus.NORMAL);
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			VoucherType voucherType = (VoucherType) voucherTypeLabel.getAttribute("voucherType");

			// create voucher number
			payment.setVoucherNumber(addVoucherNumber(voucherType, defaultDate));
			// create dbcr
			payment.setVoucherPaymentDebitCredits(createUserUpdatedDebitCredits());
			// post to general ledger immediately
			payment.setPostingDate(asDate(getLocalDate()));
			payment.setPostingVoucherNumber(getPostingVoucherNumberByVoucherType());
			payment.setGeneralLedgers(createGeneralLedgersFromVoucherPayment(payment));
		}
				
		return payment;
	}

	private List<GeneralLedger> createGeneralLedgersFromVoucherPayment(VoucherPayment payment) {
		List<GeneralLedger> generalLedgerList = new ArrayList<GeneralLedger>();
		
		for (int i = 0; i < voucherDbcrListbox.getItemCount(); i++) {
			Listitem item = voucherDbcrListbox.getItemAtIndex(i);
			
			Listcell lc;
			// create gl object
			GeneralLedger gl = new GeneralLedger();
			
			// Coa (0)
			lc = (Listcell) item.getChildren().get(0);
			Combobox coaCombobox = (Combobox) lc.getFirstChild();
			gl.setMasterCoa(coaCombobox.getSelectedItem().getValue());
			// 30/07/2021 - posting date must be the same as transaction date
			// gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingDate(payment.getTransactionDate());
			gl.setPostingVoucherNumber(payment.getPostingVoucherNumber());
			// Kredit (4)
			lc = (Listcell) item.getChildren().get(4);
			Textbox creditTextbox = (Textbox) lc.getFirstChild();
			gl.setCreditAmount((BigDecimal) creditTextbox.getAttribute("creditAmount"));
			// Debit (3)
			lc = (Listcell) item.getChildren().get(3);
			Textbox debitTextbox = (Textbox) lc.getFirstChild();
			gl.setDebitAmount((BigDecimal) debitTextbox.getAttribute("debitAmount"));
			// Descrip (2)
			lc = (Listcell) item.getChildren().get(2);
			Textbox descripTextbox = (Textbox) lc.getFirstChild();
			gl.setDbcrDescription(descripTextbox.getValue());

			gl.setTransactionDescription(payment.getTransactionDescription());
			gl.setDocumentRef(payment.getDocumentRef());
			gl.setTransactionDate(payment.getTransactionDate());
			gl.setVoucherType(payment.getVoucherType());
			gl.setVoucherNumber(payment.getVoucherNumber());

			generalLedgerList.add(gl);
		}
			
		generalLedgerList.forEach(gl->log.info(gl.toString()));
		
		return generalLedgerList;
	}

	private VoucherSerialNumber getPostingVoucherNumberByVoucherType() throws Exception {
		VoucherType voucherType = (VoucherType) voucherTypeLabel.getAttribute("voucherType");
		Date currentDate = asDate(getLocalDate());
		
		switch (voucherType) {
		case PAYMENT_BANK:
			return addVoucherNumber(VoucherType.POSTING_PAYMENTBANK, currentDate);			
		case PAYMENT_GIRO:
			return addVoucherNumber(VoucherType.POSTING_PAYMENTGIRO, currentDate);			
		case PAYMENT_CASH:
			return addVoucherNumber(VoucherType.POSTING_PAYMENTCASH, currentDate);			
		default:
			return null;
		}
		
	}

	private List<VoucherPaymentDebitCredit> createUserUpdatedDebitCredits() {
		List<VoucherPaymentDebitCredit> dbcrList = new ArrayList<VoucherPaymentDebitCredit>();
		
		// get the list items from listbox, create the dbcr object and add to the list
		for (int i = 0; i < voucherDbcrListbox.getItemCount(); i++) {
			Listitem item = voucherDbcrListbox.getItemAtIndex(i);
			
			Listcell lc;
			// create dbcr object
			VoucherPaymentDebitCredit dbcr = new VoucherPaymentDebitCredit();
			// Kredit (4)
			lc = (Listcell) item.getChildren().get(4);
			Textbox creditTextbox = (Textbox) lc.getFirstChild();
			dbcr.setCreditAmount((BigDecimal) creditTextbox.getAttribute("creditAmount"));

			// Debit (3)
			lc = (Listcell) item.getChildren().get(3);
			Textbox debitTextbox = (Textbox) lc.getFirstChild();
			dbcr.setDebitAmount((BigDecimal) debitTextbox.getAttribute("debitAmount"));
			
			// Descrip (2)
			lc = (Listcell) item.getChildren().get(2);
			Textbox descripTextbox = (Textbox) lc.getFirstChild();
			dbcr.setDbcrDescription(descripTextbox.getValue());
			
			// Coa (0)
			lc = (Listcell) item.getChildren().get(0);
			Combobox coaCombobox = (Combobox) lc.getFirstChild();
			dbcr.setMasterCoa(coaCombobox.getSelectedItem().getValue());
			
			dbcrList.add(dbcr);
		}
		
		dbcrList.forEach(dbcr->log.info(dbcr.toString()));
		
		return dbcrList;
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

	public void onClick$cancelButton(Event event) throws Exception {
		voucherPaymentDialogWin.detach();
	}	

	private Customer getCustomerFromSettlementByProxy(long id) throws Exception {
		Settlement settlement = getSettlementDao().findCustomerByProxy(id);
		
		return settlement.getCustomer();
	}	
	
	private Customer getCustomerFromVoucherPaymentByProxy(long id) throws Exception {
		VoucherPayment payment = getVoucherPaymentDao().findCustomerByProxy(id);
		
		return payment.getCustomer();
	}

	private Customer getCustomerFromGiroByProxy(long id) throws Exception {
		Giro giro = getGiroDao().findCustomerByProxy(id);
		
		return giro.getCustomer();
	}
	
	private VoucherSerialNumber getPostingVoucherNumberByProxy(long id) throws Exception {
		VoucherPayment payment = getVoucherPaymentDao().findPostingVoucherNumberByProxy(id);
		
		return payment.getPostingVoucherNumber();
	}
	
	public VoucherPaymentData getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(VoucherPaymentData paymentData) {
		this.paymentData = paymentData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public BigDecimal getTotalOrderValue() {
		return totalOrderValue;
	}

	public void setTotalOrderValue(BigDecimal totalOrderValue) {
		this.totalOrderValue = totalOrderValue;
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

/*	public List<VoucherPaymentDebitCredit> getVoucherPaymentDebitCreditList() {
		return voucherPaymentDebitCreditList;
	}

	public void setVoucherPaymentDebitCreditList(List<VoucherPaymentDebitCredit> voucherPaymentDebitCreditList) {
		this.voucherPaymentDebitCreditList = voucherPaymentDebitCreditList;
	}
*/
	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}

	/**
	 * @return the coa_VoucherDao
	 */
	public Coa_VoucherDao getCoa_VoucherDao() {
		return coa_VoucherDao;
	}

	/**
	 * @param coa_VoucherDao the coa_VoucherDao to set
	 */
	public void setCoa_VoucherDao(Coa_VoucherDao coa_VoucherDao) {
		this.coa_VoucherDao = coa_VoucherDao;
	}

	public GiroDao getGiroDao() {
		return giroDao;
	}

	public void setGiroDao(GiroDao giroDao) {
		this.giroDao = giroDao;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public Coa_AdjustmentDao getCoa_AdjustmentDao() {
		return coa_AdjustmentDao;
	}

	public void setCoa_AdjustmentDao(Coa_AdjustmentDao coa_AdjustmentDao) {
		this.coa_AdjustmentDao = coa_AdjustmentDao;
	}

	public ListModelList<VoucherPaymentDebitCredit> getDebitCreditJournalList() {
		return debitCreditJournalList;
	}

	public void setDebitCreditJournalList(ListModelList<VoucherPaymentDebitCredit> debitCreditJournalList) {
		this.debitCreditJournalList = debitCreditJournalList;
	}

}
