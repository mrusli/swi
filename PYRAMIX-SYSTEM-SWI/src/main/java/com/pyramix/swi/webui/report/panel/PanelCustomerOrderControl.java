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

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class PanelCustomerOrderControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231507L;
	
	private CustomerOrderDao customerOrderDao;
	private CustomerOrderProductDao customerOrderProductDao;
	
	private Textbox orderNoTextbox, customerTextbox, totalOrderTextbox, noteTextbox,
		subTotalTextbox, ppnTextbox, totalTextbox;
	private Datebox orderDatebox;
	private Combobox pembayaranCombobox, salesPersonCombobox;
	private Intbox jumlahHariIntbox;
	private Checkbox usePpn;
	private Label infoOrderlabel;
	private Listbox productListbox;
	
	private CustomerOrder customerOrder;
	
	private final Logger log = Logger.getLogger(PanelCustomerOrderControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// customerOrder
		setCustomerOrder((CustomerOrder) arg.get("customerOrder"));
	}

	public void onCreate$panelCustomerOrderDiv(Event event) throws Exception {
		log.info("Creating panelCustomerOrderControl with: "+getCustomerOrder().toString());
		
		// set datebox format
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());		
		
		orderNoTextbox.setValue(getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		orderDatebox.setValue(getCustomerOrder().getOrderDate());
		// customer requires proxy
		CustomerOrder customerOrderCustByProxy = 
				getCustomerOrderDao().findCustomerByProxy(getCustomerOrder().getId());
		customerTextbox.setValue(customerOrderCustByProxy.getCustomer().getCompanyType()+"."+
				customerOrderCustByProxy.getCustomer().getCompanyLegalName());
		pembayaranCombobox.setValue(getCustomerOrder().getPaymentType().toString());
		jumlahHariIntbox.setValue(getCustomerOrder().getCreditDay());
		usePpn.setChecked(getCustomerOrder().isUsePpn());
		totalOrderTextbox.setValue(toLocalFormat(getCustomerOrder().getTotalOrder()));
		noteTextbox.setValue(getCustomerOrder().getNote());
		// employeeSales requires proxy
		CustomerOrder customerOrderEmpByProxy =
				getCustomerOrderDao().findEmployeeSalesByProxy(getCustomerOrder().getId());
		salesPersonCombobox.setValue(customerOrderEmpByProxy.getEmployeeSales().getName());
		// details
		productListbox.setModel(
				new ListModelList<CustomerOrderProduct>(getCustomerOrder().getCustomerOrderProducts()));
		productListbox.setItemRenderer(getCustomerOrderProductsListitemRenderer());
		// info
		infoCustomerOrderProduct();
	}
	
	private ListitemRenderer<CustomerOrderProduct> getCustomerOrderProductsListitemRenderer() {
		
		return new ListitemRenderer<CustomerOrderProduct>() {
			
			@Override
			public void render(Listitem item, CustomerOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.Coil
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// Kode
				CustomerOrderProduct customerOrderProductInvcByProxy =
						getCustomerOrderProductDao().findInventoryCodeByProxy(product.getId());
				lc = new Listcell(customerOrderProductInvcByProxy.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(product.getDescription());
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(product.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedInteger(product.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// Unit
				lc = new Listcell(product.isByKg()? "Kg" : "Sht");
				lc.setParent(item);
				
				// Harga (Rp.)
				lc = new Listcell(toLocalFormat(product.getSellingPrice()));
				lc.setParent(item);
				
				// SubTotal (Rp.)
				lc = new Listcell(toLocalFormat(product.getSellingSubTotal()));
				lc.setParent(item);
				
				
			}
		};
	}
	
	private void infoCustomerOrderProduct() {
		int itemCount = 
				getCustomerOrder().getCustomerOrderProducts().size();
		
		// Pembelian items
		infoOrderlabel.setValue("Pembelian: "+itemCount+" item");
		
		// subtotal
		BigDecimal subTotal = BigDecimal.ZERO;
		for (CustomerOrderProduct product : getCustomerOrder().getCustomerOrderProducts()) {
			subTotal = subTotal.add(product.getSellingSubTotal());
		}
		
		// total
		subTotalTextbox.setValue(toLocalFormat(subTotal));
		ppnTextbox.setValue(toLocalFormat(getCustomerOrder().getTotalPpn()));
		totalTextbox.setValue(toLocalFormat(getCustomerOrder().getTotalOrder()));
	}
	
	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}	
}
