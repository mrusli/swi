package com.pyramix.swi.domain.inventory.transfer;

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
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;

@Entity
@Table(name = "transfer_end_product", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryTransferEndProduct extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8097560780256341089L;

	//  `transfer_product_status` int(11) DEFAULT NULL,
	@Column(name = "transfer_product_status")
	@Enumerated(EnumType.ORDINAL)	
	private InventoryTransferStatus transferProductStatus;
	
	//  `marking` varchar(255) DEFAULT NULL,
	@Column(name = "marking")
	private String marking;
	
	//  `inventory_code_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "inventory_code_id_fk")
	private InventoryCode inventoryCode;
	
	//  `thickness` decimal(19,2) DEFAULT NULL,
	@Column(name = "thickness")
	private BigDecimal thickness;
	
	//  `width` decimal(19,2) DEFAULT NULL,
	@Column(name = "width")
	private BigDecimal width;
	
	//  `length` decimal(19,2) DEFAULT NULL,
	@Column(name = "length")
	private BigDecimal length;
	
	//  `sheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "sheet_quantity")
	private int sheetQuantity;
	
	//  `weight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;
	
	//  `inventory_packing` int(11) DEFAULT NULL,
	@Column(name = "inventory_packing")
	@Enumerated(EnumType.ORDINAL)
	private InventoryPacking inventoryPacking;
	
	//  `inventory_location` int(11) DEFAULT NULL,
	@Column(name = "inventory_location")
	@Enumerated(EnumType.ORDINAL)
	private InventoryLocation inventoryLocation;
	
	//  `contract_no` varchar(255) DEFAULT NULL,
	@Column(name = "contract_no")
	private String contractNumber;
	
	//  `lc_no` varchar(255) DEFAULT NULL,
	@Column(name = "lc_no")
	private String lcNumber;
	
	//  `update_inventory` char(1) DEFAULT NULL,
	@Column(name = "update_inventory")
	@Type(type="true_false")
	private boolean updateInventory;

	//	inventory_id_ref bigint(20)
	@Column(name = "inventory_id_ref")	
	private Long inventoryIdRef;
	
	@Override
	public String toString() {
		
		return "InventoryTransferEndProduct[id="+getId()+
				", transferStatus="+getTransferProductStatus()+
				", marking="+getMarking()+
				", inventoryCode="+getInventoryCode().getProductCode()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", packing="+getInventoryPacking().toString()+
				", location="+getInventoryLocation().toString()+
				"]";
	}
	
	public InventoryTransferStatus getTransferProductStatus() {
		return transferProductStatus;
	}

	public void setTransferProductStatus(InventoryTransferStatus transferProductStatus) {
		this.transferProductStatus = transferProductStatus;
	}

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public BigDecimal getThickness() {
		return thickness;
	}

	public void setThickness(BigDecimal thickness) {
		this.thickness = thickness;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
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

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getLcNumber() {
		return lcNumber;
	}

	public void setLcNumber(String lcNumber) {
		this.lcNumber = lcNumber;
	}

	public boolean isUpdateInventory() {
		return updateInventory;
	}

	public void setUpdateInventory(boolean updateInventory) {
		this.updateInventory = updateInventory;
	}

	public Long getInventoryIdRef() {
		return inventoryIdRef;
	}

	public void setInventoryIdRef(Long inventoryIdRef) {
		this.inventoryIdRef = inventoryIdRef;
	}
}
