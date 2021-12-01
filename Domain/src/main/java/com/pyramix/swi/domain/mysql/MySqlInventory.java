package com.pyramix.swi.domain.mysql;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "inventory_current", schema=SchemaUtil.SCHEMA_MYSQL)
public class MySqlInventory extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1986310450802079791L;

	@Column(name = "inventory_packing_id_fk")
	private Long inventoryPacking;
	
	// inventory_location_id_fk
	@Column(name = "inventory_location_id_fk")
	private Long inventoryLocation;
	
	// inventory_code_id_fk
	@Column(name = "inventory_code_id_fk")
	private Long inventoryCode;
	
	// sku
	@Column(name = "sku")
	private String SKU;
	
	// inventory_marking
	@Column(name = "inventory_marking")
	private String inventoryMarking;
	
	// quantity
	@Column(name = "quantity")
	private BigDecimal qtyInventory;
	
	// sheet_quantity
	@Column(name = "sheet_quantity")
	private int sheetQuantity;

	// thickness
	@Column(name = "thickness")
	private BigDecimal thickness;
	
	// width
	@Column(name = "width")
	private BigDecimal width;

	// length
	@Column(name = "length")
	private BigDecimal length;

	// inventory_status
	@Column(name = "inventory_status")
	private int inventoryStatus;

	// temp_note
	@Column(name = "temp_note")
	private String note;
	
	// temp_cnrtnum
	@Column(name = "temp_cnrtnum")
	private String contractNum;

	// temp_datein
	@Column(name = "temp_datein")
	private Date receivedDate;
	
	// temp_lcnum
	@Column(name = "temp_lcnum")
	private String lcNum;
	
	public Long getInventoryPacking() {
		return inventoryPacking;
	}

	public void setInventoryPacking(Long inventoryPacking) {
		this.inventoryPacking = inventoryPacking;
	}

	public Long getInventoryLocation() {
		return inventoryLocation;
	}

	public void setInventoryLocation(Long inventoryLocation) {
		this.inventoryLocation = inventoryLocation;
	}

	public Long getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(Long inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public String getSKU() {
		return SKU;
	}

	public void setSKU(String sKU) {
		SKU = sKU;
	}

	public String getInventoryMarking() {
		return inventoryMarking;
	}

	public void setInventoryMarking(String inventoryMarking) {
		this.inventoryMarking = inventoryMarking;
	}

	public BigDecimal getQtyInventory() {
		return qtyInventory;
	}

	public void setQtyInventory(BigDecimal qtyInventory) {
		this.qtyInventory = qtyInventory;
	}

	public int getSheetQuantity() {
		return sheetQuantity;
	}

	public void setSheetQuantity(int sheetQuantity) {
		this.sheetQuantity = sheetQuantity;
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

	public int getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(int inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getContractNum() {
		return contractNum;
	}

	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getLcNum() {
		return lcNum;
	}

	public void setLcNum(String lcNum) {
		this.lcNum = lcNum;
	}
	
	
	
}
