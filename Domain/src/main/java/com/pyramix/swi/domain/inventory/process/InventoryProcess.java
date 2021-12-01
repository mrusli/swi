package com.pyramix.swi.domain.inventory.process;

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
@Table(name = "process", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryProcess extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1072290587003139626L;

	// order_date date
	@Column(name = "order_date")
	@Temporal(TemporalType.DATE)
	private Date orderDate;
		
	// status int(11)
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private InventoryProcessStatus processStatus;
	
	// complete_date date -- use it as completed date
	@Column(name = "complete_date")
	@Temporal(TemporalType.DATE)
	private Date completeDate;
	
	// note varchar(255)
	@Column(name = "note")
	private String note;
	
	// `process_id_fk` bigint(20) DEFAULT NULL
	// KEY `fk_process_id` (`process_id_fk`),
	// CONSTRAINT `fk_process_id_00` FOREIGN KEY (`process_id_fk`) REFERENCES `document_serial_number` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "process_id_fk")
	private DocumentSerialNumber processNumber;
	
	// `processed_by_id_fk` bigint(20) DEFAULT NULL,
	// KEY `fk_processed_by_id` (`processed_by_id_fk`),
	// CONSTRAINT `const_processed_by_id` FOREIGN KEY (`processed_by_id_fk`) REFERENCES `organization` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processed_by_id_fk")	
	private Company processedByCo;
		
	//  process_join_process_material
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true,  fetch = FetchType.LAZY)
	@JoinTable(name = "process_join_process_material",
			joinColumns = @JoinColumn(name = "id_process"),
			inverseJoinColumns = @JoinColumn(name = "id_process_material"))
	private List<InventoryProcessMaterial> processMaterials;
		
	//  inventory_join_process
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "inventory_join_process",
			joinColumns = @JoinColumn(name = "id_process"),
			inverseJoinColumns = @JoinColumn(name = "id_inventory"))
	private List<Inventory> inventoryList;
	
	@Override
	public String toString() {
		
		return "Process[id="+getId()+
				", orderDate="+getOrderDate()+
				", processStatus="+getProcessStatus()+
				", completeDate="+getCompleteDate()+
				", note="+getNote()+
				", processNumber="+getProcessNumber()+
				", processedByCo="+getProcessedByCo();
	}
	
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public DocumentSerialNumber getProcessNumber() {
		return processNumber;
	}

	public void setProcessNumber(DocumentSerialNumber processNumber) {
		this.processNumber = processNumber;
	}

	public InventoryProcessStatus getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(InventoryProcessStatus processStatus) {
		this.processStatus = processStatus;
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

	public Company getProcessedByCo() {
		return processedByCo;
	}

	public void setProcessedByCo(Company processedByCo) {
		this.processedByCo = processedByCo;
	}

	public List<InventoryProcessMaterial> getProcessMaterials() {
		return processMaterials;
	}

	public void setProcessMaterials(List<InventoryProcessMaterial> processMaterials) {
		this.processMaterials = processMaterials;
	}

	public List<Inventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

}
