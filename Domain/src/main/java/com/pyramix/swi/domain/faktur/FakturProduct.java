package com.pyramix.swi.domain.faktur;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.ProductType;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;

@Entity
@Table(name = "faktur_product", schema = SchemaUtil.SCHEMA_COMMON)
public class FakturProduct extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -639171301872134425L;

	//  `product_type` int(11) DEFAULT NULL,
	@Column(name = "product_type")
	@Enumerated(EnumType.ORDINAL)
	private ProductType productType;
	
	//  `inventory_marking` varchar(255) DEFAULT NULL,
	@Column(name = "inventory_marking")
	private String inventoryMarking;
	
	//  `pinventory_marking` varchar(255) DEFAULT NULL,
	@Column(name = "pinventory_marking")
	private String p_inventoryMarking;
	
	//  `description` varchar(255) DEFAULT NULL,
	@Column(name = "description")
	private String description;

	//  `pdescription` varchar(255) DEFAULT NULL,
	@Column(name = "pdescription")
	private String p_description;
	
	//  `by_kg` char(1) DEFAULT NULL,
	@Column(name = "by_kg")
	@Type(type = "true_false")
	private boolean byKg;
	
	//  `sheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "sheet_quantity")
	private int sheetQuantity;

	//  `psheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "psheet_quantity")
	private int p_sheetQuantity;
	
	//  `weight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;

	//  `pweight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "pweight_quantity")
	private BigDecimal p_weightQuantity;
	
	//  `unit_price` decimal(19,2) DEFAULT NULL,
	@Column(name = "unit_price")
	private BigDecimal unitPrice;

	//  `punit_price` decimal(19,2) DEFAULT NULL,
	@Column(name = "punit_price")
	private BigDecimal p_unitPrice;
	
	//  `subtotal` decimal(19,2) DEFAULT NULL,
	@Column(name = "subtotal")
	private BigDecimal subTotal;
	
	//  `psubtotal` decimal(19,2) DEFAULT NULL,
	@Column(name = "psubtotal")
	private BigDecimal p_subTotal;
	
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
	private boolean usePpn;
	
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
	//  KEY `fk_inventory_code_id_09` (`inventory_code_id_fk`),
	//  CONSTRAINT `fk_inventory_code_09` FOREIGN KEY (`inventory_code_id_fk`) REFERENCES `inventory_code` (`id`)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "inventory_code_id_fk")
	private InventoryCode inventoryCode;

	@Override
	public String toString() {
		
		return "FakturProduct[id="+getId()+
				", productType="+getProductType().toString()+
				", inventoryMarking="+getInventoryMarking()+
				", description="+getDescription()+
				", byKg="+isByKg()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", unitPrice="+getUnitPrice()+
				", subTotal="+getSubTotal()+
				", discount="+isDiscount()+
				", discountPercent="+getDiscountPercent()+
				", inventoryPacking="+getInventoryPacking().toString()+
				", inventoryLocation="+getInventoryLocation().toString()+
				", inventoryCode="+getInventoryCode().getProductCode()+
				"]";
	}
	
	//  `pinventory_code` varchar(255) DEFAULT NULL,
	@Column(name = "pinventory_code")
	private String p_inventoryCode;

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public String getInventoryMarking() {
		return inventoryMarking;
	}

	public void setInventoryMarking(String inventoryMarking) {
		this.inventoryMarking = inventoryMarking;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
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

	public boolean isUsePpn() {
		return usePpn;
	}

	public void setUsePpn(boolean usePpn) {
		this.usePpn = usePpn;
	}

	public BigDecimal getPpnPercent() {
		return ppnPercent;
	}

	public void setPpnPercent(BigDecimal ppnPercent) {
		this.ppnPercent = ppnPercent;
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

	public String getP_inventoryMarking() {
		return p_inventoryMarking;
	}

	public void setP_inventoryMarking(String p_inventoryMarking) {
		this.p_inventoryMarking = p_inventoryMarking;
	}

	public String getP_description() {
		return p_description;
	}

	public void setP_description(String p_description) {
		this.p_description = p_description;
	}

	public int getP_sheetQuantity() {
		return p_sheetQuantity;
	}

	public void setP_sheetQuantity(int p_sheetQuantity) {
		this.p_sheetQuantity = p_sheetQuantity;
	}

	public BigDecimal getP_weightQuantity() {
		return p_weightQuantity;
	}

	public void setP_weightQuantity(BigDecimal p_weightQuantity) {
		this.p_weightQuantity = p_weightQuantity;
	}

	public BigDecimal getP_unitPrice() {
		return p_unitPrice;
	}

	public void setP_unitPrice(BigDecimal p_unitPrice) {
		this.p_unitPrice = p_unitPrice;
	}

	public String getP_inventoryCode() {
		return p_inventoryCode;
	}

	public void setP_inventoryCode(String p_inventoryCode) {
		this.p_inventoryCode = p_inventoryCode;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getP_subTotal() {
		return p_subTotal;
	}

	public void setP_subTotal(BigDecimal p_subTotal) {
		this.p_subTotal = p_subTotal;
	}
	
}
