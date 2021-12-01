package com.pyramix.swi.domain.inventory.bukapeti;

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
import com.pyramix.swi.domain.serial.DocumentSerialNumber;

@Entity
@Table(name = "bukapeti", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryBukaPeti extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7386573184460548786L;

	//  `order_date` date DEFAULT NULL,
	@Column(name = "order_date")
	private Date orderDate;	
	
	//  `updated_date` date DEFAULT NULL,
	@Column(name = "updated_date")
	private Date updatedDate;
	
	//  `status` int(11) DEFAULT NULL,
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)	
	private InventoryBukaPetiStatus inventoryBukapetiStatus;
	
	//  `complete_date` date DEFAULT NULL,
	@Column(name = "complete_date")
	@Temporal(TemporalType.DATE)
	private Date completeDate;
	
	//  `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;

	//  `document_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `fk_document_id_00` (`document_id_fk`),
	//  CONSTRAINT `fk_document_id_00` FOREIGN KEY (`document_id_fk`) REFERENCES `document_serial_number` (`id`)
	//	use: BUKAPETI_ORDER(2)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id_fk")
	private DocumentSerialNumber bukapetiNumber;

	//	`bukapeti_join_bukapeti_material`
	//		  `id_bukapeti` bigint(20) NOT NULL,
	//		  `id_bukapeti_material` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true,  fetch = FetchType.LAZY)
	@JoinTable(name = "bukapeti_join_bukapeti_material",
			joinColumns = @JoinColumn(name = "id_bukapeti"),
			inverseJoinColumns = @JoinColumn(name = "id_bukapeti_material"))
	private List<InventoryBukaPetiMaterial> bukapetiMaterialList;
	
	//	`bukapeti_join_bukapeti_end_product`
	//		`id_bukapeti` bigint(20) NOT NULL,
	//		`id_bukapeti_end_product` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL  }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "bukapeti_join_bukapeti_end_product",
			joinColumns = @JoinColumn(name = "id_bukapeti"),
			inverseJoinColumns = @JoinColumn(name = "id_bukapeti_end_product"))
	private List<InventoryBukaPetiEndProduct> bukapetiEndProduct;
	
	//	`inventory_join_bukapeti`
	//		`id_inventory` bigint(20) NOT NULL,
	//		`id_bukapeti` bigint(20) NOT NULL,
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_bukapeti",
			joinColumns = @JoinColumn(name = "id_bukapeti"),
			inverseJoinColumns = @JoinColumn(name = "id_inventory"))
	private List<Inventory> inventoryList;

	@Override
	public String toString() {
		
		return "InventoryBukaPeti[id="+getId()+
				", orderDate="+getOrderDate()+
				", updatedDate="+getUpdatedDate()+
				", inventoryBukapetiStatu="+getInventoryBukapetiStatus().toString()+
				", completeDate="+getCompleteDate()+
				", note="+getNote()+
				", processNumber="+getBukapetiNumber().getSerialNo();
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

	public InventoryBukaPetiStatus getInventoryBukapetiStatus() {
		return inventoryBukapetiStatus;
	}

	public void setInventoryBukapetiStatus(InventoryBukaPetiStatus inventoryBukapetiStatus) {
		this.inventoryBukapetiStatus = inventoryBukapetiStatus;
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

	public DocumentSerialNumber getBukapetiNumber() {
		return bukapetiNumber;
	}

	public void setBukapetiNumber(DocumentSerialNumber bukapetiNumber) {
		this.bukapetiNumber = bukapetiNumber;
	}

	public List<Inventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

	public List<InventoryBukaPetiMaterial> getBukapetiMaterialList() {
		return bukapetiMaterialList;
	}

	public void setBukapetiMaterialList(List<InventoryBukaPetiMaterial> bukapetiMaterialList) {
		this.bukapetiMaterialList = bukapetiMaterialList;
	}

	public List<InventoryBukaPetiEndProduct> getBukapetiEndProduct() {
		return bukapetiEndProduct;
	}

	public void setBukapetiEndProduct(List<InventoryBukaPetiEndProduct> bukapetiEndProduct) {
		this.bukapetiEndProduct = bukapetiEndProduct;
	}
	
}
