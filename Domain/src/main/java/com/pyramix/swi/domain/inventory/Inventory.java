package com.pyramix.swi.domain.inventory;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;

@Entity
@Indexed
@Table(name = "inventory", schema=SchemaUtil.SCHEMA_COMMON)
public class Inventory extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4659364775994558553L;
	
	@Fields({
		@Field(termVector = TermVector.YES),
		@Field(name = "sortThickness", analyze = Analyze.YES, store = Store.NO, index = Index.YES)
	})	
	@SortableField(forField = "sortThickness")
	@Column(name =  "thickness")
	private BigDecimal thickness;
	
	// @Fields({
	//	@Field,
	//	@Field(name = "sortWidth", analyze = Analyze.NO, store = Store.NO, index = Index.NO)
	// })
	// @SortableField(forField = "sortWidth")
	@Column(name = "width")
	private BigDecimal width;
	
	@Column(name = "length")
	private BigDecimal length;
	
	@Column(name = "sheet_quantity")
	private int sheetQuantity;
	
	@Column(name = "weight_quantity")
	private BigDecimal weightQuantity;
	
	// @Fields({
	//	@Field,
	//	@Field(name = "sortMarking", analyze = Analyze.NO, store = Store.NO, index = Index.NO)
	// })
	// @SortableField(forField = "sortMarking")
	@Column(name = "marking")
	private String marking;
	
	@Column(name = "description")
	private String description;

	@Column(name = "contract_no")
	private String contractNumber;
	
	@Column(name = "lc_no")
	private String lcNumber;
	
	@Column(name = "receive_date")
	@Temporal(TemporalType.DATE)
	private Date receiveDate;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "inventory_code_id_fk")
	@IndexedEmbedded
	private InventoryCode inventoryCode;
	
	@Column(name = "inventory_status")
	@Enumerated(EnumType.ORDINAL)
	private InventoryStatus inventoryStatus;
	
	@Column(name = "inventory_location")
	@Enumerated(EnumType.ORDINAL)
	private InventoryLocation inventoryLocation;

	@Column(name = "inventory_packing")
	@Enumerated(EnumType.ORDINAL)
	private InventoryPacking inventoryPacking;

	@Column(name = "note")
	private String note;
	
	@Column(name = "sku")
	private String sku;
	
	//  inventory_join_process
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_process",
			joinColumns = @JoinColumn(name = "id_inventory"),
			inverseJoinColumns = @JoinColumn(name = "id_process"))
	private InventoryProcess inventoryProcess;

	//	inventory_join_bukapeti
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_bukapeti",
			joinColumns = @JoinColumn(name = "id_inventory"),
			inverseJoinColumns = @JoinColumn(name = "id_bukapeti"))
	private InventoryBukaPeti inventoryBukapeti;
	
	//	inventory_join_transfer
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_transfer",
			joinColumns = @JoinColumn(name = "id_inventory"),
			inverseJoinColumns = @JoinColumn(name = "id_transfer"))
	private InventoryTransfer inventoryTransfer;
	
	@Transient
	private int postSheetQuantity;
	
	@Transient
	private BigDecimal postWeightQuantity;
	
	@Override
	public String toString() {
		return "Inventory [id="+getId()+
				", thickness="+getThickness()+
				", width="+getWidth()+
				", length="+getLength()+
				", sheetQuantity="+getSheetQuantity()+
				", weightQuantity="+getWeightQuantity()+
				", marking="+getMarking()+
				", description="+getDescription()+
				", contractNumber="+getContractNumber()+
				", lcNumber="+getLcNumber()+
				", receiveDate="+getReceiveDate()+
				", inventoryCode="+inventoryCode.getProductCode()+
				", inventoryStatus="+getInventoryStatus().toString()+
				", inventoryLocation="+getInventoryLocation().toString()+
				", inventoryPacking="+getInventoryPacking().toString()+
				", note="+getNote()+
				", sku="+getSku()+
				"]";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InventoryCode getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(InventoryCode inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public InventoryStatus getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(InventoryStatus inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public InventoryLocation getInventoryLocation() {
		return inventoryLocation;
	}

	public void setInventoryLocation(InventoryLocation inventoryLocation) {
		this.inventoryLocation = inventoryLocation;
	}

	public InventoryPacking getInventoryPacking() {
		return inventoryPacking;
	}

	public void setInventoryPacking(InventoryPacking inventoryPacking) {
		this.inventoryPacking = inventoryPacking;
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

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getLcNumber() {
		return lcNumber;
	}

	public void setLcNumber(String lcNumber) {
		this.lcNumber = lcNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public InventoryProcess getInventoryProcess() {
		return inventoryProcess;
	}

	public void setInventoryProcess(InventoryProcess inventoryProcess) {
		this.inventoryProcess = inventoryProcess;
	}

	public InventoryBukaPeti getInventoryBukapeti() {
		return inventoryBukapeti;
	}

	public void setInventoryBukapeti(InventoryBukaPeti inventoryBukapeti) {
		this.inventoryBukapeti = inventoryBukapeti;
	}

	public InventoryTransfer getInventoryTransfer() {
		return inventoryTransfer;
	}

	public void setInventoryTransfer(InventoryTransfer inventoryTransfer) {
		this.inventoryTransfer = inventoryTransfer;
	}

	public BigDecimal getPostWeightQuantity() {
		return postWeightQuantity;
	}

	public void setPostWeightQuantity(BigDecimal postWeightQuantity) {
		this.postWeightQuantity = postWeightQuantity;
	}

	public int getPostSheetQuantity() {
		return postSheetQuantity;
	}

	public void setPostSheetQuantity(int postSheetQuantity) {
		this.postSheetQuantity = postSheetQuantity;
	}
}
