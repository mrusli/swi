package com.pyramix.swi.webui.voucher;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
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

import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.webui.common.GFCBaseController;

public class VoucherJournalViewDialogControl extends GFCBaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2068483158587201290L;

	private Window voucherJournalViewDialogWin;
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
	private boolean hasRoleManager = false;
	
	private final VoucherType GENERAL_VOUCHER 		= VoucherType.GENERAL;
	private final VoucherType PETTYCASH_VOUCHER 	= VoucherType.PETTYCASH;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setVoucherJournal(
				(VoucherJournal) arg.get("voucherJournal"));
	}
	
	public void onCreate$voucherJournalViewDialogWin(Event event) throws Exception {
		voucherJournalViewDialogWin.setTitle(
				"Detail (View) Voucher Umum dan PettyCash");
		
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
	


	private void setVoucherJournalDebitCreditInfo() {
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherJournalDebitCredit>(
						getVoucherJournalDebitCreditList()));
		voucherDbcrListbox.setItemRenderer(
				getVoucherDbcrListitemRenderer());
		
	}

	private ListitemRenderer<VoucherJournalDebitCredit> getVoucherDbcrListitemRenderer() {

		totalDebitVal 	= BigDecimal.ZERO;
		totalCreditVal 	= BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherJournalDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherJournalDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaComp());
						// initCoaNumber(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(voucherDbCr.getDbcrDescription());
				lc.setParent(item);
				
				// Debit
				lc = new Listcell(toLocalFormatWithDecimal(voucherDbCr.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit
				lc = new Listcell(toLocalFormatWithDecimal(voucherDbCr.getCreditAmount()));
				lc.setParent(item);				
				
				totalDebitVal = totalDebitVal.add(voucherDbCr.getDebitAmount());
				totalCreditVal = totalCreditVal.add(voucherDbCr.getCreditAmount());
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
	
	public void onClick$closeButton(Event event) throws Exception {
		voucherJournalViewDialogWin.detach();
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

	public List<VoucherJournalDebitCredit> getVoucherJournalDebitCreditList() {
		return voucherJournalDebitCreditList;
	}

	public void setVoucherJournalDebitCreditList(List<VoucherJournalDebitCredit> voucherJournalDebitCreditList) {
		this.voucherJournalDebitCreditList = voucherJournalDebitCreditList;
	}

	public boolean isRoleManager() {
		return hasRoleManager;
	}

	public void setHasRoleManager(boolean hasRoleManager) {
		this.hasRoleManager = hasRoleManager;
	}
}
