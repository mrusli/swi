package com.pyramix.swi.webui.inventory.transfer;

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
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferMaterial;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;
import com.pyramix.swi.webui.inventory.InventoryData;
import com.pyramix.swi.webui.inventory.InventoryListInfoType;

public class InventoryTransferDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1092734765173802116L;

	private CompanyDao companyDao;
	private SerialNumberGenerator serialNumberGenerator;
	private InventoryDao inventoryDao;
	
	private Window inventoryTransferDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox transferLocationCombobox, transferStatusCombobox;
	private Label infoMaterialLabel, infoMaterialToTransferLabel;
	private Listbox materialInventoryListbox, productInventoryListbox;
	private Tabbox transferTabbox;
	private Button removeMaterialButton;
	
	private InventoryTransfer inventoryTransfer;
	private List<Inventory> selectedMaterialList;
	private List<InventoryTransferMaterial> processMaterialList =
			new ArrayList<InventoryTransferMaterial>();
	private List<InventoryTransferEndProduct> transferEndProducts =
			new ArrayList<InventoryTransferEndProduct>();
	
	private final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.TRANSFER_ORDER;	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransfer(
				(InventoryTransfer) arg.get("inventoryTransfer"));
	}

	public void onCreate$inventoryTransferDialogWin(Event event) throws Exception {
		// init to material
		transferTabbox.setSelectedIndex(0);
		
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
		// for the material ListInfoDialog so that selected material not come up again in the list dialog
		setSelectedMaterialList(new ArrayList<Inventory>());		
		
		// process location
		setupProcessLocationCombobox();
		
		// process status
		setupProcessStatusCombobox();
		
		// display
		displayInventoryTransferInfo();

	}

	private void setupProcessLocationCombobox() throws Exception {
		Comboitem item;
		
		List<Company> companyList = getCompanyDao().findAllCompany();
		
		for (Company company : companyList) {
			if (company.getId()>1) {
				item = new Comboitem();
				item.setLabel(company.getCompanyDisplayName());
				item.setValue(company);
				item.setParent(transferLocationCombobox);
			}
		}		
	}

	private void setupProcessStatusCombobox() {
		Comboitem item;
		
		for (InventoryProcessStatus status : InventoryProcessStatus.values()) {
			item = new Comboitem();
			item.setLabel(status.toString());
			item.setValue(status);
			item.setParent(transferStatusCombobox);
		}
		
		// permohonan
		transferStatusCombobox.setSelectedIndex(0);
	}

	private void displayInventoryTransferInfo() {
		orderDatebox.setValue(asDate(getLocalDate()));
		processNumberTextbox.setValue("");
		completeDatebox.setValue(asDate(getLocalDate()));
		noteTextbox.setValue("");

		// init material info
		infoMaterialLabel.setValue("");
		// init transfer end product info
		infoMaterialToTransferLabel.setValue("");
		
		// prepare the list for the material
		getInventoryTransfer().setTransferMaterialList(
				new ArrayList<InventoryTransferMaterial>());
	}

	public void onClick$addMaterialButton(Event event) throws Exception {
		if (transferLocationCombobox.getSelectedItem()==null) {
			Messagebox.show("Belum Memilih Transfer Lokasi.",
				    "Error", 
				    Messagebox.OK,  
				    Messagebox.ERROR);
			
			return;
		}
		
		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventoryListInfoType(InventoryListInfoType.Transfer);
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

				getInventoryTransfer().getTransferMaterialList().add(
						inventoryToInventoryTransferMaterial(inventory));
				
				// for the ListInfoDialog getSelectedMaterialList() to not display the selected inventory
				// -- preventing the user from choosing the same inventory
				// -- helping user to not make mistake
				getSelectedMaterialList().add(inventory);
				
				// display
				displayInventoryMaterials(getInventoryTransfer().getTransferMaterialList());
				
				// enable the 'hapus' material button
				// --- everytime a material is added, it can be removed
				removeMaterialButton.setDisabled(false);				
			}

			private InventoryTransferMaterial inventoryToInventoryTransferMaterial(Inventory inventory) {
				InventoryTransferMaterial materialSel = new InventoryTransferMaterial();
				
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

	protected void displayInventoryMaterials(List<InventoryTransferMaterial> transferMaterialList) {
		// number of materials / petian
		int materialCount = transferMaterialList.size();		
		infoMaterialLabel.setValue("Materi: "+materialCount+" Item"); 

		materialInventoryListbox.setModel(
				new ListModelList<InventoryTransferMaterial>(transferMaterialList));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryListitemRenderer());
	}

	private ListitemRenderer<InventoryTransferMaterial> getMaterialInventoryListitemRenderer() {

		return new ListitemRenderer<InventoryTransferMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryTransferMaterial selMaterial, int index) throws Exception {
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
				
				// Lokasi
				lc = new Listcell(selMaterial.getInventoryLocation().toString());
				lc.setParent(item);
				
				// proses Transfer
				lc = initTransfer(new Listcell(), selMaterial, index);
				lc.setParent(item);
				
				item.setValue(selMaterial);

			}

			private Listcell initTransfer(Listcell listcell, InventoryTransferMaterial selMaterial, int index) {
				Button transferButton = new Button();
				transferButton.setLabel("Transfer");
				transferButton.setClass("inventoryEditButton");				
				transferButton.setDisabled(processMaterialList.contains(selMaterial));
				transferButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// switch to 'Transfer Ke' tab
						transferTabbox.setSelectedIndex(1);
						
						transferEndProducts.add(createTransferEndProduct(
								new InventoryTransferEndProduct(), selMaterial));
						
						displayInventoryTransferEndProducts(transferEndProducts);
						
						// disable the 'Transfer' button
						// -- a material can only 'Transfer' once
						disableTransferButton(index);
						
						// add the index of the material
						// -- so that the button will be disabled if the material is re-listed, such as adding new material
						processMaterialList.add(selMaterial);

					}

					private void disableTransferButton(int index) {
						Listitem item = materialInventoryListbox.getItemAtIndex(index);
						Button transferButton = (Button) item.getChildren().get(8).getFirstChild();
						
						// disable
						transferButton.setDisabled(true);						
					}

					private InventoryTransferEndProduct createTransferEndProduct(
							InventoryTransferEndProduct endProduct,
							InventoryTransferMaterial material) {

						endProduct.setTransferProductStatus(InventoryTransferStatus.proses); 
						endProduct.setMarking(material.getMarking());
						endProduct.setInventoryCode(material.getInventoryCode());
						endProduct.setThickness(material.getThickness());
						endProduct.setWidth(material.getWidth());
						endProduct.setLength(material.getLength());
						endProduct.setSheetQuantity(material.getSheetQuantity());
						endProduct.setWeightQuantity(material.getWeightQuantity());
						endProduct.setInventoryPacking(material.getInventoryPacking());
						endProduct.setInventoryLocation(InventoryLocation.swi);
						endProduct.setContractNumber(material.getContractNumber());
						endProduct.setLcNumber(material.getLcNumber());
						endProduct.setUpdateInventory(false);
						endProduct.setInventoryIdRef(material.getInventoryIdRef());
						
						return endProduct;
					}

				});
				transferButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	private void displayInventoryTransferEndProducts(
			List<InventoryTransferEndProduct> transferEndProductList) {

		productInventoryListbox.setModel(
				new ListModelList<InventoryTransferEndProduct>(transferEndProductList));
		productInventoryListbox.setItemRenderer(getProductInventoryListitemRenderer());
		
	}

	private ListitemRenderer<InventoryTransferEndProduct> getProductInventoryListitemRenderer() {

		return new ListitemRenderer<InventoryTransferEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryTransferEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Material
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
				
				// Lokasi
				lc = new Listcell("SWI");
				lc.setParent(item);
				
				item.setValue(endProduct);
			}
		};
	}

	private InventoryLocation getUserSelectedLocation() {
		Company selCompany =
				transferLocationCombobox.getSelectedItem().getValue();		
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
		
		for(int i=getInventoryTransfer().getTransferMaterialList().size(); i>0; i--) {
			Listitem item = materialInventoryListbox.getItemAtIndex(i-1);
			Button bukapetiButton = (Button) item.getChildren().get(8).getFirstChild();
			
			if (!bukapetiButton.isDisabled()) {
				lastIndexToRemove = i-1;
				
				// remove
				getInventoryTransfer().getTransferMaterialList().remove(lastIndexToRemove);

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
				getInventoryTransfer().getTransferMaterialList());

		// enable the 'hapus' material button
		removeMaterialButton.setDisabled(getInventoryTransfer().getTransferMaterialList().isEmpty());
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		// are all materials processed 'bukapeti'?		
  		for (Listitem item : materialInventoryListbox.getItems()) {
			Button transferButton = (Button) item.getChildren().get(8).getFirstChild();
			
			// button isDisabled == false -- not processed 'bukapeti' yet
			if (!transferButton.isDisabled()) {
				Messagebox.show("Tidak dapat Disimpan.  Belum Semua material di transfer.",
					    "Warning", 
					    Messagebox.OK,  
					    Messagebox.EXCLAMATION);
				
				return;
			}
		}

  		getInventoryTransfer().setOrderDate(orderDatebox.getValue());
		getInventoryTransfer().setUpdatedDate(asDate(getLocalDate()));
		getInventoryTransfer().setInventoryTransferStatus(InventoryTransferStatus.permohonan);
		getInventoryTransfer().setCompleteDate(completeDatebox.getValue());
		getInventoryTransfer().setNote(noteTextbox.getValue());
		getInventoryTransfer().setTransferNumber(createDocumentSerialNumber(DEFAULT_DOCUMENT_TYPE, orderDatebox.getValue()));
		getInventoryTransfer().setTransferFromCo(transferLocationCombobox.getSelectedItem().getValue());
		getInventoryTransfer().setInventoryList(getMaterialsWithRefToInventory());
		getInventoryTransfer().setTransferEndProductList(transferEndProducts);

		// update inventory -- set status to transfer
		// -- once the transfer is complete, status is changed back to 'ready'
		for (Inventory inventory : getInventoryTransfer().getInventoryList()) {
			inventory.setInventoryStatus(InventoryStatus.transfer);
		}
		
		// do the save process
		// -- send the event to the caller
		Events.sendEvent(Events.ON_OK, inventoryTransferDialogWin, getInventoryTransfer());
		
		inventoryTransferDialogWin.detach();

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

	private List<Inventory> getMaterialsWithRefToInventory() throws Exception {
		List<Inventory> materialInventoryRef = null;

		materialInventoryRef = new ArrayList<Inventory>();

		for (InventoryTransferMaterial bukapetiMaterial : getInventoryTransfer().getTransferMaterialList()) {
			Inventory inventoryRef =
					getInventoryDao().findInventoryById(bukapetiMaterial.getInventoryIdRef());
			
			materialInventoryRef.add(inventoryRef);		
		}
		
		return materialInventoryRef;
	}

	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTransferDialogWin.detach();
	}
	
	public InventoryTransfer getInventoryTransfer() {
		return inventoryTransfer;
	}

	public void setInventoryTransfer(InventoryTransfer inventoryTransfer) {
		this.inventoryTransfer = inventoryTransfer;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
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
