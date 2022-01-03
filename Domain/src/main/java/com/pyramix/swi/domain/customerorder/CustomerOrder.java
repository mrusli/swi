package com.pyramix.swi.domain.customerorder;

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
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.voucher.VoucherSales;

@Entity
@Table(name = "customer_order", schema = SchemaUtil.SCHEMA_COMMON)
public class CustomerOrder extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3504438163634550146L;

	//  `order_date` date DEFAULT NULL,
	@Column(name = "order_date")
	@Temporal(TemporalType.DATE)
	private Date orderDate;
	
	//  `payment_type` int(11) DEFAULT NULL,
	@Column(name = "payment_type")
	@Enumerated(EnumType.ORDINAL)
	private PaymentType paymentType;
	
	//  `jumlah_hari` int(11) DEFAULT NULL,
	@Column(name = "jumlah_hari")
	private int creditDay;
	
	// 	`use_ppn` char(1) DEFAULT NULL,
	@Column(name = "use_ppn")
	@Type(type = "true_false")
	private boolean usePpn;
	
	//	`total_order` decimal(19,2) DEFAULT NULL,
	@Column(name = "total_order")
	private BigDecimal totalOrder;
	
	// total_ppn decimal(19,2)
	@Column(name = "total_ppn")
	private BigDecimal totalPpn;
	
	// payment_complete char(1)
	@Column(name = "payment_complete")
	@Type(type = "true_false")
	private boolean paymentComplete;
	
	// amount_paid decimal(19,2)
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	//  `note` varchar(255) DEFAULT NULL,
	@Column(name = "note")
	private String note;
	
	//	`delivery_order_required` char(1) DEFAULT NULL,
	@Column(name = "delivery_order_required")
	@Type(type = "true_false")
	private boolean deliveryOrderRequired;
	
	//  `employee_sales_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_employee_sales_01` (`employee_sales_id_fk`),
	//  CONSTRAINT `fk_employee_sales_01` FOREIGN KEY (`employee_sales_id_fk`) REFERENCES `employee` (`id`),
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_sales_id_fk")
	private Employee employeeSales;
	
	//  `order_number_id_fk` bigint(20) DEFAULT NULL,
	// CONSTRAINT `fk_document_serial_no_01` FOREIGN KEY (`order_number_id_fk`) REFERENCES `document_serial_number` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "order_number_id_fk")
	private DocumentSerialNumber documentSerialNumber;
	
	//  `customer_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id_fk")
	private Customer customer;

	//  customer_order_join_employee_commissions
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "customer_order_join_employee_commissions",
			joinColumns = @JoinColumn(name = "id_customer_order"),
			inverseJoinColumns = @JoinColumn(name = "id_employee_commissions"))
	private EmployeeCommissions employeeCommissions;

	// customer_order_join_customer_order_product
	@OneToMany(cascade={ CascadeType.ALL }, orphanRemoval=true, fetch = FetchType.EAGER)
	@JoinTable(name = "customer_order_join_customer_order_product",
			joinColumns = @JoinColumn(name = "id_customer_order"),
			inverseJoinColumns = @JoinColumn(name = "id_customer_order_product"))
	private List<CustomerOrderProduct> customerOrderProducts;
	
	// customer_order_join_surat_jalan
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "customer_order_join_surat_jalan",
			joinColumns = @JoinColumn(name = "id_customer_order"),
			inverseJoinColumns = @JoinColumn(name = "id_surat_jalan"))
	private SuratJalan suratJalan;

	// customer_order_join_voucher_sales
	@OneToOne(cascade={ CascadeType.ALL }, orphanRemoval=true, fetch = FetchType.LAZY)
	@JoinTable(name = "customer_order_join_voucher_sales",
			joinColumns = @JoinColumn(name = "id_customer_order",nullable=true),
			inverseJoinColumns = @JoinColumn(name = "id_voucher_sales", nullable=true))
	private VoucherSales voucherSales;

	//  `organization_id_fk` bigint(20) DEFAULT NULL,
	//	KEY `key_orgranization_id_00` (`organization_id_fk`),
	//  CONSTRAINT `fk_organization_00` FOREIGN KEY (`organization_id_fk`) REFERENCES `organization` (`id`)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id_fk")
	private Company companyToDeliveryOrder;

	// customer_order_join_receivable
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "customer_order_join_receivable",
			joinColumns = @JoinColumn(name = "id_customer_order"),
			inverseJoinColumns = @JoinColumn(name = "id_receivable"))
	private CustomerReceivable customerReceivable;

	// order_status int(11)
	@Column(name = "order_status")
	private DocumentStatus orderStatus;

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
		return "CustomerOrder [id="+getId()+
				", orderDate="+getOrderDate()+
				", paymentType="+getPaymentType().toString()+
				", creditDay="+getCreditDay()+
				", usePpn="+isUsePpn()+
				", totalOrder="+getTotalOrder()+
				", totalPpn="+getTotalPpn()+
				", paymentComplete="+isPaymentComplete()+
				", amountPaid="+getAmountPaid()+
				", note="+getNote()+
				", deliveryOrderRequired="+isDeliveryOrderRequired()+
				", documentSerialNumber="+getDocumentSerialNumber().getSerialComp()+
				", customerOrderProducts="+getCustomerOrderProducts()+
				", orderStatus="+getOrderStatus().toString()+
				"]";
	}
	
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public int getCreditDay() {
		return creditDay;
	}

	public void setCreditDay(int creditDay) {
		this.creditDay = creditDay;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<CustomerOrderProduct> getCustomerOrderProducts() {
		return customerOrderProducts;
	}

	public void setCustomerOrderProducts(List<CustomerOrderProduct> customerOrderProducts) {
		this.customerOrderProducts = customerOrderProducts;
	}

	public BigDecimal getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(BigDecimal totalOrder) {
		this.totalOrder = totalOrder;
	}

	public boolean isUsePpn() {
		return usePpn;
	}

	public void setUsePpn(boolean usePpn) {
		this.usePpn = usePpn;
	}

	public BigDecimal getTotalPpn() {
		return totalPpn;
	}

	public void setTotalPpn(BigDecimal totalPpn) {
		this.totalPpn = totalPpn;
	}

	public DocumentSerialNumber getDocumentSerialNumber() {
		return documentSerialNumber;
	}

	public void setDocumentSerialNumber(DocumentSerialNumber documentSerialNumber) {
		this.documentSerialNumber = documentSerialNumber;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Company getCompanyToDeliveryOrder() {
		return companyToDeliveryOrder;
	}

	public void setCompanyToDeliveryOrder(Company companyToDeliveryOrder) {
		this.companyToDeliveryOrder = companyToDeliveryOrder;
	}

	public boolean isDeliveryOrderRequired() {
		return deliveryOrderRequired;
	}

	public void setDeliveryOrderRequired(boolean deliveryOrderRequired) {
		this.deliveryOrderRequired = deliveryOrderRequired;
	}
	
	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}
	
	public VoucherSales getVoucherSales() {
		return voucherSales;
	}
	
	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public EmployeeCommissions getEmployeeCommissions() {
		return employeeCommissions;
	}

	public void setEmployeeCommissions(EmployeeCommissions employeeCommissions) {
		this.employeeCommissions = employeeCommissions;
	}

	public CustomerReceivable getCustomerReceivable() {
		return customerReceivable;
	}

	public void setCustomerReceivable(CustomerReceivable customerReceivable) {
		this.customerReceivable = customerReceivable;
	}

	public DocumentStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(DocumentStatus orderStatus) {
		this.orderStatus = orderStatus;
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

	public Employee getEmployeeSales() {
		return employeeSales;
	}

	public void setEmployeeSales(Employee employeeSales) {
		this.employeeSales = employeeSales;
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
