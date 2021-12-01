package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
// import com.pyramix.swi.domain.inventory.process.completed.InventoryProcessCompleted;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class ProcessCompletedDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8756794859566040284L;

	private InventoryProcessDao inventoryProcessDao;
	
	private Window processCompletedDialogWin;
	private Datebox completedDatebox, updatedDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Listbox completedProductListbox;
	private Combobox processStatusCombobox;
	private Button addCompletedProductButton, removeCompletedProductButton,
		updateButton, saveButton; // cancelButton, 
	
	private ProcessCompletedData processCompletedData;
	private PageMode pageMode;
	private InventoryProcess inventoryProcess;
	private String requestPath;
	
	private ListModelList<InventoryProcessProduct> processProductListModelList;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setProcessCompletedData(
				(ProcessCompletedData) arg.get("processCompletedData"));
		
		setPageMode(
				getProcessCompletedData().getPageMode());

		setInventoryProcess(
				getProcessCompletedData().getInventoryProcess());		

	}	
	
	public void onCreate$processCompletedDialogWin(Event event) throws Exception {		
		completedDatebox.setLocale(getLocale());
		completedDatebox.setFormat(getLongDateFormat());
		updatedDatebox.setLocale(getLocale());
		updatedDatebox.setFormat(getLongDateFormat());
		
		// hide the 'save' to inventory button
		saveButton.setVisible(false);

		// process status
		setupProcessStatusCombobox();
		
		setProcessProductListModelList(new ListModelList<InventoryProcessProduct>());
		
		processCompletedDialogWin.setTitle("Penyelesaian Proses (Cutting Order)");
		completedDatebox.setDisabled(false);
					
		displayProcessCompletedInfo();
	}
	
	private void setupProcessStatusCombobox() {
		Comboitem comboitem;
		
		for (InventoryProcessStatus processStatus : InventoryProcessStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(processStatus.toString());
			comboitem.setValue(processStatus);
			comboitem.setParent(processStatusCombobox);
		}
	}

	private void displayProcessCompletedInfo() throws Exception {
		completedDatebox.setValue(asDate(getLocalDate()));
		updatedDatebox.setValue(completedDatebox.getValue());
		
		processNumberTextbox.setValue(getInventoryProcess().getProcessNumber().getSerialComp());
		noteTextbox.setValue(getInventoryProcess().getNote());
		
		// process status
		for (Comboitem item : processStatusCombobox.getItems()) {
			if (getInventoryProcess().getProcessStatus().equals(item.getValue())) {
				processStatusCombobox.setSelectedItem(item);
			}
		}
		
		InventoryProcess inventoryProcessByProxy = 
				getInventoryProcessDao().getProcessMaterialsByProxy(getInventoryProcess().getId());
		List<InventoryProcessMaterial> processMaterialList = inventoryProcessByProxy.getProcessMaterials();
		
		int productSerial;
		InventoryProcessMaterial processMaterialByProxy = null;
		
		for (InventoryProcessMaterial processMaterial : processMaterialList) {			
			processMaterialByProxy = 
					getInventoryProcessDao().getProcessProductsByProxy(processMaterial.getId());
			
			productSerial = 1;
			
			for (InventoryProcessProduct processProduct : processMaterialByProxy.getProcessProducts()) {
				if (processProduct.getRevision()==0) {
					String serialMarking = processProduct.getMarking()+"-"+productSerial;
					
					processProduct.setMarking(serialMarking);
					
					productSerial++;										
				}
				getProcessProductListModelList().add(processProduct);
			}
		}		
		
		completedProductListbox.setModel(getProcessProductListModelList());
		completedProductListbox.setItemRenderer(getProcessProductListitemRenderer());	
	}
	
	private ListitemRenderer<InventoryProcessProduct> getProcessProductListitemRenderer() {

		return new ListitemRenderer<InventoryProcessProduct>() {
									
			@Override
			public void render(Listitem item, InventoryProcessProduct completedProduct, int index) throws Exception {
				Listcell lc;
				
				// Status
				lc = initCompletion(new Listcell(), completedProduct);
				lc.setParent(item);
				
				// Coil No.
				lc = initCoilNo(new Listcell(), completedProduct);
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(completedProduct.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = initSpesification(new Listcell(), completedProduct);
				lc.setParent(item);
		
				// recoil
				lc = initRecoil(new Listcell(), completedProduct);
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = initQtySL(new Listcell(), completedProduct);
				lc.setParent(item);
				
				// Qty (Kg)
				lc = initQtyKg(new Listcell(), completedProduct);
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(completedProduct.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Customer
				if (completedProduct.getId().equals(Long.MIN_VALUE)) {
					lc = new Listcell(completedProduct.getCustomer()==null? "SWI" :
							completedProduct.getCustomer().getCompanyType()+". "+
							completedProduct.getCustomer().getCompanyLegalName());
					lc.setStyle("white-space: nowrap;");
				} else {
					lc = initCustomer(new Listcell(), completedProduct);
				}
				lc.setParent(item);
				
				item.setValue(completedProduct);
			}

			private Listcell initCompletion(Listcell listcell, InventoryProcessProduct completedProduct) {
				Combobox statusCombobox = new Combobox();
				Comboitem comboitem;				
				for (InventoryProcessStatus processStatus : InventoryProcessStatus.values()) {
					comboitem = new Comboitem();
					comboitem.setLabel(processStatus.toString());
					comboitem.setValue(processStatus);
					comboitem.setParent(statusCombobox);
				}
				for (Comboitem item : statusCombobox.getItems()) {
					if (completedProduct.getProductProcessStatus().equals(item.getValue())) {
						statusCombobox.setSelectedItem(item);
					}
				}
				statusCombobox.setWidth("110px");
				statusCombobox.setDisabled(completedProduct.isUpdateToInventory());
				statusCombobox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initCoilNo(Listcell listcell, InventoryProcessProduct completedProduct) {
				Textbox coilNoTextbox = new Textbox();
				
				coilNoTextbox.setValue(completedProduct.getMarking());
				coilNoTextbox.setWidth("110px");
				coilNoTextbox.setDisabled(completedProduct.isUpdateToInventory());
				coilNoTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesification(Listcell listcell, InventoryProcessProduct completedProduct) {
				Decimalbox thicknessDecimalbox = new Decimalbox();
				thicknessDecimalbox.setLocale(getLocale());
				thicknessDecimalbox.setStyle("text-align:right");
				thicknessDecimalbox.setWidth("50px");
				thicknessDecimalbox.setValue(completedProduct.getThickness());
				thicknessDecimalbox.setDisabled(false);
				thicknessDecimalbox.setParent(listcell);
				
				Label lbl01 = new Label();
				lbl01.setValue(" x ");
				lbl01.setParent(listcell);
				
				Decimalbox widthDecimalbox = new Decimalbox();
				widthDecimalbox.setLocale(getLocale());
				widthDecimalbox.setStyle("text-align:right");
				widthDecimalbox.setWidth("50px");
				widthDecimalbox.setValue(completedProduct.getWidth());
				widthDecimalbox.setDisabled(completedProduct.isRecoil());
				widthDecimalbox.setParent(listcell);
				
				Label lbl02 = new Label();
				lbl02.setValue(" x ");
				lbl02.setParent(listcell);
								
				Decimalbox lengthDecimalbox = new Decimalbox();
				lengthDecimalbox.setLocale(getLocale());
				lengthDecimalbox.setStyle("text-align:right");
				lengthDecimalbox.setWidth("50px");
				lengthDecimalbox.setValue(completedProduct.getLength());
				lengthDecimalbox.setDisabled(completedProduct.isRecoil());
				lengthDecimalbox.setParent(listcell);
												
				return listcell;
			}

			private Listcell initRecoil(Listcell listcell, InventoryProcessProduct completedProduct) {
				Checkbox recoilCheckbox = new Checkbox();
				recoilCheckbox.setChecked(completedProduct.isRecoil());
				recoilCheckbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtySL(Listcell listcell, InventoryProcessProduct completedProduct) {
				Intbox qtySLIntbox = new Intbox();
				qtySLIntbox.setValue(completedProduct.getSheetQuantity());
				qtySLIntbox.setStyle("text-align:right");
				qtySLIntbox.setWidth("60px");
				qtySLIntbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtyKg(Listcell listcell, InventoryProcessProduct completedProduct) {
				Decimalbox qtyKgDecimalbox = new Decimalbox();
				qtyKgDecimalbox.setLocale(getLocale());
				qtyKgDecimalbox.setValue(completedProduct.getWeightQuantity());
				qtyKgDecimalbox.setStyle("text-align:right");
				qtyKgDecimalbox.setWidth("80px");
				qtyKgDecimalbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initCustomer(Listcell listcell, InventoryProcessProduct completedProduct) throws Exception {
				InventoryProcessProduct processProductByProxy = 
						getInventoryProcessDao().getProcessProductsCustomerByProxy(completedProduct.getId());
				
				listcell.setLabel(completedProduct.getCustomer()==null ? "SWI" :
						processProductByProxy.getCustomer().getCompanyType()+". "+
						processProductByProxy.getCustomer().getCompanyLegalName());
				listcell.setStyle("white-space: nowrap;");
				
				return listcell;
			}

		};
	}
	
	public void onSelect$completedDatebox(Event event) throws Exception {
		updatedDatebox.setValue(completedDatebox.getValue());
	}
		
	public void onClick$addCompletedProductButton(Event event) throws Exception {		
		
		getProcessProductListModelList().add(getNewInventoryProcessProduct(new InventoryProcessProduct()));
	}

	private InventoryProcessProduct getNewInventoryProcessProduct(InventoryProcessProduct processProduct) throws Exception {
		InventoryProcess inventoryProcessByProxy = 
				getInventoryProcessDao().getProcessMaterialsByProxy(getInventoryProcess().getId());
		
		int lastMaterialIndex = 
				inventoryProcessByProxy.getProcessMaterials().size()-1;
		
		InventoryProcessMaterial inventoryProcessMaterial = 
				inventoryProcessByProxy.getProcessMaterials().get(lastMaterialIndex);
		
		processProduct.setInventoryCode(inventoryProcessMaterial.getInventoryCode());
		processProduct.setInventoryLocation(inventoryProcessMaterial.getInventoryLocation());
		processProduct.setThickness(inventoryProcessMaterial.getThickness());
		processProduct.setPrinted(false);
		processProduct.setMarking(inventoryProcessMaterial.getMarking());
		
		InventoryProcessMaterial inventoryProcessMaterialByProxy =
				getInventoryProcessDao().getProcessProductsByProxy(inventoryProcessMaterial.getId());
		
		int lastProductIndex =
				inventoryProcessMaterialByProxy.getProcessProducts().size()-1;
		
		InventoryProcessProduct inventoryProcessProduct =
				inventoryProcessMaterialByProxy.getProcessProducts().get(lastProductIndex);
		
		processProduct.setInventoryProcessType(inventoryProcessProduct.getInventoryProcessType());
		processProduct.setWidth(inventoryProcessProduct.getWidth());
		processProduct.setLength(inventoryProcessProduct.getLength());
		processProduct.setInventoryPacking(inventoryProcessProduct.getLength().compareTo(BigDecimal.ZERO)==0 ? 
				InventoryPacking.coil : InventoryPacking.petian);
		processProduct.setRecoil(false);
		
		InventoryProcessProduct inventoryProcessProductCustomerByProxy =
				getInventoryProcessDao().getProcessProductsCustomerByProxy(inventoryProcessProduct.getId());
		processProduct.setCustomer(inventoryProcessProductCustomerByProxy.getCustomer());
		
		processProduct.setSheetQuantity(0);
		processProduct.setWeightQuantity(BigDecimal.ZERO);
		processProduct.setContractNumber(inventoryProcessMaterial.getContractNumber());
		processProduct.setLcNumber(inventoryProcessMaterial.getLcNumber());
		processProduct.setProductProcessStatus(InventoryProcessStatus.selesai);
		processProduct.setUpdateToInventory(false);

		processProduct.setRevision(0);
		
		return processProduct;
	}

	public void onClick$removeCompletedProductButton(Event event) throws Exception {
		
	}
	
	/**
	 * Update the InventoryProcess together with the InventoryProcessProduct with user's modified status and coilno
	 * - InventoryProcessStatus 
	 * - coilNumber / marking with serial
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$updateButton(Event event) throws Exception {
		InventoryProcess userModInventoryProcess = getInventoryProcess();
		userModInventoryProcess.setCompleteDate(completedDatebox.getValue());
		userModInventoryProcess.setNote(noteTextbox.getValue());

		// 
		// eval
		// InventoryProcessStatus processStatus = evalAllProcessProductCompleted();
		// display
		// for (Comboitem item : processStatusCombobox.getItems()) {
		//	if (processStatus.equals(item.getValue())) {
		//		processStatusCombobox.setSelectedItem(item);
				
		//		break;
		//	}
		// }
		// set
		userModInventoryProcess.setProcessStatus(processStatusCombobox.getSelectedItem().getValue());
		
		InventoryProcess inventoryProcessByProxy = 
				getInventoryProcessDao().getProcessMaterialsByProxy(userModInventoryProcess.getId());
		List<InventoryProcessMaterial> processMaterialList = inventoryProcessByProxy.getProcessMaterials();
		
		for (InventoryProcessMaterial processMaterial : processMaterialList) {
			
			InventoryProcessMaterial processMaterialByProxy = 
					getInventoryProcessDao().getProcessProductsByProxy(processMaterial.getId());

			for (int i = 0; i < processMaterialByProxy.getProcessProducts().size(); i++) {
				InventoryProcessProduct processProduct = processMaterialByProxy.getProcessProducts().get(i);
				
				int revision = processProduct.getRevision();
				
				Listitem item = completedProductListbox.getItemAtIndex(i);
				// inventoryProcessStatus
				Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();				
				// coilnumber / marking
				Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
				
				processProduct.setProductProcessStatus(statusCombobox.getSelectedItem().getValue());
				processProduct.setMarking(coilNoTextbox.getValue());
				processProduct.setUpdateToInventory(
						statusCombobox.getSelectedItem().getValue().equals(InventoryProcessStatus.selesai));
				processProduct.setRevision(revision+1);
			}
			
			processMaterial.setProcessProducts(processMaterialByProxy.getProcessProducts());
		}
		
		userModInventoryProcess.setProcessMaterials(processMaterialList);
		
		// disable these components so that user will not be able to change the value 
		// -- and click save to inventory -- thereby causing 'disparate' values
		updateButton.setDisabled(true);
		
		completedDatebox.setDisabled(true);
		processStatusCombobox.setDisabled(true);
		noteTextbox.setDisabled(true);
		addCompletedProductButton.setDisabled(true);
		removeCompletedProductButton.setDisabled(true);
		
		for (Listitem item : completedProductListbox.getItems()) {
			// disable the inventoryProcessStatus
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			statusCombobox.setDisabled(true);
			
			// disable coilnumber / marking
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
			coilNoTextbox.setDisabled(true);
			
			// disable width
			Decimalbox widthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(2);
			widthDecimalbox.setDisabled(true);
			// disable length
			Decimalbox lengthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(4);
			lengthDecimalbox.setDisabled(true);
			
			// disable recoil
			Checkbox checkboxRecoil = (Checkbox) item.getChildren().get(4).getFirstChild();
			checkboxRecoil.setDisabled(true);
			
			// disable qtySht
			Intbox qtySLIntbox = (Intbox) item.getChildren().get(5).getFirstChild();
			qtySLIntbox.setDisabled(true);
			
			// disable qtyKg
			Decimalbox qtyKgDecimalbox = (Decimalbox) item.getChildren().get(6).getFirstChild();
			qtyKgDecimalbox.setDisabled(true);
		}
		
		// hide the cancel button forcing user to 'save' the 'selesai' product to inventory
		// -- if none of the status is 'selesai', the system will not save anything to inventory
		// cancelButton.setVisible(false);
		
		// unhide the save button, forcing user to save into the inventory
		saveButton.setVisible(true);
		
		Events.sendEvent(Events.ON_CHANGE, processCompletedDialogWin, userModInventoryProcess);
	}
	
	@SuppressWarnings("unused")
	private InventoryProcessStatus evalAllProcessProductCompleted() throws Exception {
		// final outcome of the evaluation
		InventoryProcessStatus processStatus = null;

		for (Listitem item : completedProductListbox.getItems()) {
			// check whether status is 'selesai'
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			InventoryProcessStatus productProcessStatus = 
					statusCombobox.getSelectedItem().getValue();

			if (productProcessStatus.compareTo(InventoryProcessStatus.selesai)==0) {
				processStatus = InventoryProcessStatus.selesai;
			} else {
				processStatus = InventoryProcessStatus.proses;
				
				break;
			}
		}
		
		return processStatus;
	}

	/**
	 * Save to Inventory
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$saveButton(Event event) throws Exception {
		List<Inventory> inventoryList = new ArrayList<Inventory>();

		for (Listitem item : completedProductListbox.getItems()) {
			// inventoryProcessStatus
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();				
			// coilnumber / marking
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
			// width
			Decimalbox widthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(2);
			// length			
			Decimalbox lengthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(4);
			// recoil			
			Checkbox recoilCheckbox = (Checkbox) item.getChildren().get(4).getFirstChild();
			// qty(sl)			
			Intbox qtySLIntbox = (Intbox) item.getChildren().get(5).getFirstChild();
			// qty(kg)			
			Decimalbox qtyKgDecimalbox = (Decimalbox) item.getChildren().get(6).getFirstChild();
			
			// InventoryProcessProduct
			InventoryProcessProduct processProduct = item.getValue();
						
			if (processProduct.getRevision()==0) {

				if (statusCombobox.getSelectedItem().getValue().equals(InventoryProcessStatus.selesai)) {
					inventoryList.add(createInventory(new Inventory(), processProduct, 
							coilNoTextbox.getValue(), 
							widthDecimalbox.getValue(),
							lengthDecimalbox.getValue(),
							recoilCheckbox.isChecked(),
							qtySLIntbox.getValue(),
							qtyKgDecimalbox.getValue()));
				}
				
			} else {

				if (statusCombobox.getSelectedItem().getValue().equals(InventoryProcessStatus.selesai) && 
						!processProduct.isUpdateToInventory()) {
					inventoryList.add(createInventory(new Inventory(), processProduct, 
							coilNoTextbox.getValue(),
							widthDecimalbox.getValue(),
							lengthDecimalbox.getValue(),
							recoilCheckbox.isChecked(),
							qtySLIntbox.getValue(),
							qtyKgDecimalbox.getValue()));
				}
			}
		}
		
		Events.sendEvent(Events.ON_OK, processCompletedDialogWin, inventoryList);
		
		processCompletedDialogWin.detach();
	}
	
	private Inventory createInventory(Inventory inventory, InventoryProcessProduct processProduct, 
			String coilNo, BigDecimal width, BigDecimal length, boolean recoil, Integer qtySL, BigDecimal qtyKg) {
		
		inventory.setThickness(processProduct.getThickness());
		inventory.setWidth(width);
		inventory.setLength(length);
		inventory.setSheetQuantity(qtySL);
		inventory.setWeightQuantity(qtyKg);
		inventory.setMarking(coilNo);
		inventory.setDescription(null);
		inventory.setContractNumber(processProduct.getContractNumber());
		inventory.setLcNumber(processProduct.getLcNumber());
		inventory.setReceiveDate(asDate(getLocalDate()));
		inventory.setInventoryCode(processProduct.getInventoryCode());
		inventory.setInventoryStatus(InventoryStatus.ready);
		inventory.setInventoryLocation(processProduct.getInventoryLocation());
		inventory.setInventoryPacking(processProduct.getInventoryPacking());
		inventory.setNote("");
		inventory.setSku(null);
		
		return inventory;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		processCompletedDialogWin.detach();
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public ProcessCompletedData getProcessCompletedData() {
		return processCompletedData;
	}

	public void setProcessCompletedData(ProcessCompletedData processCompletedData) {
		this.processCompletedData = processCompletedData;
	}

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}

	public ListModelList<InventoryProcessProduct> getProcessProductListModelList() {
		return processProductListModelList;
	}

	public void setProcessProductListModelList(ListModelList<InventoryProcessProduct> processProductListModelList) {
		this.processProductListModelList = processProductListModelList;
	}
}
