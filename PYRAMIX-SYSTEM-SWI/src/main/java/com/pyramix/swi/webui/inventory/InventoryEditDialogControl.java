package com.pyramix.swi.webui.inventory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryEditDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3053183560845315107L;

	private InventoryDao inventoryDao;
	
	private Inventory inventory;
	
	private Window inventoryEditDialog;
	private Label idLabel;
	private Textbox coilNoTextbox, codeTextbox, contractTextbox, lcTextbox, noteTextbox;
	private Combobox packingCombobox, locationCombobox, inventoryStatusCombobox;
	private Datebox receivingDatebox;
	private Decimalbox thicknessDecimalbox, widthDecimalbox, lengthDecimalbox, qtyWeightDecimalbox;
	private Intbox qtySLIntbox;
	
	// private EventQueue eq;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventory(
				(Inventory) Executions.getCurrent().getArg().get("inventoryToEdit"));
	}
	
	public void onCreate$inventoryEditDialog(Event event) throws Exception {
		thicknessDecimalbox.setLocale(getLocale());
		widthDecimalbox.setLocale(getLocale());
		lengthDecimalbox.setLocale(getLocale());
		
		// for packing combobox
		loadPackingCombobox();
		
		// for location combobox
		loadLocationCombobox();
		
		// for status combobox
		loadStatusCombobox();
		
		// format datebox
		receivingDatebox.setFormat(getLongDateFormat());
		
		setInventoryInfo();

	}

	private void loadPackingCombobox() {		
		for (InventoryPacking inventoryPacking : InventoryPacking.values()) {
			Comboitem comboItem = new Comboitem();
			
			comboItem.setLabel(inventoryPacking.toString());
			comboItem.setValue(inventoryPacking);
			comboItem.setParent(packingCombobox);
		}
		
	}

	private void loadLocationCombobox() {
		for (InventoryLocation inventoryLocation : InventoryLocation.values()) {
			Comboitem comboItem = new Comboitem();
			
			comboItem.setLabel(inventoryLocation.toString());
			comboItem.setValue(inventoryLocation);
			comboItem.setParent(locationCombobox);
		}
		
	}

	private void loadStatusCombobox() {
		for (InventoryStatus inventoryStatus : InventoryStatus.values()) {
			Comboitem comboItem = new Comboitem();
			
			comboItem.setLabel(inventoryStatus.toString());
			comboItem.setValue(inventoryStatus);
			comboItem.setParent(inventoryStatusCombobox);
		}
	}
	
	private void setInventoryInfo() {
		idLabel.setValue("Id#"+String.valueOf(getInventory().getId()));
		
		coilNoTextbox.setValue(getInventory().getMarking());
		packingCombobox.setValue(getInventory().getInventoryPacking().toString());
		locationCombobox.setValue(getInventory().getInventoryLocation().toString());
		inventoryStatusCombobox.setValue(getInventory().getInventoryStatus().toString());
		receivingDatebox.setValue(getInventory().getReceiveDate());
		thicknessDecimalbox.setValue(getInventory().getThickness()); 
		widthDecimalbox.setValue(getInventory().getWidth()); 
		lengthDecimalbox.setValue(getInventory().getLength());
		
		// inventoryCode
		codeTextbox.setValue(getInventory().getInventoryCode().getProductCode());
		codeTextbox.setAttribute("inventoryCode", getInventory().getInventoryCode());
		
		qtySLIntbox.setValue(getInventory().getSheetQuantity());
		qtyWeightDecimalbox.setValue(getInventory().getWeightQuantity());
		contractTextbox.setValue(getInventory().getContractNumber()); 
		lcTextbox.setValue(getInventory().getLcNumber());
		noteTextbox.setValue(getInventory().getNote());
	}

	public void onClick$codeButton(Event event) throws Exception {
		
		Window inventoryCodeDialog = 
				(Window) Executions.createComponents(
						"/inventory/InventoryCodeDialog.zul", null, null);
		
		inventoryCodeDialog.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				InventoryCode inventoryCode = (InventoryCode) event.getData();
				
				codeTextbox.setValue(inventoryCode.getProductCode());
				codeTextbox.setAttribute("inventoryCode", inventoryCode);
			}
		});
		
		inventoryCodeDialog.doModal();
		
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		Inventory userModInventory = getUpdatedInventory();

		Events.sendEvent(Events.ON_OK, inventoryEditDialog, userModInventory);
				
		inventoryEditDialog.detach();

