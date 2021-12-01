package com.pyramix.swi.webui.inventory.process;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.inventory.process.InventoryProcessType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryProcessProductEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -788207462992964329L;

	private Window inventoryProcessProductEditDialog;
	private Textbox coilNoTextbox, kodeTextbox, thicknessTextbox, packingTextbox,
		customerTextbox;
	private Intbox quantityShtLineIntbox;
	private Decimalbox widthDecimalbox, lengthDecimalbox, quantityKgDecimalbox;
	private Checkbox forSWICheckbox;
	private Button toWeightButton, toSheetButton, selectCustomerButton;
	
	private InventoryProcessProduct processProduct;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setProcessProduct(
				(InventoryProcessProduct) arg.get("processProduct"));
	}
	
	public void onCreate$inventoryProcessProductEditDialog(Event event) throws Exception {
		widthDecimalbox.setLocale(getLocale()); 
		lengthDecimalbox.setLocale(getLocale()); 
		quantityKgDecimalbox.setLocale(getLocale());
		
		displayProcessProductInfo();
		
	}
	
	private void displayProcessProductInfo() {
		coilNoTextbox.setValue(getProcessProduct().getMarking());
		kodeTextbox.setValue(getProcessProduct().getInventoryCode().getProductCode());
		thicknessTextbox.setValue(getFormatedFloatLocal(getProcessProduct().getThickness()));
		if (getProcessProduct().getInventoryProcessType().compareTo(InventoryProcessType.shearing)==0) {
			widthDecimalbox.setDisabled(true);			
			lengthDecimalbox.setDisabled(false);
		} else {
			widthDecimalbox.setDisabled(false);			
			lengthDecimalbox.setDisabled(true);			
		}
		widthDecimalbox.setValue(getProcessProduct().getWidth());
		lengthDecimalbox.setValue(getProcessProduct().getLength());		
		quantityShtLineIntbox.setValue(getProcessProduct().getSheetQuantity());
		quantityKgDecimalbox.setValue(getProcessProduct().getWeightQuantity());
		packingTextbox.setValue(getProcessProduct().getInventoryPacking().toString());
		customerTextbox.setValue(getProcessProduct().getCustomer()==null ? "" :
			getProcessProduct().getCustomer().getCompanyType()+". "+
			getProcessProduct().getCustomer().getCompanyLegalName());
		customerTextbox.setAttribute("customer", getProcessProduct().getCustomer());
		selectCustomerButton.setDisabled(getProcessProduct().getCustomer()==null);
		forSWICheckbox.setChecked(getProcessProduct().getCustomer()==null);

		if (getProcessProduct().getInventoryProcessType().compareTo(InventoryProcessType.shearing)==0) {
			// if it's re-coil
			if (getProcessProduct().getLength().compareTo(BigDecimal.ZERO)==0) {
				lengthDecimalbox.setDisabled(true);
				quantityShtLineIntbox.setDisabled(true);
				toWeightButton.setDisabled(true); 
				toSheetButton.setDisabled(true);
			} else {
				lengthDecimalbox.setDisabled(false);
				quantityShtLineIntbox.setDisabled(false);
				toWeightButton.setDisabled(false); 
				toSheetButton.setDisabled(false);				
			}
		}
	}

	public void onClick$toWeightButton(Event event) throws Exception {
		if (getProcessProduct().getInventoryProcessType().compareTo(InventoryProcessType.shearing)==0) {
			BigDecimal thickness = getProcessProduct().getThickness();
			BigDecimal width = getProcessProduct().getWidth();
			BigDecimal length = lengthDecimalbox.getValue();
			
			BigDecimal density = getProcessProduct().getInventoryCode().getInventoryType().getDensity();			
			BigDecimal weightPerSheet = thickness.multiply(width).multiply(length).multiply(density).divide(new BigDecimal(1000000));
			
			int sheetQuantity = quantityShtLineIntbox.getValue();
			quantityKgDecimalbox.setValue(weightPerSheet.multiply(new BigDecimal(sheetQuantity)));
		}
		
	}
	
	public void onClick$toSheetButton(Event event) throws Exception {
		if (getProcessProduct().getInventoryProcessType().compareTo(InventoryProcessType.shearing)==0) {
			BigDecimal thickness = getProcessProduct().getThickness();
			BigDecimal width = getProcessProduct().getWidth();
			BigDecimal length = lengthDecimalbox.getValue();
			BigDecimal density = getProcessProduct().getInventoryCode().getInventoryType().getDensity();
		
			BigDecimal weightPerSheet = thickness.multiply(width).multiply(length).multiply(density).divide(new BigDecimal(1000000));
			BigDecimal totalWeight = quantityKgDecimalbox.getValue();
		
			quantityShtLineIntbox.setValue(totalWeight.divide(weightPerSheet, RoundingMode.HALF_UP).intValue());
		}

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
	
	public void onClick$forSWICheckbox(Event event) throws Exception {
		if (forSWICheckbox.isChecked()) {
			customerTextbox.setValue("");
			customerTextbox.setAttribute("customer", null);
			selectCustomerButton.setDisabled(true);
		} else {
			selectCustomerButton.setDisabled(false);
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		InventoryProcessProduct processProduct = getUserModifiedProcessProduct();
		
		Events.sendEvent(Events.ON_OK, inventoryProcessProductEditDialog, processProduct);
		
		inventoryProcessProductEditDialog.detach();
	}
	
	private InventoryProcessProduct getUserModifiedProcessProduct() {
		InventoryProcessProduct userModProcessProduct = getProcessProduct();
		userModProcessProduct.setWidth(widthDecimalbox.getValue());
		userModProcessProduct.setLength(lengthDecimalbox.getValue());
		userModProcessProduct.setSheetQuantity(quantityShtLineIntbox.getValue());
		userModProcessProduct.setWeightQuantity(quantityKgDecimalbox.getValue());
		userModProcessProduct.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		
		return userModProcessProduct;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryProcessProductEditDialog.detach();
	}

	public InventoryProcessProduct getProcessProduct() {
		return processProduct;
	}

	public void setProcessProduct(InventoryProcessProduct processProduct) {
		this.processProduct = processProduct;
	}
	
}
