package com.pyramix.swi.domain.inventory.transfer;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;

@Entity
@Table(name = "transfer", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryTransfer extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8543641146747384042L;

	//  `order_date` date DEFAULT NULL,
	@Column(name = "order_date")
	private Date orderDate;
	
	//  `updated_date` date DEFAULT NULL,
	@Column(name = "updated_date")
	private Date updatedDate;
	
	//  `status` int(11) DEFAULT NULL,
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private InventoryTransferStatus inventoryTransferStatus;
	
	//  `complete_date` date DEFAULT NULL,
	@Column(name = "complete_date")
	@Temporal(TemporalType.DATE)
	private Date completeDate;
	
	//  `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;
	
	//  `document_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id_fk")
	private DocumentSerialNumber transferNumber;

	//	`transfer_from_id_fk` bigint(20) DEFAULT NULL,
	//	KEY `fk_transfer_from_id` (`transfer_from_id_fk`),
	//	CONSTRAINT `const_transfer_from_id` FOREIGN KEY (`transfer_from_id_fk`) REFERENCES `organization` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transfer_from_id_fk")
	private Company transferFromCo;
	
	//  `transfer_join_transfer_material`
	//		  `id_transfer` bigint(20) NOT NULL,
	//		  `id_transfer_material` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "transfer_join_transfer_material",
			joinColumns = @JoinColumn(name = "id_transfer"),
			inverseJoinColumns = @JoinColumn(name = "id_transfer_material"))
	private List<InventoryTransferMaterial> transferMaterialList;
	
	//	`transfer_join_transfer_end_product`
	//		  `id_transfer` bigint(20) NOT NULL,
	//		  `id_transfer_end_product` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "transfer_join_transfer_end_product",
			joinColumns = @JoinColumn(name = "id_transfer"),
			inverseJoinColumns = @JoinColumn(name = "id_transfer_end_product"))
	private List<InventoryTransferEndProduct> transferEndProductList;

	//	`inventory_join_transfer`
	//		  `id_inventory` bigint(20) NOT NULL,
	//		  `id_transfer` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_transfer",
			joinColumns = @JoinColumn(name = "id_transfer"),
			inverseJoinColumns = @JoinColumn(name = "id_inventory"))
	private List<Inventory> inventoryList;
	
	// driver varchar(255)
	@Column(name = "driver")
	private String supir;
	
	// plate_no varchar(255)
	@Column(name = "plate_no")
	private String no_polisi;
	
	@Override
	public String toString() {
		
		return "InventoryTransfer[id="+getId()+
				", orderDate="+getOrderDate()+
				", updatedDate="+getUpdatedDate()+
				", inventoryTransferStatus="+getInventoryTransferStatus()+
				", completeDate="+getCompleteDate()+
				", note="+getNote()+
				", transferNumber="+getTransferNumber()+"]";
	}
	
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public InventoryTransferStatus getInventoryTransferStatus() {
		return inventoryTransferStatus;
	}

	public void setInventoryTransferStatus(InventoryTransferStatus inventoryTransferStatus) {
		this.inventoryTransferStatus = inventoryTransferStatus;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<InventoryTransferMaterial> getTransferMaterialList() {
		return transferMaterialList;
	}

	public void setTransferMaterialList(List<InventoryTransferMaterial> transferMaterialList) {
		this.transferMaterialList = transferMaterialList;
	}

	public List<InventoryTransferEndProduct> getTransferEndProductList() {
		return transferEndProductList;
	}

	public void setTransferEndProductList(List<InventoryTransferEndProduct> transferEndProductList) {
		this.transferEndProductList = transferEndProductList;
	}

	public DocumentSerialNumber getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(DocumentSerialNumber transferNumber) {
		this.transferNumber = transferNumber;
	}

	public List<Inventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

	public Company getTransferFromCo() {
		return transferFromCo;
	}

	public void setTransferFromCo(Company transferFromCo) {
		this.transferFromCo = transferFromCo;
	}

	public String getSupir() {
		return supir;
	}

	public void setSupir(String supir) {
		this.supir = supir;
	}

	public String getNo_polisi() {
		return no_polisi;
	}

	public void setNo_polisi(String no_polisi) {
		this.no_polisi = no_polisi;
	}
	
	
}
