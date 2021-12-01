package com.pyramix.swi.webui.inventory.transfer;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferMaterial;
import com.pyramix.swi.persistence.inventory.transfer.dao.InventoryTransferDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryTransferViewDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2561201801992247028L;

	private InventoryTransferDao inventoryTransferDao;
	
	private Window inventoryTransferViewDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox transferLocationCombobox, transferStatusCombobox;
	private Listbox materialInventoryListbox, productInventoryListbox;
	
	private InventoryTransfer inventoryTransfer;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransfer(
				(InventoryTransfer) arg.get("inventoryTransfer"));
	}

	public void onCreate$inventoryTransferViewDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());

		orderDatebox.setValue(getInventoryTransfer().getOrderDate());
		processNumberTextbox.setValue(getInventoryTransfer().getTransferNumber().getSerialComp());
		completeDatebox.setValue(getInventoryTransfer().getCompleteDate());

		InventoryTransfer inventoryTransferByProxy = getInventoryTransferDao().getInventoryTransferFromCoByProxy(getInventoryTransfer().getId());
		
		transferLocationCombobox.setValue(inventoryTransferByProxy.getTransferFromCo().getCompanyDisplayName());
		transferStatusCombobox.setValue(getInventoryTransfer().getInventoryTransferStatus().toString());

		noteTextbox.setValue(getInventoryTransfer().getNote());

		// material
		displayInventoryTransferMaterial();
		
		// end-product
		displayInventoryTransferEndProduct();
	}
	
	private void displayInventoryTransferMaterial() throws Exception {
		InventoryTransfer inventoryTransferMaterialByProxy =
				getInventoryTransferDao().getInventoryTransferMaterialByProxy(getInventoryTransfer().getId());
		materialInventoryListbox.setModel(new ListModelList<InventoryTransferMaterial>(
				inventoryTransferMaterialByProxy.getTransferMaterialList()));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryTransferListitemRenderer());
		
	}

	private ListitemRenderer<InventoryTransferMaterial> getMaterialInventoryTransferListitemRenderer() {
		
		return new ListitemRenderer<InventoryTransferMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryTransferMaterial material, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);

				// No.Coil
				lc = new Listcell(material.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(material.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(material.getThickness())+" x "+
						getFormatedFloatLocal(material.getWidth())+" x "+
						(material.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(material.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(material.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(material.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty(Sht/Line)
				lc = new Listcell(getFormatedInteger(material.getSheetQuantity()));
				lc.setParent(item);
				
				// Lokasi
				lc = new Listcell(material.getInventoryLocation().toString());
				lc.setParent(item);
			}
		};
	}

	private void displayInventoryTransferEndProduct() throws Exception {
		InventoryTransfer inventoryTransferEndProductByProxy =
				getInventoryTransferDao().getInventoryTransferEndProductByProxy(getInventoryTransfer().getId());
		productInventoryListbox.setModel(new ListModelList<InventoryTransferEndProduct>(
				inventoryTransferEndProductByProxy.getTransferEndProductList()));
		productInventoryListbox.setItemRenderer(getEndProductInventoryTransferListitemRenderer());
		
	}

	private ListitemRenderer<InventoryTransferEndProduct> getEndProductInventoryTransferListitemRenderer() {
		
		return new ListitemRenderer<InventoryTransferEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryTransferEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Lokasi
				lc = new Listcell(endProduct.getInventoryLocation().toString());
				lc.setParent(item);
				
				// Materi
				lc = new Listcell(endProduct.getMarking());
				lc.setParent(item);

				// Kode
				lc = new Listcell(endProduct.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(						
						getFormatedFloatLocal(endProduct.getThickness())+" x "+
						getFormatedFloatLocal(endProduct.getWidth())+" x "+
						(endProduct.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(endProduct.getLength())));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(endProduct.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(endProduct.getWeightQuantity()));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(endProduct.getInventoryPacking().toString());
				lc.setParent(item);
				
			}
		};
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTransferViewDialogWin.detach();
	}
	
	public InventoryTransfer getInventoryTransfer() {
		return inventoryTransfer;
	}

	public void setInventoryTransfer(InventoryTransfer inventoryTransfer) {
		this.inventoryTransfer = inventoryTransfer;
	}

	public InventoryTransferDao getInventoryTransferDao() {
		return inventoryTransferDao;
	}

	public void setInventoryTransferDao(InventoryTransferDao inventoryTransferDao) {
		this.inventoryTransferDao = inventoryTransferDao;
	}
	
	
	
}
