package com.pyramix.swi.webui.inventory.transfer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryTransferEndProductEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7772012718173857108L;

	private Window inventoryTransferEndProductEditDialog;
	private Textbox coilNoTextbox, kodeTextbox, packingTextbox, customerTextbox; 
	private Decimalbox thicknessDecimalbox, widthDecimalbox, lengthDecimalbox, quantityKgDecimalbox;
	private Intbox quantityShtLineIntbox;
	
	private InventoryTransferEndProduct inventoryTransferEndProduct;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransferEndProduct(
				(InventoryTransferEndProduct) arg.get("inventoryTransferEndProduct"));
	}

	public void onCreate$inventoryTransferEndProductEditDialog(Event event) throws Exception {
		thicknessDecimalbox.setLocale(getLocale());
		widthDecimalbox.setLocale(getLocale());
		lengthDecimalbox.setLocale(getLocale());
		
		customerTextbox.setValue(getInventoryTransferEndProduct().getInventoryLocation().toString());
		coilNoTextbox.setValue(getInventoryTransferEndProduct().getMarking());
		kodeTextbox.setValue(getInventoryTransferEndProduct().getInventoryCode().getProductCode());
		thicknessDecimalbox.setValue(getInventoryTransferEndProduct().getThickness());
		widthDecimalbox.setValue(getInventoryTransferEndProduct().getWidth());
		lengthDecimalbox.setValue(getInventoryTransferEndProduct().getLength());
		quantityShtLineIntbox.setValue(getInventoryTransferEndProduct().getSheetQuantity());
		quantityKgDecimalbox.setValue(getInventoryTransferEndProduct().getWeightQuantity());
		packingTextbox.setValue(getInventoryTransferEndProduct().getInventoryPacking().toString());
	}

	public void onClick$saveButton(Event event) throws Exception {
		InventoryTransferEndProduct modEndProduct = 
				getUserModifiedEndProduct(getInventoryTransferEndProduct());
		
		// send Event
		Events.sendEvent(Events.ON_OK, inventoryTransferEndProductEditDialog, modEndProduct);
		
		inventoryTransferEndProductEditDialog.detach();
	}

	private InventoryTransferEndProduct getUserModifiedEndProduct(InventoryTransferEndProduct modEndProduct) {
		modEndProduct.setMarking(coilNoTextbox.getValue());
		modEndProduct.setThickness(thicknessDecimalbox.getValue());
		modEndProduct.setWidth(widthDecimalbox.getValue());
		modEndProduct.setLength(lengthDecimalbox.getValue());
		modEndProduct.setSheetQuantity(quantityShtLineIntbox.getValue());
		modEndProduct.setWeightQuantity(quantityKgDecimalbox.getValue());
		
		return modEndProduct;

	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTransferEndProductEditDialog.detach();
	}
	
	public InventoryTransferEndProduct getInventoryTransferEndProduct() {
		return inventoryTransferEndProduct;
	}

	public void setInventoryTransferEndProduct(InventoryTransferEndProduct inventoryTransferEndProduct) {
		this.inventoryTransferEndProduct = inventoryTransferEndProduct;
	}
	
	
}
