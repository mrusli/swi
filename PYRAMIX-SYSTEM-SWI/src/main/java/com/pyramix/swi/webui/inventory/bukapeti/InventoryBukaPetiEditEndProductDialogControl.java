package com.pyramix.swi.webui.inventory.bukapeti;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryBukaPetiEditEndProductDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6209791681664262933L;

	private Window inventoryBukaPetiEndProductEditDialog;
	private Textbox coilNoTextbox, kodeTextbox, packingTextbox;
	private Decimalbox thicknessDecimalbox, widthDecimalbox, lengthDecimalbox, 
		quantityKgDecimalbox;
	private Intbox quantityShtLineIntbox;
	private Checkbox forSWICheckbox;
	
	private InventoryBukaPetiEndProduct bukapetiEndProduct;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setBukapetiEndProduct(
				(InventoryBukaPetiEndProduct) arg.get("bukapetiEndProduct"));
	}
	
	public void onCreate$inventoryBukaPetiEndProductEditDialog(Event event) throws Exception {
		thicknessDecimalbox.setLocale(getLocale());
		widthDecimalbox.setLocale(getLocale());
		lengthDecimalbox.setLocale(getLocale());
		
		coilNoTextbox.setValue(getBukapetiEndProduct().getMarking());
		kodeTextbox.setValue(getBukapetiEndProduct().getInventoryCode().getProductCode());
		thicknessDecimalbox.setValue(getBukapetiEndProduct().getThickness());
		widthDecimalbox.setValue(getBukapetiEndProduct().getWidth());
		lengthDecimalbox.setValue(getBukapetiEndProduct().getLength());
		quantityShtLineIntbox.setValue(getBukapetiEndProduct().getSheetQuantity());
		quantityKgDecimalbox.setValue(getBukapetiEndProduct().getWeightQuantity());
		packingTextbox.setValue(getBukapetiEndProduct().getInventoryPacking().toString());
		forSWICheckbox.setChecked(true);
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		InventoryBukaPetiEndProduct modEndProduct = 
				getUserModifiedEndProduct(getBukapetiEndProduct());
		
		// send Event
		Events.sendEvent(Events.ON_OK, inventoryBukaPetiEndProductEditDialog, modEndProduct);
		
		inventoryBukaPetiEndProductEditDialog.detach();
	}

	private InventoryBukaPetiEndProduct getUserModifiedEndProduct(InventoryBukaPetiEndProduct modEndProduct) {
		modEndProduct.setMarking(coilNoTextbox.getValue());
		modEndProduct.setThickness(thicknessDecimalbox.getValue());
		modEndProduct.setWidth(widthDecimalbox.getValue());
		modEndProduct.setLength(lengthDecimalbox.getValue());
		modEndProduct.setSheetQuantity(quantityShtLineIntbox.getValue());
		modEndProduct.setWeightQuantity(quantityKgDecimalbox.getValue());
		
		return modEndProduct;
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		inventoryBukaPetiEndProductEditDialog.detach();
	}

	public InventoryBukaPetiEndProduct getBukapetiEndProduct() {
		return bukapetiEndProduct;
	}

	public void setBukapetiEndProduct(InventoryBukaPetiEndProduct bukapetiEndProduct) {
		this.bukapetiEndProduct = bukapetiEndProduct;
	}
}
