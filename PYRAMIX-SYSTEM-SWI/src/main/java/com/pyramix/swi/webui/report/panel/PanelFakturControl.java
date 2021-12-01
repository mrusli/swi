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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.faktur.FakturProduct;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class PanelFakturControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231513L;

	private FakturDao fakturDao;
	
	private Textbox fakturNoTextbox, customerTextbox, noteTextbox, subTotalTextbox,
		ppnTextbox, totalTextbox;
	private Datebox fakturDatebox;
	private Combobox pembayaranCombobox;
	private Intbox jumlahHariIntbox;
	private Checkbox ppnCheckbox;
	private Label infoFakturProductlabel;
	private Listbox productListbox;
	
	private Faktur faktur;
	
	private final Logger log = Logger.getLogger(PanelFakturControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setFaktur((Faktur) arg.get("faktur"));
	}

	public void onCreate$panelFakturDiv(Event event) throws Exception {
		log.info("Creating panelFakturDiv with: "+getFaktur().toString());

		// set datebox format
		fakturDatebox.setLocale(getLocale());
		fakturDatebox.setFormat(getLongDateFormat());
		
		fakturNoTextbox.setValue(getFaktur().getFakturNumber().getSerialComp());
		fakturDatebox.setValue(getFaktur().getFakturDate());
		// customer requires proxy
		Faktur fakturByProxy = getFakturDao().findCustomerByProxy(getFaktur().getId());
		customerTextbox.setValue(fakturByProxy.getCustomer().getCompanyType()+"."+
				fakturByProxy.getCustomer().getCompanyLegalName());
		pembayaranCombobox.setValue(getFaktur().getPaymentType().toString());
		jumlahHariIntbox.setValue(getFaktur().getJumlahHari());
		ppnCheckbox.setChecked(getFaktur().isUsePpn());
		noteTextbox.setValue(getFaktur().getNote());
		
		// detail
		productListbox.setModel(
				new ListModelList<FakturProduct>(getFaktur().getFakturProducts()));
		productListbox.setItemRenderer(getFakturProductListitemRenderer());
		
		// infoFakturProduct
		infoFakturProduct();
	}
	
	private ListitemRenderer<FakturProduct> getFakturProductListitemRenderer() {
		
		return new ListitemRenderer<FakturProduct>() {
			
			@Override
			public void render(Listitem item, FakturProduct product, int index) throws Exception {
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
				
				// No.Coil
				lc = new Listcell(product.getInventoryMarking());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(product.getDescription());
				lc.setParent(item);
				
				// Unit
				lc = new Listcell(product.isByKg() ? "Kg" : "Sht");
				lc.setParent(item);
				
				// Harga (Rp.)
				lc = new Listcell(toLocalFormat(product.getUnitPrice()));
				lc.setParent(item);
				
				// SubTotal (Rp.)
				lc = new Listcell(toLocalFormat(product.getSubTotal()));
				lc.setParent(item);
				
			}
		};
	}

	private void infoFakturProduct() {
		int itemCount = 
				getFaktur().getFakturProducts().size();
		
		// Faktur: 0 items
		infoFakturProductlabel.setValue("Faktur: "+itemCount+" item");
		
		// subtotal
		BigDecimal subTotal = BigDecimal.ZERO;
		for (FakturProduct product : getFaktur().getFakturProducts()) {
			subTotal = subTotal.add(product.getSubTotal());
		}
		
		subTotalTextbox.setValue(toLocalFormat(subTotal));
		
		// ppn
		ppnTextbox.setValue(toLocalFormat(getFaktur().getTotalPpn()));
		
		// total
		totalTextbox.setValue(toLocalFormat(getFaktur().getTotalOrder()));
	}
	
	public Faktur getFaktur() {
		return faktur;
	}

	public void setFaktur(Faktur faktur) {
		this.faktur = faktur;
	}

	public FakturDao getFakturDao() {
		return fakturDao;
	}

	public void setFakturDao(FakturDao fakturDao) {
		this.fakturDao = fakturDao;
	}
	
}
