package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherGiroReceiptDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherPaymentDebitCredit;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SuppressedException;

public class SettlementDialogBatalControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555508437082374957L;

	private SettlementDao settlementDao;
	private VoucherGiroReceiptDao voucherGiroReceiptDao;
	private GiroDao giroDao;
	private VoucherPaymentDao voucherPaymentDao;
	private CustomerOrderDao customerOrderDao;
	private CustomerReceivableDao customerReceivableDao;
	
	private Window settlementDialogBatalWin;
	private Tabbox settlementBatalTabbox;
	private Tab settlementTab, voucherTab, giroTab, giroPaymentTab, glTab, glGiroPaymentTab, 
		piutangTab;
	private Grid giroReceiptGrid, voucherPaymentGrid;
	private Combobox statusSettlementCombobox, statusVoucherCombobox, statusGiroCombobox, 
		statusGiroPaymentCombobox, voucherStatusCombobox, paidByCombobox, statusGLCombobox,
		statusGLPaymentCombobox, statusPiutangCombobox, voucherStatusGiroPaymentCombobox;
	// private Button saveBatalButton, saveRevertButton;
	private Datebox pembatalanSettlementDatebox, settlementDatebox, pembatalanVoucherDatebox,
		transactionDatebox,	giroDateReceiptDatebox, pembatalanGiroDatebox, giroDateDatebox,
		giroReceivedDatebox, pembatalanGiroPaymentDatebox, transactionGiroPaymentDatebox,
		glPembatalanDatebox, glPostingDatebox, glGiroPaymentPembatalanDatebox, 
		glGiroPaymentPostingDatebox, piutangPembatalanDatebox;
	private Textbox pembatalanCatatanSettlementTextbox, settlementNoCompTextbox,
		customerSettlementTextbox, paymentSettlementAmountTextbox, referenceSettlementTextbox, 
		descriptionSettlementTextbox, pembatalanVoucherCatatanTextbox, voucherNoCompTextbox, 
		voucherNoPostTextbox, customerVoucherTextbox,
		noGiroReceiptTextbox, giroBankReceiptTextbox, theSumOfGiroReceiptTextbox,
		descriptionGiroReceiptTextbox, referenceGiroReceiptTextbox, theSumOfVoucherPaymentTextbox,
		descriptionVoucherPaymentTextbox, referenceVoucherPaymentTextbox, paidByInfoTextbox,
		giroPembatalanCatatanTextbox, giroNoTextbox, giroBankTextbox, giroAmountTextbox, 
		giroCustomerTextbox, giroPaymentPembatalanCatatanTextbox, voucherNoGiroPaymentCompTextbox,
		voucherNoPostGiroPaymentTextbox, customerGiroPaymentTextbox,
		theSumOfGiroPaymentTextbox, descriptionGiroPaymentTextbox, referenceGiroPaymentTextbox,
		glPembatalanCatatanTextbox, glPostingNumberTextbox, glVoucherNumberTextbox, glDescriptionTextbox, 
		glReferenceTextbox, glGiroPaymentPembatalanCatatanTextbox, glGiroPaymentPostingNumberTextbox, 
		glGiroPaymentVoucherNumberTextbox, glGiroPaymentDescriptionTextbox, glGiroPaymentReferenceTextbox,
		piutangPembatalanCatatanTextbox;
	private Listbox settlementDetailListbox, voucherDbcrVoucherPaymentGiroReceiptListbox,
		voucherDbcrGiroPaymentListbox, glListbox, glGiroPaymentListbox, piutangListbox;
	private Label settlementIdLabel, voucherIdLabel, giroIdLabel, voucherTypeLabel, giroVoucherPaymentIdLabel,
		glId, glGiroPaymentIdLabel, voucherTypeGiroPaymentLabel, glVoucherTypeLabel,
		glGiroPaymentVoucherTypeLabel;
	
	private Settlement settlement;	
	private BigDecimal totalAmount, remainingAmount;

	private static final Logger log = Logger.getLogger(SettlementDialogBatalControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setSettlement(
				(Settlement) arg.get("settlement"));
	}
	
	public void onCreate$settlementDialogBatalWin(Event event) throws Exception {
		settlementDialogBatalWin.setTitle("Pembatalan Settlement");
		
		// settlement
		pembatalanSettlementDatebox.setLocale(getLocale());
		pembatalanSettlementDatebox.setFormat(getLongDateFormat());
		settlementDatebox.setLocale(getLocale());
		settlementDatebox.setFormat(getLongDateFormat());
		// voucher
		pembatalanVoucherDatebox.setLocale(getLocale());
		pembatalanVoucherDatebox.setFormat(getLongDateFormat());
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
		giroDateReceiptDatebox.setLocale(getLocale());
		giroDateReceiptDatebox.setFormat(getLongDateFormat());
		// giro
		pembatalanGiroDatebox.setLocale(getLocale()); 
		pembatalanGiroDatebox.setFormat(getLongDateFormat());
		giroDateDatebox.setLocale(getLocale());
		giroDateDatebox.setFormat(getLongDateFormat());
		giroReceivedDatebox.setLocale(getLocale());
		giroReceivedDatebox.setFormat(getLongDateFormat());
		// giro-payment
		pembatalanGiroPaymentDatebox.setLocale(getLocale()); 
		pembatalanGiroPaymentDatebox.setFormat(getLongDateFormat());
		transactionGiroPaymentDatebox.setLocale(getLocale());
		transactionGiroPaymentDatebox.setFormat(getLongDateFormat());
		// gl
		glPembatalanDatebox.setLocale(getLocale()); 
		glPembatalanDatebox.setFormat(getLongDateFormat());
		glPostingDatebox.setLocale(getLocale());
		glPostingDatebox.setFormat(getLongDateFormat());
		// gl Giro-Payment
		glGiroPaymentPembatalanDatebox.setLocale(getLocale()); 
		glGiroPaymentPembatalanDatebox.setFormat(getLongDateFormat());
		glGiroPaymentPostingDatebox.setLocale(getLocale());
		glGiroPaymentPostingDatebox.setFormat(getLongDateFormat());
		// piutang
		piutangPembatalanDatebox.setLocale(getLocale());
		piutangPembatalanDatebox.setFormat(getLongDateFormat());
		
		// settlement, settlementDetails, customerOrder, customerOrderProducts
		settlementTab();
		
		// <tab id="voucherTab" label="Voucher" visible="true"></tab>
		voucherTab();

		// <tab id="giroTab" label="Giro" visible="true"></tab>
		giroTab();
		
		// <tab id="glTab" label="GL" visible="true"></tab>
		glTab();
		
		// <tab id="piutangTab" label="Piutang" visible="true"></tab>
		piutangTab();
		
		// allows user to 'Batal' and to 'Revert' back to Normal status
		// saveBatalButton.setVisible(getSettlement().getSettlementStatus().equals(DocumentStatus.NORMAL));
		// saveRevertButton.setVisible(getSettlement().getSettlementStatus().equals(DocumentStatus.BATAL));
	}

	/*
	 * 
	 * SETTLEMENT 
	 * 
	 */
	private void settlementTab() throws Exception {
		settlementTab.setVisible(true);
		
		settlementIdLabel.setValue("id:#"+getSettlement().getId());
		
		// statusSettlementCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusSettlementCombobox);
		}
		// set status
		for (Comboitem comboItem : statusSettlementCombobox.getItems()) {
			if (getSettlement().getSettlementStatus().equals(comboItem.getValue())) {
				statusSettlementCombobox.setSelectedItem(comboItem);				
			}
		}
		// set tgl pembatalan - if not null
		if (getSettlement().getBatalDate()!=null) {
			pembatalanSettlementDatebox.setValue(getSettlement().getBatalDate());
		} else {
			pembatalanSettlementDatebox.setValue(asDate(getLocalDate()));
		}
		// pembatalan note
		pembatalanCatatanSettlementTextbox.setValue(getSettlement().getBatalNote());
		
		// master-settlement
		settlementNoCompTextbox.setValue(getSettlement().getSettlementNumber().getSerialComp());
		settlementDatebox.setValue(getSettlement().getSettlementDate());
		
		Settlement settlementCustomerByProxy = getSettlementDao().findCustomerByProxy(getSettlement().getId());
		customerSettlementTextbox.setValue(settlementCustomerByProxy.getCustomer().getCompanyType()+"."+
				settlementCustomerByProxy.getCustomer().getCompanyLegalName());

		paymentSettlementAmountTextbox.setValue(toLocalFormat(getSettlement().getAmountPaid()));
		setTotalAmount(getSettlement().getAmountPaid());
		referenceSettlementTextbox.setValue(getSettlement().getDocumentRef()); 
		descriptionSettlementTextbox.setValue(getSettlement().getSettlementDescription());
		
		// display
		displaySettlementDetails(getSettlement());
	}

	private void displaySettlementDetails(Settlement settlement) {
		settlementDetailListbox.setModel(
				new ListModelList<SettlementDetail>(settlement.getSettlementDetails()));
		settlementDetailListbox.setItemRenderer(getSettlementDetailListitemRenderer());
	}

	private ListitemRenderer<SettlementDetail> getSettlementDetailListitemRenderer() {
		
		return new ListitemRenderer<SettlementDetail>() {

			@Override
			public void render(Listitem item, SettlementDetail detail, int index) throws Exception {
				Listcell lc;
				
				// Status - Customer Order
				lc = initCustomerOrderStatus(new Listcell(), detail);
				lc.setParent(item);
				
				//	No.Order - CustomerOrder
				lc = new Listcell(
						detail.getCustomerOrder().getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				//	Tgl.Order - CustomerOrder
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
							detail.getCustomerOrder().getOrderDate()), getShortDateFormat()));
				lc.setParent(item);

				CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(detail.getCustomerOrder().getId());				
				SuratJalan suratJalanByProxy = customerOrder.getSuratJalan();
				
				//	No.SuratJalan - Customer Order
				lc = new Listcell(suratJalanByProxy.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);
				
				//	Tgl.SuratJalan - Customer Order
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
								suratJalanByProxy.getSuratJalanDate()), getShortDateFormat()));
				lc.setParent(item);
				
				//	Tgl.Jth-Tempo - Customer Order
				LocalDate dueDate = addDate(
						detail.getCustomerOrder().getCreditDay(), 
						asLocalDate(detail.getCustomerOrder().getOrderDate()));
				lc = new Listcell(
						dateToStringDisplay(dueDate, getShortDateFormat()));
				lc.setParent(item);
				
				//	Total Order - Customer Order
				lc = new Listcell(toLocalFormat(detail.getCustomerOrder().getTotalOrder()));
				lc.setParent(item);
				
				//	Settlement - Customer Order
				String settlement = "";
				if (detail.getCustomerOrder().isPaymentComplete()) {
					settlement = "Lunas";
				} else {
					settlement = toLocalFormat(detail.getCustomerOrder().getAmountPaid());
				}
				lc = new Listcell(settlement);
				lc.setStyle("font-weight: bold; color: red;");
				lc.setParent(item);
				
				//	Jumlah Pembayaran
				lc = new Listcell(toLocalFormat(detail.getAmountSettled()));
				lc.setStyle("font-weight: bold; color: red;");
				lc.setParent(item);
				
				//	Sisa
				lc = new Listcell(toLocalFormat(detail.getRemainingAmountToSettle()));
				lc.setParent(item);
				
				//	Jumlah Dibayarkan
				lc = initPayment(new Listcell(), detail, index);
				lc.setParent(item);				
				
				item.setValue(detail);
			}

			private Listcell initCustomerOrderStatus(Listcell listcell, SettlementDetail detail) {
				boolean isCustomerOrderBatal = detail.getCustomerOrder().getOrderStatus().equals(DocumentStatus.BATAL);
				
				// lc = new Listcell(isCustomerOrderBatal ? "Batal" : "");
				if (isCustomerOrderBatal) {
					Label label = new Label();
					label.setValue("Batal");
					label.setSclass("badge badge-red");
					label.setParent(listcell);					
				} else {
					Label label = new Label();
					label.setValue("OK");
					label.setSclass("badge badge-green");
					label.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initPayment(Listcell listcell, SettlementDetail detail, int index) {
				Decimalbox paymentDecimalbox = new Decimalbox();
				paymentDecimalbox.setValue(BigDecimal.ZERO);
				paymentDecimalbox.setLocale(getLocale());
				paymentDecimalbox.setSclass("textboxInList");
				paymentDecimalbox.setWidth("110px");
				paymentDecimalbox.setReadonly(true);
				paymentDecimalbox.setParent(listcell);
				
				if (index==0) {
					
					if (getTotalAmount().compareTo(detail.getRemainingAmountToSettle())==-1) {
						// money is less than 
						
						paymentDecimalbox.setValue(getTotalAmount());						
						setRemainingAmount(getTotalAmount().subtract(detail.getRemainingAmountToSettle()));
					} else {
						// money is more than
						
						setRemainingAmount(getTotalAmount().subtract(detail.getAmountSettled()));
						paymentDecimalbox.setValue(getTotalAmount().subtract(getRemainingAmount()));
					}

				} else {
					
					if (getRemainingAmount().compareTo(detail.getAmountToSettle())==-1) {
						// remaining money is less than
						
						paymentDecimalbox.setValue(getRemainingAmount());
						setRemainingAmount(BigDecimal.ZERO);
					} else {
						// remaining money is more than
						
						setRemainingAmount(getRemainingAmount().subtract(detail.getRemainingAmountToSettle()));
						paymentDecimalbox.setValue(detail.getRemainingAmountToSettle());
					}
				}
				
				return listcell;
			}
		};
	}

	public void onSelect$statusSettlementCombobox(Event event) throws Exception {
		DocumentStatus status = statusSettlementCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusSettlementCombobox.setStyle("color: red;");
			
			// amount NOT set to Zero, only change the status to BATAL
			// paymentSettlementAmountTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
			// setTotalAmount(BigDecimal.ZERO);
			
			for (SettlementDetail detail : getSettlement().getSettlementDetails()) {
				// reset settlement detail - Jumlah Pembayaran and Sisa
				detail.setAmountSettled(BigDecimal.ZERO);
				detail.setAmountToSettle(BigDecimal.ZERO);
				detail.setRemainingAmountToSettle(BigDecimal.ZERO);
				
				// reset customerOrder
				detail.getCustomerOrder().setPaymentComplete(false);
				detail.getCustomerOrder().setAmountPaid(BigDecimal.ZERO);
			}
			displaySettlementDetails(getSettlement());
			
		} else {
			statusSettlementCombobox.setStyle("color: black;");
			
			paymentSettlementAmountTextbox.setValue(toLocalFormat(getSettlement().getAmountPaid()));
			setTotalAmount(getSettlement().getAmountPaid());			
			
			// re-load the selected settlement (because changing from Normal to Batal affects the ref object)			
			Settlement settlement =
					getSettlementDao().findSettlementById(getSettlement().getId());
			// display
			displaySettlementDetails(settlement);
		}
	}
	
	/*
	 * 
	 * VOUCHER
	 * 
	 */
	
	private void voucherTab() throws Exception {
		// voucherGiroReceipt or voucherPayment
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null) || (getSettlement().getVoucherPayment()!=null);
		voucherTab.setVisible(visible);
		log.info("voucherTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		// which grid to display?
		giroReceiptGrid.setVisible(getSettlement().getVoucherGiroReceipt()!=null);
		voucherPaymentGrid.setVisible(getSettlement().getVoucherPayment()!=null);
		
		// statusVoucherCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusVoucherCombobox);
		}
		pembatalanVoucherDatebox.setValue(asDate(getLocalDate()));
		
		DocumentStatus voucherStatus = null;
		if (getSettlement().getVoucherGiroReceipt()!=null) {
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceipt = 
					settlement.getVoucherGiroReceipt();
			
			voucherIdLabel.setValue("id:#"+voucherGiroReceipt.getId());
			
			voucherStatus = voucherGiroReceipt.getVoucherStatus();
			voucherNoCompTextbox.setValue(voucherGiroReceipt.getVoucherNumber().getSerialComp());
			VoucherGiroReceipt voucherGiroReceiptPostingNumberByProxy =
					getVoucherGiroReceiptDao().findPostingVoucherNumberByProxy(voucherGiroReceipt.getId());
			voucherNoPostTextbox.setValue(voucherGiroReceiptPostingNumberByProxy.getPostingVoucherNumber().getSerialComp());
			transactionDatebox.setValue(voucherGiroReceipt.getTransactionDate());
			voucherTypeLabel.setValue(voucherGiroReceipt.getVoucherType().toString());
			voucherStatusCombobox.setValue(voucherGiroReceipt.getFlowStatus().toString());
			VoucherGiroReceipt voucherGiroReceiptCustomerByProxy = 
					getVoucherGiroReceiptDao().findCustomerByProxy(voucherGiroReceipt.getId());
			customerVoucherTextbox.setValue(voucherGiroReceiptCustomerByProxy.getCustomer().getCompanyType()+"."+
					voucherGiroReceiptCustomerByProxy.getCustomer().getCompanyLegalName());
			
			noGiroReceiptTextbox.setValue(voucherGiroReceipt.getGiroNumber());
			giroBankReceiptTextbox.setValue(voucherGiroReceipt.getGiroBank());
			giroDateReceiptDatebox.setValue(voucherGiroReceipt.getGiroDate());
			theSumOfGiroReceiptTextbox.setValue(toLocalFormat(voucherGiroReceipt.getTheSumOf()));
			descriptionGiroReceiptTextbox.setValue(voucherGiroReceipt.getTransactionDescription());
			referenceGiroReceiptTextbox.setValue(voucherGiroReceipt.getDocumentRef());
						
			displayVoucherGiroReceiptDbCr(voucherGiroReceipt.getVoucherGiroReceiptDebitCredits());
		} else {
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPayment =
					settlement.getVoucherPayment();
			
			voucherIdLabel.setValue("id:#"+voucherPayment.getId());
			
			voucherStatus = voucherPayment.getVoucherStatus();
			voucherNoCompTextbox.setValue(voucherPayment.getVoucherNumber().getSerialComp());
			VoucherPayment voucherPaymentPostingNumberByProxy =
					getVoucherPaymentDao().findPostingVoucherNumberByProxy(voucherPayment.getId());
			voucherNoPostTextbox.setValue(voucherPaymentPostingNumberByProxy.getPostingVoucherNumber().getSerialComp());
			transactionDatebox.setValue(voucherPayment.getTransactionDate());
			voucherTypeLabel.setValue(voucherPayment.getVoucherType().toString());
			voucherStatusCombobox.setValue(voucherPayment.getFlowStatus().toString());
			VoucherPayment voucherPaymentCustomerByProxy = 
					getVoucherPaymentDao().findCustomerByProxy(voucherPayment.getId());
			customerVoucherTextbox.setValue(voucherPaymentCustomerByProxy.getCustomer().getCompanyType()+"."+
					voucherPaymentCustomerByProxy.getCustomer().getCompanyLegalName());
			
			theSumOfVoucherPaymentTextbox.setValue(toLocalFormat(voucherPayment.getTheSumOf()));
			descriptionVoucherPaymentTextbox.setValue(voucherPayment.getTransactionDescription());
			referenceVoucherPaymentTextbox.setValue(voucherPayment.getDocumentRef());
			paidByCombobox.setValue(voucherPayment.getPaidBy().toString());
			paidByInfoTextbox.setValue(voucherPayment.getPaidByNote());

			pembatalanVoucherCatatanTextbox.setValue(
					"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+
					" ("+voucherPayment.getTransactionDescription()+")");			
			
			displayVoucherPaymentDbCr(voucherPayment.getVoucherPaymentDebitCredits());
		}
		// set status		
		for (Comboitem comboItem : statusVoucherCombobox.getItems()) {
			if (voucherStatus.equals(comboItem.getValue())) {
				statusVoucherCombobox.setSelectedItem(comboItem);				
			}
		}
	}
	
	private void displayVoucherGiroReceiptDbCr(List<VoucherGiroReceiptDebitCredit> debitCredits) {
		voucherDbcrVoucherPaymentGiroReceiptListbox.setModel(
			new ListModelList<VoucherGiroReceiptDebitCredit>(debitCredits));
		voucherDbcrVoucherPaymentGiroReceiptListbox.setItemRenderer(getVoucherGiroReceiptDbCrItemRenderer());
	}

	private ListitemRenderer<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDbCrItemRenderer() {
		
		return new ListitemRenderer<VoucherGiroReceiptDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherGiroReceiptDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(dbcr.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(dbcr.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(dbcr);
			}
		};
	}

	private void displayVoucherPaymentDbCr(List<VoucherPaymentDebitCredit> debitCredits) {
		voucherDbcrVoucherPaymentGiroReceiptListbox.setModel(
			new ListModelList<VoucherPaymentDebitCredit>(debitCredits));
		voucherDbcrVoucherPaymentGiroReceiptListbox.setItemRenderer(getVoucherPaymentDbCrListitemRenderer());
	}

	private ListitemRenderer<VoucherPaymentDebitCredit> getVoucherPaymentDbCrListitemRenderer() {
		
		return new ListitemRenderer<VoucherPaymentDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherPaymentDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(dbcr.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(dbcr.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(dbcr);
			}
		};
	}

	public void onSelect$statusVoucherCombobox(Event event) throws Exception {
		DocumentStatus status = statusVoucherCombobox.getSelectedItem().getValue();
		statusVoucherCombobox.setStyle(status.equals(DocumentStatus.BATAL) ? "color:red;" : "color:black;");
		
		if (getSettlement().getVoucherGiroReceipt()!=null) {
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceipt = 
					settlement.getVoucherGiroReceipt();
		
			List<VoucherGiroReceiptDebitCredit> reverseDbCrList = null;
			
			if (status.equals(DocumentStatus.BATAL)) {
				pembatalanVoucherCatatanTextbox.setValue(
						"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+
						" ("+voucherGiroReceipt.getTransactionDescription()+")");

				reverseDbCrList = 
						new ArrayList<VoucherGiroReceiptDebitCredit>(voucherGiroReceipt.getVoucherGiroReceiptDebitCredits());
				
				for (VoucherGiroReceiptDebitCredit dbcr : voucherGiroReceipt.getVoucherGiroReceiptDebitCredits()) {
					
					String dbcrDescription = dbcr.getDbcrDescription() + 
							" (Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+")";
					
					VoucherGiroReceiptDebitCredit reverseDbCr = new VoucherGiroReceiptDebitCredit();
					reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
					reverseDbCr.setDbcrDescription(dbcrDescription);
					
					
					if (dbcr.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
						reverseDbCr.setDebitAmount(dbcr.getCreditAmount());
						reverseDbCr.setCreditAmount(BigDecimal.ZERO); 
					}
					
					if (dbcr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
						reverseDbCr.setDebitAmount(BigDecimal.ZERO);
						reverseDbCr.setCreditAmount(dbcr.getDebitAmount()); 
					}
					
					reverseDbCrList.add(reverseDbCr);
				}
				
				reverseDbCrList.sort((o1, o2) -> {
					return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
				});
				
			} else {
				pembatalanVoucherCatatanTextbox.setValue("");
				reverseDbCrList = 
						new ArrayList<VoucherGiroReceiptDebitCredit>(voucherGiroReceipt.getVoucherGiroReceiptDebitCredits());
			}
			
			displayVoucherGiroReceiptDbCr(reverseDbCrList);
		} else {
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPayment =
					settlement.getVoucherPayment();
			
			List<VoucherPaymentDebitCredit> reverseDbCrList = null;
			
			if (status.equals(DocumentStatus.BATAL)) {
				pembatalanVoucherCatatanTextbox.setValue(
						"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+
						" ("+voucherPayment.getTransactionDescription()+")");
				
				reverseDbCrList =
						new ArrayList<VoucherPaymentDebitCredit>(voucherPayment.getVoucherPaymentDebitCredits());
				
				for (VoucherPaymentDebitCredit dbcr : voucherPayment.getVoucherPaymentDebitCredits()) {
					String dbcrDescription = dbcr.getDbcrDescription() + 
							" (Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+")";
					
					VoucherPaymentDebitCredit reverseDbCr = new VoucherPaymentDebitCredit();
					reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
					reverseDbCr.setDbcrDescription(dbcrDescription);
					
					if (dbcr.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
						reverseDbCr.setDebitAmount(dbcr.getCreditAmount());
						reverseDbCr.setCreditAmount(BigDecimal.ZERO); 
					}
					
					if (dbcr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
						reverseDbCr.setDebitAmount(BigDecimal.ZERO);
						reverseDbCr.setCreditAmount(dbcr.getDebitAmount()); 
					}
					
					reverseDbCrList.add(reverseDbCr);
				}
				
				reverseDbCrList.sort((o1, o2) -> {
					return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
				});				
				
			} else {
				pembatalanVoucherCatatanTextbox.setValue("");
				reverseDbCrList =
						new ArrayList<VoucherPaymentDebitCredit>(voucherPayment.getVoucherPaymentDebitCredits());
			}
			
			displayVoucherPaymentDbCr(reverseDbCrList);
		}
	}
	
	/*
	 * 
	 * GIRO
	 * 
	 */
	
	private void giroTab() throws Exception {
		// voucherGiroReceipt not null means there's a giro
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null);
		giroTab.setVisible(visible);
		
		if (visible) {
			// use voucherGiroReceipt to obtain the Giro
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceiptByProxy = 
					settlement.getVoucherGiroReceipt();
			
			// get the giro and check whether the giro has been cashed
			Giro giro = voucherGiroReceiptByProxy.getGiro();
			log.info(giro.toString());
			
			giroIdLabel.setValue("id:#"+giro.getId());
			
			// statusGiroCombobox
			Comboitem comboitem;
			for (DocumentStatus documentStatus : DocumentStatus.values()) {
				comboitem = new Comboitem();
				comboitem.setLabel(documentStatus.toString());
				comboitem.setValue(documentStatus);
				comboitem.setParent(statusGiroCombobox);
			}
			// set status
			for (Comboitem comboItem : statusGiroCombobox.getItems()) {
				if (giro.getGiroStatus().equals(comboItem.getValue())) {
					statusGiroCombobox.setSelectedItem(comboItem);				
				}
			}
			// pembatalan date
			pembatalanGiroDatebox.setValue(asDate(getLocalDate()));
			// giro
			giroNoTextbox.setValue(giro.getGiroNumber());
			giroBankTextbox.setValue(giro.getGiroBank());
			giroDateDatebox.setValue(giro.getGiroDate());
			giroReceivedDatebox.setValue(giro.getGiroReceivedDate());
			giroAmountTextbox.setValue(toLocalFormat(giro.getGiroAmount()));
			Giro giroCustomerByProxy = 
					getGiroDao().findCustomerByProxy(giro.getId());
			giroCustomerTextbox.setValue(giroCustomerByProxy.getCustomer().getCompanyType()+"."+
					giroCustomerByProxy.getCustomer().getCompanyLegalName());

			// voucherPayment not null means giro has been cashed
			boolean isGiroVoucherPayment = (giro.getVoucherPayment()!=null);
			giroPaymentTab(isGiroVoucherPayment, giro.getId());
		}
	}

	public void onSelect$statusGiroCombobox(Event event) throws Exception {
		DocumentStatus status = statusGiroCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusGiroCombobox.setStyle("color: red;");
			
			giroPembatalanCatatanTextbox.setValue(
					"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp());

		} else {
			statusGiroCombobox.setStyle("color: black;");
			
			giroPembatalanCatatanTextbox.setValue("");
			
		}
		
	}
	
	/*
	 * 
	 * GIRO PAYMENT (VOUCHER PAYMENT with GIRO)
	 * 
	 */
	private void giroPaymentTab(boolean isGiroVoucherPayment, Long giroId) throws Exception {
		giroPaymentTab.setVisible(isGiroVoucherPayment);
		
		if (isGiroVoucherPayment) {
			Giro giroVoucherPaymentByProxy = 
					getGiroDao().findVoucherPaymentByProxy(giroId);
			VoucherPayment voucherPaymentByGiro = giroVoucherPaymentByProxy.getVoucherPayment();
			log.info(voucherPaymentByGiro.toString());

			giroVoucherPaymentIdLabel.setValue("id:#"+voucherPaymentByGiro.getId());
			
			// statusGiroPaymentCombobox
			Comboitem comboitem;
			for (DocumentStatus documentStatus : DocumentStatus.values()) {
				comboitem = new Comboitem();
				comboitem.setLabel(documentStatus.toString());
				comboitem.setValue(documentStatus);
				comboitem.setParent(statusGiroPaymentCombobox);
			}
			// status
			for (Comboitem comboItem : statusGiroPaymentCombobox.getItems()) {
				if (voucherPaymentByGiro.getVoucherStatus().equals(comboItem.getValue())) {
					statusGiroPaymentCombobox.setSelectedItem(comboItem);				
				}
			}
			// pembatalan date
			pembatalanGiroPaymentDatebox.setValue(asDate(getLocalDate()));

			// giroVoucherPayment
			voucherNoGiroPaymentCompTextbox.setValue(voucherPaymentByGiro.getVoucherNumber().getSerialComp());
			VoucherPayment voucherPaymentByGiroPostingNumberByProxy =
					getVoucherPaymentDao().findPostingVoucherNumberByProxy(voucherPaymentByGiro.getId());
			voucherNoPostGiroPaymentTextbox.setValue(
					voucherPaymentByGiroPostingNumberByProxy.getPostingVoucherNumber().getSerialComp());
			transactionGiroPaymentDatebox.setValue(voucherPaymentByGiro.getTransactionDate());
			voucherTypeGiroPaymentLabel.setValue(voucherPaymentByGiro.getVoucherType().toString());
			voucherStatusGiroPaymentCombobox.setValue(voucherPaymentByGiro.getFlowStatus().toString());
			VoucherPayment voucherPaymentByGiroCustomerByProxy =
					getVoucherPaymentDao().findCustomerByProxy(voucherPaymentByGiro.getId());
			customerGiroPaymentTextbox.setValue(voucherPaymentByGiroCustomerByProxy.getCustomer().getCompanyType()+"."+
					voucherPaymentByGiroCustomerByProxy.getCustomer().getCompanyLegalName());
			theSumOfGiroPaymentTextbox.setValue(toLocalFormat(voucherPaymentByGiro.getTheSumOf()));
			descriptionGiroPaymentTextbox.setValue(voucherPaymentByGiro.getTransactionDescription());
			referenceGiroPaymentTextbox.setValue(voucherPaymentByGiro.getDocumentRef());
			
			displayGiroVoucherPaymentDbCr(voucherPaymentByGiro.getVoucherPaymentDebitCredits());
		}
	}

	private void displayGiroVoucherPaymentDbCr(List<VoucherPaymentDebitCredit> voucherPaymentDebitCredits) {
		voucherDbcrGiroPaymentListbox.setModel(
				new ListModelList<VoucherPaymentDebitCredit>(voucherPaymentDebitCredits));
		voucherDbcrGiroPaymentListbox.setItemRenderer(getGiroVoucherPaymentItemRenderer());
		
	}

	private ListitemRenderer<VoucherPaymentDebitCredit> getGiroVoucherPaymentItemRenderer() {

		return new ListitemRenderer<VoucherPaymentDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherPaymentDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(dbcr.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(dbcr.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(dbcr);
			}
		};
	}

	public void onSelect$statusGiroPaymentCombobox(Event event) throws Exception {
		DocumentStatus status = statusGiroPaymentCombobox.getSelectedItem().getValue();
		
		List<VoucherPaymentDebitCredit> reverseDbCrList = null;
		
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt voucherGiroReceiptByProxy = 
				settlement.getVoucherGiroReceipt();
		Giro giro = voucherGiroReceiptByProxy.getGiro();
		Giro giroVoucherPaymentByProxy = 
				getGiroDao().findVoucherPaymentByProxy(giro.getId());
		VoucherPayment voucherPaymentByGiro = giroVoucherPaymentByProxy.getVoucherPayment();

		if (status.equals(DocumentStatus.BATAL)) {			
			statusGiroPaymentCombobox.setStyle("color: red;");
			giroPaymentPembatalanCatatanTextbox.setValue("Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+
					" ("+voucherPaymentByGiro.getTransactionDescription()+")");	
			
			reverseDbCrList =
					new ArrayList<VoucherPaymentDebitCredit>(voucherPaymentByGiro.getVoucherPaymentDebitCredits());
			
			for (VoucherPaymentDebitCredit dbcr : voucherPaymentByGiro.getVoucherPaymentDebitCredits()) {
				
				String dbcrDescription = dbcr.getDbcrDescription() +
						" (Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+")";
				
				VoucherPaymentDebitCredit reverseDbCr = new VoucherPaymentDebitCredit();
				reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
				reverseDbCr.setDbcrDescription(dbcrDescription);
				
				if (dbcr.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseDbCr.setDebitAmount(dbcr.getCreditAmount());
					reverseDbCr.setCreditAmount(BigDecimal.ZERO);
				}
				
				if (dbcr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseDbCr.setDebitAmount(BigDecimal.ZERO);
					reverseDbCr.setCreditAmount(dbcr.getDebitAmount());
				}
				
				reverseDbCrList.add(reverseDbCr);
			}
			
			reverseDbCrList.sort((o1, o2) -> {
				return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
			});
			
		} else {
			statusGiroPaymentCombobox.setStyle("color: black;");
			giroPaymentPembatalanCatatanTextbox.setValue("");

			reverseDbCrList =
					new ArrayList<VoucherPaymentDebitCredit>(voucherPaymentByGiro.getVoucherPaymentDebitCredits());
		}
		
		displayGiroVoucherPaymentDbCr(reverseDbCrList);
	}
	
	/*
	 * 
	 * GL
	 * 
	 */
	private void glTab() throws Exception {
		// voucherGiroReceipt or voucherPayment not null means GL has been created and needs to be reversed
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null) || (getSettlement().getVoucherPayment()!=null);
		glTab.setVisible(visible);
		log.info("glTab: visible: "+visible);
		if (!visible) {
			return;
		}

		// statusGLCombobox
		Comboitem comboitem;
		for (DocumentStatus status : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(status.toString());
			comboitem.setValue(status);
			comboitem.setParent(statusGLCombobox);			
		}
		
		statusGLCombobox.setSelectedIndex(0);
		glPembatalanDatebox.setValue(asDate(getLocalDate()));
		
		if (getSettlement().getVoucherGiroReceipt()!=null) {
			// gl from voucherGiroReceipt
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceiptByProxy =
					settlement.getVoucherGiroReceipt();
			VoucherGiroReceipt voucherGiroReceiptGeneralLedgerByProxy =
					getVoucherGiroReceiptDao().findGeneralLedgerByProxy(voucherGiroReceiptByProxy.getId());
			List<GeneralLedger> glList = 
					voucherGiroReceiptGeneralLedgerByProxy.getGeneralLedgers();
				
			// log
			glList.forEach(gl -> log.info(gl.toString()));
			
			if (glList!=null) {
				// get the 1st index
				GeneralLedger glMetaInfo = glList.get(0);
				
				glId.setValue("id:#"+glMetaInfo.getId());
				
				glPostingDatebox.setValue(glMetaInfo.getPostingDate());
				glPostingNumberTextbox.setValue(glMetaInfo.getPostingVoucherNumber().getSerialComp());
				glVoucherNumberTextbox.setValue(glMetaInfo.getVoucherNumber().getSerialComp());
				glVoucherTypeLabel.setValue(glMetaInfo.getVoucherType().toString());
				glDescriptionTextbox.setValue(glMetaInfo.getTransactionDescription());
				glReferenceTextbox.setValue(glMetaInfo.getDocumentRef());
				
				displayGLList(glList);				
			}		
			
			// get the giro and check whether the giro has been cashed (whether there's a voucherPayment)
			Giro giro = voucherGiroReceiptByProxy.getGiro();
			
			log.info(giro.toString());
			
			// voucherPayment not null means giro has been cashed and gl need to be reversed
			boolean isGiroVoucherPayment = (giro.getVoucherPayment()!=null);
			glGiroPaymentTab(isGiroVoucherPayment, giro.getId());
			
		} else {
			// gl from voucherPayment
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPaymentByProxy =
					settlement.getVoucherPayment();
			VoucherPayment voucherPaymentGeneralLedgerByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPaymentByProxy.getId());
			List<GeneralLedger> glList =
					voucherPaymentGeneralLedgerByProxy.getGeneralLedgers();
			// log
			glList.forEach(gl -> log.info(gl.toString()));
			
			if (glList!=null) {
				// get the 1st index
				GeneralLedger glMetaInfo = glList.get(0);
				
				glId.setValue("id:#"+glMetaInfo.getId());
				
				glPostingDatebox.setValue(glMetaInfo.getPostingDate());
				glPostingNumberTextbox.setValue(glMetaInfo.getPostingVoucherNumber().getSerialComp());
				glVoucherNumberTextbox.setValue(glMetaInfo.getVoucherNumber().getSerialComp());
				glVoucherTypeLabel.setValue(glMetaInfo.getVoucherType().toString());
				glDescriptionTextbox.setValue(glMetaInfo.getTransactionDescription());
				glReferenceTextbox.setValue(glMetaInfo.getDocumentRef());
				
				displayGLList(glList);
			}
			
		}
	}

	private void displayGLList(List<GeneralLedger> glList) {
		glListbox.setModel(new ListModelList<GeneralLedger>(glList));
		glListbox.setItemRenderer(getGLListItemRenderer());
	}

	private ListitemRenderer<GeneralLedger> getGLListItemRenderer() {
		
		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(gl.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(gl.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(gl.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(gl);
			}
		};
	}
	
	public void onSelect$statusGLCombobox(Event event) throws Exception {
		DocumentStatus status = statusGLCombobox.getSelectedItem().getValue();
		List<GeneralLedger> reverseDbCrList = null;
		List<GeneralLedger> glList = null;
		
		if (getSettlement().getVoucherGiroReceipt()!=null) {
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceiptByProxy =
					settlement.getVoucherGiroReceipt();
			VoucherGiroReceipt voucherGiroReceiptGeneralLedgerByProxy =
					getVoucherGiroReceiptDao().findGeneralLedgerByProxy(voucherGiroReceiptByProxy.getId());
			glList = 
					voucherGiroReceiptGeneralLedgerByProxy.getGeneralLedgers();
		} else {
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPayment =
					settlement.getVoucherPayment();
			VoucherPayment voucherPaymentGLByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPayment.getId());
			glList = 
					voucherPaymentGLByProxy.getGeneralLedgers();
		}
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusGLCombobox.setStyle("color: red;");

			if (glList!=null) {
				// get the 1st index
				GeneralLedger glMetaInfo = glList.get(0);
				glPembatalanCatatanTextbox.setValue(
						"Pembatalan Settlment No:"+getSettlement().getSettlementNumber().getSerialComp()+
						" ("+glMetaInfo.getTransactionDescription()+")");					
			}
			
			reverseDbCrList = 
					new ArrayList<GeneralLedger>(glList);
			
			for (GeneralLedger gl : glList) {
				String dbcrDescriptionString = gl.getDbcrDescription() +
						" (Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+")";
				
				GeneralLedger reverseGL = new GeneralLedger();
				reverseGL.setPostingDate(gl.getPostingDate());
				reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
				reverseGL.setVoucherNumber(gl.getVoucherNumber());
				reverseGL.setVoucherType(gl.getVoucherType());
				reverseGL.setTransactionDescription(gl.getTransactionDescription());
				reverseGL.setDocumentRef(gl.getDocumentRef());
				reverseGL.setTransactionDate(gl.getTransactionDate());
				
				reverseGL.setMasterCoa(gl.getMasterCoa());
				reverseGL.setDbcrDescription(dbcrDescriptionString);
				
				if (gl.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseGL.setDebitAmount(gl.getCreditAmount());
					reverseGL.setCreditAmount(BigDecimal.ZERO);
				} else {
					reverseGL.setDebitAmount(BigDecimal.ZERO);
					reverseGL.setCreditAmount(gl.getDebitAmount());
				}
			
				reverseDbCrList.add(reverseGL);
			}
			
		} else {
			statusGLCombobox.setStyle("color:black;");
			glPembatalanCatatanTextbox.setValue("");
			
			reverseDbCrList = 
					new ArrayList<GeneralLedger>(glList);
			
		}
			
		// sort by MasterCOA
		reverseDbCrList.sort((o1, o2) -> {
			return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
		});
		// display
		displayGLList(reverseDbCrList);
	}
	
	/*
	 * 
	 * GL - GiroPayment
	 * 
	 */
	private void glGiroPaymentTab(boolean isGiroVoucherPayment, Long giroId) throws Exception {
		glGiroPaymentTab.setVisible(isGiroVoucherPayment);
		
		if (isGiroVoucherPayment) {
			// statusGLPaymentCombobox
			Comboitem comboitem;
			for (DocumentStatus status : DocumentStatus.values()) {
				comboitem = new Comboitem();
				comboitem.setLabel(status.toString());
				comboitem.setValue(status);
				comboitem.setParent(statusGLPaymentCombobox);
			}
			
			statusGLPaymentCombobox.setSelectedIndex(0);
			glGiroPaymentPembatalanDatebox.setValue(asDate(getLocalDate()));
			
			Giro giroVoucherPaymentByProxy = 
					getGiroDao().findVoucherPaymentByProxy(giroId);
			VoucherPayment voucherPaymentByGiro = 
					giroVoucherPaymentByProxy.getVoucherPayment();
			VoucherPayment voucherPaymentGeneralLedgerByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPaymentByGiro.getId());
			List<GeneralLedger> glList =
					voucherPaymentGeneralLedgerByProxy.getGeneralLedgers();
			// log
			glList.forEach(gl -> log.info(gl.toString()));
		
			if (glList!=null) {
				// get the 1st index
				GeneralLedger glMetaInfo = glList.get(0);
				
				glGiroPaymentIdLabel.setValue("id:#"+glMetaInfo.getId());
				
				glGiroPaymentPostingDatebox.setValue(glMetaInfo.getPostingDate());
				glGiroPaymentPostingNumberTextbox.setValue(glMetaInfo.getPostingVoucherNumber().getSerialComp());
				glGiroPaymentVoucherNumberTextbox.setValue(glMetaInfo.getVoucherNumber().getSerialComp());
				glGiroPaymentVoucherTypeLabel.setValue(glMetaInfo.getVoucherType().toString());
				glGiroPaymentDescriptionTextbox.setValue(glMetaInfo.getTransactionDescription());
				glGiroPaymentReferenceTextbox.setValue(glMetaInfo.getDocumentRef());
				
				displayGLGiroPaymentList(glList);
			}			
			
		}
		
	}

	private void displayGLGiroPaymentList(List<GeneralLedger> glList) {
		glGiroPaymentListbox.setModel(new ListModelList<GeneralLedger>(glList));
		glGiroPaymentListbox.setItemRenderer(getGLGiroPaymentListItemRenderer());
	}

	private ListitemRenderer<GeneralLedger> getGLGiroPaymentListItemRenderer() {
		
		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(gl.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(gl.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(gl.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(gl);
			}
		};
	}

	public void onSelect$statusGLPaymentCombobox(Event event) throws Exception {
		DocumentStatus status = statusGLPaymentCombobox.getSelectedItem().getValue();
		List<GeneralLedger> reverseDbCrList = null;
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusGLPaymentCombobox.setStyle("color:red;");
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceipt =
					settlement.getVoucherGiroReceipt();
			Giro giro = 
					getGiroDao().findVoucherPaymentByProxy(voucherGiroReceipt.getId());
			VoucherPayment voucherPayment =
					giro.getVoucherPayment();
			VoucherPayment voucherPaymentGLByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPayment.getId());
			List<GeneralLedger> glList = 
					voucherPaymentGLByProxy.getGeneralLedgers();
			if (glList!=null) {
				// get the 1st index
				GeneralLedger glMetaInfo = glList.get(0);
				glGiroPaymentPembatalanCatatanTextbox.setValue(
						"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+
						" ("+glMetaInfo.getTransactionDescription()+")");					
			}
			
			reverseDbCrList = 
					new ArrayList<GeneralLedger>(glList);
			
			for (GeneralLedger gl : glList) {
				String dbcrDescriptionString = gl.getDbcrDescription() +
						" (Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp()+")";
				
				GeneralLedger reverseGL = new GeneralLedger();
				reverseGL.setPostingDate(gl.getPostingDate());
				reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
				reverseGL.setVoucherNumber(gl.getVoucherNumber());
				reverseGL.setVoucherType(gl.getVoucherType());
				reverseGL.setTransactionDescription(gl.getTransactionDescription());
				reverseGL.setDocumentRef(gl.getDocumentRef());
				reverseGL.setTransactionDate(gl.getTransactionDate());
				
				reverseGL.setMasterCoa(gl.getMasterCoa());
				reverseGL.setDbcrDescription(dbcrDescriptionString);
				
				if (gl.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseGL.setDebitAmount(gl.getCreditAmount());
					reverseGL.setCreditAmount(BigDecimal.ZERO);
				} else {
					reverseGL.setDebitAmount(BigDecimal.ZERO);
					reverseGL.setCreditAmount(gl.getDebitAmount());
				}
			
				reverseDbCrList.add(reverseGL);
			}
			
		} else {
			statusGLPaymentCombobox.setStyle("color:red;");
			glGiroPaymentPembatalanCatatanTextbox.setValue("");

		}
		
		// sort by MasterCOA
		reverseDbCrList.sort((o1, o2) -> {
			return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
		});
		// display
		displayGLGiroPaymentList(reverseDbCrList);
	}
	
	/*
	 * 
	 * PIUTANG
	 * 
	 */
	private void piutangTab() throws Exception {
		Settlement settlementCustomerReceivableByProxy =
				getSettlementDao().findCustomerReceivableByProxy(getSettlement().getId());
		CustomerReceivable customerReceivable =
				settlementCustomerReceivableByProxy.getCustomerReceivable();
		boolean visible = (customerReceivable != null);
		piutangTab.setVisible(visible);
		if (!visible) {
			return;
		}

		// statusPiutangCombobox
		Comboitem comboitem;
		for (DocumentStatus status : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(status.toString());
			comboitem.setValue(status);
			comboitem.setParent(statusPiutangCombobox);
		}
		
		statusPiutangCombobox.setSelectedIndex(0);
		piutangPembatalanDatebox.setValue(asDate(getLocalDate()));
		
		// receivableActivity
		List<CustomerReceivableActivity> receivableActivityList = 
				customerReceivable.getCustomerReceivableActivities();
		// receivableActivity Holder - receivableActivity matching settlementDetail
		List<CustomerReceivableActivity> activityHolderList =
				new ArrayList<CustomerReceivableActivity>();
		
		for (SettlementDetail settlementDetail : getSettlement().getSettlementDetails()) {

			for (CustomerReceivableActivity receivableActivity : receivableActivityList) {
				// find settlementDetail matching the receivableActivity
				if (settlementDetail.getCustomerOrder().getId().compareTo(receivableActivity.getCustomerOrderId())==0) {
					activityHolderList.add(receivableActivity);
					break;
				}
			}
		}
		// log
		activityHolderList.forEach(act -> log.info(act.toString()));
		
		displayCustomerReceivableActivityList(activityHolderList);
	}
	
	private void displayCustomerReceivableActivityList(List<CustomerReceivableActivity> activityHolderList) {
		piutangListbox.setModel(new ListModelList<CustomerReceivableActivity>(activityHolderList));
		piutangListbox.setItemRenderer(getCustomerReceivableActivityItemRenderer());
	}

	private ListitemRenderer<CustomerReceivableActivity> getCustomerReceivableActivityItemRenderer() {
		
		return new ListitemRenderer<CustomerReceivableActivity>() {
			
			@Override
			public void render(Listitem item, CustomerReceivableActivity activity, int index) throws Exception {
				Listcell lc;
				
				Label label;
				Vbox vbox;
				
				CustomerOrder customerOrder =
						getCustomerOrderDao().findCustomerOrderById(activity.getCustomerOrderId());
				List<CustomerOrderProduct> productList =
						customerOrder.getCustomerOrderProducts();
				
				// Order-No.
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(customerOrder.getDocumentSerialNumber().getSerialComp());
				label.setParent(vbox);
				//
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(sht)
					label = new Label();
					label.setValue("Qty[Sht] ");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty (sht)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(Integer.toString(product.getSheetQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);					
					}
				}

				// Tgl.Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(dateToStringDisplay(
						asLocalDate(customerOrder.getOrderDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(kg)
					label = new Label();
					label.setValue("Qty[Kg]");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty(kg)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormatWithDecimal(product.getWeightQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);
					}
				}
				
				// Tgl.Jatuh-Tempo
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				LocalDate paymentDueDate = addDate(customerOrder.getCreditDay(), 
						asLocalDate(customerOrder.getOrderDate()));
				label.setValue(dateToStringDisplay(paymentDueDate, getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: kode
					label = new Label();
					label.setValue("Kode");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: kode
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getUserModInventoryCode());
						label.setStyle("white-space:nowrap;");
						label.setParent(vbox);					
					}
				}
				
				// Total-Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountSales()));
				label.setStyle("float: right;");
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: spesifikasi
					label = new Label();
					label.setValue("Spesifikasi");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: spesifikasi
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getDescription());
						label.setParent(vbox);
					}
				}
				
				// Tgl.Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getPaymentDate()==null? " - " :
						dateToStringDisplay(asLocalDate(activity.getPaymentDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: harga(rp.)
					label = new Label();
					label.setValue("Harga(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: harga
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingPrice())+(product.isByKg()? "/KG":"/SHT"));
						label.setStyle("float:right;");
						label.setParent(vbox);
					}
					// subTotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setParent(vbox);
					// ppn
					label = new Label();
					label.setValue("PPN 10%");
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue("Total(Rp.)");
					label.setParent(vbox);
				}
				
				// Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountPaid()));
				label.setStyle("float:right;");
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: subtotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					BigDecimal subTotal = BigDecimal.ZERO;
					BigDecimal ppn = BigDecimal.ZERO;
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingSubTotal()));
						label.setStyle("float:right;");
						label.setParent(vbox);
						
						subTotal = subTotal.add(product.getSellingSubTotal());
					}
					// subTotal
					label = new Label();
					label.setStyle("border-top: 2px dotted red;float:right;");
					label.setValue(toLocalFormat(subTotal)); // "999.999.999,-"
					label.setParent(vbox);
					ppn = subTotal.multiply(new BigDecimal(0.1));
					// ppn
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(ppn):"-");
					label.setStyle("float:right");
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(subTotal.add(ppn)):toLocalFormat(subTotal));
					label.setStyle("float:right");
					label.setParent(vbox);
				}
				
				// Sisa
				lc = new Listcell();
				lc.setStyle("vertical-align:top;float:right;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getRemainingAmount()));
				label.setParent(vbox);
				
				
				// Status
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				if (activity.getReceivableStatus().equals(DocumentStatus.BATAL)) {
					label.setValue("BATAL");
					label.setStyle("color:red;");
				} else {
					label.setValue(activity.isPaymentComplete() ? "Lunas" : " - ");
				}
				label.setParent(vbox);
				
				// activityId
				lc = new Listcell("id:#"+activity.getId());
				lc.setParent(item);
				
				item.setValue(activity);
			}
		};
	}

	public void onSelect$statusPiutangCombobox(Event event) throws Exception {
		DocumentStatus status = statusPiutangCombobox.getSelectedItem().getValue();

		Settlement settlementCustomerReceivableByProxy =
				getSettlementDao().findCustomerReceivableByProxy(getSettlement().getId());
		CustomerReceivable customerReceivable =
				settlementCustomerReceivableByProxy.getCustomerReceivable();
		// receivableActivity
		List<CustomerReceivableActivity> receivableActivityList = 
				customerReceivable.getCustomerReceivableActivities();
		// receivableActivity Holder - receivableActivity matching settlementDetail
		List<CustomerReceivableActivity> activityHolderList =
				new ArrayList<CustomerReceivableActivity>();

		for (SettlementDetail settlementDetail : getSettlement().getSettlementDetails()) {

			for (CustomerReceivableActivity receivableActivity : receivableActivityList) {
				// find settlementDetail matching the receivableActivity
				if (settlementDetail.getCustomerOrder().getId().compareTo(receivableActivity.getCustomerOrderId())==0) {
					activityHolderList.add(receivableActivity);
					break;
				}
			}
		}

		if (status.equals(DocumentStatus.BATAL)) {
			statusPiutangCombobox.setStyle("color:red;");
			piutangPembatalanCatatanTextbox.setValue(
					"Pembatalan Settlement No:"+getSettlement().getSettlementNumber().getSerialComp());
			
			for (CustomerReceivableActivity activity : activityHolderList) {
				activity.setReceivableStatus(DocumentStatus.BATAL);
			}
			
		} else {
			statusPiutangCombobox.setStyle("color:black;");
			piutangPembatalanCatatanTextbox.setValue("");

			for (CustomerReceivableActivity activity : activityHolderList) {
				activity.setReceivableStatus(DocumentStatus.NORMAL);
			}
		}
		// display
		displayCustomerReceivableActivityList(activityHolderList);
	}
	
	public void onClick$saveBatalButton(Event event) throws Exception {
		Tabs tabs = settlementBatalTabbox.getTabs();
		List<Tab> tabList = tabs.getChildren();
		List<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : tabList) {
			if (tab.isVisible()) {
				activeTabs.add(tab);
			}
		}
		activeTabs.forEach(tab->log.info(tab.getId()));
		
		boolean confirm = false;
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				// check statusSettlementCombobox
				confirm = statusSettlementCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("voucherTab")==0) {
				// statusVoucherCombobox
				confirm = statusVoucherCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("giroTab")==0) {
				// statusGiroCombobox
				confirm = statusGiroCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("giroPaymentTab")==0) {
				// statusGiroPaymentCombobox
				confirm = statusGiroPaymentCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("glTab")==0) {
				// statusGLCombobox
				confirm = statusGLCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("glGiroPaymentTab")==0) {
				// statusGLPaymentCombobox
				confirm = statusGLPaymentCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("piutangTab")==0) {
				// statusPiutangCombobox
				confirm = statusPiutangCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			}
			tab.setAttribute("confirm", confirm);								
		}
		activeTabs.forEach(tab->log.info(tab.getAttribute("confirm")));

		// check whether all tabs are confirmed -- if not confirmed throw exception
		for (Tab tab : activeTabs) {
			if (tab.getAttribute("confirm")==null) {
				continue;
			}			
			if (!(boolean) tab.getAttribute("confirm")) {
				throw new SuppressedException(tab.getLabel()+" NOT set to 'BATAL'", true);
			}
		}
		
		// update
		updatePembatalan(activeTabs);
		
		settlementDialogBatalWin.detach();
	}
	
	private void updatePembatalan(List<Tab> activeTabs) throws Exception {	
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				// settlement
				Settlement modSettlement = getModifiedSettlement();
				getSettlementDao().update(modSettlement);
				// verify
				long modSettlementId = modSettlement.getId();
				modSettlement = getSettlementDao().findSettlementById(modSettlementId);
				log.info("Settlement Id: "+modSettlement.getId()+
					" Settlement Status: "+modSettlement.getSettlementStatus().toString()+
					" Batal Date: "+modSettlement.getBatalDate()+
					" Batal Note: "+modSettlement.getBatalNote());
			} else if (tab.getId().compareTo("voucherTab")==0) {
				if (getSettlement().getVoucherGiroReceipt()!=null) {
					// voucherGiroReceipt
					VoucherGiroReceipt modVoucherGiroReceipt = getModifiedVoucherGiroReceipt();
					getVoucherGiroReceiptDao().update(modVoucherGiroReceipt);
					// verify
					long modVoucherGiroReceiptId = modVoucherGiroReceipt.getId();
					modVoucherGiroReceipt = getVoucherGiroReceiptDao().findVoucherGiroReceiptById(modVoucherGiroReceiptId);
					log.info("VoucherGiroReceipt Id: "+modVoucherGiroReceipt.getId()+
							" VoucherGiroReceipt Status: "+modVoucherGiroReceipt.getVoucherStatus().toString()+
							" Batal Date: "+modVoucherGiroReceipt.getBatalDate()+
							" Batal Note: "+modVoucherGiroReceipt.getBatalNote());
					// additional logs - reversed db/cr in the details
					modVoucherGiroReceipt.getVoucherGiroReceiptDebitCredits().forEach(dbcr ->
						log.info(dbcr.toString()));
				} else {
					// voucherPayment
					VoucherPayment modVoucherPayment = getModifiedVoucherPayment();
					getVoucherPaymentDao().update(modVoucherPayment);
					// verify
					long modVoucherPaymentId = modVoucherPayment.getId();
					modVoucherPayment = getVoucherPaymentDao().findVoucherPaymentById(modVoucherPaymentId);
					log.info("VoucherGiroReceipt Id: "+modVoucherPayment.getId()+
							" VoucherGiroReceipt Status: "+modVoucherPayment.getVoucherStatus().toString()+
							" Batal Date: "+modVoucherPayment.getBatalDate()+
							" Batal Note: "+modVoucherPayment.getBatalNote());
					// additional logs - reversed db/cr in the details
					modVoucherPayment.getVoucherPaymentDebitCredits().forEach(dbcr ->
						log.info(dbcr.toString()));
				}
			} else if (tab.getId().compareTo("giroTab")==0) {
				// giro
				Giro modGiro = getModifiedGiro();
				getGiroDao().update(modGiro);
				// verify
				long modGiroId = modGiro.getId();
				modGiro = getGiroDao().findGiroById(modGiroId);
				log.info("Giro Id: "+modGiro.getId()+
					" Giro Status: "+modGiro.getGiroStatus().toString()+
					" Batal Date: "+modGiro.getBatalDate()+
					" Batal Note: "+modGiro.getBatalNote());
			} else if (tab.getId().compareTo("giroPaymentTab")==0) {
				// giro VoucherPayment
				VoucherPayment modVoucherPayment = getModifiedGiroVoucherPayment();
				getVoucherPaymentDao().update(modVoucherPayment);
				// verify
				long modVoucherPaymentId = modVoucherPayment.getId();
				modVoucherPayment = getVoucherPaymentDao().findVoucherPaymentById(modVoucherPaymentId);
				log.info("VoucherPayment Id: "+modVoucherPayment.getId()+
					" VoucherPayment Status: "+modVoucherPayment.getVoucherStatus().toString()+
					" Batal Date: "+modVoucherPayment.getBatalDate()+
					" Batal Note: "+modVoucherPayment.getBatalNote());
				// additonal logs - reversed db/cr in the details
				modVoucherPayment.getVoucherPaymentDebitCredits().forEach(dbcr ->
					log.info(dbcr.toString()));
			} else if (tab.getId().compareTo("glTab")==0) {
				// GL
				if (getSettlement().getVoucherGiroReceipt()!=null) {
					// GL from VoucherGiroReceipt
					VoucherGiroReceipt modVoucherGiroReceiptGeneralLedger = getModifiedVoucherGiroReceiptGeneralLedger();
					getVoucherGiroReceiptDao().update(modVoucherGiroReceiptGeneralLedger);
					// verify
					long modVoucherGiroReceiptId = modVoucherGiroReceiptGeneralLedger.getId();
					modVoucherGiroReceiptGeneralLedger = getVoucherGiroReceiptDao().findVoucherGiroReceiptById(modVoucherGiroReceiptId);
					// GL
					VoucherGiroReceipt glByProxy = 
							getVoucherGiroReceiptDao().findGeneralLedgerByProxy(modVoucherGiroReceiptGeneralLedger.getId());
					glByProxy.getGeneralLedgers().forEach(gl ->
						log.info(gl.toString()));
				} else {
					// GL from VoucherPayment
					VoucherPayment modVoucherPaymentGeneralLedger = getModifiedVoucherPaymentGeneralLedger();
					getVoucherPaymentDao().update(modVoucherPaymentGeneralLedger);
					// verify
					long modVoucherPaymentId = modVoucherPaymentGeneralLedger.getId();
					modVoucherPaymentGeneralLedger = getVoucherPaymentDao().findVoucherPaymentById(modVoucherPaymentId);
					// GL
					VoucherPayment glByProxy =
							getVoucherPaymentDao().findGeneralLedgerByProxy(modVoucherPaymentGeneralLedger.getId());
					glByProxy.getGeneralLedgers().forEach(gl ->
						log.info(gl.toString()));
				}
			} else if (tab.getId().compareTo("glGiroPaymentTab")==0) {
				// GL - GiroVoucherPayment
				VoucherPayment modVoucherPaymentGeneralLedger = getModifiedGiroVoucherPaymentGeneralLedger();
				getVoucherPaymentDao().update(modVoucherPaymentGeneralLedger);
				// verify
				long modVoucherPaymentId = modVoucherPaymentGeneralLedger.getId();
				modVoucherPaymentGeneralLedger = getVoucherPaymentDao().findVoucherPaymentById(modVoucherPaymentId);
				// GL
				VoucherPayment glByProxy =
						getVoucherPaymentDao().findGeneralLedgerByProxy(modVoucherPaymentGeneralLedger.getId());
				glByProxy.getGeneralLedgers().forEach(gl ->
						log.info(gl.toString()));
			} else if (tab.getId().compareTo("piutangTab")==0) {
				// Piutang
				CustomerReceivable receivable = getModifiedCustomerReceivableActivities();
				getCustomerReceivableDao().update(receivable);
				// verify
				long modCustomerReceivableId = receivable.getId();
				receivable = getCustomerReceivableDao().findCustomerReceivableById(modCustomerReceivableId);
				receivable.getCustomerReceivableActivities().forEach(activity ->
						log.info(activity.toString()));
			}
		}
	}

	public void onClick$saveRevertButton(Event event) throws Exception {
		log.info("revert back to normal");
		
		Tabs tabs = settlementBatalTabbox.getTabs();
		List<Tab> tabList = tabs.getChildren();
		List<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : tabList) {
			if (tab.isVisible()) {
				activeTabs.add(tab);
			}
		}
		activeTabs.forEach(tab->log.info(tab.getId()));
		
		boolean confirm = false;
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				// check statusSettlementCombobox
				confirm = statusSettlementCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("voucherTab")==0) {
				// statusVoucherCombobox
				confirm = statusVoucherCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("giroTab")==0) {
				// statusGiroCombobox
				confirm = statusGiroCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("giroPaymentTab")==0) {
				// statusGiroPaymentCombobox
				confirm = statusGiroPaymentCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			}
			tab.setAttribute("confirm", confirm);								
		}
		activeTabs.forEach(tab->log.info(tab.getAttribute("confirm")));

		// check whether all tabs are confirmed -- if not confirmed throw exception
		for (Tab tab : activeTabs) {
			if (tab.getAttribute("confirm")==null) {
				continue;
			}			
			if (!(boolean) tab.getAttribute("confirm")) {
				throw new SuppressedException(tab.getLabel()+" NOT set to 'NORMAL'", true);
			}
		}
		
		// update
		updatePembatalan(activeTabs);

	}
	
	/**
	 * Settlement - Settlement, SettlementDetail, CustomerOrder
	 * @return
	 */
	private Settlement getModifiedSettlement() {
		Settlement modSettlement = getSettlement();
		
		// amountPaid not set to ZERO, only status change to BATAL
		// modSettlement.setAmountPaid(BigDecimal.ZERO);
		modSettlement.setSettlementStatus(statusSettlementCombobox.getSelectedItem().getValue());
		modSettlement.setBatalDate(pembatalanSettlementDatebox.getValue());
		modSettlement.setBatalNote(pembatalanCatatanSettlementTextbox.getValue());
		
		return modSettlement;
	}
	
	private VoucherGiroReceipt getModifiedVoucherGiroReceipt() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt modVoucherGiroReceipt = settlement.getVoucherGiroReceipt();
		
		modVoucherGiroReceipt.setVoucherStatus(statusVoucherCombobox.getSelectedItem().getValue());
		modVoucherGiroReceipt.setBatalDate(pembatalanVoucherDatebox.getValue());
		modVoucherGiroReceipt.setBatalNote(pembatalanVoucherCatatanTextbox.getValue());
		
		// clear the details list
		// read from listbox and add into the details list
		modVoucherGiroReceipt.getVoucherGiroReceiptDebitCredits().clear();
		
		for (Listitem item : voucherDbcrVoucherPaymentGiroReceiptListbox.getItems()) {
			VoucherGiroReceiptDebitCredit dbcr = item.getValue();
			
			VoucherGiroReceiptDebitCredit reverseDbCr = new VoucherGiroReceiptDebitCredit();
			reverseDbCr.setCreditAmount(dbcr.getCreditAmount());
			reverseDbCr.setDebitAmount(dbcr.getDebitAmount());
			reverseDbCr.setDbcrDescription(dbcr.getDbcrDescription());
			reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
			
			modVoucherGiroReceipt.getVoucherGiroReceiptDebitCredits().add(reverseDbCr);
		}
		
		return modVoucherGiroReceipt;
	}

	private VoucherPayment getModifiedVoucherPayment() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
		VoucherPayment modVoucherPayment = settlement.getVoucherPayment();
		
		modVoucherPayment.setVoucherStatus(statusVoucherCombobox.getSelectedItem().getValue());
		modVoucherPayment.setBatalDate(pembatalanVoucherDatebox.getValue());
		modVoucherPayment.setBatalNote(pembatalanVoucherCatatanTextbox.getValue());

		// clear the details list
		// read from listbox and add into the details list		
		modVoucherPayment.getVoucherPaymentDebitCredits().clear();
		
		for (Listitem item : voucherDbcrVoucherPaymentGiroReceiptListbox.getItems()) {
			VoucherPaymentDebitCredit dbcr = item.getValue();
			
			VoucherPaymentDebitCredit reverseDbCr = new VoucherPaymentDebitCredit();
			reverseDbCr.setCreditAmount(dbcr.getCreditAmount());
			reverseDbCr.setDebitAmount(dbcr.getDebitAmount());
			reverseDbCr.setDbcrDescription(dbcr.getDbcrDescription());
			reverseDbCr.setMasterCoa(dbcr.getMasterCoa());

			modVoucherPayment.getVoucherPaymentDebitCredits().add(reverseDbCr);
		}
		
		return modVoucherPayment;
	}
	
	private Giro getModifiedGiro() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt voucherGiroReceiptByProxy = 
				settlement.getVoucherGiroReceipt();
		
		// get the giro and check whether the giro has been cashed
		Giro modGiro = voucherGiroReceiptByProxy.getGiro();
 
		modGiro.setGiroStatus(statusGiroCombobox.getSelectedItem().getValue());
		modGiro.setBatalDate(pembatalanGiroDatebox.getValue());
		modGiro.setBatalNote(giroPembatalanCatatanTextbox.getValue());
		
		return modGiro;
	}	
	
	private VoucherPayment getModifiedGiroVoucherPayment() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt voucherGiroReceiptByProxy = 
				settlement.getVoucherGiroReceipt();
		Giro giro = voucherGiroReceiptByProxy.getGiro();
		Giro giroVoucherPaymentByProxyGiro =
				getGiroDao().findVoucherPaymentByProxy(giro.getId());
		
		VoucherPayment modVoucherPayment = giroVoucherPaymentByProxyGiro.getVoucherPayment();
		
		modVoucherPayment.setVoucherStatus(statusGiroPaymentCombobox.getSelectedItem().getValue());
		modVoucherPayment.setBatalDate(pembatalanGiroPaymentDatebox.getValue());
		modVoucherPayment.setBatalNote(giroPaymentPembatalanCatatanTextbox.getValue());
		
		modVoucherPayment.getVoucherPaymentDebitCredits().clear();
		
		for (Listitem item : voucherDbcrGiroPaymentListbox.getItems()) {
			VoucherPaymentDebitCredit dbcr = item.getValue();
			
			VoucherPaymentDebitCredit reverseDbCr = new VoucherPaymentDebitCredit();
			reverseDbCr.setCreditAmount(dbcr.getCreditAmount());
			reverseDbCr.setDebitAmount(dbcr.getDebitAmount());
			reverseDbCr.setDbcrDescription(dbcr.getDbcrDescription());
			reverseDbCr.setMasterCoa(dbcr.getMasterCoa());

			modVoucherPayment.getVoucherPaymentDebitCredits().add(reverseDbCr);
		}
		
		return modVoucherPayment;
	}
	
	private VoucherGiroReceipt getModifiedVoucherGiroReceiptGeneralLedger() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt voucherGiroReceipt = settlement.getVoucherGiroReceipt();
		VoucherGiroReceipt voucherGiroReceiptGLByProxy =
				getVoucherGiroReceiptDao().findGeneralLedgerByProxy(voucherGiroReceipt.getId());
		
		voucherGiroReceiptGLByProxy.getGeneralLedgers().clear();
		
		for (Listitem item : glListbox.getItems()) {
			GeneralLedger gl = item.getValue();
			
			GeneralLedger reverseGL = new GeneralLedger();
			reverseGL.setPostingDate(gl.getPostingDate());
			reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
			reverseGL.setVoucherNumber(gl.getVoucherNumber());
			reverseGL.setVoucherType(gl.getVoucherType());
			reverseGL.setTransactionDescription(gl.getTransactionDescription());
			reverseGL.setDocumentRef(gl.getDocumentRef());
			reverseGL.setTransactionDate(gl.getTransactionDate());
			
			reverseGL.setCreditAmount(gl.getCreditAmount());
			reverseGL.setDebitAmount(gl.getDebitAmount());
			reverseGL.setDbcrDescription(gl.getDbcrDescription());
			reverseGL.setMasterCoa(gl.getMasterCoa());
			
			voucherGiroReceiptGLByProxy.getGeneralLedgers().add(reverseGL);
		}
		
		return voucherGiroReceiptGLByProxy;
	}
	
	private VoucherPayment getModifiedVoucherPaymentGeneralLedger() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
		VoucherPayment voucherPayment = settlement.getVoucherPayment();
		VoucherPayment voucherPaymentGLByProxy =
				getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPayment.getId());
		
		voucherPaymentGLByProxy.getGeneralLedgers().clear();
		
		for (Listitem item : glListbox.getItems()) {
			GeneralLedger gl = item.getValue();
			
			GeneralLedger reverseGL = new GeneralLedger();
			reverseGL.setPostingDate(gl.getPostingDate());
			reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
			reverseGL.setVoucherNumber(gl.getVoucherNumber());
			reverseGL.setVoucherType(gl.getVoucherType());
			reverseGL.setTransactionDescription(gl.getTransactionDescription());
			reverseGL.setDocumentRef(gl.getDocumentRef());
			reverseGL.setTransactionDate(gl.getTransactionDate());
			
			reverseGL.setCreditAmount(gl.getCreditAmount());
			reverseGL.setDebitAmount(gl.getDebitAmount());
			reverseGL.setDbcrDescription(gl.getDbcrDescription());
			reverseGL.setMasterCoa(gl.getMasterCoa());
			
			voucherPaymentGLByProxy.getGeneralLedgers().add(reverseGL);
		}		
		
		return voucherPaymentGLByProxy;
	}

	private VoucherPayment getModifiedGiroVoucherPaymentGeneralLedger() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt voucherGiroReceiptByProxy = 
				settlement.getVoucherGiroReceipt();
		Giro giro = voucherGiroReceiptByProxy.getGiro();
		Giro giroVoucherPaymentByProxy = 
				getGiroDao().findVoucherPaymentByProxy(giro.getId());
		VoucherPayment voucherPayment = giroVoucherPaymentByProxy.getVoucherPayment();
		VoucherPayment voucherPaymentGLByProxy =
				getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPayment.getId());
		
		voucherPaymentGLByProxy.getGeneralLedgers().clear();
		
		for (Listitem item : glGiroPaymentListbox.getItems()) {
			GeneralLedger gl = item.getValue();
			
			GeneralLedger reverseGL = new GeneralLedger();
			reverseGL.setPostingDate(gl.getPostingDate());
			reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
			reverseGL.setVoucherNumber(gl.getVoucherNumber());
			reverseGL.setVoucherType(gl.getVoucherType());
			reverseGL.setTransactionDescription(gl.getTransactionDescription());
			reverseGL.setDocumentRef(gl.getDocumentRef());
			reverseGL.setTransactionDate(gl.getTransactionDate());
			
			reverseGL.setCreditAmount(gl.getCreditAmount());
			reverseGL.setDebitAmount(gl.getDebitAmount());
			reverseGL.setDbcrDescription(gl.getDbcrDescription());
			reverseGL.setMasterCoa(gl.getMasterCoa());
			
			voucherPaymentGLByProxy.getGeneralLedgers().add(reverseGL);			
		}
		
		return voucherPaymentGLByProxy;
	}
	
	private CustomerReceivable getModifiedCustomerReceivableActivities() throws Exception {
		Settlement settlementCustomerReceivableByProxy =
				getSettlementDao().findCustomerReceivableByProxy(getSettlement().getId());
		CustomerReceivable customerReceivable =
				settlementCustomerReceivableByProxy.getCustomerReceivable();

		for (Listitem item : piutangListbox.getItems()) {
			CustomerReceivableActivity activity = item.getValue();
			
			for (CustomerReceivableActivity receivableActivity : customerReceivable.getCustomerReceivableActivities()) {
				
				if (activity.getId().compareTo(receivableActivity.getId())==0) {
					receivableActivity.setPaymentDate(null);
					receivableActivity.setAmountPaid(BigDecimal.ZERO);
					receivableActivity.setAmountPaidPpn(BigDecimal.ZERO);
					receivableActivity.setPaymentComplete(false);
					receivableActivity.setRemainingAmount(BigDecimal.ZERO);
					receivableActivity.setPaymentDescription("Pembatalan Settlement No.:"+
							getSettlement().getSettlementNumber().getSerialComp());
					
					break;
				}
				
			}
			
		}
		
		return customerReceivable;
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		
		settlementDialogBatalWin.detach();
	}
	
	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}

	public VoucherGiroReceiptDao getVoucherGiroReceiptDao() {
		return voucherGiroReceiptDao;
	}

	public void setVoucherGiroReceiptDao(VoucherGiroReceiptDao voucherGiroReceiptDao) {
		this.voucherGiroReceiptDao = voucherGiroReceiptDao;
	}

	public GiroDao getGiroDao() {
		return giroDao;
	}

	public void setGiroDao(GiroDao giroDao) {
		this.giroDao = giroDao;
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}
}
