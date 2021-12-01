package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.inventory.process.InventoryProcessStatus;
import com.pyramix.swi.domain.inventory.process.InventoryProcessType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class InventoryProcessDialogCuttingControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2902984289613749704L;

	private Window inventoryProcessDialogCuttingWin;
	private Combobox processTypeCombobox;
	private Tabbox processTypeTabbox;
	private Textbox coilNumberTextbox, inventoryCodeTextbox, specificationTextbox, 
		remainQtyTextbox, shearingThicknessTextbox, shearingWidthTextbox, 
		customerTextbox;
	private Decimalbox shearingLengthDecimalbox, shearingQtyWeightDecimalbox, 
		slittingQtyWeightDecimalbox;
	private Intbox shearingQtyIntbox;
	private Checkbox recoilCheckbox, forSWICheckbox;
	private Button selectCustomerButton;
	private Listbox slittingSizeListbox;
	
	private InventoryProcessMaterialData materialData;
	private InventoryProcessMaterial processMaterial;
	private BigDecimal materialQuantity;
	// for slitting
	private List<InventoryProcessSlittingData> slittingDataList;
	private ListModelList<InventoryProcessSlittingData> slittingDataListModelList;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setMaterialData(
				(InventoryProcessMaterialData) arg.get("materialData"));
	}
	
	public void onCreate$inventoryProcessDialogCuttingWin(Event event) throws Exception {
		// init
		shearingLengthDecimalbox.setLocale(getLocale());
		shearingQtyIntbox.setLocale(getLocale());
		shearingQtyWeightDecimalbox.setLocale(getLocale());
		slittingQtyWeightDecimalbox.setLocale(getLocale());
		
		// shearing or slitting selection
		setupProcessTypeCombobox();
		
		setProcessMaterial(
				getMaterialData().getProcessMaterial());
		
		setMaterialQuantity(
				getMaterialData().getMaterialRemainQuantity());

		// populate the data
		displayProcessMaterial();
		
		// init shearing process
		initShearingProcess();
		
		// init slitting process
		initSlittingProcess();
	}

	private void setupProcessTypeCombobox() {
		Comboitem comboitem;
		
		for (InventoryProcessType processType : InventoryProcessType.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(processType.toString());
			comboitem.setValue(processType);
			comboitem.setParent(processTypeCombobox);
		}
		
		// defaulted to Shearing
		processTypeCombobox.setSelectedIndex(0);
	}

	private void displayProcessMaterial() {
		coilNumberTextbox.setValue(getProcessMaterial().getMarking());
		inventoryCodeTextbox.setValue(getProcessMaterial().getInventoryCode().getProductCode());
		specificationTextbox.setValue(
				getFormatedFloatLocal(getProcessMaterial().getThickness())+" x "+
				getFormatedFloatLocal(getProcessMaterial().getWidth())+" x "+
					(getProcessMaterial().getLength().compareTo(BigDecimal.ZERO)==0 ?
						"Coil" : 
						getFormatedFloatLocal(getProcessMaterial().getLength())));
		remainQtyTextbox.setValue(getFormatedFloatLocal(getMaterialQuantity()));
	}
		
	//-----------------------------------------------------------
	// for shearing
	//-----------------------------------------------------------
	
	private void initShearingProcess() {
		shearingThicknessTextbox.setValue(
				getFormatedFloatLocal(getProcessMaterial().getThickness()));
		shearingWidthTextbox.setValue(
				getFormatedFloatLocal(getProcessMaterial().getWidth()));
		
		shearingLengthDecimalbox.setValue(getMaterialData().getShearingLength());
		shearingQtyIntbox.setValue(getMaterialData().getShearingSheetQuantity());
		shearingQtyWeightDecimalbox.setValue(getMaterialData().getShearingQuantityWeight());
		// customer
		if (getMaterialData().getShearingCustomer()==null) {
			customerTextbox.setValue("");
			selectCustomerButton.setDisabled(false);
			// it's for SWI
			forSWICheckbox.setChecked(true);
		} else {
			customerTextbox.setValue(getMaterialData().getShearingCustomer().getCompanyType()+". "+
					getMaterialData().getShearingCustomer().getCompanyLegalName());
			customerTextbox.setAttribute("customer", getMaterialData().getShearingCustomer());
			// it's not for SWI
			forSWICheckbox.setChecked(false);
		}
		
	}
	
	public void onClick$shearingQtyShtToKgButton(Event event) throws Exception {
		BigDecimal thickness = getProcessMaterial().getThickness();
		BigDecimal width = getProcessMaterial().getWidth();
		BigDecimal length = shearingLengthDecimalbox.getValue();
		BigDecimal density = getProcessMaterial().getInventoryCode().getInventoryType().getDensity();
		
		BigDecimal weightPerSheet = thickness.multiply(width).multiply(length).multiply(density).divide(new BigDecimal(1000000));
		
		int sheetQuantity = shearingQtyIntbox.getValue();
		shearingQtyWeightDecimalbox.setValue(weightPerSheet.multiply(new BigDecimal(sheetQuantity)));
	}
	
	public void onClick$shearingQtyKgToShtButton(Event event) throws Exception {
		BigDecimal thickness = getProcessMaterial().getThickness();
		BigDecimal width = getProcessMaterial().getWidth();
		BigDecimal length = shearingLengthDecimalbox.getValue();
		BigDecimal density = getProcessMaterial().getInventoryCode().getInventoryType().getDensity();
		
		BigDecimal weightPerSheet = thickness.multiply(width).multiply(length).multiply(density).divide(new BigDecimal(1000000));
		BigDecimal totalWeight = shearingQtyWeightDecimalbox.getValue();
		
		shearingQtyIntbox.setValue(totalWeight.divide(weightPerSheet, RoundingMode.HALF_UP).intValue());
	}
	
	public void onClick$selectCustomerButton(Event event) throws Exception {
		Window customerDialogWin = 
				(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);

		customerDialogWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Customer selCustomer = (Customer) event.getData();

				customerTextbox.setValue(selCustomer.getCompanyType()+". "+
						selCustomer.getCompanyLegalName());
				customerTextbox.setAttribute("customer", selCustomer);
				
				forSWICheckbox.setChecked(false);
			}

		});	
		
		customerDialogWin.doModal();
	}
	
	public void onSelect$processTypeCombobox(Event event) throws Exception {
		processTypeTabbox.setSelectedIndex(
				processTypeCombobox.getSelectedIndex());
	}
	

	public void onClick$recoilCheckbox(Event event) throws Exception {
		shearingLengthDecimalbox.setValue(BigDecimal.ZERO);
		shearingQtyIntbox.setValue(0);

		if (recoilCheckbox.isChecked()) {
			// re-coil
			shearingQtyWeightDecimalbox.setValue(getMaterialQuantity());
			
		} else {
			// not re-coil
			shearingQtyWeightDecimalbox.setValue(BigDecimal.ZERO);
		}
	}
		
	public void onClick$forSWICheckbox(Event event) throws Exception {
		if (forSWICheckbox.isChecked()) {
			customerTextbox.setValue("");
			customerTextbox.setAttribute("customer", null);
			selectCustomerButton.setDisabled(true);			
		} else {
			selectCustomerButton.setDisabled(false);
		}
	}
	
	//-----------------------------------------------------------
	// for slitting
	//-----------------------------------------------------------

	private void initSlittingProcess() {
		setSlittingDataList(new ArrayList<InventoryProcessSlittingData>());
		setSlittingDataListModelList(new ListModelList<InventoryProcessSlittingData>(getSlittingDataList()));

		slittingSizeListbox.setModel(getSlittingDataListModelList());
		slittingSizeListbox.setItemRenderer(getInventoryProcessSlittingDataListitemRenderer(PageMode.VIEW));
	}
	
	private ListitemRenderer<InventoryProcessSlittingData> getInventoryProcessSlittingDataListitemRenderer(PageMode pageMode) {
		
		return new ListitemRenderer<InventoryProcessSlittingData>() {
			
			@Override
			public void render(Listitem item, InventoryProcessSlittingData slittingData, int index) throws Exception {
				Listcell lc;
				
				// Lebar (mm)
				if (pageMode.compareTo(PageMode.EDIT)==0) {
					lc = initSlittingWidth(new Listcell(), slittingData);
				} else {
					lc = new Listcell(getFormatedFloatLocal(slittingData.getSlittingWidth()));					
				}
				lc.setParent(item);
				
				// Line
				if (pageMode.compareTo(PageMode.EDIT)==0) {
					lc = initSlittingLine(new Listcell(), slittingData, index);
				} else {
					lc = new Listcell(getFormatedInteger(slittingData.getSlittingLine()));					
				}
				lc.setParent(item);				
				
				// Qty(Kg)
				if (pageMode.compareTo(PageMode.EDIT)==0) {
					lc = initTotalLineWeight(new Listcell(), slittingData);
				} else {
					lc = new Listcell(getFormatedFloatLocal(slittingData.getTotalLineWeight()));					
				}
				lc.setParent(item);				
				
				// Customer
				if (pageMode.compareTo(PageMode.EDIT)==0) {
					lc = initSlittingForCustomer(new Listcell(), slittingData, index);
				} else {
					lc = new Listcell(slittingData.getSlittingForCustomer()==null ? " " : 
						slittingData.getSlittingForCustomer().getCompanyType()+". "+
						slittingData.getSlittingForCustomer().getCompanyLegalName());					
				}
				lc.setParent(item);
			}

			private Listcell initSlittingWidth(Listcell listcell, InventoryProcessSlittingData slittingData) {
				Decimalbox slittingWidthDecimalbox = new Decimalbox();
				slittingWidthDecimalbox.setLocale(getLocale());
				slittingWidthDecimalbox.setValue(slittingData.getSlittingWidth());
				slittingWidthDecimalbox.setStyle("text-align:right");
				slittingWidthDecimalbox.setWidth("90px");
				slittingWidthDecimalbox.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initSlittingLine(Listcell listcell, InventoryProcessSlittingData slittingData, int index) {
				Intbox slittingLineIntbox = new Intbox();
				slittingLineIntbox.setLocale(getLocale());
				slittingLineIntbox.setValue(slittingData.getSlittingLine());
				slittingLineIntbox.setStyle("text-align:right");
				slittingLineIntbox.setWidth("90px");
				slittingLineIntbox.setParent(listcell);
				
				Button calcTotalWeightButton = new Button();
				calcTotalWeightButton.setLabel(">>");
				calcTotalWeightButton.setSclass("selectButton");
				calcTotalWeightButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Listitem listitem = slittingSizeListbox.getItemAtIndex(index);
						// decimalbox
						Decimalbox slittingWidthDecimalbox = (Decimalbox) listitem.getChildren().get(0).getChildren().get(0);
						// intbox
						Intbox slittingLineIntbox = (Intbox) listitem.getChildren().get(1).getChildren().get(0);
						// decimalbox
						Decimalbox totalLineWeight = (Decimalbox) listitem.getChildren().get(2).getChildren().get(0);
						// slitting weight - user enter
						BigDecimal slittingWeight = slittingQtyWeightDecimalbox.getValue();
						BigDecimal coilWidth = getProcessMaterial().getWidth();
												
						// per mm weight : slittingWeight / total coil width
						totalLineWeight.setValue(slittingWeight.divide(coilWidth, 3, RoundingMode.HALF_UP).
								multiply(slittingWidthDecimalbox.getValue()).
								multiply(new BigDecimal(slittingLineIntbox.getValue())));						
					}
				});
				calcTotalWeightButton.setParent(listcell);
				
				return listcell;
			}

			private Listcell initTotalLineWeight(Listcell listcell, InventoryProcessSlittingData slittingData) {
				Decimalbox totalLineWeight = new Decimalbox();
				totalLineWeight.setLocale(getLocale());
				totalLineWeight.setValue(slittingData.getTotalLineWeight());
				totalLineWeight.setStyle("text-align:right");
				totalLineWeight.setWidth("120px");
				totalLineWeight.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initSlittingForCustomer(Listcell listcell, InventoryProcessSlittingData slittingData, int index) {
				Textbox customerTextbox = new Textbox();
				customerTextbox.setValue("");
				customerTextbox.setDisabled(true);
				customerTextbox.setWidth("240px");
				customerTextbox.setValue(slittingData.getSlittingForCustomer()==null? " " :
					slittingData.getSlittingForCustomer().getCompanyType()+". "+
						slittingData.getSlittingForCustomer().getCompanyLegalName());
				customerTextbox.setAttribute("customer", slittingData.getSlittingForCustomer());
				customerTextbox.setParent(listcell);
				
				Button selectCustomerButton = new Button();
				selectCustomerButton.setLabel("...");
				selectCustomerButton.setSclass("selectButton");
				selectCustomerButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Window customerDialogWin = 
								(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);

						customerDialogWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Customer selCustomer = (Customer) event.getData();

								customerTextbox.setValue(selCustomer.getCompanyType()+". "+
										selCustomer.getCompanyLegalName());
								customerTextbox.setAttribute("customer", selCustomer);
								
								Listitem listitem =
										slittingSizeListbox.getItemAtIndex(index);
								Checkbox swiCheckbox = (Checkbox) listitem.getChildren().get(3).getChildren().get(2);
								swiCheckbox.setChecked(false);
							}

						});	
						
						customerDialogWin.doModal();						
					}
				});
				selectCustomerButton.setParent(listcell);
				
				Checkbox forSWICheckbox = new Checkbox();
				forSWICheckbox.setLabel("SWI");
				forSWICheckbox.setChecked(slittingData.getSlittingForCustomer()==null);
				forSWICheckbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Checkbox forSWICheckbox = (Checkbox) event.getTarget();

						Listitem listitem =
								slittingSizeListbox.getItemAtIndex(index);
						Textbox customerTextbox = (Textbox) listitem.getChildren().get(3).getChildren().get(0);
						Button selectCustomerButton = (Button) listitem.getChildren().get(3).getChildren().get(1);
						if (forSWICheckbox.isChecked()) {
							customerTextbox.setValue("");
							customerTextbox.setAttribute("customer", null);
							selectCustomerButton.setDisabled(true);			
						} else {
							selectCustomerButton.setDisabled(false);
						}
						
					}
				});
				forSWICheckbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addRowButton(Event event) throws Exception {
		if (slittingQtyWeightDecimalbox.getValue()==null) {
			Messagebox.show("Belum menentukan kuantitas slitting.",
				    "Error", 
				    Messagebox.OK,  
				    Messagebox.ERROR);

			return;
		}
		
		if (slittingSizeListbox.getItems().isEmpty()) {
			getSlittingDataListModelList().add(
					initInventoryProcessSlittingData(new InventoryProcessSlittingData()));
			slittingSizeListbox.setModel(getSlittingDataListModelList());
			slittingSizeListbox.setItemRenderer(getInventoryProcessSlittingDataListitemRenderer(PageMode.EDIT));		

		} else {
			int prevIndex = getSlittingDataListModelList().getSize()-1;
			
			getSlittingDataListModelList().add(
					subInitInventoryProcessSlittingData(new InventoryProcessSlittingData(), prevIndex));			
		}
		
	}


	private InventoryProcessSlittingData initInventoryProcessSlittingData(InventoryProcessSlittingData slittingData) {
		slittingData.setSlittingWidth(BigDecimal.ZERO);
		slittingData.setSlittingLine(0);
		slittingData.setTotalLineWeight(BigDecimal.ZERO);
		slittingData.setSlittingForCustomer(null);
		
		return slittingData;
	}

	private InventoryProcessSlittingData subInitInventoryProcessSlittingData(
			InventoryProcessSlittingData slittingData, int prevIndex) {

		BigDecimal materialWidth = getProcessMaterial().getWidth();
		
		slittingData.setSlittingWidth(materialWidth.subtract(getTotalSlittingWidth()));
		slittingData.setSlittingLine(0);
		slittingData.setTotalLineWeight(BigDecimal.ZERO);
		slittingData.setSlittingForCustomer(getPreviousSelectedCustomer(prevIndex));		
		
		return slittingData;
	}

	private Customer getPreviousSelectedCustomer(int prevIndex) {
		Listitem listitem = slittingSizeListbox.getItemAtIndex(prevIndex);
		// Textbox
		Textbox customerTextbox = (Textbox) listitem.getChildren().get(3).getChildren().get(0);
		// Checkbox
		Checkbox swiCheckbox = (Checkbox) listitem.getChildren().get(3).getChildren().get(2);
		
		if (swiCheckbox.isChecked())
			return null;
		else 
			return (Customer) customerTextbox.getAttribute("customer");
	}

	public void onClick$removeRowButton(Event event) throws Exception {
		if (getSlittingDataListModelList().getSize()==0) {
			
			return;
		}
		
		int lastIndex = getSlittingDataListModelList().getSize()-1;
		
		getSlittingDataListModelList().remove(lastIndex);
	}
	
	private BigDecimal getTotalSlittingWidth() {
		BigDecimal totalSlittingWidth = BigDecimal.ZERO;
		
		for (int i = 0; i < slittingSizeListbox.getItemCount(); i++) {
			Listitem listitem = slittingSizeListbox.getItemAtIndex(i);
			
			Decimalbox slittingWidthDecimalbox = (Decimalbox) listitem.getChildren().get(0).getChildren().get(0);
			Intbox slittingLineIntbox = (Intbox) listitem.getChildren().get(1).getChildren().get(0);
			
			BigDecimal totalWidth = slittingWidthDecimalbox.getValue().multiply(new BigDecimal(slittingLineIntbox.getValue()));
			
			totalSlittingWidth = totalSlittingWidth.add(totalWidth);
		}
		return totalSlittingWidth;
	}

	//-----------------------------------------------------------
	// for adding / saving into the InventoryProcess dialog
	//-----------------------------------------------------------
	
	public void onClick$addButton(Event event) throws Exception {
		List<InventoryProcessProduct> processProductList;
		if (processTypeCombobox.getSelectedIndex()==0) {
			// shearing
			processProductList = 
					new ArrayList<InventoryProcessProduct>();
			processProductList.add(getUserModifiedShearingProcessProduct(
					new InventoryProcessProduct()));
			
			getMaterialData().setShearingLength(shearingLengthDecimalbox.getValue());
			getMaterialData().setShearingSheetQuantity(shearingQtyIntbox.getValue());
			getMaterialData().setShearingQuantityWeight(shearingQtyWeightDecimalbox.getValue());
			getMaterialData().setShearingCustomer((Customer) customerTextbox.getAttribute("customer"));
			
			getMaterialData().setProcessProductList(processProductList);
		} else {
			// slitting
			processProductList = getUserModifiedSlittingProcessProduct(new ArrayList<InventoryProcessProduct>());
			
			getMaterialData().setProcessProductList(processProductList);			
		}
		
		Events.sendEvent(Events.ON_OK, inventoryProcessDialogCuttingWin, getMaterialData());
		
		inventoryProcessDialogCuttingWin.detach();
	}

	private List<InventoryProcessProduct> getUserModifiedSlittingProcessProduct(
			ArrayList<InventoryProcessProduct> productList) {
		
		for (Listitem item : slittingSizeListbox.getItems()) {
			Decimalbox slittingWidthDecimalbox = (Decimalbox) item.getChildren().get(0).getFirstChild();
			Intbox slittingLineIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
			Decimalbox totalLineWeight = (Decimalbox) item.getChildren().get(2).getFirstChild();
			Textbox customerTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
			Checkbox forSWICheckbox = (Checkbox) item.getChildren().get(3).getChildren().get(2);
			
			InventoryProcessProduct processProduct = new InventoryProcessProduct();

			processProduct.setInventoryCode(getProcessMaterial().getInventoryCode());
			processProduct.setInventoryLocation(getProcessMaterial().getInventoryLocation());
			processProduct.setMaterialIdRef(getProcessMaterial().getInventoryIdRef());
			processProduct.setThickness(getProcessMaterial().getThickness());
			processProduct.setLength(BigDecimal.ZERO);
			processProduct.setPrinted(false);

			processProduct.setInventoryProcessType(InventoryProcessType.slitting);
			processProduct.setInventoryPacking(InventoryPacking.coil);
			processProduct.setMarking(getProcessMaterial().getMarking());
			// for customer
			processProduct.setCustomer(forSWICheckbox.isChecked() ? null : 
				(Customer) customerTextbox.getAttribute("customer"));			
			// slitting width
			processProduct.setWidth(slittingWidthDecimalbox.getValue());
						
			// slitting line
			processProduct.setSheetQuantity(slittingLineIntbox.getValue());
			
			// total slitting weight
			processProduct.setWeightQuantity(totalLineWeight.getValue());
			
			processProduct.setContractNumber(getProcessMaterial().getContractNumber());
			processProduct.setLcNumber(getProcessMaterial().getLcNumber());			
			// status set to 'selesai' - we're assuming it's always 'selesai' after request to process
			// -- status will be used ONLY during ProcessCompletedDialoag
			processProduct.setProductProcessStatus(InventoryProcessStatus.selesai);
			processProduct.setUpdateToInventory(false);			
			
			// to be used during 'ProcessCompleted'
			processProduct.setRevision(0);
			
			productList.add(processProduct);
		}
		
		return productList;
	}

	private InventoryProcessProduct getUserModifiedShearingProcessProduct(
			InventoryProcessProduct processProduct) {
		
		processProduct.setInventoryCode(getProcessMaterial().getInventoryCode());
		processProduct.setInventoryLocation(getProcessMaterial().getInventoryLocation());
		processProduct.setMaterialIdRef(getProcessMaterial().getInventoryIdRef());
		processProduct.setThickness(getProcessMaterial().getThickness());
		processProduct.setWidth(getProcessMaterial().getWidth());
		processProduct.setPrinted(false);
		
		processProduct.setInventoryProcessType(InventoryProcessType.shearing);
		processProduct.setInventoryPacking(recoilCheckbox.isChecked() ? 
				InventoryPacking.coil : InventoryPacking.petian);
		processProduct.setRecoil(recoilCheckbox.isChecked());
		processProduct.setMarking(getProcessMaterial().getMarking());

		// --> customerTextbox.getAttribute("customer");
		processProduct.setCustomer(forSWICheckbox.isChecked() ? null : 
			(Customer) customerTextbox.getAttribute("customer"));

		// shearingLengthDecimalbox
		processProduct.setLength(shearingLengthDecimalbox.getValue());
		
		// shearingQtyIntbox
		processProduct.setSheetQuantity(shearingQtyIntbox.getValue());
		
		// shearingQtyWeightDecimalbox
		processProduct.setWeightQuantity(shearingQtyWeightDecimalbox.getValue());
		
		processProduct.setContractNumber(getProcessMaterial().getContractNumber());
		processProduct.setLcNumber(getProcessMaterial().getLcNumber());
		
		// status set to 'selesai' - we're assuming it's always 'selesai' after request to process
		// -- status will be used ONLY during ProcessCompletedDialoag
		processProduct.setProductProcessStatus(InventoryProcessStatus.selesai);
		processProduct.setUpdateToInventory(false);
		
		// to be used during 'ProcessCompleted'
		processProduct.setRevision(0);
		
		return processProduct;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryProcessDialogCuttingWin.detach();
	}

	public InventoryProcessMaterialData getMaterialData() {
		return materialData;
	}

	public void setMaterialData(InventoryProcessMaterialData materialData) {
		this.materialData = materialData;
	}

	public InventoryProcessMaterial getProcessMaterial() {
		return processMaterial;
	}

	public void setProcessMaterial(InventoryProcessMaterial processMaterial) {
		this.processMaterial = processMaterial;
	}

	public BigDecimal getMaterialQuantity() {
		return materialQuantity;
	}

	public void setMaterialQuantity(BigDecimal materialQuantity) {
		this.materialQuantity = materialQuantity;
	}

	public List<InventoryProcessSlittingData> getSlittingDataList() {
		return slittingDataList;
	}

	public void setSlittingDataList(List<InventoryProcessSlittingData> slittingDataList) {
		this.slittingDataList = slittingDataList;
	}

	public ListModelList<InventoryProcessSlittingData> getSlittingDataListModelList() {
		return slittingDataListModelList;
	}

	public void setSlittingDataListModelList(ListModelList<InventoryProcessSlittingData> slittingDatListModelList) {
		this.slittingDataListModelList = slittingDatListModelList;
	}

}
