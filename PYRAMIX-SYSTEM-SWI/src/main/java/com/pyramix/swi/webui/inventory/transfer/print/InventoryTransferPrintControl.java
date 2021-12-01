package com.pyramix.swi.webui.inventory.transfer.print;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferMaterial;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferStatus;
import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeType;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.persistence.inventory.transfer.dao.InventoryTransferDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PrintUtil;

public class InventoryTransferPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4043565737771571750L;

	private InventoryTransferDao inventoryTransferDao;
	private EmployeeDao employeeDao;
	
	private Window inventoryTransferPrintWin;
	private Label transferDateLabel, companyNameLabel, transferNumberLabel, companyAddress01Label,
		companyAddress02Label, companyCityLabel, companyTelephoneLabel, companyFaxLabel, fromWarehouseLabel,
		toWarehouseLabel, kendaraanLabel, selectedSupirLabel, selectedKendaraanLabel;
	private Combobox supirCombobox;
	private Grid transferMaterialGrid;
	private Vbox printVbox;
	
	private InventoryTransfer inventoryTransfer;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setInventoryTransfer(
				(InventoryTransfer) arg.get("inventoryTransfer"));
	}

	public void onCreate$inventoryTransferPrintWin(Event event) throws Exception {
		// supir & kendaraan
		supirCombobox.setValue(getInventoryTransfer().getSupir());
		kendaraanLabel.setValue(getInventoryTransfer().getNo_polisi());
		
		setupEmployeeCombobox();
		
		transferDateLabel.setValue("Jakarta, "+dateToStringDisplay(asLocalDate(
				getInventoryTransfer().getOrderDate()), getLongDateFormat()));

		// company - internal
		InventoryTransfer inventoryTransferCompanyByProxy = 
				getInventoryTransferDao().getInventoryTransferFromCoByProxy(getInventoryTransfer().getId());
		Company company =
				inventoryTransferCompanyByProxy.getTransferFromCo();
		
		companyNameLabel.setValue(company.getCompanyType().toString()+"."+
				company.getCompanyLegalName());
		companyAddress01Label.setValue(company.getAddress01());
		companyAddress02Label.setValue(company.getAddress02());
		companyCityLabel.setValue(company.getCity());
		companyTelephoneLabel.setValue(company.getPhone());
		companyFaxLabel.setValue(company.getFax());
		
		// number
		transferNumberLabel.setValue(
				getInventoryTransfer().getTransferNumber().getSerialComp());
		
		// from - to
		fromWarehouseLabel.setValue(company.getCompanyDisplayName());
		toWarehouseLabel.setValue("SWI");
		// supir - kendaraan
		selectedSupirLabel.setValue(getInventoryTransfer().getSupir());
		selectedKendaraanLabel.setValue(getInventoryTransfer().getNo_polisi());
		
		// inventory transfer material
		InventoryTransfer inventoryTransferMaterialByProxy =
				getInventoryTransferDao().getInventoryTransferMaterialByProxy(getInventoryTransfer().getId());
		transferMaterialGrid.setModel(
				new ListModelList<InventoryTransferMaterial>(
						inventoryTransferMaterialByProxy.getTransferMaterialList()));
		transferMaterialGrid.setRowRenderer(getTransferMaterialGridRowRenderer());
	}


	private void setupEmployeeCombobox() throws Exception {
		// list of employees - supir ONLY
		List<Employee> employeeList =
				getEmployeeDao().findAllEmployeesByEmployeeType(EmployeeType.Supir, true);

		Comboitem comboitem;
		
		for (Employee employee : employeeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(employee.getName());
			comboitem.setValue(employee);
			comboitem.setParent(supirCombobox);
		}		
	}

	public void onSelect$supirCombobox(Event event) throws Exception {
		Employee selEmployee = 
				supirCombobox.getSelectedItem().getValue();
		Employee employeeKendaraanByProxy =
			getEmployeeDao().findEmployeeKenadaraanByProxy(selEmployee.getId());
		
		List<Kendaraan> employeeKendaraanList =
			employeeKendaraanByProxy.getEmployeeKendaraanList();
		kendaraanLabel.setValue(employeeKendaraanList.get(0).getNomorPolisi());
		
		selectedSupirLabel.setValue(selEmployee.getName());
		selectedKendaraanLabel.setValue(kendaraanLabel.getValue());
	}
	
	private RowRenderer<InventoryTransferMaterial> getTransferMaterialGridRowRenderer() {
		
		return new RowRenderer<InventoryTransferMaterial>() {
			
			@Override
			public void render(Row row, InventoryTransferMaterial material, int index) throws Exception {
				// No.
				row.appendChild(new Label(String.valueOf(index+1)+"."));
				
				// Qty(Sht)
				row.appendChild(new Label(getFormatedInteger(material.getSheetQuantity())));
				
				// Qty(Kg)
				row.appendChild(new Label(getFormatedFloatLocal(material.getWeightQuantity())));
				
				// Tipe
				row.appendChild(new Label(material.getInventoryCode().getProductCode()));
				row.setStyle("white-space: nowrap;");

				// Deskripsi
				row.appendChild(new Label(
						getFormatedFloatLocal(material.getThickness())+" x "+
						getFormatedFloatLocal(material.getWidth())+" x "+
						(material.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : 
								getFormatedFloatLocal(material.getLength()))));
				
				
				// Coil No.
				row.appendChild(new Label(material.getMarking()));
			}
		};
	}
	
	public void onClick$updateButton(Event event) throws Exception {
		getInventoryTransfer().setSupir(supirCombobox.getValue());
		getInventoryTransfer().setNo_polisi(kendaraanLabel.getValue());
		
		if (getInventoryTransfer().getInventoryTransferStatus().equals(InventoryTransferStatus.selesai)) {
			// do nothing - no change to status
		} else {		
			getInventoryTransfer().setInventoryTransferStatus(InventoryTransferStatus.proses);
		}
		
		Events.sendEvent(Events.ON_OK, inventoryTransferPrintWin, getInventoryTransfer());		
	}
	
	public void onClick$printButton(Event event) throws Exception {

		PrintUtil.print(printVbox);
		
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

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}
}
