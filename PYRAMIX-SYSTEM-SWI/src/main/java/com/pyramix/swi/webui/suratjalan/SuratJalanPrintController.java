package com.pyramix.swi.webui.suratjalan;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;

import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.suratjalan.SuratJalanProduct;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PrintUtil;

public class SuratJalanPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8186000971380137014L;

	private SuratJalanDao suratJalanDao;
	private SettingsDao settingsDao;
	
	private Label customerNameLabel, customerAddress01Label, customerAddress02Label, 
		customerCityLabel, customerTelephoneLabel, customerFaxLabel, suratJalanDateLabel, 
		suratJalanNumberLabel, deliveryDateLabel, customerPaymentLabel, deliveryOrderNumberLabel,
		companyLabel, customerPurchaseOrderLabel;
	private Grid suratJalanProductGrid;
	private Radiogroup printOptionRadioGroup;
	private Column qtyShtColumn, qtyKgColumn;
	
	private Vbox printVbox;
	
	private SuratJalan suratJalan;
	
	private final long DEFAULT_COMPANY_ID	= 1L;
	private final Logger log = Logger.getLogger(SuratJalanPrintController.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSuratJalan(
				(SuratJalan) arg.get("suratJalan"));
	}

	public void onCreate$suratJalanPrintWin(Event event) throws Exception {
		// set printOptionRadioGroup to print with 'Qty(Sheet) + Qty(Kg)' as default.
		printOptionRadioGroup.setSelectedIndex(0);
		
		// display the data to print with option 'Qty(Sheet) + Qty(Kg)'
		displayDataToPrint(0);
		
	}

	private void displayDataToPrint(int selIndex) throws Exception {
		Settings settings = getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID);
		Company company = settings.getSelectedCompany();
		
		Customer customerByProxy = getCustomerByProxy(getSuratJalan().getId());
		
		customerNameLabel.setValue(customerByProxy==null ? "TUNAI" : 
			customerByProxy.getCompanyType()+". "+
			customerByProxy.getCompanyLegalName());
		customerAddress01Label.setValue(customerByProxy==null ? " " : 
			customerByProxy.getAddress01());
		customerAddress02Label.setValue(customerByProxy==null ? " " : 
			customerByProxy.getAddress02());
		customerCityLabel.setValue(customerByProxy==null ? " " : 
			customerByProxy.getCity());
		customerTelephoneLabel.setValue(customerByProxy==null ? " " : 
			customerByProxy.getPhone().isEmpty() ? "" : "Tel: "+customerByProxy.getPhone()); 
		customerFaxLabel.setValue(customerByProxy==null ? " " : 
			customerByProxy.getFax().isEmpty() ? "" : "Fax:"+customerByProxy.getFax());
		
		suratJalanDateLabel.setValue("Jakarta, "+dateToStringDisplay(
				asLocalDate(getSuratJalan().getSuratJalanDate()), getLongDateFormat()));
		deliveryDateLabel.setValue("Tgl.Kirim:"+dateToStringDisplay(
				asLocalDate(getSuratJalan().getDeliveryDate()), getLongDateFormat()));
		
		suratJalanNumberLabel.setValue(getSuratJalan().isUsePpn() ? 
				"SURAT JALAN NO: P"+getSuratJalan().getSuratJalanNumber().getSerialComp() :
					"SURAT JALAN NO: "+getSuratJalan().getSuratJalanNumber().getSerialComp());
		
		customerPaymentLabel.setValue(getSuratJalan().getPaymentType().compareTo(PaymentType.tunai)==0 ?
				"Pembayaran: Tunai" :
					"Pembayaran: "+getSuratJalan().getPaymentType().toString()+"-"+
					getSuratJalan().getJumlahHari()+" Hari");
		
		customerPurchaseOrderLabel.setValue(getSuratJalan().getNote().isEmpty()? "" : "PO No.: "+getSuratJalan().getNote());
		
		suratJalanProductGrid.setModel(
				new ListModelList<SuratJalanProduct>(getSuratJalan().getSuratJalanProducts()));
		suratJalanProductGrid.setRowRenderer(new RowRenderer<SuratJalanProduct>() {
			
			@Override
			public void render(Row row, SuratJalanProduct product, int index) throws Exception {
				// No.
				row.appendChild(new Label(String.valueOf(index+1)+"."));
				
				if (selIndex==0) {
					// Selected Option to print: Qty(Sheet) + Qty(Kg)

					qtyShtColumn.setLabel("Qty(Sht)");
					qtyKgColumn.setLabel("Qty(Kg)");
					
					// Qty(Sht)
					row.appendChild(new Label(getFormatedInteger(product.getP_sheetQuantity())));
					
					// Qty(Kg)
					row.appendChild(new Label(getFormatedFloatLocal(product.getP_weightQuantity())));
				} else if (selIndex==1) {
					// Selected Option to print: Qty(Sheet)
					
					qtyShtColumn.setLabel("Qty(Sht)");
					
					// Qty(Sht)
					row.appendChild(new Label(getFormatedInteger(product.getP_sheetQuantity())));
					
					qtyKgColumn.setLabel("");
					
					// Qty(Kg)
					row.appendChild(new Label(""));					
				} else {
					// Selected Option to print: Qty(Kg)

					qtyShtColumn.setLabel("");
					
					// Qty(Sht)
					row.appendChild(new Label(""));
					
					qtyKgColumn.setLabel("Qty(Kg)");
					
					// Qty(Kg)
					row.appendChild(new Label(getFormatedFloatLocal(product.getP_weightQuantity())));
					
				}
				
				
				// Tipe
				row.appendChild(new Label(product.getP_inventoryCode()));
				
				// Deskripsi
				row.appendChild(new Label(product.getP_description()));
				
			}
		});
		
		DeliveryOrder deliveryOrderByProxy = getDeliveryOrderByProxy(getSuratJalan().getId());
		deliveryOrderNumberLabel.setValue(deliveryOrderByProxy==null ? 
				" " : "D/O No.:"+deliveryOrderByProxy.getDeliveryOrderNumber().getSerialComp());
		
		companyLabel.setValue(company.getCompanyType()+". "+
				company.getCompanyLegalName());
	}
	
	public void onCheck$printOptionRadioGroup(Event event) throws Exception {
		log.info("Selected Option to Print: " + 
				printOptionRadioGroup.getSelectedItem().getLabel() + 
				"["+printOptionRadioGroup.getSelectedIndex()+"]");
		
		displayDataToPrint(printOptionRadioGroup.getSelectedIndex());
	}
	
	public void onClick$printButton(Event event) throws Exception {
		
		PrintUtil.print(printVbox);
		
	}

	private DeliveryOrder getDeliveryOrderByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findDeliveryOrderByProxy(getSuratJalan().getId());
		
		return suratJalan.getDeliveryOrder();
	}
	
	private Customer getCustomerByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findCustomerByProxy(id);
		
		return suratJalan.getCustomer();
	}	
	
	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}
	
}
