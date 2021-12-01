package com.pyramix.swi.domain.customerorder;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;

@Entity
@Table(name = "customer_order_product", schema = SchemaUtil.SCHEMA_COMMON)
public class CustomerOrderProduct extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 335368365686202058L;

	//  `product_type` int(11) DEFAULT NULL,
	@Column(name = "product_type")
	@Enumerated(EnumType.ORDINAL)
	private ProductType productType;
	
	/*
	 * NOTE: MOST OF THE VALUES IN THIS OBJECT ARE USER MODIFIED
	 * - inventoryCode is required for saving the user modified value 
	 * - thickness, width, length are required for weight / sheet calculation
	 * - description values may not be the same as the thickness, width, length
	 * 
	 * Calculation:
	 * - user MUST MANUALLY calculate the weight / sheet based on the modified description
	 */

	//  `inventory_marking` varchar(255) DEFAULT NULL,
	@Column(name = "inventory_marking")
	private String marking;
	
	//  `inventory_code` varchar(255) DEFAULT NULL,
	@Column(name = "inventory_code")
	private String userModInventoryCode;
	
	//  `thickness` decimal(19,2) DEFAULT NULL,
	@Column(name = "thickness")
	private BigDecimal thickness;
	
	//  `width` decimal(19,2) DEFAULT NULL,
	@Column(name = "width")
	private BigDecimal width;
	
	//  `length` decimal(19,2) DEFAULT NULL,
	@Column(name = "length")
	private BigDecimal length;
	
	//  `description` varchar(255) DEFAULT NULL,
	@Column(name = "description")
	private String description;
	
	//  `by_kg` char(1) DEFAULT NULL,
	@Column(name = "by_kg")
	@Type(type = "true_false")
	private boolean byKg;
	
	//  `sheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "sheet_quantity")
	private int sheetQuantity;
		
	//  `weight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;

	//  `selling_price` decimal(19,2) DEFAULT NULL,
	@Column(name = "selling_price")
	private BigDecimal sellingPrice;
	
	//  `selling_subtotal` decimal(19,2) DEFAULT NULL,
	@Column(name = "selling_subtotal")
	private BigDecimal sellingSubTotal;
	
	//  `diskon` char(1) DEFAULT NULL,
	@Column(name = "diskon")
	@Type(type = "true_false")
	private boolean discount;
	
	//  `diskon_percent` decimal(19,2) DEFAULT NULL,
	@Column(name = "diskon_percent")
	private BigDecimal discountPercent;
	
	//  `ppn` char(1) DEFAULT NULL,
	@Column(name = "ppn")
	@Type(type = "true_false")
	private boolean ppn;
	
	//  `ppn_percent` decimal(19,2) DEFAULT NULL,
	@Column(name = "ppn_percent")
	private BigDecimal ppnPercent;
	
	//  `inventory_packing` int(11) DEFAULT NULL,
	@Column(name = "inventory_packing")
	@Enumerated(EnumType.ORDINAL)
	private InventoryPacking inventoryPacking;
 
	//  `inventory_location` int(11) DEFAULT NULL,
	@Column(name = "inventory_location")
	@Enumerated(EnumType.ORDINAL)
	private InventoryLocation inventoryLocation;
	
	//  `inventory_code_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_code_id_fk")
	private InventoryCode inventoryCode;
	
	// customer_order_product_join_inventory
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "customer_order_product_join_inventory",
	 		joinColumns = @JoinColumn(name = "id_customer_order_product"),
			inverseJoinColumns = @JoinColumn(name = "id_inventory"))
	private Inventory inventory;

	@Override
	public String toString() {
		return "CustomerOrderProduct[id="+getId()+
				", productType="+getProductType().toString()+
				", marking="+getMarking()+
				", userModInventoryCode="+getUserModInventoryCode()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", description="+getDescription()+
				", byKg="+isByKg()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", sellingPrice="+getSellingPrice()+
				", sellingSubTotal="+getSellingSubTotal()+
				", discount="+isDiscount()+
				", discountPercent="+getDiscountPercent()+
				", ppn="+isPpn()+
				", ppnPercent="+getPpnPercent()+
				", inventoryPacking="+getInventoryPacking().toString()+
				", inventoryLocation="+getInventoryLocation().toString()+
				"]";
	}
	
	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isByKg() {
		return byKg;
	}

	public void setByKg(boolean byKg) {
		this.byKg = byKg;
	}

	public int getSheetQuantity() {
		return sheetQuantity;
	}

	public void setSheetQuantity(int sheetQuantity) {
		this.sheetQuantity = sheetQuantity;
	}

	public BigDecimal getWeightQuantity() {
		return weightQuantity;
	}

	public void setWeightQuantity(BigDecimal weightQuantity) {
		this.weightQuantity = weightQuantity;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public InventoryPacking getInventoryPacking() {
		return inventoryPacking;
	}

	public void setInventoryPacking(InventoryPacking inventoryPacking) {
		this.inventoryPacking = inventoryPacking;
	}

	public InventoryLocation getInventoryLocation() {
		return inventoryLocation;
	}

	public void setInventoryLocation(InventoryLocation inventoryLocation) {
		this.inventoryLocation = inventoryLocation;
	}

	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public boolean isDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}

	public boolean isPpn() {
		return ppn;
	}

	public void setPpn(boolean ppn) {
		this.ppn = ppn;
	}

	public BigDecimal getPpnPercent() {
		return ppnPercent;
	}

	public void setPpnPercent(BigDecimal ppnPercent) {
		this.ppnPercent = ppnPercent;
	}

	/**
	 * @return the thickness
	 */
	public BigDecimal getThickness() {
		return thickness;
	}

	/**
	 * @param thickness the thickness to set
	 */
	public void setThickness(BigDecimal thickness) {
		this.thickness = thickness;
	}

	/**
	 * @return the width
	 */
	public BigDecimal getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	/**
	 * @return the length
	 */
	public BigDecimal getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(BigDecimal length) {
		this.length = length;
	}

	/**
	 * @return the userModInventoryCode
	 */
	public String getUserModInventoryCode() {
		return userModInventoryCode;
	}

	/**
	 * @param userModInventoryCode the userModInventoryCode to set
	 */
	public void setUserModInventoryCode(String userModInventoryCode) {
		this.userModInventoryCode = userModInventoryCode;
	}

	/**
	 * @return the sellingSubTotal
	 */
	public BigDecimal getSellingSubTotal() {
		return sellingSubTotal;
	}

	/**
	 * @param sellingSubTotal the sellingSubTotal to set
	 */
	public void setSellingSubTotal(BigDecimal sellingSubTotal) {
		this.sellingSubTotal = sellingSubTotal;
	}

 	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
 
}
