package com.pyramix.swi.webui.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryAddDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3714116135737394137L;
	
	private InventoryDao inventoryDao;
	
	private Window inventoryAddDialog;
	
	private Listbox inventoryListbox;
	private Datebox receivingDatebox;
	private Textbox contractTextbox, lcTextbox;
	
	private List<Inventory> inventoryList;
	private List<InventoryCode> inventoryCodeList;
	
	private ListModelList<Inventory> inventoryListModel;
	
	public void onCreate$inventoryAddDialog(Event event) throws Exception {
		// set the receiving date -- today date
		receivingDatebox.setFormat(getLongDateFormat());
		receivingDatebox.setValue(asDate(getLocalDate()));
		
		// inventory list
		setInventoryList(new ArrayList<Inventory>());
		
		setInventoryListModel(new ListModelList<Inventory>(getInventoryList()));
		
		inventoryListbox.setModel(getInventoryListModel());
		inventoryListbox.setItemRenderer(getInventoryListitemRenderer());
	}

	private ListitemRenderer<Inventory> getInventoryListitemRenderer() {

		return new ListitemRenderer<Inventory>() {
			
			@Override
			public void render(Listitem item, Inventory inventory, int arg2) throws Exception {
				Listcell lc;
				
				// Kode
				lc = initInventoryCode(new Listcell(), inventory.getInventoryCode());
				lc.setParent(item);

				// Ketebalan(mm)
				lc = initKetebalan(new Listcell(), inventory.getThickness());
				lc.setParent(item);				

				// Lebar(mm)
				lc = initLebar(new Listcell(), inventory.getWidth());
				lc.setParent(item);				
				
				// Panjang(mm)
				lc = initPanjang(new Listcell(), inventory.getLength());
				lc.setParent(item);				
				
				// Qty (Sht/Line)
				lc = initQtySL(new Listcell(), inventory.getSheetQuantity());
				lc.setParent(item);
				
				// Qty (Kg)
				lc = intQtyKg(new Listcell(), inventory.getWeightQuantity());
				lc.setParent(item);
			
				// Packing
				lc = initPacking(new Listcell(), inventory.getInventoryPacking());
				lc.setParent(item);
				
				// Lokasi
				lc = initLokasi(new Listcell(), inventory.getInventoryLocation());
				lc.setParent(item);
				
				// No.Coil
				lc = initCoilNo(new Listcell(), inventory.getMarking());
				lc.setParent(item);
				
				// Status
				lc = initStatus(new Listcell(), inventory.getInventoryStatus());
				lc.setParent(item);
				
			}

			private Listcell initInventoryCode(Listcell listcell, InventoryCode inventoryCode) {
				Textbox codeTextbox = new Textbox();
				codeTextbox.setWidth("125px");
				codeTextbox.setSclass("z-textbox-in-listbox");
				codeTextbox.setReadonly(true);
				codeTextbox.setValue(inventoryCode != null ? inventoryCode.getProductCode() : "");
				codeTextbox.setAttribute("inventoryCode", inventoryCode);
				codeTextbox.setParent(listcell);
				
				Button selButton = new Button();
				selButton.setWidth("25px");
				selButton.setLabel("...");
				selButton.setSclass("selectButton-in-listbox");
				selButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
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
					
				});
				selButton.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initKetebalan(Listcell listcell, BigDecimal ketebalanValue) {
				Decimalbox ktblnDecimalbox = new Decimalbox();
				ktblnDecimalbox.setWidth("90px");
				ktblnDecimalbox.setSclass("z-textbox-in-listbox");
				// ktblnDecimalbox.setFormat(getFloatFormatLocal());
				ktblnDecimalbox.setLocale(getLocale());
				ktblnDecimalbox.setValue(ketebalanValue);
				ktblnDecimalbox.setParent(listcell);

				return listcell;
			}
			
			private Listcell initLebar(Listcell listcell, BigDecimal lebarValue) {
				Decimalbox lbrDecimalbox = new Decimalbox();
				lbrDecimalbox.setWidth("80px");
				lbrDecimalbox.setSclass("z-textbox-in-listbox");
				// lbrDecimalbox.setFormat(getFloatFormatLocal());
				lbrDecimalbox.setLocale(getLocale());
				lbrDecimalbox.setValue(lebarValue);
				lbrDecimalbox.setParent(listcell);

				return listcell;
			}

			private Listcell initPanjang(Listcell listcell, BigDecimal panjangValue) {
				Decimalbox pnjngDecimalbox = new Decimalbox();
				pnjngDecimalbox.setWidth("80px");
				pnjngDecimalbox.setSclass("z-textbox-in-listbox");
				// pnjngDecimalbox.setFormat(getFloatFormatLocal());
				pnjngDecimalbox.setLocale(getLocale());
				pnjngDecimalbox.setValue(panjangValue);
				pnjngDecimalbox.setParent(listcell);

				return listcell;
			}
			
			private Listcell initQtySL(Listcell listcell, int sheetQuantity) {
				Intbox qtyIntbox = new Intbox();
				qtyIntbox.setWidth("50px");
				qtyIntbox.setSclass("z-textbox-in-listbox");
				qtyIntbox.setFormat(getIntFormat());
				qtyIntbox.setMaxlength(6);
				qtyIntbox.setValue(sheetQuantity);
				qtyIntbox.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell intQtyKg(Listcell listcell, BigDecimal weightQuantity) {
				Decimalbox qtyDecimalbox = new Decimalbox();
				qtyDecimalbox.setWidth("70px");
				qtyDecimalbox.setSclass("z-textbox-in-listbox");
				// qtyDecimalbox.setFormat(getFloatFormatLocal());
				qtyDecimalbox.setLocale(getLocale());
				qtyDecimalbox.setMaxlength(9);
				qtyDecimalbox.setValue(weightQuantity);
				qtyDecimalbox.setParent(listcell);

				return listcell;
			}

			private Listcell initPacking(Listcell listcell, InventoryPacking inventoryPacking) {
				Combobox packingCombobox = new Combobox();
				for (InventoryPacking inventoryPackingEnum : InventoryPacking.values()) {
					Comboitem comboitem = new Comboitem();
					
					comboitem.setLabel(inventoryPackingEnum.toString());
					comboitem.setValue(inventoryPackingEnum);
					comboitem.setParent(packingCombobox);
				}
				packingCombobox.setWidth("105px");
				packingCombobox.setSclass("z-combobox-input-in-listbox");
				packingCombobox.setReadonly(true);
				packingCombobox.setValue(inventoryPacking != null ? inventoryPacking.toString() : "");
				packingCombobox.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initLokasi(Listcell listcell, InventoryLocation inventoryLocation) {
				Combobox lokasiCombobox = new Combobox();
				for (InventoryLocation inventoryLocationEnum : InventoryLocation.values()) {
					Comboitem comboItem = new Comboitem();
					
					comboItem.setLabel(inventoryLocationEnum.toString());
					comboItem.setValue(inventoryLocationEnum);
					comboItem.setParent(lokasiCombobox);
				}				
				lokasiCombobox.setWidth("105px");
				lokasiCombobox.setSclass("z-combobox-input-in-listbox");
				lokasiCombobox.setReadonly(true);
				lokasiCombobox.setValue(inventoryLocation != null ? inventoryLocation.toString() : "");				
				lokasiCombobox.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initCoilNo(Listcell listcell, String marking) {
				Textbox markingTextbox = new Textbox();
				markingTextbox.setWidth("140px");
				markingTextbox.setSclass("z-textbox-in-listbox");
				markingTextbox.setMaxlength(18);
				markingTextbox.setValue(marking);
				markingTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initStatus(Listcell listcell, InventoryStatus inventoryStatus) {
				Combobox statusCombobox = new Combobox();
				for (InventoryStatus inventoryStatusEnum : InventoryStatus.values()) {
					Comboitem comboitem = new Comboitem();
					
					comboitem.setLabel(inventoryStatusEnum.toString());
					comboitem.setValue(inventoryStatusEnum);
					comboitem.setParent(statusCombobox);
				}
				statusCombobox.setWidth("105px");
				statusCombobox.setSclass("z-combobox-input-in-listbox");
				statusCombobox.setReadonly(true);
				statusCombobox.setValue(inventoryStatus != null ? inventoryStatus.toString() : "");
				statusCombobox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addRowButton(Event event) {
		if (getInventoryListModel().getSize() == InventoryControl.NEW_INVENTORY_MAX_ROW) {
			Messagebox.show("Batas maksimum penambahan inventory baru. Maksimum "+InventoryControl.NEW_INVENTORY_MAX_ROW+" baris.", 
					"Error", Messagebox.OK,  Messagebox.ERROR);
			
			return;
		}
		
		int newIndex = getInventoryListModel().getSize();
		
		Inventory inventory = new Inventory();
		
		// replicate 1st row
		if (newIndex > 0) {
			Listitem listitem = inventoryListbox.getItemAtIndex(newIndex-1);
			
			inventory.setInventoryCode(getPrevInventoryCode(newIndex-1));
			inventory.setThickness(getPrevDecimalbox(listitem, InventoryControl.IDX_KETEBALAN));
			inventory.setWidth(getPrevDecimalbox(listitem, InventoryControl.IDX_LEBAR));
			inventory.setLength(getPrevDecimalbox(listitem, InventoryControl.IDX_PANJANG));
			inventory.setSheetQuantity(getPrevIntbox(listitem, InventoryControl.IDX_QTY_SL));
			inventory.setWeightQuantity(getPrevDecimalbox(listitem, InventoryControl.IDX_QTY_KG));
			inventory.setInventoryPacking(getPrevInventoryPacking(newIndex-1));
			inventory.setInventoryLocation(getPrevInventoryLocation(newIndex-1));
			inventory.setMarking(getPrevTextbox(listitem, InventoryControl.IDX_NO_COIL));
			inventory.setInventoryStatus(getPrevInventoryStatus(newIndex-1));
		}
		
		getInventoryList().add(inventory);
		
		getInventoryListModel().add(newIndex, inventory);
	}

	private InventoryCode getPrevInventoryCode(int index) {
		Listitem listitem = inventoryListbox.getItemAtIndex(index);
		
		Textbox codeTextbox = (Textbox) listitem.getChildren().get(InventoryControl.IDX_KODE).getChildren().get(0);
		
		return (InventoryCode) codeTextbox.getAttribute("inventoryCode");
	}
	
	private BigDecimal getPrevDecimalbox(Listitem listitem, int colIndex) {
		Decimalbox decimalbox = (Decimalbox) listitem.getChildren().get(colIndex).getFirstChild();
		
		return decimalbox.getValue();
	}

	private int getPrevIntbox(Listitem listitem, int colIndex) {
		Intbox intbox = (Intbox) listitem.getChildren().get(colIndex).getFirstChild();
		
		return intbox.getValue();
	}	
	
	private InventoryLocation getPrevInventoryLocation(int index) {
		Listitem listitem = inventoryListbox.getItemAtIndex(index);
		
		Combobox lokasiCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_LOKASI).getFirstChild();
		
		return lokasiCombobox.getSelectedItem() != null ? lokasiCombobox.getSelectedItem().getValue() : null;
	}
	
	private String getPrevTextbox(Listitem listitem, int colIndex) {
		Textbox textbox = (Textbox) listitem.getChildren().get(colIndex).getFirstChild();
		
		return textbox.getValue();
	}	

	private InventoryPacking getPrevInventoryPacking(int index) {
		Listitem listitem = inventoryListbox.getItemAtIndex(index);
		
		Combobox packingCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_PACKING).getFirstChild();
		
		return packingCombobox.getSelectedItem() != null ? packingCombobox.getSelectedItem().getValue() : null;
	}
	
	private InventoryStatus getPrevInventoryStatus(int index) {
		Listitem listitem = inventoryListbox.getItemAtIndex(index);

		Combobox statusCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_STATUS).getFirstChild();
		
		return statusCombobox.getSelectedItem() != null ? statusCombobox.getSelectedItem().getValue() : null;
	}
	
	public void onClick$removeRowButton(Event event) {
		if (getInventoryListModel().isEmpty()) {
			Messagebox.show("Tidak ada inventory untuk dihapus. ", 
					"Error", Messagebox.OK,  Messagebox.ERROR);
			
			return;
		}
		
		int lastIndex = getInventoryListModel().getSize()-1;
		
		getInventoryList().remove(lastIndex);
		
		getInventoryListModel().remove(lastIndex);
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		Events.sendEvent(Events.ON_OK, inventoryAddDialog, getNewInventoryList());
		
		// close
		inventoryAddDialog.detach();

/*		try {
			// save
			getInventoryDao().save(getNewInventoryList());
			
			// notify
			Clients.showNotification("Penambahan Inventory sukses tersimpan.", "info", null, "middle_center", 5);
			
			// re-index -- because indexer is set to normal
			getInventoryDao().createIndexer();
			
			// notify to update
			EventQueue eq = EventQueues.lookup("interactive", EventQueues.APPLICATION, true);
			eq.publish(new Event("onDataChanged", inventoryAddDialog, "data has changed..."));
					
		} catch (Exception e) {
			Messagebox.show("Inventory baru tidak dapat disimpan. "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
		}
*/	}
	
	public List<Inventory> getNewInventoryList() throws Exception {
		List<Inventory> newInventoryList = new ArrayList<Inventory>();
		
		// read the values entered in the listbox
		for (Listitem listitem : inventoryListbox.getItems()) {
			Inventory inventory = new Inventory();
			
			// access
			Textbox kodeTextbox = (Textbox) listitem.getChildren().get(InventoryControl.IDX_KODE).getFirstChild();
			Decimalbox ktblnDecimalbox = (Decimalbox) listitem.getChildren().get(InventoryControl.IDX_KETEBALAN).getFirstChild();
			Decimalbox lbrDecimalbox = (Decimalbox) listitem.getChildren().get(InventoryControl.IDX_LEBAR).getFirstChild();
			Decimalbox pnjngDecimalbox = (Decimalbox) listitem.getChildren().get(InventoryControl.IDX_PANJANG).getFirstChild();
			Intbox qtyIntbox = (Intbox) listitem.getChildren().get(InventoryControl.IDX_QTY_SL).getFirstChild();
			Decimalbox qtyDecimalbox = (Decimalbox) listitem.getChildren().get(InventoryControl.IDX_QTY_KG).getFirstChild();
			Combobox packingCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_PACKING).getFirstChild();
			Combobox lokasiCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_LOKASI).getFirstChild();
			Textbox markingTextbox = (Textbox) listitem.getChildren().get(InventoryControl.IDX_NO_COIL).getFirstChild();
			Combobox statusCombobox = (Combobox) listitem.getChildren().get(InventoryControl.IDX_STATUS).getFirstChild();
			
			// obtain
			InventoryCode inventoryCode = (InventoryCode) kodeTextbox.getAttribute("inventoryCode");
			BigDecimal thickness = ktblnDecimalbox.getValue();
			BigDecimal width = lbrDecimalbox.getValue();
			BigDecimal length = pnjngDecimalbox.getValue();
			int qtySL=0;
			if (qtyIntbox.getValue() == null) {
				throw new Exception("Qty (Sht/Line) tidak ada nilai.  Masukan angka '0' (nol) apabila tidak mengetahui.");
			} else {
				qtySL = qtyIntbox.getValue();
			}
			BigDecimal qtyKG=BigDecimal.ZERO;
			if (qtyDecimalbox.getValue() == null) {
				throw new Exception("Qty (Kg) tidak ada nilai.  Masukang angka '0' (nol) apabila tidak mengetahui.");
			} else {
				qtyKG = qtyDecimalbox.getValue();				
			}
			InventoryPacking packing = packingCombobox.getSelectedItem() != null ? packingCombobox.getSelectedItem().getValue() : null;
			InventoryLocation location = lokasiCombobox.getSelectedItem() != null ? lokasiCombobox.getSelectedItem().getValue() : null;
			String marking = markingTextbox.getValue();
			InventoryStatus status = statusCombobox.getSelectedItem() != null ? statusCombobox.getSelectedItem().getValue() : null;
			
			// set
			if (inventoryCode == null) {
				throw new Exception("Kode Inventory belum diisi.");
			} else {
				inventory.setInventoryCode(inventoryCode);
			}
			if (thickness == null) {
				throw new Exception("Ketebalan Inventory belum diisi.");
			} else {
				inventory.setThickness(thickness);
			}
			if (width == null) {
				throw new Exception("Lebar Inventory belum diisi.");
			} else {
				inventory.setWidth(width);
			}
			if (length == null) {
				throw new Exception("Panjang Inventory belum diisi.  Isi '0' (nol) untuk packing 'Coil'.");
			} else {
				inventory.setLength(length);				
			}
			inventory.setSheetQuantity(qtySL);
			inventory.setWeightQuantity(qtyKG);
			if (packing == null) {
				throw new Exception("Packing Inventory belum dipilih.  Pilih salah satu packing.");
			} else {
				inventory.setInventoryPacking(packing);
			}
			if (location == null) {
				throw new Exception("Lokasi Inventory belum dipilih.  Pilih salah satu lokasi.");
			} else {
				inventory.setInventoryLocation(location);
			}
			if (marking.isEmpty()) {
				throw new Exception("No Coil belum diisi.");
			} else {
				inventory.setMarking(marking);
			}
			if (status == null) {
				throw new Exception("Status Inventory belum dipilih.  Pilih salah satu status.");
			} else {
				inventory.setInventoryStatus(status);
			}
			if (receivingDatebox.getValue() == null) {
				throw new Exception("Tanggal Penerimaan Inventory belum dipilih.  Pilih tanggal.");
			} else {
				inventory.setReceiveDate(receivingDatebox.getValue());
			}
			if (contractTextbox.getValue() == null) {
				throw new Exception("Nomor Kontrak belum diisi.");
			} else {
				inventory.setContractNumber(contractTextbox.getValue());
			}
			if (lcTextbox.getValue() == null) {
				throw new Exception("Nomor LC belum diisi.");
			} else {
				inventory.setLcNumber(lcTextbox.getValue());
			}
			
			// add to list
			newInventoryList.add(inventory);
		}
				
		return newInventoryList;
	}
	
	public void onClick$cancelButton(Event event) {
		inventoryAddDialog.detach();
	}

	public List<Inventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

	public ListModelList<Inventory> getInventoryListModel() {
		return inventoryListModel;
	}

	public void setInventoryListModel(ListModelList<Inventory> inventoryListModel) {
		this.inventoryListModel = inventoryListModel;
	}

	public List<InventoryCode> getInventoryCodeList() {
		return inventoryCodeList;
	}

	public void setInventoryCodeList(List<InventoryCode> inventoryCodeList) {
		this.inventoryCodeList = inventoryCodeList;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	

}
