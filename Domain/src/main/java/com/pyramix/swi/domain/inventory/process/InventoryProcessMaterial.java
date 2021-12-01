package com.pyramix.swi.domain.inventory.process;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;

@Entity
@Table(name = "process_material", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryProcessMaterial extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502585448416247402L;

	//  `marking` varchar(255) DEFAULT NULL,
	@Column(name = "marking")
	private String marking;
	
	//  `thickness` decimal(19,2) DEFAULT NULL,
	@Column(name = "thickness")
	private BigDecimal thickness;
	
	//  `width` decimal(19,2) DEFAULT NULL,
	@Column(name = "width")
	private BigDecimal width;

	//  `length` decimal(19,2) DEFAULT NULL,
	@Column(name = "length")
	private BigDecimal length;

	//  `inventory_packing` int(11) DEFAULT NULL,
	@Column(name = "inventory_packing")
	@Enumerated(EnumType.ORDINAL)
	private InventoryPacking inventoryPacking;

	//  `weight_quantity` decimal(19,2) DEFAULT NULL,
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;

	//  `sheet_quantity` int(11) DEFAULT NULL,
	@Column(name = "sheet_quantity")
	private int sheetQuantity;

	//  `inventory_location` int(11) DEFAULT NULL,
	@Column(name = "inventory_location")
	@Enumerated(EnumType.ORDINAL)
	private InventoryLocation inventoryLocation;

	//  `inventory_code_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `fk_inventory_code_id_04` (`inventory_code_id_fk`),
	//  CONSTRAINT `fk_inventory_code_04` FOREIGN KEY (`inventory_code_id_fk`) REFERENCES `inventory_code` (`id`)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "inventory_code_id_fk")
	private InventoryCode inventoryCode;

	//  `inventory_id_ref` bigint(20) DEFAULT NULL,
	@Column(name = "inventory_id_ref")
	private Long inventoryIdRef;
	
	//  process_join_process_material
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "process_join_process_material",
			joinColumns = @JoinColumn(name = "id_process_material"),
			inverseJoinColumns = @JoinColumn(name = "id_process"))
	private InventoryProcess inventoryProcess;
	
	//	process_material_join_process_products
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "process_material_join_process_products",
			joinColumns = @JoinColumn(name = "id_process_material"),
			inverseJoinColumns = @JoinColumn(name = "id_process_product"))
	private List<InventoryProcessProduct> processProducts;
	
	// contract_no varchar(255)
	@Column(name = "contract_no")
	private String contractNumber;
	
	// lc_no varchar(255)
	@Column(name = "lc_no")
	private String lcNumber;
	
	@Override
	public String toString() {
		
		return "InventoryProcessMaterial[id="+getId()+
				", marking="+getMarking()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", inventoryPacking="+getInventoryPacking()+
				", weightQuantity="+getWeightQuantity()+
				", sheetQuantity="+getSheetQuantity()+
				", inventoryLocation="+getInventoryLocation();
	}
	
	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
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

	public InventoryPacking getInventoryPacking() {
		return inventoryPacking;
	}

	public void setInventoryPacking(InventoryPacking inventoryPacking) {
		this.inventoryPacking = inventoryPacking;
	}

	public BigDecimal getWeightQuantity() {
		return weightQuantity;
	}

	public void setWeightQuantity(BigDecimal weightQuantity) {
		this.weightQuantity = weightQuantity;
	}

	public int getSheetQuantity() {
		return sheetQuantity;
	}

	public void setSheetQuantity(int sheetQuantity) {
		this.sheetQuantity = sheetQuantity;
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

	public Long getInventoryIdRef() {
		return inventoryIdRef;
	}

	public void setInventoryIdRef(Long inventoryIdRef) {
		this.inventoryIdRef = inventoryIdRef;
	}

	public List<InventoryProcessProduct> getProcessProducts() {
		return processProducts;
	}

	public void setProcessProducts(List<InventoryProcessProduct> processProducts) {
		this.processProducts = processProducts;
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

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}

}
