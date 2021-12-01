package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;
import com.pyramix.swi.webui.inventory.InventoryData;
import com.pyramix.swi.webui.inventory.InventoryListInfoType;

public class InventoryProcessDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -272110113516027946L;

	private CompanyDao companyDao;
	private InventoryProcessDao inventoryProcessDao;
	private InventoryDao inventoryDao;
	private SerialNumberGenerator serialNumberGenerator;
	
	private Window inventoryProcessDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox processLocationCombobox, processStatusCombobox;
	private Listbox materialInventoryListbox, productInventoryListbox;
	private Tabbox processTabbox;
	private Label infoMaterialToProcessLabel, infoMaterialLabel, infoQtyProcessLabel;
	private Button addProcessButton, nextButton, saveButton, removeMaterialButton, addMaterialButton;
	
	private InventoryProcessData inventoryProcessData;
	private InventoryProcess inventoryProcess;
	private PageMode pageMode;
	
	private List<Inventory> selectedMaterialList;
	private InventoryProcessMaterial materialToProcess;
	private int materialIndex = 0;
	private int materialItem = 0; // how many materials to process
	private BigDecimal remainQuantity = BigDecimal.ZERO;
	private int processCount = 0; // number of processes for each material
	private InventoryProcessMaterialData materialData = null;
	
	private final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.PROCESS_ORDER;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryProcessData(
				(InventoryProcessData) arg.get("inventoryProcessData"));
	}
	
	public void onCreate$inventoryProcessDialogWin(Event event) throws Exception {
		// init to 'material'
		processTabbox.setSelectedIndex(0);
		// init material info
		infoMaterialToProcessLabel.setValue("");
		// init object to null
		setMaterialToProcess(null);
		
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
		// for the ListInfoDialog so that selected material not come up again in the list dialog
		setSelectedMaterialList(new ArrayList<Inventory>());
		
		// process location
		setupProcessLocationCombobox();
		
		// process status
		setupProcessStatusCombobox();
		
		setPageMode(getInventoryProcessData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			inventoryProcessDialogWin.setTitle("Merubah (Edit) Proses Inventory");
			// disable the 'Tambah' and 'Hapus' material button
			addMaterialButton.setDisabled(true);
			removeMaterialButton.setDisabled(true);
			break;
		case NEW:
			inventoryProcessDialogWin.setTitle("Membuat (Create) Proses Inventory");
			// disable the 'Hapus' material button
			// -- enable this button ONLY when a material is successfully added
			removeMaterialButton.setDisabled(true);
			// material not added yet
			// -- disable the next button
			nextButton.setDisabled(true);
			// disable the 'save' button
			// -- enable this button after all materials have been processed
			saveButton.setDisabled(true);
			break;
		case VIEW:
			inventoryProcessDialogWin.setTitle("Melihat (View) Proses Inventory");
			break;
		default:
			break;
		}
		
		setInventoryProcess(
				getInventoryProcessData().getInventoryProcess());
		
		displayInventoryProcessInfo();
	}

	private void setupProcessLocationCombobox() throws Exception {
		Comboitem item;
		
		List<Company> companyList = getCompanyDao().findAllCompany();
		
		for (Company company : companyList) {
			if (company.getId()>1) {
				item = new Comboitem();
				item.setLabel(company.getCompanyDisplayName());
				item.setValue(company);
				item.setParent(processLocationCombobox);
			}
		}
	}

	private void setupProcessStatusCombobox() {
		Comboitem item;
		
		for (InventoryProcessStatus status : InventoryProcessStatus.values()) {
			item = new Comboitem();
			item.setLabel(status.toString());
			item.setValue(status);
			item.setParent(processStatusCombobox);
		}
		
		// permohonan
		processStatusCombobox.setSelectedIndex(0);
	}

	private void displayInventoryProcessInfo() throws Exception {
		orderDatebox.setValue(asDate(getLocalDate()));
		processNumberTextbox.setValue("");
		completeDatebox.setValue(asDate(getLocalDate()));
		noteTextbox.setValue("");
		
		infoMaterialLabel.setValue("");
		infoMaterialToProcessLabel.setValue("");
		
		// init the material
		getInventoryProcess().setProcessMaterials(
				new ArrayList<InventoryProcessMaterial>());
		
	}

	private void displayInventoryMaterials(List<InventoryProcessMaterial> materialInventoryList) {
		// number of materials / coils
		int materialCount = materialInventoryList.size();		
		infoMaterialLabel.setValue("Materi: "+materialCount+" Item"); 
		
		materialInventoryListbox.setModel(
				new ListModelList<InventoryProcessMaterial>(materialInventoryList));
		materialInventoryListbox.setItemRenderer(
				getInventoryProcessSelectedListitemRenderer());
	}

	private ListitemRenderer<InventoryProcessMaterial> getInventoryProcessSelectedListitemRenderer() {
		
		return new ListitemRenderer<InventoryProcessMaterial>() {
			
			BigDecimal totalProductWeightQty, balanceQty;
			
			@Override
			public void render(Listitem item, InventoryProcessMaterial selMaterial, int index) throws Exception {
				Listcell lc;

				InventoryProcessMaterial processMaterialByProxy = null;
				
				if (selMaterial.getId().compareTo(Long.MIN_VALUE)==1) {
					processMaterialByProxy =
							getInventoryProcessDao().getProcessProductsByProxy(selMaterial.getId());			
				}

				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(selMaterial.getMarking());
				lc.setParent(item);
				
				// Kode				
				lc = initInventoryCode(new Listcell(), selMaterial);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(selMaterial.getThickness())+" x "+
						getFormatedFloatLocal(selMaterial.getWidth())+" x "+
						(selMaterial.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(selMaterial.getLength())));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(selMaterial.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(selMaterial.getWeightQuantity()));
				lc.setParent(item);
				
				// Proses Qty(Kg)
				if (selMaterial.getId().compareTo(Long.MIN_VALUE)==0) {
					totalProductWeightQty = BigDecimal.ZERO;
					lc = new Listcell(getFormatedFloatLocal(totalProductWeightQty));
				} else {
					lc = initProcessQtyKg(new Listcell(), selMaterial, processMaterialByProxy);
				}
				lc.setParent(item);
				
				// Proses Qty(Sht/Line)
				if (selMaterial.getId().compareTo(Long.MIN_VALUE)==0) {
					lc = new Listcell("0");
				} else {
					lc = initProcessQtySht(new Listcell(), selMaterial, processMaterialByProxy);
				}
				lc.setParent(item);
				
				// Sisa Qty(Kg)
				lc = initProcessBalanceQtyKg(new Listcell(), selMaterial);
				lc.setParent(item);				
				
				// process material
				lc = new Listcell();
						// initMaterialProcess(new Listcell(), selMaterial, processMaterialByProxy);
				lc.setParent(item);
				
				item.setValue(selMaterial);				
			}

			private Listcell initInventoryCode(Listcell listcell, InventoryProcessMaterial selInventory) {
				if (selInventory.getInventoryCode() == null) {
					listcell.setLabel("UNKNOWN");
					listcell.setStyle("background-color: red;");
					
					return listcell;
				} else {
					listcell.setLabel(selInventory.getInventoryCode().getProductCode());
				}
	
				return listcell;
			}

			private Listcell initProcessQtyKg(Listcell listcell, InventoryProcessMaterial selInventory, InventoryProcessMaterial processMaterialByProxy) throws Exception {
				totalProductWeightQty = BigDecimal.ZERO;
				for (InventoryProcessProduct product : processMaterialByProxy.getProcessProducts()) {
					totalProductWeightQty = totalProductWeightQty.add(product.getWeightQuantity());
				}
				
				listcell.setLabel(getFormatedFloatLocal(totalProductWeightQty));
				
				return listcell;
			}

			private Listcell initProcessQtySht(Listcell listcell, InventoryProcessMaterial selInventory, InventoryProcessMaterial processMaterialByProxy) throws Exception {
				int totalQtySht = 0;
				for (InventoryProcessProduct product : processMaterialByProxy.getProcessProducts()) {
					totalQtySht = totalQtySht + product.getSheetQuantity();
				}
				
				listcell.setLabel(getFormatedInteger(totalQtySht));
				
				return listcell;
			}
			
			private Listcell initProcessBalanceQtyKg(Listcell listcell, InventoryProcessMaterial selInventory) {
				balanceQty = selInventory.getWeightQuantity().subtract(totalProductWeightQty);
				
				listcell.setLabel(getFormatedFloatLocal(balanceQty));
				
				return listcell;
			}
			
		};
	}
	
	private ListitemRenderer<InventoryProcessProduct> getInventoryProcessProductsListitemRenderer() {

		return new ListitemRenderer<InventoryProcessProduct>() {
			
			@Override
			public void render(Listitem item, InventoryProcessProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.Coil
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
				lc.setValue(product.getWeightQuantity());
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(product.getInventoryPacking().toString());
				lc.setParent(item);

				// Customer
				String customerName = "";
				if (product.getId()!=Long.MIN_VALUE) {
					InventoryProcessProduct productByProxy =
							getInventoryProcessDao().getProcessProductsCustomerByProxy(product.getId());
					customerName = productByProxy.getCustomer()==null ? "SWI" : 
						productByProxy.getCustomer().getCompanyType()+". "+
							productByProxy.getCustomer().getCompanyLegalName();
				} else {
					customerName = product.getCustomer()==null ? "SWI" :
					product.getCustomer().getCompanyType()+". "+
						product.getCustomer().getCompanyLegalName();
				}
				lc = new Listcell(customerName);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// edit
				lc = initProductEdit(new Listcell(), product);
				lc.setParent(item);
				
				item.setValue(product);				
			}

			private Listcell initProductEdit(Listcell listcell, InventoryProcessProduct product) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, InventoryProcessProduct> args = Collections.singletonMap("processProduct", product);
						
						Window productEditDialogWin = 
								(Window) Executions.createComponents("/inventory/process/InventoryProcessProductEditDialog.zul", null, args);
						
						productEditDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								InventoryProcessProduct editedProduct = (InventoryProcessProduct) event.getData();
												
								product.setWidth(editedProduct.getWidth());
								product.setLength(editedProduct.getLength());
								product.setSheetQuantity(editedProduct.getSheetQuantity());
								product.setWeightQuantity(editedProduct.getWeightQuantity());
								product.setCustomer(editedProduct.getCustomer());
								
								// display into the listbox
								productInventoryListbox.setModel(
										new ListModelList<InventoryProcessProduct>(
												getMaterialToProcess().getProcessProducts()));
								productInventoryListbox.setItemRenderer(getInventoryProcessProductsListitemRenderer());	

								BigDecimal totalProductWeightQty = BigDecimal.ZERO;
								for (InventoryProcessProduct product : getMaterialToProcess().getProcessProducts()) {
									totalProductWeightQty = totalProductWeightQty.add(product.getWeightQuantity());
								}

								// remainQty -- re-calc the remainQty after process products are modified
								remainQuantity = getMaterialToProcess().getWeightQuantity();
								
								// remain qty
								remainQuantity = remainQuantity.subtract(totalProductWeightQty);
								
								// reset the proses info label
								// Proses: DY.978K (4.058,00 Kg) - SPCC - 2.00 x 1,200.00 x Coil - Sisa: 3,450Kg
/*	
 * 								Display NOT right after removing process and then edit							
 * 
 * 								infoMaterialToProcessLabel.setValue("Proses: "+
										getMaterialToProcess().getMarking()+" ("+
										getFormatedFloatLocal(getMaterialToProcess().getWeightQuantity())+" Kg.) - "+
										getMaterialToProcess().getInventoryCode().getProductCode()+" - "+
										getFormatedFloatLocal(getMaterialToProcess().getThickness())+" x "+
										getFormatedFloatLocal(getMaterialToProcess().getWidth())+" x "+
										"Coil - Sisa: "+
										getFormatedFloatLocal(remainQuantity)+" Kg.");
*/
								// Total Proses: 8,880Kg
								infoQtyProcessLabel.setValue("Total Proses: "+getFormatedFloatLocal(totalProductWeightQty)+" Kg.");

								// disable the button if the remainQuantity is not zero
								saveButton.setDisabled(!(remainQuantity.compareTo(BigDecimal.ZERO)==0));
							}
						});
						
						productEditDialogWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}
		
	public void onClick$addMaterialButton(Event event) throws Exception {
		if (processLocationCombobox.getSelectedItem()==null) {
			Messagebox.show("Belum Memilih Lokasi Proses.",
				    "Error", 
				    Messagebox.OK,  
				    Messagebox.ERROR);
			
			return;
		}
		
		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventoryListInfoType(InventoryListInfoType.Process);
		inventoryData.setInventoryLocation(getUserSelectedLocation());
		inventoryData.setSelectedInventoryList(getSelectedMaterialList());

		Map<String, InventoryData> arg =
				Collections.singletonMap("inventoryData", inventoryData);

		Window inventoryDialogWin = 
				(Window) Executions.createComponents("/inventory/InventoryListInfoDialog.zul", null, arg);

		inventoryDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the selected inventory
				Inventory inventory = (Inventory) event.getData();
				
				getInventoryProcess().getProcessMaterials().add(
						inventoryToInventoryProcessMaterial(inventory));
					
				// for the ListInfoDialog getSelectedMaterialList() to not display the selected inventory
				// -- preventing the user from choosing the same inventory
				// -- helping user to not make mistake
				getSelectedMaterialList().add(inventory);
					
				// display
				displayInventoryMaterials(getInventoryProcess().getProcessMaterials());
				// enable the 'hapus' material button
				removeMaterialButton.setDisabled(false);
				// allow user to click
				nextButton.setDisabled(false);				
			}

			private InventoryProcessMaterial inventoryToInventoryProcessMaterial(Inventory inventory) {
				InventoryProcessMaterial materialSel = new InventoryProcessMaterial();
				
				materialSel.setMarking(inventory.getMarking());
				materialSel.setInventoryCode(inventory.getInventoryCode());
				materialSel.setThickness(inventory.getThickness());
				materialSel.setWidth(inventory.getWidth());
				materialSel.setLength(inventory.getLength());
				materialSel.setInventoryPacking(inventory.getInventoryPacking());
				materialSel.setWeightQuantity(inventory.getWeightQuantity());
				materialSel.setSheetQuantity(inventory.getSheetQuantity());
				materialSel.setInventoryLocation(inventory.getInventoryLocation());
				materialSel.setInventoryIdRef(inventory.getId());
				materialSel.setContractNumber(inventory.getContractNumber());
				materialSel.setLcNumber(inventory.getLcNumber());
				
				return materialSel;
			}
		});
		
		inventoryDialogWin.doModal();
	}
	
	private InventoryLocation getUserSelectedLocation() {
		Company selCompany =
				processLocationCombobox.getSelectedItem().getValue();		
		switch (selCompany.getId().intValue()) {
		case 2:
			return InventoryLocation.sunter;
		case 3:
			return InventoryLocation.karawang;
		default:
			return null;
		}
	}

	public void onClick$removeMaterialButton(Event event) throws Exception {
		int lastIndexToRemove = 0;
		int indexofLastSelectedMaterialFromInventory = 0;
		
		// new
		lastIndexToRemove = 
				getInventoryProcess().getProcessMaterials().size() - 1;
			
		// remove
		getInventoryProcess().getProcessMaterials().remove(lastIndexToRemove);
			
		// display
		displayInventoryMaterials(
				getInventoryProcess().getProcessMaterials());
			
		// NOTE: after a user select an inventory from the dialog, the selected inventory IS NOT in the
		//       list anymore.  By removing the inventory (as material), the same inventory must be 'returned'
		//       into the list.
		indexofLastSelectedMaterialFromInventory = getSelectedMaterialList().size() - 1;
		getSelectedMaterialList().remove(indexofLastSelectedMaterialFromInventory);
		
		// disable the 'Hapus' material button when there's no more material to remove
		removeMaterialButton.setDisabled(lastIndexToRemove==0);
		nextButton.setDisabled(lastIndexToRemove==0);
	}
	
	public void onClick$nextButton(Event event) throws Exception {
		// switch to 'process' tab
		processTabbox.setSelectedIndex(1);

		// how many materials to process
		materialItem = getInventoryProcess().getProcessMaterials().size();
		// set material
		setMaterialToProcess(getInventoryProcess().getProcessMaterials().get(materialIndex));
		// set the remain qty to the material weight
		remainQuantity = getMaterialToProcess().getWeightQuantity();
		// inform user of the material to process
		infoMaterialToProcessLabel.setValue("Proses: Material No."+(materialIndex+1)+" - "+
				getMaterialToProcess().getMarking()+" ("+
				getFormatedFloatLocal(getMaterialToProcess().getWeightQuantity())+" Kg.) - "+
				getMaterialToProcess().getInventoryCode().getProductCode()+" - "+
				getFormatedFloatLocal(getMaterialToProcess().getThickness())+" x "+
				getFormatedFloatLocal(getMaterialToProcess().getWidth())+" x "+
				"Coil - Sisa: "+
				getFormatedFloatLocal(remainQuantity)+" Kg.");
		// prepare the 'placeholder' for the products
		getMaterialToProcess().setProcessProducts(new ArrayList<InventoryProcessProduct>());
		
		// disable the location
		processLocationCombobox.setDisabled(true);
		// disable the nextButton
		nextButton.setDisabled(true);
	}
	
	public void onClick$addProcessButton(Event event) throws Exception {
		if (processCount==0) {
			materialData = new InventoryProcessMaterialData();
			materialData.setProcessMaterial(getMaterialToProcess());
			materialData.setMaterialRemainQuantity(remainQuantity);
			materialData.setShearingLength(BigDecimal.ZERO);
			materialData.setShearingSheetQuantity(0);
			materialData.setShearingQuantityWeight(BigDecimal.ZERO);
			materialData.setShearingCustomer(null);
		} else {
			materialData.setMaterialRemainQuantity(remainQuantity);
		}
		
		Map<String, InventoryProcessMaterialData> args =
				Collections.singletonMap("materialData", materialData);

		Window processMaterialWin = (Window) Executions.createComponents(
				"/inventory/process/InventoryProcessDialogCutting.zul", null, args);
		
		processMaterialWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				materialData = (InventoryProcessMaterialData) event.getData();
				
				List<InventoryProcessProduct> userProductList = 
						materialData.getProcessProductList();
				BigDecimal totalProductWeightQty = BigDecimal.ZERO;
				
				getMaterialToProcess().getProcessProducts().addAll(userProductList);

				for (InventoryProcessProduct product : getMaterialToProcess().getProcessProducts()) {
					totalProductWeightQty = totalProductWeightQty.add(product.getWeightQuantity());
				}

				// remainQty -- re-calc the remainQty after process products are added
				remainQuantity = getMaterialToProcess().getWeightQuantity();
				
				// remain qty
				remainQuantity = remainQuantity.subtract(totalProductWeightQty);
				
				// set the proses info label (which item to process)
				// Proses: DY.978K (4.058,00 Kg) - SPCC - 2.00 x 1,200.00 x Coil - Sisa: 3,450Kg
				infoMaterialToProcessLabel.setValue("Proses: Material No."+(materialIndex+1)+" - "+
						getMaterialToProcess().getMarking()+" ("+
						getFormatedFloatLocal(getMaterialToProcess().getWeightQuantity())+" Kg.) - "+
						getMaterialToProcess().getInventoryCode().getProductCode()+" - "+
						getFormatedFloatLocal(getMaterialToProcess().getThickness())+" x "+
						getFormatedFloatLocal(getMaterialToProcess().getWidth())+" x "+
						"Coil - Sisa: "+
						getFormatedFloatLocal(remainQuantity)+" Kg.");

				// list the product for this material -- if any
				// display into the listbox
				productInventoryListbox.setModel(
						new ListModelList<InventoryProcessProduct>(
								getMaterialToProcess().getProcessProducts()));
				productInventoryListbox.setItemRenderer(getInventoryProcessProductsListitemRenderer());	
				
				// Total Proses: 8,880Kg
				infoQtyProcessLabel.setValue("Total Proses: "+getFormatedFloatLocal(totalProductWeightQty)+" Kg.");
				
				if (remainQuantity.setScale(0, BigDecimal.ROUND_HALF_DOWN).compareTo(BigDecimal.ZERO)==0) {
					// selected material has been completedly processed
					materialIndex++;
					if (materialIndex < materialItem) {
						// allows user to process the next material
						setMaterialToProcess(getInventoryProcess().getProcessMaterials().get(materialIndex));
						// prepare the 'placeholder' for the products
						getMaterialToProcess().setProcessProducts(new ArrayList<InventoryProcessProduct>());
						// set the remain qty to the material weight
						remainQuantity = getMaterialToProcess().getWeightQuantity();
						// inform user of the material to process
						infoMaterialToProcessLabel.setValue("Proses: Material No."+(materialIndex+1)+" - "+
								getMaterialToProcess().getMarking()+" ("+
								getFormatedFloatLocal(getMaterialToProcess().getWeightQuantity())+" Kg.) - "+
								getMaterialToProcess().getInventoryCode().getProductCode()+" - "+
								getFormatedFloatLocal(getMaterialToProcess().getThickness())+" x "+
								getFormatedFloatLocal(getMaterialToProcess().getWidth())+" x "+
								"Coil - Sisa: "+
								getFormatedFloatLocal(remainQuantity)+" Kg.");
						processCount = 0;
					} else {
						// all materials had been processed
						// -- display all products
						displayAllProcessProducts();
						// Total Proses: 8,880Kg
						infoQtyProcessLabel.setValue("Total Proses: "+
								getFormatedFloatLocal(getTotalProcessProductWeightQty())+" Kg.");
						// clear the infoMaterialToProcess label
						infoMaterialToProcessLabel.setValue("");
						// -- enable this button because all materials have been processed
						saveButton.setDisabled(false);
						// -- disable this button because all materials have been processed
						addProcessButton.setDisabled(true);
					}
				} else {
					// allows user to process the same material
					processCount++;
				}
			}

			private void displayAllProcessProducts() {
				List<InventoryProcessProduct> processProductList = new ArrayList<InventoryProcessProduct>();
				
				for (InventoryProcessMaterial processMaterial : getInventoryProcess().getProcessMaterials()) {
					for (InventoryProcessProduct processProduct : processMaterial.getProcessProducts()) {
						processProductList.add(processProduct);
					}
				}
				
				productInventoryListbox.setModel(
						new ListModelList<InventoryProcessProduct>(processProductList));
				productInventoryListbox.setItemRenderer(
						getInventoryProcessProductsListitemRenderer());					
			}
			
			private BigDecimal getTotalProcessProductWeightQty() {
				BigDecimal processProductWeight = BigDecimal.ZERO;
				
				for (InventoryProcessMaterial processMaterial : getInventoryProcess().getProcessMaterials()) {
					for (InventoryProcessProduct processProduct : processMaterial.getProcessProducts()) {
						processProductWeight = processProductWeight.add(processProduct.getWeightQuantity());
					}
				}
				
				return processProductWeight;
			}

		});
		processMaterialWin.doModal();
	}

	public void onClick$removeProcess(Event event) throws Exception {
		if (getMaterialToProcess().getProcessProducts().size()==0) {
			
			return;
		}
		
		int lastIndex = getMaterialToProcess().getProcessProducts().size()-1;
		
		getMaterialToProcess().getProcessProducts().remove(lastIndex);

		BigDecimal totalProductWeightQty = BigDecimal.ZERO;
		for (InventoryProcessProduct product : getMaterialToProcess().getProcessProducts()) {
			totalProductWeightQty = totalProductWeightQty.add(product.getWeightQuantity());
		}

		// remainQty -- re-calc the remainQty after process products are added
		remainQuantity = getMaterialToProcess().getWeightQuantity();
		
		// remain qty
		remainQuantity = remainQuantity.subtract(totalProductWeightQty);

/*		
 * 		Info is NOT right.  After removing the process the material indicates Material #2, 
 * 		although it's just 1 material.
 * 
 * 		infoMaterialToProcessLabel.setValue("Proses: Material No."+(materialIndex+1)+" - "+
				getMaterialToProcess().getMarking()+" ("+
				getFormatedFloatLocal(getMaterialToProcess().getWeightQuantity())+" Kg.) - "+
				getMaterialToProcess().getInventoryCode().getProductCode()+" - "+
				getFormatedFloatLocal(getMaterialToProcess().getThickness())+" x "+
				getFormatedFloatLocal(getMaterialToProcess().getWidth())+" x "+
				"Coil - Sisa: "+
				getFormatedFloatLocal(remainQuantity)+" Kg.");
*/
		// list the product for this material -- if any
		// display into the listbox
		productInventoryListbox.setModel(
				new ListModelList<InventoryProcessProduct>(
						getMaterialToProcess().getProcessProducts()));
		productInventoryListbox.setItemRenderer(getInventoryProcessProductsListitemRenderer());	
		
		// Total Proses: 8,880Kg
		infoQtyProcessLabel.setValue("Total Proses: "+getFormatedFloatLocal(totalProductWeightQty)+" Kg.");
		
		addProcessButton.setDisabled(remainQuantity.compareTo(BigDecimal.ZERO)==0);
		saveButton.setDisabled(remainQuantity.compareTo(BigDecimal.ZERO)!=0);
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		InventoryProcess userModProcess = getUserModifiedInventoryProcess();

		// save OR update -- depending on the caller of this dialog window
		Events.sendEvent(Events.ON_OK, inventoryProcessDialogWin, userModProcess);

		// disable the 'Tambah' dan 'Hapus' material button
		addMaterialButton.setDisabled(true);
		removeMaterialButton.setDisabled(true);
		// prevent multiple saves / updates
		saveButton.setDisabled(true);
		
		inventoryProcessDialogWin.detach();		
	}

	private InventoryProcess getUserModifiedInventoryProcess() throws Exception {
		InventoryProcess userModProcess = getInventoryProcess();

		userModProcess.setProcessNumber(createDocumentSerialNumber(DEFAULT_DOCUMENT_TYPE, orderDatebox.getValue()));
		userModProcess.setProcessedByCo(processLocationCombobox.getSelectedItem().getValue());			
		userModProcess.setOrderDate(orderDatebox.getValue());
		userModProcess.setCompleteDate(completeDatebox.getValue());
		userModProcess.setProcessStatus(processStatusCombobox.getSelectedItem().getValue());
		userModProcess.setNote(noteTextbox.getValue());

		userModProcess.setInventoryList(getUserModifiedProcessMaterialsWithRefToInventory());

		// CHANGE / UPDATE Inventory Status to 'process'
		for (Inventory inventory : userModProcess.getInventoryList()) {
			inventory.setInventoryStatus(InventoryStatus.process);
		}
		
		return userModProcess;
	}

	private List<Inventory> getUserModifiedProcessMaterialsWithRefToInventory() throws Exception {
		List<Inventory> materialInventoryRef = null;

		materialInventoryRef = new ArrayList<Inventory>();

		for (InventoryProcessMaterial processMaterial : getInventoryProcess().getProcessMaterials()) {
			Inventory inventoryRef = 
					getInventoryDao().findInventoryById(processMaterial.getInventoryIdRef());
			
			materialInventoryRef.add(inventoryRef);
		}
		
		return materialInventoryRef;
	}	
	
	public void onClick$cancelButton(Event event) throws Exception {
		Events.sendEvent(Events.ON_CANCEL, inventoryProcessDialogWin, null);
		
		inventoryProcessDialogWin.detach();
	}
	
	private DocumentSerialNumber createDocumentSerialNumber(DocumentType documentType, Date currentDate) throws Exception {
		int serialNum =	getSerialNumberGenerator().getSerialNumber(documentType, currentDate);
		
		DocumentSerialNumber documentSerNum = new DocumentSerialNumber();
		documentSerNum.setDocumentType(documentType);
		documentSerNum.setSerialDate(currentDate);
		documentSerNum.setSerialNo(serialNum);
		documentSerNum.setSerialComp(formatSerialComp(
			documentType.toCode(documentType.getValue()), currentDate, serialNum));
		
		return documentSerNum;
	}
	
	public InventoryProcessData getInventoryProcessData() {
		return inventoryProcessData;
	}

	public void setInventoryProcessData(InventoryProcessData inventoryProcessData) {
		this.inventoryProcessData = inventoryProcessData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}

	public List<Inventory> getSelectedMaterialList() {
		return selectedMaterialList;
	}

	public void setSelectedMaterialList(List<Inventory> selectedInventoryList) {
		this.selectedMaterialList = selectedInventoryList;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public InventoryProcessMaterial getMaterialToProcess() {
		return materialToProcess;
	}

	public void setMaterialToProcess(InventoryProcessMaterial materialToProcess) {
		this.materialToProcess = materialToProcess;
	}

/*	public List<InventoryProcessProduct> getProcessProductList() {
		return processProductList;
	}

	public void setProcessProductList(List<InventoryProcessProduct> processProductList) {
		this.processProductList = processProductList;
	}
*/
	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

/*	public List<InventoryProcessMaterial> getProcessMaterialList() {
		return processMaterialList;
	}

	public void setProcessMaterialList(List<InventoryProcessMaterial> processMaterialList) {
		this.processMaterialList = processMaterialList;
	}
*/
}
