package com.pyramix.swi.webui.faktur;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;

import com.pyramix.swi.domain.bank.Bank;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.faktur.FakturProduct;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PrintUtil;

public class FakturPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4814848616458613411L;

	private FakturDao fakturDao;
	private SettingsDao settingsDao;
	
	private Label fakturDateLabel, fakturNumberLabel, customerNameLabel, 
		customerAddress01Label, customerAddress02Label, customerCityLabel,
		customerPaymentLabel, customerTelephoneLabel, customerFaxLabel,
		subTotalLabel, ppnTotalLabel, fakturTotalLabel, suratJalanNumberLabel,
		suratJalanDateLabel, companyLabel, bankAccountlabel, customerPurchaseOrderLabel,
		fakturNoteLabel;
	private Grid fakturProductGrid;
	private Vbox printVbox;
	
	private FakturData fakturData;
	private Faktur faktur;
	
	private final long DEFAULT_COMPANY_ID	= 1L;	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setFakturData((FakturData) arg.get("fakturData"));
	}

	public void onCreate$fakturPrintWin(Event event) throws Exception {
		setFaktur(getFakturData().getFaktur());
		Settings settings = getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID);
		Company company = settings.getSelectedCompany();
		Bank ppnBank = settings.getPpnTransactionBank();
		Bank nonPpnBank = settings.getNonPpnTransactionBank();
		
		fakturDateLabel.setValue(dateToStringDisplay(
				asLocalDate(getFaktur().getFakturDate()), getLongDateFormat()));
		if (getFaktur().getFakturStatus().equals(DocumentStatus.BATAL)) {
			fakturNumberLabel.setValue(getFaktur().getFakturNumber().getSerialComp()+" - BATAL");			
		} else {
			fakturNumberLabel.setValue(getFaktur().getFakturNumber().getSerialComp());
		}
		
		Customer customerByProxy = getCustomerByProxy(getFaktur().getId());
		if (customerByProxy==null) {
			customerNameLabel.setValue("tunai");
			customerAddress01Label.setValue(" ");
			customerAddress02Label.setValue(" ");
			customerCityLabel.setValue("");
			customerTelephoneLabel.setValue("");
			customerFaxLabel.setValue("");
			customerPurchaseOrderLabel.setValue("");
		} else {
			customerNameLabel.setValue(customerByProxy.getCompanyType()+". "+
					customerByProxy.getCompanyLegalName());
			customerAddress01Label.setValue(customerByProxy.getAddress01());
			customerAddress02Label.setValue(customerByProxy.getAddress02());
			customerCityLabel.setValue(customerByProxy.getCity());
			customerTelephoneLabel.setValue(customerByProxy.getPhone().isEmpty() ? "" : "Tel: "+customerByProxy.getPhone());
			customerFaxLabel.setValue(customerByProxy.getFax().isEmpty() ? "" : "Fax:"+customerByProxy.getFax());
			customerPurchaseOrderLabel.setValue(getFaktur().getNote().isEmpty() ? "" : "PO No.: "+getFaktur().getNote());
		}
		
		customerPaymentLabel.setValue(getFaktur().getPaymentType().compareTo(PaymentType.tunai)==0 ?
				"Pembayaran: Tunai" : "Pembayaran: "+getFaktur().getPaymentType().toString()+" - "+
					getFaktur().getJumlahHari()+" Hari ");
		
		fakturProductGrid.setModel(
				new ListModelList<FakturProduct>(getFaktur().getFakturProducts()));
		fakturProductGrid.setRowRenderer(new RowRenderer<FakturProduct>() {

			@Override
			public void render(Row row, FakturProduct product, int index) throws Exception {
				// No.
				row.appendChild(new Label(String.valueOf(index+1)+"."));
				
				// Nama Barang / Jasa Kena Pajak
				row.appendChild(new Label(product.getP_inventoryCode()+" "+
						product.getP_description()));
				
				// Kuantum
				row.appendChild(product.isByKg() ? 
						new Label(getFormatedFloatLocal(product.getP_weightQuantity())+" Kg") :
						new Label(getFormatedInteger(product.getP_sheetQuantity())+" Lembar"));
				
				// Harga Satuan (Rp.)
				row.appendChild(new Label(toLocalFormatWithDecimal(product.getP_unitPrice())));
						// getFormatedDecimal(product.getP_unitPrice())));
						// toLocalFormat(product.getP_unitPrice())));
				
				// Harga Jual (Rp.)
				row.appendChild(new Label(toLocalFormat(product.getP_subTotal())));
			}
		});
		BigDecimal subTotal = getFaktur().isUsePpn() ? 
				getFaktur().getTotalOrder().subtract(getFaktur().getTotalPpn()) : 
					getFaktur().getTotalOrder();
		subTotalLabel.setValue(toLocalFormat(subTotal));
		ppnTotalLabel.setValue(toLocalFormat(getFaktur().getTotalPpn()));
		fakturTotalLabel.setValue(toLocalFormat(getFaktur().getTotalOrder()));

		if (getFakturData().getSuratJalan()!=null) {
			suratJalanNumberLabel.setValue(
					"Faktur sesuai dengan SuratJalan No.: "+
							getFakturData().getSuratJalan().getSuratJalanNumber().getSerialComp());
			suratJalanDateLabel.setValue("Tgl:"+dateToStringDisplay(
					asLocalDate(getFakturData().getSuratJalan().getSuratJalanDate()), getShortDateFormat()));
			fakturNoteLabel.setValue(getFaktur().getNote());
		}

		if (getFaktur().isUsePpn()) {
			bankAccountlabel.setValue(
				ppnBank.getCompany().getCompanyType()+". "+ppnBank.getCompany().getCompanyLegalName()+" - A/C "+
				ppnBank.getNomorRekening()+" - BANK "+
				ppnBank.getNamaBank()+" "+
				ppnBank.getNamaCabang());
		} else {
			bankAccountlabel.setValue(
				nonPpnBank.getAccountOwnerName()+" - A/C "+
				nonPpnBank.getNomorRekening()+" - BANK "+
				nonPpnBank.getNamaBank()+" "+
				nonPpnBank.getNamaCabang());
		}
		
		companyLabel.setValue(company.getCompanyType()+". "+
				company.getCompanyLegalName());
	}
	
	public void onClick$printButton(Event event) throws Exception {
		
		PrintUtil.print(printVbox);
		
	}

	private Customer getCustomerByProxy(long id) throws Exception {
		Faktur faktur = getFakturDao().findCustomerByProxy(id);
		
		return faktur.getCustomer();
	}

	public Faktur getFaktur() {
		return faktur;
	}

	public void setFaktur(Faktur faktur) {
		this.faktur = faktur;
	}

	public FakturData getFakturData() {
		return fakturData;
	}

	public void setFakturData(FakturData fakturData) {
		this.fakturData = fakturData;
	}

	public FakturDao getFakturDao() {
		return fakturDao;
	}

	public void setFakturDao(FakturDao fakturDao) {
		this.fakturDao = fakturDao;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}
}
