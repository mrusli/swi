package com.pyramix.swi.webui.inventory;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class InventoryTypeDialogTypeControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7688762210134701917L;
	
	private Window inventoryTypeDialogTypeWin;
	private Textbox typeNameTextbox, descriptionTextbox;
	private Decimalbox typeDensityDecimalbox;
	
	private InventoryTypeData inventoryTypeData;
	private InventoryType inventoryType;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTypeData(
				(InventoryTypeData) arg.get("inventoryTypeData"));
	}

	public void onCreate$inventoryTypeDialogTypeWin(Event event) throws Exception {
		typeDensityDecimalbox.setLocale(getLocale());
		
		setPageMode(getInventoryTypeData().getPageMode());
		
		switch (getPageMode()) {
		case NEW:
			inventoryTypeDialogTypeWin.setTitle("Tambah (Add) Inventory Type");
			
			break;
		case EDIT:
			inventoryTypeDialogTypeWin.setTitle("Merubah (Edit) Inventory Type");
			
			break;
		default:
			break;
		}

		setInventoryType(getInventoryTypeData().getInventoryType());
		
		displayInventoryTypeInfo();
	}

	private void displayInventoryTypeInfo() {
		if (getInventoryType().getId()==Long.MIN_VALUE) {
			// new
			typeNameTextbox.setValue(""); 
			typeDensityDecimalbox.setValue(BigDecimal.ZERO);
			descriptionTextbox.setValue("");
		} else {
			// edit
			typeNameTextbox.setValue(getInventoryType().getProductType());
			typeDensityDecimalbox.setValue(getInventoryType().getDensity());
			descriptionTextbox.setValue(getInventoryType().getProductDescription());
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		InventoryType inventoryType = getUserModifiedInventoryType(getInventoryType());
		
		Events.sendEvent(getPageMode().compareTo(PageMode.NEW)==0 ? 
				Events.ON_OK : Events.ON_CHANGE, inventoryTypeDialogTypeWin, inventoryType);
		
		inventoryTypeDialogTypeWin.detach();
	}
	
	private InventoryType getUserModifiedInventoryType(InventoryType inventoryType) {
		inventoryType.setProductType(typeNameTextbox.getValue());
		inventoryType.setDensity(typeDensityDecimalbox.getValue());
		inventoryType.setProductDescription(descriptionTextbox.getValue());
		
		return inventoryType;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryTypeDialogTypeWin.detach();
	}

	public InventoryTypeData getInventoryTypeData() {
		return inventoryTypeData;
	}

	public void setInventoryTypeData(InventoryTypeData inventoryTypeData) {
		this.inventoryTypeData = inventoryTypeData;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

}
