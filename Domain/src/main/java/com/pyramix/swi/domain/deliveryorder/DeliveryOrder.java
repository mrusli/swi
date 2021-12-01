package com.pyramix.swi.domain.deliveryorder;

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
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;

@Entity
@Table(name = "delivery_order", schema = SchemaUtil.SCHEMA_COMMON)
public class DeliveryOrder extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2449669605107462515L;

	// `date` date DEFAULT NULL,
	@Column(name = "date")
	@Temporal(TemporalType.DATE)
	private Date deliveryOrderDate;

	// `do_type` int(11) DEFAULT NULL,
	@Column(name = "do_type")
	@Enumerated(EnumType.ORDINAL)
	private DeliveryOrderType deliveryOrderType;

	// `company_id_fk` bigint(20) DEFAULT NULL,
	// KEY `key_company_id_01` (`company_id_fk`),
	// CONSTRAINT `fk_company_01` FOREIGN KEY (`company_id_fk`) REFERENCES
	// `organization` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id_fk")
	private Company company;

	// `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;

	// `delivery_order_number_id_fk` bigint(20) DEFAULT NULL,
	// KEY `key_document_serial_no_04` (`delivery_order_number_id_fk`),
	// CONSTRAINT `fk_document_serial_no_04` FOREIGN KEY
	// (`delivery_order_number_id_fk`) REFERENCES `document_serial_number` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "delivery_order_number_id_fk")
	private DocumentSerialNumber deliveryOrderNumber;

	// delivery_order_join_delivery_order_product
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "delivery_order_join_delivery_order_product", joinColumns = @JoinColumn(name = "id_delivery_order"), inverseJoinColumns = @JoinColumn(name = "id_delivery_order_product"))
	private List<DeliveryOrderProduct> deliveryOrderProducts;

	// delivery_order_status int(11)
	@Column(name = "delivery_order_status")
	private DocumentStatus deliveryOrderStatus;

	// `surat_jalan_id_fk` bigint(20) DEFAULT NULL,
	// KEY `key_surat_jalan_id_01` (`surat_jalan_id_fk`),
	// CONSTRAINT `fk_surat_jalan_01` FOREIGN KEY (`surat_jalan_id_fk`) REFERENCES `surat_jalan` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "surat_jalan_id_fk")
	private SuratJalan suratJalan;

	//  `create_date` date DEFAULT NULL,
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;

	//  `modified_date` datetime DEFAULT NULL,
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	//  `check_date` datetime DEFAULT NULL,
	@Column(name = "check_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkDate;

	//  `user_create_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_user_create_08` (`user_create_id_fk`),
	//  CONSTRAINT `fk_user_create_08` FOREIGN KEY (`user_create_id_fk`) REFERENCES `auth_user` (`id`)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_create_id_fk")
	private User userCreate;
	
	@Override
	public String toString() {

		return "DeliveryOrder[id="+getId()+ 
				", deliveryOrderDate="+getDeliveryOrderDate()+
				", deliveryOrderType="+getDeliveryOrderType().toString()+
				", note="+getNote()+
				", deliveryOrderNumber="+getDeliveryOrderNumber().getSerialComp()+
				", deliveryOrderProducts="+getDeliveryOrderProducts()+
				", deliveryOrderStatus="+getDeliveryOrderStatus().toString()+
				"]";
	}

	public Date getDeliveryOrderDate() {
		return deliveryOrderDate;
	}

	public void setDeliveryOrderDate(Date deliveryOrderDate) {
		this.deliveryOrderDate = deliveryOrderDate;
	}

	public DeliveryOrderType getDeliveryOrderType() {
		return deliveryOrderType;
	}

	public void setDeliveryOrderType(DeliveryOrderType deliveryOrderType) {
		this.deliveryOrderType = deliveryOrderType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DocumentSerialNumber getDeliveryOrderNumber() {
		return deliveryOrderNumber;
	}

	public void setDeliveryOrderNumber(DocumentSerialNumber deliveryOrderNumber) {
		this.deliveryOrderNumber = deliveryOrderNumber;
	}

	public List<DeliveryOrderProduct> getDeliveryOrderProducts() {
		return deliveryOrderProducts;
	}

	public void setDeliveryOrderProducts(List<DeliveryOrderProduct> deliveryOrderProducts) {
		this.deliveryOrderProducts = deliveryOrderProducts;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DocumentStatus getDeliveryOrderStatus() {
		return deliveryOrderStatus;
	}

	public void setDeliveryOrderStatus(DocumentStatus deliveryOrderStatus) {
		this.deliveryOrderStatus = deliveryOrderStatus;
	}

	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public User getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}
}
