package com.pyramix.swi.webui.inventory.bukapeti;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class InventoryBukaPetiViewDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1472280916684899334L;

	private InventoryBukaPetiDao inventoryBukapetiDao;
	
	private Window inventoryBukapetiViewDialogWin;
	private Datebox orderDatebox, completeDatebox;
	private Textbox processNumberTextbox, noteTextbox;
	private Combobox bukapetiLocationCombobox, bukapetiStatusCombobox;
	private Label infoMaterialLabel, infoEndProductLabel;
	private Listbox materialInventoryListbox, productInventoryListbox;
	
	private InventoryBukaPeti inventoryBukapeti;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setInventoryBukapeti(
				(InventoryBukaPeti) arg.get("inventoryBukapeti"));
	}

	public void onCreate$inventoryBukapetiViewDialogWin(Event event) throws Exception {
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		completeDatebox.setLocale(getLocale());
		completeDatebox.setFormat(getLongDateFormat());
		
		orderDatebox.setValue(getInventoryBukapeti().getOrderDate());
		processNumberTextbox.setValue(getInventoryBukapeti().getBukapetiNumber().getSerialComp());
		completeDatebox.setValue(getInventoryBukapeti().getCompleteDate());
		
		bukapetiLocationCombobox.setValue("SWI");
		bukapetiStatusCombobox.setValue(getInventoryBukapeti().getInventoryBukapetiStatus().toString());
		
		noteTextbox.setValue(getInventoryBukapeti().getNote());
		
		// material
		displayInventoryBukapetiMaterial();
		
		// end-product
		displayInventoryBukapetiEndProduct();
	}
	
	private void displayInventoryBukapetiMaterial() throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukapetiMaterialsByProxy(getInventoryBukapeti().getId());
		List<InventoryBukaPetiMaterial> bukapetiMaterialList =
				inventoryBukapetiByProxy.getBukapetiMaterialList();

		// Materi: 2 items - 12MT
		infoMaterialLabel.setValue("Materi: "+bukapetiMaterialList.size()+" petian - "+
				getFormatedFloatLocal(getTotalMaterialWeightQuantity(bukapetiMaterialList))+
				" Kg.");

		materialInventoryListbox.setModel(
				new ListModelList<InventoryBukaPetiMaterial>(bukapetiMaterialList));
		materialInventoryListbox.setItemRenderer(getMaterialInventoryListitemRenderer());
		
	}

	private BigDecimal getTotalMaterialWeightQuantity(List<InventoryBukaPetiMaterial> bukapetiMaterialList) {
		BigDecimal totalQtyMaterial = BigDecimal.ZERO;
		for (InventoryBukaPetiMaterial material : bukapetiMaterialList) {
			totalQtyMaterial = totalQtyMaterial.add(material.getWeightQuantity());
		}
		
		return totalQtyMaterial;
	}

	private ListitemRenderer<InventoryBukaPetiMaterial> getMaterialInventoryListitemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiMaterial>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiMaterial material, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(getFormatedInteger(index+1)+".");
				lc.setParent(item);

				// No.Coil
				lc = new Listcell(material.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Kode
				lc = new Listcell(material.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(
						getFormatedFloatLocal(material.getThickness())+" x "+
						getFormatedFloatLocal(material.getWidth())+" x "+
						(material.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(material.getLength())));
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(material.getInventoryPacking().toString());
				lc.setParent(item);

				// Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(material.getWeightQuantity()));
				lc.setParent(item);

				// Qty(Sht/Line)
				lc = new Listcell(getFormatedInteger(material.getSheetQuantity()));
				lc.setParent(item);
				
			}
		};
	}
	
	
	private void displayInventoryBukapetiEndProduct() throws Exception {
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukaPetiEndProductByProxy(getInventoryBukapeti().getId());

		// Hasil Buka Peti: 2 Ukuran
		infoEndProductLabel.setValue("Hasil Buka Peti: "+
				inventoryBukapetiByProxy.getBukapetiEndProduct().size()+" Ukuran");

		productInventoryListbox.setModel(
				new ListModelList<InventoryBukaPetiEndProduct>(inventoryBukapetiByProxy.getBukapetiEndProduct()));
		productInventoryListbox.setItemRenderer(getProductInventoryListitemRenderer());
	}

	private ListitemRenderer<InventoryBukaPetiEndProduct> getProductInventoryListitemRenderer() {
		
		return new ListitemRenderer<InventoryBukaPetiEndProduct>() {
			
			@Override
			public void render(Listitem item, InventoryBukaPetiEndProduct endProduct, int index) throws Exception {
				Listcell lc;
				
				// Material
				lc = new Listcell(endProduct.getMarking());
				lc.setParent(item);

				// Kode
				lc = new Listcell(endProduct.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(						
						getFormatedFloatLocal(endProduct.getThickness())+" x "+
						getFormatedFloatLocal(endProduct.getWidth())+" x "+
						(endProduct.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(endProduct.getLength())));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(endProduct.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(endProduct.getWeightQuantity()));
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(endProduct.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Customer
				lc = new Listcell("SWI");
				lc.setParent(item);
				
			}
		};
	}

	public void onClick$cancelButton(Event event) throws Exception {
		inventoryBukapetiViewDialogWin.detach();
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
