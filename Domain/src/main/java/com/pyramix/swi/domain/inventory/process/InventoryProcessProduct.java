package com.pyramix.swi.domain.inventory.process;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.organization.Customer;

@Entity
@Table(name = "process_products", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryProcessProduct extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7350025503086579402L;

	//  `process_type` int(11) DEFAULT NULL,
	@Column(name = "process_type")
	@Enumerated(EnumType.ORDINAL)
	private InventoryProcessType inventoryProcessType;
	
	// process_product_status
	@Column(name = "process_product_status")
	@Enumerated(EnumType.ORDINAL)
	private InventoryProcessStatus productProcessStatus;
	
	//  `marking` varchar(255) DEFAULT NULL,
	@Column(name = "marking")
	private String marking;
	
	//  `inventory_code_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `fk_inventory_code_id_05` (`inventory_code_id_fk`),
	//  CONSTRAINT `fk_inventory_code_05` FOREIGN KEY (`inventory_code_id_fk`) REFERENCES `inventory_code` (`id`)
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
	
	//  `recoil` char(1)
	@Column(name = "recoil")
	private boolean recoil;
	
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

	//  `print` char(1) DEFAULT NULL,
	@Column(name = "print")
	@Type(type="true_false")
	private boolean printed;
		
	//  `customer_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `fk_customer_id_01` (`customer_id_fk`),
	//  CONSTRAINT `fk_customer_01` FOREIGN KEY (`customer_id_fk`) REFERENCES `customer` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;
	
	//	process_material_join_process_products
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "process_material_join_process_products",
			joinColumns = @JoinColumn(name = "id_process_product"),
			inverseJoinColumns = @JoinColumn(name = "id_process_material"))
	private InventoryProcessMaterial processMaterial;
	
	//	`process_material_id_ref` bigint(20) DEFAULT NULL,
	@Column(name = "process_material_id_ref")
	private Long materialIdRef;
	
	// contract_no varchar(255)
	@Column(name = "contract_no")
	private String contractNumber;
	
	// lc_no varchar(255)
	@Column(name = "lc_no")
	private String lcNumber;
	
	// update_to_inventory char(1)
	@Column(name = "update_to_inventory")
	@Type(type="true_false")
	private boolean updateToInventory;
	
	// revision int(11)
	@Column(name = "revision")
	private int revision;
	
	@Override
	public String toString() {
		
		return "InventoryProcessProduct[id="+getId()+
				", processType="+getInventoryProcessType().toString()+
				", marking="+getMarking()+
				", inventoryCode="+getInventoryCode().getProductCode()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", inventoryPacking="+getInventoryPacking().toString()+
				", inventoryLocation="+getInventoryLocation().toString()+
				", printed="+isPrinted()+
				", materialIdRef="+getMaterialIdRef()+
				"]";
		
	}
	
	public InventoryProcessType getInventoryProcessType() {
		return inventoryProcessType;
	}

	public void setInventoryProcessType(InventoryProcessType inventoryProcessType) {
		this.inventoryProcessType = inventoryProcessType;
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

	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted(boolean printed) {
		this.printed = printed;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Get the id of the material that this product is produced, which is the inventory id
	 * 
	 * @return
	 */
	public Long getMaterialIdRef() {
		return materialIdRef;
	}

	/**
	 * Set the id of the material this product is produced, which is the inventory id
	 * 
	 * @param materialIdRef - inventory id
	 */
	public void setMaterialIdRef(Long materialIdRef) {
		this.materialIdRef = materialIdRef;
	}

	public InventoryProcessMaterial getProcessMaterial() {
		return processMaterial;
	}

	public void setProcessMaterial(InventoryProcessMaterial processMaterial) {
		this.processMaterial = processMaterial;
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

	public InventoryProcessStatus getProductProcessStatus() {
		return productProcessStatus;
	}

	public void setProductProcessStatus(InventoryProcessStatus productProcessStatus) {
		this.productProcessStatus = productProcessStatus;
	}

	public boolean isUpdateToInventory() {
		return updateToInventory;
	}

	public void setUpdateToInventory(boolean updateToInventory) {
		this.updateToInventory = updateToInventory;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public boolean isRecoil() {
		return recoil;
	}

	public void setRecoil(boolean recoil) {
		this.recoil = recoil;
	}
		
}
