package com.pyramix.swi.webui.inventory.process.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.persistence.inventory.process.dao.InventoryProcessDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageHandler;

public class CuttingOrderPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8894812801067246293L;

	private InventoryProcessDao inventoryProcessDao;
	
	private Iframe iframe;
	
	private CuttingOrderData cuttingOrderData;
	private PageHandler handler= new PageHandler();
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCuttingOrderData(
				(CuttingOrderData) arg.get("cuttingOrderData"));
	}
	
	public void onCreate$cuttingOrderPrintWin(Event event) throws Exception {
		// InventoryProcess to CuttingOrder
		CuttingOrder cuttingOrder = getCuttingOrder(getCuttingOrderData().getInventoryProcess(), new CuttingOrder());
		
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		dataField.put("cuttingOrderCompany", cuttingOrder.getCuttingOrderCompany());
		dataField.put("cuttingOrderAddress01", cuttingOrder.getCuttingOrderCompanyAddress01());
		dataField.put("cuttingOrderAddress02", cuttingOrder.getCuttingOrderCompanyAddress02());
		dataField.put("cuttingOrderCity", cuttingOrder.getCuttingOrderCity()+" "+cuttingOrder.getCuttingOrderPostalCode());
		dataField.put("cuttingOrderPostalCode", cuttingOrder.getCuttingOrderPostalCode());
		dataField.put("cuttingOrderTelephone", cuttingOrder.getCuttingOrderCompanyTelephone()+" Fax:"+cuttingOrder.getCuttingOrderCompanyFax());
		dataField.put("cuttingOrderEmail", cuttingOrder.getCuttingOrderCompanyEmail());
		dataField.put("printedDate", dateToStringDisplay(getLocalDate(), getShortDateFormat()));
		dataField.put("cuttingOrderDate", cuttingOrder.getCuttingOrderDate());
		dataField.put("cuttingOrderNumber", cuttingOrder.getCuttingOrderNumber());
		dataField.put("cuttingOrderNote", cuttingOrder.getCuttingOrderNote());
		
		// InventoryProcessMaterial to CuttingOrderMaterial
		InventoryProcess inventoryProcessByProxy =
				getInventoryProcessDao().getProcessMaterialsByProxy(
						getCuttingOrderData().getInventoryProcess().getId());		
		List<InventoryProcessMaterial> processMaterialList = 
				inventoryProcessByProxy.getProcessMaterials();		
		List<CuttingOrderMaterial> cuttingOrderMaterialList = getCuttingOrderMaterial(processMaterialList, 
				new ArrayList<CuttingOrderMaterial>());
		HashMap<String, Object> dataList = new HashMap<String, Object>();
		dataList.put("cuttingOrderMaterial", cuttingOrderMaterialList);
		
		iframe.setContent(handler.generateReportAMedia(dataField, dataList, 
				"/inventory/process/print/CuttingOrderPrint.jasper", 
				"CuttingOrder-"+
				dateToStringDisplay(getLocalDate(), getEmphYearMonthShort())));

	}

	private CuttingOrder getCuttingOrder(InventoryProcess inventoryProcess, CuttingOrder cuttingOrder) throws Exception {
		InventoryProcess inventoryProcessByProxy = 
				getInventoryProcessDao().getProcessedByCoByProxy(inventoryProcess.getId());
		cuttingOrder.setCuttingOrderCompany(inventoryProcessByProxy.getProcessedByCo().getCompanyType()+". "+
				inventoryProcessByProxy.getProcessedByCo().getCompanyLegalName()+" ("+
				inventoryProcessByProxy.getProcessedByCo().getCompanyDisplayName()+")");
		cuttingOrder.setCuttingOrderCompanyAddress01(inventoryProcessByProxy.getProcessedByCo().getAddress01());
		cuttingOrder.setCuttingOrderCompanyAddress02(inventoryProcessByProxy.getProcessedByCo().getAddress02());
		cuttingOrder.setCuttingOrderCity(inventoryProcessByProxy.getProcessedByCo().getCity());
		cuttingOrder.setCuttingOrderPostalCode(inventoryProcessByProxy.getProcessedByCo().getPostalCode());
		cuttingOrder.setCuttingOrderCompanyTelephone(inventoryProcessByProxy.getProcessedByCo().getPhone());
		cuttingOrder.setCuttingOrderCompanyFax(inventoryProcessByProxy.getProcessedByCo().getFax());
		cuttingOrder.setCuttingOrderCompanyEmail(inventoryProcessByProxy.getProcessedByCo().getEmail());
		cuttingOrder.setCuttingOrderDate(dateToStringDisplay(asLocalDate(
				inventoryProcessByProxy.getOrderDate()), getLongDateFormat()));
		cuttingOrder.setCuttingOrderNumber(inventoryProcessByProxy.getProcessNumber().getSerialComp());
		cuttingOrder.setCuttingOrderNote(inventoryProcessByProxy.getNote());
		
		return cuttingOrder;
	}

	private List<CuttingOrderMaterial> getCuttingOrderMaterial(List<InventoryProcessMaterial> processMaterialList, 
			ArrayList<CuttingOrderMaterial> cuttingOrderMaterialList) throws Exception {
		for (InventoryProcessMaterial processMaterial : processMaterialList) {
			CuttingOrderMaterial material = new CuttingOrderMaterial();
			material.setInventoryCodeName(processMaterial.getInventoryCode().getProductCode());
			material.setInventoryMarking(processMaterial.getMarking());
			material.setInventoryQtyKg(getFormatedFloatLocal(processMaterial.getWeightQuantity()));
			material.setInventorySpec(getFormatedFloatLocal(processMaterial.getThickness())+" x "+
					getFormatedFloatLocal(processMaterial.getWidth())+" x "+
					"Coil");
			List<CuttingOrderProduct> cuttingOrderProductList = new ArrayList<CuttingOrderProduct>();
			
			InventoryProcessMaterial inventoryProcessMaterialByProxy =
					getInventoryProcessDao().getProcessProductsByProxy(processMaterial.getId());
			
			for (InventoryProcessProduct processProduct : inventoryProcessMaterialByProxy.getProcessProducts()) {
				CuttingOrderProduct product = new CuttingOrderProduct();
				product.setSpecification(getFormatedFloatLocal(processProduct.getThickness())+" x "+
						getFormatedFloatLocal(processProduct.getWidth())+" x "+
						(processProduct.getLength().compareTo(BigDecimal.ZERO)==0 ? "Coil" :
						getFormatedFloatLocal(processProduct.getLength())));
				product.setQuantityShtLine(getFormatedInteger(processProduct.getSheetQuantity()));
				product.setQtuantityKg(getFormatedFloatLocal(processProduct.getWeightQuantity()));
				InventoryProcessProduct processProductProxy = 
						getInventoryProcessDao().getProcessProductsCustomerByProxy(processProduct.getId());
				product.setCustomerName(processProductProxy.getCustomer()==null ? "SWI" : 
					processProductProxy.getCustomer().getCompanyType()+". "+
						processProductProxy.getCustomer().getCompanyLegalName());
				
				cuttingOrderProductList.add(product);
			}
			material.setCuttingOrderProducts(cuttingOrderProductList);
			
			cuttingOrderMaterialList.add(material);
		}
		
		return cuttingOrderMaterialList;
	}

	public CuttingOrderData getCuttingOrderData() {
		return cuttingOrderData;
	}

	public void setCuttingOrderData(CuttingOrderData cuttingOrderData) {
		this.cuttingOrderData = cuttingOrderData;
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}
	
}
