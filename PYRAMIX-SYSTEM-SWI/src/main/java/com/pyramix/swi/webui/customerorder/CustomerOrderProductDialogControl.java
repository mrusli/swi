package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerOrderProductDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5325036471728394391L;

	private CustomerOrderProductDao customerOrderProductDao;
	private InventoryDao inventoryDao;
	
	private Window customerOrderProductDialogWin;
	private Decimalbox hargaDecimalbox, subTotalDecimalbox, quantityKgDecimalbox;
	private Textbox coilNoTextbox, kodeTextbox, spesifikasiTextbox, packingTextbox;
	private Intbox quantityShtLineIntbox;
	private Checkbox unitCheckbox;
	
	private CustomerOrderProduct customerOrderProduct;
	
	private final BigDecimal DIVISOR = new BigDecimal(1000000);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrderProduct(
				(CustomerOrderProduct) arg.get("customerOrderProduct"));
	}

	public void onCreate$customerOrderProductDialogWin(Event event) throws Exception {
		hargaDecimalbox.setLocale(getLocale());
		subTotalDecimalbox.setLocale(getLocale());
		quantityKgDecimalbox.setLocale(getLocale());
		/*
		 * NEED TO SET FORMAT to decimalFormat - with the decimalFormat, you will then
		 * be able to obtain the correct subtotal value during edit and display
		 * 
		 * NOTE: 28/02/2018: Dua Angka di belakang koma utk harga di sini (HARGA) 
		 * seharusnya di pakai pak. ( rp. 152.727.27) 
		 */
		// hargaDecimalbox.setFormat(getDecimalFormatLocal());
		// subTotalDecimalbox.setFormat(getDecimalFormatLocal());
		
		customerOrderProductDialogWin.setTitle("Merubah (Edit)");
		
		// display info
		displayCustomerOrderProductInfo();
	}
	
	private void displayCustomerOrderProductInfo() throws Exception {
		CustomerOrderProduct product = getCustomerOrderProduct();
		
		coilNoTextbox.setValue(product.getMarking());
		if (product.getId()==Long.MIN_VALUE) {
			kodeTextbox.setValue(product.getInventoryCode().getProductCode());
		} else {
			InventoryCode inventoryCodeByProxy = getInventoryCodeByProxy(product.getId());
			kodeTextbox.setValue(inventoryCodeByProxy.getProductCode());
		}
		spesifikasiTextbox.setValue(product.getDescription());
		packingTextbox.setValue(product.getInventoryPacking().toString());
				
		// qty-kg
		quantityKgDecimalbox.setValue(product.getWeightQuantity());
		// qty-sht
		quantityShtLineIntbox.setValue(product.getSheetQuantity());
		// kg or sht ?
		unitCheckbox.setChecked(product.isByKg());
		unitCheckbox.setLabel(product.isByKg() ? "Kg" : "Sht");
		// pricing
		hargaDecimalbox.setValue(product.getSellingPrice());
		BigDecimal subTotal = product.isByKg() ? product.getWeightQuantity().multiply(product.getSellingPrice()) :
			product.getSellingPrice().multiply(new BigDecimal(product.getSheetQuantity()));				
		subTotalDecimalbox.setValue(subTotal);
	}

	public void onClick$unitCheckbox(Event event) throws Exception {
		unitCheckbox.setLabel(unitCheckbox.isChecked() ? "Kg" : "Sht");
	}
	
	public void onClick$toWeightButton(Event event) throws Exception {		
		// to calc qty(kg) from the qth(sht)
		
		// NOTE: THE FIRST TIME : the inventoryCode MUST reference Inventory class.  Why?  The CustomerOrderProduct is
		// not created yet:
		// 		System.out.println("customerOrderProductId="+(getCustomerOrderProduct().getId()==Long.MIN_VALUE));
		// 		==> return true

		// WRONG: InventoryType inventoryType = getCustomerOrderProduct().getInventoryCode().getInventoryType();
		BigDecimal density;
		
		if (getCustomerOrderProduct().getId()==Long.MIN_VALUE) {
			long inventoryId = getCustomerOrderProduct().getInventory().getId();
			Inventory inventory = getInventoryDao().findInventoryById(inventoryId);
			// density
			density = inventory.getInventoryCode().getInventoryType().getDensity();
		} else {
			InventoryCode inventoryCodeByProxy = getInventoryCodeByProxy(getCustomerOrderProduct().getId());
			// density
			density = inventoryCodeByProxy.getInventoryType().getDensity();
		}
		
		BigDecimal thickness = getCustomerOrderProduct().getThickness();
		BigDecimal width = getCustomerOrderProduct().getWidth();
		BigDecimal length = getCustomerOrderProduct().getLength();
		
		BigDecimal kgPerSheet = thickness.multiply(width).multiply(length).multiply(density)
				.divide(DIVISOR, RoundingMode.HALF_UP);

		int shtQty = quantityShtLineIntbox.getValue();
		quantityKgDecimalbox.setValue(kgPerSheet.multiply(new BigDecimal(shtQty)).setScale(2, RoundingMode.HALF_UP));
	}
	
	public void onClick$toSheetButton(Event event) throws Exception {
		// to calc qty(sht) from qty(kg)
		
		// NOTE: THE FIRST TIME : the inventoryCode MUST reference Inventory class.  Why?  The CustomerOrderProduct is
		// not created yet:
		// 		System.out.println("customerOrderProductId="+(getCustomerOrderProduct().getId()==Long.MIN_VALUE));
		// 		==> return true
		
		BigDecimal density;
		
		if (getCustomerOrderProduct().getId()==Long.MIN_VALUE) {
			long inventoryId = getCustomerOrderProduct().getInventory().getId();
			Inventory inventory = getInventoryDao().findInventoryById(inventoryId);
			// density
			density = inventory.getInventoryCode().getInventoryType().getDensity();
		} else {
			InventoryCode inventoryCodeByProxy = getInventoryCodeByProxy(getCustomerOrderProduct().getId());
			// density
			density = inventoryCodeByProxy.getInventoryType().getDensity();
		}
		
		BigDecimal thickness = getCustomerOrderProduct().getThickness();
		BigDecimal width = getCustomerOrderProduct().getWidth();
		BigDecimal length = getCustomerOrderProduct().getLength();

		BigDecimal kgRefSheet = BigDecimal.ONE.multiply(width).multiply(length).multiply(density)
				.divide(DIVISOR, RoundingMode.HALF_UP);
		BigDecimal qtyKg = quantityKgDecimalbox.getValue();
		
		quantityShtLineIntbox.setValue(qtyKg.divide(kgRefSheet, RoundingMode.HALF_UP)
				.divide(thickness, RoundingMode.HALF_UP).intValue());
	}
	
	public void onClick$calculateHargaButton(Event event) throws Exception {
		subTotalDecimalbox.setValue(unitCheckbox.isChecked() ?
				// by kg
				quantityKgDecimalbox.getValue().multiply(hargaDecimalbox.getValue()) :
				// by sht
				hargaDecimalbox.getValue().multiply(new BigDecimal(quantityShtLineIntbox.getValue())));
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		CustomerOrderProductData userModProduct = new CustomerOrderProductData();
		
		userModProduct.setCoilNo(coilNoTextbox.getValue());
		userModProduct.setKode(kodeTextbox.getValue());
		userModProduct.setSpesifikasi(spesifikasiTextbox.getValue());
		userModProduct.setQtyKg(quantityKgDecimalbox.getValue());
		userModProduct.setQtySht(quantityShtLineIntbox.getValue());
		userModProduct.setByKg(unitCheckbox.isChecked());
		userModProduct.setHarga(hargaDecimalbox.getValue());
		userModProduct.setSubTotal(subTotalDecimalbox.getValue());

		Events.sendEvent(Events.ON_CHANGE, customerOrderProductDialogWin, userModProduct);
		
		customerOrderProductDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		customerOrderProductDialogWin.detach();
	}

	private InventoryCode getInventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct customerOrderProduct = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return customerOrderProduct.getInventoryCode();
	}	
	
	/**
	 * @return the customerOrderProduct
	 */
	public CustomerOrderProduct getCustomerOrderProduct() {
		return customerOrderProduct;
	}

	/**
	 * @param customerOrderProduct the customerOrderProduct to set
	 */
	public void setCustomerOrderProduct(CustomerOrderProduct customerOrderProduct) {
		this.customerOrderProduct = customerOrderProduct;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
}
