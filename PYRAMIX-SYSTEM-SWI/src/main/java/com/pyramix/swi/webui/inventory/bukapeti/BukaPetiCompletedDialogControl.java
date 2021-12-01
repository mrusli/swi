package com.pyramix.swi.webui.inventory.bukapeti;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
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
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiStatus;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class BukaPetiCompletedDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6378466809576506551L;

	private InventoryBukaPetiDao inventoryBukapetiDao;
	
	private Window bukapetiCompletedDialogWin;
	private Datebox completedDatebox, updatedDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox bukapetiStatusCombobox; 
	private Button saveButton, cancelButton;
	private Listbox completedProductListbox;
	private Label infoCompletedProductLabel;
	
	private InventoryBukaPeti inventoryBukapeti;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryBukapeti(
				(InventoryBukaPeti) arg.get("inventoryBukapeti"));
	}

	public void onCreate$bukapetiCompletedDialogWin(Event event) throws Exception {
		completedDatebox.setLocale(getLocale());
		completedDatebox.setFormat(getLongDateFormat());
		updatedDatebox.setLocale(getLocale());
		updatedDatebox.setFormat(getLongDateFormat());
		
		// bukapeti status
		setupInventoryBukapetiStatus();
		
		// hide the 'save' to inventory button
		saveButton.setVisible(false);

		displayBukapetiCompletedInfo();
	}
	
	private void setupInventoryBukapetiStatus() {
		Comboitem comboitem;
		
		for (InventoryBukaPetiStatus bukapetiStatus : InventoryBukaPetiStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(bukapetiStatus.toString());
			comboitem.setValue(bukapetiStatus);
			comboitem.setParent(bukapetiStatusCombobox);
		}
	}

	private void displayBukapetiCompletedInfo() throws Exception {
		completedDatebox.setValue(asDate(getLocalDate()));
		updatedDatebox.setValue(completedDatebox.getValue());
		processNumberTextbox.setValue(getInventoryBukapeti().getBukapetiNumber().getSerialComp());

		for (Comboitem comboitem : bukapetiStatusCombobox.getItems()) {
			if (getInventoryBukapeti().getInventoryBukapetiStatus().equals(comboitem.getValue())) {
				bukapetiStatusCombobox.setSelectedItem(comboitem);
			}
		}
		
		noteTextbox.setValue(getInventoryBukapeti().getNote());

		displayInventoryBukaPetiEndProduct();
	}

	private void displayInventoryBukaPetiEndProduct() throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukaPetiEndProductByProxy(getInventoryBukapeti().getId());
		List<InventoryBukaPetiEndProduct> bukapetiEndProductList = 
				inventoryBukapetiByProxy.getBukapetiEndProduct();
		
		// Hasil Buka Peti: 2 Ukuran
		infoCompletedProductLabel.setValue("Hasil Buka Peti: "+
				inventoryBukapetiByProxy.getBukapetiEndProduct().size()+" Ukuran");
		
		completedProductListbox.setModel(
				new ListModelList<InventoryBukaPetiEndProduct>(bukapetiEndProductList));
		completedProductListbox.setItemRenderer(getCompletedProductListitemRenderer());
	}

	private ListitemRenderer<InventoryBukaPetiEndProduct> getCompletedProductListitemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Status
				lc = initCompletion(new Listcell(), endProduct);
				lc.setParent(item);
				
				// Coil No.
				lc = initCoilNo(new Listcell(), endProduct);
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(endProduct.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);

				// Spesifikasi
				lc = initSpesification(new Listcell(), endProduct);
				lc.setParent(item);

				// Qty (Sht/Line)
				lc = initQtySL(new Listcell(), endProduct);
				lc.setParent(item);
				
				// Qty (Kg)
				lc = initQtyKg(new Listcell(), endProduct);
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(endProduct.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Customer
				lc = new Listcell("SWI");
				lc.setParent(item);
				
				item.setValue(endProduct);
			}

			private Listcell initCompletion(Listcell listcell, InventoryBukaPetiEndProduct endProduct) {
				Combobox statusCombobox = new Combobox();
				Comboitem comboitem;				
				for (InventoryBukaPetiStatus bukapetiStatus : InventoryBukaPetiStatus.values()) {
					comboitem = new Comboitem();
					comboitem.setLabel(bukapetiStatus.toString());
					comboitem.setValue(bukapetiStatus);
					comboitem.setParent(statusCombobox);
				}
				for (Comboitem item : statusCombobox.getItems()) {
					if (endProduct.getBukapetiProductStatus().equals(item.getValue())) {
						statusCombobox.setSelectedItem(item);
					}
				}
				statusCombobox.setWidth("110px");
				statusCombobox.setDisabled(endProduct.isUpdateToInventory());
				statusCombobox.setParent(listcell);

				return listcell;
			}
			
			private Listcell initCoilNo(Listcell listcell, InventoryBukaPetiEndProduct endProduct) {
				Textbox coilNoTextbox = new Textbox();
				
				coilNoTextbox.setValue(endProduct.getMarking());
				coilNoTextbox.setWidth("110px");
				coilNoTextbox.setDisabled(endProduct.isUpdateToInventory());
				coilNoTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesification(Listcell listcell, InventoryBukaPetiEndProduct endProduct) {
				Decimalbox thicknessDecimalbox = new Decimalbox();
				thicknessDecimalbox.setLocale(getLocale());
				thicknessDecimalbox.setStyle("text-align:right");
				thicknessDecimalbox.setWidth("50px");
				thicknessDecimalbox.setValue(endProduct.getThickness());
				thicknessDecimalbox.setDisabled(endProduct.isUpdateToInventory());
				thicknessDecimalbox.setParent(listcell);
				
				Label lbl01 = new Label();
				lbl01.setValue(" x ");
				lbl01.setParent(listcell);
				
				Decimalbox widthDecimalbox = new Decimalbox();
				widthDecimalbox.setLocale(getLocale());
				widthDecimalbox.setStyle("text-align:right");
				widthDecimalbox.setWidth("50px");
				widthDecimalbox.setValue(endProduct.getWidth());
				widthDecimalbox.setDisabled(endProduct.isUpdateToInventory());
				widthDecimalbox.setParent(listcell);
				
				Label lbl02 = new Label();
				lbl02.setValue(" x ");
				lbl02.setParent(listcell);
								
				Decimalbox lengthDecimalbox = new Decimalbox();
				lengthDecimalbox.setLocale(getLocale());
				lengthDecimalbox.setStyle("text-align:right");
				lengthDecimalbox.setWidth("50px");
				lengthDecimalbox.setValue(endProduct.getLength());
				lengthDecimalbox.setDisabled(endProduct.isUpdateToInventory());
				lengthDecimalbox.setParent(listcell);
												
				return listcell;
			}

			private Listcell initQtySL(Listcell listcell, InventoryBukaPetiEndProduct endProduct) {
				Intbox qtySLIntbox = new Intbox();
				qtySLIntbox.setValue(endProduct.getSheetQuantity());
				qtySLIntbox.setStyle("text-align:right");
				qtySLIntbox.setWidth("60px");
				qtySLIntbox.setDisabled(endProduct.isUpdateToInventory());
				qtySLIntbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtyKg(Listcell listcell, InventoryBukaPetiEndProduct endProduct) {
				Decimalbox qtyKgDecimalbox = new Decimalbox();
				qtyKgDecimalbox.setLocale(getLocale());
				qtyKgDecimalbox.setValue(endProduct.getWeightQuantity());
				qtyKgDecimalbox.setStyle("text-align:right");
				qtyKgDecimalbox.setWidth("80px");
				qtyKgDecimalbox.setDisabled(endProduct.isUpdateToInventory());
				qtyKgDecimalbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$updateButton(Event event) throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukaPetiEndProductByProxy(getInventoryBukapeti().getId());
		
		completedDatebox.setDisabled(true);
		inventoryBukapetiByProxy.setCompleteDate(completedDatebox.getValue());
		noteTextbox.setDisabled(true);
		inventoryBukapetiByProxy.setNote(noteTextbox.getValue());

		inventoryBukapetiByProxy.setUpdatedDate(updatedDatebox.getValue());
		
		// eval and set overall status
		inventoryBukapetiByProxy.setInventoryBukapetiStatus(evalAllBukapetiEndProductCompleted());
		// update the overall status
		for (Comboitem comboitem : bukapetiStatusCombobox.getItems()) {
			if (inventoryBukapetiByProxy.getInventoryBukapetiStatus().equals(comboitem.getValue())) {
				bukapetiStatusCombobox.setSelectedItem(comboitem);
			}
		}
		bukapetiStatusCombobox.setDisabled(true);
		
		// update endproduct
		for(int i=0; i<inventoryBukapetiByProxy.getBukapetiEndProduct().size(); i++) {
			Listitem item = completedProductListbox.getItemAtIndex(i);
			
			// Status
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			statusCombobox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setBukapetiProductStatus(
					statusCombobox.getSelectedItem().getValue());
			
			
			// Coil No.
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
			coilNoTextbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setMarking(
					coilNoTextbox.getValue());
			
			// spek: thickness
			Decimalbox thicknessDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(0);
			thicknessDecimalbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setThickness(
					thicknessDecimalbox.getValue());
			
			// spek: width
			Decimalbox widthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(2);
			widthDecimalbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setWidth(
					widthDecimalbox.getValue());
			
			// spek: length
			Decimalbox lengthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(4);
			lengthDecimalbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setLength(
					lengthDecimalbox.getValue());
			
			// Qty (Sht/Line)
			Intbox qtyShtLineIntbox = (Intbox) item.getChildren().get(4).getFirstChild();
			qtyShtLineIntbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setSheetQuantity(
					qtyShtLineIntbox.getValue());
			
			// Qty (Kg)
			Decimalbox qtyDecimalbox = (Decimalbox) item.getChildren().get(5).getFirstChild();
			qtyDecimalbox.setDisabled(true);
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setWeightQuantity(
					qtyDecimalbox.getValue());
			
			inventoryBukapetiByProxy.getBukapetiEndProduct().get(i).setUpdateToInventory(
					statusCombobox.getSelectedItem().getValue().equals(InventoryBukaPetiStatus.selesai));
		}

		// hide the cancel button forcing user to 'save' the 'selesai' product to inventory
		// -- if none of the status is 'selesai', the system will not save anything to inventory
		cancelButton.setVisible(false);
		
		// unhide the save button, forcing user to save into the inventory
		saveButton.setVisible(true);
		
		Events.sendEvent(Events.ON_CHANGE, bukapetiCompletedDialogWin, inventoryBukapetiByProxy);

	}
	
	private InventoryBukaPetiStatus evalAllBukapetiEndProductCompleted() {
		InventoryBukaPetiStatus bukapetiOverallStatus = null;
		
		for (Listitem item : completedProductListbox.getItems()) {
			// check whether status is 'selesai'
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			InventoryBukaPetiStatus bukapetiStatus = 
					statusCombobox.getSelectedItem().getValue();
			
			if (bukapetiStatus.compareTo(InventoryBukaPetiStatus.selesai)==0) {
				bukapetiOverallStatus = InventoryBukaPetiStatus.selesai;
			} else {
				bukapetiOverallStatus = InventoryBukaPetiStatus.proses;
				
				break;
			}
		}
		
		return bukapetiOverallStatus;
	}

	public void onSelect$completedDatebox(Event event) throws Exception {
		// No need to update -- when the user update, this should indicate the actual date
		// updatedDatebox.setValue(completedDatebox.getValue());
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		List<Inventory> inventoryList = new ArrayList<Inventory>();

		for (Listitem item : completedProductListbox.getItems()) {

			// Status
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();

			// Coil No.
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
		
			// spek: thickness
			Decimalbox thicknessDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(0);

			// spek: width
			Decimalbox widthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(2);
			
			// spek: length
			Decimalbox lengthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(4);
			
			// Qty (Sht/Line)
			Intbox qtyShtLineIntbox = (Intbox) item.getChildren().get(4).getFirstChild();
			
			// Qty (Kg)
			Decimalbox qtyDecimalbox = (Decimalbox) item.getChildren().get(5).getFirstChild();
			
			InventoryBukaPetiEndProduct endProduct = item.getValue();
			
			if (statusCombobox.getSelectedItem().getValue().equals(InventoryBukaPetiStatus.selesai)) {
				Inventory inventory = new Inventory();
				
				inventory.setThickness(thicknessDecimalbox.getValue());
				inventory.setWidth(widthDecimalbox.getValue());
				inventory.setLength(lengthDecimalbox.getValue());
				inventory.setSheetQuantity(qtyShtLineIntbox.getValue());
				inventory.setWeightQuantity(qtyDecimalbox.getValue());
				inventory.setMarking(coilNoTextbox.getValue());
				inventory.setDescription(null);
				inventory.setContractNumber(endProduct.getContractNumber());
				inventory.setLcNumber(endProduct.getLcNumber());
				inventory.setReceiveDate(asDate(getLocalDate()));
				inventory.setInventoryCode(endProduct.getInventoryCode());
				inventory.setInventoryStatus(InventoryStatus.ready);
				inventory.setInventoryLocation(endProduct.getInventoryLocation());
				inventory.setInventoryPacking(endProduct.getInventoryPacking());
				inventory.setNote("");
				inventory.setSku(null);
				
				inventoryList.add(inventory);
			}
		}
		
		Events.sendEvent(Events.ON_OK, bukapetiCompletedDialogWin, inventoryList);
		
		bukapetiCompletedDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		bukapetiCompletedDialogWin.detach();
	}

	public InventoryBukaPeti getInventoryBukapeti() {
		return inventoryBukapeti;
	}

	public void setInventoryBukapeti(InventoryBukaPeti inventoryBukapeti) {
		this.inventoryBukapeti = inventoryBukapeti;
	}

	public InventoryBukaPetiDao getInventoryBukapetiDao() {
		return inventoryBukapetiDao;
	}

	public void setInventoryBukapetiDao(InventoryBukaPetiDao inventoryBukapetiDao) {
		this.inventoryBukapetiDao = inventoryBukapetiDao;
	}
	
}
