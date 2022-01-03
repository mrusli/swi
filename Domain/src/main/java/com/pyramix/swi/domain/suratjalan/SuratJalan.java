package com.pyramix.swi.domain.suratjalan;

import java.math.BigDecimal;
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

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.user.User;

@Entity
@Table(name = "surat_jalan", schema = SchemaUtil.SCHEMA_COMMON)
public class SuratJalan extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2919210365160969979L;

	//  `surat_jalan_date` date DEFAULT NULL,
	@Column(name = "surat_jalan_date")
	@Temporal(TemporalType.DATE)
	private Date suratJalanDate;
	
	//  `delivery_date` date DEFAULT NULL,
	@Column(name = "delivery_date")
	@Temporal(TemporalType.DATE)
	private Date deliveryDate;
	
	//  `payment_type` int(11) DEFAULT NULL,
	@Column(name = "payment_type")
	@Enumerated(EnumType.ORDINAL)
	private PaymentType paymentType;
	
	//  `jumlah_hari` int(11) DEFAULT NULL,
	@Column(name = "jumlah_hari")
	private int jumlahHari;
	
	//  `use_ppn` char(1) DEFAULT NULL,
	@Column(name = "use_ppn")
	@Type(type = "true_false")
	private boolean usePpn;
	
	//  `total_order` decimal(19,2) DEFAULT NULL,
	@Column(name = "total_order")
	private BigDecimal totalOrder;
	
	//  `total_ppn` decimal(19,2) DEFAULT NULL,
	@Column(name = "total_ppn")
	private BigDecimal totalPpn;
	
	//  `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;
	
	//	`employee_commissions_id_fk` bigint(20) DEFAULT NULL,
	//	KEY `key_employee_commissions_02` (`employee_commissions_id_fk`),
	//	CONSTRAINT `fk_employee_commissions_02` FOREIGN KEY (`employee_commissions_id_fk`) REFERENCES `employee_commissions` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_commissions_id_fk")
	private EmployeeCommissions employeeCommissions;
	
	//  `surat_jalan_number_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_document_serial_no_02` (`surat_jalan_number_id_fk`),
	//  CONSTRAINT `fk_document_serial_no_02` FOREIGN KEY (`surat_jalan_number_id_fk`) REFERENCES `document_serial_number` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "surat_jalan_number_id_fk")
	private DocumentSerialNumber suratJalanNumber;
	
	//  `customer_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_customer_id_04` (`customer_id_fk`),
	//  CONSTRAINT `fk_customer_04` FOREIGN KEY (`customer_id_fk`) REFERENCES `customer` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;
	
	//  `delivery_order_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_delivery_order_id_01` (`delivery_order_id_fk`),
	//  CONSTRAINT `fk_delivery_order_01` FOREIGN KEY (`delivery_order_id_fk`) REFERENCES `delivery_order` (`id`),
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_order_id_fk")
	private DeliveryOrder deliveryOrder;
	
	//  `faktur_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_faktur_id_01` (`faktur_id_fk`),
	//  CONSTRAINT `fk_faktur_01` FOREIGN KEY (`faktur_id_fk`) REFERENCES `faktur` (`id`),  
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "faktur_id_fk")
	private Faktur faktur;
	
	//	surat_jalan_join_surat_jalan_product
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.EAGER)
	@JoinTable(name = "surat_jalan_join_surat_jalan_product",
			joinColumns = @JoinColumn(name = "id_surat_jalan"),
			inverseJoinColumns = @JoinColumn(name = "id_surat_jalan_product"))
	private List<SuratJalanProduct> suratJalanProducts;

	//  surat_jalan_status int(11)
	@Column(name = "surat_jalan_status")
	private DocumentStatus suratJalanStatus;

	// create_date date 
	@Column(name = "create_date")
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	// modified_date datetime 
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	// check_date datetime 
	@Column(name = "check_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkDate;
	
	// user_create_id_fk bigint(20)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_create_id_fk")
	private User userCreate;		

	//	batal_date date
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;

	@Override
	public String toString() {
		return "SuratJalan[id="+getId()+
				", SuratJalanDate="+getSuratJalanDate()+
				", DeliveryDate="+getDeliveryDate()+
				", PaymentType="+getPaymentType().toString()+
				", JumlahHari="+getJumlahHari()+
				", UsePpn="+isUsePpn()+
				", TotalOrder="+getTotalOrder()+
				", TotalPpn="+getTotalPpn()+
				", Note="+getNote()+
				", SuratJalanStatus="+getSuratJalanStatus().toString()+
				"]";
	}
	
	public Date getSuratJalanDate() {
		return suratJalanDate;
	}

	public void setSuratJalanDate(Date suratJalanDate) {
		this.suratJalanDate = suratJalanDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public int getJumlahHari() {
		return jumlahHari;
	}

	public void setJumlahHari(int jumlahHari) {
		this.jumlahHari = jumlahHari;
	}

	public boolean isUsePpn() {
		return usePpn;
	}

	public void setUsePpn(boolean usePpn) {
		this.usePpn = usePpn;
	}

	public BigDecimal getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(BigDecimal totalOrder) {
		this.totalOrder = totalOrder;
	}

	public BigDecimal getTotalPpn() {
		return totalPpn;
	}

	public void setTotalPpn(BigDecimal totalPpn) {
		this.totalPpn = totalPpn;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DocumentSerialNumber getSuratJalanNumber() {
		return suratJalanNumber;
	}

	public void setSuratJalanNumber(DocumentSerialNumber suratJalanNumber) {
		this.suratJalanNumber = suratJalanNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public Faktur getFaktur() {
		return faktur;
	}

	public void setFaktur(Faktur faktur) {
		this.faktur = faktur;
	}

	public List<SuratJalanProduct> getSuratJalanProducts() {
		return suratJalanProducts;
	}

	public void setSuratJalanProducts(List<SuratJalanProduct> suratJalanProducts) {
		this.suratJalanProducts = suratJalanProducts;
	}

	public EmployeeCommissions getEmployeeCommissions() {
		return employeeCommissions;
	}

	public void setEmployeeCommissions(EmployeeCommissions employeeCommissions) {
		this.employeeCommissions = employeeCommissions;
	}

	public DocumentStatus getSuratJalanStatus() {
		return suratJalanStatus;
	}

	public void setSuratJalanStatus(DocumentStatus suratJalanStatus) {
		this.suratJalanStatus = suratJalanStatus;
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

	public Date getBatalDate() {
		return batalDate;
	}

	public void setBatalDate(Date batalDate) {
		this.batalDate = batalDate;
	}

	public String getBatalNote() {
		return batalNote;
	}

	public void setBatalNote(String batalNote) {
		this.batalNote = batalNote;
	}
	


}
