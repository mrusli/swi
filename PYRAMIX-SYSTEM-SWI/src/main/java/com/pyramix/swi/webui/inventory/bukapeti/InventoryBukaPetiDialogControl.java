package com.pyramix.swi.webui.inventory.bukapeti;

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
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiStatus;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;
import com.pyramix.swi.webui.inventory.InventoryData;
import com.pyramix.swi.webui.inventory.InventoryListInfoType;

public class InventoryBukaPetiDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4880465440602859055L;

	private SerialNumberGenerator serialNumberGenerator;
	private InventoryDao inventoryDao;
	
	private Window inventoryBukapetiDialogWin;
	private Tabbox bukapetiTabbox;
	private Datebox orderDatebox, completeDatebox;
	private Combobox bukapetiLocationCombobox, bukapetiStatusCombobox;
	private Textbox processNumberTextbox, noteTextbox;
	private Label infoMaterialLabel, infoMaterialToBukapetiLabel;
	private Listbox materialInventoryListbox, productInventoryListbox;
	private Button removeMaterialButton;
	
	private InventoryBukaPeti inventoryBukapeti;
	private List<Inventory> selectedMaterialList;
	private List<InventoryBukaPetiMaterial> processMaterialList = 
			new ArrayList<InventoryBukaPetiMaterial>();
	// private List<InventoryBukaPetiProduct> bukapetiProducts = 
	//		new ArrayList<InventoryBukaPetiProduct>();
	private List<InventoryBukaPetiEndProduct> bukapetiEndProducts =
			new ArrayList<InventoryBukaPetiEndProduct>();
	
	private final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.BUKAPETI_ORDER;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setInventoryBukapeti(
				(InventoryBukaPeti) arg.get("inventoryBukapeti"));
	}
	
	public void onCreate$inventoryBukapetiDialogWin(Event event) throws Exception {
		// init to 'material'
		bukapetiTabbox.setSelectedIndex(0);

		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());	
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());

		// always SWI
		bukapetiLocationCombobox.setValue("SWI");
		
		// permohonan
		bukapetiStatusCombobox.setValue(InventoryBukaPetiStatus.permohonan.toString());
		
		displayInventoryBukapetiInfo();
		
		// for the material ListInfoDialog so that selected material not come up again in the list dialog
		setSelectedMaterialList(new ArrayList<Inventory>());

		// disable the 'Hapus' material button
		// -- enable this button ONLY when a material is successfully added
		removeMaterialButton.setDisabled(true);
		// disable the 'save' button
		// -- enable this button after all materials have been processed
		// saveButton.setDisabled(true);
	}
	
	private void displayInventoryBukapetiInfo() {
		orderDatebox.setValue(asDate(getLocalDate()));
		processNumberTextbox.setValue("");
		completeDatebox.setValue(asDate(getLocalDate()));
		noteTextbox.setValue("");
		
		// reset all info
		infoMaterialLabel.setValue("");
		infoMaterialToBukapetiLabel.setValue("");

		// prepare the list for the material
		getInventoryBukapeti().setBukapetiMaterialList(
				new ArrayList<InventoryBukaPetiMaterial>());
		
	}

	public void onClick$addMaterialButton(Event event) throws Exception {
		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventoryListInfoType(InventoryListInfoType.BukaPeti);
		inventoryData.setInventoryLocation(InventoryLocation.swi);
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

				getInventoryBukapeti().getBukapetiMaterialList().add(
						inventoryToInventoryBukapetiMaterial(inventory));
				
				// for the ListInfoDialog getSelectedMaterialList() to not display the selected inventory
				// -- preventing the user from choosing the same inventory
				// -- helping user to not make mistake
				getSelectedMaterialList().add(inventory);

				// display
				displayInventoryMaterials(getInventoryBukapeti().getBukapetiMaterialList());
				
				// enable the 'hapus' material button
				// --- everytime a material is added, it can be removed
				removeMaterialButton.setDisabled(false);
				
				// disable the 'save' button
				// --- everytime a material is added, it must be processed first, before it's saved
				// saveButton.setDisabled(true);
			}

			private InventoryBukaPetiMaterial inventoryToInventoryBukapetiMaterial(Inventory inventory) {
				InventoryBukaPetiMaterial materialSel = new InventoryBukaPetiMaterial();
				
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
	
	protected void displayInventoryMaterials(List<InventoryBukaPetiMaterial> bukapetiMaterialList) {
		// number of materials / petian
		int materialCount = bukapetiMaterialList.size();		
		infoMaterialLabel.setValue("Materi: "+materialCount+" Item"); 
		
		materialInventoryListbox.setModel(
				new ListModelList<InventoryBukaPetiMaterial>(bukapetiMaterialList));
		materialInventoryListbox.setItemRenderer(materialInventoryListitemRenderer());
		
	}

	private ListitemRenderer<InventoryBukaPetiMaterial> materialInventoryListitemRenderer() {

		return new ListitemRenderer<InventoryBukaPetiMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiMaterial selMaterial, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(selMaterial.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(selMaterial.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(selMaterial.getThickness())+" x "+
						getFormatedFloatLocal(selMaterial.getWidth())+" x "+
						(selMaterial.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(selMaterial.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(selMaterial.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(selMaterial.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty(Sht/Line)
				lc = new Listcell(getFormatedInteger(selMaterial.getSheetQuantity()));
				lc.setParent(item);

				// proses bukapeti
				lc = initBukapeti(new Listcell(), selMaterial, index);
				lc.setParent(item);
				
				item.setValue(selMaterial);
			}

			private Listcell initBukapeti(Listcell lc, InventoryBukaPetiMaterial selMaterial, int index) {
				Button bukapetiButton = new Button();

				bukapetiButton.setLabel("Buka Peti");
				bukapetiButton.setClass("inventoryEditButton");				
				bukapetiButton.setDisabled(processMaterialList.contains(selMaterial));
				
				bukapetiButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// switch to 'Buka Peti' tab
						bukapetiTabbox.setSelectedIndex(1);
						
						// InventoryBukaPetiMaterial bukapetiMaterial =
						//		getInventoryBukapeti().getBukapetiMaterialList().get(index);

						// create empty arraylist container
						selMaterial.setBukapetiProductList(
								 new ArrayList<InventoryBukaPetiProduct>());

						// add bukapetiProduct 
						// -- attached to the material
						// -- one material to one product
						selMaterial.getBukapetiProductList().add(createBukapetiProduct(
								new InventoryBukaPetiProduct(), selMaterial));						

						// add bukapetiEndProduct
						// -- for display, regardless of which material selected, the products are always displayed
						// -- determined whether to add into the list 
						// -- if the products are same, the qtySht and qtyWeight will be accumulated -- no new product will be added	
						// -- for saving to inventory						
						addBukapetiEndProduct(selMaterial);
						
						// display bukapetiProduct
						displayInventoryBukapetiProducts(bukapetiEndProducts);

						// disable the 'Buka Peti' button
						// -- a material can only 'Buka Peti' once
						disableBukapetiProcessButton(index);
						
						// add the index of the material
						// -- so that the button will be disabled if the material is re-listed, such as adding new material
						// processIndexList.add(index);
						processMaterialList.add(selMaterial);
						
						// disable the 'Hapus' material button
						removeMaterialButton.setDisabled(checkMaterialListItem());
					}

					private InventoryBukaPetiProduct createBukapetiProduct(InventoryBukaPetiProduct bukapetiProduct, InventoryBukaPetiMaterial material) {
						bukapetiProduct.setBukapetiProductStatus(InventoryBukaPetiStatus.proses); 
						bukapetiProduct.setMarking(material.getMarking());
						bukapetiProduct.setInventoryCode(material.getInventoryCode());
						bukapetiProduct.setThickness(material.getThickness());
						bukapetiProduct.setWidth(material.getWidth());
						bukapetiProduct.setLength(material.getLength());
						bukapetiProduct.setSheetQuantity(material.getSheetQuantity());
						bukapetiProduct.setWeightQuantity(material.getWeightQuantity());
						bukapetiProduct.setInventoryPacking(InventoryPacking.lembaran);
						bukapetiProduct.setInventoryLocation(material.getInventoryLocation());
						bukapetiProduct.setPrinted(false);
						bukapetiProduct.setCustomer(null);
						bukapetiProduct.setMaterialIdRef(material.getId());
						bukapetiProduct.setContractNumber(material.getContractNumber());
						bukapetiProduct.setLcNumber(material.getLcNumber());
						bukapetiProduct.setUpdateToInventory(false);
						bukapetiProduct.setRevision(0);
						
						return bukapetiProduct;
					}
					
					private InventoryBukaPetiEndProduct createBukapetiEndProduct(InventoryBukaPetiEndProduct endProduct,
							InventoryBukaPetiMaterial selMaterial) {

						endProduct.setBukapetiProductStatus(InventoryBukaPetiStatus.proses);
						endProduct.setMarking(selMaterial.getMarking());
						endProduct.setInventoryCode(selMaterial.getInventoryCode());
						endProduct.setThickness(selMaterial.getThickness());
						endProduct.setWidth(selMaterial.getWidth());
						endProduct.setLength(selMaterial.getLength());
						endProduct.setSheetQuantity(selMaterial.getSheetQuantity());
						endProduct.setWeightQuantity(selMaterial.getWeightQuantity());
						endProduct.setInventoryPacking(InventoryPacking.lembaran);
						endProduct.setInventoryLocation(selMaterial.getInventoryLocation());
						endProduct.setContractNumber(selMaterial.getContractNumber());
						endProduct.setLcNumber(selMaterial.getLcNumber());
						endProduct.setUpdateToInventory(false);

						return endProduct;
					}

					private void addBukapetiEndProduct(InventoryBukaPetiMaterial selMaterial) {
						int bukapetiQtySht = 0;
						BigDecimal bukapetiQtyKg = BigDecimal.ZERO;
						
						for (InventoryBukaPetiEndProduct endProduct : bukapetiEndProducts) {
							if (isEndProductMatch(selMaterial, endProduct)) {
								
								bukapetiQtySht = endProduct.getSheetQuantity();
								bukapetiQtySht = bukapetiQtySht+selMaterial.getSheetQuantity();
								
								bukapetiQtyKg = endProduct.getWeightQuantity();
								bukapetiQtyKg = bukapetiQtyKg.add(selMaterial.getWeightQuantity());
								
								endProduct.setSheetQuantity(bukapetiQtySht);
								endProduct.setWeightQuantity(bukapetiQtyKg);
							}
						}
						
						if (bukapetiQtySht==0) {
							bukapetiEndProducts.add(createBukapetiEndProduct(
									new InventoryBukaPetiEndProduct(), selMaterial));
						}
					}

					private boolean isEndProductMatch(InventoryBukaPetiMaterial selMaterial, 
							InventoryBukaPetiEndProduct endProduct) {
						
						boolean isCodeMatch = selMaterial.getInventoryCode().getId().compareTo(endProduct.getInventoryCode().getId())==0;
						
						boolean isSpecMatch = 
								selMaterial.getThickness().compareTo(endProduct.getThickness())==0 && 
								selMaterial.getWidth().compareTo(endProduct.getWidth())==0 &&
								selMaterial.getLength().compareTo(endProduct.getLength())==0;
						
						return isCodeMatch && isSpecMatch;
					}
					
					private void disableBukapetiProcessButton(int index) {
						Listitem item = materialInventoryListbox.getItemAtIndex(index);
						Button bukapetiButton = (Button) item.getChildren().get(7).getFirstChild();
						
						// disable
						bukapetiButton.setDisabled(true);
					}

					private boolean checkMaterialListItem() {
						boolean disable = false;
						
						for (Listitem item: materialInventoryListbox.getItems()) {
							Button bukapetiButton = (Button) item.getChildren().get(7).getFirstChild();
							
							disable = bukapetiButton.isDisabled();
							
							if (!disable) {
								break;
							}
						}
						
						return disable;
					}
				});
				bukapetiButton.setParent(lc);
				
				return lc;
			}
		};
	}

	private void displayInventoryBukapetiProducts(List<InventoryBukaPetiEndProduct> endProductList) {
		productInventoryListbox.setModel(
				new ListModelList<InventoryBukaPetiEndProduct>(endProductList));
		productInventoryListbox.setItemRenderer(getProductInventoryBukapetiProductListitemRenderer());
		
	}
	
	private ListitemRenderer<InventoryBukaPetiEndProduct> getProductInventoryBukapetiProductListitemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Material - marking
				lc = new Listcell(endProduct.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(endProduct.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(endProduct.getThickness())+" x "+
						getFormatedFloatLocal(endProduct.getWidth())+" x "+
						(endProduct.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(endProduct.getLength())));
				lc.setStyle("white-space: nowrap;");
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
				lc = new Listcell("swi");
				lc.setParent(item);

				item.setValue(endProduct);
			}
		};
	}
	
	public void onClick$removeMaterialButton(Event event) throws Exception {
		int lastIndexToRemove = 0;
		int indexofLastSelectedMaterialFromInventory = 0;
		
		for(int i=getInventoryBukapeti().getBukapetiMaterialList().size(); i>0; i--) {
			Listitem item = materialInventoryListbox.getItemAtIndex(i-1);
			Button bukapetiButton = (Button) item.getChildren().get(7).getFirstChild();
			
			if (!bukapetiButton.isDisabled()) {
				lastIndexToRemove = i-1;
				
				// remove
				getInventoryBukapeti().getBukapetiMaterialList().remove(lastIndexToRemove);

				// NOTE: after a user select an inventory from the dialog, the selected inventory IS NOT in the
				//       list anymore.  By removing the inventory (as material), the same inventory must be 'returned'
				//       into the list.
				indexofLastSelectedMaterialFromInventory = lastIndexToRemove;
				getSelectedMaterialList().remove(indexofLastSelectedMaterialFromInventory);

				break;
			}
		}
		// display
		displayInventoryMaterials(
				getInventoryBukapeti().getBukapetiMaterialList());

		// enable the 'hapus' material button
		removeMaterialButton.setDisabled(getInventoryBukapeti().getBukapetiMaterialList().isEmpty());
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		// are all materials processed 'bukapeti'?		
  		for (Listitem item : materialInventoryListbox.getItems()) {
			Button bukapetiButton = (Button) item.getChildren().get(7).getFirstChild();
			
			// button isDisabled == false -- not processed 'bukapeti' yet
			if (!bukapetiButton.isDisabled()) {
				Messagebox.show("Tidak dapat Disimpan.  Belum Semua material dilakukan proses buka peti.",
					    "Warning", 
					    Messagebox.OK,  
					    Messagebox.EXCLAMATION);
				
				return;
			}
		}
 		
		getInventoryBukapeti().setOrderDate(orderDatebox.getValue());
		getInventoryBukapeti().setUpdatedDate(asDate(getLocalDate()));
		getInventoryBukapeti().setInventoryBukapetiStatus(InventoryBukaPetiStatus.permohonan);
		getInventoryBukapeti().setCompleteDate(completeDatebox.getValue());
		getInventoryBukapeti().setNote(noteTextbox.getValue());
		getInventoryBukapeti().setBukapetiNumber(createDocumentSerialNumber(DEFAULT_DOCUMENT_TYPE, orderDatebox.getValue()));
		getInventoryBukapeti().setInventoryList(getMaterialsWithRefToInventory());
		getInventoryBukapeti().setBukapetiEndProduct(bukapetiEndProducts);

		for (Inventory inventory : getInventoryBukapeti().getInventoryList()) {
			inventory.setInventoryStatus(InventoryStatus.bukapeti);
		}
		
		// do the save process
		// -- send the event to the caller
		Events.sendEvent(Events.ON_OK, inventoryBukapetiDialogWin, getInventoryBukapeti());
		
		inventoryBukapetiDialogWin.detach();
	}
	
