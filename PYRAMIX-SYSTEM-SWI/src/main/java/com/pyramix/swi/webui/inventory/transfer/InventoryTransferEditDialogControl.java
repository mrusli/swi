package com.pyramix.swi.webui.inventory.transfer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
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

public class InventoryTransferEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5045445395732327544L;

	private InventoryTransferDao inventoryTransferDao;

	private Window inventoryTransferEditDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox transferLocationCombobox, transferStatusCombobox;
	private Label infoMaterialLabel;
	private Listbox materialInventoryListbox, productInventoryListbox;
	
	private InventoryTransfer inventoryTransfer;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransfer(
				(InventoryTransfer) arg.get("inventoryTransfer"));
	}
	
	public void onCreate$inventoryTransferEditDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());	
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
		setInventoryTransferInfo();
		
	}

	private void setInventoryTransferInfo() throws Exception {
		orderDatebox.setValue(getInventoryTransfer().getOrderDate());
		processNumberTextbox.setValue(getInventoryTransfer().getTransferNumber().getSerialComp());
		completeDatebox.setValue(getInventoryTransfer().getCompleteDate());
		
		InventoryTransfer inventoryTransferFromByProxy = 
				getInventoryTransferDao().getInventoryTransferFromCoByProxy(getInventoryTransfer().getId());
		
		transferLocationCombobox.setValue(inventoryTransferFromByProxy.getTransferFromCo()==null?
				" " : inventoryTransferFromByProxy.getTransferFromCo().getCompanyDisplayName());
		transferStatusCombobox.setValue(getInventoryTransfer().getInventoryTransferStatus().toString());
		
		noteTextbox.setValue(getInventoryTransfer().getNote());

		// list materi dari
		listMaterialFrom();
		
		// list product tujuan
		listEndProductTrnasferTo();
	}

	private void listMaterialFrom() throws Exception {
		InventoryTransfer inventoryTransferMaterialByProxy = 
				getInventoryTransferDao().getInventoryTransferMaterialByProxy(getInventoryTransfer().getId());		
		List<InventoryTransferMaterial> inventoryTransferMaterial =
				inventoryTransferMaterialByProxy.getTransferMaterialList();
		
		// Materi: 2 items - 12MT
		infoMaterialLabel.setValue("Materi: "+inventoryTransferMaterial.size()+" item - "+
				getFormatedFloatLocal(getTotalMaterialWeightQuantity(inventoryTransferMaterial))+
				" Kg.");
		
		materialInventoryListbox.setModel(
				new ListModelList<InventoryTransferMaterial>(inventoryTransferMaterialByProxy.getTransferMaterialList()));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryListitemRenderer());
	}

	private BigDecimal getTotalMaterialWeightQuantity(List<InventoryTransferMaterial> inventoryTransferMaterialList) {
		BigDecimal totalQtyMaterial = BigDecimal.ZERO;
		for (InventoryTransferMaterial material : inventoryTransferMaterialList) {
			totalQtyMaterial = totalQtyMaterial.add(material.getWeightQuantity());
		}
		
		return totalQtyMaterial;
	}

	private ListitemRenderer<InventoryTransferMaterial> getMaterialInventoryListitemRenderer() {
		
		return new ListitemRenderer<InventoryTransferMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryTransferMaterial transferMaterial, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(transferMaterial.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(transferMaterial.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(transferMaterial.getThickness())+" x "+
						getFormatedFloatLocal(transferMaterial.getWidth())+" x "+
						(transferMaterial.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(transferMaterial.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(transferMaterial.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(transferMaterial.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty(Sht/Line)
				lc = new Listcell(getFormatedInteger(transferMaterial.getSheetQuantity()));
				lc.setParent(item);
				
				// Lokasi
				lc = new Listcell(transferMaterial.getInventoryLocation().toString());
				lc.setParent(item);
			}
		};
	}

	private void listEndProductTrnasferTo() throws Exception {
		InventoryTransfer inventoryTransferEndProductByProxy =
				getInventoryTransferDao().getInventoryTransferEndProductByProxy(getInventoryTransfer().getId());
		List<InventoryTransferEndProduct> inventoryTransferEndProductList =
				inventoryTransferEndProductByProxy.getTransferEndProductList();
		
		productInventoryListbox.setModel(
				new ListModelList<InventoryTransferEndProduct>(inventoryTransferEndProductList));
		productInventoryListbox.setItemRenderer(getEndProductInventoryListitemRenderer());
	}

	private ListitemRenderer<InventoryTransferEndProduct> getEndProductInventoryListitemRenderer() {

		return new ListitemRenderer<InventoryTransferEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryTransferEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Lokasi
				lc = new Listcell("SWI");
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
								
				// edit
				lc = initEditEndProduct(new Listcell(), endProduct, index);
				lc.setParent(item);
			}

			private Listcell initEditEndProduct(Listcell listcell, InventoryTransferEndProduct endProduct, int index) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryTransferEndProduct> args =
								Collections.singletonMap("inventoryTransferEndProduct", endProduct);

						Window endProductEditWin = 
								(Window) Executions.createComponents(
										"/inventory/transfer/InventoryTransferEditEndProductDialog.zul", null, args);

						endProductEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryTransferEndProduct modTransferEndProduct = 
										(InventoryTransferEndProduct) event.getData();
								
								Listitem item = (Listitem) listcell.getParent();
								
								Listcell lc;

								// coilNoTextbox --> #1: Material
								lc = (Listcell) item.getChildren().get(1);
								lc.setLabel(modTransferEndProduct.getMarking());
								
								// thicknessDecimalbox x widthDecimalbox x lengthDecimalbox --> #3: Spesifikasi
								lc = (Listcell) item.getChildren().get(3);
								lc.setLabel(
										getFormatedFloatLocal(modTransferEndProduct.getThickness())+" x "+
										getFormatedFloatLocal(modTransferEndProduct.getWidth())+" x "+
										(modTransferEndProduct.getLength().compareTo(BigDecimal.ZERO)==0 ?
												"Coil" : 
												getFormatedFloatLocal(modTransferEndProduct.getLength())));
								
								// quantityShtLineIntbox --> #4: Qty (Sht/Line)
								lc = (Listcell) item.getChildren().get(4);
								lc.setLabel(getFormatedInteger(modTransferEndProduct.getSheetQuantity()));
								
								// quantityKgDecimalbox --> #5: Qty (Kg)
								lc = (Listcell) item.getChildren().get(5);
								lc.setLabel(getFormatedFloatLocal(modTransferEndProduct.getWeightQuantity()));
			
								item.setValue(modTransferEndProduct);
							}
						});
						
						endProductEditWin.doModal();
					}
				});
				editButton.setParent(listcell);				
				
				return listcell;

			}
		};
	}

	public void onClick$saveButton(Event event) throws Exception {
		InventoryTransfer inventoryTransferByProxy =
				getInventoryTransferDao().getInventoryTransferEndProductByProxy(getInventoryTransfer().getId());
		
		inventoryTransferByProxy.setOrderDate(orderDatebox.getValue());
		inventoryTransferByProxy.setCompleteDate(completeDatebox.getValue());
		inventoryTransferByProxy.setNote(noteTextbox.getValue());

		for (int i=0; i<inventoryTransferByProxy.getTransferEndProductList().size(); i++) {
			InventoryTransferEndProduct endProduct =
					inventoryTransferByProxy.getTransferEndProductList().get(i);
			
			InventoryTransferEndProduct modEndProduct =
					productInventoryListbox.getItemAtIndex(i).getValue();
			
			endProduct.setMarking(modEndProduct.getMarking());
			endProduct.setThickness(modEndProduct.getThickness());
			endProduct.setWidth(modEndProduct.getWidth());
			endProduct.setLength(modEndProduct.getLength());
			endProduct.setSheetQuantity(modEndProduct.getSheetQuantity());
			endProduct.setWeightQuantity(modEndProduct.getWeightQuantity());
		}
		
		Events.sendEvent(Events.ON_OK, inventoryTransferEditDialogWin, inventoryTransferByProxy);
		
		inventoryTransferEditDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTransferEditDialogWin.detach();
	}
	
	public InventoryTransferDao getInventoryTransferDao() {
		return inventoryTransferDao;
	}

	public void setInventoryTransferDao(InventoryTransferDao inventoryTransferDao) {
		this.inventoryTransferDao = inventoryTransferDao;
	}

	public InventoryTransfer getInventoryTransfer() {
		return inventoryTransfer;
	}

	public void setInventoryTransfer(InventoryTransfer inventoryTransfer) {
		this.inventoryTransfer = inventoryTransfer;
	}
	
}
