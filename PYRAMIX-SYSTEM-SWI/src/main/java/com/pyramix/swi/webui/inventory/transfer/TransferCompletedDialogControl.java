package com.pyramix.swi.webui.inventory.transfer;

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

import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.persistence.inventory.transfer.dao.InventoryTransferDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class TransferCompletedDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8821253748091925728L;

	private InventoryTransferDao inventoryTransferDao;
	
	private Window transferCompletedDialogWin;
	private Datebox completedDatebox, updatedDatebox;
	private Button saveButton, cancelButton;
	private Combobox transferStatusCombobox;
	private Textbox processNumberTextbox, noteTextbox;
	private Label infoCompletedProductLabel;
	private Listbox completedProductListbox;
	
	private InventoryTransfer inventoryTransfer;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransfer(
				(InventoryTransfer) arg.get("inventoryTransfer"));
	}

	public void onCreate$transferCompletedDialogWin(Event event) throws Exception {
		completedDatebox.setLocale(getLocale());
		completedDatebox.setFormat(getLongDateFormat());
		updatedDatebox.setLocale(getLocale());
		updatedDatebox.setFormat(getLongDateFormat());
		
		setupTransferStatus();
		
		// hide the 'save' to inventory button
		saveButton.setVisible(false);

		displayTransferCompletedInfo();
	}
	
	private void setupTransferStatus() {
		Comboitem comboitem;
		
		for (InventoryTransferStatus transferStatus : InventoryTransferStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(transferStatus.toString());
			comboitem.setValue(transferStatus);
			comboitem.setParent(transferStatusCombobox);
		}
	}

	private void displayTransferCompletedInfo() throws Exception {
		// completed and updated date
		completedDatebox.setValue(asDate(getLocalDate()));
		updatedDatebox.setValue(completedDatebox.getValue());
		processNumberTextbox.setValue(getInventoryTransfer().getTransferNumber().getSerialComp());
		// status
		for (Comboitem item : transferStatusCombobox.getItems()) {
			if (item.getValue().equals(getInventoryTransfer().getInventoryTransferStatus())) {
				transferStatusCombobox.setSelectedItem(item);
			}
		}
		// note
		noteTextbox.setValue(getInventoryTransfer().getNote());
		
		// display product -- to update to inventory later
		displayInventoryTransferEndProduct();
	}

	private void displayInventoryTransferEndProduct() throws Exception {
		InventoryTransfer inventoryTransferEndProductByProxy = 
				getInventoryTransferDao().getInventoryTransferEndProductByProxy(getInventoryTransfer().getId());
		List<InventoryTransferEndProduct> endProductList =
				inventoryTransferEndProductByProxy.getTransferEndProductList();
		
		infoCompletedProductLabel.setValue("Hasil Transfer: "+
				endProductList.size()+" petian.");
		
		completedProductListbox.setModel(
				new ListModelList<InventoryTransferEndProduct>(endProductList));
		completedProductListbox.setItemRenderer(getCompletedProductListitemRenderer());
	}

	private ListitemRenderer<InventoryTransferEndProduct> getCompletedProductListitemRenderer() {
		
		return new ListitemRenderer<InventoryTransferEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryTransferEndProduct endProduct, int index) throws Exception {
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
				
				// Lokasi
				lc = new Listcell("SWI");
				lc.setParent(item);
				
				item.setValue(endProduct);
			}

			private Listcell initCompletion(Listcell listcell, InventoryTransferEndProduct endProduct) {
				Combobox statusCombobox = new Combobox();
				Comboitem comboitem;				
				for (InventoryTransferStatus transferStatus : InventoryTransferStatus.values()) {
					comboitem = new Comboitem();
					comboitem.setLabel(transferStatus.toString());
					comboitem.setValue(transferStatus);
					comboitem.setParent(statusCombobox);
				}
				for (Comboitem item : statusCombobox.getItems()) {
					if (item.getValue().equals(endProduct.getTransferProductStatus())) {
						statusCombobox.setSelectedItem(item);
					}
				}
				statusCombobox.setWidth("110px");
				statusCombobox.setDisabled(endProduct.isUpdateInventory());
				statusCombobox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initCoilNo(Listcell listcell, InventoryTransferEndProduct endProduct) {
				Textbox coilNoTextbox = new Textbox();
				
				coilNoTextbox.setValue(endProduct.getMarking());
				coilNoTextbox.setWidth("110px");
				coilNoTextbox.setDisabled(endProduct.isUpdateInventory());
				coilNoTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesification(Listcell listcell, InventoryTransferEndProduct endProduct) {
				Decimalbox thicknessDecimalbox = new Decimalbox();
				thicknessDecimalbox.setLocale(getLocale());
				thicknessDecimalbox.setStyle("text-align:right");
				thicknessDecimalbox.setWidth("50px");
				thicknessDecimalbox.setValue(endProduct.getThickness());
				thicknessDecimalbox.setDisabled(endProduct.isUpdateInventory());
				thicknessDecimalbox.setParent(listcell);
				
				Label lbl01 = new Label();
				lbl01.setValue(" x ");
				lbl01.setParent(listcell);
				
				Decimalbox widthDecimalbox = new Decimalbox();
				widthDecimalbox.setLocale(getLocale());
				widthDecimalbox.setStyle("text-align:right");
				widthDecimalbox.setWidth("50px");
				widthDecimalbox.setValue(endProduct.getWidth());
				widthDecimalbox.setDisabled(endProduct.isUpdateInventory());
				widthDecimalbox.setParent(listcell);
				
				Label lbl02 = new Label();
				lbl02.setValue(" x ");
				lbl02.setParent(listcell);
								
				Decimalbox lengthDecimalbox = new Decimalbox();
				lengthDecimalbox.setLocale(getLocale());
				lengthDecimalbox.setStyle("text-align:right");
				lengthDecimalbox.setWidth("50px");
				lengthDecimalbox.setValue(endProduct.getLength());
				lengthDecimalbox.setDisabled(endProduct.isUpdateInventory());
				lengthDecimalbox.setParent(listcell);
												
				return listcell;
			}

			private Listcell initQtySL(Listcell listcell, InventoryTransferEndProduct endProduct) {
				Intbox qtySLIntbox = new Intbox();
				qtySLIntbox.setValue(endProduct.getSheetQuantity());
				qtySLIntbox.setStyle("text-align:right");
				qtySLIntbox.setWidth("60px");
				qtySLIntbox.setDisabled(endProduct.isUpdateInventory());
				qtySLIntbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtyKg(Listcell listcell, InventoryTransferEndProduct endProduct) {
				Decimalbox qtyKgDecimalbox = new Decimalbox();
				qtyKgDecimalbox.setLocale(getLocale());
				qtyKgDecimalbox.setValue(endProduct.getWeightQuantity());
				qtyKgDecimalbox.setStyle("text-align:right");
				qtyKgDecimalbox.setWidth("80px");
				qtyKgDecimalbox.setDisabled(endProduct.isUpdateInventory());
				qtyKgDecimalbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$updateButton(Event event) throws Exception {
		InventoryTransfer inventoryTransferEndProductByProxy = 
				getInventoryTransferDao().getInventoryTransferEndProductByProxy(getInventoryTransfer().getId());
		
		completedDatebox.setDisabled(true);
		inventoryTransferEndProductByProxy.setCompleteDate(completedDatebox.getValue());
		noteTextbox.setDisabled(true);
		inventoryTransferEndProductByProxy.setNote(noteTextbox.getValue());
		inventoryTransferEndProductByProxy.setUpdatedDate(updatedDatebox.getValue());
		
		// eval and set overall status
		inventoryTransferEndProductByProxy.setInventoryTransferStatus(evalAllBukapetiEndProductCompleted());
		// update the overall status
		for (Comboitem item : transferStatusCombobox.getItems()) {
			if (item.getValue().equals(inventoryTransferEndProductByProxy.getInventoryTransferStatus())) {
				transferStatusCombobox.setSelectedItem(item);
			}
		}
		transferStatusCombobox.setDisabled(true);
		
		// update endproduct
		for(int i=0; i<inventoryTransferEndProductByProxy.getTransferEndProductList().size(); i++) {
			Listitem item = completedProductListbox.getItemAtIndex(i);

			// Status
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			statusCombobox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setTransferProductStatus(
					statusCombobox.getSelectedItem().getValue());
			
			// Coil No.
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
			coilNoTextbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setMarking(coilNoTextbox.getValue());
			
			// spek: thickness
			Decimalbox thicknessDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(0);
			thicknessDecimalbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setThickness(thicknessDecimalbox.getValue());
			
			// spek: width
			Decimalbox widthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(2);
			widthDecimalbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setWidth(widthDecimalbox.getValue());
			
			// spek: length
			Decimalbox lengthDecimalbox = (Decimalbox) item.getChildren().get(3).getChildren().get(4);
			lengthDecimalbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setLength(lengthDecimalbox.getValue());
			
			// Qty (Sht/Line)
			Intbox qtyShtLineIntbox = (Intbox) item.getChildren().get(4).getFirstChild();
			qtyShtLineIntbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setSheetQuantity(qtyShtLineIntbox.getValue());
			
			// Qty (Kg)
			Decimalbox qtyDecimalbox = (Decimalbox) item.getChildren().get(5).getFirstChild();
			qtyDecimalbox.setDisabled(true);
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setWeightQuantity(qtyDecimalbox.getValue());
			
			inventoryTransferEndProductByProxy.getTransferEndProductList().get(i).setUpdateInventory(
					statusCombobox.getSelectedItem().getValue().equals(InventoryTransferStatus.selesai));
			
			// hide the cancel button forcing user to 'save' the 'selesai' product to inventory
			// -- if none of the status is 'selesai', the system will not save anything to inventory
			cancelButton.setVisible(false);

			// unhide the save button, forcing user to save into the inventory
			saveButton.setVisible(true);

			Events.sendEvent(Events.ON_CHANGE, transferCompletedDialogWin, inventoryTransferEndProductByProxy);
			
		}
		
	}
	
	private InventoryTransferStatus evalAllBukapetiEndProductCompleted() {
		InventoryTransferStatus transferOverallStatus = null;
		
		for (Listitem item : completedProductListbox.getItems()) {
			// check whether status is 'selesai'
			Combobox statusCombobox = (Combobox) item.getChildren().get(0).getFirstChild();
			InventoryTransferStatus transferStatus =
					statusCombobox.getSelectedItem().getValue();
			
			if (transferStatus.equals(InventoryTransferStatus.selesai)) {
				transferOverallStatus = InventoryTransferStatus.selesai;
			} else {
				transferOverallStatus = InventoryTransferStatus.proses;
				
				break;
			}
		}
		
		return transferOverallStatus;
	}

	public void onClick$saveButton(Event event) throws Exception {

		List<InventoryTransferEndProduct> endProductList = new ArrayList<InventoryTransferEndProduct>();
		
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

			if (statusCombobox.getSelectedItem().getValue().equals(InventoryTransferStatus.selesai)) {
				InventoryTransferEndProduct endProduct = item.getValue();
				
				endProduct.setMarking(coilNoTextbox.getValue());
				endProduct.setThickness(thicknessDecimalbox.getValue());
				endProduct.setWidth(widthDecimalbox.getValue());
				endProduct.setLength(lengthDecimalbox.getValue());
				endProduct.setSheetQuantity(qtyShtLineIntbox.getValue());
				endProduct.setWeightQuantity(qtyDecimalbox.getValue());
				
				endProduct.setInventoryLocation(endProduct.getInventoryLocation());
				
				endProductList.add(endProduct);
			}
		}
		
		Events.sendEvent(Events.ON_OK, transferCompletedDialogWin, endProductList);
		
		transferCompletedDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		transferCompletedDialogWin.detach();
	}
	
	public InventoryTransfer getInventoryTransfer() {
		return inventoryTransfer;
	}

	public void setInventoryTransfer(InventoryTransfer inventoryTransfer) {
		this.inventoryTransfer = inventoryTransfer;
	}

	public InventoryTransferDao getInventoryTransferDao() {
		return inventoryTransferDao;
	}

	public void setInventoryTransferDao(InventoryTransferDao inventoryTransferDao) {
		this.inventoryTransferDao = inventoryTransferDao;
	}

	
}
