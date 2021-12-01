package com.pyramix.swi.webui.report;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
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

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherPaymentDebitCredit;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;


public class ReportSalesVoucherPaymentDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231518L;
	
	private VoucherPaymentDao voucherPaymentDao;
	
	private Window reportSalesVoucherPaymentDialogWin;
	private Textbox settlementNumberTextbox, voucherNoCompTextbox, voucherNoPostTextbox,
		paidByInfoTextbox, customerTextbox, theSumOfTextbox, descriptionTextbox, referenceTextbox;
	private Datebox transactionDatebox;
	private Combobox paidByCombobox, voucherStatusCombobox;
	private Label paidByLabel, voucherTypeLabel;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private SettlementData settlementData;
	private Settlement settlement;
	private VoucherPayment voucherPayment;
	
	private final Logger log = Logger.getLogger(ReportSalesVoucherPaymentDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setSettlementData((SettlementData) arg.get("settlementData"));
	}

	public void onCreate$reportSalesVoucherPaymentDialogWin(Event event) throws Exception {
		log.info("Creating voucher payment dialog");
		
		// settlement
		setSettlement( 
				getSettlementData().getSettlement());
		
		// voucherPayment
		setVoucherPayment( 
				getSettlementData().getVoucherPayment());
		
		reportSettlementForVoucherPayment();
		
		reportVoucherPaymentInfo();
	}
		
	private void reportSettlementForVoucherPayment() {
		settlementNumberTextbox.setValue(
				getSettlement().getSettlementNumber().getSerialComp());
	}
	
	public void onClick$settlementButton(Event event) throws Exception {
		// pass the settlement data
		Map<String, Settlement> arg = Collections.singletonMap("settlement", getSettlement());
		
		// settlement dialog window
		Window settlementWin = 
				(Window) Executions.createComponents("/report/ReportSalesSettlementDialog.zul", null, arg);
		
		settlementWin.doModal();
	}
	
	private void reportVoucherPaymentInfo() throws Exception {
		voucherNoCompTextbox.setValue(getVoucherPayment().getVoucherNumber().getSerialComp());
		// posting voucher number requires proxy
		VoucherPayment voucherPaymentPostProxy =
				getVoucherPaymentDao().findPostingVoucherNumberByProxy(getVoucherPayment().getId());
		voucherNoPostTextbox.setValue(voucherPaymentPostProxy.getPostingVoucherNumber().getSerialComp());
		transactionDatebox.setValue(getVoucherPayment().getTransactionDate());
		paidByCombobox.setValue(getVoucherPayment().getPaidBy().toString());
		// always paid by 'Bank', thereby display 'Bank Info'
		paidByLabel.setValue("Bank Info:");
		paidByInfoTextbox.setValue(getVoucherPayment().getPaidByNote());
		voucherTypeLabel.setValue(getVoucherPayment().getVoucherType().toString());
		voucherStatusCombobox.setValue(getVoucherPayment().getFlowStatus().toString());
		// customer requres proxy
		VoucherPayment voucherPaymentCustByProxy =
				getVoucherPaymentDao().findCustomerByProxy(getVoucherPayment().getId());
		customerTextbox.setValue(voucherPaymentCustByProxy.getCustomer().getCompanyType().toString()+"."+
				voucherPaymentCustByProxy.getCustomer().getCompanyLegalName());
		theSumOfTextbox.setValue(toLocalFormat(getVoucherPayment().getTheSumOf()));
		descriptionTextbox.setValue(getVoucherPayment().getTransactionDescription());
		referenceTextbox.setValue(getVoucherPayment().getDocumentRef());
		
		// details
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherPaymentDebitCredit>(
						getVoucherPayment().getVoucherPaymentDebitCredits()));
		voucherDbcrListbox.setItemRenderer(getVoucherPaymentDebitCreditListitemRenderer());
		
		// calculate total
		calculateAndDisplayTotal(getVoucherPayment().getVoucherPaymentDebitCredits());
	}
	
	private ListitemRenderer<VoucherPaymentDebitCredit> getVoucherPaymentDebitCreditListitemRenderer() {
		
		return new ListitemRenderer<VoucherPaymentDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherPaymentDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setParent(item);
				
				// Debit
				lc = new Listcell(toLocalFormat(dbcr.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit
				lc = new Listcell(toLocalFormat(dbcr.getCreditAmount()));
				lc.setParent(item);
				
			}
		};
		
	}
	
	private void calculateAndDisplayTotal(List<VoucherPaymentDebitCredit> voucherPaymentDbCr) {
		BigDecimal totalDb = BigDecimal.ZERO;
		BigDecimal totalCr = BigDecimal.ZERO;
		
		for (VoucherPaymentDebitCredit dbcr : voucherPaymentDbCr) {
			totalDb = totalDb.add(dbcr.getDebitAmount());
			totalCr = totalCr.add(dbcr.getCreditAmount());
		}
		
		totalDebitListfooter.setLabel(toLocalFormat(totalDb));
		totalCreditListfooter.setLabel(toLocalFormat(totalCr));
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		reportSalesVoucherPaymentDialogWin.detach();
	}

	public SettlementData getSettlementData() {
		return settlementData;
	}

	public void setSettlementData(SettlementData settlementData) {
		this.settlementData = settlementData;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public VoucherPayment getVoucherPayment() {
		return voucherPayment;
	}

	public void setVoucherPayment(VoucherPayment voucherPayment) {
		this.voucherPayment = voucherPayment;
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}
}
