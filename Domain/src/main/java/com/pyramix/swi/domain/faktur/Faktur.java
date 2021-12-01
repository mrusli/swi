package com.pyramix.swi.domain.faktur;

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
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;

@Entity
@Table(name = "faktur", schema = SchemaUtil.SCHEMA_COMMON)
public class Faktur extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8458230227064012263L;

	//  `faktur_date` date DEFAULT NULL,
	@Column(name = "faktur_date")
	@Temporal(TemporalType.DATE)
	private Date fakturDate;
	
	//  `faktur_type` int(11) DEFAULT NULL,
	@Column(name = "faktur_type")
	@Enumerated(EnumType.ORDINAL)
	private FakturType fakturType;
	
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
	
	//  `faktur_number_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_document_serial_no_03` (`faktur_number_id_fk`),
	//  CONSTRAINT `fk_document_serial_no_03` FOREIGN KEY (`faktur_number_id_fk`) REFERENCES `document_serial_number` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "faktur_number_id_fk")
	private DocumentSerialNumber fakturNumber;
	
	//  `customer_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_customer_id_03` (`customer_id_fk`),
	//  CONSTRAINT `fk_customer_03` FOREIGN KEY (`customer_id_fk`) REFERENCES `customer` (`id`),
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;

	// faktur_join_faktur_product
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name = "faktur_join_faktur_product",
			joinColumns = @JoinColumn(name = "id_faktur"),
			inverseJoinColumns = @JoinColumn(name = "id_faktur_product"))
	private List<FakturProduct> fakturProducts;

	//	faktur_status int(11)
	@Column(name = "faktur_status")
	private DocumentStatus fakturStatus;

	// `surat_jalan_id_fk` bigint(20) DEFAULT NULL,
	// KEY `key_surat_jalan_id_02` (`surat_jalan_id_fk`),
	// CONSTRAINT `fk_surat_jalan_02` FOREIGN KEY (`surat_jalan_id_fk`) REFERENCES `surat_jalan` (`id`)
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
	//  KEY `key_user_create_07` (`user_create_id_fk`),
	//  CONSTRAINT `fk_user_create_07` FOREIGN KEY (`user_create_id_fk`) REFERENCES `auth_user` (`id`)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_create_id_fk")
	private User userCreate;
	
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

	@Override
	public String toString() {
		
		return "Faktur[id="+getId()+
				", fakturDate="+getFakturDate()+
				", paymentType="+getPaymentType().toString()+
				", jumlahHari="+getJumlahHari()+
				", usePpn="+isUsePpn()+
				", totalOrder="+getTotalOrder()+
				", totalPpn="+getTotalPpn()+
				", note="+getNote()+
				", fakturNumber="+getFakturNumber().getSerialComp()+
				", fakturProducts="+getFakturProducts()+
				", fakturStatus="+getFakturStatus().toString()+
				"]";
	}
	
	public Date getFakturDate() {
		return fakturDate;
	}

	public void setFakturDate(Date fakturDate) {
		this.fakturDate = fakturDate;
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

	public DocumentSerialNumber getFakturNumber() {
		return fakturNumber;
	}

	public void setFakturNumber(DocumentSerialNumber fakturNumber) {
		this.fakturNumber = fakturNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the fakturProducts
	 */
	public List<FakturProduct> getFakturProducts() {
		return fakturProducts;
	}

	/**
	 * @param fakturProducts the fakturProducts to set
	 */
	public void setFakturProducts(List<FakturProduct> fakturProducts) {
		this.fakturProducts = fakturProducts;
	}

	public DocumentStatus getFakturStatus() {
		return fakturStatus;
	}

	public void setFakturStatus(DocumentStatus fakturStatus) {
		this.fakturStatus = fakturStatus;
	}

	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	/**
	 * @return the fakturType
	 */
	public FakturType getFakturType() {
		return fakturType;
	}

	/**
	 * @param fakturType the fakturType to set
	 */
	public void setFakturType(FakturType fakturType) {
		this.fakturType = fakturType;
	}

}
