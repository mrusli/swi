package com.pyramix.swi.webui.report;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8740426989041992400L;

	private CustomerOrderProductDao customerOrderProductDao;
	
	private Window reportSalesDialogWin;
	private Listbox reportSalesListbox;
	private Listfooter subTotalListfooter;
	
	private CustomerOrder customerOrder;
	
	private BigDecimal subTotalPenjualan = BigDecimal.ZERO;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrder(
				(CustomerOrder) arg.get("customerOrder"));
	}

	public void onCreate$reportSalesDialogWin(Event event) throws Exception {
		reportSalesDialogWin.setTitle("Detail Penjualan");
		
		reportSalesListbox.setModel(
				new ListModelList<CustomerOrderProduct>(
						getCustomerOrder().getCustomerOrderProducts()));
		reportSalesListbox.setItemRenderer(getReportSalesItemRenderer());
	}	
	
	private ListitemRenderer<CustomerOrderProduct> getReportSalesItemRenderer() {

		subTotalPenjualan = BigDecimal.ZERO;
		
		return new ListitemRenderer<CustomerOrderProduct>() {
			
			@Override
			public void render(Listitem item, CustomerOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// Quantity / Unit
				lc = new Listcell(product.isByKg() ? 
						getFormatedFloatLocal(product.getWeightQuantity())+" Kg.":
							getFormatedInteger(product.getSheetQuantity())+" Sht.");
				lc.setParent(item);		
				
				// Kode
				InventoryCode codeByProxy = getInventoryCodeByProxy(product.getId());
				lc = new Listcell(codeByProxy.getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(product.getDescription());
				lc.setParent(item);
				
				// Harga (Rp.)
				lc = new Listcell(toLocalFormatWithDecimal(product.getSellingPrice()));
				lc.setParent(item);
				
				// SubTotal (Rp.)
				lc = new Listcell(toLocalFormat(product.getSellingSubTotal()));
				lc.setParent(item);
				
				// accumulate
				subTotalPenjualan = subTotalPenjualan.add(product.getSellingSubTotal());
			}
		};
	}

	public void onAfterRender$reportSalesListbox(Event event) throws Exception {
		subTotalListfooter.setLabel(toLocalFormat(subTotalPenjualan));
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		reportSalesDialogWin.detach();
	}

	protected InventoryCode getInventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct product = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return product.getInventoryCode();
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}
	
}
