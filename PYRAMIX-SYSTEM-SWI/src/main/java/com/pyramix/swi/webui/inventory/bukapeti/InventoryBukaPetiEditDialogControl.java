package com.pyramix.swi.webui.inventory.bukapeti;

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

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryBukaPetiEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -178315500888045400L;
	
	private InventoryBukaPetiDao inventoryBukapetiDao;
	
	private Window inventoryBukapetiEditDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox processLocationCombobox, processStatusCombobox;
	private Label infoMaterialLabel, infoEndProductLabel;
	private Listbox materialInventoryListbox, productInventoryListbox;
	
	private InventoryBukaPeti inventoryBukapeti;
	private InventoryBukaPetiEndProduct modBukaPetiEndProduct;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryBukapeti(
				(InventoryBukaPeti) arg.get("inventoryBukapeti"));
	}	
	
	public void onCreate$inventoryBukapetiEditDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());	
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
		setInventoryBukaPetiInfo();
	}
	
	private void setInventoryBukaPetiInfo() throws Exception {
		orderDatebox.setValue(getInventoryBukapeti().getOrderDate());
		processNumberTextbox.setValue(getInventoryBukapeti().getBukapetiNumber().getSerialComp());
		completeDatebox.setValue(getInventoryBukapeti().getCompleteDate());
		
		processLocationCombobox.setValue("SWI");
		processStatusCombobox.setValue(getInventoryBukapeti().getInventoryBukapetiStatus().toString());
		noteTextbox.setValue(getInventoryBukapeti().getNote());
		
		// material
		listBukaPetiMaterial();
		
		// endproduct
		listBukaPetiEndProduct();
	}

	private void listBukaPetiMaterial() throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukapetiMaterialsByProxy(getInventoryBukapeti().getId());
		List<InventoryBukaPetiMaterial> bukapetiMaterialList =
				inventoryBukapetiByProxy.getBukapetiMaterialList();
				
		// Materi: 2 items - 12MT
		infoMaterialLabel.setValue("Materi: "+bukapetiMaterialList.size()+" petian - "+
				getFormatedFloatLocal(getTotalMaterialWeightQuantity(bukapetiMaterialList))+
				" Kg.");
		
		// list material
		materialInventoryListbox.setModel(new ListModelList<InventoryBukaPetiMaterial>(
				inventoryBukapetiByProxy.getBukapetiMaterialList()));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryListitemRenderer());
	}

	private BigDecimal getTotalMaterialWeightQuantity(List<InventoryBukaPetiMaterial> bukapetiMaterialList) {
		BigDecimal totalQtyMaterial = BigDecimal.ZERO;
		for (InventoryBukaPetiMaterial material : bukapetiMaterialList) {
			totalQtyMaterial = totalQtyMaterial.add(material.getWeightQuantity());
		}
		
		return totalQtyMaterial;
	}	

	private ListitemRenderer<InventoryBukaPetiMaterial> getMaterialInventoryListitemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiMaterial bukapetiMaterial, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(bukapetiMaterial.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(bukapetiMaterial.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(bukapetiMaterial.getThickness())+" x "+
						getFormatedFloatLocal(bukapetiMaterial.getWidth())+" x "+
						(bukapetiMaterial.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(bukapetiMaterial.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(bukapetiMaterial.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(bukapetiMaterial.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty(Sht/Line)
				lc = new Listcell(getFormatedInteger(bukapetiMaterial.getSheetQuantity()));
				lc.setParent(item);
			}
		};
	}

	private void listBukaPetiEndProduct() throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukaPetiEndProductByProxy(getInventoryBukapeti().getId());
		
		// Hasil Buka Peti: 2 Ukuran
		infoEndProductLabel.setValue("Hasil Buka Peti: "+
				inventoryBukapetiByProxy.getBukapetiEndProduct().size()+" Ukuran");
		
		// list endproduct
		productInventoryListbox.setModel(new ListModelList<InventoryBukaPetiEndProduct>(
				inventoryBukapetiByProxy.getBukapetiEndProduct()));
		productInventoryListbox.setItemRenderer(getBukaPetiEndProductListItemRenderer());
	}

	private ListitemRenderer<InventoryBukaPetiEndProduct> getBukaPetiEndProductListItemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Material
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
				
				// Customer
				lc = new Listcell("SWI");
				lc.setParent(item);
				
				// edit
				lc = initEditEndProduct(new Listcell(), endProduct, index);
				lc.setParent(item);
				
				item.setValue(endProduct);
			}

			private Listcell initEditEndProduct(Listcell listcell, InventoryBukaPetiEndProduct endProduct,int selIndex) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryBukaPetiEndProduct> args = 
								Collections.singletonMap("bukapetiEndProduct", endProduct);
						
						Window endProductEditWin = 
								(Window) Executions.createComponents("/inventory/bukapeti/InventoryBukaPetiEditEndProductDialog.zul", null, args);
						
						endProductEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								modBukaPetiEndProduct = (InventoryBukaPetiEndProduct) event.getData();
								
								Listitem item = (Listitem) listcell.getParent();
								
								Listcell lc;
								
								// #0: Material
								lc = (Listcell) item.getChildren().get(0);
								lc.setLabel(modBukaPetiEndProduct.getMarking());
								
								// #2: Spesifikasi
								lc = (Listcell) item.getChildren().get(2);
								lc.setLabel(
										getFormatedFloatLocal(modBukaPetiEndProduct.getThickness())+" x "+
										getFormatedFloatLocal(modBukaPetiEndProduct.getWidth())+" x "+
										(modBukaPetiEndProduct.getLength().compareTo(BigDecimal.ZERO)==0 ?
												"Coil" : 
												getFormatedFloatLocal(modBukaPetiEndProduct.getLength())));
								
								// #3: Qty (Sht/Line)
								lc = (Listcell) item.getChildren().get(3);
								lc.setLabel(getFormatedInteger(modBukaPetiEndProduct.getSheetQuantity()));
								
								// #4: Qty (Kg)
								lc = (Listcell) item.getChildren().get(4);
								lc.setLabel(getFormatedFloatLocal(modBukaPetiEndProduct.getWeightQuantity()));
								
								item.setValue(modBukaPetiEndProduct);
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
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukaPetiEndProductByProxy(getInventoryBukapeti().getId());
		
		inventoryBukapetiByProxy.setOrderDate(orderDatebox.getValue());
		inventoryBukapetiByProxy.setCompleteDate(completeDatebox.getValue());
		inventoryBukapetiByProxy.setNote(noteTextbox.getValue());
		
		for (int i=0; i<inventoryBukapetiByProxy.getBukapetiEndProduct().size(); i++) {
			InventoryBukaPetiEndProduct endProduct =
					inventoryBukapetiByProxy.getBukapetiEndProduct().get(i);
			
			Listitem item =
					productInventoryListbox.getItemAtIndex(i);
			InventoryBukaPetiEndProduct modEndProduct = item.getValue();
			
			endProduct.setMarking(modEndProduct.getMarking());
			endProduct.setThickness(modEndProduct.getThickness());
			endProduct.setWidth(modEndProduct.getWidth());
			endProduct.setLength(modEndProduct.getLength());
			endProduct.setSheetQuantity(modEndProduct.getSheetQuantity());
			endProduct.setWeightQuantity(modEndProduct.getWeightQuantity());
		}
		
		Events.sendEvent(Events.ON_OK, inventoryBukapetiEditDialogWin, inventoryBukapetiByProxy);
		
		inventoryBukapetiEditDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryBukapetiEditDialogWin.detach();
	}

	public InventoryBukaPeti getInventoryBukapeti() {
		return inventoryBukapeti;
	}

	public void setInventoryBukapeti(InventoryBukaPeti inventoryBukapeti) {
		this.inventoryBukapeti = inventoryBukapeti;
	}

	public InventoryBukaPetiDao getInventoryBukapetiDao() {
		return inventoryBukapetiDao;
	}

	public void setInventoryBukapetiDao(InventoryBukaPetiDao inventoryBukapetiDao) {
		this.inventoryBukapetiDao = inventoryBukapetiDao;
	}
}