/*		try {
			// save / update the changes into DB
			// getInventoryDao().update(userModInventory);			
			
			// re-index -- because indexer is set to normal
			// getInventoryDao().createIndexer();			
			
			// notify
			// Clients.showNotification("Perubahan Inventory sukses tersimpan.", "info", null, "middle_center", 5);
			
			// notify to update
			// eq = EventQueues.lookup("interactive", EventQueues.APPLICATION, true);
			// eq.publish(new Event(Events.ON_CHANGE, inventoryEditDialog, userModInventory));
			
			// detach
			
		} catch (Exception e) {
			Messagebox.show("Perubahan Inventory tidak dapat disimpan. "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
		}
*/	}
	
	private Inventory getUpdatedInventory() throws Exception {
		Inventory updatedInventory = getInventory();

		if (coilNoTextbox.getValue().isEmpty()) {
			throw new Exception("No Coil belum diisi.");
		}
		if (packingCombobox.getSelectedItem() == null) {
			throw new Exception("Packing Inventory belum dipilih.  Pilih salah satu packing.");
		}
		if (locationCombobox.getSelectedItem() == null) {
			throw new Exception("Lokasi Inventory belum dipilih.  Pilih salah satu lokasi.");
		}
		if (inventoryStatusCombobox.getSelectedItem() == null) {
			throw new Exception("Status Inventory belum dipilih.  Pilih salah satu status.");
		}
		if (receivingDatebox.getValue() == null) {
			throw new Exception("Tanggal Penerimaan Inventory belum dipilih.  Pilih tanggal.");
		}
		if (thicknessDecimalbox.getValue() == null) {
			throw new Exception("Ketebalan Inventory belum diisi.");
		}
		if (widthDecimalbox.getValue() == null) {
			throw new Exception("Lebar Inventory belum diisi.");
		}
		if (lengthDecimalbox.getValue() == null) {
			throw new Exception("Panjang Inventory belum diisi.  Isi '0' (nol) untuk packing 'Coil'.");
		}
		if (codeTextbox.getAttribute("inventoryCode") == null) {
			throw new Exception("Kode Inventory belum diisi.");
		}
		if (qtySLIntbox.getValue() == null) {
			throw new Exception("Qty (Sht/Line) tidak ada nilai.  Masukan angka '0' (nol) apabila tidak mengetahui.");
		}
		if (qtyWeightDecimalbox.getValue() == null) {
			throw new Exception("Qty (Kg) tidak ada nilai.  Masukang angka '0' (nol) apabila tidak mengetahui.");
		}
		if (contractTextbox.getValue().isEmpty()) {
			throw new Exception("Nomor Kontrak belum diisi.");
		}
		if (lcTextbox.getValue().isEmpty()) {
			throw new Exception("Nomor LC belum diisi.");
		}
		
		updatedInventory.setMarking(coilNoTextbox.getValue());			
		updatedInventory.setInventoryPacking(packingCombobox.getSelectedItem().getValue());
		updatedInventory.setInventoryLocation(locationCombobox.getSelectedItem().getValue());
		updatedInventory.setInventoryStatus(inventoryStatusCombobox.getSelectedItem().getValue());		
		updatedInventory.setReceiveDate(receivingDatebox.getValue());
		updatedInventory.setThickness(thicknessDecimalbox.getValue());
		updatedInventory.setWidth(widthDecimalbox.getValue());
		updatedInventory.setLength(lengthDecimalbox.getValue());
		updatedInventory.setInventoryCode((InventoryCode) codeTextbox.getAttribute("inventoryCode"));
		updatedInventory.setSheetQuantity(qtySLIntbox.getValue());
		updatedInventory.setWeightQuantity(qtyWeightDecimalbox.getValue());
		updatedInventory.setContractNumber(contractTextbox.getValue());
		updatedInventory.setLcNumber(lcTextbox.getValue());
		updatedInventory.setNote(noteTextbox.getValue());
		
		return updatedInventory;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryEditDialog.detach();
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

}
