package com.pyramix.swi.webui.report.panel;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.suratjalan.SuratJalanProduct;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class PanelSuratJalanControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231515L;
	
	private SuratJalanDao suratJalanDao;
	
	private Textbox orderNoTextbox, customerTextbox, noteTextbox;
	private Datebox orderDatebox, deliveryDatebox;
	private Label infoSuratJalanProductlabel;
	private Listbox productListbox;
	
	private SuratJalan suratJalan;
	
	private final Logger log = Logger.getLogger(PanelSuratJalanControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// suratJalan
		setSuratJalan((SuratJalan) arg.get("suratJalan"));
	}

	public void onCreate$panelSuratJalanDiv(Event event) throws Exception {		
		log.info("Creating panelSuratJalanDiv using: "+getSuratJalan().toString());

		// set datebox format
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		deliveryDatebox.setLocale(getLocale());
		deliveryDatebox.setFormat(getLongDateFormat());
		
		orderNoTextbox.setValue(getSuratJalan().getSuratJalanNumber().getSerialComp());
		orderDatebox.setValue(getSuratJalan().getSuratJalanDate());
		deliveryDatebox.setValue(getSuratJalan().getDeliveryDate());
		// customer requires proxy
		SuratJalan suratJalanCustByProxy = 
				getSuratJalanDao().findCustomerByProxy(getSuratJalan().getId());
		customerTextbox.setValue(suratJalanCustByProxy.getCustomer().getCompanyType()+"."+
				suratJalanCustByProxy.getCustomer().getCompanyLegalName());
		noteTextbox.setValue(getSuratJalan().getNote());
		// details
		productListbox.setModel(
				new ListModelList<SuratJalanProduct>(getSuratJalan().getSuratJalanProducts()));
		productListbox.setItemRenderer(getSuratJalanProductListitemRenderer());
		
		displayInfoProduct();
	}
	
	private ListitemRenderer<SuratJalanProduct> getSuratJalanProductListitemRenderer() {
		
		return new ListitemRenderer<SuratJalanProduct>() {
			
			@Override
			public void render(Listitem item, SuratJalanProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);

				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedInteger(product.getWeightQuantity()));
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(product.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(product.getDescription());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(product.getInventoryMarking());
				lc.setParent(item);
				
			}
		};
		
	}

	private void displayInfoProduct() {
		int itemCount =
				getSuratJalan().getSuratJalanProducts().size();
		// SuratJalan: 2 items
		infoSuratJalanProductlabel.setValue("Surat Jalan: "+getFormatedInteger(itemCount)+" item");
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
	
}
