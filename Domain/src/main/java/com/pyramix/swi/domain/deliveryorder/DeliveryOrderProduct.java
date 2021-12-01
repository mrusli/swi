package com.pyramix.swi.domain.deliveryorder;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.InventoryCode;

@Entity
@Table(name = "delivery_order_product", schema = SchemaUtil.SCHEMA_COMMON)
public class DeliveryOrderProduct extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7415238417553333251L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_code_id_fk")
	private InventoryCode inventoryCode;

	//  `pinventory_code` varchar(255) DEFAULT NULL,
	@Column(name = "pinventory_code")
	private String p_inventoryCode;
	
	@Column(name = "inventory_marking")
	private String marking;

	//  `pinventory_marking` varchar(255) DEFAULT NULL,
	@Column(name = "pinventory_marking")
	private String p_marking;
	
	@Column(name = "sheet_quantity")
	private int sheetQuantity;

	//  `psheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "psheet_quantity")
	private int p_sheetQuantity;
	
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;

	//  `pweight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "pweight_quantity")
	private BigDecimal p_weightQuantity;
	
	@Column(name = "description")
	private String description;

	//  `pdescription` varchar(255) DEFAULT NULL,
	@Column(name = "pdescription")
	private String p_description;

	@Override
	public String toString() {
		
		return "DeliveryOrderProduct[id="+getId()+
				", marking="+getMarking()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", description="+getDescription()+
				"]";
	}
	
	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getP_inventoryCode() {
		return p_inventoryCode;
	}

	public void setP_inventoryCode(String p_inventoryCode) {
		this.p_inventoryCode = p_inventoryCode;
	}

	public String getP_marking() {
		return p_marking;
	}

	public void setP_marking(String p_marking) {
		this.p_marking = p_marking;
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

	public String getP_description() {
		return p_description;
	}

	public void setP_description(String p_description) {
		this.p_description = p_description;
	}	
}
