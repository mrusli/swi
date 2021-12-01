package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryProcessViewDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6907234075200491227L;

	private InventoryProcessDao inventoryProcessDao;
	
	private Window inventoryProcessViewDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox processLocationCombobox, processStatusCombobox;
	private Listbox materialInventoryListbox, productInventoryListbox;
	private Tabbox processTabbox;
	private Label infoMaterialToProcessLabel, infoQtyProcessLabel;

	private InventoryProcessData inventoryProcessData;
	private InventoryProcess inventoryProcess;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryProcessData(
				(InventoryProcessData) arg.get("inventoryProcessData"));
	}
	
	public void onCreate$inventoryProcessViewDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale()); 
		completeDatebox.setLocale(getLocale());
		
		setInventoryProcess(
				getInventoryProcessData().getInventoryProcess());
		
		displayInventoryProcessInfo();
	}
	
	private void displayInventoryProcessInfo() throws Exception {
		InventoryProcess inventoryProcessByCoByProxy =
				getInventoryProcessDao().getProcessedByCoByProxy(getInventoryProcess().getId());
		
		orderDatebox.setValue(getInventoryProcess().getOrderDate());
		completeDatebox.setValue(getInventoryProcess().getCompleteDate());
		processNumberTextbox.setValue(getInventoryProcess().getProcessNumber().getSerialComp());
		processLocationCombobox.setValue(inventoryProcessByCoByProxy.getProcessedByCo().getCompanyDisplayName());
		processStatusCombobox.setValue(getInventoryProcess().getProcessStatus().toString());
		noteTextbox.setValue(getInventoryProcess().getNote());
		
		InventoryProcess inventoryProcessMaterialByProxy =
				getInventoryProcessDao().getProcessMaterialsByProxy(getInventoryProcess().getId());
		
		materialInventoryListbox.setModel(
				new ListModelList<InventoryProcessMaterial>(
						inventoryProcessMaterialByProxy.getProcessMaterials()));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryItemRenderer());
		
	}

	private ListitemRenderer<InventoryProcessMaterial> getMaterialInventoryItemRenderer() {

		return new ListitemRenderer<InventoryProcessMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryProcessMaterial material, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(material.getMarking());
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(material.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(material.getThickness())+" x "+
						getFormatedFloatLocal(material.getWidth())+" x "+
						(material.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(material.getLength())));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(material.getInventoryPacking().toString());
				lc.setParent(item);				
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(material.getWeightQuantity()));
				lc.setParent(item);				
				
				// Proses Qty(Kg)
				lc = new Listcell();
				lc.setParent(item);
				
				// Proses Qty(Sht/Line)
				lc = new Listcell();
				lc.setParent(item);
				
				// Sisa Qty(Kg)
				lc = new Listcell();
				lc.setParent(item);
				
				// Edit the products for this material
				lc = initView(new Listcell(), material, index);
				lc.setParent(item);
				
			}

			private Listcell initView(Listcell listcell, InventoryProcessMaterial material, int index) {
				Button viewButton = new Button();
				
				viewButton.setLabel("View");
				viewButton.setClass("inventoryEditButton");
				viewButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// switch to process tab
						processTabbox.setSelectedIndex(1);

						InventoryProcessMaterial inventoryProcessMaterialProductsByProxy =
								getInventoryProcessDao().getProcessProductsByProxy(material.getId());

						// update label status of the process: remain quantity, etc;
						updateQuantityStatus(
								inventoryProcessMaterialProductsByProxy.getProcessProducts(), 
								material, index);
						
						// list only the products for this material				
						productInventoryListbox.setModel(
								new ListModelList<InventoryProcessProduct>(
										inventoryProcessMaterialProductsByProxy.getProcessProducts()));
						productInventoryListbox.setItemRenderer(getProductInventoryItemRederer());								

					}
					
				});
				viewButton.setParent(listcell);
					
				return listcell;
			}
		};
	}

	private ListitemRenderer<InventoryProcessProduct> getProductInventoryItemRederer() {
		
		return new ListitemRenderer<InventoryProcessProduct>() {
			
			@Override
			public void render(Listitem item, InventoryProcessProduct product, int index) throws Exception {
				Listcell lc;
				
				// Material
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(product.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(product.getThickness())+" x "+
						getFormatedFloatLocal(product.getWidth())+" x "+
						(product.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(product.getLength())));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(product.getWeightQuantity()));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(product.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Customer
				String customerName = "";
				InventoryProcessProduct productByProxy =
					getInventoryProcessDao().getProcessProductsCustomerByProxy(product.getId());
				customerName = productByProxy.getCustomer()==null ? "SWI" : 
					productByProxy.getCustomer().getCompanyType()+". "+
					productByProxy.getCustomer().getCompanyLegalName();

				lc = new Listcell(customerName);
				lc.setParent(item);
			}
		};
	}

	public void onSelect$processTabbox(Event event) throws Exception {
		if (processTabbox.getSelectedIndex()==1) {
			// list all products of all the materials
			InventoryProcess inventoryProcessMaterialByProxy =
					getInventoryProcessDao().getProcessMaterialsByProxy(getInventoryProcess().getId());

			List<InventoryProcessProduct> processProductList = getProcessProductList(inventoryProcessMaterialByProxy);
			infoMaterialToProcessLabel.setValue("Proses Menjadi: "+processProductList.size()+" Product");						
			productInventoryListbox.setModel(
					new ListModelList<InventoryProcessProduct>(processProductList));
			productInventoryListbox.setItemRenderer(getProductInventoryItemRederer());
			
			// Total Proses: 8.880,00 Kg
			infoQtyProcessLabel.setValue("Total Proses: "+
				getFormatedFloatLocal(
					getTotalProductWeight(processProductList))+" Kg.");	
		}
	}
	
	private void updateQuantityStatus(List<InventoryProcessProduct> processProductList,
			InventoryProcessMaterial material, int index) {
		
		BigDecimal totalProductWeightQty = getTotalProductWeight(processProductList);
		
		// set the proses info label of the material to process
		// Proses: DY.978K (4.058,00 Kg) - SPCC - 2.00 x 1,200.00 x Coil - Sisa: 3,450Kg
		infoMaterialToProcessLabel.setValue("Proses: Material No."+(index+1)+" - "+
				material.getMarking()+" ("+
				getFormatedFloatLocal(material.getWeightQuantity())+" Kg.) - "+
				material.getInventoryCode().getProductCode()+" - "+
				getFormatedFloatLocal(material.getThickness())+" x "+
				getFormatedFloatLocal(material.getWidth())+" x "+
				"Coil - Sisa: "+
				getFormatedFloatLocal(material.getWeightQuantity().subtract(totalProductWeightQty))+" Kg.");					

		// Total Proses: 8,880Kg
		infoQtyProcessLabel.setValue("Total Proses: "+getFormatedFloatLocal(totalProductWeightQty)+" Kg.");
	}	
	
	private List<InventoryProcessProduct> getProcessProductList(InventoryProcess inventoryProcessMaterialByProxy)
			throws Exception {
		List<InventoryProcessProduct> processProductList = new ArrayList<InventoryProcessProduct>();
		for (InventoryProcessMaterial material : inventoryProcessMaterialByProxy.getProcessMaterials()) {
			InventoryProcessMaterial inventoryProcessMaterialProductsByProxy =
					getInventoryProcessDao().getProcessProductsByProxy(material.getId());
			for (InventoryProcessProduct product : inventoryProcessMaterialProductsByProxy.getProcessProducts()) {
				processProductList.add(product);
			}
		}
		return processProductList;
	}
	
	private BigDecimal getTotalProductWeight(List<InventoryProcessProduct> processProductList) {
		BigDecimal totalProductWeightQty = BigDecimal.ZERO;
		for (InventoryProcessProduct product : processProductList) {
			totalProductWeightQty = totalProductWeightQty.add(product.getWeightQuantity());
		}

		return totalProductWeightQty;
	}

	public void onClick$closeButton(Event event) throws Exception {
		inventoryProcessViewDialogWin.detach();
	}

	public InventoryProcessData getInventoryProcessData() {
		return inventoryProcessData;
	}

	public void setInventoryProcessData(InventoryProcessData inventoryProcessData) {
		this.inventoryProcessData = inventoryProcessData;
	}

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}
}
