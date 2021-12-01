package com.pyramix.swi.webui.report.panel;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSalesDebitCredit;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class PanelVoucherSalesControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231512L;

	private VoucherSalesDao voucherSalesDao;
	
	private Textbox voucherNoCompTextbox, voucherNoPostTextbox, customerTextbox,
		theSumOfTextbox, ppnAmountTextbox, referenceTextbox, descriptionTextbox;
	private Datebox transactionDatebox;
	private Combobox pembayaranCombobox, voucherStatusCombobox;
	private Intbox jumlahHariIntbox;
	private Label voucherTypeLabel, infoDebitCreditlabel;
	private Checkbox usePpnCheckbox;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private final Logger log = Logger.getLogger(PanelVoucherSalesControl.class);
	
	private VoucherSales voucherSales;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// voucherSales
		setVoucherSales((VoucherSales) arg.get("voucherSales"));
	}

	public void onCreate$panelVoucherSalesDiv(Event event) throws Exception {		
		log.info("Creating panelVoucherSalesDiv with: "+getVoucherSales().toString());

		// set datebox format
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
		
		voucherNoCompTextbox.setValue(getVoucherSales().getVoucherNumber().getSerialComp());
		voucherNoPostTextbox.setValue(getVoucherSales().getPostingVoucherNumber().getSerialComp());
		transactionDatebox.setValue(getVoucherSales().getTransactionDate());
		pembayaranCombobox.setValue(getVoucherSales().getPaymentType().toString());
		jumlahHariIntbox.setValue(getVoucherSales().getJumlahHari());
		voucherTypeLabel.setValue(getVoucherSales().getVoucherType().toString());
		voucherStatusCombobox.setValue(getVoucherSales().getFlowStatus().toString());
		// customer requires proxy
		VoucherSales voucherSalesCustByProxy = 
				getVoucherSalesDao().findCustomerByProxy(getVoucherSales().getId());
		customerTextbox.setValue(voucherSalesCustByProxy.getCustomer().getCompanyType()+"."+
				voucherSalesCustByProxy.getCustomer().getCompanyLegalName());
		theSumOfTextbox.setValue(toLocalFormat(getVoucherSales().getTheSumOf()));
		usePpnCheckbox.setChecked(getVoucherSales().isUsePpn());
		ppnAmountTextbox.setValue(toLocalFormat(getVoucherSales().getPpnAmount()));
		referenceTextbox.setValue(getVoucherSales().getDocumentRef());
		descriptionTextbox.setValue(getVoucherSales().getTransactionDescription());
		// detail
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherSalesDebitCredit>(
						getVoucherSales().getVoucherSalesDebitCredits()));
		voucherDbcrListbox.setItemRenderer(getVoucherSalesDebitCreditListitemRenderer());
		// info
		infoDebitCredit();
	}

	private ListitemRenderer<VoucherSalesDebitCredit> getVoucherSalesDebitCreditListitemRenderer() {
		
		return new ListitemRenderer<VoucherSalesDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherSalesDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setStyle("white-space: nowrap;");
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
	
	private void infoDebitCredit() {
		int itemCount = 
				getVoucherSales().getVoucherSalesDebitCredits().size();
		
		infoDebitCreditlabel.setValue("Debit/Kredit: "+itemCount+" items");
		
		// total
		BigDecimal totalDebit = BigDecimal.ZERO;
		BigDecimal totalCredit = BigDecimal.ZERO;
		for (VoucherSalesDebitCredit dbcr : getVoucherSales().getVoucherSalesDebitCredits()) {
			totalDebit = totalDebit.add(dbcr.getDebitAmount());
			totalCredit = totalCredit.add(dbcr.getCreditAmount());
		}
		
		totalDebitListfooter.setLabel(toLocalFormat(totalDebit));
		totalCreditListfooter.setLabel(toLocalFormat(totalCredit));
	}
	
	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

}
