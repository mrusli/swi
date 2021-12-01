package com.pyramix.swi.webui.inventory.bukapeti.print;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;

import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.persistence.inventory.bukapeti.dao.InventoryBukaPetiDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PrintUtil;

public class InventoryBukaPetiOrderPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625741164524066355L;

	private InventoryBukaPetiDao inventoryBukapetiDao;
	
	private Vbox printVbox;
	private Label bukapetiDateLabel, bukapetiNumberLabel;
	private Grid bukapetiMaterialGrid;
	
	private InventoryBukaPeti inventoryBukapeti;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryBukapeti(
				(InventoryBukaPeti) arg.get("inventoryBukapeti"));
	}	
	
	public void onCreate$inventoryBukapetiPrintWin(Event event) throws Exception {
		bukapetiDateLabel.setValue("Jakarta, "+dateToStringDisplay(
				asLocalDate(getInventoryBukapeti().getOrderDate()), getLongDateFormat()));
		bukapetiNumberLabel.setValue(
				"BUKA PETI NO: "+getInventoryBukapeti().getBukapetiNumber().getSerialComp());
		
		InventoryBukaPeti inventoryBukapetiByProxy = 
				getInventoryBukapetiDao().getBukapetiMaterialsByProxy(getInventoryBukapeti().getId());
		
		bukapetiMaterialGrid.setModel(
				new ListModelList<InventoryBukaPetiMaterial>(inventoryBukapetiByProxy.getBukapetiMaterialList()));
		bukapetiMaterialGrid.setRowRenderer(getBukapetiMaterialGridRowRenderer());
	}
	
	private RowRenderer<InventoryBukaPetiMaterial> getBukapetiMaterialGridRowRenderer() {

		return new RowRenderer<InventoryBukaPetiMaterial>() {
			
			@Override
			public void render(Row row, InventoryBukaPetiMaterial bukapetiMaterial, int index) throws Exception {
				// No.
				row.appendChild(new Label(String.valueOf(index+1)+"."));
				
				// Qty(Sht)
				row.appendChild(new Label(getFormatedInteger(bukapetiMaterial.getSheetQuantity())));
				
				// Qty(Kg)
				row.appendChild(new Label(getFormatedFloatLocal(bukapetiMaterial.getWeightQuantity())));
				
				// Tipe
				row.appendChild(new Label(bukapetiMaterial.getInventoryCode().getProductCode()));
				row.setStyle("white-space: nowrap;");
				
				// Deskripsi
				row.appendChild(new Label(
						getFormatedFloatLocal(bukapetiMaterial.getThickness())+" x "+
						getFormatedFloatLocal(bukapetiMaterial.getWidth())+" x "+
						(bukapetiMaterial.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(bukapetiMaterial.getLength()))));
				
				// Coil No.
				row.appendChild(new Label(bukapetiMaterial.getMarking()));
				
			}
		};
	}

	public void onClick$printButton(Event event) throws Exception {
		
		PrintUtil.print(printVbox);
		
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
