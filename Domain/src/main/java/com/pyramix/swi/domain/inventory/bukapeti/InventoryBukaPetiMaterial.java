package com.pyramix.swi.domain.inventory.bukapeti;

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
@Table(name = "bukapeti_material", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryBukaPetiMaterial extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8237617713079026672L;

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

	//  `inventory_id_ref` bigint(20) DEFAULT NULL,
	@Column(name = "inventory_id_ref")
	private Long inventoryIdRef;

	//  `contract_no` varchar(255) DEFAULT NULL,
	@Column(name = "contract_no")
	private String contractNumber;
	
	//  `lc_no` varchar(255) DEFAULT NULL,
	@Column(name = "lc_no")
	private String lcNumber;

	// `bukapeti_join_bukapeti_material` (
	//		  `id_bukapeti` bigint(20) NOT NULL,
	//		  `id_bukapeti_material` bigint(20) NOT NULL,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "bukapeti_join_bukapeti_material",
			joinColumns = @JoinColumn(name = "id_bukapeti_material"),
			inverseJoinColumns = @JoinColumn(name = "id_bukapeti"))
	private InventoryBukaPeti inventoryBukapeti;
	
	//	`bukapeti_material_join_bukapeti_product`
	//		  `id_bukapeti_material` bigint(20) NOT NULL,
	//		  `id_bukapeti_product` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "bukapeti_material_join_bukapeti_product",
			joinColumns = @JoinColumn(name = "id_bukapeti_material"),
			inverseJoinColumns = @JoinColumn(name = "id_bukapeti_product"))
	private List<InventoryBukaPetiProduct> bukapetiProductList;

	@Override
	public String toString() {
		
		return "InventoryBukaPetiMaterial[id="+getId()+
				", marking="+getMarking()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", inventoryPacking="+getInventoryPacking()+
				", weightQuantity="+getWeightQuantity()+
				", sheetQuantity="+getSheetQuantity()+
				", inventoryLocation="+getInventoryLocation()+
				"]";
		
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

	public Long getInventoryIdRef() {
		return inventoryIdRef;
	}

	public void setInventoryIdRef(Long inventoryIdRef) {
		this.inventoryIdRef = inventoryIdRef;
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

	public InventoryBukaPeti getInventoryBukapeti() {
		return inventoryBukapeti;
	}

	public void setInventoryBukapeti(InventoryBukaPeti inventoryBukapeti) {
		this.inventoryBukapeti = inventoryBukapeti;
	}

	public List<InventoryBukaPetiProduct> getBukapetiProductList() {
		return bukapetiProductList;
	}

	public void setBukapetiProductList(List<InventoryBukaPetiProduct> bukapetiProductList) {
		this.bukapetiProductList = bukapetiProductList;
	}
	
	
}