/*	private List<InventoryBukaPetiEndProduct> bukapetiProductToEndProduct() {
		
		List<InventoryBukaPetiEndProduct> endproductList = new ArrayList<InventoryBukaPetiEndProduct>();
		
		for (InventoryBukaPetiProduct product : bukapetiProducts) {
			InventoryBukaPetiEndProduct endProduct = new InventoryBukaPetiEndProduct();
			endProduct.setBukapetiProductStatus(product.getBukapetiProductStatus());
			endProduct.setMarking(product.getMarking());
			endProduct.setInventoryCode(product.getInventoryCode());
			endProduct.setThickness(product.getThickness());
			endProduct.setWidth(product.getWidth());
			endProduct.setLength(product.getLength());
			endProduct.setSheetQuantity(product.getSheetQuantity());
			endProduct.setWeightQuantity(product.getWeightQuantity());
			endProduct.setInventoryPacking(product.getInventoryPacking());
			endProduct.setInventoryLocation(product.getInventoryLocation());
			endProduct.setContractNumber(product.getContractNumber());
			endProduct.setLcNumber(product.getLcNumber());
			endProduct.setUpdateToInventory(false);
			
			endproductList.add(endProduct);
		}
		
		return endproductList;
	}
*/
	private List<Inventory> getMaterialsWithRefToInventory() throws Exception {
		List<Inventory> materialInventoryRef = null;

		materialInventoryRef = new ArrayList<Inventory>();

		for (InventoryBukaPetiMaterial bukapetiMaterial : getInventoryBukapeti().getBukapetiMaterialList()) {
			Inventory inventoryRef =
					getInventoryDao().findInventoryById(bukapetiMaterial.getInventoryIdRef());
			
			materialInventoryRef.add(inventoryRef);		
		}
		
		return materialInventoryRef;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryBukapetiDialogWin.detach();
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
	
	public InventoryBukaPeti getInventoryBukapeti() {
		return inventoryBukapeti;
	}

	public void setInventoryBukapeti(InventoryBukaPeti inventoryBukapeti) {
		this.inventoryBukapeti = inventoryBukapeti;
	}

	public List<Inventory> getSelectedMaterialList() {
		return selectedMaterialList;
	}

	public void setSelectedMaterialList(List<Inventory> selectedMaterialList) {
		this.selectedMaterialList = selectedMaterialList;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
}
