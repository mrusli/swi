package com.pyramix.swi.webui.report.panel;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class PanelDeliveryOrderControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231514L;

	private DeliveryOrderDao deliveryOrderDao;
	
	private Textbox deliveryOrderNoTextbox, noteTextbox;
	private Datebox deliveryDatebox;
	private Combobox locationCombobox;
	private Label infoDeliveryOrderProductlabel;
	private Listbox productListbox;
	
	private DeliveryOrder deliveryOrder;
	
	private final Logger log = Logger.getLogger(PanelDeliveryOrderControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// deliveryOrder
		setDeliveryOrder((DeliveryOrder) arg.get("deliveryOrder"));
	}

	public void onCreate$panelDeliveryOrderDiv(Event event) throws Exception {
		log.info("Creating PanelDeliveryOrderDiv");
		log.info(getDeliveryOrder()==null ? "DeliveryOrder is NULL" : getDeliveryOrder().toString());
		
		if (getDeliveryOrder()==null) {
			log.info("Exiting PanelDeliveryOrderDiv");
			
			return;
		}
		
		deliveryOrderNoTextbox.setValue(getDeliveryOrder().getDeliveryOrderNumber().getSerialComp());
		deliveryDatebox.setValue(getDeliveryOrder().getDeliveryOrderDate());
		// location is Company, requires proxy
		DeliveryOrder deliveryOrderByProxy = 
				getDeliveryOrderDao().findCompanyByProxy(getDeliveryOrder().getId());
		locationCombobox.setValue(deliveryOrderByProxy.getCompany().getCompanyType()+"."+
				deliveryOrderByProxy.getCompany().getCompanyLegalName());
		noteTextbox.setValue(getDeliveryOrder().getNote());
		
		// details
		productListbox.setModel(
				new ListModelList<DeliveryOrderProduct>(
						getDeliveryOrder().getDeliveryOrderProducts()));
		productListbox.setItemRenderer(getDeliveryOrderProductListitemRenderer());
		
		// info
		infoDeliveryOrderProduct();
	}
	
	private ListitemRenderer<DeliveryOrderProduct> getDeliveryOrderProductListitemRenderer() {
		
		return new ListitemRenderer<DeliveryOrderProduct>() {
			
			@Override
			public void render(Listitem item, DeliveryOrderProduct product, int index) throws Exception {
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
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
			}
		};
	}
	
	private void infoDeliveryOrderProduct() {
		int itemCount = 
				getDeliveryOrder().getDeliveryOrderProducts().size();
		
		// DeliveryOrder: 0 items
		infoDeliveryOrderProductlabel.setValue("DeliveryOrder: "+getFormatedInteger(itemCount)+" item");
	}
	
	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public DeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	public void setDeliveryOrderDao(DeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
	}
	
}
