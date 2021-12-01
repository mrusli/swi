package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryProcessEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3729890785609634878L;

	private InventoryProcessDao inventoryProcessDao;
	
	private Window inventoryProcessEditDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox processLocationCombobox, processStatusCombobox;
	private Listbox materialInventoryListbox, productInventoryListbox;
	private Tabbox processTabbox;
	private Label infoMaterialToProcessLabel, infoQtyProcessLabel;
	private Button addProcessButton, removeProcess, saveButton;
	
	private InventoryProcessData inventoryProcessData;
	private InventoryProcess inventoryProcess;
	// selected material and it's index when user clicks the 'Edit' button for the material
	private InventoryProcessMaterial selMaterial; 
	private int selMaterialIndex;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryProcessData(
				(InventoryProcessData) arg.get("inventoryProcessData"));
	}
	
	public void onCreate$inventoryProcessEditDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale()); 
		orderDatebox.setFormat(getLongDateFormat());
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
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
				lc.setStyle("white-space: nowrap;");
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
				lc = initEdit(new Listcell(), material, index);
				lc.setParent(item);
				
				item.setValue(material);
			}

			private Listcell initEdit(Listcell listcell, InventoryProcessMaterial material, int materialIndex) {
				Button editButton = new Button();
				
				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// switch to process tab
						processTabbox.setSelectedIndex(1);

						// set selected
						selMaterial = material;
						selMaterialIndex = materialIndex;
						
						InventoryProcessMaterial inventoryProcessMaterialProductsByProxy =
								getInventoryProcessDao().getProcessProductsByProxy(material.getId());

						// update label status of the process: remain quantity, etc;
						updateQuantityStatus(
								inventoryProcessMaterialProductsByProxy.getProcessProducts(), 
								material, materialIndex);
						
						// enable the add and remove process buttons
						addProcessButton.setDisabled((
								material.getWeightQuantity().subtract(
										getTotalProductWeight(inventoryProcessMaterialProductsByProxy.getProcessProducts())).
											compareTo(BigDecimal.ZERO)==0));
						removeProcess.setDisabled(false);
						
						// list only the products for this material				
						productInventoryListbox.setModel(
								new ListModelList<InventoryProcessProduct>(
										inventoryProcessMaterialProductsByProxy.getProcessProducts()));
						productInventoryListbox.setItemRenderer(getProductInventoryItemRederer());								
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
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
			
			// disable the add and remove process buttons
			addProcessButton.setDisabled(true);
			removeProcess.setDisabled(true);
		}
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
				lc.setStyle("white-space: nowrap;");
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
				if (product.getId().compareTo(Long.MIN_VALUE)==0) {
					// when user click add process -- the product object is 'new'
					customerName = product.getCustomer()==null ? "SWI" : 
						product.getCustomer().getCompanyType()+". "+
							product.getCustomer().getCompanyLegalName();
				}  else {
					// when user click 'Edit' button for the material 
					// -- the product object is retrieved from database table
					InventoryProcessProduct productByProxy =
							getInventoryProcessDao().getProcessProductsCustomerByProxy(product.getId());
					customerName = productByProxy.getCustomer()==null ? "SWI" : 
						productByProxy.getCustomer().getCompanyType()+". "+
							productByProxy.getCustomer().getCompanyLegalName();
				}
				lc = new Listcell(customerName);
				lc.setParent(item);
				
				item.setValue(product);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$addProcessButton(Event event) throws Exception {
		// add to the listmodellist
		ListModelList productListModelList = (ListModelList) productInventoryListbox.getModel();
		
		InventoryProcessMaterialData materialData = new InventoryProcessMaterialData();
		materialData.setProcessMaterial(selMaterial);
		materialData.setMaterialRemainQuantity(
				selMaterial.getWeightQuantity().
				subtract(getTotalProductWeight(productListModelList)));
		materialData.setShearingLength(BigDecimal.ZERO);
		materialData.setShearingSheetQuantity(0);
		materialData.setShearingQuantityWeight(BigDecimal.ZERO);
		materialData.setShearingCustomer(null);
		
		Map<String, InventoryProcessMaterialData> args =
				Collections.singletonMap("materialData", materialData);

		Window processCuttingWin = (Window) Executions.createComponents(
				"/inventory/process/InventoryProcessDialogCutting.zul", null, args);
		
		processCuttingWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				InventoryProcessMaterialData materialData = (InventoryProcessMaterialData) event.getData();
				
				ListModelList productListModelList = (ListModelList) productInventoryListbox.getModel();
				productListModelList.addAll(materialData.getProcessProductList());
				
				// display quantity status of this material process after removal
				// -- must use the quantity from the listmodel
				updateQuantityStatus(productListModelList, selMaterial, selMaterialIndex);
				// enable / disable the add and remove process buttons
				addProcessButton.setDisabled((
						selMaterial.getWeightQuantity().subtract(
								getTotalProductWeight(productListModelList)).
									compareTo(BigDecimal.ZERO)==0));
				saveButton.setDisabled((				
						selMaterial.getWeightQuantity().subtract(
								getTotalProductWeight(productListModelList))).
									compareTo(BigDecimal.ZERO)>0);

			}
		});
		
		processCuttingWin.doModal();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$removeProcess(Event event) throws Exception {
		// remove row from listbox -- from the listmodellist
		ListModelList productListModelList = (ListModelList) productInventoryListbox.getModel();		
		int lastIndexToRemove = productListModelList.size()-1;
		productListModelList.remove(lastIndexToRemove);			
		
		// display quantity status of this material process after removal
		// -- must use the quantity from the listmodel
		updateQuantityStatus(productListModelList, selMaterial, selMaterialIndex);

		// enable / disable the add and remove process buttons
		addProcessButton.setDisabled((
				selMaterial.getWeightQuantity().subtract(
						getTotalProductWeight(productListModelList))).
							compareTo(BigDecimal.ZERO)==0);
		removeProcess.setDisabled(lastIndexToRemove==0);
		
		saveButton.setDisabled((				
				selMaterial.getWeightQuantity().subtract(
						getTotalProductWeight(productListModelList))).
							compareTo(BigDecimal.ZERO)>0);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$saveButton(Event event) throws Exception {
		ListModelList productListModelList = (ListModelList) productInventoryListbox.getModel();
		
		InventoryProcess inventoryProcessByProxy = 
				getInventoryProcessDao().getProcessMaterialsByProxy(getInventoryProcess().getId());		
		inventoryProcessByProxy.getProcessMaterials().clear();
		
		for (Listitem materialItem : materialInventoryListbox.getItems()) {
			InventoryProcessMaterial material = materialItem.getValue();
		
			if (material.getMarking().compareTo(selMaterial.getMarking())==0) {	
				InventoryProcessMaterial inventoryProcessMaterialByProxy =
						getInventoryProcessDao().getProcessProductsByProxy(material.getId());
				inventoryProcessMaterialByProxy.getProcessProducts().clear();
				inventoryProcessMaterialByProxy.getProcessProducts().addAll(productListModelList);
				// for (int i=0; i<productListModelList.size(); i++) {
				//	InventoryProcessProduct processProduct = (InventoryProcessProduct) productListModelList.get(i);
				//	System.out.println(processProduct);
				//	inventoryProcessMaterialByProxy.getProcessProducts().add(processProduct);
				// }		
				
				material.setProcessProducts(inventoryProcessMaterialByProxy.getProcessProducts());
			}
			inventoryProcessByProxy.getProcessMaterials().add(material);
		}
				
		Events.sendEvent(Events.ON_OK, inventoryProcessEditDialogWin, inventoryProcessByProxy);
		
		inventoryProcessEditDialogWin.detach();
	}

	private void updateQuantityStatus(List<InventoryProcessProduct> processProductList, InventoryProcessMaterial material, int materialIndex) {
		BigDecimal totalProductWeightQty = getTotalProductWeight(processProductList);
		
		// set the proses info label of the material to process
		// Proses: DY.978K (4.058,00 Kg) - SPCC - 2.00 x 1,200.00 x Coil - Sisa: 3,450Kg
		infoMaterialToProcessLabel.setValue("Proses: Material No."+(materialIndex+1)+" - "+
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
	
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryProcessEditDialogWin.detach();
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
